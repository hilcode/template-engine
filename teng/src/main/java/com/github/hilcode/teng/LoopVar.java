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

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public final class LoopVar<E>
{
	public static final <T> LoopVar<T> make(
			final boolean first,
			final boolean last,
			final int index,
			final int count,
			final T item)
	{
		Preconditions.checkArgument(index >= 0, "Invalid 'index'; 'index' must be nonnegative.");
		Preconditions.checkArgument(index < count, "Invalid 'count'; 'index' must be less than 'count'.");
		Preconditions.checkNotNull(item, "Missing 'item'.");
		return new LoopVar<T>(first, last, index, count, item);
	}

	public final boolean first;
	public final boolean last;
	public final int index;
	public final int count;
	public final E item;

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.first ? 1231 : 1237);
		result = prime * result + (this.last ? 1231 : 1237);
		result = prime * result + this.index;
		result = prime * result + this.count;
		result = prime * result + this.item.hashCode();
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
		@SuppressWarnings("unchecked")
		final LoopVar<E> other = (LoopVar<E>) object;
		return this.first == other.first &&
				this.last == other.last &&
				this.index == other.index &&
				this.count == other.count &&
				this.item.equals(other.item);
	}

	@Override
	public String toString()
	{
		return MoreObjects
				.toStringHelper(getClass())
				.add("first", this.first)
				.add("last", this.last)
				.add("index", this.index)
				.add("count", this.count)
				.add("item", this.item)
				.toString();
	}

	private LoopVar(
			final boolean first,
			final boolean last,
			final int index,
			final int count,
			final E item)
	{
		this.first = first;
		this.last = last;
		this.index = index;
		this.count = count;
		this.item = item;
	}
}
