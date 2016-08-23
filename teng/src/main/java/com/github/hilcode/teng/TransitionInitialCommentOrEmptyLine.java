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

import static com.github.hilcode.teng.Mode.INITIAL;
import com.github.hilcode.teng.StateMachine.ModeAndState;
import com.google.common.base.Preconditions;

public final class TransitionInitialCommentOrEmptyLine
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
		return trimmedLine.isEmpty() || trimmedLine.startsWith("#")
				? new ModeAndState<Mode, TengParserState>(INITIAL, state)
				: ModeAndState.<Mode, TengParserState> none();
	}
}
