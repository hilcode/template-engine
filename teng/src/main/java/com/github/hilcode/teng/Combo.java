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
import com.google.common.collect.ComparisonChain;

public final class Combo
	implements
		Comparable<Combo>
{
	public final ComboType comboType;

	public final int begin;

	public final int beginDelimiterLength;

	public final int end;

	public final int endDelimiterLength;

	public Combo(
			final ComboType comboType,
			final int begin,
			final int beginDelimiterLength,
			final int end,
			final int endDelimiterLength)
	{
		Preconditions.checkNotNull(comboType, "Missing '" + comboType + "'.");
		Preconditions.checkArgument(begin >= 0, "Invalid 'begin'.");
		Preconditions.checkArgument(beginDelimiterLength > 0, "Invalid 'beginDelimiterLength'.");
		Preconditions.checkArgument(endDelimiterLength > 0, "Invalid 'endDelimiterLength'.");
		Preconditions.checkArgument(begin + beginDelimiterLength < end, "Invalid 'end'.");
		this.comboType = comboType;
		this.begin = begin;
		this.beginDelimiterLength = beginDelimiterLength;
		this.end = end;
		this.endDelimiterLength = endDelimiterLength;
	}

	@Override
	public int compareTo(final Combo other)
	{
		return ComparisonChain
				.start()
				.compare(this.begin, other.begin)
				.compare(this.beginDelimiterLength, other.beginDelimiterLength)
				.compare(this.end, other.end)
				.compare(this.endDelimiterLength, other.endDelimiterLength)
				.compare(this.comboType, other.comboType)
				.result();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.begin;
		result = prime * result + this.beginDelimiterLength;
		result = prime * result + this.end;
		result = prime * result + this.endDelimiterLength;
		result = prime * result + this.comboType.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object object)
	{
		if (this == object)
		{
			return true;
		}
		if (object == null || getClass() != object.getClass())
		{
			return false;
		}
		final Combo other = (Combo) object;
		return compareTo(other) == 0;
	}

	@Override
	public String toString()
	{
		return "(Combo" +
				" begin=" + this.begin +
				" beginDelimiterLength=" + this.beginDelimiterLength +
				" end=" + this.end +
				" endDelimiterLength=" + this.endDelimiterLength +
				" comboType=" + this.comboType +
				")";
	}
}
