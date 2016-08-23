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

import com.google.common.base.Preconditions;

public final class TemplateLine
{
	public static final TemplateLine NONE = new TemplateLine();

	public static final TemplateLine templateLine(final int lineNumber, final String line)
	{
		return new TemplateLine(lineNumber, line);
	}

	public final int lineNumber;

	public final String line;

	public TemplateLine(final int lineNumber, final String line)
	{
		Preconditions.checkArgument(lineNumber > 0, "Invalid 'lineNumber'.");
		Preconditions.checkNotNull(line, "Missing 'line'.");
		this.lineNumber = lineNumber;
		this.line = line;
	}

	private TemplateLine()
	{
		this.lineNumber = 0;
		this.line = "";
	}
}
