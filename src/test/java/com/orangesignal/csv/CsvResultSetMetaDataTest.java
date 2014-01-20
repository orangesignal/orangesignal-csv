/*
 * Copyright 2009-2013 the original author or authors.
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

package com.orangesignal.csv;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * {@link CsvResultSetMetaData} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 */
public class CsvResultSetMetaDataTest {

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
	}

	@Test
	public void test() throws Exception {
		final CsvReader reader = new CsvReader(new StringReader("id\r\nNULL"), cfg);
		try {
			final CsvResultSetMetaData meta = new CsvResultSetMetaData(reader);
			assertThat(meta.getColumnCount(), is(1));
			assertThat(meta.isAutoIncrement(1), is(false));
			assertThat(meta.isCaseSensitive(1), is(true));
			assertThat(meta.isSearchable(1), is(false));
			assertThat(meta.isCurrency(1), is(false));
			assertThat(meta.isNullable(1), is(ResultSetMetaData.columnNullableUnknown));
			assertThat(meta.isSigned(1), is(false));
			assertThat(meta.getColumnDisplaySize(1), is(Integer.MAX_VALUE));
			assertThat(meta.getColumnLabel(1), is("id"));
			assertThat(meta.getColumnName(1), is("id"));
			assertThat(meta.getSchemaName(1), is(""));
			assertThat(meta.getPrecision(1), is(0));
			assertThat(meta.getScale(1), is(0));
			assertThat(meta.getTableName(1), is(""));
			assertThat(meta.getCatalogName(1), is(""));
			assertThat(meta.getColumnType(1), is(Types.VARCHAR));
			assertThat(meta.getColumnTypeName(1), is(String.class.getName()));
			assertThat(meta.isReadOnly(1), is(true));
			assertThat(meta.isWritable(1), is(false));
			assertThat(meta.isDefinitelyWritable(1), is(false));
			assertThat(meta.getColumnClassName(1), is(String.class.getName()));
		} finally {
			reader.close();
		}
	}

	@Test(expected = SQLException.class)
	public void testGetColumnNameSQLException1() throws Exception {
		final CsvReader reader = new CsvReader(new StringReader("id\r\nNULL"), cfg);
		try {
			final CsvResultSetMetaData meta = new CsvResultSetMetaData(reader);
			meta.getColumnName(0);
		} finally {
			reader.close();
		}
	}

	@Test(expected = SQLException.class)
	public void testGetColumnNameSQLException2() throws Exception {
		final CsvReader reader = new CsvReader(new StringReader("id\r\nNULL"), cfg);
		try {
			final CsvResultSetMetaData meta = new CsvResultSetMetaData(reader);
			meta.getColumnName(meta.getColumnCount() + 1);
		} finally {
			reader.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUnwrap() throws Exception {
		final CsvReader reader = new CsvReader(new StringReader("id\r\nNULL"), cfg);
		try {
			final CsvResultSetMetaData meta = new CsvResultSetMetaData(reader);
			meta.unwrap(this.getClass());
		} finally {
			reader.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testIsWrapperFor() throws Exception {
		final CsvReader reader = new CsvReader(new StringReader("id\r\nNULL"), cfg);
		try {
			final CsvResultSetMetaData meta = new CsvResultSetMetaData(reader);
			meta.isWrapperFor(this.getClass());
		} finally {
			reader.close();
		}
	}

}