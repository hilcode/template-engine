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

import static com.github.hilcode.teng.LineAndSource.lineAndSource;
import static com.github.hilcode.teng.Mode.INITIAL;
import static com.github.hilcode.teng.Mode.PARAMETERS;
import static com.github.hilcode.teng.Mode.TEMPLATE;
import static com.google.common.base.Strings.repeat;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.List;
import com.github.hilcode.teng.StateMachine.ModeAndState;
import com.github.hilcode.teng.StateMachine.Transition;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public final class TengParser
{
	private final StateMachine<Line, Mode, TengParserState> fsm;

	public TengParser()
	{
		final ImmutableMap.Builder<Mode, ImmutableList<Transition<Line, Mode, TengParserState>>> modes = ImmutableMap.builder();
		modes.put(
				INITIAL,
				ImmutableList.of(
						new TransitionInitialCommentOrEmptyLine(),
						new TransitionInitialTemplateLine()));
		modes.put(
				PARAMETERS,
				ImmutableList.of(
						new TransitionParametersCommentOrEmptyLine(),
						new TransitionParametersBlockTemplateDelimitersSetting(),
						new TransitionParametersInlineTemplateDelimitersSetting(),
						new TransitionParametersVariableDelimitersSetting(),
						new TransitionParametersDirectivePrefixSetting(),
						new TransitionParametersEndOfLineSetting(),
						new TransitionParametersParameter(),
						new TransitionParametersFinish()));
		modes.put(
				TEMPLATE,
				ImmutableList.of(
						new TransitionTemplateIf(),
						new TransitionTemplateElseIf(),
						new TransitionTemplateElse(),
						new TransitionTemplateEndIf(),
						new TransitionTemplateFor(),
						new TransitionTemplateEndFor(),
						new TransitionTemplateLine()));
		this.fsm = new StateMachine<Line,Mode,TengParserState>(modes.build());
	}

	public JavaTemplate parse(final Reader source)
	{
		Mode mode = INITIAL;
		TengParserState state = new TengParserState();
		state.templateBuilder.add(lineAndSource("/*"));
		state.templateBuilder.add(lineAndSource(" * This is generated code."));
		state.templateBuilder.add(lineAndSource(" */"));
		final LineNumberReader reader = new LineNumberReader(source);
		while (true)
		{
			final Optional<String> maybeLine = readLine(reader);
			final int lineNumber = reader.getLineNumber();
			if (!maybeLine.isPresent())
			{
				break;
			}
			final Line line = new Line(lineNumber, maybeLine.get());
			final ModeAndState<Mode, TengParserState> modeAndState = this.fsm.transition(mode, state, line);
			mode = modeAndState.mode;
			state = modeAndState.state;
		}
		if (state.blockTemplate)
		{
			state.templateBuilder.add(lineAndSource("                return lines.build();"));
		}
		else
		{
			state.templateBuilder.add(lineAndSource("                return lineBuilder.toString();"));
		}
		state.templateBuilder.add(lineAndSource("            }"));
		state.templateBuilder.add(lineAndSource("        };"));
		state.templateBuilder.add(lineAndSource("    }"));
		state.templateBuilder.add(lineAndSource("}"));
		//
		final List<LineAndSource> templateBuilder = Lists.newArrayList();
		if (state.loopVarNeeded)
		{
			templateBuilder.add(lineAndSource("import com.github.hilcode.teng.LoopVar;"));
		}
		templateBuilder.add(lineAndSource("import com.github.hilcode.teng.Template;"));
		if (state.tengNeeded)
		{
			templateBuilder.add(lineAndSource("import com.github.hilcode.teng.Teng;"));
		}
		if (state.immutableListNeeded)
		{
			templateBuilder.add(lineAndSource("import com.google.common.collect.ImmutableList;"));
		}
		templateBuilder.add(lineAndSource(""));
		if (state.templateName.packageName == PackageName.NONE)
		{
			state.templateBuilder.addAll(3, templateBuilder);
		}
		else
		{
			state.templateBuilder.addAll(5, templateBuilder);
		}
		//
		int maxLineLength = 0;
		for (final LineAndSource lineAndSource : state.templateBuilder)
		{
			final int lineLength = lineAndSource.lineOfCode.length();
			if (lineLength > maxLineLength)
			{
				maxLineLength = lineLength;
			}
		}
		final String lineNumberMask = "%" + String.valueOf(state.templateBuilder.size()).length() + "d";
		final ImmutableList.Builder<String> linesOfCode = ImmutableList.builder();
		for (final LineAndSource lineAndSource : state.templateBuilder)
		{
			final StringBuilder lineOfCode = new StringBuilder();
			lineOfCode.append(lineAndSource.lineOfCode);
			if (lineAndSource.templateLine != TemplateLine.NONE)
			{
				lineOfCode.append(repeat(" ", maxLineLength - lineAndSource.lineOfCode.length()));
				lineOfCode.append(" // #");
				lineOfCode.append(String.format(lineNumberMask, Integer.valueOf(lineAndSource.templateLine.lineNumber)));
				lineOfCode.append(": ");
				lineOfCode.append(lineAndSource.templateLine.line);
			}
			lineOfCode.append(state.endOfLine);
			linesOfCode.add(lineOfCode.toString());
		}
		return new JavaTemplate(state.templateName, linesOfCode.build());
	}

	public static final Optional<String> readLine(final LineNumberReader source)
	{
		try
		{
			return Optional.fromNullable(source.readLine());
		}
		catch (final IOException e)
		{
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}
