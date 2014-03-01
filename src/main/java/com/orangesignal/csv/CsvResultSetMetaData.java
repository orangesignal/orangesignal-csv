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

package com.orangesignal.csv;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.List;

/**
 * {@link ResultSetMetaData} の実装クラスを提供します。
 *
 * @author Koji Sugisawa
 */
public class CsvResultSetMetaData implements Serializable, ResultSetMetaData {

	private static final long serialVersionUID = -177998106514695495L;

	/**
	 * 列名のリストを保持します。
	 */
	private final List<String> columns;

	/**
	 * コンストラクタです。
	 *
	 * @param reader 区切り文字形式入力ストリーム
	 * @throws IOException 入出力例外が発生した場合
	 */
	public CsvResultSetMetaData(final CsvReader reader) throws IOException {
		columns = reader.readValues();
		if (columns == null) {
			throw new IOException("No header is available");
		}
	}

	// ------------------------------------------------------------------------

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	/**
	 * {@inheritDoc}
	 * この実装は常に <code>false</code> を返します。
	 */
	@Override public boolean isAutoIncrement(final int column) { return false; }

	/**
	 * {@inheritDoc}
	 * この実装は常に <code>true</code> を返します。
	 */
	@Override public boolean isCaseSensitive(final int column) { return true; }

	/**
	 * {@inheritDoc}
	 * この実装は常に <code>false</code> を返します。
	 */
	@Override
	public boolean isSearchable(final int column) { return false; }

	/**
	 * {@inheritDoc}
	 * この実装は常に <code>false</code> を返します。
	 */
	@Override public boolean isCurrency(final int column) { return false; }

	/**
	 * {@inheritDoc}
	 * この実装は常に {@link ResultSetMetaData#columnNullableUnknown} を返します。
	 */
	@Override public int isNullable(final int column) { return columnNullableUnknown; }

	/**
	 * {@inheritDoc}
	 * この実装は常に <code>false</code> を返します。
	 */
	@Override public boolean isSigned(final int column) { return false; }

	/**
	 * {@inheritDoc}
	 * この実装は常に {@link Integer#MAX_VALUE} を返します。
	 */
	@Override public int getColumnDisplaySize(final int column) { return Integer.MAX_VALUE; }

	@Override
	public String getColumnLabel(final int column) throws SQLException {
		return getColumnName(column);
	}

	@Override
	public String getColumnName(final int column) throws SQLException {
		if (column < 1 || column > columns.size()) {
			throw new SQLException("Invalid column " + column);
		}
		return columns.get(column - 1);
	}

	/**
	 * {@inheritDoc}
	 * この実装は常に空文字列 (<code>""</code>) を返します。
	 */
	@Override public String getSchemaName(final int column) { return ""; }

	/**
	 * {@inheritDoc}
	 * この実装は常に <code>0</code> を返します。
	 */
	@Override public int getPrecision(final int column) { return 0; }

	/**
	 * {@inheritDoc}
	 * この実装は常に <code>0</code> を返します。
	 */
	@Override public int getScale(final int column) { return 0; }

	/**
	 * {@inheritDoc}
	 * この実装は常に空文字列 (<code>""</code>) を返します。
	 */
	@Override public String getTableName(final int column) { return ""; }

	/**
	 * {@inheritDoc}
	 * この実装は常に空文字列 (<code>""</code>) を返します。
	 */
	@Override public String getCatalogName(final int column) { return ""; }

	/**
	 * {@inheritDoc}
	 * この実装は常に {@link Types#VARCHAR} を返します。
	 */
	@Override public int getColumnType(final int column) { return Types.VARCHAR; }

	/**
	 * {@inheritDoc}
	 * この実装は常に "java.lang.String" を返します。
	 */
	@Override public String getColumnTypeName(final int column) { return String.class.getName(); }

	/**
	 * {@inheritDoc}
	 * この実装は常に <code>true</code> を返します。
	 */
	@Override public boolean isReadOnly(final int column) { return true; }

	/**
	 * {@inheritDoc}
	 * この実装は常に <code>false</code> を返します。
	 */
	@Override public boolean isWritable(final int column) { return false; }

	/**
	 * {@inheritDoc}
	 * この実装は常に <code>false</code> を返します。
	 */
	@Override public boolean isDefinitelyWritable(final int column) { return false; }

	/**
	 * {@inheritDoc}
	 * この実装は常に "java.lang.String" を返します。
	 */
	@Override public String getColumnClassName(final int column) { return String.class.getName(); }

	// ------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public <T> T unwrap(final Class<T> iface) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("unwrap(Class) not supported");
	}

	/**
	 * {@inheritDoc}
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public boolean isWrapperFor(final Class<?> iface) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("isWrapperFor(Class) not supported");
	}

}