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

package com.orangesignal.csv.bean;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;

import com.orangesignal.csv.handlers.SampleType;

/**
 * {@link CsvValueConverter} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 */
public class SimpleCsvValueConverterTest {

	@Test
	public void testGetDateFormat() {
		final SimpleCsvValueConverter c = new SimpleCsvValueConverter();
		assertThat(c.getDateFormat(), nullValue());
	}

	@Test
	public void testSetDateFormat() {
		final SimpleCsvValueConverter c = new SimpleCsvValueConverter();
		c.setDateFormat(new SimpleDateFormat("yyyy/MM/dd"));
		assertThat(c.getDateFormat(), notNullValue());
	}

	@Test
	public void testConvertStringClassOfQ() {
		final SimpleCsvValueConverter c = new SimpleCsvValueConverter();

		// boolean
		assertThat(((Boolean) c.convert(null, Boolean.TYPE)).booleanValue(), is(false));
		assertThat(((Boolean) c.convert("0", Boolean.TYPE)).booleanValue(), is(false));
		assertThat(((Boolean) c.convert("1", Boolean.TYPE)).booleanValue(), is(true));
		assertThat(((Boolean) c.convert("false", Boolean.TYPE)).booleanValue(), is(false));
		assertThat(((Boolean) c.convert("true", Boolean.TYPE)).booleanValue(), is(true));
		assertThat(((Boolean) c.convert("FALSE", Boolean.TYPE)).booleanValue(), is(false));
		assertThat(((Boolean) c.convert("TRUE", Boolean.TYPE)).booleanValue(), is(true));
		assertThat(((Boolean) c.convert("f", Boolean.TYPE)).booleanValue(), is(false));
		assertThat(((Boolean) c.convert("t", Boolean.TYPE)).booleanValue(), is(true));
		assertThat(((Boolean) c.convert("F", Boolean.TYPE)).booleanValue(), is(false));
		assertThat(((Boolean) c.convert("T", Boolean.TYPE)).booleanValue(), is(true));
		assertThat(((Boolean) c.convert("no", Boolean.TYPE)).booleanValue(), is(false));
		assertThat(((Boolean) c.convert("yes", Boolean.TYPE)).booleanValue(), is(true));
		assertThat(((Boolean) c.convert("NO", Boolean.TYPE)).booleanValue(), is(false));
		assertThat(((Boolean) c.convert("YES", Boolean.TYPE)).booleanValue(), is(true));
		assertThat(((Boolean) c.convert("n", Boolean.TYPE)).booleanValue(), is(false));
		assertThat(((Boolean) c.convert("y", Boolean.TYPE)).booleanValue(), is(true));
		assertThat(((Boolean) c.convert("N", Boolean.TYPE)).booleanValue(), is(false));
		assertThat(((Boolean) c.convert("Y", Boolean.TYPE)).booleanValue(), is(true));
		assertThat(((Boolean) c.convert("off", Boolean.TYPE)).booleanValue(), is(false));
		assertThat(((Boolean) c.convert("on", Boolean.TYPE)).booleanValue(), is(true));
		assertThat(((Boolean) c.convert("OFF", Boolean.TYPE)).booleanValue(), is(false));
		assertThat(((Boolean) c.convert("ON", Boolean.TYPE)).booleanValue(), is(true));
		assertThat(((Boolean) c.convert("x", Boolean.TYPE)).booleanValue(), is(false));
		assertThat(((Boolean) c.convert("o", Boolean.TYPE)).booleanValue(), is(true));
		assertThat(((Boolean) c.convert("X", Boolean.TYPE)).booleanValue(), is(false));
		assertThat(((Boolean) c.convert("O", Boolean.TYPE)).booleanValue(), is(true));

		// Boolean
		assertNull(c.convert(null, Boolean.class));
		assertThat((Boolean) c.convert("0", Boolean.class), is(Boolean.FALSE));
		assertThat((Boolean) c.convert("1", Boolean.class), is(Boolean.TRUE));
		assertThat((Boolean) c.convert("false", Boolean.class), is(Boolean.FALSE));
		assertThat((Boolean) c.convert("true", Boolean.class), is(Boolean.TRUE));
		assertThat((Boolean) c.convert("FALSE", Boolean.class), is(Boolean.FALSE));
		assertThat((Boolean) c.convert("TRUE", Boolean.class), is(Boolean.TRUE));
		assertThat((Boolean) c.convert("f", Boolean.class), is(Boolean.FALSE));
		assertThat((Boolean) c.convert("t", Boolean.class), is(Boolean.TRUE));
		assertThat((Boolean) c.convert("F", Boolean.class), is(Boolean.FALSE));
		assertThat((Boolean) c.convert("T", Boolean.class), is(Boolean.TRUE));
		assertThat((Boolean) c.convert("no", Boolean.class), is(Boolean.FALSE));
		assertThat((Boolean) c.convert("yes", Boolean.class), is(Boolean.TRUE));
		assertThat((Boolean) c.convert("NO", Boolean.class), is(Boolean.FALSE));
		assertThat((Boolean) c.convert("YES", Boolean.class), is(Boolean.TRUE));
		assertThat((Boolean) c.convert("n", Boolean.class), is(Boolean.FALSE));
		assertThat((Boolean) c.convert("y", Boolean.class), is(Boolean.TRUE));
		assertThat((Boolean) c.convert("N", Boolean.class), is(Boolean.FALSE));
		assertThat((Boolean) c.convert("Y", Boolean.class), is(Boolean.TRUE));
		assertThat((Boolean) c.convert("off", Boolean.class), is(Boolean.FALSE));
		assertThat((Boolean) c.convert("on", Boolean.class), is(Boolean.TRUE));
		assertThat((Boolean) c.convert("OFF", Boolean.class), is(Boolean.FALSE));
		assertThat((Boolean) c.convert("ON", Boolean.class), is(Boolean.TRUE));
		assertThat((Boolean) c.convert("x", Boolean.class), is(Boolean.FALSE));
		assertThat((Boolean) c.convert("o", Boolean.class), is(Boolean.TRUE));
		assertThat((Boolean) c.convert("X", Boolean.class), is(Boolean.FALSE));
		assertThat((Boolean) c.convert("O", Boolean.class), is(Boolean.TRUE));

		// int / Integer
		assertThat(((Integer) c.convert(null, Integer.TYPE)).intValue(), is(0));
		assertThat(((Integer) c.convert("100", Integer.TYPE)).intValue(), is(100));
		assertNull(c.convert(null, Integer.class));
		assertThat(((Integer) c.convert("100", Integer.class)), is(Integer.valueOf(100)));

		// Number
		assertNull(c.convert(null, Number.class));
		assertThat(((Number) c.convert("100", Number.class)).intValue(), is(100));

		// java.util.Date
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		c.setDateFormat((DateFormat) df.clone());
		assertNull(c.convert(null, java.util.Date.class));
		assertThat(df.format((java.util.Date) c.convert("2009/10/21", java.util.Date.class)), is("2009/10/21"));

		// java.sql.Date
		assertNull(c.convert(null, java.sql.Date.class));
		assertThat(df.format((java.sql.Date) c.convert("2009/10/21", java.sql.Date.class)), is("2009/10/21"));

		// java.sql.Time
		df = new SimpleDateFormat("HH:mm:ss");
		c.setDateFormat((DateFormat) df.clone());
		assertNull(c.convert(null, java.sql.Time.class));
		assertThat(df.format((java.sql.Time) c.convert("12:34:56", java.sql.Time.class)), is("12:34:56"));

		// java.sql.Timestamp
		df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		c.setDateFormat((DateFormat) df.clone());
		assertNull(c.convert(null, java.sql.Timestamp.class));
		assertThat(df.format((java.sql.Timestamp) c.convert("2009/10/21 12:34:56.789", java.sql.Timestamp.class)), is("2009/10/21 12:34:56.789"));

		// String
		assertNull(c.convert(null, String.class));
		assertThat(((String) c.convert("test", String.class)), is("test"));

		// Enum
		assertNull(c.convert(null, SampleType.class));
		assertThat(((SampleType) c.convert("A", SampleType.class)), is(SampleType.A));
		assertThat(((SampleType) c.convert("B", SampleType.class)), is(SampleType.B));
		assertThat(((SampleType) c.convert("C", SampleType.class)), is(SampleType.C));
	}

	@Test
	public void testConvertObject() throws ParseException {
		final SimpleCsvValueConverter c = new SimpleCsvValueConverter();
		assertNull(c.convert(null));
		assertThat(c.convert(Boolean.FALSE), is("false"));
		assertThat(c.convert(Boolean.TRUE), is("true"));
		assertThat(c.convert(100), is("100"));
		assertThat(c.convert(10.05), is("10.05"));
		assertThat(c.convert("test"), is("test"));
		assertThat(c.convert(SampleType.A), is("A"));

		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		c.setDateFormat((DateFormat) df.clone());
		assertThat(c.convert(df.parse("2009/10/21")), is("2009/10/21"));
	}

}