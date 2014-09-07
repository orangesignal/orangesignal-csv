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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.annotation.CsvColumnException;
import com.orangesignal.csv.bean.CsvEntityTemplate;
import com.orangesignal.csv.entity.DefaultValuePrice;
import com.orangesignal.csv.entity.Issue30;
import com.orangesignal.csv.entity.Price;
import com.orangesignal.csv.entity.Price2;
import com.orangesignal.csv.entity.RequiredPrice;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;

/**
 * {@link CsvEntityReader} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public class CsvEntityReaderTest {

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
	public void testNewInstanceCsvReaderClass() throws IOException {
		final CsvEntityReader<Price> reader = CsvEntityReader.newInstance(
				new CsvReader(new StringReader("")),
				Price.class
			);
		reader.close();
	}

	@Test
	public void testNewInstanceCsvReaderCsvBeanTemplate() throws IOException {
		final CsvEntityReader<Price> reader = CsvEntityReader.newInstance(
				new CsvReader(new StringReader("")),
				CsvEntityTemplate.newInstance(Price.class)
			);
		reader.close();
	}

	// ------------------------------------------------------------------------
	// コンストラクタ

	@Test
	public void testConstructorCsvReaderClass() throws IOException {
		final CsvEntityReader<Price> reader = new CsvEntityReader<Price>(
				new CsvReader(new StringReader("")),
				Price.class
			);
		reader.close();
	}

	@Test
	public void testConstructorCsvReaderClassIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvReader must not be null");
		final CsvEntityReader<Price> reader = new CsvEntityReader<Price>(
				null,
				Price.class
			);
		reader.close();
	}

	@Test
	public void testConstructorCsvReaderClassIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Class must not be null");
		final Class<Price> type = null;
		final CsvEntityReader<Price> reader = new CsvEntityReader<Price>(
				new CsvReader(new StringReader("")),
				type
			);
		reader.close();
	}

	@Test
	public void testConstructorCsvReaderCsvBeanTemplate() throws IOException {
		final CsvEntityReader<Price> reader = new CsvEntityReader<Price>(
				new CsvReader(new StringReader("")),
				CsvEntityTemplate.newInstance(Price.class)
			);
		reader.close();
	}

	@Test
	public void testConstructorCsvReaderCsvBeanTemplateIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvReader must not be null");
		final CsvEntityReader<Price> reader = new CsvEntityReader<Price>(
				null,
				CsvEntityTemplate.newInstance(Price.class)
			);
		reader.close();
	}

	@Test
	public void testConstructorCsvReaderClassCsvBeanConfigIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvEntityTemplate must not be null");
		final CsvEntityTemplate<Price> template = null;
		final CsvEntityReader<Price> reader = new CsvEntityReader<Price>(
				new CsvReader(new StringReader("")),
				template
			);
		reader.close();
	}

	// ------------------------------------------------------------------------
	// オーバーライド メソッド

	@Test
	public void testClosed() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("CsvReader closed");
		final CsvEntityReader<Price> reader = CsvEntityReader.newInstance(new CsvReader(new StringReader("")), Price.class);
		reader.close();
		// Act
		reader.close();
	}

	// ------------------------------------------------------------------------
	// パブリック メソッド

	@Test
	public void testRequiredCsvColumnException() throws IOException {
		exception.expect(CsvColumnException.class);
		exception.expectMessage(String.format("[line: %d] %s must not be null", 3, "シンボル"));

		final CsvEntityReader<RequiredPrice> reader = CsvEntityReader.newInstance(
				new CsvReader(new StringReader("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2009/10/28,10:24:00\r\nNULL,NULL,NULL,0,NULL,NULL"), cfg),
				RequiredPrice.class
			);
		try {
			final RequiredPrice o1 = reader.read();
			assertThat(o1.symbol, is("AAAA"));
			assertThat(o1.name, is("aaa"));
			assertThat(o1.price.longValue(), is(10000L));
			assertThat(o1.volume.longValue(), is(10L));

			final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
			assertThat(df.format(o1.date), is("2009/10/28 10:24:00"));

			// Act
			reader.read();
		} finally {
			reader.close();
		}
	}

	@Test
	public void testRequired() throws IOException {
		final CsvEntityReader<RequiredPrice> reader = CsvEntityReader.newInstance(
				new CsvReader(new StringReader("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2009/10/28,10:24:00\r\nXXXX,xxx,NULL,0,NULL,NULL"), cfg),
				RequiredPrice.class
			);
		try {
			final RequiredPrice o1 = reader.read();
			assertThat(o1.symbol, is("AAAA"));
			assertThat(o1.name, is("aaa"));
			assertThat(o1.price.longValue(), is(10000L));
			assertThat(o1.volume.longValue(), is(10L));

			final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
			assertThat(df.format(o1.date), is("2009/10/28 10:24:00"));

			final RequiredPrice o2 = reader.read();
			assertThat(o2.symbol, is("XXXX"));
			assertThat(o2.name, is("xxx"));
			assertNull(o2.price);
			assertThat(o2.volume.longValue(), is(0L));
			assertThat(df.format(o2.date), is("2014/02/02 12:00:00"));

			final RequiredPrice last = reader.read();
			assertNull(last);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testDefaultValue() throws IOException {
		final CsvEntityReader<DefaultValuePrice> reader = CsvEntityReader.newInstance(
				new CsvReader(new StringReader("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2009/10/28,10:24:00\r\nNULL,NULL,NULL,0,NULL,NULL"), cfg),
				DefaultValuePrice.class
			);
		try {
			final DefaultValuePrice o1 = reader.read();
			assertThat(o1.symbol, is("AAAA"));
			assertThat(o1.name, is("aaa"));
			assertThat(o1.price.longValue(), is(10000L));
			assertThat(o1.volume.longValue(), is(10L));

			final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
			assertThat(df.format(o1.date), is("2009/10/28 10:24:00"));

			final DefaultValuePrice o2 = reader.read();
			assertThat(o2.symbol, is("XXXX"));
			assertThat(o2.name, is("xxx"));
			assertNull(o2.price);
			assertThat(o2.volume.longValue(), is(0L));
			assertThat(df.format(o2.date), is("2014/02/02 12:00:00"));

			final DefaultValuePrice last = reader.read();
			assertNull(last);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoadPrice() throws IOException {
		final CsvEntityReader<Price> reader = CsvEntityReader.newInstance(
				new CsvReader(new StringReader("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2009/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL"), cfg),
				Price.class
			);
		try {
			final Price o1 = reader.read();
			assertThat(o1.symbol, is("AAAA"));
			assertThat(o1.name, is("aaa"));
			assertThat(o1.price.longValue(), is(10000L));
			assertThat(o1.volume.longValue(), is(10L));

			final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
			assertThat(df.format(o1.date), is("2009/10/28 10:24:00"));

			final Price o2 = reader.read();
			assertThat(o2.symbol, is("BBBB"));
			assertThat(o2.name, is("bbb"));
			assertNull(o2.price);
			assertThat(o2.volume.longValue(), is(0L));
			assertNull(o2.date);

			final Price last = reader.read();
			assertNull(last);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testIssue30() throws IOException {
		final CsvConfig cfg = new CsvConfig();
		cfg.setIgnoreEmptyLines(true);

		final CsvEntityReader<Issue30> reader = CsvEntityReader.newInstance(
				new CsvReader(new StringReader("1,name\r\n\r\n2,dare"), cfg),
				Issue30.class
			);
		try {
			final Issue30 o1 = reader.read();
			assertThat(o1.no, is(1));
			assertThat(o1.name, is("name"));

			final Issue30 o2 = reader.read();
			assertNull(o2);

			final Issue30 o3 = reader.read();
			assertThat(o3.no, is(2));
			assertThat(o3.name, is("dare"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoadPrice2() throws IOException {
		final CsvEntityReader<Price2> reader = CsvEntityReader.newInstance(
				new CsvReader(new StringReader("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2009/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL"), cfg),
				Price2.class
			);
		try {
			final Price2 o1 = reader.read();
			assertNull(o1.symbol);
			assertNull(o1.name);
			assertThat(o1.price.longValue(), is(10000L));
			assertThat(o1.volume.longValue(), is(10L));
			assertNull(o1.date);

			final Price2 o2 = reader.read();
			assertNull(o2.symbol);
			assertNull(o2.name);
			assertNull(o2.price);
			assertThat(o2.volume.longValue(), is(0L));
			assertNull(o2.date);

			final Price2 last = reader.read();
			assertNull(last);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testFilter() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

		final CsvEntityReader<Price> reader = CsvEntityReader.newInstance(
				new CsvReader(new StringReader(
						"シンボル,名称,価格,出来高,日付,時刻\r\n" +
						"GCQ09,COMEX 金 2009年08月限,1\\,058.70,10,2008/08/06,12:00:00\r\n" +
						"GCU09,COMEX 金 2009年09月限,1\\,068.70,10,2008/09/06,12:00:00\r\n" +
						"GCV09,COMEX 金 2009年10月限,1\\,078.70,11,2008/10/06,12:00:00\r\n" +
						"GCX09,COMEX 金 2009年11月限,1\\,088.70,12,2008/11/06,12:00:00\r\n" +
						"GCZ09,COMEX 金 2009年12月限,1\\,098.70,13,2008/12/06,12:00:00\r\n"
					), cfg),
					CsvEntityTemplate.newInstance(Price.class)
						.filter(new SimpleCsvNamedValueFilter()
								.ne("シンボル", "gcu09", true)
								.ne("日付", "2008/11/06")
							)
			);
		try {
			final Price o0 = reader.read();
			assertThat(o0.symbol, is("GCQ09"));
			assertThat(o0.name, is("COMEX 金 2009年08月限"));
			assertThat(o0.price.doubleValue(), is(1058.70D));
			assertThat(o0.volume.longValue(), is(10L));
			assertThat(o0.date, is(df.parse("2008/08/06 12:00:00")));

			final Price o1 = reader.read();
			assertThat(o1.symbol, is("GCV09"));
			assertThat(o1.name, is("COMEX 金 2009年10月限"));
			assertThat(o1.price.doubleValue(), is(1078.70D));
			assertThat(o1.volume.longValue(), is(11L));
			assertThat(o1.date, is(df.parse("2008/10/06 12:00:00")));

			final Price o2 = reader.read();
			assertThat(o2.symbol, is("GCZ09"));
			assertThat(o2.name, is("COMEX 金 2009年12月限"));
			assertThat(o2.price.doubleValue(), is(1098.70D));
			assertThat(o2.volume.longValue(), is(13L));
			assertThat(o2.date, is(df.parse("2008/12/06 12:00:00")));

			final Price last = reader.read();
			assertNull(last);
		} finally {
			reader.close();
		}
	}

}