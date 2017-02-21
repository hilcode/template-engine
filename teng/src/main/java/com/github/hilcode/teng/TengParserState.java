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

import static com.github.hilcode.teng.EndOfLine.NATIVE;
import java.util.List;
import com.google.common.collect.Lists;

public final class TengParserState
{
	public final List<LineAndSource> templateBuilder;

	public boolean loopVarNeeded = false;

	public boolean tengNeeded = false;

	public boolean immutableListNeeded = false;

	public JavaFile templateName = null;

	public boolean blockTemplate = true;

	public String blockTemplateBegin = "@{";

	public String blockTemplateEnd = "}@";

	public String inlineTemplateBegin = "%{";

	public String inlineTemplateEnd = "}%";

	public String variableBegin = "${";

	public String variableEnd = "}$";

	public String directivePrefix = "#";

	public EndOfLine endOfLine = NATIVE;

	public int indentationLevel = 0;

	public final List<Parameter> parameters = Lists.newArrayList();

	public final List<Import> imports = Lists.newArrayList();

	public TengParserState()
	{
		this.templateBuilder = Lists.newArrayList();
	}
}
