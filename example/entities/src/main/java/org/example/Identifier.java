/*
 * Copyright (C) 2017 H.C. Wijbenga
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
package org.example;

import java.util.UUID;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public abstract class Identifier<ID extends Identifier<ID>>
	implements
		Comparable<ID>
{
	public final UUID value;

	public Identifier(final UUID value)
	{
		Preconditions.checkNotNull(value, "Missing 'value'.");
		this.value = value;
	}

	@Override
	public int compareTo(final ID other)
	{
		return this.value.compareTo(other.value);
	}

	@Override
	public final int hashCode()
	{
		return 31 + this.value.hashCode();
	}

	@Override
	public final boolean equals(final Object object)
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
		final Identifier<ID> other = (Identifier<ID>) object;
		return this.value.equals(other.value);
	}

	@Override
	public final String toString()
	{
		return MoreObjects
				.toStringHelper(getClass())
				.add("value", this.value)
				.toString();
	}
}
