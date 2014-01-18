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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.bean.CsvBeanTemplate;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;
import com.orangesignal.csv.model.SampleBean;

/**
 * {@link CsvBeanWriter} クラスの単体テストです。
 *
 * @author 杉澤 浩二
 * @since 1.4.0
 */
public class CsvBeanWriterTest {

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

		final CsvBeanWriter<SampleBean> writer = CsvBeanWriter.newInstance(w, c);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterClassIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvWriter must not be null");

		final CsvWriter w = null;
		final Class<SampleBean> c = SampleBean.class;

		final CsvBeanWriter<SampleBean> writer = CsvBeanWriter.newInstance(w, c);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterClassIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Class must not be null");

		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final Class<SampleBean> c = null;

		final CsvBeanWriter<SampleBean> writer = CsvBeanWriter.newInstance(w, c);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterCsvBeanTemplate() throws IOException {
		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final CsvBeanTemplate<SampleBean> template = CsvBeanTemplate.newInstance(SampleBean.class);

		final CsvBeanWriter<SampleBean> writer = CsvBeanWriter.newInstance(w, template);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterCsvBeanTemplateIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvWriter must not be null");

		final CsvWriter w = null;
		final CsvBeanTemplate<SampleBean> template = CsvBeanTemplate.newInstance(SampleBean.class);

		final CsvBeanWriter<SampleBean> writer = CsvBeanWriter.newInstance(w, template);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterCsvBeanTemplateIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvBeanTemplate must not be null");

		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final CsvBeanTemplate<SampleBean> template = null;

		final CsvBeanWriter<SampleBean> writer = CsvBeanWriter.newInstance(w, template);
		writer.close();
	}

	// ------------------------------------------------------------------------
	// コンストラクタ

	@Test
	public void testConstructorCsvWriterClass() throws IOException {
		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final Class<SampleBean> c = SampleBean.class;

		final CsvBeanWriter<SampleBean> writer = new CsvBeanWriter<SampleBean>(w, c);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterClassIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvWriter must not be null");

		final CsvWriter w = null;
		final Class<SampleBean> c = SampleBean.class;

		final CsvBeanWriter<SampleBean> writer = new CsvBeanWriter<SampleBean>(w, c);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterClassIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Class must not be null");

		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final Class<SampleBean> c = null;

		final CsvBeanWriter<SampleBean> writer = new CsvBeanWriter<SampleBean>(w, c);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterCsvBeanTemplate() throws IOException {
		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final CsvBeanTemplate<SampleBean> template = CsvBeanTemplate.newInstance(SampleBean.class);

		final CsvBeanWriter<SampleBean> writer = new CsvBeanWriter<SampleBean>(w, template);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterCsvBeanTemplateIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvWriter must not be null");

		final CsvWriter w = null;
		final CsvBeanTemplate<SampleBean> template = CsvBeanTemplate.newInstance(SampleBean.class);

		final CsvBeanWriter<SampleBean> writer = new CsvBeanWriter<SampleBean>(w, template);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterCsvBeanTemplateIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvBeanTemplate must not be null");

		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final CsvBeanTemplate<SampleBean> template = null;

		final CsvBeanWriter<SampleBean> writer = new CsvBeanWriter<SampleBean>(w, template);
		writer.close();
	}

	// ------------------------------------------------------------------------
	// オーバーライド メソッド

	@Test
	public void testFlush() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvBeanWriter<SampleBean> writer = CsvBeanWriter.newInstance(new CsvWriter(sw, cfg), SampleBean.class);
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
		final CsvBeanWriter<SampleBean> writer = CsvBeanWriter.newInstance(
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
		final CsvBeanWriter<SampleBean> writer = CsvBeanWriter.newInstance(
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
		final CsvBeanWriter<SampleBean> writer = CsvBeanWriter.newInstance(
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
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\nAAAA,aaa,10000,10,NULL\r\nBBBB,bbb,NULL,0,NULL\r\n"));
	}

	@Test
	public void testWrite1() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvBeanWriter<SampleBean> writer = CsvBeanWriter.newInstance(
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
		final CsvBeanWriter<SampleBean> writer = CsvBeanWriter.newInstance(
				new CsvWriter(sw, cfg),
				CsvBeanTemplate.newInstance(SampleBean.class)
					.includes("name")
			);
		try {
			writer.write(new SampleBean("AAAA", "aaa", 10000, 10, null));
			writer.write(new SampleBean("BBBB", "bbb", null, 0, null));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("name\r\naaa\r\nbbb\r\n"));
	}

	@Test
	public void testWrite3() throws IOException {
		final List<SampleBean> list = new ArrayList<SampleBean>();
		list.add(new SampleBean("AAAA", "aaa", 10000, 10, null));
		list.add(new SampleBean("BBBB", "bbb", null, 0, null));

		final StringWriter sw = new StringWriter();
		final CsvBeanWriter<SampleBean> writer = CsvBeanWriter.newInstance(
				new CsvWriter(sw, cfg),
				CsvBeanTemplate.newInstance(SampleBean.class)
					.excludes("name", "date")
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
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final StringWriter sw = new StringWriter();
		final CsvBeanWriter<SampleBean> writer = CsvBeanWriter.newInstance(
				new CsvWriter(sw, cfg),
				CsvBeanTemplate.newInstance(SampleBean.class)
					.excludes("name")
					.format("price", new DecimalFormat("#,##0"))
					.format("date", new SimpleDateFormat("yyyy/MM/dd"))
			);
		try {
			writer.write(new SampleBean("AAAA", "aaa", 10000, 10, df.parse("2008/10/28")));
			writer.write(new SampleBean("BBBB", "bbb", null, 0, null));
			writer.write(new SampleBean("CCCC", "ccc", 20000, 100, df.parse("2008/10/26")));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,price,volume,date\r\nAAAA,10\\,000,10,2008/10/28\r\nBBBB,NULL,0,NULL\r\nCCCC,20\\,000,100,2008/10/26\r\n"));
	}

	@Test
	public void testFilter() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvBeanWriter<SampleBean> writer = CsvBeanWriter.newInstance(
				new CsvWriter(sw, cfg),
				CsvBeanTemplate.newInstance(SampleBean.class)
					.excludes("name")
					.format("price", new DecimalFormat("0.00"))
					.format("date", new SimpleDateFormat("yyyy/MM/dd"))
					.filter(new SimpleCsvNamedValueFilter()
							.ne("symbol", "gcu09", true)
							.ne("date", "2008/11/06")
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