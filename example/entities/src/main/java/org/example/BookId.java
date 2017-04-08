package org.example;

import java.util.UUID;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

public final class BookId
	extends
		Identifier<BookId>
{
	private static final Interner<BookId> INTERNER = Interners.newWeakInterner();

	public final static BookId make(final UUID value)
	{
		return INTERNER.intern(new BookId(value));
	}

	private BookId(final UUID value)
	{
		super(value);
	}
}
