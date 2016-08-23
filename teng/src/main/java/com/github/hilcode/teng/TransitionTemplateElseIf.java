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
import static com.google.common.base.Strings.repeat;
import com.github.hilcode.teng.StateMachine.ModeAndState;
import com.google.common.base.Preconditions;

public final class TransitionTemplateElseIf
	implements
		StateMachine.Transition<Line, Mode, TengParserState>
{
	@Override
	public ModeAndState<Mode, TengParserState> apply(final Mode mode, final TengParserState state, final Line line)
	{
		Preconditions.checkNotNull(mode, "Missing 'mode'.");
		Preconditions.checkNotNull(state, "Missing 'state'.");
		Preconditions.checkNotNull(line, "Missing 'line'.");
		final String trimmedLine = line.text.trim();
		if (trimmedLine.startsWith(state.directivePrefix + "else-if ") || trimmedLine.startsWith(state.directivePrefix + "else-if\t"))
		{
			state.indentationLevel--;
			state.templateBuilder.add(lineAndSource(repeat("    ", state.indentationLevel + 4) + "}"));
			final String expression = trimmedLine.substring(state.directivePrefix.length() + 8).trim();
			state.templateBuilder.add(lineAndSource(repeat("    ", state.indentationLevel + 4) + "else if (" + expression + ")", templateLine(line.number, line.text)));
			state.templateBuilder.add(lineAndSource(repeat("    ", state.indentationLevel + 4) + "{"));
			state.indentationLevel++;
			return new ModeAndState<Mode, TengParserState>(TEMPLATE, state);
		}
		else
		{
			return ModeAndState.none();
		}
	}
}
