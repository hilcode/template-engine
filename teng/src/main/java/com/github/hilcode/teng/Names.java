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
package com.github.hilcode.teng;

import java.util.Iterator;
import java.util.regex.Pattern;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

public final class Names
{
	private static final Pattern VALID_FIELD_NAME = Pattern.compile("[A-Z][A-Z0-9]*(_[A-Z0-9]+)*");

	public static final <E extends Enum<E>> String extractJavaClassName(final Enum<E> instance)
	{
		final String fieldName = instance.name();
		Preconditions.checkArgument(VALID_FIELD_NAME.matcher(fieldName).matches(), "Invalid field name: '" + fieldName + "'.");
		final StringBuilder sb = new StringBuilder();
		final Iterator<String> namePartIt = Splitter.on('_').split(fieldName).iterator();
		while (namePartIt.hasNext())
		{
			sb.append(capitalize(namePartIt.next()));
		}
		return sb.toString();
	}

	public static final <E extends Enum<E>> String extractJavaFieldName(final Enum<E> instance)
	{
		final String fieldName = instance.name();
		Preconditions.checkArgument(VALID_FIELD_NAME.matcher(fieldName).matches(), "Invalid field name: '" + fieldName + "'.");
		final StringBuilder sb = new StringBuilder();
		final Iterator<String> namePartIt = Splitter.on('_').split(fieldName).iterator();
		sb.append(namePartIt.next().toLowerCase());
		while (namePartIt.hasNext())
		{
			sb.append(capitalize(namePartIt.next()));
		}
		return sb.toString();
	}

	public static final String capitalize(final String text)
	{
		Preconditions.checkNotNull(text, "Missing 'text'.");
		return text.isEmpty()
				? text
				: text.length() == 1
						? text.toUpperCase()
						: Character.toUpperCase(text.charAt(0)) + text.substring(1).toLowerCase();
	}
}
