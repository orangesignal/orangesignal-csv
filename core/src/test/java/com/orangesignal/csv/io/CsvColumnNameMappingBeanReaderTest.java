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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.bean.CsvColumnNameMappingBeanTemplate;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;
import com.orangesignal.csv.model.SampleBean;

/**
 * {@link CsvColumnNameMappingBeanReader} クラスの単体テストです。
 *
 * @author 杉澤 浩二
 * @since 1.4.0
 */
public class CsvColumnNameMappingBeanReaderTest {

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
		final CsvColumnNameMappingBeanReader<SampleBean> reader = CsvColumnNameMappingBeanReader.newInstance(
				new CsvReader(new StringReader("")),
				SampleBean.class
			);
		reader.close();
	}

	@Test
	public void testNewInstanceCsvReaderCsvColumnNameMappingBeanTemplate() throws IOException {
		final CsvColumnNameMappingBeanReader<SampleBean> reader = CsvColumnNameMappingBeanReader.newInstance(
				new CsvReader(new StringReader("")),
				CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class)
			);
		reader.close();
	}

	// ------------------------------------------------------------------------
	// コンストラクタ

	@Test
	public void testConstructorCsvReaderClass() throws IOException {
		final CsvColumnNameMappingBeanReader<SampleBean> reader = new CsvColumnNameMappingBeanReader<SampleBean>(
				new CsvReader(new StringReader("")),
				SampleBean.class
			);
		reader.close();
	}

	@Test
	public void testConstructorCsvReaderClassIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvReader must not be null");
		final CsvColumnNameMappingBeanReader<SampleBean> reader = new CsvColumnNameMappingBeanReader<SampleBean>(
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
		final CsvColumnNameMappingBeanReader<SampleBean> reader = new CsvColumnNameMappingBeanReader<SampleBean>(
				new CsvReader(new StringReader("")),
				type
			);
		reader.close();
	}

	@Test
	public void testConstructorCsvReaderCsvBeanTemplate() throws IOException {
		final CsvColumnNameMappingBeanReader<SampleBean> reader = new CsvColumnNameMappingBeanReader<SampleBean>(
				new CsvReader(new StringReader("")),
				CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class)
			);
		reader.close();
	}

	@Test
	public void testConstructorCsvReaderCsvBeanTemplateIllegalArgumentException1() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvReader must not be null");
		final CsvColumnNameMappingBeanReader<SampleBean> reader = new CsvColumnNameMappingBeanReader<SampleBean>(
				null,
				CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class)
			);
		reader.close();
	}

	@Test
	public void testConstructorCsvReaderClassCsvBeanConfigIllegalArgumentException2() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvColumnNameMappingBeanTemplate must not be null");
		final CsvColumnNameMappingBeanTemplate<SampleBean> template = null;
		final CsvColumnNameMappingBeanReader<SampleBean> reader = new CsvColumnNameMappingBeanReader<SampleBean>(
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
		final CsvColumnNameMappingBeanReader<SampleBean> reader = CsvColumnNameMappingBeanReader.newInstance(new CsvReader(new StringReader("")), SampleBean.class);
		reader.close();
		// Act
		reader.close();
	}

	// ------------------------------------------------------------------------
	// パブリック メソッド

	@Test
	public void testRead1() throws IOException {
		final CsvColumnNameMappingBeanReader<SampleBean> reader = CsvColumnNameMappingBeanReader.newInstance(
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
	public void testRead2() throws IOException {
		final CsvColumnNameMappingBeanReader<SampleBean> reader = CsvColumnNameMappingBeanReader.newInstance(
				new CsvReader(new StringReader("シンボル,名称,価格,出来高\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg),
				CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class)
					.column("シンボル", "symbol")
					.column("名称",     "name")
					.column("価格",     "price")
					.column("出来高",   "volume")
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
	public void testRead3() throws IOException {
		final CsvColumnNameMappingBeanReader<SampleBean> reader = CsvColumnNameMappingBeanReader.newInstance(
				new CsvReader(new StringReader("シンボル,名称,価格,出来高\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg),
				CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class)
					.column("シンボル", "symbol")
					.column("価格",     "price")
					.column("出来高",   "volume")
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

			final SampleBean last = reader.read();
			assertNull(last);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testRead4() throws IOException {
		final Map<String, String> columnMapping = new HashMap<String, String>();
		columnMapping.put("シンボル", "symbol");
		columnMapping.put("名称",     "name");
		columnMapping.put("価格",     "price");
		columnMapping.put("出来高",   "volume");

		final CsvColumnNameMappingBeanReader<SampleBean> reader = CsvColumnNameMappingBeanReader.newInstance(
				new CsvReader(new StringReader("シンボル,名称,価格,出来高\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg),
				CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class).columnMapping(columnMapping)
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
	public void testRead5() throws IOException {
		final Map<String, String> columnMapping = new HashMap<String, String>();
		columnMapping.put("シンボル", "symbol");
		columnMapping.put("価格",     "price");
		columnMapping.put("出来高",   "volume");

		final CsvColumnNameMappingBeanReader<SampleBean> reader = CsvColumnNameMappingBeanReader.newInstance(
				new CsvReader(new StringReader("シンボル,名称,価格,出来高\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg),
				CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class).columnMapping(columnMapping)
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

			final SampleBean last = reader.read();
			assertNull(last);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testRead6() throws IOException {
		final CsvColumnNameMappingBeanReader<SampleBean> reader = CsvColumnNameMappingBeanReader.newInstance(
				new CsvReader(new StringReader("シンボル,名称,価格,出来高,日付,時刻\r\nAAAA,aaa,10\\,000,10,2009/10/28,10:24:00\r\nBBBB,bbb,NULL,0,NULL,NULL"), cfg),
				CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class)
					.column("シンボル", "symbol")
					.column("名称",     "name")
					.column("価格",     "price", new DecimalFormat("#,##0"))
					.column("出来高",   "volume")
					.column("日付",     "date", new SimpleDateFormat("yyyy/MM/dd"))
					.column("時刻",     "date", new SimpleDateFormat("HH:mm:ss"))
			);
		try {
			final SampleBean o1 = reader.read();
			assertThat(o1.symbol, is("AAAA"));
			assertThat(o1.name, is("aaa"));
			assertThat(o1.price.longValue(), is(10000L));
			assertThat(o1.volume.longValue(), is(10L));
			assertThat(new SimpleDateFormat("yyyy/MM/dd").format(o1.date), is("2009/10/28"));

			final SampleBean o2 = reader.read();
			assertThat(o2.symbol, is("BBBB"));
			assertThat(o2.name, is("bbb"));
			assertNull(o2.price);
			assertThat(o2.volume.longValue(), is(0L));
			assertNull(o2.date);

			final SampleBean last = reader.read();
			assertNull(last);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadFilter() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");

		final CsvColumnNameMappingBeanReader<SampleBean> reader = CsvColumnNameMappingBeanReader.newInstance(
				new CsvReader(new StringReader(
					"シンボル,名称,価格,出来高,日付\r\n" +
					"GCQ09,COMEX 金 2009年08月限,1058.70,10,2008/08/06\r\n" +
					"GCU09,COMEX 金 2009年09月限,1068.70,10,2008/09/06\r\n" +
					"GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\n" +
					"GCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n" +
					"GCZ09,COMEX 金 2009年12月限,1098.70,13,2008/12/06\r\n"
				), cfg),
				CsvColumnNameMappingBeanTemplate.newInstance(SampleBean.class)
					.column("シンボル", "symbol")
					.column("名称", "name")
					.column("価格", "price")
					.column("出来高", "volume")
					.column("日付", "date", new SimpleDateFormat("yyyy/MM/dd"))
					.filter(new SimpleCsvNamedValueFilter().ne(0, "gcu09", true))
			);
		try {
			final SampleBean o0 = reader.read();
			assertThat(o0.symbol, is("GCQ09"));
			assertThat(o0.name, is("COMEX 金 2009年08月限"));
			assertThat(o0.price.doubleValue(), is(1058.70D));
			assertThat(o0.volume.longValue(), is(10L));
			assertThat(o0.date, is(df.parse("2008/08/06")));

			final SampleBean o1 = reader.read();
			assertThat(o1.symbol, is("GCV09"));
			assertThat(o1.name, is("COMEX 金 2009年10月限"));
			assertThat(o1.price.doubleValue(), is(1078.70D));
			assertThat(o1.volume.longValue(), is(11L));
			assertThat(o1.date, is(df.parse("2008/10/06")));

			final SampleBean o2 = reader.read();
			assertThat(o2.symbol, is("GCX09"));
			assertThat(o2.name, is("COMEX 金 2009年11月限"));
			assertThat(o2.price.doubleValue(), is(1088.70D));
			assertThat(o2.volume.longValue(), is(12L));
			assertThat(o2.date, is(df.parse("2008/11/06")));

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