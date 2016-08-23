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

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

public final class EntityInfo
{
	public final String name;

	@JsonSerialize(as = List.class)
	public final ImmutableList<FieldInfo> fields;

	public EntityInfo(
			@JsonProperty("name") final String name,
			@JsonProperty("fields") final List<FieldInfo> fields)
	{
		this.name = name;
		this.fields = ImmutableList.copyOf(fields);
	}

	@Override
	public String toString()
	{
		return MoreObjects
				.toStringHelper(getClass())
				.add("name", this.name)
				.add("fields", this.fields)
				.toString();
	}
}
