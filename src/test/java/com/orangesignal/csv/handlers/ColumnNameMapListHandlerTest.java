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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;

/**
 * {@link ColumnNameMapListHandler} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 */
public class ColumnNameMapListHandlerTest {

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
	public void testLoad1() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg);
		try {
			final List<Map<String, String>> list = new ColumnNameMapListHandler().load(reader);
			assertThat(list.size(), is(2));
			final Map<String, String> m1 = list.get(0);
			assertThat(m1.get("symbol"), is("AAAA"));
			assertThat(m1.get("name"), is("aaa"));
			assertThat(m1.get("price"), is("10000"));
			assertThat(m1.get("volume"), is("10"));
			final Map<String, String> m2 = list.get(1);
			assertThat(m2.get("symbol"), is("BBBB"));
			assertThat(m2.get("name"), is("bbb"));
			assertTrue(m2.containsKey("price"));
			assertNull(m2.get("price"));
			assertThat(m2.get("volume"), is("0"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoadOffsetLimit() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg);
		try {
			final List<Map<String, String>> list = new ColumnNameMapListHandler().offset(1).limit(1).load(reader);

			assertThat(list.size(), is(1));
			final Map<String, String> m2 = list.get(0);
			assertThat(m2.get("symbol"), is("BBBB"));
			assertThat(m2.get("name"), is("bbb"));
			assertTrue(m2.containsKey("price"));
			assertNull(m2.get("price"));
			assertThat(m2.get("volume"), is("0"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoad2() throws IOException {
		cfg.setSkipLines(1);
		final CsvReader reader = new CsvReader(new StringReader("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg);
		try {
			final List<Map<String, String>> list = new ColumnNameMapListHandler()
				.addColumn("symbol")
				.addColumn("name")
				.addColumn("price")
				.addColumn("volume")
				.load(reader);

			assertThat(list.size(), is(2));
			final Map<String, String> m1 = list.get(0);
			assertThat(m1.get("symbol"), is("AAAA"));
			assertThat(m1.get("name"), is("aaa"));
			assertThat(m1.get("price"), is("10000"));
			assertThat(m1.get("volume"), is("10"));
			final Map<String, String> m2 = list.get(1);
			assertThat(m2.get("symbol"), is("BBBB"));
			assertThat(m2.get("name"), is("bbb"));
			assertTrue(m2.containsKey("price"));
			assertNull(m2.get("price"));
			assertThat(m2.get("volume"), is("0"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoadFilter() throws IOException {
		final CsvReader reader = new CsvReader(new StringReader(
				"symbol,name,price,volume,date\r\n" +
				"GCU09,COMEX 金 2009年09月限,1068.70,10,2008/09/06\r\n" +
				"GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\n" +
				"GCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n"
			), cfg);
		try {
			final List<Map<String, String>> list = new ColumnNameMapListHandler()
				.filter(new SimpleCsvNamedValueFilter().ne(0, "gcu09", true))
				.offset(1).limit(1)
				.load(reader);

			assertThat(list.size(), is(1));
			final Map<String, String> m2 = list.get(0);
			assertThat(m2.get("symbol"), is("GCX09"));
			assertThat(m2.get("name"), is("COMEX 金 2009年11月限"));
			assertThat(m2.get("price"), is("1088.70"));
			assertThat(m2.get("volume"), is("12"));
			assertThat(m2.get("date"), is("2008/11/06"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testSaveNoHeader() throws IOException {
		final List<Map<String, String>> list = new ArrayList<Map<String, String>>(3);
		final Map<String, String> m1 = new LinkedHashMap<String, String>(4);
		m1.put("symbol", "AAAA");
		m1.put("name", "aaa");
		m1.put("price", "10000");
		m1.put("volume", "10");
		list.add(m1);
		final Map<String, String> m2 = new LinkedHashMap<String, String>(4);
		m2.put("symbol", "BBBB");
		m2.put("name", "bbb");
		m2.put("price", null);
		m2.put("volume", "0");
		list.add(m2);

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			new ColumnNameMapListHandler().header(false).save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("AAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	public void testSave1() throws IOException {
		final List<Map<String, String>> list = new ArrayList<Map<String, String>>(3);
		final Map<String, String> m1 = new LinkedHashMap<String, String>(4);
		m1.put("symbol", "AAAA");
		m1.put("name", "aaa");
		m1.put("price", "10000");
		m1.put("volume", "10");
		list.add(m1);
		final Map<String, String> m2 = new LinkedHashMap<String, String>(4);
		m2.put("symbol", "BBBB");
		m2.put("name", "bbb");
		m2.put("price", null);
		m2.put("volume", "0");
		list.add(m2);

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			new ColumnNameMapListHandler().save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	public void testSave2() throws IOException {
		final List<Map<String, String>> list = new ArrayList<Map<String, String>>(3);
		final Map<String, String> m1 = new LinkedHashMap<String, String>(4);
		m1.put("symbol", "AAAA");
		m1.put("name", "aaa");
//		m1.put("price", "10000");
		m1.put("volume", "10");
		list.add(m1);
		final Map<String, String> m2 = new LinkedHashMap<String, String>(4);
		m2.put("symbol", "BBBB");
		m2.put("name", "bbb");
//		m2.put("price", null);
		m2.put("volume", "0");
		list.add(m2);

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			new ColumnNameMapListHandler()
				.addColumn("symbol")
				.addColumn("name")
				.addColumn("price")
				.addColumn("volume")
				.save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,NULL,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	public void testSaveFilter() throws Exception {
		final List<Map<String, String>> list = new ArrayList<Map<String, String>>(3);
		final Map<String, String> m0 = new LinkedHashMap<String, String>(5);
		m0.put("symbol", "GCU09");
		m0.put("name", "COMEX 金 2009年09月限");
		m0.put("price", "1068.70");
		m0.put("volume", "10");
		m0.put("date", "2008/09/06");
		list.add(m0);
		final Map<String, String> m1 = new LinkedHashMap<String, String>(5);
		m1.put("symbol", "GCV09");
		m1.put("name", "COMEX 金 2009年10月限");
		m1.put("price", "1078.70");
		m1.put("volume", "11");
		m1.put("date", "2008/10/06");
		list.add(m1);
		final Map<String, String> m2 = new LinkedHashMap<String, String>(5);
		m2.put("symbol", "GCX09");
		m2.put("name", "COMEX 金 2009年11月限");
		m2.put("price", "1088.70");
		m2.put("volume", "12");
		m2.put("date", "2008/11/06");
		list.add(m2);

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			new ColumnNameMapListHandler()
				.filter(new SimpleCsvNamedValueFilter().ne("symbol", "gcu09", true))
				.save(list, writer);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\nGCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\nGCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n"));
	}

}