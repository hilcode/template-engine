package org.example;

import java.util.UUID;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

public final class PublisherId
	extends
		Identifier<PublisherId>
{
	private static final Interner<PublisherId> INTERNER = Interners.newWeakInterner();

	public final static PublisherId make(final UUID value)
	{
		return INTERNER.intern(new PublisherId(value));
	}

	private PublisherId(final UUID value)
	{
		super(value);
	}
}
