/*
 * Copyright 2009-2013 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.orangesignal.csv;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.util.regex.Pattern;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link CsvConfig} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
public final class CsvConfigTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testCsvConfig() {
		new CsvConfig();
	}

	@Test
	public void testCsvConfigChar() {
		new CsvConfig('\t');
	}

	@Test
	public void testCsvConfigCharCharChar() {
		new CsvConfig('\t', '\'', '|');
	}

	@Test
	public void testCsvConfigCharCharCharBooleanBoolean() {
		new CsvConfig('\t', '\'', '|', false, false);
	}

	@Test
	public void testValidate1() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid separator character");
		new CsvConfig('\r').validate();
	}

	@Test
	public void testValidate2() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid separator character");
		new CsvConfig('\n').validate();
	}

	@Test
	public void testValidate3() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid quote character");
		new CsvConfig(',', ',', '"').validate();
	}

	@Test
	public void testValidate4() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid quote character");
		new CsvConfig(',', '\r', '"').validate();
	}

	@Test
	public void testValidate5() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid quote character");
		new CsvConfig(',', '\n', '"').validate();
	}

	@Test
	public void testValidate6() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid escape character");
		new CsvConfig(',', '"', ',').validate();
	}

	@Test
	public void testValidate7() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid escape character");
		new CsvConfig(',', '"', '\r').validate();
	}

	@Test
	public void testValidate8() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid escape character");
		new CsvConfig(',', '"', '\n').validate();
	}

	@Test
	public void testGetSeparator() {
		assertThat(new CsvConfig().getSeparator(), is(','));
	}

	@Test
	public void testSetSeparator() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setSeparator('\t');
		assertThat(cfg.getSeparator(), is('\t'));
	}

	@Test
	public void testWithSeparator() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withSeparator('\t');
		assertThat(cfg.getSeparator(), is('\t'));
	}

	@Test
	public void testGetQuote() {
		assertThat(new CsvConfig().getQuote(), is('"'));
	}

	@Test
	public void testSetQuote() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setQuote('\u0000');
		assertThat(cfg.getQuote(), is('\u0000'));
	}

	@Test
	public void testWithQuote() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withQuote('\u0000');
		assertThat(cfg.getQuote(), is('\u0000'));
	}

	@Test
	public void testGetEscape() {
		assertThat(new CsvConfig().getEscape(), is('\\'));
	}

	@Test
	public void testSetEscape() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setEscape('"');
		assertThat(cfg.getEscape(), is('"'));
	}

	@Test
	public void testWithEscape() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withEscape('"');
		assertThat(cfg.getEscape(), is('"'));
	}

	@Test
	public void testIsQuoteDisabled() {
		assertThat(new CsvConfig().isQuoteDisabled(), is(true));
	}

	@Test
	public void testSetQuoteDisabled() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setQuoteDisabled(false);
		assertThat(cfg.isQuoteDisabled(), is(false));
	}

	@Test
	public void testWithQuoteDisabled() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withQuoteDisabled(false);
		assertThat(cfg.isQuoteDisabled(), is(false));
	}

	@Test
	public void testIsEscapeDisabled() {
		assertThat(new CsvConfig().isEscapeDisabled(), is(true));
	}

	@Test
	public void testSetEscapeDisabled() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setEscapeDisabled(false);
		assertThat(cfg.isEscapeDisabled(), is(false));
	}

	@Test
	public void testWithEscapeDisabled() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withEscapeDisabled(false);
		assertThat(cfg.isEscapeDisabled(), is(false));
	}

	@Test
	public void testGetBreakString() {
		final CsvConfig cfg = new CsvConfig();
		assertThat(cfg.getBreakString(), nullValue());
	}

	@Test
	public void testSetBreakString() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setBreakString("\n");
		assertThat(cfg.getBreakString(), is("\n"));
	}

	@Test
	public void testWithBreakString() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withBreakString("\n");
		assertThat(cfg.getBreakString(), is("\n"));
	}

	@Test
	public void testGetNullString() {
		final CsvConfig cfg = new CsvConfig();
		assertThat(cfg.getNullString(), nullValue());
		assertThat(cfg.isIgnoreCaseNullString(), is(false));
	}

	@Test
	public void testSetNullStringString() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setNullString("null");
		assertThat(cfg.getNullString(), is("null"));
		assertThat(cfg.isIgnoreCaseNullString(), is(false));
	}

	@Test
	public void testWithNullStringString() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withNullString("null");
		assertThat(cfg.getNullString(), is("null"));
		assertThat(cfg.isIgnoreCaseNullString(), is(false));
	}

	@Test
	public void testSetNullStringStringBoolean() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setNullString("null", true);
		assertThat(cfg.getNullString(), is("null"));
		assertThat(cfg.isIgnoreCaseNullString(), is(true));
	}

	@Test
	public void testWithNullStringStringBoolean() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withNullString("null", true);
		assertThat(cfg.getNullString(), is("null"));
		assertThat(cfg.isIgnoreCaseNullString(), is(true));
	}

	@Test
	public void testIsIgnoreLeadingWhitespaces() {
		assertThat(new CsvConfig().isIgnoreLeadingWhitespaces(), is(false));
	}

	@Test
	public void testSetIgnoreLeadingWhitespaces() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setIgnoreLeadingWhitespaces(true);
		assertThat(cfg.isIgnoreLeadingWhitespaces(), is(true));
	}

	@Test
	public void testWithIgnoreLeadingWhitespaces() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withIgnoreLeadingWhitespaces(true);
		assertThat(cfg.isIgnoreLeadingWhitespaces(), is(true));
	}


	@Test
	public void testIsIgnoreTrailingWhitespaces() {
		assertThat(new CsvConfig().isIgnoreTrailingWhitespaces(), is(false));
	}

	@Test
	public void testSetIgnoreTrailingWhitespaces() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setIgnoreTrailingWhitespaces(true);
		assertThat(cfg.isIgnoreTrailingWhitespaces(), is(true));
	}

	@Test
	public void testWithIgnoreTrailingWhitespaces() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withIgnoreTrailingWhitespaces(true);
		assertThat(cfg.isIgnoreTrailingWhitespaces(), is(true));
	}

	@Test
	public void testIsIgnoreEmptyLines() {
		assertThat(new CsvConfig().isIgnoreEmptyLines(), is(false));
	}

	@Test
	public void testSetIgnoreEmptyLines() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setIgnoreEmptyLines(true);
		assertThat(cfg.isIgnoreEmptyLines(), is(true));
	}

	@Test
	public void testWithIgnoreEmptyLines() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withIgnoreEmptyLines(true);
		assertThat(cfg.isIgnoreEmptyLines(), is(true));
	}

	@Test
	public void testGetIgnoreLinePatterns() {
		assertThat(new CsvConfig().getIgnoreLinePatterns(), nullValue());
	}

	@Test
	public void testSetIgnoreLinePatterns() {
		final Pattern pattern = Pattern.compile("^#[ ]*$");
		final CsvConfig cfg = new CsvConfig();
		cfg.setIgnoreLinePatterns(pattern);
		assertThat(cfg.getIgnoreLinePatterns().length, is(1));
		assertThat(cfg.getIgnoreLinePatterns()[0], is(pattern));
	}

	@Test
	public void testWithIgnoreLinePatterns() {
		final Pattern pattern = Pattern.compile("^#[ ]*$");
		final CsvConfig cfg = new CsvConfig();
		cfg.withIgnoreLinePatterns(pattern);
		assertThat(cfg.getIgnoreLinePatterns().length, is(1));
		assertThat(cfg.getIgnoreLinePatterns()[0], is(pattern));
	}

	@Test
	public void testGetSkipLines() {
		assertThat(new CsvConfig().getSkipLines(), is(0));
	}

	@Test
	public void testSetSkipLines() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setSkipLines(2);
		assertThat(cfg.getSkipLines(), is(2));
	}

	@Test
	public void testWithSkipLines() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withSkipLines(2);
		assertThat(cfg.getSkipLines(), is(2));
	}


	@Test
	public void testGetLineSeparator() {
		assertThat(new CsvConfig().getLineSeparator(), is(System.getProperty("line.separator")));
	}

	@Test
	public void testSetLineSeparator() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setLineSeparator("\n");
		assertThat(cfg.getLineSeparator(), is("\n"));
	}

	@Test
	public void testWithLineSeparator() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withLineSeparator("\n");
		assertThat(cfg.getLineSeparator(), is("\n"));
	}

	@Test
	public void testGetQuotePolicy() {
		assertThat(new CsvConfig().getQuotePolicy(), is(QuotePolicy.ALL));
	}

	@Test
	public void testSetQuotePolicy() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setQuotePolicy(QuotePolicy.MINIMAL);
		assertThat(cfg.getQuotePolicy(), is(QuotePolicy.MINIMAL));
	}

	@Test
	public void testWithQuotePolicy() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withQuotePolicy(QuotePolicy.MINIMAL);
		assertThat(cfg.getQuotePolicy(), is(QuotePolicy.MINIMAL));
	}

	@Test
	public void testSetQuotePolicyIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("QuotePolicy must not be null");
		new CsvConfig().setQuotePolicy(null);
	}

	@Test
	public void testWithQuotePolicyIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("QuotePolicy must not be null");
		new CsvConfig().withQuotePolicy(null);
	}

	@Test
	public void testIsUtf8bomPolicy() {
		assertThat(new CsvConfig().isUtf8bomPolicy(), is(false));
	}

	@Test
	public void testSetUtf8bomPolicy() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setUtf8bomPolicy(true);
		assertThat(cfg.isUtf8bomPolicy(), is(true));
	}

	@Test
	public void testWithUtf8bomPolicy() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withUtf8bomPolicy(true);
		assertThat(cfg.isUtf8bomPolicy(), is(true));
	}

	@Test
	public void testIsVariableColumns() {
		assertThat(new CsvConfig().isVariableColumns(), is(true));
	}
	@Test
	public void testSetVariableColumns() {
		final CsvConfig cfg = new CsvConfig();
		cfg.setVariableColumns(false);
		assertThat(cfg.isVariableColumns(), is(false));
	}

	@Test
	public void testWithVariableColumns() {
		final CsvConfig cfg = new CsvConfig();
		cfg.withVariableColumns(false);
		assertThat(cfg.isVariableColumns(), is(false));
	}

	@Test
	public void testWithAllMethods() {
		final Pattern pattern = Pattern.compile("^#[ ]*$");
		final CsvConfig cfg = new CsvConfig();
		cfg.withSeparator('\t')
			.withQuote('\u0000')
			.withEscape('"')
			.withQuoteDisabled(false)
			.withEscapeDisabled(false)
			.withBreakString("\n")
			.withNullString("null", true)
			.withIgnoreLeadingWhitespaces(true)
			.withIgnoreTrailingWhitespaces(true)
			.withIgnoreEmptyLines(true)
			.withIgnoreLinePatterns(pattern)
			.withSkipLines(2)
			.withLineSeparator("\n")
			.withQuotePolicy(QuotePolicy.MINIMAL)
			.withUtf8bomPolicy(true)
			.withVariableColumns(false);
		assertThat(cfg.getSeparator(), is('\t'));
		assertThat(cfg.getQuote(), is('\u0000'));
		assertThat(cfg.getEscape(), is('"'));
		assertThat(cfg.isQuoteDisabled(), is(false));
		assertThat(cfg.isEscapeDisabled(), is(false));
		assertThat(cfg.getBreakString(), is("\n"));
		assertThat(cfg.getNullString(), is("null"));
		assertThat(cfg.isIgnoreCaseNullString(), is(true));
		assertThat(cfg.isIgnoreLeadingWhitespaces(), is(true));
		assertThat(cfg.isIgnoreTrailingWhitespaces(), is(true));
		assertThat(cfg.isIgnoreEmptyLines(), is(true));
		assertThat(cfg.getIgnoreLinePatterns().length, is(1));
		assertThat(cfg.getIgnoreLinePatterns()[0], is(pattern));
		assertThat(cfg.getSkipLines(), is(2));
		assertThat(cfg.getLineSeparator(), is("\n"));
		assertThat(cfg.getQuotePolicy(), is(QuotePolicy.MINIMAL));
		assertThat(cfg.isUtf8bomPolicy(), is(true));
		assertThat(cfg.isVariableColumns(), is(false));
	}

	@Test
	public void testClone() {
		final CsvConfig cfg = new CsvConfig('\t', '^', '|').clone();
		assertThat(cfg.getSeparator(), is('\t'));
		assertThat(cfg.getQuote(), is('^'));
		assertThat(cfg.getEscape(), is('|'));
	}

}