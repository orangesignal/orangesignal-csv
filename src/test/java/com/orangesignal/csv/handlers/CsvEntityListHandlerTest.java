/*
 * Copyright 2009 the original author or authors.
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

package com.orangesignal.csv.handlers;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Test;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.QuotePolicy;
import com.orangesignal.csv.entity.Price;
import com.orangesignal.csv.entity.Price2;
import com.orangesignal.csv.filters.SimpleBeanFilter;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;
import com.orangesignal.csv.model.SampleBean;
import com.orangesignal.csv.model.SampleQuote;

/**
 * {@link CsvEntityListHandler} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 */
public class CsvEntityListHandlerTest {

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

	@Test(expected = IllegalArgumentException.class)
	public void testCsvEntityListHandlerIllegalArgumentException1() {
		new CsvEntityListHandler<Price>(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCsvEntityListHandlerIllegalArgumentException2() {
		new CsvEntityListHandler<SampleBean>(SampleBean.class);
	}

	@Test
	public void testLoadPrice() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2009/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL"), cfg);
		try {
			final List<Price> list = new CsvEntityListHandler<Price>(Price.class)
				.load(reader);

			assertThat(list.size(), is(2));
			final Price o1 = list.get(0);
			assertThat(o1.symbol, is("AAAA"));
			assertThat(o1.name, is("aaa"));
			assertThat(o1.price.longValue(), is(10000L));
			assertThat(o1.volume.longValue(), is(10L));

			final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

			assertThat(df.format(o1.date), is("2009/10/28 10:24:00"));
			final Price o2 = list.get(1);
			assertThat(o2.symbol, is("BBBB"));
			assertThat(o2.name, is("bbb"));
			assertNull(o2.price);
			assertThat(o2.volume.longValue(), is(0L));
			assertNull(o2.date);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoadPrice2() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2009/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL"), cfg);
		try {
			final List<Price2> list = new CsvEntityListHandler<Price2>(Price2.class)
				.load(reader);

			assertThat(list.size(), is(2));
			final Price2 o1 = list.get(0);
			assertNull(o1.symbol);
			assertNull(o1.name);
			assertThat(o1.price.longValue(), is(10000L));
			assertThat(o1.volume.longValue(), is(10L));
			assertNull(o1.date);
			final Price2 o2 = list.get(1);
			assertNull(o2.symbol);
			assertNull(o2.name);
			assertNull(o2.price);
			assertThat(o2.volume.longValue(), is(0L));
			assertNull(o2.date);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoadOffsetLimit() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2009/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL"), cfg);
		try {
			final List<Price> list = new CsvEntityListHandler<Price>(Price.class).offset(1).limit(1).load(reader);
			assertThat(list.size(), is(1));
			final Price o2 = list.get(0);
			assertThat(o2.symbol, is("BBBB"));
			assertThat(o2.name, is("bbb"));
			assertNull(o2.price);
			assertThat(o2.volume.longValue(), is(0L));
			assertNull(o2.date);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoadFilter() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
		final CsvReader reader = new CsvReader(new StringReader(
				"シンボル,名称,価格,出来高,日付,時刻\r\n" +
				"GCQ09,COMEX 金 2009年08月限,1\\,058.70,10,2008/08/06,12:00:00\r\n" +
				"GCU09,COMEX 金 2009年09月限,1\\,068.70,10,2008/09/06,12:00:00\r\n" +
				"GCV09,COMEX 金 2009年10月限,1\\,078.70,11,2008/10/06,12:00:00\r\n" +
				"GCX09,COMEX 金 2009年11月限,1\\,088.70,12,2008/11/06,12:00:00\r\n" +
				"GCZ09,COMEX 金 2009年12月限,1\\,098.70,13,2008/12/06,12:00:00\r\n"
			), cfg);
		try {
			final List<Price> list = new CsvEntityListHandler<Price>(Price.class)
				.filter(new SimpleCsvNamedValueFilter().ne("シンボル", "gcu09", true))
				.filter(new SimpleBeanFilter().ne("date", df.parse("2008/11/06 12:00:00")))
				.offset(1).limit(1)
				.load(reader);

			assertThat(list.size(), is(1));
			final Price o1 = list.get(0);
			assertThat(o1.symbol, is("GCV09"));
			assertThat(o1.name, is("COMEX 金 2009年10月限"));
			assertThat(o1.price.doubleValue(), is(1078.70D));
			assertThat(o1.volume.longValue(), is(11L));
			assertThat(o1.date, is(df.parse("2008/10/06 12:00:00")));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testSave() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

		final List<Price> list = new ArrayList<Price>();
		list.add(new Price("AAAA", "aaa", 10000, 10, df.parse("2008/10/28 10:24:00")));
		list.add(new Price("BBBB", "bbb", null, 0, null));
		list.add(new Price("CCCC", "ccc", 20000, 100, df.parse("2008/10/26 14:20:10")));

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			new CsvEntityListHandler<Price>(Price.class).save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\nCCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n"));
	}

	@Test
	public void testSaveFilter() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

		final List<Price> list = new ArrayList<Price>();
		list.add(new Price("GCU09", "COMEX 金 2009年09月限", 1068, 10, df.parse("2008/09/06 12:00:00")));
		list.add(new Price("GCV09", "COMEX 金 2009年10月限", 1078, 11, df.parse("2008/10/06 12:00:00")));
		list.add(new Price("GCX09", "COMEX 金 2009年11月限", 1088, 12, df.parse("2008/11/06 12:00:00")));

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			new CsvEntityListHandler<Price>(Price.class)
				.filter(new SimpleCsvNamedValueFilter().ne("シンボル", "gcu09", true))
				.filter(new SimpleBeanFilter().ne("date", df.parse("2008/11/06 12:00:00")))
				.save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nGCV09,COMEX 金 2009年10月限,1\\,078,11,2008/10/06,12:00:00\r\n"));
	}

	@Test
	public void testSaveQuote() throws Exception {
		CsvConfig cfg = new CsvConfig(',');
		cfg.setEscapeDisabled(false);
		cfg.setNullString("NULL");
		cfg.setIgnoreTrailingWhitespaces(true);
		cfg.setIgnoreLeadingWhitespaces(true);
		cfg.setIgnoreEmptyLines(true);
		cfg.setLineSeparator(Constants.CRLF);
		cfg.setQuoteDisabled(false);
		cfg.setQuotePolicy(QuotePolicy.COLUMN);

		final List<SampleQuote> list = new ArrayList<SampleQuote>();
		list.add(new SampleQuote(1, "aaa"));
		list.add(new SampleQuote(2, ""));
		list.add(new SampleQuote(3, null));
		list.add(new SampleQuote(4, "d\"d\"d"));

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			new CsvEntityListHandler<SampleQuote>(SampleQuote.class).save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("No.,ラベル\r\n1,\"aaa\"\r\n2,NULL\r\n3,NULL\r\n4,\"d\\\"d\\\"d\"\r\n"));
	}

}