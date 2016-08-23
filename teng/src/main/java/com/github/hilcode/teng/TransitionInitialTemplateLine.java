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

import static com.github.hilcode.teng.LineAndSource.lineAndSource;
import static com.github.hilcode.teng.Mode.PARAMETERS;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.github.hilcode.teng.StateMachine.ModeAndState;
import com.google.common.base.Preconditions;

public final class TransitionInitialTemplateLine
	implements
		StateMachine.Transition<Line, Mode, TengParserState>
{
	private static final Pattern TEMPLATE_LINE;
	static
	{
		TEMPLATE_LINE = Pattern.compile("^template[ \t]+(inline[ \t]+)?(([a-zA-Z_][A-Za-z0-9_]*[.])*[A-Z][A-Za-z0-9_]*)$");
	}

	@Override
	public ModeAndState<Mode, TengParserState> apply(final Mode mode, final TengParserState state, final Line line)
	{
		Preconditions.checkNotNull(mode, "Missing 'mode'.");
		Preconditions.checkNotNull(state, "Missing 'state'.");
		Preconditions.checkNotNull(line, "Missing 'line'.");
		final Matcher matcher = TEMPLATE_LINE.matcher(line.text);
		if (matcher.matches())
		{
			state.blockTemplate = matcher.group(1) == null;
			final String templateName_ = matcher.group(2);
			final int lastDotOffset = templateName_.lastIndexOf('.');
			state.templateName = lastDotOffset == -1
					? new JavaFile(templateName_)
					: new JavaFile(new PackageName(templateName_.substring(0, lastDotOffset)), templateName_.substring(lastDotOffset + 1));
			if (state.templateName.packageName != PackageName.NONE)
			{
				state.templateBuilder.add(lineAndSource(String.format("package %s;", state.templateName.packageName.value)));
				state.templateBuilder.add(lineAndSource(""));
			}
			state.templateBuilder.add(lineAndSource("public final class " + state.templateName.name + " implements Template", TemplateLine.templateLine(line.number, line.text)));
			state.templateBuilder.add(lineAndSource("{"));
			state.templateBuilder.add(lineAndSource("    public static final Template." + (state.blockTemplate ? "Block" : "Inline") + " execute("));
			return new ModeAndState<Mode, TengParserState>(PARAMETERS, state);
		}
		else
		{
			return ModeAndState.none();
		}
	}
}
