/*
 * Copyright 2009-2010 the original author or authors.
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
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link CsvWriter} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 */
public class CsvWriterTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testCsvWriterWriterIntCsvConfig() throws IOException {
		// Act
		final CsvWriter writer = new CsvWriter(new StringWriter(), 8192, new CsvConfig());
		writer.close();
	}

	@Test
	public void testCsvWriterWriterIntCsvConfigIllegalArgumentException1() throws IOException {
		// Arrange
		exception.expect(IllegalArgumentException.class);
		// Act
		final CsvWriter writer = new CsvWriter(new StringWriter(), 0, new CsvConfig());
		writer.close();
	}

	@Test
	public void testCsvWriterWriterIntCsvConfigIllegalArgumentException2() throws IOException {
		// Arrange
		exception.expect(IllegalArgumentException.class);
		// Act
		final CsvWriter writer = new CsvWriter(new StringWriter(), -8192, new CsvConfig());
		writer.close();
	}

	@Test
	public void testCsvWriterWriterIntCsvConfigIllegalArgumentException3() throws IOException {
		// Arrange
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvConfig must not be null");
		// Act
		final CsvWriter writer = new CsvWriter(new StringWriter(), 8192, null);
		writer.close();
	}

	@Test
	public void testCsvWriterWriterCsvConfig() throws IOException {
		// Act
		final CsvWriter writer = new CsvWriter(new StringWriter(), new CsvConfig());
		writer.close();
	}

	@Test
	public void testCsvWriterWriterCsvConfigIllegalArgumentException() throws IOException {
		// Arrange
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvConfig must not be null");
		// Act
		final CsvWriter writer = new CsvWriter(new StringWriter(), null);
		writer.close();
	}

	@Test
	public void testCsvWriterWriterInt() throws IOException {
		// Act
		final CsvWriter writer = new CsvWriter(new StringWriter(), 8192);
		writer.close();
	}

	@Test
	public void testCsvWriterWriterIntIllegalArgumentException1() throws IOException {
		// Arrange
		exception.expect(IllegalArgumentException.class);
		// Act
		final CsvWriter writer = new CsvWriter(new StringWriter(), 0);
		writer.close();
	}

	@Test
	public void testCsvWriterWriterIntIllegalArgumentException2() throws IOException {
		// Arrange
		exception.expect(IllegalArgumentException.class);
		// Act
		final CsvWriter writer = new CsvWriter(new StringWriter(), -8192);
		writer.close();
	}

	@Test
	public void testCsvWriterWriter() throws IOException {
		// Act
		final CsvWriter writer = new CsvWriter(new StringWriter());
		writer.close();
	}

	@Test
	public void testWriteValues() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '\\');
		cfg.setNullString("NULL");
		cfg.setLineSeparator("\r\n");

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			// Act
			writer.writeValues(Arrays.asList(new String[]{ "aaa", "b\nb\\\\b", "c\"cc" }));
			writer.writeValues(Arrays.asList(new String[]{ "zzz", "yyy", null }));
			writer.flush();
			// Assert
			assertThat(sw.getBuffer().toString(), is("\"aaa\",\"b\nb\\\\b\",\"c\\\"cc\"\r\n\"zzz\",\"yyy\",NULL\r\n"));
		} finally {
			writer.close();
		}
	}

	@Test
	public void testWriteValues2() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '\\');
		cfg.setNullString("NULL");
		cfg.setQuotePolicy(QuotePolicy.MINIMAL);
		cfg.setLineSeparator("\r\n");

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			// Act
			writer.writeValues(Arrays.asList(new String[]{ "aaa", "b\nb\\\\b", "c\"cc" }));
			writer.writeValues(Arrays.asList(new String[]{ "zzz", "yyy", null }));
			writer.flush();
			// Assert
			assertThat(sw.getBuffer().toString(), is("aaa,\"b\nb\\\\b\",\"c\\\"cc\"\r\nzzz,yyy,NULL\r\n"));
		} finally {
			writer.close();
		}
	}

	@Test
	public void testWriteValues3() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '\\');
		cfg.setQuoteDisabled(true);
		cfg.setNullString("NULL");
		cfg.setLineSeparator("\r\n");

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			// Act
			writer.writeValues(Arrays.asList(new String[]{ "aaa", "b,bb", "ccc" }));
			writer.writeValues(Arrays.asList(new String[]{ "zzz", "yyy", null }));
			writer.flush();
			// Assert
			assertThat(sw.getBuffer().toString(), is("aaa,b\\,bb,ccc\r\nzzz,yyy,NULL\r\n"));
		} finally {
			writer.close();
		}
	}

/*
	@Test
	public void testWriteUtf8bomToStringWriter() throws IOException {
		final CsvConfig cfg = new CsvConfig();
		cfg.setLineSeparator("\r\n");
		cfg.setUtf8bomPolicy(true);

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			// Act
			writer.writeValues(Arrays.asList(new String[]{ "佐藤", "鈴木" }));
			writer.flush();
			// Assert
			assertThat(sw.getBuffer().toString(), is("\uFEFF佐藤,鈴木\r\n"));
		} finally {
			writer.close();
		}
	}
*/

	@Test
	public void testWriteNoNQuote() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '\\');
		cfg.setEscapeDisabled(false);
		cfg.setQuoteDisabled(false);
		cfg.setQuotePolicy(QuotePolicy.COLUMN);
		cfg.setNullString("NULL");
		cfg.setLineSeparator("\r\n");

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			// Act
			writer.writeValues(Arrays.asList(new String[]{ "aaa", "b,\"b", "ccc" }));
			writer.writeValues(Arrays.asList(new String[]{ "zzz", "", null }));
			writer.flush();
			// Assert
			assertThat(sw.getBuffer().toString(), is("aaa,\"b,\\\"b\",ccc\r\nzzz,NULL,NULL\r\n"));
		} finally {
			writer.close();
		}
	}

	@Test
	public void testWriteValuesCsvValueException() throws IOException {
		final CsvConfig cfg = new CsvConfig();
		cfg.setVariableColumns(false);

		final CsvWriter writer = new CsvWriter(new StringWriter(), cfg);
		try {
			// Act
			writer.writeValues(Arrays.asList(new String[]{ "a", "bb", "c" }));
			writer.writeValues(Arrays.asList(new String[]{ "x", "y" }));
			writer.flush();
		} catch (final CsvValueException e) {
			// Assert
			assertThat(e.getMessage(), is("Invalid column count."));
			final List<String> tokens = e.getValues();
			assertThat(tokens.size(), is(2));
			assertThat(tokens.get(0), is("x"));
			assertThat(tokens.get(1), is("y"));
		} finally {
			writer.close();
		}
	}

	@Test
	public void testClosed() throws IOException {
		// Arrange
		exception.expect(IOException.class);
		exception.expectMessage("Stream closed");
		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, new CsvConfig());
		writer.close();
		// Act
		writer.flush();
	}

}