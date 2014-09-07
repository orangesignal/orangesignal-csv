/*
 * Copyright 2009-2014 the original author or authors.
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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link CsvReader} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 */
public final class CsvReaderTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testCsvReaderReaderIntCsvConfig() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader(""), 8192, new CsvConfig());
		reader.close();
	}

	@Test
	public void testCsvReaderReaderIntCsvConfigIllegalArgumentException1() {
		exception.expect(IllegalArgumentException.class);
		final Reader reader = new StringReader("");
		try {
			new CsvReader(reader, 0, new CsvConfig());
		} finally {
			Csv.closeQuietly(reader);
		}
	}

	@Test
	public void testCsvReaderReaderIntCsvConfigIllegalArgumentException2() {
		exception.expect(IllegalArgumentException.class);
		final Reader reader = new StringReader("");
		try {
			new CsvReader(reader, -8192, new CsvConfig());
		} finally {
			Csv.closeQuietly(reader);
		}
	}

	@Test
	public void testCsvReaderReaderIntCsvConfigIllegalArgumentException3() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvConfig must not be null");
		final Reader reader = new StringReader("");
		try {
			new CsvReader(reader, 8192, null);
		} finally {
			Csv.closeQuietly(reader);
		}
	}

	@Test
	public void testCsvReaderReaderCsvConfig() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader(""), new CsvConfig());
		reader.close();
	}

	@Test
	public void testCsvReaderReaderCsvConfigIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvConfig must not be null");
		final Reader reader = new StringReader("");
		try {
			new CsvReader(reader, null);
		} finally {
			Csv.closeQuietly(reader);
		}
	}

	@Test
	public void testCsvReaderReaderInt() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader(""), 8192);
		reader.close();
	}

	@Test
	public void testCsvReaderReaderIntIllegalArgumentException1() {
		exception.expect(IllegalArgumentException.class);
		final Reader reader = new StringReader("");
		try {
			new CsvReader(reader, 0);
		} finally {
			Csv.closeQuietly(reader);
		}
	}

	@Test
	public void testCsvReaderReaderIntIllegalArgumentException2() {
		exception.expect(IllegalArgumentException.class);
		final Reader reader = new StringReader("");
		try {
			new CsvReader(reader, -8192);
		} finally {
			Csv.closeQuietly(reader);
		}
	}

	@Test
	public void testCsvReaderReader() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader(""));
		reader.close();
	}

	@Test
	public void testReadTokens() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '\\');
		cfg.setNullString("NULL");
		cfg.setBreakString("\n");
		cfg.setIgnoreTrailingWhitespaces(true);
		cfg.setIgnoreLeadingWhitespaces(true);
		cfg.setIgnoreEmptyLines(true);
		cfg.setIgnoreLinePatterns(Pattern.compile("^#.*$"));

		final CsvReader reader = new CsvReader(new StringReader("# text/tab-separated-values   \r\n aaa , \"b\r\nb\\\\b\" , \"c\\\"cc\" \r\n zzz , yyy , NULL \r\n# Copyright 2009 OrangeSignal.   "), cfg);
		try {
			final List<CsvToken> tokens1 = reader.readTokens();
			assertThat(reader.getLineNumber(), is(2));
			assertThat(reader.getStartLineNumber(), is(2));
			assertThat(reader.getEndLineNumber(), is(3));
			assertThat(tokens1.size(), is(3));
			assertThat(tokens1.get(0).getValue(), is("aaa"));
			assertThat(tokens1.get(0).getStartLineNumber(), is(2));
			assertThat(tokens1.get(0).getEndLineNumber(), is(2));
			assertThat(tokens1.get(0).isEnclosed(), is(false));
			assertThat(tokens1.get(1).getValue(), is("b\nb\\\\b"));
			assertThat(tokens1.get(1).getStartLineNumber(), is(2));
			assertThat(tokens1.get(1).getEndLineNumber(), is(3));
			assertThat(tokens1.get(1).isEnclosed(), is(true));
			assertThat(tokens1.get(2).getValue(), is("c\"cc"));
			assertThat(tokens1.get(2).getStartLineNumber(), is(3));
			assertThat(tokens1.get(2).getEndLineNumber(), is(3));
			assertThat(tokens1.get(2).isEnclosed(), is(true));

			final List<CsvToken> tokens2 = reader.readTokens();
			assertThat(reader.getLineNumber(), is(3));
			assertThat(reader.getStartLineNumber(), is(4));
			assertThat(reader.getEndLineNumber(), is(4));
			assertThat(tokens2.size(), is(3));
			assertThat(tokens2.get(0).getValue(), is("zzz"));
			assertThat(tokens2.get(0).getStartLineNumber(), is(4));
			assertThat(tokens2.get(0).getEndLineNumber(), is(4));
			assertThat(tokens2.get(0).isEnclosed(), is(false));
			assertThat(tokens2.get(1).getValue(), is("yyy"));
			assertThat(tokens2.get(1).getStartLineNumber(), is(4));
			assertThat(tokens2.get(1).getEndLineNumber(), is(4));
			assertThat(tokens2.get(1).isEnclosed(), is(false));
			assertThat(tokens2.get(2).getValue(), nullValue());
			assertThat(tokens2.get(1).getStartLineNumber(), is(4));
			assertThat(tokens2.get(1).getEndLineNumber(), is(4));
			assertThat(tokens2.get(1).isEnclosed(), is(false));

			final List<CsvToken> tokens3 = reader.readTokens();
			assertThat(reader.getLineNumber(), is(4));
			assertThat(reader.getStartLineNumber(), is(5));
			assertThat(reader.getEndLineNumber(), is(5));
			assertThat(tokens3, nullValue());
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadTokensIssue30() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader("aaa\r\n\r\nccc"));
		try {
			final List<CsvToken> tokens1 = reader.readTokens();
			assertThat(tokens1.size(), is(1));
			assertThat(tokens1.get(0).getValue(), is("aaa"));

			final List<CsvToken> tokens2 = reader.readTokens();
			assertThat(tokens2.size(), is(1));
			assertThat(tokens2.get(0).getValue(), is(""));

			final List<CsvToken> tokens3 = reader.readTokens();
			assertThat(tokens3.size(), is(1));
			assertThat(tokens3.get(0).getValue(), is("ccc"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadValuesIssue30() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader("aaa\r\n\r\nccc"));
		try {
			final List<String> values1 = reader.readValues();
			assertThat(values1.size(), is(1));
			assertThat(values1.get(0), is("aaa"));

			final List<String> values2 = reader.readValues();
			assertThat(values2.size(), is(1));
			assertThat(values2.get(0), is(""));

			final List<String> values3 = reader.readValues();
			assertThat(values3.size(), is(1));
			assertThat(values3.get(0), is("ccc"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadValues() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '\\');
		cfg.setNullString("NULL");
		cfg.setBreakString("\n");
		cfg.setIgnoreTrailingWhitespaces(true);
		cfg.setIgnoreLeadingWhitespaces(true);
		cfg.setIgnoreEmptyLines(true);
		cfg.setIgnoreLinePatterns(Pattern.compile("^#.*$"));

		final CsvReader reader = new CsvReader(new StringReader("# text/tab-separated-values   \r\n aaa , \"b\r\nb\\\\b\" , \"c\\\"cc\",   , \r\n zzz , yyy , NULL \r\n# Copyright 2009 OrangeSignal.   "), cfg);
		try {
			final List<String> line1 = reader.readValues();
			assertThat(line1.size(), is(5));
			assertThat(line1.get(0), is("aaa"));
			assertThat(line1.get(1), is("b\nb\\\\b"));
			assertThat(line1.get(2), is("c\"cc"));
			assertThat(line1.get(3), is(""));
			assertThat(line1.get(4), is(""));

			final List<String> line2 = reader.readValues();
			assertThat(line2.size(), is(3));
			assertThat(line2.get(0), is("zzz"));
			assertThat(line2.get(1), is("yyy"));
			assertThat(line2.get(2), nullValue());

			final List<String> line3 = reader.readValues();
			assertThat(line3, nullValue());
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadValues2() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '\\');
		cfg.setQuoteDisabled(true);
		cfg.setNullString("NULL");
		cfg.setIgnoreEmptyLines(true);

		final CsvReader reader = new CsvReader(new StringReader("aaa,b\\,b\\,b,c\\,cc\r\nz\\,zz,yyy,NULL\r\n"), cfg);
		try {
			final List<String> line1 = reader.readValues();
			assertThat(line1.size(), is(3));
			assertThat(line1.get(0), is("aaa"));
			assertThat(line1.get(1), is("b,b,b"));
			assertThat(line1.get(2), is("c,cc"));

			final List<String> line2 = reader.readValues();
			assertThat(line2.size(), is(3));
			assertThat(line2.get(0), is("z,zz"));
			assertThat(line2.get(1), is("yyy"));
			assertThat(line2.get(2), nullValue());

			final List<String> line3 = reader.readValues();
			assertThat(line3, nullValue());
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadTSV() throws IOException {
		final CsvConfig cfg = new CsvConfig('\t', '"', '\\');
		cfg.setNullString("NULL");
		cfg.setBreakString("\n");
		cfg.setIgnoreTrailingWhitespaces(true);
		cfg.setIgnoreLeadingWhitespaces(true);
		cfg.setIgnoreEmptyLines(true);
		cfg.setIgnoreLinePatterns(Pattern.compile("^#.*$"));

		final CsvReader reader = new CsvReader(new StringReader("# text/tab-separated-values   \r\n aaa \t \"b\r\nb\\\\b\" \t \"c\\\"cc\" \r\n zzz \t yyy \t NULL \r\n# Copyright 2009 OrangeSignal.   "), cfg);
		try {
			final List<String> line1 = reader.readValues();
			assertThat(line1.size(), is(3));
			assertThat(line1.get(0), is("aaa"));
			assertThat(line1.get(1), is("b\nb\\\\b"));
			assertThat(line1.get(2), is("c\"cc"));

			final List<String> line2 = reader.readValues();
			assertThat(line2.size(), is(3));
			assertThat(line2.get(0), is("zzz"));
			assertThat(line2.get(1), is("yyy"));
			assertThat(line2.get(2), nullValue());

			final List<String> line3 = reader.readValues();
			assertThat(line3, nullValue());
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadRFC4180_2_1() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '"');
		cfg.setIgnoreEmptyLines(true);

		final CsvReader reader = new CsvReader(new StringReader("aaa,bbb,ccc\r\nzzz,yyy,xxx\r\n"), cfg);
		try {
			final List<String> line1 = reader.readValues();
			assertThat(line1.size(), is(3));
			assertThat(line1.get(0), is("aaa"));
			assertThat(line1.get(1), is("bbb"));
			assertThat(line1.get(2), is("ccc"));

			final List<String> line2 = reader.readValues();
			assertThat(line2.size(), is(3));
			assertThat(line2.get(0), is("zzz"));
			assertThat(line2.get(1), is("yyy"));
			assertThat(line2.get(2), is("xxx"));

			final List<String> line3 = reader.readValues();
			assertThat(line3, nullValue());
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadRFC4180_2_2() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '"');
		cfg.setIgnoreEmptyLines(true);

		final CsvReader reader = new CsvReader(new StringReader("aaa,bbb,ccc\r\nzzz,yyy,xxx"), cfg);
		try {
			final List<String> line1 = reader.readValues();
			assertThat(line1.size(), is(3));
			assertThat(line1.get(0), is("aaa"));
			assertThat(line1.get(1), is("bbb"));
			assertThat(line1.get(2), is("ccc"));

			final List<String> line2 = reader.readValues();
			assertThat(line2.size(), is(3));
			assertThat(line2.get(0), is("zzz"));
			assertThat(line2.get(1), is("yyy"));
			assertThat(line2.get(2), is("xxx"));

			final List<String> line3 = reader.readValues();
			assertThat(line3, nullValue());
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadRFC4180_2_3() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '"');
		cfg.setIgnoreEmptyLines(true);

		final CsvReader reader = new CsvReader(new StringReader("field_name,field_name,field_name\r\naaa,bbb,ccc\r\nzzz,yyy,xxx\r\n"), cfg);
		try {
			final List<String> line0 = reader.readValues();
			assertThat(line0.size(), is(3));
			assertThat(line0.get(0), is("field_name"));
			assertThat(line0.get(1), is("field_name"));
			assertThat(line0.get(2), is("field_name"));

			final List<String> line1 = reader.readValues();
			assertThat(line1.size(), is(3));
			assertThat(line1.get(0), is("aaa"));
			assertThat(line1.get(1), is("bbb"));
			assertThat(line1.get(2), is("ccc"));

			final List<String> line2 = reader.readValues();
			assertThat(line2.size(), is(3));
			assertThat(line2.get(0), is("zzz"));
			assertThat(line2.get(1), is("yyy"));
			assertThat(line2.get(2), is("xxx"));

			final List<String> line3 = reader.readValues();
			assertThat(line3, nullValue());
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadRFC4180_5() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '"');
		cfg.setIgnoreEmptyLines(true);

		final CsvReader reader = new CsvReader(new StringReader("\"aaa\",\"bbb\",\"ccc\"\r\nzzz,yyy,xxx"), cfg);
		try {
			final List<String> line1 = reader.readValues();
			assertThat(line1.size(), is(3));
			assertThat(line1.get(0), is("aaa"));
			assertThat(line1.get(1), is("bbb"));
			assertThat(line1.get(2), is("ccc"));

			final List<String> line2 = reader.readValues();
			assertThat(line2.size(), is(3));
			assertThat(line2.get(0), is("zzz"));
			assertThat(line2.get(1), is("yyy"));
			assertThat(line2.get(2), is("xxx"));

			final List<String> line3 = reader.readValues();
			assertThat(line3, nullValue());
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadRFC4180_6() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '"');
		cfg.setIgnoreEmptyLines(true);

		final CsvReader reader = new CsvReader(new StringReader("\"aaa\",\"b\r\nbb\",\"ccc\"\r\nzzz,yyy,xxx"), cfg);
		try {
			final List<String> line1 = reader.readValues();
			assertThat(line1.size(), is(3));
			assertThat(line1.get(0), is("aaa"));
			assertThat(line1.get(1), is("b\r\nbb"));
			assertThat(line1.get(2), is("ccc"));

			final List<String> line2 = reader.readValues();
			assertThat(line2.size(), is(3));
			assertThat(line2.get(0), is("zzz"));
			assertThat(line2.get(1), is("yyy"));
			assertThat(line2.get(2), is("xxx"));

			final List<String> line3 = reader.readValues();
			assertThat(line3, nullValue());
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadRFC4180_7() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '"');
		cfg.setIgnoreEmptyLines(true);

		final CsvReader reader = new CsvReader(new StringReader("\"aaa\",\"b\"\"bb\",\"ccc\""), cfg);
		try {
			final List<String> line1 = reader.readValues();
			assertThat(line1.size(), is(3));
			assertThat(line1.get(0), is("aaa"));
			assertThat(line1.get(1), is("b\"bb"));
			assertThat(line1.get(2), is("ccc"));

			final List<String> line2 = reader.readValues();
			assertThat(line2, nullValue());
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadEscapedDoubleQuoteWithLineBreak() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '"');
		cfg.setIgnoreEmptyLines(true);
		final CsvReader reader = new CsvReader(new StringReader("\"a\",\"b\",\"c\"\r\n\"1,000\",\"2,000\",\"3,000\"\r\n\"a\",\"\"\"b\"\"b\r\nb\",\"c\"\r\n"), cfg);
		try {
			final List<String> line1 = reader.readValues();
			assertThat(line1.size(), is(3));
			assertThat(line1.get(0), is("a"));
			assertThat(line1.get(1), is("b"));
			assertThat(line1.get(2), is("c"));

			final List<String> line2 = reader.readValues();
			assertThat(line2.size(), is(3));
			assertThat(line2.get(0), is("1,000"));
			assertThat(line2.get(1), is("2,000"));
			assertThat(line2.get(2), is("3,000"));

			final List<String> line3 = reader.readValues();
			assertThat(line3.size(), is(3));
			assertThat(line3.get(0), is("a"));
			assertThat(line3.get(1), is("\"b\"b\r\nb"));
			assertThat(line3.get(2), is("c"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadEscapedDoubleQuoteWithLineBreak2() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '"');
		cfg.setIgnoreEmptyLines(true);
		final CsvReader reader = new CsvReader(new StringReader("\"a\",\"b\",\"c\"\r\n\"1,000\",\"2,000\",\"3,000\"\r\n\"a\",\"\"\"b\"\"b\"\"\r\nb\",\"c\"\r\n"), cfg);
		try {
			final List<String> line1 = reader.readValues();
			assertThat(line1.size(), is(3));
			assertThat(line1.get(0), is("a"));
			assertThat(line1.get(1), is("b"));
			assertThat(line1.get(2), is("c"));

			final List<String> line2 = reader.readValues();
			assertThat(line2.size(), is(3));
			assertThat(line2.get(0), is("1,000"));
			assertThat(line2.get(1), is("2,000"));
			assertThat(line2.get(2), is("3,000"));

			final List<String> line3 = reader.readValues();
			assertThat(line3.size(), is(3));
			assertThat(line3.get(0), is("a"));
			assertThat(line3.get(1), is("\"b\"b\"\r\nb"));
			assertThat(line3.get(2), is("c"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadEscapedDoubleQuote() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '"');
		cfg.setIgnoreEmptyLines(true);
		final CsvReader reader = new CsvReader(new StringReader("\"\"\"\"\"x\"\"\"\"\",\"y\"\"y\"\"y\",\"z\"\"\"\"z\""), cfg);
		try {
			final List<String> line1 = reader.readValues();
			assertThat(line1.size(), is(3));
			assertThat(line1.get(0), is("\"\"x\"\""));
			assertThat(line1.get(1), is("y\"y\"y"));
			assertThat(line1.get(2), is("z\"\"z"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadUtf8bomFromInputStreamReader() throws IOException {
		final byte[] bytes = "\uFEFF佐藤,鈴木".getBytes("UTF8");
//		System.out.println(new HexDumpEncoder().encodeBuffer(bytes));

		final CsvConfig cfg = new CsvConfig();
		cfg.setIgnoreEmptyLines(true);
		final CsvReader reader = new CsvReader(new InputStreamReader(new ByteArrayInputStream(bytes), "UTF8"));
		try {
			final List<String> line = reader.readValues();
			assertThat(line.size(), is(2));
			assertThat(line.get(0), is("佐藤"));
			assertThat(line.get(1), is("鈴木"));
		} finally {
			reader.close();
		}
	}

/*
	@Test
	public void testReadUtf8bomFromStringReader() throws IOException {
		final CsvConfig cfg = new CsvConfig();
		cfg.setIgnoreEmptyLines(true);
		final CsvReader reader = new CsvReader(new StringReader(new String("\uFEFF佐藤,鈴木")));
		try {
			final List<String> line = reader.readValues();
			assertThat(line.size(), is(2));
			assertThat(line.get(0), is("佐藤"));
			assertThat(line.get(1), is("鈴木"));
		} finally {
			reader.close();
		}
	}
*/

	@Test
	public void testReadUtf8bomFromFileInputStream() throws IOException {
		final CsvConfig cfg = new CsvConfig();
		cfg.setIgnoreEmptyLines(true);
		final CsvReader reader = new CsvReader(new InputStreamReader(new FileInputStream("src/test/resources/utf8bom.csv"), "UTF-8"), cfg);
		try {
			final List<String> line = reader.readValues();
			assertThat(line.size(), is(2));
			assertThat(line.get(0), is("佐藤"));
			assertThat(line.get(1), is("鈴木"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadTokensCsvTokenException() throws IOException {
		final CsvConfig cfg = new CsvConfig();
		cfg.setVariableColumns(false);

		final CsvReader reader = new CsvReader(new StringReader("a,b,c\r\nx,y"), cfg);
		try {
			final List<CsvToken> line1 = reader.readTokens();
			assertThat(line1.size(), is(3));
			assertThat(line1.get(0).getValue(), is("a"));
			assertThat(line1.get(1).getValue(), is("b"));
			assertThat(line1.get(2).getValue(), is("c"));

			// Act
			reader.readTokens();
		} catch (final CsvTokenException e) {
			// Assert
			assertThat(e.getMessage(), is("Invalid column count in CSV input on line 2."));
			final List<CsvToken> tokens = e.getTokens();
			assertThat(tokens.size(), is(2));
			assertThat(tokens.get(0).getValue(), is("x"));
			assertThat(tokens.get(1).getValue(), is("y"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadValuesCsvTokenException() throws IOException {
		final CsvConfig cfg = new CsvConfig();
		cfg.setVariableColumns(false);

		final CsvReader reader = new CsvReader(new StringReader("a,b,c\r\nx,y"), cfg);
		try {
			final List<String> line1 = reader.readValues();
			assertThat(line1.size(), is(3));
			assertThat(line1.get(0), is("a"));
			assertThat(line1.get(1), is("b"));
			assertThat(line1.get(2), is("c"));

			// Act
			reader.readValues();
		} catch (final CsvTokenException e) {
			// Assert
			assertThat(e.getMessage(), is("Invalid column count in CSV input on line 2."));
			final List<CsvToken> tokens = e.getTokens();
			assertThat(tokens.size(), is(2));
			assertThat(tokens.get(0).getValue(), is("x"));
			assertThat(tokens.get(1).getValue(), is("y"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testClosed() throws IOException {
		// Arrange
		exception.expect(IOException.class);
		exception.expectMessage("Reader closed");
		final CsvReader reader = new CsvReader(new StringReader(""), new CsvConfig());
		reader.close();
		// Act
		reader.readValues();
	}

}