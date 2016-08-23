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
import static com.github.hilcode.teng.Mode.TEMPLATE;
import static com.github.hilcode.teng.TemplateLine.templateLine;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.github.hilcode.teng.StateMachine.ModeAndState;

public final class TransitionParametersFinish
	implements
		StateMachine.Transition<Line, Mode, TengParserState>
{
	private static final Pattern FINISH_LINE;
	static
	{
		FINISH_LINE = Pattern.compile("^-----$");
	}

	@Override
	public ModeAndState<Mode, TengParserState> apply(final Mode mode, final TengParserState state, final Line line)
	{
		final Matcher finishLineMatcher = FINISH_LINE.matcher(line.text);
		if (finishLineMatcher.matches())
		{
			final Iterator<Parameter> parameterIt = state.parameters.iterator();
			while (parameterIt.hasNext())
			{
				final Parameter parameter = parameterIt.next();
				if (parameterIt.hasNext())
				{
					state.templateBuilder.add(
							lineAndSource(
									"        final " + parameter.type + " " + parameter.name + ",",
									templateLine(parameter.lineNumber, parameter.line)));
				}
				else
				{
					state.templateBuilder.add(
							lineAndSource(
									"        final " + parameter.type + " " + parameter.name,
									templateLine(parameter.lineNumber, parameter.line)));
				}
			}
			state.templateBuilder.add(lineAndSource("    )"));
			state.templateBuilder.add(lineAndSource("    {"));
			state.templateBuilder.add(lineAndSource("        return new Template." + (state.blockTemplate ? "Block" : "Inline") + "()"));
			state.templateBuilder.add(lineAndSource("        {"));
			state.templateBuilder.add(lineAndSource("            @Override"));
			state.templateBuilder.add(lineAndSource("            public " + (state.blockTemplate ? "Iterable<String>" : "String") + " execute()"));
			state.templateBuilder.add(lineAndSource("            {"));
			if (state.blockTemplate)
			{
				state.templateBuilder.add(lineAndSource("                final ImmutableList.Builder<String> lines = ImmutableList.builder();"));
				state.immutableListNeeded = true;
			}
			else
			{
				state.templateBuilder.add(lineAndSource("                final StringBuilder lineBuilder = new StringBuilder();"));
			}
			return new ModeAndState<Mode, TengParserState>(TEMPLATE, state);
		}
		else
		{
			return ModeAndState.none();
		}
	}
}
