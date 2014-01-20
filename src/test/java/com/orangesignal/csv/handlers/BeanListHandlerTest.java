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
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.bean.NullCsvValueConverter;
import com.orangesignal.csv.filters.SimpleBeanFilter;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;
import com.orangesignal.csv.model.SampleBean;

/**
 * {@link BeanListHandler} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 */
public class BeanListHandlerTest {

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
	public void testBeanListHandlerIllegalArgumentException() {
		new BeanListHandler<SampleBean>(null);
	}



	@Test
	public void testGetType() {
		assertThat(new BeanListHandler<SampleBean>(SampleBean.class).getType().getName(), is(SampleBean.class.getName()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValueParserMappingIllegalArgumentException() {
		new BeanListHandler<SampleBean>(SampleBean.class).valueParserMapping(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValueFormatterMappingIllegalArgumentException() {
		new BeanListHandler<SampleBean>(SampleBean.class).valueFormatterMapping(null);
	}

	@Test
	public void testValueConverter() {
		new BeanListHandler<SampleBean>(SampleBean.class).valueConverter(new NullCsvValueConverter());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValueConverterIllegalArgumentException() {
		new BeanListHandler<SampleBean>(SampleBean.class).valueConverter(null);
	}



	@Test(expected = IllegalArgumentException.class)
	public void testIncludesIllegalArgumentException() {
		new BeanListHandler<SampleBean>(SampleBean.class).excludes("aaa").includes("bbb");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExcludesIllegalArgumentException() {
		new BeanListHandler<SampleBean>(SampleBean.class).includes("aaa").excludes("bbb");
	}

	@Test
	public void testLoad1() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg);
		try {
			final List<SampleBean> list = new BeanListHandler<SampleBean>(SampleBean.class).load(reader);
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
		final CsvReader reader = new CsvReader(new StringReader("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg);
		try {
			final List<SampleBean> list = new BeanListHandler<SampleBean>(SampleBean.class).includes("name").load(reader);
			assertThat(list.size(), is(2));
			final SampleBean o1 = list.get(0);
			assertNull(o1.symbol);
			assertThat(o1.name, is("aaa"));
			assertNull(o1.price);
			assertNull(o1.volume);
			final SampleBean o2 = list.get(1);
			assertNull(o2.symbol);
			assertThat(o2.name, is("bbb"));
			assertNull(o2.price);
			assertNull(o2.volume);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoad3() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg);
		try {
			final List<SampleBean> list = new BeanListHandler<SampleBean>(SampleBean.class).excludes("name").load(reader);
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
	public void testLoad4() throws Exception {
		final CsvReader reader = new CsvReader(new StringReader("symbol,name,price,volume,date\r\nAAAA,aaa,10\\,000,10,2008/10/28\r\nBBBB,bbb,NULL,0,NULL"), cfg);
		try {
			final List<SampleBean> list = new BeanListHandler<SampleBean>(SampleBean.class)
				.excludes("name")
				.format("price", new DecimalFormat("#,##0"))
				.format("date", new SimpleDateFormat("yyyy/MM/dd"))
				.load(reader);

			assertThat(list.size(), is(2));
			final SampleBean o1 = list.get(0);
			assertThat(o1.symbol, is("AAAA"));
			assertNull(o1.name);
			assertThat(o1.price.longValue(), is(10000L));
			assertThat(o1.volume.longValue(), is(10L));
			assertThat(new SimpleDateFormat("yyyy/MM/dd").format(o1.date), is("2008/10/28"));
			final SampleBean o2 = list.get(1);
			assertThat(o2.symbol, is("BBBB"));
			assertNull(o2.name);
			assertNull(o2.price);
			assertThat(o2.volume.longValue(), is(0L));
			assertNull(o2.date);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoadOffsetLimit() throws Exception {
		final CsvReader reader = new CsvReader(new StringReader("name\nA\nB\nC\nD\nE\nF\nG"), cfg);
		try {
			final List<SampleBean> list = new BeanListHandler<SampleBean>(SampleBean.class)
				.includes("name")
				.offset(2)
				.limit(3)
				.load(reader);

			assertThat(list.size(), is(3));
			assertThat(list.get(0).name, is("C"));
			assertThat(list.get(1).name, is("D"));
			assertThat(list.get(2).name, is("E"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoadFilter() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final CsvReader reader = new CsvReader(new StringReader(
				"symbol,name,price,volume,date\r\n" +
				"GCQ09,COMEX 金 2009年08月限,1058.70,10,2008/08/06\r\n" +
				"GCU09,COMEX 金 2009年09月限,1068.70,10,2008/09/06\r\n" +
				"GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\n" +
				"GCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n" +
				"GCZ09,COMEX 金 2009年12月限,1098.70,13,2008/12/06\r\n"
			), cfg);
		try {
			final List<SampleBean> list = new BeanListHandler<SampleBean>(SampleBean.class)
				.format("date", new SimpleDateFormat("yyyy/MM/dd"))
				.filter(new SimpleCsvNamedValueFilter().ne("symbol", "gcu09", true))
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
	public void testSort() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final CsvReader reader = new CsvReader(new StringReader(
				"symbol,name,price,volume,date\r\n" +
				"GCQ09,COMEX 金 2009年08月限,1058.70,10,2008/08/06\r\n" +
				"GCU09,COMEX 金 2009年09月限,1068.70,10,2008/09/06\r\n" +
				"GCV09,COMEX 金 2009年10月限,1088.70,11,2008/10/06\r\n" +
				"GCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n" +
				"GCZ09,COMEX 金 2009年12月限,1098.70,13,2008/12/06\r\n"
			), cfg);
		try {
			final List<SampleBean> list = new BeanListHandler<SampleBean>(SampleBean.class)
				.format("date", new SimpleDateFormat("yyyy/MM/dd"))
				.order(BeanOrder.desc("price"), BeanOrder.asc("volume"))
				.offset(1).limit(3)
				.load(reader);

			assertThat(list.size(), is(3));
			final SampleBean o1 = list.get(0);
			assertThat(o1.symbol, is("GCV09"));
			assertThat(o1.name, is("COMEX 金 2009年10月限"));
			assertThat(o1.price.doubleValue(), is(1088.70D));
			assertThat(o1.volume.longValue(), is(11L));
			assertThat(o1.date, is(df.parse("2008/10/06")));
			final SampleBean o2 = list.get(1);
			assertThat(o2.symbol, is("GCX09"));
			assertThat(o2.name, is("COMEX 金 2009年11月限"));
			assertThat(o2.price.doubleValue(), is(1088.70D));
			assertThat(o2.volume.longValue(), is(12L));
			assertThat(o2.date, is(df.parse("2008/11/06")));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testSave1() throws IOException {
		final List<SampleBean> list = new ArrayList<SampleBean>();
		list.add(new SampleBean("AAAA", "aaa", 10000, 10, null));
		list.add(new SampleBean("BBBB", "bbb", null, 0, null));

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			new BeanListHandler<SampleBean>(SampleBean.class).save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\nAAAA,aaa,10000,10,NULL\r\nBBBB,bbb,NULL,0,NULL\r\n"));
	}

	@Test
	public void testSave2() throws IOException {
		final List<SampleBean> list = new ArrayList<SampleBean>();
		list.add(new SampleBean("AAAA", "aaa", 10000, 10, null));
		list.add(new SampleBean("BBBB", "bbb", null, 0, null));

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			new BeanListHandler<SampleBean>(SampleBean.class).includes("name").save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("name\r\naaa\r\nbbb\r\n"));
	}

	@Test
	public void testSave3() throws IOException {
		final List<SampleBean> list = new ArrayList<SampleBean>();
		list.add(new SampleBean("AAAA", "aaa", 10000, 10, null));
		list.add(new SampleBean("BBBB", "bbb", null, 0, null));

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			new BeanListHandler<SampleBean>(SampleBean.class).excludes("name", "date").save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,price,volume\r\nAAAA,10000,10\r\nBBBB,NULL,0\r\n"));
	}

	@Test
	public void testSave4() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");

		final List<SampleBean> list = new ArrayList<SampleBean>();
		list.add(new SampleBean("AAAA", "aaa", 10000, 10, df.parse("2008/10/28")));
		list.add(new SampleBean("BBBB", "bbb", null, 0, null));
		list.add(new SampleBean("CCCC", "ccc", 20000, 100, df.parse("2008/10/26")));

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			new BeanListHandler<SampleBean>(SampleBean.class)
				.excludes("name")
				.format("price", new DecimalFormat("#,##0"))
				.format("date", new SimpleDateFormat("yyyy/MM/dd"))
				.save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,price,volume,date\r\nAAAA,10\\,000,10,2008/10/28\r\nBBBB,NULL,0,NULL\r\nCCCC,20\\,000,100,2008/10/26\r\n"));
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
			new BeanListHandler<SampleBean>(SampleBean.class)
				.excludes("name")
				.format("price", new DecimalFormat("0.00"))
				.format("date", new SimpleDateFormat("yyyy/MM/dd"))
				.filter(new SimpleCsvNamedValueFilter().ne("symbol", "gcu09", true))
				.filter(new SimpleBeanFilter().ne("date", df.parse("2008/11/06")))
				.save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,price,volume,date\r\nGCV09,1078.70,11,2008/10/06\r\n"));
	}

}