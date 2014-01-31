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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;

/**
 * {@link CsvColumnNameMapWriter} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public class CsvColumnNameMapWriterTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

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

	// ------------------------------------------------------------------------
	// コンストラクタ

	@Test
	public void testConstructorCsvWriterIllegalArgumentException() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvWriter must not be null");
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(null);
		writer.close();
	}

	@Test
	public void testConstructorCsvWriterListIllegalArgumentException() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvWriter must not be null");
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(null, Arrays.asList("symbol", "name", "price", "volume"));
		writer.close();
	}

	// ------------------------------------------------------------------------
	// オーバーライド メソッド

	@Test
	public void testFlush() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(new CsvWriter(sw, cfg));
		try {
			final Map<String, String> m1 = new LinkedHashMap<String, String>(4);
			m1.put("symbol", "AAAA");
			m1.put("name", "aaa");
			m1.put("price", "10000");
			m1.put("volume", "10");
			final boolean r1 = writer.write(m1);
			assertTrue(r1);

			writer.flush();
			assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\n"));

			final Map<String, String> m2 = new LinkedHashMap<String, String>(4);
			m2.put("symbol", "BBBB");
			m2.put("name", "bbb");
			m2.put("price", null);
			m2.put("volume", "0");
			final boolean r2 = writer.write(m2);
			assertTrue(r2);

			writer.flush();
			assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));

		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	public void testFlushIOException() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("CsvWriter closed");
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(new CsvWriter(new StringWriter(), cfg));
		writer.close();
		// Act
		writer.flush();
	}

	@Test
	public void testCloseIOException() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("CsvWriter closed");
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(new CsvWriter(new StringWriter(), cfg));
		writer.close();
		// Act
		writer.close();
	}

	// ------------------------------------------------------------------------
	// パブリック メソッド

	@Test
	public void testWriteNoHeader() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(new CsvWriter(sw, cfg), false);
		try {
			final Map<String, String> m1 = new LinkedHashMap<String, String>(4);
			m1.put("symbol", "AAAA");
			m1.put("name", "aaa");
			m1.put("price", "10000");
			m1.put("volume", "10");

			writer.writeHeader(m1);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is(""));

			final boolean r1 = writer.write(m1);
			assertTrue(r1);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("AAAA,aaa,10000,10\r\n"));

			final Map<String, String> m2 = new LinkedHashMap<String, String>(4);
			m2.put("symbol", "BBBB");
			m2.put("name", "bbb");
			m2.put("price", null);
			m2.put("volume", "0");
			final boolean r2 = writer.write(m2);
			assertTrue(r2);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("AAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("AAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	public void testWriteHeader() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(new CsvWriter(sw, cfg));
		try {
			final Map<String, String> m1 = new LinkedHashMap<String, String>(4);
			m1.put("symbol", "AAAA");
			m1.put("name", "aaa");
			m1.put("price", "10000");
			m1.put("volume", "10");

			writer.writeHeader(m1);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\n"));

			final boolean r1 = writer.write(m1);
			assertTrue(r1);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\n"));

			final Map<String, String> m2 = new LinkedHashMap<String, String>(4);
			m2.put("symbol", "BBBB");
			m2.put("name", "bbb");
			m2.put("price", null);
			m2.put("volume", "0");
			final boolean r2 = writer.write(m2);
			assertTrue(r2);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	public void testNoHeader() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("No header is available");
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(new CsvWriter(new StringWriter(), cfg));
		try {
			writer.write(null);
		} finally {
			writer.close();
		}
	}

	@Test
	public void testWrite1() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(new CsvWriter(sw, cfg));
		try {
			final Map<String, String> m1 = new LinkedHashMap<String, String>(4);
			m1.put("symbol", "AAAA");
			m1.put("name", "aaa");
			m1.put("price", "10000");
			m1.put("volume", "10");
			final boolean r1 = writer.write(m1);
			assertTrue(r1);

			final Map<String, String> m2 = new LinkedHashMap<String, String>(4);
			m2.put("symbol", "BBBB");
			m2.put("name", "bbb");
			m2.put("price", null);
			m2.put("volume", "0");
			final boolean r2 = writer.write(m2);
			assertTrue(r2);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	public void testWrite2() throws IOException {
		final StringWriter sw = new StringWriter();
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(
				new CsvWriter(sw, cfg),
				Arrays.asList("symbol", "name", "price", "volume")
			);
		try {
			final Map<String, String> m1 = new LinkedHashMap<String, String>(4);
			m1.put("symbol", "AAAA");
			m1.put("name", "aaa");
//			m1.put("price", "10000");
			m1.put("volume", "10");
			final boolean r1 = writer.write(m1);
			assertTrue(r1);

			final Map<String, String> m2 = new LinkedHashMap<String, String>(4);
			m2.put("symbol", "BBBB");
			m2.put("name", "bbb");
//			m2.put("price", null);
			m2.put("volume", "0");
			final boolean r2 = writer.write(m2);
			assertTrue(r2);
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume\r\nAAAA,aaa,NULL,10\r\nBBBB,bbb,NULL,0\r\n"));
	}

	@Test
	public void testWriteFilter() throws Exception {
		final StringWriter sw = new StringWriter();
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(new CsvWriter(sw, cfg));
		try {
			writer.setFilter(new SimpleCsvNamedValueFilter().ne("symbol", "gcu09", true));

			final Map<String, String> m0 = new LinkedHashMap<String, String>(5);
			m0.put("symbol", "GCU09");
			m0.put("name", "COMEX 金 2009年09月限");
			m0.put("price", "1068.70");
			m0.put("volume", "10");
			m0.put("date", "2008/09/06");
			final boolean r0 = writer.write(m0);
			assertFalse(r0);

			final Map<String, String> m1 = new LinkedHashMap<String, String>(5);
			m1.put("symbol", "GCV09");
			m1.put("name", "COMEX 金 2009年10月限");
			m1.put("price", "1078.70");
			m1.put("volume", "11");
			m1.put("date", "2008/10/06");
			final boolean r1 = writer.write(m1);
			assertTrue(r1);

			final Map<String, String> m2 = new LinkedHashMap<String, String>(5);
			m2.put("symbol", "GCX09");
			m2.put("name", "COMEX 金 2009年11月限");
			m2.put("price", "1088.70");
			m2.put("volume", "12");
			m2.put("date", "2008/11/06");
			final boolean r2 = writer.write(m2);
			assertTrue(r2);

		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\nGCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\nGCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n"));
	}

	// ------------------------------------------------------------------------
	// セッター / ゲッター

	@Test
	public void testFilter() throws Exception {
		final SimpleCsvNamedValueFilter filter = new SimpleCsvNamedValueFilter().ne("symbol", "gcu09", true);
		assertNotNull(filter);

		final StringWriter sw = new StringWriter();
		final CsvColumnNameMapWriter writer = new CsvColumnNameMapWriter(new CsvWriter(sw, cfg));
		try {
			writer.setFilter(filter);
			assertEquals(filter, writer.getFilter());

			final Map<String, String> m0 = new LinkedHashMap<String, String>(5);
			m0.put("symbol", "GCU09");
			m0.put("name", "COMEX 金 2009年09月限");
			m0.put("price", "1068.70");
			m0.put("volume", "10");
			m0.put("date", "2008/09/06");
			final boolean r0 = writer.write(m0);
			assertFalse(r0);

			assertEquals(filter, writer.getFilter());

			final Map<String, String> m1 = new LinkedHashMap<String, String>(5);
			m1.put("symbol", "GCV09");
			m1.put("name", "COMEX 金 2009年10月限");
			m1.put("price", "1078.70");
			m1.put("volume", "11");
			m1.put("date", "2008/10/06");
			final boolean r1 = writer.write(m1);
			assertTrue(r1);

			assertEquals(filter, writer.getFilter());

			final Map<String, String> m2 = new LinkedHashMap<String, String>(5);
			m2.put("symbol", "GCX09");
			m2.put("name", "COMEX 金 2009年11月限");
			m2.put("price", "1088.70");
			m2.put("volume", "12");
			m2.put("date", "2008/11/06");
			final boolean r2 = writer.write(m2);
			assertTrue(r2);

			assertEquals(filter, writer.getFilter());
		} finally {
			writer.close();
		}
		assertThat(sw.getBuffer().toString(), is("symbol,name,price,volume,date\r\nGCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\nGCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n"));
		assertEquals(filter, writer.getFilter());
	}

}