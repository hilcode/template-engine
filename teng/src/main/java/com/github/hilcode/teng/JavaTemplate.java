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

import java.util.Iterator;
import com.google.common.collect.ImmutableList;

public final class JavaTemplate
	implements
		Iterable<String>
{
	public final JavaFile javaFile;

	private final ImmutableList<String> lines;

	public JavaTemplate(final JavaFile javaFile, final ImmutableList<String> lines)
	{
		this.javaFile = javaFile;
		this.lines = lines;
	}

	@Override
	public Iterator<String> iterator()
	{
		return this.lines.iterator();
	}
}
