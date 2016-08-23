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
package com.github.hilcode.teng.mojo;

import java.io.File;
import com.google.common.base.Predicate;

public final class FileExtensionPredicate
	implements
		Predicate<File>
{
	private final String extension;

	public FileExtensionPredicate(final String extension)
	{
		this.extension = extension;
	}

	@Override
	public boolean apply(final File input)
	{
		return input.getName().endsWith(this.extension);
	}
}
