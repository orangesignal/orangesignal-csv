/*
 * Copyright 2013 the original author or authors.
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.bean.CsvBeanTemplate;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;
import com.orangesignal.csv.model.SampleBean;

/**
 * {@link CsvBeanReader} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public class CsvBeanReaderTest {

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
		final CsvBeanReader<SampleBean> reader = CsvBeanReader.newInstance(
				new CsvReader(new StringReader("")),
				SampleBean.class
			);
		reader.close();
	}

	@Test
	public void testNewInstanceCsvReaderCsvBeanTemplate() throws IOException {
		final CsvBeanReader<SampleBean> reader = CsvBeanReader.newInstance(
				new CsvReader(new StringReader("")),
				CsvBeanTemplate.newInstance(SampleBean.class)
			);
		reader.close();
	}

	// ------------------------------------------------------------------------
	// コンストラクタ

	@Test
	public void testConstructorCsvReaderClass() throws IOException {
		final CsvBeanReader<SampleBean> reader = new CsvBeanReader<SampleBean>(
				new CsvReader(new StringReader("")),
				SampleBean.class
			);
		reader.close();
	}

	@Test
	public void testConstructorCsvReaderClassIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvReader must not be null");
		final CsvBeanReader<SampleBean> reader = new CsvBeanReader<SampleBean>(
				null,
				SampleBean.class
			);
		reader.close();
	}

	@Test
	public void testConstructorCsvReaderClassIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Class must not be null");
		final Class<SampleBean> type = null;
		final CsvBeanReader<SampleBean> reader = new CsvBeanReader<SampleBean>(
				new CsvReader(new StringReader("")),
				type
			);
		reader.close();
	}

	@Test
	public void testConstructorCsvReaderCsvBeanTemplate() throws IOException {
		final CsvBeanReader<SampleBean> reader = new CsvBeanReader<SampleBean>(
				new CsvReader(new StringReader("")),
				CsvBeanTemplate.newInstance(SampleBean.class)
			);
		reader.close();
	}

	@Test
	public void testConstructorCsvReaderCsvBeanTemplateIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvReader must not be null");
		final CsvBeanReader<SampleBean> reader = new CsvBeanReader<SampleBean>(
				null,
				CsvBeanTemplate.newInstance(SampleBean.class)
			);
		reader.close();
	}

	@Test
	public void testConstructorCsvReaderClassCsvBeanConfigIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvBeanTemplate must not be null");
		final CsvBeanTemplate<SampleBean> template = null;
		final CsvBeanReader<SampleBean> reader = new CsvBeanReader<SampleBean>(
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
		final CsvBeanReader<SampleBean> reader = CsvBeanReader.newInstance(new CsvReader(new StringReader("")), SampleBean.class);
		reader.close();
		// Act
		reader.close();
	}

	// ------------------------------------------------------------------------
	// パブリック メソッド

	@Test
	public void testLoad1() throws IOException {
		final CsvBeanReader<SampleBean> reader = CsvBeanReader.newInstance(
				new CsvReader(new StringReader("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg),
				SampleBean.class
			);
		try {
			final SampleBean o1 = reader.read();
			assertThat(o1.symbol, is("AAAA"));
			assertThat(o1.name, is("aaa"));
			assertThat(o1.price.longValue(), is(10000L));
			assertThat(o1.volume.longValue(), is(10L));

			final SampleBean o2 = reader.read();
			assertThat(o2.symbol, is("BBBB"));
			assertThat(o2.name, is("bbb"));
			assertNull(o2.price);
			assertThat(o2.volume.longValue(), is(0L));

			final SampleBean last = reader.read();
			assertNull(last);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoad2() throws IOException {
		final CsvBeanReader<SampleBean> reader = CsvBeanReader.newInstance(
				new CsvReader(new StringReader("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg),
				CsvBeanTemplate.newInstance(SampleBean.class).includes("name")
			);
		try {
			final SampleBean o1 = reader.read();
			assertNull(o1.symbol);
			assertThat(o1.name, is("aaa"));
			assertNull(o1.price);
			assertNull(o1.volume);
			final SampleBean o2 = reader.read();
			assertNull(o2.symbol);
			assertThat(o2.name, is("bbb"));
			assertNull(o2.price);
			assertNull(o2.volume);
			final SampleBean o3 = reader.read();
			assertNull(o3);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoad3() throws IOException {
		final CsvBeanReader<SampleBean> reader = CsvBeanReader.newInstance(
				new CsvReader(new StringReader("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg),
				CsvBeanTemplate.newInstance(SampleBean.class).excludes("name")
			);
		try {
			final SampleBean o1 = reader.read();
			assertThat(o1.symbol, is("AAAA"));
			assertNull(o1.name);
			assertThat(o1.price.longValue(), is(10000L));
			assertThat(o1.volume.longValue(), is(10L));
			final SampleBean o2 = reader.read();
			assertThat(o2.symbol, is("BBBB"));
			assertNull(o2.name);
			assertNull(o2.price);
			assertThat(o2.volume.longValue(), is(0L));
			final SampleBean o3 = reader.read();
			assertNull(o3);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoad4() throws Exception {
		final CsvBeanReader<SampleBean> reader = CsvBeanReader.newInstance(
				new CsvReader(new StringReader("symbol,name,price,volume,date\r\nAAAA,aaa,10\\,000,10,2008/10/28\r\nBBBB,bbb,NULL,0,NULL"), cfg),
				CsvBeanTemplate.newInstance(SampleBean.class)
					.excludes("name")
					.format("price", new DecimalFormat("#,##0"))
					.format("date", new SimpleDateFormat("yyyy/MM/dd"))
			);
		try {
			final SampleBean o1 = reader.read();
			assertThat(o1.symbol, is("AAAA"));
			assertNull(o1.name);
			assertThat(o1.price.longValue(), is(10000L));
			assertThat(o1.volume.longValue(), is(10L));
			assertThat(new SimpleDateFormat("yyyy/MM/dd").format(o1.date), is("2008/10/28"));
			final SampleBean o2 = reader.read();
			assertThat(o2.symbol, is("BBBB"));
			assertNull(o2.name);
			assertNull(o2.price);
			assertThat(o2.volume.longValue(), is(0L));
			assertNull(o2.date);
			final SampleBean o3 = reader.read();
			assertNull(o3);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoadFilter() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");

		final CsvBeanReader<SampleBean> reader = CsvBeanReader.newInstance(
				new CsvReader(new StringReader(
						"symbol,name,price,volume,date\r\n" +
						"GCQ09,COMEX 金 2009年08月限,1058.70,10,2008/08/06\r\n" +
						"GCU09,COMEX 金 2009年09月限,1068.70,10,2008/09/06\r\n" +
						"GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\n" +
						"GCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n" +
						"GCZ09,COMEX 金 2009年12月限,1098.70,13,2008/12/06\r\n"
					), cfg),
					CsvBeanTemplate.newInstance(SampleBean.class)
						.format("date", new SimpleDateFormat("yyyy/MM/dd"))
						.filter(
						new SimpleCsvNamedValueFilter()
							.ne("symbol", "gcu09", true)
							.ne("date", "2008/11/06")
					)
			);
		try {
			final SampleBean o1 = reader.read();
			assertThat(o1.symbol, is("GCQ09"));
			assertThat(o1.name, is("COMEX 金 2009年08月限"));
			assertThat(o1.price.doubleValue(), is(1058.70D));
			assertThat(o1.volume.longValue(), is(10L));
			assertThat(o1.date, is(df.parse("2008/08/06")));
			final SampleBean o2 = reader.read();
			assertThat(o2.symbol, is("GCV09"));
			assertThat(o2.name, is("COMEX 金 2009年10月限"));
			assertThat(o2.price.doubleValue(), is(1078.70D));
			assertThat(o2.volume.longValue(), is(11L));
			assertThat(o2.date, is(df.parse("2008/10/06")));
			final SampleBean o3 = reader.read();
			assertThat(o3.symbol, is("GCZ09"));
			assertThat(o3.name, is("COMEX 金 2009年12月限"));
			assertThat(o3.price.doubleValue(), is(1098.70D));
			assertThat(o3.volume.longValue(), is(13L));
			assertThat(o3.date, is(df.parse("2008/12/06")));
			final SampleBean last = reader.read();
			assertNull(last);
		} finally {
			reader.close();
		}
	}

}