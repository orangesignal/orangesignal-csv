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

package com.orangesignal.csv.manager;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.entity.Price;
import com.orangesignal.csv.filters.SimpleBeanFilter;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;

/**
 * {@link CsvEntityManager} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 */
public class CsvEntityManagerTest {

	private static CsvConfig cfg;

	@Rule
	public ExpectedException exception = ExpectedException.none();

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

	@Test
	public void testCsvEntityManager() {
		new CsvEntityManager();
		new CsvEntityManager(cfg);
	}

	@Test
	public void testCsvEntityManagerIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvConfig must not be null");
		new CsvEntityManager(null);
	}

	@Test
	public void testConfig() {
		new CsvEntityManager().config(cfg);
	}

	@Test
	public void testConfigIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvConfig must not be null");
		new CsvEntityManager().config(null);
	}

	@Test
	public void testLoad() throws Exception {
		final Reader reader = new StringReader(
				"シンボル,名称,価格,出来高,日付,時刻\r\n" +
				"GCQ09,COMEX 金 2009年08月限,1\\,058.70,10,2008/08/06,12:00:00\r\n" +
				"GCU09,COMEX 金 2009年09月限,1\\,068.70,10,2008/09/06,12:00:00\r\n" +
				"GCV09,COMEX 金 2009年10月限,1\\,078.70,11,2008/10/06,12:00:00\r\n" +
				"GCX09,COMEX 金 2009年11月限,1\\,088.70,12,2008/11/06,12:00:00\r\n" +
				"GCZ09,COMEX 金 2009年12月限,1\\,098.70,13,2008/12/06,12:00:00\r\n"
			);

		try {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			final List<Price> list = new CsvEntityManager(cfg)
				.load(Price.class)
				.filter(new SimpleCsvNamedValueFilter().ne("シンボル", "gcu09", true))
				.filter(new SimpleBeanFilter().ne("date", df.parse("2008/11/06 12:00:00")))
				.offset(1).limit(1)
				.from(reader);

			assertThat(list.size(), is(1));
			final Price o1 = list.get(0);
			assertThat(o1.symbol, is("GCV09"));
			assertThat(o1.name, is("COMEX 金 2009年10月限"));
			assertThat(o1.price.doubleValue(), is(1078.70D));
			assertThat(o1.volume.longValue(), is(11L));
			assertThat(o1.date, is(df.parse("2008/10/06 12:00:00")));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testSave() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		final List<Price> list = new ArrayList<Price>();
		list.add(new Price("GCU09", "COMEX 金 2009年09月限", 1068, 10, df.parse("2008/09/06 12:00:00")));
		list.add(new Price("GCV09", "COMEX 金 2009年10月限", 1078, 11, df.parse("2008/10/06 12:00:00")));
		list.add(new Price("GCX09", "COMEX 金 2009年11月限", 1088, 12, df.parse("2008/11/06 12:00:00")));

		final StringWriter sw = new StringWriter();
		try {
			new CsvEntityManager(cfg)
				.save(list, Price.class)
				.filter(new SimpleCsvNamedValueFilter().ne("シンボル", "gcu09", true))
				.filter(new SimpleBeanFilter().ne("date", df.parse("2008/11/06 12:00:00")))
				.to(sw);

			assertThat(sw.getBuffer().toString(), is("シンボル,名称,価格,出来高,日付,時刻\r\nGCV09,COMEX 金 2009年10月限,1\\,078,11,2008/10/06,12:00:00\r\n"));
		} finally {
			sw.close();
		}
	}

}