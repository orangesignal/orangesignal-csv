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
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.bean.CsvColumnNameMappingBeanTemplate;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;
import com.orangesignal.csv.model.SampleBean;

/**
 * {@link CsvColumnNameMappingBeanWriter} クラスの単体テストです。
 *
 * @author 杉澤 浩二
 * @since 1.4.0
 */
public class CsvColumnNameMappingBeanWriterTest {

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

		final CsvColumnNameMappingBeanWriter<SampleBean> writer = CsvColumnNameMappingBeanWriter.newInstance(w, c);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterClassIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvWriter must not be null");

		final CsvWriter w = null;
		final Class<SampleBean> c = SampleBean.class;

		final CsvColumnNameMappingBeanWriter<SampleBean> writer = CsvColumnNameMappingBeanWriter.newInstance(w, c);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterClassIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Class must not be null");

		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final Class<SampleBean> c = null;

		final CsvColumnNameMappingBeanWriter<SampleBean> writer = CsvColumnNameMappingBeanWriter.newInstance(w, c);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterCsvColumnNameMappingBeanTemplate() throws IOException {
		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final CsvColumnNameMappingBeanTemplate<SampleBean> template = CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class);

		final CsvColumnNameMappingBeanWriter<SampleBean> writer = CsvColumnNameMappingBeanWriter.newInstance(w, template);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterCsvColumnNameMappingBeanTemplateIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvWriter must not be null");

