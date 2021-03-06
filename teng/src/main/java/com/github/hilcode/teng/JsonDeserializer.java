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
package com.github.hilcode.teng;

import java.io.File;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonDeserializer
	implements
		Deserializer
{
	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public <MODEL> MODEL transform(final File file, final Class<? extends MODEL> modelClass)
	{
		try
		{
			return this.mapper.readValue(file, modelClass);
		}
		catch (final Exception e)
		{
			throw new TengException(e.getMessage(), e);
		}
	}
}
