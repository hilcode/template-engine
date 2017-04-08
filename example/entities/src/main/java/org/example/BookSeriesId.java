package org.example;

import java.util.UUID;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

public final class BookSeriesId
	extends
		Identifier<BookSeriesId>
{
	private static final Interner<BookSeriesId> INTERNER = Interners.newWeakInterner();

	public final static BookSeriesId make(final UUID value)
	{
		return INTERNER.intern(new BookSeriesId(value));
	}

	private BookSeriesId(final UUID value)
	{
		super(value);
	}
}
