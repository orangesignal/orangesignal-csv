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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;

import com.orangesignal.csv.Constants;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;

/**
 * {@link ResultSetHandler} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
public class ResultSetHandlerTest {

	private static CsvConfig cfg;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cfg = new CsvConfig(',', '"', '\\');
		cfg.setNullString("NULL");
		cfg.setBreakString("\n");
		cfg.setIgnoreTrailingWhitespaces(true);
		cfg.setIgnoreLeadingWhitespaces(true);
		cfg.setIgnoreEmptyLines(true);
		cfg.setIgnoreLinePatterns(Pattern.compile("^#.*$"));
		cfg.setLineSeparator(Constants.CRLF);
	}

	@Test
	public void testLoad() throws Exception {
		final ResultSet rs = new ResultSetHandler().load(
				new CsvReader(new StringReader(
					"# text/tab-separated-values   \r\n" +
					" col1 , \"col2\" , \"col3\" \r\n" +
					" aaa , \"b\r\nb\\\\b\" , \"c\\\"cc\" \r\n" +
					" zzz , yyy , NULL \r\n" +
					"# Copyright 2009 OrangeSignal.   "
				), cfg)
			);
		try {
			assertThat(rs.getMetaData().getColumnCount(), is(3));
			assertTrue(rs.next());
			assertThat(rs.getRow(), is(1));
			assertThat(rs.getString("col1"), is("aaa"));
			assertThat(rs.getString("col2"), is("b\nb\\\\b"));
			assertThat(rs.getString("col3"), is("c\"cc"));

			assertTrue(rs.next());
			assertThat(rs.getRow(), is(2));
			assertThat(rs.getString("col1"), is("zzz"));
			assertThat(rs.getString("col2"), is("yyy"));
			assertNull(rs.getString("col3"));
			assertTrue(rs.wasNull());

			assertFalse(rs.next());
		} finally {
			rs.close();
		}
	}

	@Test
	public void testSave()  throws Exception {
		final ResultSet rs = new ResultSetHandler().load(
				new CsvReader(new StringReader(
					"# text/tab-separated-values   \r\n" +
					" col1 , \"col2\" , \"col3\" \r\n" +
					" aaa , \"b\r\nb\\\\b\" , \"c\\\"cc\" \r\n" +
					" zzz , yyy , NULL \r\n" +
					"# Copyright 2009 OrangeSignal.   "
				), cfg)
			);
		try {
			final StringWriter sw = new StringWriter();
			final CsvWriter writer = new CsvWriter(sw, cfg);
			try {
				new ResultSetHandler().save(rs, writer);
				writer.flush();
				assertThat(sw.getBuffer().toString(), is("\"col1\",\"col2\",\"col3\"\r\n\"aaa\",\"b\nb\\\\b\",\"c\\\"cc\"\r\n\"zzz\",\"yyy\",NULL\r\n"));
			} finally {
				writer.close();
			}
		} finally {
			rs.close();
		}
	}

}