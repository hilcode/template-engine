/*
 * Copyright (C) 2017 H.C. Wijbenga
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.hilcode.teng.mojo;

import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.CLASS_PATH;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import com.github.hilcode.teng.JavaTemplate;
import com.github.hilcode.teng.Template;
import com.github.hilcode.teng.TengException;
import com.github.hilcode.teng.TengParser;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

public final class MojoUtils
{
	public static final ImmutableList<File> processTemplateFiles(final File rootDir, final File generatedSourcesTengDir)
	{
		final ImmutableList.Builder<File> javaFiles = ImmutableList.builder();
		for (final File templateFile : findFiles(rootDir, ".teng"))
		{
			javaFiles.add(processTemplateFile(generatedSourcesTengDir, templateFile));
		}
		return javaFiles.build();
	}

	public static final ImmutableList<File> findFiles(final File rootDir, final String extension)
	{
		final Predicate<File> predicate = new FileExtensionPredicate(extension);
		final ImmutableList.Builder<File> filesBuilder = ImmutableList.builder();
		for (final File file : Files.fileTreeTraverser().breadthFirstTraversal(rootDir).filter(predicate))
		{
			filesBuilder.add(file);
		}
		return filesBuilder.build();
	}

	public static final File processTemplateFile(final File generatedSourcesTengDir, final File templateFile)
	{
		final JavaTemplate javaTemplate = compileTemplate(templateFile);
		final File javaDir = new File(generatedSourcesTengDir, javaTemplate.javaFile.packageName.value.replace('.', '/'));
		javaDir.mkdirs();
		final File javaFile = new File(javaDir, javaTemplate.javaFile.name + ".java");
		final FileWriter fileWriter = fileWriter(javaFile);
		try
		{
			for (final String line : javaTemplate)
			{
				append(fileWriter, line);
			}
			System.out.println("Created '" + javaFile + "'");
			return javaFile;
		}
		finally
		{
			close(fileWriter);
		}
	}

	public static final JavaTemplate compileTemplate(final File templateFile)
	{
		final FileReader fileReader = fileReader(templateFile);
		try
		{
			final TengParser tengParser = new TengParser();
			return tengParser.parse(fileReader);
		}
		finally
		{
			close(fileReader);
		}
	}

	public static final URLClassLoader compileJavaFiles(
			final ClassLoader parent,
			final MavenProject mavenProject,
			final File classDir,
			final List<File> javaFiles)
	{
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, Locale.getDefault(), Charset.forName("UTF-8"));
		try
		{
			final ImmutableList<File> classpath = makeClasspath(mavenProject);
			setFileManagerLocation(fileManager, CLASS_PATH, classpath);
			setFileManagerLocation(fileManager, CLASS_OUTPUT, Arrays.asList(classDir));
			System.err.println("Using classpath: " + Joiner.on(':').join(classpath));
			compiler
					.getTask(
							null,
							fileManager,
							null,
							ImmutableList.<String> of(),
							null,
							fileManager.getJavaFileObjectsFromFiles(javaFiles))
					.call();
		}
		finally
		{
			close(fileManager);
		}
		return new URLClassLoader(
				new URL[]
				{
					fromUriToUrl(classDir.toURI())
				},
				parent);
	}

	public static final ImmutableList<File> makeClasspath(final MavenProject mavenProject)
	{
		final ImmutableList.Builder<File> classpath = ImmutableList.builder();
		classpath.add(new File(mavenProject.getBuild().getDirectory()));
		for (final Artifact artifact : mavenProject.getDependencyArtifacts())
		{
			classpath.add(artifact.getFile());
		}
		return classpath.build();
	}

	public static final void setFileManagerLocation(
			final StandardJavaFileManager fileManager,
			final StandardLocation standardLocation,
			final Iterable<File> path)
	{
		try
		{
			fileManager.setLocation(standardLocation, path);
		}
		catch (final IOException e)
		{
			throw new TengException(e.getMessage(), e);
		}
	}

	public static final URL fromUriToUrl(final URI uri)
	{
		try
		{
			return uri.toURL();
		}
		catch (final MalformedURLException e)
		{
			throw new TengException(e.getMessage(), e);
		}
	}

	public static final <MODEL> String executeTemplate(final ClassLoader classLoader, final String templateName, final MODEL model)
	{
		final Class<?> templateClass = loadClass(classLoader, templateName);
		final Method executeMethod = templateClass.getMethods()[0];
		if (executeMethod.getReturnType() == Template.Block.class)
		{
			final StringBuilder result = new StringBuilder();
			for (final String line : invokeStaticMethod(Template.Block.class, executeMethod, model).execute())
			{
				result.append(line);
			}
			return result.toString();
		}
		else
		{
			return invokeStaticMethod(Template.Inline.class, executeMethod, model).execute();
		}
	}

	public static final Class<?> loadClass(final ClassLoader classLoader, final String fullyQualifiedClassName)
	{
		try
		{
			return classLoader.loadClass(fullyQualifiedClassName);
		}
		catch (final ClassNotFoundException e)
		{
			throw new TengException(e.getMessage(), e);
		}
	}

	public static final boolean isClassAvailable(final ClassLoader classLoader, final String fullyQualifiedClassName)
	{
		try
		{
			classLoader.loadClass(fullyQualifiedClassName);
			return true;
		}
		catch (final Exception e)
		{
			return false;
		}
	}

	public static final <T> T newInstance(final Class<T> classT)
	{
		try
		{
			return classT.newInstance();
		}
		catch (final Exception e)
		{
			throw new TengException(e.getMessage(), e);
		}
	}

	public static final <OUTPUT, INPUT> OUTPUT invokeStaticMethod(
			final Class<OUTPUT> returnType,
			final Method staticMethod,
			final INPUT parameter)
	{
		try
		{
			Preconditions.checkState(returnType.isAssignableFrom(staticMethod.getReturnType()), "Incompatible types.");
			@SuppressWarnings("unchecked")
			final OUTPUT result = (OUTPUT) staticMethod.invoke(Void.class, parameter);
			return result;
		}
		catch (final Exception e)
		{
			throw new TengException(e.getMessage(), e);
		}
	}

	public static final FileReader fileReader(final File file)
	{
		try
		{
			return new FileReader(file);
		}
		catch (final FileNotFoundException e)
		{
			throw new TengException(e.getMessage(), e);
		}
	}

	public static final FileWriter fileWriter(final File file)
	{
		try
		{
			return new FileWriter(file);
		}
		catch (final IOException e)
		{
			throw new TengException(e.getMessage(), e);
		}
	}

	public static final void append(final FileWriter fileWriter, final String text)
	{
		try
		{
			fileWriter.append(text);
		}
		catch (final IOException e)
		{
			throw new TengException(e.getMessage(), e);
		}
	}

	public static final void close(final Closeable closeable)
	{
		try
		{
			closeable.close();
		}
		catch (final IOException e)
		{
			throw new TengException(e.getMessage(), e);
		}
	}
}
