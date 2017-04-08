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

import com.google.common.collect.ImmutableList;

public interface BluePrint
{
	public static enum Entity
	{
		PUBLISHER(
				Field.PUBLISHER_ID,
				Field.PUBLISHER_NAME,
				Field.INCORPORATION_YEAR),
		BOOK(
				Field.BOOK_ID,
				Field.AUTHOR,
				Field.TITLE,
				Field.PUBLISH_YEAR,
				Field.ISBN),
		AUTHOR(
				Field.AUTHOR_ID,
				Field.AUTHOR_NAME),
		BOOK_SERIES(
				Field.BOOK_SERIES_ID,
				Field.BOOK_SERIES_NAME,
				Field.BOOKS),
		//
		;
		public final ImmutableList<Field> fields;

		private Entity(final Field... fields)
		{
			this.fields = ImmutableList.copyOf(fields);
		}
	}

	public static enum Field
	{
		PUBLISHER("publisher", Type.PUBLISHER),
		PUBLISHER_ID("publisherId", Type.PUBLISHER_ID),
		BOOK("book", Type.BOOK),
		BOOK_ID("bookId", Type.BOOK_ID),
		AUTHOR("author", Type.AUTHOR),
		AUTHOR_ID("authorId", Type.AUTHOR_ID),
		BOOK_SERIES("bookSeries", Type.BOOK_SERIES),
		BOOK_SERIES_ID("bookSeriesId", Type.BOOK_SERIES_ID),
		TITLE("title", Type.TEXT_256),
		PUBLISHER_NAME("name", Type.TEXT_32),
		BOOK_SERIES_NAME("name", Type.TEXT_32),
		AUTHOR_NAME("name", Type.TEXT_128),
		PUBLISH_YEAR("publishYear", Type.YEAR),
		INCORPORATION_YEAR("incorporationYear", Type.YEAR),
		ISBN("isbn", Type.ISBN),
		BOOKS("books", Type.BOOKS),
		//
		;
		public final String name;

		public final Type type;

		private Field(final String name, final Type type)
		{
			this.name = name;
			this.type = type;
		}
	}

	public static enum Type
	{
		TEXT_32,
		TEXT_128,
		TEXT_256,
		YEAR,
		ISBN,
		AUTHOR,
		AUTHOR_ID,
		BOOK,
		BOOK_ID,
		BOOK_SERIES,
		BOOK_SERIES_ID,
		PUBLISHER,
		PUBLISHER_ID,
		BOOKS,
	}
}
