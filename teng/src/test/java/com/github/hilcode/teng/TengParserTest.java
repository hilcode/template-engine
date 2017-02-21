/*
 * Copyright (C) 2016 H.C. Wijbenga
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
package com.github.hilcode.teng;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import org.junit.Before;
import org.junit.Test;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class TengParserTest
{
	private static final File TARGET_DIR = new File("target");

	private static final File CLASSES_DIR = new File(TARGET_DIR, "classes");

	private static final File TARGET_GENERATEDSOURCES_DIR = new File(TARGET_DIR, "generated-sources");

	private static final File TENG_DIR = new File(TARGET_GENERATEDSOURCES_DIR, "teng");

	private static final File TENG_CLASSES_DIR = new File(TARGET_GENERATEDSOURCES_DIR, "classes");

	private TengParser parser;

	@Before
	public void setUp()
	{
		this.parser = new TengParser();
	}

	@Test
	public final void test1()
	{
		final ImmutableList<String> lines = ImmutableList.of(
				"# Copyright (C) 2016 Me",
				"",
				"template TheBlockTemplate1",
				"",
				"String type",
				"-----");
		compileTemplateJavaFile(outputTemplateJavaFile(lines));
	}

	@Test
	public final void test2()
	{
		final ImmutableList<String> lines = ImmutableList.of(
				"# Copyright (C) 2016 Me",
				"",
				"template TheBlockTemplate2",
				"",
				"String type",
				"-----",
				"xxx");
		compileTemplateJavaFile(outputTemplateJavaFile(lines));
	}

	@Test
	public final void test3()
	{
		final ImmutableList<String> lines = ImmutableList.of(
				"# Copyright (C) 2016 Me",
				"",
				"template\t inline\t TheInlineTemplate3",
				"String type",
				"String name",
				"-----",
				"xxx",
				"yyy");
		compileTemplateJavaFile(outputTemplateJavaFile(lines));
	}

	@Test
	public final void test4()
	{
		final ImmutableList<String> lines = ImmutableList.of(
				"# Copyright (C) 2016 Me",
				"",
				"template TheBlockTemplate4",
				"# bla bla",
				"block-template-delimiters  ##@{   }@##",
				"inline-template-delimiters ##%{   }%##",
				"variable-delimiters        ##${   }$##",
				"directive-prefix           ###",
				"end-of-line                windows",
				"-----",
				"xxx");
		compileTemplateJavaFile(outputTemplateJavaFile(lines));
	}

	@Test
	public final void test5()
	{
		final ImmutableList<String> lines = ImmutableList.of(
				"# Copyright (C) 2016 Me",
				"",
				"template TheBlockTemplate5",
				"java.util.Collection<Object> beeps",
				"java.util.Collection<String> beeps2",
				"-----",
				"verbatim",
				"#if \"hello\" == \"world\" && \"bye\" == \"goodBye\"",
				"ver\"batim",
				"#if \"hello\" == \"world\" && \"bye\" == \"goodBye\"",
				"verbatim",
				"#else-if 1 == 1",
				"verbatim",
				"#else",
				"verbatim",
				"#end-if",
				"verbatim",
				"#else-if 1 == 1",
				"verbatim",
				"#if \"hello\" == \"world\" && \"bye\" == \"goodBye\"",
				"verbatim",
				"#else-if 1 == 1",
				"verbatim",
				"#else",
				"verbatim",
				"#end-if",
				"verbatim",
				"#else",
				"verbatim",
				"#if \"hello\" == \"world\" && \"bye\" == \"goodBye\"",
				"verbatim",
				"#else-if 1 == 1",
				"verbatim",
				"#else",
				"#for final Object beep : beeps",
				"#for String beep2 : beeps2",
				"verbatim",
				"#end-for",
				"#end-for",
				"#end-if",
				"verbatim",
				"#end-if",
				"verbatim",
				"");
		compileTemplateJavaFile(outputTemplateJavaFile(lines));
	}

	@Test
	public final void test6()
	{
		test4();
		final ImmutableList<String> lines = ImmutableList.of(
				"# Copyright (C) 2016 Me",
				"",
				"template TheBlockTemplate6",
				"-----",
				"    @{TheBlockTemplate4()}@",
				"");
		compileTemplateJavaFile(outputTemplateJavaFile(lines));
	}

	@Test
	public final void test7()
	{
		test3();
		final ImmutableList<String> lines = ImmutableList.of(
				"# Copyright (C) 2016 Me",
				"",
				"template TheBlockTemplate7",
				"-----",
				"    %{TheInlineTemplate3(\"Hello\", \"World!\")}%",
				"");
		compileTemplateJavaFile(outputTemplateJavaFile(lines));
	}

	@Test
	public final void test8()
	{
		final ImmutableList<String> lines = ImmutableList.of(
				"# Copyright (C) 2016 Me",
				"",
				"template TheBlockTemplate8",
				"java.util.List<Object> list",
				"-----",
				"    ${list.size()}$",
				"");
		compileTemplateJavaFile(outputTemplateJavaFile(lines));
	}

	@Test
	public final void test9()
	{
		test3();
		test4();
		final ImmutableList<String> lines = ImmutableList.of(
				"# Copyright (C) 2016 Me",
				"",
				"template TheBlockTemplate9",
				"String name",
				"String type",
				"-----",
				"    ${name.length()}$    %{TheInlineTemplate3(type, name)}%    @{TheBlockTemplate4()}@",
				"",
				"    ${type.charAt(0)}$    %{TheInlineTemplate3(type, name)}%    @{TheBlockTemplate4()}@",
				"");
		compileTemplateJavaFile(outputTemplateJavaFile(lines));
	}

	@Test
	public final void test10()
	{
		final ImmutableList<String> lines = ImmutableList.of(
				"# Copyright (C) 2016 Me",
				"",
				"template TheBlockTemplate10",
				"",
				"java.util.Map<String, Object> map",
				"java.util.List<String>        list",
				"java.util.Set<Object>         set",
				"",
				"-----",
				"verbatim",
				"#for final String key : map.keySet()",
				"    #for String item : list",
				"        #for Object beep3:set",
				"            verbatim",
				"        #end-for",
				"    #end-for",
				"#end-for",
				"");
		compileTemplateJavaFile(outputTemplateJavaFile(lines));
	}

	@Test
	public final void field()
	{
		final ImmutableList<String> lines = ImmutableList.of(
				"template org.example.Field",
				"",
				"String type",
				"String name",
				"",
				"-----",
				"",
				"private ${type}$ ${name}$;",
				"");
		compileTemplateJavaFile(outputTemplateJavaFile(lines));
	}

	@Test
	public final void getter()
	{
		final ImmutableList<String> lines = ImmutableList.of(
				"template org.example.Getter",
				"",
				"String type",
				"String name",
				"",
				"-----",
				"",
				"public ${type}$ ${name}$()",
				"{",
				"\treturn ${name}$;",
				"}",
				"");
		compileTemplateJavaFile(outputTemplateJavaFile(lines));
	}

	@Test
	public final void setter()
	{
		final ImmutableList<String> lines = ImmutableList.of(
				"template org.example.Setter",
				"",
				"String type",
				"String name",
				"",
				"-----",
				"",
				"public Object ${name}$(final ${type}$ value)",
				"{",
				"\tthis.${name}$ = value;",
				"\treturn this;",
				"}",
				"");
		compileTemplateJavaFile(outputTemplateJavaFile(lines));
	}

	@Test
	public final void entity()
	{
		field();
		getter();
		setter();
		final ImmutableList<String> lines = ImmutableList.of(
				"template Entity",
				"",
				"String name",
				"ImmutableList<com.github.hilcode.teng.stuff.FieldInfo> fields",
				"",
				"-----",
				"public final class ${name}$",
				"{",
				"    #for com.github.hilcode.teng.stuff.FieldInfo loopVar : fields",
				"\t@{ org.example.Field ( loopVar.item.type(), loopVar.item.name() ) }@",
				"    #end-for",
				"    #for com.github.hilcode.teng.stuff.FieldInfo loopVar : fields",
				"",
				"\t@{org.example.Getter(loopVar.item.type(), loopVar.item.name())}@",
				"",
				"\t@{org.example.Setter(loopVar.item.type(), loopVar.item.name())}@",
				"    #end-for",
				"}");
		compileTemplateJavaFile(outputTemplateJavaFile(lines));
	}

	@Test
	public final void importClass()
	{
		field();
		getter();
		setter();
		final ImmutableList<String> lines = ImmutableList.of(
				"template org.example.Entity",
				"",
				"import com.github.hilcode.teng.stuff.FieldInfo",
				"",
				"String name",
				"ImmutableList<FieldInfo> fields",
				"",
				"-----",
				"public final class ${name}$",
				"{",
				"    #for FieldInfo loopVar : fields",
				"\t@{ org.example.Field ( loopVar.item.type(), loopVar.item.name() ) }@",
				"    #end-for",
				"    #for FieldInfo loopVar : fields",
				"",
				"\t@{org.example.Getter(loopVar.item.type(), loopVar.item.name())}@",
				"",
				"\t@{org.example.Setter(loopVar.item.type(), loopVar.item.name())}@",
				"    #end-for",
				"}");
		compileTemplateJavaFile(outputTemplateJavaFile(lines));
	}

	@Test
	public final void importSeveralClasses()
	{
		field();
		getter();
		setter();
		final ImmutableList<String> lines = ImmutableList.of(
				"template org.example.Entity2",
				"",
				"import com.github.hilcode.teng.stuff.FieldInfo",
				"import org.example.Getter",
				"import org.example.Setter",
				"",
				"String name",
				"ImmutableList<FieldInfo> fields",
				"",
				"-----",
				"public final class ${name}$",
				"{",
				"    #for FieldInfo loopVar : fields",
				"\t@{ Field ( loopVar.item.type(), loopVar.item.name() ) }@",
				"    #end-for",
				"    #for FieldInfo loopVar : fields",
				"",
				"\t@{Getter(loopVar.item.type(), loopVar.item.name())}@",
				"",
				"\t@{Setter(loopVar.item.type(), loopVar.item.name())}@",
				"    #end-for",
				"}");
		compileTemplateJavaFile(outputTemplateJavaFile(lines));
	}

	public final File outputTemplateJavaFile(final ImmutableList<String> lines)
	{
		final StringReader source = new StringReader(Joiner.on('\n').join(lines) + "\n");
		final JavaTemplate template = this.parser.parse(source);
		final File packageDir = new File(TENG_DIR, template.javaFile.packageName.value.replace('.', '/'));
		packageDir.mkdirs();
		final File javaFile = new File(packageDir, template.javaFile.name + ".java");
		try
		{
			final FileWriter fw = new FileWriter(javaFile);
			try
			{
				for (final String line : template)
				{
					fw.append(line);
				}
				return javaFile;
			}
			finally
			{
				fw.close();
			}
		}
		catch (final Exception e)
		{
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public final void compileTemplateJavaFile(final File javaFile)
	{
		final JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		final StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(diagnostics, Locale.getDefault(), Charset.forName("UTF-8"));
		try
		{
			CLASSES_DIR.mkdirs();
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Lists.newArrayList(CLASSES_DIR));
			fileManager.setLocation(StandardLocation.CLASS_PATH, extractClasspath());
			fileManager.setLocation(StandardLocation.SOURCE_PATH, Lists.newArrayList(TENG_DIR));
		}
		catch (final IOException e)
		{
			throw new IllegalStateException(e.getMessage(), e);
		}
		final Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(javaFile));
		final boolean result = javaCompiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits).call().booleanValue();
		if (!result)
		{
			for (final Diagnostic<?> error : diagnostics.getDiagnostics())
			{
				System.err.println(error);
			}
			throw new IllegalStateException("Compilation failed.");
		}
	}

	public final ImmutableList<File> extractClasspath()
	{
		final String javaClasspath = System.getProperty("java.class.path");
		final String pathSeparator = System.getProperty("path.separator");
		final ImmutableList.Builder<File> classpathBuilder = ImmutableList.builder();
		for (final String classpathEntry : Splitter.on(pathSeparator).split(javaClasspath))
		{
			classpathBuilder.add(new File(classpathEntry));
		}
		classpathBuilder.add(TENG_CLASSES_DIR);
		return classpathBuilder.build();
	}
}
