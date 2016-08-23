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

public final class LineAndSource
{
	public static final LineAndSource NONE = new LineAndSource();

	public static final LineAndSource lineAndSource(final String lineOfCode, final TemplateLine templateLine)
	{
		return new LineAndSource(lineOfCode, templateLine);
	}

	public static final LineAndSource lineAndSource(final String lineOfCode)
	{
		return new LineAndSource(lineOfCode, TemplateLine.NONE);
	}

	public final String lineOfCode;

	public final TemplateLine templateLine;

	public LineAndSource(final String lineOfCode, final TemplateLine templateLine)
	{
		Preconditions.checkNotNull(lineOfCode, "Missing 'lineOfCode'.");
		Preconditions.checkNotNull(templateLine, "Missing 'templateLine'.");
		this.lineOfCode = lineOfCode;
		this.templateLine = templateLine;
	}

	private LineAndSource()
	{
		this.lineOfCode = "";
		this.templateLine = TemplateLine.NONE;
	}
}
