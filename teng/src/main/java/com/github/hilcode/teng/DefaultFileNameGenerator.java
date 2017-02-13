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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import com.google.common.base.Optional;

public final class DefaultFileNameGenerator
	implements
		FileNameGenerator
{
	@Override
	public <MODEL> File generateFileName(final File projectDir, final MODEL model, final Template template)
	{
		final File srcMainJava = new File(projectDir, "src/main/java");
		final File packageDir = new File(srcMainJava, template.getClass().getPackage().getName().replace('.', '/'));
		final Optional<String> name = extractName(model);
		final String entityName = name.isPresent()
				? name.get()
				: model.getClass().getSimpleName() + template.getClass().getSimpleName();
		return new File(packageDir, entityName + ".java");
	}

	public <MODEL> Optional<String> extractName(final MODEL model)
	{
		if (model.getClass().isEnum())
		{
			return Optional.of(Names.extractJavaClassName((Enum<?>) model));
		}
		final Optional<String> resultMethodGetName = extractUsingMethod(model, "getName");
		if (resultMethodGetName.isPresent())
		{
			return resultMethodGetName;
		}
		final Optional<String> resultMethodName = extractUsingMethod(model, "name");
		if (resultMethodName.isPresent())
		{
			return resultMethodName;
		}
		return extractNameField(model);
	}

	public static final <MODEL> Optional<String> extractUsingMethod(final MODEL model, final String methodName)
	{
		try
		{
			try
			{
				final Method nameMethod = model.getClass().getMethod(methodName);
				if (nameMethod.getReturnType() == String.class)
				{
					final Object result = nameMethod.invoke(model);
					return result != null
							? Optional.of(result.toString())
							: Optional.<String> absent();
				}
			}
			catch (final NoSuchMethodException e)
			{
				// Ignore.
			}
			return Optional.absent();
		}
		catch (final Exception e)
		{
			throw new TengException(
					"Unable to execute method '" + methodName + "' on '" + model.getClass() + "': " + e.getMessage(),
					e);
		}
	}

	public static final <MODEL> Optional<String> extractNameField(final MODEL model)
	{
		try
		{
			try
			{
				final Field field = model.getClass().getField("name");
				if (field.getType() == String.class)
				{
					final Object result = field.get(model);
					return result != null
							? Optional.of(result.toString())
							: Optional.<String> absent();
				}
			}
			catch (final NoSuchFieldException e)
			{
				// Ignore.
			}
			return Optional.absent();
		}
		catch (final Exception e)
		{
			throw new TengException(
					"Unable to retrieve field 'name' from '" + model.getClass() + "': " + e.getMessage(),
					e);
		}
	}
}
