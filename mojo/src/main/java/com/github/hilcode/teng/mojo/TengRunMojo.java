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

import static com.google.common.io.Files.fileTreeTraverser;
import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import javax.inject.Inject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import com.github.hilcode.teng.Deserializer;
import com.github.hilcode.teng.FileNameGenerator;
import com.github.hilcode.teng.Template;
import com.github.hilcode.teng.TengException;
import com.google.common.base.Predicate;
import com.google.common.io.Files;

@Mojo(name = "run", defaultPhase = GENERATE_SOURCES, requiresOnline = false)
public final class TengRunMojo
	extends
		AbstractMojo
{
	@Parameter(defaultValue = "src/main/teng", property = "teng.dataDir")
	private File dataDir;

	@Parameter(defaultValue = "com.github.hilcode.teng.JsonDeserializer", property = "teng.deserializerClassName")
	private String deserializerClassName;

	@Parameter(defaultValue = "com.github.hilcode.teng.DefaultFileNameGenerator", property = "teng.fileNameGeneratorClassName")
	private String fileNameGeneratorClassName;

	@Parameter(defaultValue = ".json", property = "teng.dataFileExtension")
	private String dataFileExtension;

	@Parameter(required = true, property = "teng.templateClassName")
	private String templateClassName;

	@Parameter(required = true, property = "teng.modelClassName")
	private String modelClassName;

	@Parameter(defaultValue = "${project.build.directory}/generated-sources/teng", property = "teng.outputDir")
	private File outputDir;

	@Inject
	private MavenProject mavenProject;

	@Override
	public void execute() throws MojoExecutionException
	{
		try
		{
			final ClassLoader classLoader = getClassLoader(this.templateClassName, this.mavenProject, this.outputDir);
			final Template template = instantiateClass(classLoader, this.templateClassName, Template.class);
			final Deserializer deserializer = instantiateClass(classLoader, this.deserializerClassName, Deserializer.class);
			final FileNameGenerator fileNameGenerator = instantiateClass(classLoader, this.fileNameGeneratorClassName, FileNameGenerator.class);
			@SuppressWarnings("unchecked")
			final Class<Object> modelClass = (Class<Object>) MojoUtils.loadClass(classLoader, this.modelClassName);
			final Predicate<File> predicate = new FileExtensionPredicate(this.dataFileExtension);
			final Iterable<File> dataFiles = fileTreeTraverser()
					.breadthFirstTraversal(this.dataDir)
					.filter(predicate);
			for (final File dataFile : dataFiles)
			{
				executeDataFile(
						classLoader,
						template,
						deserializer,
						fileNameGenerator,
						modelClass,
						this.templateClassName,
						this.mavenProject.getBasedir(),
						dataFile);
			}
			this.mavenProject.addCompileSourceRoot(this.outputDir.getPath());
		}
		catch (final TengException e)
		{
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	public static ClassLoader getClassLoader(
			final String templateClassName,
			final MavenProject mavenProject,
			final File outputDir)
	{
		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		if (MojoUtils.isClassAvailable(contextClassLoader, templateClassName))
		{
			return contextClassLoader;
		}
		else
		{
			throw new TengException("Unable to load class '" + templateClassName + "'.");
		}
	}

	public static final <T> T instantiateClass(final ClassLoader classLoader, final String className, final Class<T> classT)
	{
		final Class<?> class_ = MojoUtils.loadClass(classLoader, className);
		checkForCorrectInstance(className, classT, class_);
		@SuppressWarnings("unchecked")
		final Class<? extends T> classT_ = (Class<? extends T>) class_;
		return MojoUtils.newInstance(classT_);
	}

	public static void checkForCorrectInstance(final String templateClassName, final Class<?> expectedClass, final Class<?> actualClass)
	{
		if (!expectedClass.isAssignableFrom(actualClass))
		{
			throw new TengException("Unable to cast " + templateClassName + " to " + expectedClass.getName() + ".");
		}
	}

	public static void executeDataFile(
			final ClassLoader classLoader,
			final Template template,
			final Deserializer deserializer,
			final FileNameGenerator fileNameGenerator,
			final Class<Object> modelClass,
			final String templateClassName,
			final File baseDir,
			final File dataFile)
	{
		final Object model = deserializer.transform(dataFile, modelClass);
		final String result = MojoUtils.executeTemplate(classLoader, templateClassName, model);
		final File file = fileNameGenerator.generateFileName(baseDir, model, template);
		if (!readExistingFile(file).equals(result))
		{
			file.getParentFile().mkdirs();
			final FileWriter fileWriter = MojoUtils.fileWriter(file);
			try
			{
				MojoUtils.append(fileWriter, result);
			}
			finally
			{
				MojoUtils.close(fileWriter);
			}
		}
	}

	public static String readExistingFile(final File file)
	{
		if (file.exists())
		{
			try
			{
				return Files.toString(file, Charset.defaultCharset());
			}
			catch (final Exception e)
			{
				// Ignore.
			}
		}
		return "";
	}
}