		final CsvWriter w = null;
		final CsvColumnNameMappingBeanTemplate<SampleBean> template = CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class);

		final CsvColumnNameMappingBeanWriter<SampleBean> writer = CsvColumnNameMappingBeanWriter.newInstance(w, template);
		writer.close();
	}

	@Test
	public void testNewInstanceCsvWriterCsvColumnNameMappingBeanTemplateIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvColumnNameMappingBeanTemplate must not be null");

		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final CsvColumnNameMappingBeanTemplate<SampleBean> template = null;

		final CsvColumnNameMappingBeanWriter<SampleBean> writer = CsvColumnNameMappingBeanWriter.newInstance(w, template);
		writer.close();
	}

	// ------------------------------------------------------------------------
	// コンストラクタ

	@Test
	public void testConstructorCsvWriterClass() throws IOException {
		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final Class<SampleBean> c = SampleBean.class;

		final CsvColumnNameMappingBeanWriter<SampleBean> writer = new CsvColumnNameMappingBeanWriter<SampleBean>(w, c);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterClassIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvWriter must not be null");

		final CsvWriter w = null;
		final Class<SampleBean> c = SampleBean.class;

		final CsvColumnNameMappingBeanWriter<SampleBean> writer = new CsvColumnNameMappingBeanWriter<SampleBean>(w, c);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterClassIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Class must not be null");

		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final Class<SampleBean> c = null;

		final CsvColumnNameMappingBeanWriter<SampleBean> writer = new CsvColumnNameMappingBeanWriter<SampleBean>(w, c);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterCsvColumnNameMappingBeanTemplate() throws IOException {
		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final CsvColumnNameMappingBeanTemplate<SampleBean> template = CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class);

		final CsvColumnNameMappingBeanWriter<SampleBean> writer = new CsvColumnNameMappingBeanWriter<SampleBean>(w, template);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterCsvColumnNameMappingBeanTemplateIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvWriter must not be null");

		final CsvWriter w = null;
		final CsvColumnNameMappingBeanTemplate<SampleBean> template = CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class);

		final CsvColumnNameMappingBeanWriter<SampleBean> writer = new CsvColumnNameMappingBeanWriter<SampleBean>(w, template);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterCsvColumnNameMappingBeanTemplateIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvColumnNameMappingBeanTemplate must not be null");

		final CsvWriter w = new CsvWriter(new StringWriter(), cfg);
		final CsvColumnNameMappingBeanTemplate<SampleBean> template = null;

		final CsvColumnNameMappingBeanWriter<SampleBean> writer = new CsvColumnNameMappingBeanWriter<SampleBean>(w, template);
		writer.close();
	}

	// ------------------------------------------------------------------------
	// オーバーライド メソッド

	@Test
	public void testFlush() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnNameMappingBeanWriter<SampleBean> writer = CsvColumnNameMappingBeanWriter.newInstance(new CsvWriter(sw, cfg), SampleBean.class);
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
		final CsvColumnNameMappingBeanWriter<SampleBean> writer = CsvColumnNameMappingBeanWriter.newInstance(
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
		final CsvColumnNameMappingBeanWriter<SampleBean> writer = CsvColumnNameMappingBeanWriter.newInstance(
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
		final CsvColumnNameMappingBeanWriter<SampleBean> writer = CsvColumnNameMappingBeanWriter.newInstance(new CsvWriter(sw, cfg), SampleBean.class);
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
	public void testWrite1() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvColumnNameMappingBeanWriter<SampleBean> writer = CsvColumnNameMappingBeanWriter.newInstance(
				new CsvWriter(sw, cfg),
				SampleBean.class
			);
		try {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			writer.write(new SampleBean("AAAA", "aaa", 10000, 10, df.parse("2009/10/28")));
			writer.write(new SampleBean("BBBB", "bbb", null, 0, null));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\nAAAA,aaa,10000,10,Wed Oct 28 00:00:00 JST 2009\r\nBBBB,bbb,NULL,0,NULL\r\n"));
	}

	@Test
	public void testWrite2() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnNameMappingBeanWriter<SampleBean> writer = CsvColumnNameMappingBeanWriter.newInstance(
				new CsvWriter(sw, cfg),
				CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class)
					.column("シンボル", "symbol")
					.column("名称",     "name")
					.column("価格",     "price")
					.column("出来高",   "volume")
			);
		try {
			writer.write(new SampleBean("AAAA", "aaa", 10000, 10, null));
			writer.write(new SampleBean("BBBB", "bbb", null, 0, null));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	public void testWrite3() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnNameMappingBeanWriter<SampleBean> writer = CsvColumnNameMappingBeanWriter.newInstance(
				new CsvWriter(sw, cfg),
				CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class)
					.column("シンボル", "symbol")
					.column("価格",     "price")
					.column("出来高",   "volume")
			);
		try {
			writer.write(new SampleBean("AAAA", "aaa", 10000, 10, null));
			writer.write(new SampleBean("BBBB", "bbb", null, 0, null));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,価格,出来高\r\nAAAA,10000,10\r\nBBBB,NULL,0\r\n"));
	}

	@Test
	public void testWrite4() throws IOException {
		final Map<String, String> columnMapping = new LinkedHashMap<String, String>();
		columnMapping.put("シンボル", "symbol");
		columnMapping.put("名称",     "name");
		columnMapping.put("価格",     "price");
		columnMapping.put("出来高",   "volume");

		final StringWriter sw = new StringWriter();
		final CsvColumnNameMappingBeanWriter<SampleBean> writer = CsvColumnNameMappingBeanWriter.newInstance(
				new CsvWriter(sw, cfg),
				CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class)
					.columnMapping(columnMapping)
			);
		try {
			writer.write(new SampleBean("AAAA", "aaa", 10000, 10, null));
			writer.write(new SampleBean("BBBB", "bbb", null, 0, null));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	public void testWrite5() throws IOException {
		final Map<String, String> columnMapping = new LinkedHashMap<String, String>();
		columnMapping.put("シンボル", "symbol");
		columnMapping.put("価格",     "price");
		columnMapping.put("出来高",   "volume");

		final StringWriter sw = new StringWriter();
		final CsvColumnNameMappingBeanWriter<SampleBean> writer = CsvColumnNameMappingBeanWriter.newInstance(
				new CsvWriter(sw, cfg),
				CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class)
					.columnMapping(columnMapping)
			);
		try {
			writer.write(new SampleBean("AAAA", "aaa", 10000, 10, null));
			writer.write(new SampleBean("BBBB", "bbb", null, 0, null));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,価格,出来高\r\nAAAA,10000,10\r\nBBBB,NULL,0\r\n"));
	}

	@Test
	public void testWrite6() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvColumnNameMappingBeanWriter<SampleBean> writer = CsvColumnNameMappingBeanWriter.newInstance(
				new CsvWriter(sw, cfg),
				CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class)
					.column("シンボル", "symbol")
					.column("名称",     "name")
					.column("価格",     "price", new DecimalFormat("#,##0"))
					.column("出来高",   "volume")
					.column("日付",     "date", new SimpleDateFormat("yyyy/MM/dd"))
					.column("時刻",     "date", new SimpleDateFormat("HH:mm:ss"))
			);
		try {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			writer.write(new SampleBean("AAAA", "aaa", 10000, 10, df.parse("2008/10/28 10:24:00")));
			writer.write(new SampleBean("BBBB", "bbb", null, 0, null));
			writer.write(new SampleBean("CCCC", "ccc", 20000, 100, df.parse("2008/10/26 14:20:10")));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2008/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL\r\nCCCC,ccc,20\\,000,100,2008/10/26,14:20:10\r\n"));
	}

	@Test
	public void testFilter() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvColumnNameMappingBeanWriter<SampleBean> writer = CsvColumnNameMappingBeanWriter.newInstance(
				new CsvWriter(sw, cfg),
				CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class)
					.column("シンボル", "symbol")
					.column("名称",     "name")
					.column("価格",     "price", new DecimalFormat("0.00"))
					.column("出来高",   "volume")
					.column("日付",     "date", new SimpleDateFormat("yyyy/MM/dd"))
					.filter(new SimpleCsvNamedValueFilter()
							.ne("シンボル", "gcu09", true)
							.ne("日付", "2008/11/06")
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
		assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付\r\nGCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\n"));
	}

}