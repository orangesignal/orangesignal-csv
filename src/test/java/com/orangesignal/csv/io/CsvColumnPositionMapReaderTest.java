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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.filters.SimpleCsvValueFilter;

/**
 * {@link CsvColumnPositionMapReader} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public class CsvColumnPositionMapReaderTest {

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
	public void testConstructorCsvReaderIllegalArgumentException() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvReader must not be null");
		final CsvColumnPositionMapReader reader = new CsvColumnPositionMapReader(null);
		reader.close();
	}

	// ------------------------------------------------------------------------
	// オーバーライド メソッド

	@Test
	public void testClosed() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("CsvReader closed");
		final CsvColumnPositionMapReader reader = new CsvColumnPositionMapReader(new CsvReader(new StringReader("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg));
		reader.close();
		// Act
		reader.close();
	}

	// ------------------------------------------------------------------------
	// パブリック メソッド

	@Test
	public void testRead1() throws IOException {
		cfg.setSkipLines(1);	// ヘッダは不要なので読飛ばす指定をする
		final CsvColumnPositionMapReader reader = new CsvColumnPositionMapReader(new CsvReader(new StringReader("symbol,name,price,volume\r\nAAAA,aaa,10000,10\r\nBBBB,bbb,NULL,0"), cfg));
		try {
			final Map<Integer, String> m1 = reader.read();
			assertThat(m1.get(0), is("AAAA"));
			assertThat(m1.get(1), is("aaa"));
			assertThat(m1.get(2), is("10000"));
			assertThat(m1.get(3), is("10"));

			final Map<Integer, String> m2 = reader.read();
			assertThat(m2.get(0), is("BBBB"));
			assertThat(m2.get(1), is("bbb"));
			assertTrue(m2.containsKey(2));
			assertNull(m2.get(2));
			assertThat(m2.get(3), is("0"));

			final Map<Integer, String> last = reader.read();
			assertNull(last);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testReadFilter() throws IOException {
		final CsvColumnPositionMapReader reader = new CsvColumnPositionMapReader(new CsvReader(new StringReader(
//				"symbol,name,price,volume,date\r\n" +
				"GCU09,COMEX 金 2009年09月限,1068.70,10,2008/09/06\r\n" +
				"GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\n" +
				"GCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n"
			), cfg));
		try {
			reader.setFilter(new SimpleCsvValueFilter().ne(0, "gcu09", true));

			final Map<Integer, String> m1 = reader.read();
			assertThat(m1.get(0), is("GCV09"));
			assertThat(m1.get(1), is("COMEX 金 2009年10月限"));
			assertThat(m1.get(2), is("1078.70"));
			assertThat(m1.get(3), is("11"));
			assertThat(m1.get(4), is("2008/10/06"));

			final Map<Integer, String> m2 = reader.read();
			assertThat(m2.get(0), is("GCX09"));
			assertThat(m2.get(1), is("COMEX 金 2009年11月限"));
			assertThat(m2.get(2), is("1088.70"));
			assertThat(m2.get(3), is("12"));
			assertThat(m2.get(4), is("2008/11/06"));

			final Map<Integer, String> last = reader.read();
			assertNull(last);
		} finally {
			reader.close();
		}
	}

	// ------------------------------------------------------------------------
	// セッター / ゲッター

	@Test
	public void testFilter() throws IOException {
		final SimpleCsvValueFilter filter = new SimpleCsvValueFilter().ne(0, "gcu09", true);
		assertNotNull(filter);

		final CsvColumnPositionMapReader reader = new CsvColumnPositionMapReader(new CsvReader(new StringReader(
//				"symbol,name,price,volume,date\r\n" +
				"GCU09,COMEX 金 2009年09月限,1068.70,10,2008/09/06\r\n" +
				"GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\n" +
				"GCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n"
			), cfg));
		try {
			reader.setFilter(filter);
			assertEquals(filter, reader.getFilter());

			final Map<Integer, String> m1 = reader.read();
			assertThat(m1.get(0), is("GCV09"));
			assertThat(m1.get(1), is("COMEX 金 2009年10月限"));
			assertThat(m1.get(2), is("1078.70"));
			assertThat(m1.get(3), is("11"));
			assertThat(m1.get(4), is("2008/10/06"));

			assertEquals(filter, reader.getFilter());

			final Map<Integer, String> m2 = reader.read();
			assertThat(m2.get(0), is("GCX09"));
			assertThat(m2.get(1), is("COMEX 金 2009年11月限"));
			assertThat(m2.get(2), is("1088.70"));
			assertThat(m2.get(3), is("12"));
			assertThat(m2.get(4), is("2008/11/06"));

			assertEquals(filter, reader.getFilter());

			final Map<Integer, String> last = reader.read();
			assertNull(last);

			assertEquals(filter, reader.getFilter());
		} finally {
			reader.close();
		}

		assertEquals(filter, reader.getFilter());
	}

}