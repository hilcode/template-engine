package org.example;

import java.util.UUID;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

public final class AuthorId
	extends
		Identifier<AuthorId>
{
	private static final Interner<AuthorId> INTERNER = Interners.newWeakInterner();

	public final static AuthorId make(final UUID value)
	{
		return INTERNER.intern(new AuthorId(value));
	}

	private AuthorId(final UUID value)
	{
		super(value);
	}
}
