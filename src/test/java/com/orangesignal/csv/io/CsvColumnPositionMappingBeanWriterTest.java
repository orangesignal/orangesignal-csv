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
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.bean.CsvColumnPositionMappingBeanTemplate;
import com.orangesignal.csv.filters.SimpleCsvValueFilter;
import com.orangesignal.csv.model.SampleBean;

/**
 * {@link CsvColumnPositionMappingBeanWriter} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public class CsvColumnPositionMappingBeanWriterTest {

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
		final Class<SampleBean> c = SampleBean.class;

		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = CsvColumnPositionMappingBeanWriter.newInstance(w, c);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterClassIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvWriter must not be null");

		final CsvWriter w = null;
		final Class<SampleBean> c = SampleBean.class;

		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = CsvColumnPositionMappingBeanWriter.newInstance(w, c);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterClassIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Class must not be null");

		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final Class<SampleBean> c = null;

		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = CsvColumnPositionMappingBeanWriter.newInstance(w, c);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterCsvBeanTemplate() throws IOException {
		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final CsvColumnPositionMappingBeanTemplate<SampleBean> template = CsvColumnPositionMappingBeanTemplate.newInstance(SampleBean.class);

		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = CsvColumnPositionMappingBeanWriter.newInstance(w, template);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterCsvBeanTemplateIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvWriter must not be null");

		final CsvWriter w = null;
		final CsvColumnPositionMappingBeanTemplate<SampleBean> template = CsvColumnPositionMappingBeanTemplate.newInstance(SampleBean.class);

		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = CsvColumnPositionMappingBeanWriter.newInstance(w, template);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterCsvBeanTemplateIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvColumnPositionMappingBeanTemplate must not be null");

		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final CsvColumnPositionMappingBeanTemplate<SampleBean> template = null;

		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = CsvColumnPositionMappingBeanWriter.newInstance(w, template);
		writer.close();
	}

	// ------------------------------------------------------------------------
	// コンストラクタ

	@Test
	public void testConstructorCsvWriterClass() throws IOException {
		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final Class<SampleBean> c = SampleBean.class;

		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = new CsvColumnPositionMappingBeanWriter<SampleBean>(w, c);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterClassIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvWriter must not be null");

		final CsvWriter w = null;
		final Class<SampleBean> c = SampleBean.class;

		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = new CsvColumnPositionMappingBeanWriter<SampleBean>(w, c);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterClassIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Class must not be null");

		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final Class<SampleBean> c = null;

		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = new CsvColumnPositionMappingBeanWriter<SampleBean>(w, c);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterCsvBeanTemplate() throws IOException {
		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final CsvColumnPositionMappingBeanTemplate<SampleBean> template = CsvColumnPositionMappingBeanTemplate.newInstance(SampleBean.class);

		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = new CsvColumnPositionMappingBeanWriter<SampleBean>(w, template);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterCsvBeanTemplateIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvWriter must not be null");

		final CsvWriter w = null;
		final CsvColumnPositionMappingBeanTemplate<SampleBean> template = CsvColumnPositionMappingBeanTemplate.newInstance(SampleBean.class);

		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = new CsvColumnPositionMappingBeanWriter<SampleBean>(w, template);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterCsvBeanTemplateIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvColumnPositionMappingBeanTemplate must not be null");

		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final CsvColumnPositionMappingBeanTemplate<SampleBean> template = null;

		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = new CsvColumnPositionMappingBeanWriter<SampleBean>(w, template);
		writer.close();
	}

	// ------------------------------------------------------------------------
	// オーバーライド メソッド

	@Test
	public void testFlush() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = CsvColumnPositionMappingBeanWriter.newInstance(
				new CsvWriter(sw, cfg),
				SampleBean.class
			);
		try {
			writer.writeHeader();
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\n"));

			writer.write(new SampleBean("AAAA", "aaa", 10000, 10, null));
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\nAAAA,aaa,10000,10,NULL\r\n"));

			writer.write(new SampleBean("BBBB", "bbb", null, 0, null));
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\nAAAA,aaa,10000,10,NULL\r\nBBBB,bbb,NULL,0,NULL\r\n"));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\nAAAA,aaa,10000,10,NULL\r\nBBBB,bbb,NULL,0,NULL\r\n"));
	}

	@Test
	public void testFlushIOException() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("CsvWriter closed");
		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = CsvColumnPositionMappingBeanWriter.newInstance(
				new CsvWriter(new StringWriter(), cfg),
				SampleBean.class
			);
		writer.close();
		// Act
		writer.flush();
	}

	@Test
	public void testCloseIOException() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("CsvWriter closed");
		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = CsvColumnPositionMappingBeanWriter.newInstance(
				new CsvWriter(new StringWriter(), cfg),
				SampleBean.class
			);
		writer.close();
		// Act
		writer.close();
	}

	// ------------------------------------------------------------------------
	// パブリック メソッド

	@Test
	public void testWriteHeader() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = CsvColumnPositionMappingBeanWriter.newInstance(
				new CsvWriter(sw, cfg),
				SampleBean.class
			);
		try {
			writer.writeHeader();
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\n"));

			writer.write(new SampleBean("AAAA", "aaa", 10000, 10, null));
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\nAAAA,aaa,10000,10,NULL\r\n"));

			writer.writeHeader();
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\nAAAA,aaa,10000,10,NULL\r\n"));

			writer.write(new SampleBean("BBBB", "bbb", null, 0, null));
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\nAAAA,aaa,10000,10,NULL\r\nBBBB,bbb,NULL,0,NULL\r\n"));

			writer.writeHeader();
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\nAAAA,aaa,10000,10,NULL\r\nBBBB,bbb,NULL,0,NULL\r\n"));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\nAAAA,aaa,10000,10,NULL\r\nBBBB,bbb,NULL,0,NULL\r\n"));
	}

	@Test
	public void testWrite1() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = CsvColumnPositionMappingBeanWriter.newInstance(
				new CsvWriter(sw, cfg),
				SampleBean.class
			);
		try {
			writer.write(new SampleBean("AAAA", "aaa", 10000, 10, null));
			writer.write(new SampleBean("BBBB", "bbb", null, 0, null));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\nAAAA,aaa,10000,10,NULL\r\nBBBB,bbb,NULL,0,NULL\r\n"));
	}

	@Test
	public void testWrite2() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = CsvColumnPositionMappingBeanWriter.newInstance(
				new CsvWriter(sw, cfg),
				CsvColumnPositionMappingBeanTemplate.newInstance(SampleBean.class)
					.column("name")
					.column("symbol")
					.column("price")
					.column("volume")
			);
		try {
			writer.write(new SampleBean("AAAA", "aaa", 10000, 10, null));
			writer.write(new SampleBean("BBBB", "bbb", null, 0, null));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("name,symbol,price,volume\r\naaa,AAAA,10000,10\r\nbbb,BBBB,NULL,0\r\n"));
	}

	@Test
	public void testWrite3() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = CsvColumnPositionMappingBeanWriter.newInstance(
				new CsvWriter(sw, cfg),
				CsvColumnPositionMappingBeanTemplate.newInstance(SampleBean.class)
					.column("symbol")
					.column("price")
					.column("volume")
			);
		try {
			writer.write(new SampleBean("AAAA", "aaa", 10000, 10, null));
			writer.write(new SampleBean("BBBB", "bbb", null, 0, null));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,price,volume\r\nAAAA,10000,10\r\nBBBB,NULL,0\r\n"));
	}

	@Test
	public void testWrite4() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = CsvColumnPositionMappingBeanWriter.newInstance(
				new CsvWriter(sw, cfg),
				CsvColumnPositionMappingBeanTemplate.newInstance(SampleBean.class)
					.column("symbol")
					.column("price", new DecimalFormat("#,##0"))
					.column("volume")
					.column("date", new SimpleDateFormat("yyyy/MM/dd"))
					.column("date", new SimpleDateFormat("HH:mm:ss"))
			);
		try {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			writer.write(new SampleBean("AAAA", "aaa", 10000, 10, df.parse("2008/10/28 10:24:00")));
			writer.write(new SampleBean("BBBB", "bbb", null, 0, null));
			writer.write(new SampleBean("CCCC", "ccc", 20000, 100, df.parse("2008/10/26 14:20:10")));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,price,volume,date,date\r\nAAAA,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,NULL,0,NULL,NULL\r\nCCCC,20\\,000,100,2008/10/26,14:20:10\r\n"));
	}

	@Test
	public void testFilter() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvColumnPositionMappingBeanWriter<SampleBean> writer = CsvColumnPositionMappingBeanWriter.newInstance(
				new CsvWriter(sw, cfg),
				CsvColumnPositionMappingBeanTemplate.newInstance(SampleBean.class)
					.column("symbol")
					.column("price", new DecimalFormat("0.00"))
					.column("volume")
					.column("date", new SimpleDateFormat("yyyy/MM/dd"))
					.filter(new SimpleCsvValueFilter()
							.ne(0, "gcu09", true)
							.ne(3, "2008/11/06")
						)
			);
		try {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			writer.write(new SampleBean("GCU09", "COMEX 金 2009年09月限", 1068.70, 10, df.parse("2008/09/06")));
			writer.write(new SampleBean("GCV09", "COMEX 金 2009年10月限", 1078.70, 11, df.parse("2008/10/06")));
			writer.write(new SampleBean("GCX09", "COMEX 金 2009年11月限", 1088.70, 12, df.parse("2008/11/06")));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,price,volume,date\r\nGCV09,1078.70,11,2008/10/06\r\n"));
	}

}