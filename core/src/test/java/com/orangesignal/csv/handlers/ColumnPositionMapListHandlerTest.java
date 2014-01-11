/*
 * Copyright (c) 2009 OrangeSignal.com All rights reserved.
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

package com.orangesignal.csv.handlers;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.filters.SimpleCsvValueFilter;
import com.orangesignal.csv.handlers.ColumnPositionMapListHandler;

/**
 * {@link ColumnPositionMapListHandler} クラスの単体テストです。
 *
 * @author 杉澤 浩二
 */
public class ColumnPositionMapListHandlerTest {

	private CsvConfig cfg;

	@Before
	public void setUp() throws Exception {
		cfg = new CsvConfig(',');
		cfg.setNullString("NULL");
		cfg.setIgnoreTrailingWhitespaces(true);
		cfg.setIgnoreLeadingWhitespaces(true);
		cfg.setIgnoreEmptyLines(true);
		cfg.setLineSeparator(Constants.CRLF);
	}

	@Test
	public void testLoad() throws IOException {
		cfg.setSkipLines(1);	// ヘッダは不要なので読飛ばす指定をする
		final CsvReader reader = new CsvReader(new StringReader("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg);
		try {
			final List<Map<Integer, String>> list = new ColumnPositionMapListHandler().load(reader);
			assertThat(list.size(), is(2));
			final Map<Integer, String> m1 = list.get(0);
			assertThat(m1.get(0), is("AAAA"));
			assertThat(m1.get(1), is("aaa"));
			assertThat(m1.get(2), is("10000"));
			assertThat(m1.get(3), is("10"));
			final Map<Integer, String> m2 = list.get(1);
			assertThat(m2.get(0), is("BBBB"));
			assertThat(m2.get(1), is("bbb"));
			assertTrue(m2.containsKey(2));
			assertNull(m2.get(2));
			assertThat(m2.get(3), is("0"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoadOffsetLimit() throws IOException {
		cfg.setSkipLines(1);	// ヘッダは不要なので読飛ばす指定をする
		final CsvReader reader = new CsvReader(new StringReader("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg);
		try {
			final List<Map<Integer, String>> list = new ColumnPositionMapListHandler().offset(1).limit(1).load(reader);
			assertThat(list.size(), is(1));
			final Map<Integer, String> m2 = list.get(0);
			assertThat(m2.get(0), is("BBBB"));
			assertThat(m2.get(1), is("bbb"));
			assertTrue(m2.containsKey(2));
			assertNull(m2.get(2));
			assertThat(m2.get(3), is("0"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoadFilter() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader(
//				"symbol,name,price,volume,date\r\n" +
				"GCU09,COMEX 金 2009年09月限,1068.70,10,2008/09/06\r\n" +
				"GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\n" +
				"GCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n"
			), cfg);
		try {
			final List<Map<Integer, String>> list = new ColumnPositionMapListHandler()
				.filter(new SimpleCsvValueFilter().ne(0, "gcu09", true))
				.offset(1).limit(1)
				.load(reader);

			assertThat(list.size(), is(1));
			final Map<Integer, String> m2 = list.get(0);
			assertThat(m2.get(0), is("GCX09"));
			assertThat(m2.get(1), is("COMEX 金 2009年11月限"));
			assertThat(m2.get(2), is("1088.70"));
			assertThat(m2.get(3), is("12"));
			assertThat(m2.get(4), is("2008/11/06"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testSave() throws IOException {
		final List<Map<Integer, String>> list = new ArrayList<Map<Integer, String>>(3);
		final Map<Integer, String> m0 = new HashMap<Integer, String>(4);
		m0.put(0, "symbol");
		m0.put(1, "name");
		m0.put(2, "price");
		m0.put(3, "volume");
		list.add(m0);
		final Map<Integer, String> m1 = new HashMap<Integer, String>(4);
		m1.put(0, "AAAA");
		m1.put(1, "aaa");
		m1.put(2, "10000");
		m1.put(3, "10");
		list.add(m1);
		final Map<Integer, String> m2 = new HashMap<Integer, String>(4);
		m2.put(0, "BBBB");
		m2.put(1, "bbb");
		m2.put(2, null);
		m2.put(3, "0");
		list.add(m2);

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			new ColumnPositionMapListHandler().save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	public void testSaveFilter() throws Exception {
		final List<Map<Integer, String>> list = new ArrayList<Map<Integer, String>>(3);
		final Map<Integer, String> m0 = new HashMap<Integer, String>(5);
		m0.put(0, "GCU09");
		m0.put(1, "COMEX 金 2009年09月限");
		m0.put(2, "1068.70");
		m0.put(3, "10");
		m0.put(4, "2008/09/06");
		list.add(m0);
		final Map<Integer, String> m1 = new HashMap<Integer, String>(5);
		m1.put(0, "GCV09");
		m1.put(1, "COMEX 金 2009年10月限");
		m1.put(2, "1078.70");
		m1.put(3, "11");
		m1.put(4, "2008/10/06");
		list.add(m1);
		final Map<Integer, String> m2 = new HashMap<Integer, String>(5);
		m2.put(0, "GCX09");
		m2.put(1, "COMEX 金 2009年11月限");
		m2.put(2, "1088.70");
		m2.put(3, "12");
		m2.put(4, "2008/11/06");
		list.add(m2);

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			new ColumnPositionMapListHandler()
				.filter(new SimpleCsvValueFilter().ne(0, "gcu09", true))
				.save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\nGCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n"));
	}

}
