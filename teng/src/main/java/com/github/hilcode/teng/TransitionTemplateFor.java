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

public final class TransitionTemplateFor
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
		if (trimmedLine.startsWith(state.directivePrefix + "for ") || trimmedLine.startsWith(state.directivePrefix + "for\t"))
		{
			final String originalExpression = trimmedLine.substring(state.directivePrefix.length() + 4).trim();
			final String expression = originalExpression.startsWith("final ") || originalExpression.startsWith("final\t")
					? originalExpression.substring("final ".length()).trim()
					: originalExpression;
			final int indexOfColon = expression.indexOf(':');
			final String collection = expression.substring(indexOfColon + 1).trim();
			final String typeAndVar = expression.substring(0, indexOfColon).trim();
			final int indexOfLastSpace = Math.max(typeAndVar.lastIndexOf(' '), typeAndVar.lastIndexOf('\t'));
			final String loopVariableName = typeAndVar.substring(indexOfLastSpace + 1);
			final String type = typeAndVar.substring(0, indexOfLastSpace);
			state.templateBuilder.add(
					lineAndSource(
							repeat("    ", state.indentationLevel + 4) + "for (final LoopVar<" + type + "> " + loopVariableName + " : Teng.loop(" + collection + "))",
							templateLine(line.number, line.text)));
			state.templateBuilder.add(lineAndSource(repeat("    ", state.indentationLevel + 4) + "{"));
			state.indentationLevel++;
			state.loopVarNeeded = true;
			state.tengNeeded = true;
			return new ModeAndState<Mode, TengParserState>(TEMPLATE, state);
		}
		else
		{
			return ModeAndState.none();
		}
	}
}
