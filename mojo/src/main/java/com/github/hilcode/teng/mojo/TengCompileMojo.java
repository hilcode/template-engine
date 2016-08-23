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
import javax.inject.Inject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import com.github.hilcode.teng.TengException;

@Mojo(name = "compile", requiresOnline = false)
public final class TengCompileMojo
	extends
		AbstractMojo
{
	@Parameter(defaultValue = "src/main/teng", property = "teng.templateDir")
	private File templateDir;

	@Parameter(defaultValue = "${project.build.directory}/generated-sources/teng", property = "teng.templateOutputDir")
	private File templateOutputDir;

	@Inject
	private MavenProject mavenProject;

	@Override
	public void execute()
		throws MojoExecutionException
	{
		try
		{
			MojoUtils.processTemplateFiles(this.templateDir, this.templateOutputDir);
			this.mavenProject.addCompileSourceRoot(this.templateOutputDir.getPath());
		}
		catch (final TengException e)
		{
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}
