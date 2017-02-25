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

import static com.github.hilcode.teng.EndOfLine.UNIX;
import java.util.List;
import com.google.common.collect.Lists;

public final class TengParserState
{
	public final List<LineAndSource> templateBuilder;

	public final List<Parameter> parameters;

	public final List<Import> imports;

	public int indentationLevel;

	public boolean loopVarNeeded;

	public boolean tengNeeded;

	public boolean immutableListNeeded;

	public JavaFile templateName;

	public boolean blockTemplate;

	public String blockTemplateBegin;

	public String blockTemplateEnd;

	public String inlineTemplateBegin;

	public String inlineTemplateEnd;

	public String variableBegin;

	public String variableEnd;

	public String directivePrefix;

	public EndOfLine endOfLine;

	public TengParserState()
	{
		this.templateBuilder = Lists.newArrayList();
		this.parameters = Lists.newArrayList();
		this.imports = Lists.newArrayList();
		this.indentationLevel = 0;
		this.loopVarNeeded = false;
		this.tengNeeded = false;
		this.immutableListNeeded = false;
		this.templateName = null;
		this.blockTemplate = true;
		this.blockTemplateBegin = "@{";
		this.blockTemplateEnd = "}@";
		this.inlineTemplateBegin = "%{";
		this.inlineTemplateEnd = "}%";
		this.variableBegin = "${";
		this.variableEnd = "}$";
		this.directivePrefix = "#";
		this.endOfLine = UNIX;
	}
}
