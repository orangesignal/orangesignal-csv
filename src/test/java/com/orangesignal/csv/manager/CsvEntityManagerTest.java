/*
 * Copyright 2009-2014 the original author or authors.
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
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.entity.IntArrayEntity;
import com.orangesignal.csv.entity.IntegerArrayEntity;
import com.orangesignal.csv.entity.StringArrayEntity;
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
			df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

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
		df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

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

	@Test
	public void testStringArraySave() throws Exception {
		final List<StringArrayEntity> list = new ArrayList<StringArrayEntity>();

		final StringArrayEntity o1 = new StringArrayEntity();
		o1.array = new String[]{ "あ", null, "う" };
		o1.str = "えお";
		list.add(o1);

		final StringArrayEntity o2 = new StringArrayEntity();
		o2.array = new String[]{ "ア", "イ", "ウ" };
		o2.str = "エオ";
		list.add(o2);

		final StringArrayEntity o3 = new StringArrayEntity();
		o3.array = new String[]{ null, null, null };
		o3.str = null;
		list.add(o3);

		final StringArrayEntity o4 = new StringArrayEntity();
		o4.array = null;
		o4.str = "null";
		list.add(o4);

		final StringWriter sw = new StringWriter();
		try {
			new CsvEntityManager(cfg).save(list, StringArrayEntity.class).to(sw);
			assertThat(sw.getBuffer().toString(), is("あ,NULL,う,えお\r\nア,イ,ウ,エオ\r\nNULL,NULL,NULL,NULL\r\nNULL,NULL,NULL,null\r\n"));
		} finally {
			sw.close();
		}
	}

	@Test
	public void testStringArrayLoad() throws Exception {
		final Reader reader = new StringReader("あ,NULL,う,えお\r\nア,イ,ウ,エオ\r\nNULL,NULL,NULL,NULL\r\nNULL,NULL,NULL,null\r\n");
		try {
			final List<StringArrayEntity> list = new CsvEntityManager(cfg).load(StringArrayEntity.class).from(reader);

			assertThat(list.size(), is(4));

			final StringArrayEntity o1 = list.get(0);
			assertThat(o1.array.length, is(3));
			assertThat(o1.array[0], is("あ"));
			assertThat(o1.array[1], nullValue());
			assertThat(o1.array[2], is("う"));
			assertThat(o1.str, is("えお"));

			final StringArrayEntity o2 = list.get(1);
			assertThat(o2.array.length, is(3));
			assertThat(o2.array[0], is("ア"));
			assertThat(o2.array[1], is("イ"));
			assertThat(o2.array[2], is("ウ"));
			assertThat(o2.str, is("エオ"));

			final StringArrayEntity o3 = list.get(2);
			assertThat(o3.array.length, is(3));
			assertThat(o3.array[0], nullValue());
			assertThat(o3.array[1], nullValue());
			assertThat(o3.array[2], nullValue());
			assertThat(o3.str, nullValue());

			final StringArrayEntity o4 = list.get(3);
			assertThat(o4.array.length, is(3));
			assertThat(o4.array[0], nullValue());
			assertThat(o4.array[1], nullValue());
			assertThat(o4.array[2], nullValue());
			assertThat(o4.str, is("null"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testIntegerArraySave() throws Exception {
		final List<IntegerArrayEntity> list = new ArrayList<IntegerArrayEntity>();

		final IntegerArrayEntity o1 = new IntegerArrayEntity();
		o1.array = new Integer[]{ 1, null, 3 };
		o1.str = "えお";
		list.add(o1);

		final IntegerArrayEntity o2 = new IntegerArrayEntity();
		o2.array = new Integer[]{ 4, 5, 6 };
		o2.str = "エオ";
		list.add(o2);

		final StringWriter sw = new StringWriter();
		try {
			new CsvEntityManager(cfg).save(list, IntegerArrayEntity.class).to(sw);
			assertThat(sw.getBuffer().toString(), is("1,NULL,3,えお\r\n4,5,6,エオ\r\n"));
		} finally {
			sw.close();
		}
	}

	@Test
	public void testIntegerArrayLoad() throws Exception {
		final Reader reader = new StringReader("1,NULL,3,えお\r\n4,5,6,エオ\r\n");
		try {
			final List<IntegerArrayEntity> list = new CsvEntityManager(cfg).load(IntegerArrayEntity.class).from(reader);

			assertThat(list.size(), is(2));

			final IntegerArrayEntity o1 = list.get(0);
			assertThat(o1.array.length, is(3));
			assertThat(o1.array[0], is(1));
			assertThat(o1.array[1], nullValue());
			assertThat(o1.array[2], is(3));
			assertThat(o1.str, is("えお"));

			final IntegerArrayEntity o2 = list.get(1);
			assertThat(o2.array.length, is(3));
			assertThat(o2.array[0], is(4));
			assertThat(o2.array[1], is(5));
			assertThat(o2.array[2], is(6));
			assertThat(o2.str, is("エオ"));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testIntArraySave() throws Exception {
		final List<IntArrayEntity> list = new ArrayList<IntArrayEntity>();

		final IntArrayEntity o1 = new IntArrayEntity();
		o1.array = new int[]{ 1, 2, 3 };
		o1.str = "えお";
		list.add(o1);

		final IntArrayEntity o2 = new IntArrayEntity();
		o2.array = new int[]{ 4, 5, 6 };
		o2.str = "エオ";
		list.add(o2);

		final StringWriter sw = new StringWriter();
		try {
			new CsvEntityManager(cfg).save(list, IntArrayEntity.class).to(sw);
			assertThat(sw.getBuffer().toString(), is("1,2,3,えお\r\n4,5,6,エオ\r\n"));
		} finally {
			sw.close();
		}
	}

	@Test
	public void testIntArrayLoad() throws Exception {
		final Reader reader = new StringReader("1,2,3,えお\r\n4,5,6,エオ\r\n");
		try {
			final List<IntArrayEntity> list = new CsvEntityManager(cfg).load(IntArrayEntity.class).from(reader);

			assertThat(list.size(), is(2));

			final IntArrayEntity o1 = list.get(0);
			assertThat(o1.array.length, is(3));
			assertThat(o1.array[0], is(1));
			assertThat(o1.array[1], is(2));
			assertThat(o1.array[2], is(3));
			assertThat(o1.str, is("えお"));

			final IntArrayEntity o2 = list.get(1);
			assertThat(o2.array.length, is(3));
			assertThat(o2.array[0], is(4));
			assertThat(o2.array[1], is(5));
			assertThat(o2.array[2], is(6));
			assertThat(o2.str, is("エオ"));
		} finally {
			reader.close();
		}
	}

}