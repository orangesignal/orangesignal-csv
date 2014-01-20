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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.filters.SimpleBeanFilter;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;
import com.orangesignal.csv.model.SampleBean;

/**
 * {@link ColumnNameMappingBeanListHandler} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 */
public class ColumnNameMappingBeanListHandlerTest {

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
	public void testColumnNameMappingBeanListHandlerIllegalArgumentException() {
		new ColumnNameMappingBeanListHandler<SampleBean>(null);
	}

	@Test
	public void testLoad1() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg);
		try {
			final List<SampleBean> list = new ColumnNameMappingBeanListHandler<SampleBean>(SampleBean.class).load(reader);
			assertThat(list.size(), is(2));
			final SampleBean o1 = list.get(0);
			assertThat(o1.symbol, is("AAAA"));
			assertThat(o1.name, is("aaa"));
			assertThat(o1.price.longValue(), is(10000L));
			assertThat(o1.volume.longValue(), is(10L));
			final SampleBean o2 = list.get(1);
			assertThat(o2.symbol, is("BBBB"));
			assertThat(o2.name, is("bbb"));
			assertNull(o2.price);
			assertThat(o2.volume.longValue(), is(0L));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoad2() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader("シンボル,名称,価格,出来高\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg);
		try {
			final List<SampleBean> list = new ColumnNameMappingBeanListHandler<SampleBean>(SampleBean.class)
				.column("シンボル", "symbol")
				.column("名称",     "name")
				.column("価格",     "price")
				.column("出来高",   "volume")
				.load(reader);

			assertThat(list.size(), is(2));
			final SampleBean o1 = list.get(0);
			assertThat(o1.symbol, is("AAAA"));
			assertThat(o1.name, is("aaa"));
			assertThat(o1.price.longValue(), is(10000L));
			assertThat(o1.volume.longValue(), is(10L));
			final SampleBean o2 = list.get(1);
			assertThat(o2.symbol, is("BBBB"));
			assertThat(o2.name, is("bbb"));
			assertNull(o2.price);
			assertThat(o2.volume.longValue(), is(0L));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoad3() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader("シンボル,名称,価格,出来高\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg);
		try {
			final List<SampleBean> list = new ColumnNameMappingBeanListHandler<SampleBean>(SampleBean.class)
				.column("シンボル", "symbol")
				.column("価格",     "price")
				.column("出来高",   "volume")
				.load(reader);

			assertThat(list.size(), is(2));
			final SampleBean o1 = list.get(0);
			assertThat(o1.symbol, is("AAAA"));
			assertNull(o1.name);
			assertThat(o1.price.longValue(), is(10000L));
			assertThat(o1.volume.longValue(), is(10L));
			final SampleBean o2 = list.get(1);
			assertThat(o2.symbol, is("BBBB"));
			assertNull(o2.name);
			assertNull(o2.price);
			assertThat(o2.volume.longValue(), is(0L));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoad4() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader("シンボル,名称,価格,出来高\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg);
		try {
			final Map<String, String> columnMapping = new HashMap<String, String>();
			columnMapping.put("シンボル", "symbol");
			columnMapping.put("名称",     "name");
			columnMapping.put("価格",     "price");
			columnMapping.put("出来高",   "volume");
			final List<SampleBean> list = new ColumnNameMappingBeanListHandler<SampleBean>(SampleBean.class).columnMapping(columnMapping).load(reader);

			assertThat(list.size(), is(2));
			final SampleBean o1 = list.get(0);
			assertThat(o1.symbol, is("AAAA"));
			assertThat(o1.name, is("aaa"));
			assertThat(o1.price.longValue(), is(10000L));
			assertThat(o1.volume.longValue(), is(10L));
			final SampleBean o2 = list.get(1);
			assertThat(o2.symbol, is("BBBB"));
			assertThat(o2.name, is("bbb"));
			assertNull(o2.price);
			assertThat(o2.volume.longValue(), is(0L));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoad5() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader("シンボル,名称,価格,出来高\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg);
		try {
			final Map<String, String> columnMapping = new HashMap<String, String>();
			columnMapping.put("シンボル", "symbol");
			columnMapping.put("価格",     "price");
			columnMapping.put("出来高",   "volume");
			final List<SampleBean> list = new ColumnNameMappingBeanListHandler<SampleBean>(SampleBean.class).columnMapping(columnMapping).load(reader);

			assertThat(list.size(), is(2));
			final SampleBean o1 = list.get(0);
			assertThat(o1.symbol, is("AAAA"));
			assertNull(o1.name);
			assertThat(o1.price.longValue(), is(10000L));
			assertThat(o1.volume.longValue(), is(10L));
			final SampleBean o2 = list.get(1);
			assertThat(o2.symbol, is("BBBB"));
			assertNull(o2.name);
			assertNull(o2.price);
			assertThat(o2.volume.longValue(), is(0L));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoad6() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2009/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL"), cfg);
		try {
			final List<SampleBean> list = new ColumnNameMappingBeanListHandler<SampleBean>(SampleBean.class)
				.column("シンボル", "symbol")
				.column("名称",     "name")
				.column("価格",     "price", new DecimalFormat("#,##0"))
				.column("出来高",   "volume")
				.column("日付",     "date", new SimpleDateFormat("yyyy/MM/dd"))
				.column("時刻",     "date", new SimpleDateFormat("HH:mm:ss"))
				.load(reader);

			assertThat(list.size(), is(2));
			final SampleBean o1 = list.get(0);
			assertThat(o1.symbol, is("AAAA"));
			assertThat(o1.name, is("aaa"));
			assertThat(o1.price.longValue(), is(10000L));
			assertThat(o1.volume.longValue(), is(10L));
			assertThat(new SimpleDateFormat("yyyy/MM/dd").format(o1.date), is("2009/10/28"));
			final SampleBean o2 = list.get(1);
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
	public void testLoadOffsetLimit() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2009/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL"), cfg);
		try {
			final List<SampleBean> list = new ColumnNameMappingBeanListHandler<SampleBean>(SampleBean.class)
				.column("シンボル", "symbol")
				.column("名称",     "name")
				.column("価格",     "price", new DecimalFormat("#,##0"))
				.column("出来高",   "volume")
				.column("日付",     "date", new SimpleDateFormat("yyyy/MM/dd"))
				.column("時刻",     "date", new SimpleDateFormat("HH:mm:ss"))
				.offset(1)
				.limit(1)
				.load(reader);

			assertThat(list.size(), is(1));
			final SampleBean o2 = list.get(0);
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
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final CsvReader reader = new CsvReader(new StringReader(
				"シンボル,名称,価格,出来高,日付\r\n" +
				"GCQ09,COMEX 金 2009年08月限,1058.70,10,2008/08/06\r\n" +
				"GCU09,COMEX 金 2009年09月限,1068.70,10,2008/09/06\r\n" +
				"GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\n" +
				"GCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n" +
				"GCZ09,COMEX 金 2009年12月限,1098.70,13,2008/12/06\r\n"
			), cfg);
		try {
			final List<SampleBean> list = new ColumnNameMappingBeanListHandler<SampleBean>(SampleBean.class)
				.column("シンボル", "symbol")
				.column("名称", "name")
				.column("価格", "price")
				.column("出来高", "volume")
				.column("日付", "date", new SimpleDateFormat("yyyy/MM/dd"))
				.filter(new SimpleCsvNamedValueFilter().ne(0, "gcu09", true))
				.filter(new SimpleBeanFilter().ne("date", df.parse("2008/11/06")))
				.offset(1).limit(1)
				.load(reader);

			assertThat(list.size(), is(1));
			final SampleBean o1 = list.get(0);
			assertThat(o1.symbol, is("GCV09"));
			assertThat(o1.name, is("COMEX 金 2009年10月限"));
			assertThat(o1.price.doubleValue(), is(1078.70D));
			assertThat(o1.volume.longValue(), is(11L));
			assertThat(o1.date, is(df.parse("2008/10/06")));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testSave1() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");

		final List<SampleBean> list = new ArrayList<SampleBean>();
		list.add(new SampleBean("AAAA", "aaa", 10000, 10, df.parse("2009/10/28")));
		list.add(new SampleBean("BBBB", "bbb", null, 0, null));

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			new ColumnNameMappingBeanListHandler<SampleBean>(SampleBean.class).save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\nAAAA,aaa,10000,10,Wed Oct 28 00:00:00 JST 2009\r\nBBBB,bbb,NULL,0,NULL\r\n"));
	}

	@Test
	public void testSave2() throws IOException {
		final List<SampleBean> list = new ArrayList<SampleBean>();
		list.add(new SampleBean("AAAA", "aaa", 10000, 10, null));
		list.add(new SampleBean("BBBB", "bbb", null, 0, null));

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			new ColumnNameMappingBeanListHandler<SampleBean>(SampleBean.class)
				.column("シンボル", "symbol")
				.column("名称",     "name")
				.column("価格",     "price")
				.column("出来高",   "volume")
				.save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	public void testSave3() throws IOException {
		final List<SampleBean> list = new ArrayList<SampleBean>();
		list.add(new SampleBean("AAAA", "aaa", 10000, 10, null));
		list.add(new SampleBean("BBBB", "bbb", null, 0, null));

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			new ColumnNameMappingBeanListHandler<SampleBean>(SampleBean.class)
				.column("シンボル", "symbol")
				.column("価格",     "price")
				.column("出来高",   "volume")
				.save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,価格,出来高\r\nAAAA,10000,10\r\nBBBB,NULL,0\r\n"));
	}

	@Test
	public void testSave4() throws IOException {
		final List<SampleBean> list = new ArrayList<SampleBean>();
		list.add(new SampleBean("AAAA", "aaa", 10000, 10, null));
		list.add(new SampleBean("BBBB", "bbb", null, 0, null));

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			final Map<String, String> columnMapping = new LinkedHashMap<String, String>();
			columnMapping.put("シンボル", "symbol");
			columnMapping.put("名称",     "name");
			columnMapping.put("価格",     "price");
			columnMapping.put("出来高",   "volume");
			new ColumnNameMappingBeanListHandler<SampleBean>(SampleBean.class).columnMapping(columnMapping).save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	public void testSave5() throws IOException {
		final List<SampleBean> list = new ArrayList<SampleBean>();
		list.add(new SampleBean("AAAA", "aaa", 10000, 10, null));
		list.add(new SampleBean("BBBB", "bbb", null, 0, null));

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			final Map<String, String> columnMapping = new LinkedHashMap<String, String>();
			columnMapping.put("シンボル", "symbol");
			columnMapping.put("価格",     "price");
			columnMapping.put("出来高",   "volume");
			new ColumnNameMappingBeanListHandler<SampleBean>(SampleBean.class).columnMapping(columnMapping).save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,価格,出来高\r\nAAAA,10000,10\r\nBBBB,NULL,0\r\n"));
	}

	@Test
	public void testSave6() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		final List<SampleBean> list = new ArrayList<SampleBean>();
		list.add(new SampleBean("AAAA", "aaa", 10000, 10, df.parse("2008/10/28 10:24:00")));
		list.add(new SampleBean("BBBB", "bbb", null, 0, null));
		list.add(new SampleBean("CCCC", "ccc", 20000, 100, df.parse("2008/10/26 14:20:10")));

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			new ColumnNameMappingBeanListHandler<SampleBean>(SampleBean.class)
				.column("シンボル", "symbol")
				.column("名称",     "name")
				.column("価格",     "price", new DecimalFormat("#,##0"))
				.column("出来高",   "volume")
				.column("日付",     "date", new SimpleDateFormat("yyyy/MM/dd"))
				.column("時刻",     "date", new SimpleDateFormat("HH:mm:ss"))
				.save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\nCCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n"));
	}

	@Test
	public void testSaveFilter() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");

		final List<SampleBean> list = new ArrayList<SampleBean>();
		list.add(new SampleBean("GCU09", "COMEX 金 2009年09月限", 1068.70, 10, df.parse("2008/09/06")));
		list.add(new SampleBean("GCV09", "COMEX 金 2009年10月限", 1078.70, 11, df.parse("2008/10/06")));
		list.add(new SampleBean("GCX09", "COMEX 金 2009年11月限", 1088.70, 12, df.parse("2008/11/06")));

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			new ColumnNameMappingBeanListHandler<SampleBean>(SampleBean.class)
				.column("シンボル", "symbol")
				.column("名称",     "name")
				.column("価格",     "price", new DecimalFormat("0.00"))
				.column("出来高",   "volume")
				.column("日付",     "date", new SimpleDateFormat("yyyy/MM/dd"))
				.filter(new SimpleCsvNamedValueFilter().ne("シンボル", "gcu09", true))
				.filter(new SimpleBeanFilter().ne("date", df.parse("2008/11/06")))
				.save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付\r\nGCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\n"));
	}

}