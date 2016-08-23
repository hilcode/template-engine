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

public final class JavaFile
{
	public final PackageName packageName;

	public final String name;

	public JavaFile(final PackageName packageName, final String name)
	{
		Preconditions.checkNotNull(packageName, "Missing 'packageName'.");
		Preconditions.checkNotNull(name, "Missing 'name'.");
		Preconditions.checkArgument(name.length() > 0, "Empty 'name'.");
		this.packageName = packageName;
		this.name = name;
	}

	public JavaFile(final String name)
	{
		Preconditions.checkNotNull(name, "Missing 'name'.");
		Preconditions.checkArgument(name.length() > 0, "Empty 'name'.");
		this.packageName = PackageName.NONE;
		this.name = name;
	}
}
