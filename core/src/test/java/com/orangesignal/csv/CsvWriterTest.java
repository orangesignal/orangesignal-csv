/*
 * Copyright (c) 2009-2010 OrangeSignal.com All rights reserved.
 * 
 * これは Apache ライセンス Version 2.0 (以下、このライセンスと記述) に
 * 従っています。このライセンスに準拠する場合以外、このファイルを使用
 * してはなりません。このライセンスのコピーは以下から入手できます。
 * 
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 * 
 * 適用可能な法律がある、あるいは文書によって明記されている場合を除き、
 * このライセンスの下で配布されているソフトウェアは、明示的であるか暗黙の
 * うちであるかを問わず、「保証やあらゆる種類の条件を含んでおらず」、
 * 「あるがまま」の状態で提供されるものとします。
 * このライセンスが適用される特定の許諾と制限については、このライセンス
 * を参照してください。
 */

package com.orangesignal.csv;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.QuotePolicy;

/**
 * {@link CsvWriter} クラスの単体テストです。
 *
 * @author 杉澤 浩二
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
