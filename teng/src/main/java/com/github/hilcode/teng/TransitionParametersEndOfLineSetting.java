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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.github.hilcode.teng.StateMachine.ModeAndState;

public final class TransitionParametersEndOfLineSetting
	implements
		StateMachine.Transition<Line, Mode, TengParserState>
{
	private static final Pattern END_OF_LINE_SETTING_LINE;
	static
	{
		END_OF_LINE_SETTING_LINE = Pattern.compile("^end-of-line[ \t]+(native|unix|windows)$");
	}

	@Override
	public ModeAndState<Mode, TengParserState> apply(final Mode mode, final TengParserState state, final Line line)
	{
		final Matcher endOfLineSettingLineMatcher = END_OF_LINE_SETTING_LINE.matcher(line.text);
		if (endOfLineSettingLineMatcher.matches())
		{
			state.endOfLine = EndOfLine.valueOf(endOfLineSettingLineMatcher.group(1).toUpperCase());
			return new ModeAndState<Mode, TengParserState>(mode, state);
		}
		else
		{
			return ModeAndState.none();
		}
	}
}