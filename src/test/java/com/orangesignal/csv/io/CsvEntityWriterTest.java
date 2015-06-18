/*
 * Copyright 2014 the original author or authors.
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

package com.orangesignal.csv.io;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.QuotePolicy;
import com.orangesignal.csv.bean.CsvEntityTemplate;
import com.orangesignal.csv.entity.DefaultValuePrice;
import com.orangesignal.csv.entity.Price;
import com.orangesignal.csv.entity.WritableEntity;
import com.orangesignal.csv.entity.WritableNoHeaderEntity;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;
import com.orangesignal.csv.model.SampleQuote;

/**
 * {@link CsvEntityWriter} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public class CsvEntityWriterTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

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
			df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

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
	public void testWriteHeader1() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvEntityWriter<Price> writer = CsvEntityWriter.newInstance(new CsvWriter(sw, cfg), Price.class);
		try {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

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
	public void testWriteHeader2() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvEntityWriter<Price> writer = CsvEntityWriter.newInstance(new CsvWriter(sw, cfg), Price.class, true);
		try {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

			writer.writeHeader();
			writer.flush();
			assertThat(sw.getBuffer().toString(), is(""));

			writer.write(new Price("AAAA", "aaa", 10000, 10, df.parse("2008/10/28 10:24:00")));
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("AAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\n"));

			writer.writeHeader();
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("AAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\n"));

			writer.write(new Price("BBBB", "bbb", null, 0, null));
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("AAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\n"));

			writer.writeHeader();
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("AAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\n"));

			writer.write(new Price("CCCC", "ccc", 20000, 100, df.parse("2008/10/26 14:20:10")));
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("AAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\nCCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n"));

			writer.writeHeader();
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("AAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\nCCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n"));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("AAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\nCCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n"));
	}

	@Test
	public void testWriteDisableHeader1() throws Exception {
		final StringWriter sw = new StringWriter();

		// Arrange
		sw.append("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\nCCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n");
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\nCCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n"));

		// Act
		final CsvEntityWriter<Price> writer = CsvEntityWriter.newInstance(new CsvWriter(sw, cfg), Price.class, true);
		try {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

			writer.write(new Price("AAAA", "aaa", 10000, 10, df.parse("2008/10/28 10:24:00")));
			writer.write(new Price("BBBB", "bbb", null, 0, null));
			writer.write(new Price("CCCC", "ccc", 20000, 100, df.parse("2008/10/26 14:20:10")));
		} finally {
			writer.close();
		}

		// Assert
		assertThat(sw.getBuffer().toString(), is(
				"シンボル,名称,価格,出来高,日付,時刻\r\n" +
				"AAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\n" +
				"BBBB,bbb,NULL,0,NULL,NULL\r\n" +
				"CCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n" +
				"AAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\n" +
				"BBBB,bbb,NULL,0,NULL,NULL\r\n" +
				"CCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n"
			));
	}

	@Test
	public void testWriteDisableHeader2() throws Exception {
		final StringWriter sw = new StringWriter();

		// Arrange
		sw.append("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\nCCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n");

		// Act
		final CsvEntityWriter<Price> writer = CsvEntityWriter.newInstance(new CsvWriter(sw, cfg), Price.class, false);
		try {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

			writer.write(new Price("AAAA", "aaa", 10000, 10, df.parse("2008/10/28 10:24:00")));
			writer.write(new Price("BBBB", "bbb", null, 0, null));
			writer.write(new Price("CCCC", "ccc", 20000, 100, df.parse("2008/10/26 14:20:10")));
		} finally {
			writer.close();
		}

		// Assert
		assertThat(sw.getBuffer().toString(), is(
				"シンボル,名称,価格,出来高,日付,時刻\r\n" +
				"AAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\n" +
				"BBBB,bbb,NULL,0,NULL,NULL\r\n" +
				"CCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n" +
				"シンボル,名称,価格,出来高,日付,時刻\r\n" +
				"AAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\n" +
				"BBBB,bbb,NULL,0,NULL,NULL\r\n" +
				"CCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n"
			));
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
			df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

			writer.write(new Price("AAAA", "aaa", 10000, 10, df.parse("2008/10/28 10:24:00")));
			writer.write(new Price("BBBB", "bbb", null, 0, null));
			writer.write(new Price("CCCC", "ccc", 20000, 100, df.parse("2008/10/26 14:20:10")));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\nCCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n"));
	}
/*
	@Test
	public void testIssue29() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvEntityWriter<Travel> writer = CsvEntityWriter.newInstance(
				new CsvWriter(sw, cfg),
				Travel.class
			);
		try {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

			writer.write(new Travel("aaa", df.parse("2008/10/28")));
			writer.write(new Travel("bbb", null));
			writer.write(new Travel("ccc", df.parse("2008/10/26")));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("CODE,DATE\r\naaa,2008/10/28\r\nbbb,NULL\r\nccc,2008/10/26\r\n"));
	}
*/
	@Test
	public void testDefaultValue() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvEntityWriter<DefaultValuePrice> writer = CsvEntityWriter.newInstance(
				new CsvWriter(sw, cfg),
				DefaultValuePrice.class
			);
		try {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

			writer.write(new DefaultValuePrice("AAAA", "aaa", 10000, 10, df.parse("2008/10/28 10:24:00")));
			writer.write(new DefaultValuePrice(null, null, null, 0, null));
			writer.write(new DefaultValuePrice("CCCC", "ccc", 20000, 100, df.parse("2008/10/26 14:20:10")));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nXXXX,xxx,NULL,0,2014/02/02,12:00:00\r\nCCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n"));
	}

	@Test
	public void testWritable() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvEntityWriter<WritableEntity> writer = CsvEntityWriter.newInstance(
				new CsvWriter(sw, cfg),
				WritableEntity.class
			);
		try {
			writer.writeHeader();
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("col2,col3\r\n"));

			final WritableEntity o1 = new WritableEntity();
			o1.array = new String[]{ "あ", null, "う" };
			o1.str = "えお";
			writer.write(o1);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("col2,col3\r\nNULL,う\r\n"));

			final WritableEntity o2 = new WritableEntity();
			o2.array = new String[]{ "ア", "イ", "ウ" };
			o2.str = "エオ";
			writer.write(o2);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("col2,col3\r\nNULL,う\r\nイ,ウ\r\n"));

			final WritableEntity o3 = new WritableEntity();
			o3.array = new String[]{ null, null, null };
			o3.str = null;
			writer.write(o3);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("col2,col3\r\nNULL,う\r\nイ,ウ\r\nNULL,NULL\r\n"));

			final WritableEntity o4 = new WritableEntity();
			o4.array = null;
			o4.str = "null";
			writer.write(o4);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("col2,col3\r\nNULL,う\r\nイ,ウ\r\nNULL,NULL\r\nNULL,NULL\r\n"));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("col2,col3\r\nNULL,う\r\nイ,ウ\r\nNULL,NULL\r\nNULL,NULL\r\n"));
	}

	@Test
	public void testWritableNoHeader() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvEntityWriter<WritableNoHeaderEntity> writer = CsvEntityWriter.newInstance(
				new CsvWriter(sw, cfg),
				WritableNoHeaderEntity.class
			);
		try {
			final WritableNoHeaderEntity o1 = new WritableNoHeaderEntity();
			o1.array = new String[]{ "あ", null, "う" };
			o1.str = "えお";
			writer.write(o1);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("NULL,う\r\n"));

			final WritableNoHeaderEntity o2 = new WritableNoHeaderEntity();
			o2.array = new String[]{ "ア", "イ", "ウ" };
			o2.str = "エオ";
			writer.write(o2);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("NULL,う\r\nイ,ウ\r\n"));

			final WritableNoHeaderEntity o3 = new WritableNoHeaderEntity();
			o3.array = new String[]{ null, null, null };
			o3.str = null;
			writer.write(o3);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("NULL,う\r\nイ,ウ\r\nNULL,NULL\r\n"));

			final WritableNoHeaderEntity o4 = new WritableNoHeaderEntity();
			o4.array = null;
			o4.str = "null";
			writer.write(o4);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("NULL,う\r\nイ,ウ\r\nNULL,NULL\r\nNULL,NULL\r\n"));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("NULL,う\r\nイ,ウ\r\nNULL,NULL\r\nNULL,NULL\r\n"));
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
			df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

			writer.write(new Price("GCU09", "COMEX 金 2009年09月限", 1068, 10, df.parse("2008/09/06 12:00:00")));
			writer.write(new Price("GCV09", "COMEX 金 2009年10月限", 1078, 11, df.parse("2008/10/06 12:00:00")));
			writer.write(new Price("GCX09", "COMEX 金 2009年11月限", 1088, 12, df.parse("2008/11/06 12:00:00")));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nGCV09,COMEX 金 2009年10月限,1\\,078,11,2008/10/06,12:00:00\r\n"));
	}

	/**
	 * 
	public void testWrite() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvEntityWriter<Price> writer = CsvEntityWriter.newInstance(
				new CsvWriter(sw, cfg),
				Price.class
			);
		try {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

			writer.write(new Price("AAAA", "aaa", 10000, 10, df.parse("2008/10/28 10:24:00")));
			writer.write(new Price("BBBB", "bbb", null, 0, null));
			writer.write(new Price("CCCC", "ccc", 20000, 100, df.parse("2008/10/26 14:20:10")));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\nCCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n"));
	}	 * 
	 */

	@Test
	public void testWriteQuote() throws Exception {
		CsvConfig cfg = new CsvConfig(',');
		cfg.setEscapeDisabled(false);
		cfg.setNullString("NULL");
		cfg.setIgnoreTrailingWhitespaces(true);
		cfg.setIgnoreLeadingWhitespaces(true);
		cfg.setIgnoreEmptyLines(true);
		cfg.setLineSeparator(Constants.CRLF);
		cfg.setQuoteDisabled(false);
		cfg.setQuotePolicy(QuotePolicy.COLUMN);

		final StringWriter sw = new StringWriter();
		final CsvEntityWriter<SampleQuote> writer = CsvEntityWriter.newInstance(
				new CsvWriter(sw, cfg),
				SampleQuote.class
			);
		try {
			writer.write(new SampleQuote(1, "aaa"));
			writer.write(new SampleQuote(2, ""));
			writer.write(new SampleQuote(3, null));
			writer.write(new SampleQuote(4, "d\"d\"d"));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("No.,ラベル\r\n1,\"aaa\"\r\n2,NULL\r\n3,NULL\r\n4,\"d\\\"d\\\"d\"\r\n"));
	}

	// ------------------------------------------------------------------------
	// getter / setter

	@Test
	public void testGetTemplate() throws Exception {
		// Arrange
		final CsvEntityWriter<Price> writer = new CsvEntityWriter<Price>(new CsvWriter(new StringWriter(), cfg), Price.class);
		try {
			// Act & Assert
			assertThat(writer.getTemplate(), notNullValue());
		} finally {
			writer.close();
		}
	}

	@Test
	public void testIsDisableWriteHeader() throws IOException {
		CsvEntityWriter<Price> writer;

		// Arrange
		writer = CsvEntityWriter.newInstance(new CsvWriter(new StringWriter(), cfg), Price.class);
		try {
			// Act & Assert
			assertThat(writer.isDisableWriteHeader(), is(false));
		} finally {
			writer.close();
		}

		// Arrange
		writer = CsvEntityWriter.newInstance(new CsvWriter(new StringWriter(), cfg), Price.class, true);
		try {
			// Act & Assert
			assertThat(writer.isDisableWriteHeader(), is(true));
		} finally {
			writer.close();
		}

		// Arrange
		writer = new CsvEntityWriter<Price>(new CsvWriter(new StringWriter(), cfg), Price.class, false);
		try {
			// Act & Assert
			assertThat(writer.isDisableWriteHeader(), is(false));
		} finally {
			writer.close();
		}

		// Arrange
		writer = new CsvEntityWriter<Price>(new CsvWriter(new StringWriter(), cfg), Price.class, true);
		try {
			// Act & Assert
			assertThat(writer.isDisableWriteHeader(), is(true));
		} finally {
			writer.close();
		}
	}

}