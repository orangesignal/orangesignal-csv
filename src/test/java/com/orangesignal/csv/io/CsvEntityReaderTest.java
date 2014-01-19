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
import java.text.SimpleDateFormat;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.bean.CsvEntityTemplate;
import com.orangesignal.csv.entity.Price;
import com.orangesignal.csv.entity.Price2;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;

/**
 * {@link CsvEntityReader} クラスの単体テストです。
 *
 * @author 杉澤 浩二
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
			assertThat(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(o1.date), is("2009/10/28 10:24:00"));

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