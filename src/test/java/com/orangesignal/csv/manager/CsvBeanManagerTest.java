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
import com.orangesignal.csv.filters.SimpleBeanFilter;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;
import com.orangesignal.csv.model.SampleBean;

/**
 * {@link CsvBeanManager} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 */
public class CsvBeanManagerTest {

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
	public void testCsvBeanManager() {
		new CsvBeanManager();
		new CsvBeanManager(cfg);
	}

	@Test
	public void testCsvBeanManagerIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvConfig must not be null");
		new CsvBeanManager(null);
	}

	@Test
	public void testConfig() {
		new CsvBeanManager().config(cfg);
	}

	@Test
	public void testConfigIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("CsvConfig must not be null");
		new CsvBeanManager().config(null);
	}

	@Test
	public void testLoad() throws Exception {
		final Reader reader = new StringReader(
				"symbol,name,price,volume,date\r\n" +
				"GCQ09,COMEX 金 2009年08月限,1058.70,10,2008/08/06\r\n" +
				"GCU09,COMEX 金 2009年09月限,1068.70,10,2008/09/06\r\n" +
				"GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\n" +
				"GCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n" +
				"GCZ09,COMEX 金 2009年12月限,1098.70,13,2008/12/06\r\n"
			);

		try {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			final List<SampleBean> list
				= new CsvBeanManager(cfg)
					.load(SampleBean.class)
					.format("date", new SimpleDateFormat("yyyy/MM/dd"))
					.filter(new SimpleCsvNamedValueFilter().ne("symbol", "gcu09", true))
					.filter(new SimpleBeanFilter().ne("date", df.parse("2008/11/06")))
					.offset(1).limit(1)
					.from(reader);

			assertThat(list.size(), is(1));
			final SampleBean o1 = list.get(0);
			assertThat(o1.symbol, is("GCV09"));
			assertThat(o1.name, is("COMEX 金 2009年10月限"));
			assertThat(o1.price.doubleValue(), is(1078.70D));
			assertThat(o1.volume.longValue(), is(11L));
			assertThat(o1.date, is(df.parse("2008/10/06")));
		} finally {
			reader.close();
		}
	}

	@Test
	public void testSave() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");

		final List<SampleBean> list = new ArrayList<SampleBean>();
		list.add(new SampleBean("GCU09", "COMEX 金 2009年09月限", 1068.70, 10, df.parse("2008/09/06")));
		list.add(new SampleBean("GCV09", "COMEX 金 2009年10月限", 1078.70, 11, df.parse("2008/10/06")));
		list.add(new SampleBean("GCX09", "COMEX 金 2009年11月限", 1088.70, 12, df.parse("2008/11/06")));

		final StringWriter sw = new StringWriter();
		try {
			new CsvBeanManager(cfg)
				.save(list, SampleBean.class)
				.excludes("name")
				.format("price", new DecimalFormat("0.00"))
				.format("date", new SimpleDateFormat("yyyy/MM/dd"))
				.filter(new SimpleCsvNamedValueFilter().ne("symbol", "gcu09", true))
				.filter(new SimpleBeanFilter().ne("date", df.parse("2008/11/06")))
				.to(sw);
			assertThat(sw.getBuffer().toString(), is("symbol,price,volume,date\r\nGCV09,1078.70,11,2008/10/06\r\n"));
		} finally {
			sw.close();
		}
	}

}
