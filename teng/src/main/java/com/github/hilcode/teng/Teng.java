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

import java.io.IOException;
import java.util.Collection;
import com.google.common.collect.ImmutableList;

public final class Teng
{
	public static final String show(final boolean value)
	{
		return String.valueOf(value);
	}

	public static final String show(final byte value)
	{
		return String.valueOf(value);
	}

	public static final String show(final short value)
	{
		return String.valueOf(value);
	}

	public static final String show(final char value)
	{
		return String.valueOf(value);
	}

	public static final String show(final int value)
	{
		return String.valueOf(value);
	}

	public static final String show(final long value)
	{
		return String.valueOf(value);
	}

	public static final String show(final float value)
	{
		return String.valueOf(value);
	}

	public static final String show(final double value)
	{
		return String.valueOf(value);
	}

	public static final String show(final String value)
	{
		return value;
	}

	public static final String show(final Object value)
	{
		return value.toString();
	}

	public static final Iterable<String> execute(final String prefix, final Template.Block template)
	{
		final ImmutableList.Builder<String> linesBuilder = ImmutableList.builder();
		for (final String line : template.execute())
		{
			linesBuilder.add(line.trim().isEmpty() ? line : prefix + line);
		}
		return linesBuilder.build();
	}

	public static final String execute(final Template.Inline template)
	{
		return template.execute();
	}

	public static final <T> Iterable<LoopVar<T>> loop(final Collection<T> collection)
	{
		final ImmutableList.Builder<LoopVar<T>> listBuilder = ImmutableList.builder();
		final int count = collection.size();
		int index = 0;
		for (final T item : collection)
		{
			listBuilder.add(LoopVar.make(index == 0, index == count - 1, index, count, item));
			index++;
		}
		return listBuilder.build();
	}

	public static final <T> Iterable<LoopVar<T>> loop(final Iterable<T> collection)
	{
		if (collection instanceof Collection)
		{
			return loop((Collection<T>) collection);
		}
		else
		{
			return loop(ImmutableList.copyOf(collection));
		}
	}

	public static final void execute(final Template.Block template, final Appendable writer)
	{
		for (final String line : template.execute())
		{
			try
			{
				writer.append(line);
			}
			catch (final IOException e)
			{
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
	}
}
