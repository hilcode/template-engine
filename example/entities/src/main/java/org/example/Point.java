package org.example;

public final class Point
{
	private int x;

	private int y;

	public int x()
	{
		return x;
	}

	public Object x(final int value)
	{
		this.x = value;
		return this;
	}

	public int y()
	{
		return y;
	}

	public Object y(final int value)
	{
		this.y = value;
		return this;
	}
}
