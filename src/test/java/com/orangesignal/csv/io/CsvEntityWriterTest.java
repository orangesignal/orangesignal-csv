/*
 * Copyright (c) 2013 OrangeSignal.com All rights reserved.
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

package com.orangesignal.csv.io;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.bean.CsvEntityTemplate;
import com.orangesignal.csv.entity.Price;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;

/**
 * {@link CsvEntityWriter} クラスの単体テストです。
 *
 * @author 杉澤 浩二
 * @since 1.4.0
 */
public class CsvEntityWriterTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private static CsvConfig cfg;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cfg = new CsvConfig(',');
		cfg.setEscapeDisabled(false);
		cfg.setNullString("NULL");
		cfg.setIgnoreTrailingWhitespaces(true);
		cfg.setIgnoreLeadingWhitespaces(true);
		cfg.setIgnoreEmptyLines(true);
		cfg.setLineSeparator(Constants.CRLF);
	}

	// ------------------------------------------------------------------------
	// 利便性のための静的メソッド

	@Test
	public void testNewInstanceCsvWriterClass() throws IOException {
		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final Class<Price> c = Price.class;

		final CsvEntityWriter<Price> writer = CsvEntityWriter.newInstance(w, c);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterClassIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvWriter must not be null");

		final CsvWriter w = null;
		final Class<Price> c = Price.class;

		final CsvEntityWriter<Price> writer = CsvEntityWriter.newInstance(w, c);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterClassIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Class must not be null");

		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final Class<Price> c = null;

		final CsvEntityWriter<Price> writer = CsvEntityWriter.newInstance(w, c);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterCsvEntityTemplate() throws IOException {
		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final CsvEntityTemplate<Price> template = CsvEntityTemplate.newInstance(Price.class);

		final CsvEntityWriter<Price> writer = CsvEntityWriter.newInstance(w, template);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterCsvEntityTemplateIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvWriter must not be null");

		final CsvWriter w = null;
		final CsvEntityTemplate<Price> template = CsvEntityTemplate.newInstance(Price.class);

		final CsvEntityWriter<Price> writer = CsvEntityWriter.newInstance(w, template);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterCsvEntityTemplateIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvEntityTemplate must not be null");

		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final CsvEntityTemplate<Price> template = null;

		final CsvEntityWriter<Price> writer = CsvEntityWriter.newInstance(w, template);
		writer.close();
	}

	// ------------------------------------------------------------------------
	// コンストラクタ

	@Test
	public void testConstructorCsvWriterClass() throws IOException {
		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final Class<Price> c = Price.class;

		final CsvEntityWriter<Price> writer = new CsvEntityWriter<Price>(w, c);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterClassIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvWriter must not be null");

		final CsvWriter w = null;
		final Class<Price> c = Price.class;

		final CsvEntityWriter<Price> writer = new CsvEntityWriter<Price>(w, c);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterClassIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Class must not be null");

		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final Class<Price> c = null;

		final CsvEntityWriter<Price> writer = new CsvEntityWriter<Price>(w, c);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterCsvEntityTemplate() throws IOException {
		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final CsvEntityTemplate<Price> template = CsvEntityTemplate.newInstance(Price.class);

		final CsvEntityWriter<Price> writer = new CsvEntityWriter<Price>(w, template);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterCsvEntityTemplateIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvWriter must not be null");

		final CsvWriter w = null;
		final CsvEntityTemplate<Price> template = CsvEntityTemplate.newInstance(Price.class);

		final CsvEntityWriter<Price> writer = new CsvEntityWriter<Price>(w, template);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterCsvEntityTemplateIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvEntityTemplate must not be null");

		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final CsvEntityTemplate<Price> template = null;

		final CsvEntityWriter<Price> writer = new CsvEntityWriter<Price>(w, template);
		writer.close();
	}

	// ------------------------------------------------------------------------
	// オーバーライド メソッド

	@Test
	public void testFlush() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvEntityWriter<Price> writer = CsvEntityWriter.newInstance(
				new CsvWriter(sw, cfg),
				Price.class
			);
		try {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

			writer.writeHeader();
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\n"));

			writer.write(new Price("AAAA", "aaa", 10000, 10, df.parse("2008/10/28 10:24:00")));
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\n"));

			writer.write(new Price("BBBB", "bbb", null, 0, null));
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\n"));

			writer.write(new Price("CCCC", "ccc", 20000, 100, df.parse("2008/10/26 14:20:10")));
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\nCCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n"));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\nCCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n"));
	}

	@Test
	public void testFlushIOException() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("CsvWriter closed");
		final CsvEntityWriter<Price> writer = CsvEntityWriter.newInstance(
				new CsvWriter(new StringWriter(), cfg),
				Price.class
			);
		writer.close();
		// Act
		writer.flush();
	}

	@Test
	public void testCloseIOException() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("CsvWriter closed");
		final CsvEntityWriter<Price> writer = CsvEntityWriter.newInstance(
				new CsvWriter(new StringWriter(), cfg),
				Price.class
			);
		writer.close();
		// Act
		writer.close();
	}

	// ------------------------------------------------------------------------
	// パブリック メソッド

	@Test
	public void testWriteHeader() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvEntityWriter<Price> writer = CsvEntityWriter.newInstance(
				new CsvWriter(sw, cfg),
				Price.class
			);
		try {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

			writer.writeHeader();
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\n"));

			writer.write(new Price("AAAA", "aaa", 10000, 10, df.parse("2008/10/28 10:24:00")));
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\n"));

			writer.writeHeader();
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\n"));

			writer.write(new Price("BBBB", "bbb", null, 0, null));
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\n"));

			writer.writeHeader();
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\n"));

			writer.write(new Price("CCCC", "ccc", 20000, 100, df.parse("2008/10/26 14:20:10")));
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\nCCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n"));

			writer.writeHeader();
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\nCCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n"));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\nCCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n"));
	}

	@Test
	public void testWrite() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvEntityWriter<Price> writer = CsvEntityWriter.newInstance(
				new CsvWriter(sw, cfg),
				Price.class
			);
		try {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			writer.write(new Price("AAAA", "aaa", 10000, 10, df.parse("2008/10/28 10:24:00")));
			writer.write(new Price("BBBB", "bbb", null, 0, null));
			writer.write(new Price("CCCC", "ccc", 20000, 100, df.parse("2008/10/26 14:20:10")));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\nCCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n"));
	}

	@Test
	public void testFilter() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvEntityWriter<Price> writer = CsvEntityWriter.newInstance(
				new CsvWriter(sw, cfg),
				CsvEntityTemplate.newInstance(Price.class)
					.filter(new SimpleCsvNamedValueFilter()
							.ne("シンボル", "gcu09", true)
							.ne("日付", "2008/11/06")
						)
			);
		try {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			writer.write(new Price("GCU09", "COMEX 金 2009年09月限", 1068, 10, df.parse("2008/09/06 12:00:00")));
			writer.write(new Price("GCV09", "COMEX 金 2009年10月限", 1078, 11, df.parse("2008/10/06 12:00:00")));
			writer.write(new Price("GCX09", "COMEX 金 2009年11月限", 1088, 12, df.parse("2008/11/06 12:00:00")));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nGCV09,COMEX 金 2009年10月限,1\\,078,11,2008/10/06,12:00:00\r\n"));
	}

}