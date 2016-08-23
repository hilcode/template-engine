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
import java.io.StringReader;
import org.junit.Before;
import org.junit.Test;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

public class TengParserTest
{
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
		outputTemplateJavaFile(lines);
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
		outputTemplateJavaFile(lines);
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
		outputTemplateJavaFile(lines);
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
		outputTemplateJavaFile(lines);
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
		outputTemplateJavaFile(lines);
	}

	@Test
	public final void test6()
	{
		final ImmutableList<String> lines = ImmutableList.of(
				"# Copyright (C) 2016 Me",
				"",
				"template TheBlockTemplate6",
				"-----",
				"    @{TheBlockTemplate4()}@",
				"");
		outputTemplateJavaFile(lines);
	}

	@Test
	public final void test7()
	{
		final ImmutableList<String> lines = ImmutableList.of(
				"# Copyright (C) 2016 Me",
				"",
				"template TheBlockTemplate7",
				"-----",
				"    %{TheInlineTemplate3(\"Hello\", \"World!\")}%",
				"");
		outputTemplateJavaFile(lines);
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
		outputTemplateJavaFile(lines);
	}

	@Test
	public final void test9()
	{
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
		outputTemplateJavaFile(lines);
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
		outputTemplateJavaFile(lines);
	}

	@Test
	public final void field()
	{
		final ImmutableList<String> lines = ImmutableList.of(
				"template Field",
				"",
				"String type",
				"String name",
				"",
				"-----",
				"",
				"private ${type}$ ${name}$;",
				"");
		outputTemplateJavaFile(lines);
	}

	@Test
	public final void getter()
	{
		final ImmutableList<String> lines = ImmutableList.of(
				"template Getter",
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
		outputTemplateJavaFile(lines);
	}

	@Test
	public final void setter()
	{
		final ImmutableList<String> lines = ImmutableList.of(
				"template Setter",
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
		outputTemplateJavaFile(lines);
	}

	@Test
	public final void entity()
	{
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
				"\t@{ Field ( loopVar.item.type, loopVar.item.name ) }@",
				"    #end-for",
				"    #for com.github.hilcode.teng.stuff.FieldInfo loopVar : fields",
				"",
				"\t@{Getter(loopVar.item.type, loopVar.item.name)}@",
				"",
				"\t@{Setter(loopVar.item.type, loopVar.item.name)}@",
				"    #end-for",
				"}");
		outputTemplateJavaFile(lines);
	}

	public final void outputTemplateJavaFile(final ImmutableList<String> lines)
	{
		final File rootDir = new File("target/generated-sources/teng");
		final StringReader source = new StringReader(Joiner.on('\n').join(lines) + "\n");
		final JavaTemplate template = this.parser.parse(source);
		final File packageDir = new File(rootDir, template.javaFile.packageName.value.replace('.', '/'));
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
}
