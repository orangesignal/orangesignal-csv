/*
 * Copyright (c) 2009-2013 OrangeSignal.com All rights reserved.
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

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvResultSetMetaData;

/**
 * {@link CsvResultSetMetaData} クラスの単体テストです。
 *
 * @author 杉澤 浩二
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
