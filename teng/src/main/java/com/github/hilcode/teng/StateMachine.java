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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public final class StateMachine<INPUT, MODE, STATE>
{
	private final ImmutableMap<MODE, ImmutableList<Transition<INPUT, MODE, STATE>>> modes;

	public StateMachine(final ImmutableMap<MODE, ImmutableList<Transition<INPUT, MODE, STATE>>> modes)
	{
		Preconditions.checkNotNull(modes, "Missing 'modes'.");
		this.modes = modes;
	}

	public ModeAndState<MODE, STATE> transition(final MODE mode, final STATE state, final INPUT input)
	{
		Preconditions.checkNotNull(mode, "Missing 'mode'.");
		Preconditions.checkNotNull(state, "Missing 'state'.");
		Preconditions.checkNotNull(input, "Missing 'input'.");
		final ImmutableList<Transition<INPUT, MODE, STATE>> transitions = this.modes.get(mode);
		for (final Transition<INPUT, MODE, STATE> transition : transitions)
		{
			final ModeAndState<MODE, STATE> modeAndState = transition.apply(mode, state, input);
			if (modeAndState != ModeAndState.NONE)
			{
				return modeAndState;
			}
		}
		throw new IllegalStateException("No applicable transition found.");
	}

	public static final class ModeAndState<MODE, STATE>
	{
		private static final ModeAndState<?, ?> NONE = new ModeAndState<Object, Object>();

		public static final <MODE, STATE> ModeAndState<MODE, STATE> none()
		{
			@SuppressWarnings("unchecked")
			final ModeAndState<MODE, STATE> none = (ModeAndState<MODE, STATE>) NONE;
			return none;
		}

		public final MODE mode;

		public final STATE state;

		public ModeAndState(final MODE mode, final STATE state)
		{
			Preconditions.checkNotNull(mode, "Missing 'mode'.");
			Preconditions.checkNotNull(state, "Missing 'state'.");
			this.mode = mode;
			this.state = state;
		}

		private ModeAndState()
		{
			this.mode = null;
			this.state = null;
		}
	}

	public interface Transition<INPUT, MODE, STATE>
	{
		ModeAndState<MODE, STATE> apply(MODE mode, STATE state, INPUT input);
	}
}
