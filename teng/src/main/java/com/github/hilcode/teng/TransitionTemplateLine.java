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

import static com.github.hilcode.teng.ComboType.BLOCK_TEMPLATE;
import static com.github.hilcode.teng.ComboType.INLINE_TEMPLATE;
import static com.github.hilcode.teng.ComboType.VARIABLE;
import static com.github.hilcode.teng.LineAndSource.lineAndSource;
import static com.github.hilcode.teng.Mode.TEMPLATE;
import static com.github.hilcode.teng.TemplateLine.templateLine;
import static com.google.common.base.Strings.repeat;
import java.util.Collections;
import java.util.List;
import com.github.hilcode.teng.StateMachine.ModeAndState;
import com.google.common.collect.Lists;

public final class TransitionTemplateLine
	implements
		StateMachine.Transition<Line, Mode, TengParserState>
{
	@Override
	public ModeAndState<Mode, TengParserState> apply(final Mode mode, final TengParserState state, final Line line)
	{
		final List<Combo> blockTemplateCombos = extractCombos(state.blockTemplateBegin, state.blockTemplateEnd, BLOCK_TEMPLATE, line.text);
		final List<Combo> inlineTemplateCombos = extractCombos(state.inlineTemplateBegin, state.inlineTemplateEnd, INLINE_TEMPLATE, line.text);
		final List<Combo> variableCombos = extractCombos(state.variableBegin, state.variableEnd, VARIABLE, line.text);
		final List<Combo> combos = Lists.newArrayList();
		combos.addAll(blockTemplateCombos);
		combos.addAll(inlineTemplateCombos);
		combos.addAll(variableCombos);
		Collections.sort(combos);
		int blockTemplateCount = 0;
		for (final Combo combo : combos)
		{
			if (combo.comboType == ComboType.BLOCK_TEMPLATE)
			{
				blockTemplateCount++;
			}
		}
		if (blockTemplateCount > 1)
		{
			throw new IllegalStateException("Too many block templates; only 1 is allowed.");
		}
		else if (blockTemplateCount == 1 && combos.get(combos.size() - 1).comboType != ComboType.BLOCK_TEMPLATE)
		{
			throw new IllegalStateException("The block template must end the line; nothing may follow it.");
		}
		for (int i = 1; i < combos.size(); i++)
		{
			final Combo lft = combos.get(i - 1);
			final Combo rgt = combos.get(i);
			if (lft.end >= rgt.begin)
			{
				throw new IllegalStateException("Illegal nesting.");
			}
		}
		final List<Object> lineParts = Lists.newArrayList();
		if (combos.isEmpty())
		{
			lineParts.add(line.text.replace("\\", "\\\\").replace("\"", "\\\""));
		}
		else if (combos.size() == 1)
		{
			final Combo combo = combos.get(0);
			if (combo.begin > 0)
			{
				lineParts.add(line.text.substring(0, combo.begin).replace("\\", "\\\\").replace("\"", "\\\""));
			}
			lineParts.add(combo);
			if (combo.end + combo.endDelimiterLength < line.text.length())
			{
				lineParts.add(line.text.substring(combo.end + combo.endDelimiterLength).replace("\\", "\\\\").replace("\"", "\\\""));
			}
		}
		else
		{
			final Combo firstCombo = combos.get(0);
			if (firstCombo.begin > 0)
			{
				lineParts.add(line.text.substring(0, firstCombo.begin).replace("\\", "\\\\").replace("\"", "\\\""));
			}
			lineParts.add(firstCombo);
			for (int i = 1; i < combos.size(); i++)
			{
				final Combo lft = combos.get(i - 1);
				final Combo rgt = combos.get(i);
				if (lft.end + 1 != rgt.begin)
				{
					lineParts.add(line.text.substring(lft.end + lft.endDelimiterLength, rgt.begin).replace("\\", "\\\\").replace("\"", "\\\""));
				}
				lineParts.add(rgt);
			}
			final Combo lastCombo = combos.get(combos.size() - 1);
			if (lastCombo.end + lastCombo.endDelimiterLength < line.text.length())
			{
				lineParts.add(line.text.substring(lastCombo.end + lastCombo.endDelimiterLength).replace("\\", "\\\\").replace("\"", "\\\""));
			}
		}
		final StringBuilder lineBuilder = new StringBuilder();
		boolean blockTemplate_ = false;
		for (final Object linePart : lineParts)
		{
			if (linePart instanceof String)
			{
				if (state.blockTemplate || ((String) linePart).length() > 0)
				{
					if (lineBuilder.length() > 0)
					{
						lineBuilder.append(" + ");
					}
					lineBuilder.append('"').append((String) linePart).append('"');
				}
			}
			else
			{
				final Combo combo = (Combo) linePart;
				switch (combo.comboType)
				{
					case VARIABLE:
					{
						if (lineBuilder.length() > 0)
						{
							lineBuilder.append(" + ");
						}
						lineBuilder.append("Teng.show(").append(line.text.substring(combo.begin + combo.beginDelimiterLength, combo.end)).append(")");
						state.tengNeeded = true;
						break;
					}
					case INLINE_TEMPLATE:
					{
						if (lineBuilder.length() > 0)
						{
							lineBuilder.append(" + ");
						}
						final String inlineTemplate = line.text.substring(combo.begin + combo.beginDelimiterLength, combo.end).trim();
						final int indexOfLeftParenthesis = inlineTemplate.indexOf('(');
						if (indexOfLeftParenthesis == -1)
						{
							throw new IllegalStateException("No '(' found.");
						}
						else if (indexOfLeftParenthesis == 0)
						{
							throw new IllegalStateException("No template name found.");
						}
						final String templateName_ = inlineTemplate.substring(0, indexOfLeftParenthesis).trim();
						lineBuilder.append("Teng.execute(");
						lineBuilder.append(templateName_).append(".execute").append(inlineTemplate.substring(indexOfLeftParenthesis));
						lineBuilder.append(")");
						state.tengNeeded = true;
						break;
					}
					case BLOCK_TEMPLATE:
					default:
					{
						blockTemplate_ = true;
						final String blockTemplate__ = line.text.substring(combo.begin + combo.beginDelimiterLength, combo.end).trim();
						final int indexOfLeftParenthesis = blockTemplate__.indexOf('(');
						if (indexOfLeftParenthesis == -1)
						{
							throw new IllegalStateException("No '(' found.");
						}
						else if (indexOfLeftParenthesis == 0)
						{
							throw new IllegalStateException("No template name found.");
						}
						final String templateName_ = blockTemplate__.substring(0, indexOfLeftParenthesis).trim();
						lineBuilder.append(", ").append(templateName_).append(".execute").append(blockTemplate__.substring(indexOfLeftParenthesis));
						break;
					}
				}
			}
		}
		final String result;
		if (blockTemplate_)
		{
			result = repeat("    ", state.indentationLevel + 4) + "lines.addAll(Teng.execute(" + lineBuilder.toString() + "));";
			state.tengNeeded = true;
		}
		else
		{
			if (state.blockTemplate)
			{
				result = repeat("    ", state.indentationLevel + 4) + "lines.add(" + lineBuilder.toString() + " + \"" + state.endOfLine.escapedEol + "\");";
			}
			else
			{
				result = lineBuilder.length() > 0
						? repeat("    ", state.indentationLevel + 4) + "lineBuilder.append(" + lineBuilder.toString() + ");"
						: null;
			}
		}
		if (result != null)
		{
			state.templateBuilder.add(lineAndSource(result, templateLine(line.number, line.text)));
		}
		return new ModeAndState<Mode, TengParserState>(TEMPLATE, state);
	}

	public static final List<Combo> extractCombos(final String begin, final String end, final ComboType comboType, final String line)
	{
		final List<Integer> beginIndices = extractIndices(begin, line);
		final List<Integer> endIndices = extractIndices(end, line);
		if (beginIndices.size() != endIndices.size())
		{
			throw new IllegalStateException("Unmatched " + comboType.name + "s.");
		}
		final List<Combo> combos = Lists.newArrayList();
		for (final Integer endIndex : endIndices)
		{
			Integer beginIndex = null;
			for (int index = beginIndices.size() - 1; index >= 0; index--)
			{
				final Integer beginIndex_ = beginIndices.get(index);
				if (beginIndex_ != null && beginIndex_.intValue() < endIndex.intValue())
				{
					beginIndices.set(index, null);
					beginIndex = beginIndex_;
					break;
				}
			}
			if (beginIndex == null)
			{
				throw new IllegalStateException("Illegally nested " + comboType.name + "s.");
			}
			combos.add(new Combo(comboType, beginIndex.intValue(), begin.length(), endIndex.intValue(), end.length()));
		}
		return combos;
	}

	public static final List<Integer> extractIndices(final String sentinel, final String line)
	{
		final List<Integer> indices = Lists.newArrayList();
		int index = line.indexOf(sentinel);
		while (index != -1)
		{
			indices.add(Integer.valueOf(index));
			index = line.indexOf(sentinel, index + 1);
		}
		return indices;
	}
}
