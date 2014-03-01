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
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;

/**
 * {@link ResultSet} の実装クラスを提供します。
 *
 * @author Koji Sugisawa
 */
public class CsvResultSet implements ResultSet {

	private CsvReader reader;

	/**
	 * この ResultSet オブジェクトの列の型とプロパティーに関する情報を保持します。
	 */
	private CsvResultSetMetaData meta;

	/**
	 * 現在の行を保持します。
	 */
	private List<String> row;

	/**
	 * 現在の行の番号を保持します。
	 */
	private int rowNumber = 0;

	/**
	 * 最後に読み込まれた列の値が NULL であるかどうかを保持します。
	 */
	private boolean wasNull;

	/**
	 * コンストラクタです。
	 *
	 * @param reader 区切り文字形式入力ストリーム
	 * @throws IllegalArgumentException <code>reader</code> が <code>null</code> の場合
	 * @throws IOException 入出力例外が発生した場合
	 */
	public CsvResultSet(final CsvReader reader) throws IOException {
		if (reader == null) {
			throw new IllegalArgumentException("CsvReader must not be null");
		}
		this.reader = reader;
		this.meta = new CsvResultSetMetaData(reader);
	}

	// ------------------------------------------------------------------------

	private void ensureOpen() throws SQLException {
		if (reader == null) {
		    throw new SQLException("Stream closed");
		}
	}

	@Override
	public boolean next() throws SQLException {
		ensureOpen();
		try {
			row = reader.readValues();
			if (row != null) {
				rowNumber++;
				return true;
			}
			return false;
		} catch (final IOException e) {
			throw new SQLException(e.getMessage(), e);
		}
	}

	@Override
	public void close() {
		try {
			reader.close();
		} catch (final IOException e) {
			// 無視する
		}
		reader = null;
		meta = null;
		row = null;
		rowNumber = 0;
		wasNull = false;
	}

	@Override
	public boolean wasNull() throws SQLException {
		ensureOpen();
		return wasNull;
	}

	// ------------------------------------------------------------------------

	@Override
	public String getString(final int columnIndex) throws SQLException {
		ensureOpen();
		if (row == null) {
			throw new SQLException("No data is available");
		}
		if (columnIndex < 1 || columnIndex > row.size()) {
			throw new SQLException(String.format("Invalid column index %d", columnIndex));
		}
		final String s = row.get(columnIndex - 1);
		wasNull = s == null;
		return s;
	}

	@Override
	public boolean getBoolean(final int columnIndex) throws SQLException {
		final String s = getString(columnIndex);
		if (s == null) {
			return false;
		} else if ("1".equalsIgnoreCase(s)) {
			return true;
		} else if ("0".equalsIgnoreCase(s)) {
			return false;
		} else if ("true".equalsIgnoreCase(s)) {
			return true;
		} else if ("false".equalsIgnoreCase(s)) {
			return false;
		} else if ("on".equalsIgnoreCase(s)) {
			return true;
		} else if ("off".equalsIgnoreCase(s)) {
			return false;
		} else if ("yes".equalsIgnoreCase(s)) {
			return true;
		} else if ("no".equalsIgnoreCase(s)) {
			return false;
		}

		throw new SQLException(String.format("Bad format for BOOLEAN '%s' in column %d.", s, columnIndex));
	}

	@Override
	public byte getByte(final int columnIndex) throws SQLException {
		final String s = getString(columnIndex);
		if (s == null) {
			return 0;
		}
		return Byte.valueOf(s).byteValue();
	}

	@Override
	public short getShort(final int columnIndex) throws SQLException {
		final String s = getString(columnIndex);
		if (s == null) {
			return 0;
		}
		return Short.valueOf(s).shortValue();
	}

	@Override
	public int getInt(final int columnIndex) throws SQLException {
		final String s = getString(columnIndex);
		if (s == null) {
			return 0;
		}
		return Integer.valueOf(s).intValue();
	}

	@Override
	public long getLong(final int columnIndex) throws SQLException {
		final String s = getString(columnIndex);
		if (s == null) {
			return 0L;
		}
		return Long.valueOf(s).longValue();
	}

	@Override
	public float getFloat(final int columnIndex) throws SQLException {
		final String s = getString(columnIndex);
		if (s == null) {
			return 0F;
		}
		return Float.valueOf(s).floatValue();
	}

	@Override
	public double getDouble(final int columnIndex) throws SQLException {
		final String s = getString(columnIndex);
		if (s == null) {
			return 0D;
		}
		return Double.valueOf(s).doubleValue();
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@SuppressWarnings("deprecation")
	@Override
	public BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("getBigDecimal(int, int) not supported");
	}

	@Override
	public byte[] getBytes(final int columnIndex) throws SQLException {
		final String s = getString(columnIndex);
		if (s == null) {
			return null;
		}
		return s.getBytes();
	}

	@Override
	public Date getDate(final int columnIndex) throws SQLException {
		final String s = getString(columnIndex);
		if (s == null) {
			return null;
		}
		try {
			return new Date(DateFormat.getDateInstance().parse(s).getTime());
		} catch (final ParseException e) {
			throw new SQLException(String.format("Bad format for DATE '%s' in column %d.", s, columnIndex), e);
		}
	}

	@Override
	public Time getTime(final int columnIndex) throws SQLException {
		final String s = getString(columnIndex);
		if (s == null) {
			return null;
		}
		try {
			return new Time(DateFormat.getTimeInstance().parse(s).getTime());
		} catch (final ParseException e) {
			throw new SQLException(String.format("Bad format for TIME '%s' in column %d.", s, columnIndex), e);
		}
	}

	@Override
	public Timestamp getTimestamp(final int columnIndex) throws SQLException {
		final String s = getString(columnIndex);
		if (s == null) {
			return null;
		}
		try {
			return new Timestamp(DateFormat.getDateTimeInstance().parse(s).getTime());
		} catch (final ParseException e) {
			throw new SQLException(String.format("Bad format for TIMESTAMP '%s' in column %d.", s, columnIndex), e);
		}
	}

	@Override
	public InputStream getAsciiStream(final int columnIndex) throws SQLException {
		final Clob clob = getClob(columnIndex);
		if (clob == null) {
			return null;
		}
		return clob.getAsciiStream();
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@SuppressWarnings("deprecation")
	@Override
	public InputStream getUnicodeStream(final int columnIndex) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("getUnicodeStream(int) not supported");
	}

	@Override
	public InputStream getBinaryStream(final int columnIndex) throws SQLException {
		final Blob blob = getBlob(columnIndex);
		if (blob == null) {
			return null;
		}
		return blob.getBinaryStream();
	}

	// ------------------------------------------------------------------------

	@Override
	public String getString(final String columnLabel) throws SQLException {
		return getString(findColumn(columnLabel));
	}

	@Override
	public boolean getBoolean(final String columnLabel) throws SQLException {
		return getBoolean(findColumn(columnLabel));
	}

	@Override
	public byte getByte(final String columnLabel) throws SQLException {
		return getByte(findColumn(columnLabel));
	}

	@Override
	public short getShort(final String columnLabel) throws SQLException {
		return getShort(findColumn(columnLabel));
	}

	@Override
	public int getInt(final String columnLabel) throws SQLException {
		return getInt(findColumn(columnLabel));
	}

	@Override
	public long getLong(final String columnLabel) throws SQLException {
		return getLong(findColumn(columnLabel));
	}

	@Override
	public float getFloat(final String columnLabel) throws SQLException {
		return getFloat(findColumn(columnLabel));
	}

	@Override
	public double getDouble(final String columnLabel) throws SQLException {
		return getDouble(findColumn(columnLabel));
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@SuppressWarnings("deprecation")
	@Override
	public BigDecimal getBigDecimal(final String columnLabel, final int scale) throws SQLException {
		return getBigDecimal(findColumn(columnLabel), scale);
	}

	@Override
	public byte[] getBytes(final String columnLabel) throws SQLException {
		return getBytes(findColumn(columnLabel));
	}

	@Override
	public Date getDate(final String columnLabel) throws SQLException {
		return getDate(findColumn(columnLabel));
	}

	@Override
	public Time getTime(final String columnLabel) throws SQLException {
		return getTime(findColumn(columnLabel));
	}

	@Override
	public Timestamp getTimestamp(final String columnLabel) throws SQLException {
		return getTimestamp(findColumn(columnLabel));
	}

	@Override
	public InputStream getAsciiStream(final String columnLabel) throws SQLException {
		return getAsciiStream(findColumn(columnLabel));
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@SuppressWarnings("deprecation")
	@Override
	public InputStream getUnicodeStream(final String columnLabel) throws SQLException {
		return getUnicodeStream(findColumn(columnLabel));
	}

	@Override
	public InputStream getBinaryStream(final String columnLabel) throws SQLException {
		return getBinaryStream(findColumn(columnLabel));
	}

	// ------------------------------------------------------------------------

	/**
	 * この実装は <code>null</code> を返します。
	 */
	@Override
	public SQLWarning getWarnings() throws SQLException {
		ensureOpen();
		return null;
	}

	@Override
	public void clearWarnings() throws SQLException {
		ensureOpen();
	}

	// ------------------------------------------------------------------------

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public String getCursorName() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("getCursorName() not supported");
	}

	@Override
	public CsvResultSetMetaData getMetaData() throws SQLException {
		ensureOpen();
		return meta;
	}

	// ------------------------------------------------------------------------

	@Override
	public Object getObject(final int columnIndex) throws SQLException {
		return getString(columnIndex);
	}

	@Override
	public Object getObject(String columnLabel) throws SQLException {
		return getObject(findColumn(columnLabel));
	}

	@Override
	public int findColumn(final String columnLabel) throws SQLException {
		ensureOpen();
		if (columnLabel != null) {
			final int max = meta.getColumnCount();
			for (int i = 1 ; i <= max; i++) {
				if (columnLabel.equalsIgnoreCase(meta.getColumnName(i))) {
					return i;
				}
			}
		}
		throw new SQLException("invalid column label " + columnLabel);
	}

	@Override
	public Reader getCharacterStream(final int columnIndex) throws SQLException {
		final String s = getString(columnIndex);
		if (s == null) {
			return null;
		}
		return new StringReader(s);
	}

	@Override
	public Reader getCharacterStream(final String columnLabel) throws SQLException {
		return getCharacterStream(findColumn(columnLabel));
	}

	@Override
	public BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
		return new BigDecimal(getString(columnIndex));
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		return getBigDecimal(findColumn(columnLabel));
	}

	// ------------------------------------------------------------------------

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public boolean isBeforeFirst() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("isBeforeFirst() not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public boolean isAfterLast() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("isAfterLast() not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public boolean isFirst() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("isFirst() not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public boolean isLast() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("isLast() not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void beforeFirst() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("beforeFirst() not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void afterLast() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("afterLast() not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public boolean first() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("first() not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public boolean last() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("last() not supported");
	}

	@Override
	public int getRow() throws SQLException {
		ensureOpen();
		return rowNumber;
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public boolean absolute(final int row) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("absolute(int) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public boolean relative(final int rows) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("relative(int) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public boolean previous() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("previous() not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void setFetchDirection(final int direction) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("setFetchDirection(int) not supported");
	}

	/**
	 * この実装は {@link ResultSet#FETCH_FORWARD} を返します。
	 */
	@Override
	public int getFetchDirection() throws SQLException {
		ensureOpen();
		return ResultSet.FETCH_FORWARD;
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void setFetchSize(final int rows) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("setFetchSize(int) not supported");
	}

	/**
	 * この実装は <code>0</code> を返します。
	 */
	@Override
	public int getFetchSize() throws SQLException {
		ensureOpen();
		return 0;
	}

	/**
	 * この実装は {@link ResultSet#TYPE_FORWARD_ONLY} を返します。
	 */
	@Override
	public int getType() throws SQLException {
		ensureOpen();
		return ResultSet.TYPE_FORWARD_ONLY;
	}

	/**
	 * この実装は {@link ResultSet#CONCUR_READ_ONLY} を返します。
	 */
	@Override
	public int getConcurrency() throws SQLException {
		ensureOpen();
		return ResultSet.CONCUR_READ_ONLY;
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public boolean rowUpdated() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("rowUpdated() not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public boolean rowInserted() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("rowInserted() not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public boolean rowDeleted() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("rowDeleted() not supported");
	}

	// ------------------------------------------------------------------------
	// update - columnIndex

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateNull(final int columnIndex) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateNull(int) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateBoolean(int columnIndex, boolean x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateBoolean(int, boolean) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateByte(final int columnIndex, final byte x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateByte(int, byte) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateShort(final int columnIndex, final short x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateShort(int, short) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateInt(final int columnIndex, final int x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateInt(int, int) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateLong(final int columnIndex, final long x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateLong(int, long) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateFloat(final int columnIndex, final float x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateFloat(int, float) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateDouble(final int columnIndex, final double x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateDouble(int, double) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateBigDecimal(final int columnIndex, final BigDecimal x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateBigDecimal(int, BigDecimal) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateString(final int columnIndex, final String x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateString(int, String) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateBytes(final int columnIndex, final byte[] x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateBytes(int, byte[]) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateDate(final int columnIndex, final Date x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateDate(int, Date) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateTime(final int columnIndex, final Time x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateTime(int, Time) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateTimestamp(final int columnIndex, final Timestamp x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateTimestamp(int, Timestamp) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateAsciiStream(final int columnIndex, final InputStream x, final int length) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateAsciiStream(int, InputStream, int) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateBinaryStream(final int columnIndex, final InputStream x, final int length) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateBinaryStream(int, InputStream, int) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateCharacterStream(final int columnIndex, final Reader x, final int length) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateCharacterStream(int, Reader, int) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateObject(final int columnIndex, final Object x, final int scaleOrLength) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateObject(int, Object, int) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateObject(final int columnIndex, final Object x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateObject(int, Object) not supported");
	}

	// ------------------------------------------------------------------------
	// update - columnLabel

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateNull(final String columnLabel) throws SQLException {
		updateNull(findColumn(columnLabel));
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateBoolean(final String columnLabel, final boolean x) throws SQLException {
		updateBoolean(findColumn(columnLabel), x);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateByte(final String columnLabel, final byte x) throws SQLException {
		updateByte(findColumn(columnLabel), x);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateShort(final String columnLabel, final short x) throws SQLException {
		updateShort(findColumn(columnLabel), x);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateInt(final String columnLabel, final int x) throws SQLException {
		updateInt(findColumn(columnLabel), x);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateLong(final String columnLabel, final long x) throws SQLException {
		updateLong(findColumn(columnLabel), x);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateFloat(final String columnLabel, final float x) throws SQLException {
		updateFloat(findColumn(columnLabel), x);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateDouble(final String columnLabel, final double x) throws SQLException {
		updateDouble(findColumn(columnLabel), x);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateBigDecimal(final String columnLabel, final BigDecimal x) throws SQLException {
		updateBigDecimal(findColumn(columnLabel), x);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateString(final String columnLabel, final String x) throws SQLException {
		updateString(findColumn(columnLabel), x);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateBytes(final String columnLabel, final byte[] x) throws SQLException {
		updateBytes(findColumn(columnLabel), x);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateDate(final String columnLabel, final Date x) throws SQLException {
		updateDate(findColumn(columnLabel), x);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateTime(final String columnLabel, final Time x) throws SQLException {
		updateTime(findColumn(columnLabel), x);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateTimestamp(final String columnLabel, final Timestamp x) throws SQLException {
		updateTimestamp(findColumn(columnLabel), x);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateAsciiStream(final String columnLabel, final InputStream x, final int length) throws SQLException {
		updateAsciiStream(findColumn(columnLabel), x, length);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateBinaryStream(final String columnLabel, final InputStream x, final int length) throws SQLException {
		updateBinaryStream(findColumn(columnLabel), x, length);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateCharacterStream(final String columnLabel, final Reader reader, final int length) throws SQLException {
		updateCharacterStream(findColumn(columnLabel), reader, length);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateObject(final String columnLabel, final Object x, final int scaleOrLength) throws SQLException {
		updateObject(findColumn(columnLabel), x, scaleOrLength);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateObject(final String columnLabel, final Object x) throws SQLException {
		updateObject(findColumn(columnLabel), x);
	}

	// ------------------------------------------------------------------------

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void insertRow() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("insertRow() not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateRow() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateRow() not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void deleteRow() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("deleteRow() not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void refreshRow() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("refreshRow() not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void cancelRowUpdates() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("cancelRowUpdates() not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void moveToInsertRow() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("moveToInsertRow() not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void moveToCurrentRow() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("moveToCurrentRow() not supported");
	}

	/**
	 * この実装は <code>null</code> を返します。
	 */
	@Override
	public Statement getStatement() throws SQLException {
		ensureOpen();
		return null;
	}

	// ------------------------------------------------------------------------

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public Object getObject(final int columnIndex, final Map<String, Class<?>> map) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("getObject(int, Map) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public Ref getRef(final int columnIndex) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("getRef(int) not supported");
	}

	@Override
	public Blob getBlob(final int columnIndex) throws SQLException {
		final String s = getString(columnIndex);
		if (s == null) {
			return null;
		}
		return new SerialBlob(s.getBytes());
	}

	@Override
	public Clob getClob(final int columnIndex) throws SQLException {
		final String s = getString(columnIndex);
		if (s == null) {
			return null;
		}
		return new SerialClob(s.toCharArray());
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public Array getArray(final int columnIndex) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("getArray(int) not supported");
	}

	// ------------------------------------------------------------------------

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public Object getObject(final String columnLabel, final Map<String, Class<?>> map) throws SQLException {
		return getObject(findColumn(columnLabel), map);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public Ref getRef(final String columnLabel) throws SQLException {
		return getRef(findColumn(columnLabel));
	}

	@Override
	public Blob getBlob(final String columnLabel) throws SQLException {
		return getBlob(findColumn(columnLabel));
	}

	@Override
	public Clob getClob(final String columnLabel) throws SQLException {
		return getClob(findColumn(columnLabel));
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public Array getArray(final String columnLabel) throws SQLException {
		return getArray(findColumn(columnLabel));
	}

	// ------------------------------------------------------------------------

	@Override
	public Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
		final String s = getString(columnIndex);
		if (s == null) {
			return null;
		}
		try {
			final DateFormat df = DateFormat.getDateInstance();
			df.setCalendar(cal);
			return new Date(df.parse(s).getTime());
		} catch (final ParseException e) {
			throw new SQLException(String.format("Bad format for DATE '%s' in column %d.", s, columnIndex), e);
		}
	}

	@Override
	public Date getDate(final String columnLabel, final Calendar cal) throws SQLException {
		return getDate(findColumn(columnLabel), cal);
	}

	@Override
	public Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
		final String s = getString(columnIndex);
		if (s == null) {
			return null;
		}
		try {
			final DateFormat df = DateFormat.getTimeInstance();
			df.setCalendar(cal);
			return new Time(df.parse(s).getTime());
		} catch (final ParseException e) {
			throw new SQLException(String.format("Bad format for TIME '%s' in column %d.", s, columnIndex), e);
		}
	}

	@Override
	public Time getTime(final String columnLabel, final Calendar cal) throws SQLException {
		return getTime(findColumn(columnLabel), cal);
	}

	@Override
	public Timestamp getTimestamp(final int columnIndex, final Calendar cal) throws SQLException {
		final String s = getString(columnIndex);
		if (s == null) {
			return null;
		}
		try {
			final DateFormat df = DateFormat.getDateTimeInstance();
			df.setCalendar(cal);
			return new Timestamp(df.parse(s).getTime());
		} catch (final ParseException e) {
			throw new SQLException(String.format("Bad format for TIMESTAMP '%s' in column %d.", s, columnIndex), e);
		}
	}

	@Override
	public Timestamp getTimestamp(final String columnLabel, final Calendar cal) throws SQLException {
		return getTimestamp(findColumn(columnLabel), cal);
	}

	// ------------------------------------------------------------------------

	@Override
	public URL getURL(final int columnIndex) throws SQLException {
		final String s = getString(columnIndex);
		if (s == null) {
			return null;
		}
		try {
			return new URL(s);
		} catch (final MalformedURLException e) {
			throw new SQLException(String.format("Bad format for URL '%s' in column %d.", s, columnIndex), e);
		}
	}

	@Override
	public URL getURL(final String columnLabel) throws SQLException {
		return getURL(findColumn(columnLabel));
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateRef(final int columnIndex, final Ref x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateRef(int, Ref) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateRef(final String columnLabel, final Ref x) throws SQLException {
		updateRef(findColumn(columnLabel), x);
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateBlob(final int columnIndex, final Blob x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateBlob(int, Blob) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateBlob(final String columnLabel, final Blob x) throws SQLException {
		updateBlob(findColumn(columnLabel), x);
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateClob(final int columnIndex, final Clob x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateBlob(int, Clob) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateClob(final String columnLabel, final Clob x) throws SQLException {
		updateClob(findColumn(columnLabel), x);
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateArray(final int columnIndex, final Array x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateArray(int, Array) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateArray(final String columnLabel, final Array x) throws SQLException {
		updateArray(findColumn(columnLabel), x);
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public RowId getRowId(final int columnIndex) throws SQLException {
		throw new SQLFeatureNotSupportedException("getRowId(int) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public RowId getRowId(final String columnLabel) throws SQLException {
		return getRowId(findColumn(columnLabel));
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateRowId(final int columnIndex, final RowId x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateRowId(int, RowId) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateRowId(final String columnLabel, final RowId x) throws SQLException {
		updateRowId(findColumn(columnLabel), x);
	}

	/**
	 * この実装は {@link ResultSet#HOLD_CURSORS_OVER_COMMIT} を返します。
	 */
	@Override
	public int getHoldability() throws SQLException {
		ensureOpen();
		return ResultSet.HOLD_CURSORS_OVER_COMMIT;
	}

	@Override
	public boolean isClosed() {
		return reader == null;
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateNString(final int columnIndex, final String string) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateNString(int, String) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateNString(final String columnLabel, final String string) throws SQLException {
		updateNString(findColumn(columnLabel), string);
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateNClob(final int columnIndex, final NClob clob) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateNClob(int, NClob) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateNClob(final String columnLabel, final NClob clob) throws SQLException {
		updateNClob(findColumn(columnLabel), clob);
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public NClob getNClob(final int columnIndex) throws SQLException {
		throw new SQLFeatureNotSupportedException("getNClob(int) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public NClob getNClob(final String columnLabel) throws SQLException {
		return getNClob(findColumn(columnLabel));
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public SQLXML getSQLXML(final int columnIndex) throws SQLException {
		throw new SQLFeatureNotSupportedException("getSQLXML(int) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public SQLXML getSQLXML(final String columnLabel) throws SQLException {
		return getSQLXML(findColumn(columnLabel));
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateSQLXML(final int columnIndex, final SQLXML xmlObject) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateSQLXML(int, SQLXML) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateSQLXML(final String columnLabel, final SQLXML xmlObject) throws SQLException {
		updateSQLXML(findColumn(columnLabel), xmlObject);
	}

	@Override
	public String getNString(final int columnIndex) throws SQLException {
		return getString(columnIndex);
	}

	@Override
	public String getNString(final String columnLabel) throws SQLException {
		return getNString(findColumn(columnLabel));
	}

	@Override
	public Reader getNCharacterStream(final int columnIndex) throws SQLException {
		return getCharacterStream(columnIndex);
	}

	@Override
	public Reader getNCharacterStream(final String columnLabel) throws SQLException {
		return getNCharacterStream(findColumn(columnLabel));
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateNCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateNCharacterStream(int, Reader, long) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateNCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
		updateNCharacterStream(findColumn(columnLabel), reader, length);
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateAsciiStream(final int columnIndex, final InputStream x, final long length) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateAsciiStream(int, InputStream, long) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateBinaryStream(final int columnIndex, final InputStream x, final long length) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateBinaryStream(int, InputStream, long) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateCharacterStream(int, Reader, long) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateAsciiStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
		updateAsciiStream(findColumn(columnLabel), x, length);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateBinaryStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
		updateBinaryStream(findColumn(columnLabel), x, length);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
		updateCharacterStream(findColumn(columnLabel), reader, length);
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateBlob(final int columnIndex, final InputStream inputStream, final long length) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateBlob(int, InputStream, long) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateBlob(final String columnLabel, final InputStream inputStream, final long length) throws SQLException {
		updateBlob(findColumn(columnLabel), inputStream, length);
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateClob(final int columnIndex, final Reader reader, final long length) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateClob(int, Reader, long) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
		updateClob(findColumn(columnLabel), reader, length);
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateNClob(final int columnIndex, final Reader reader, final long length) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateNClob(int, Reader, long) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateNClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
		updateNClob(findColumn(columnLabel), reader, length);
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateNCharacterStream(final int columnIndex, final Reader x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateNCharacterStream(int, Reader) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateNCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
		updateNCharacterStream(findColumn(columnLabel), reader);
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateAsciiStream(final int columnIndex, final InputStream x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateAsciiStream(int, InputStream) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateBinaryStream(final int columnIndex, final InputStream x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateBinaryStream(int, InputStream) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateCharacterStream(final int columnIndex, final Reader x) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateCharacterStream(int, Reader) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateAsciiStream(final String columnLabel, final InputStream x) throws SQLException {
		updateAsciiStream(findColumn(columnLabel), x);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateBinaryStream(final String columnLabel, final InputStream x) throws SQLException {
		updateBinaryStream(findColumn(columnLabel), x);
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
		updateCharacterStream(findColumn(columnLabel), reader);
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateBlob(final int columnIndex, final InputStream inputStream) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateBlob(int, InputStream) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateBlob(final String columnLabel, final InputStream inputStream) throws SQLException {
		updateBlob(findColumn(columnLabel), inputStream);
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateClob(final int columnIndex, final Reader reader) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateClob(int, Reader) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateClob(final String columnLabel, final Reader reader) throws SQLException {
		updateClob(findColumn(columnLabel), reader);
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateNClob(final int columnIndex, final Reader reader) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("updateNClob(int, Reader) not supported");
	}

	/**
	 * この実装は {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public void updateNClob(final String columnLabel, final Reader reader) throws SQLException {
		updateNClob(findColumn(columnLabel), reader);
	}

	// ------------------------------------------------------------------------

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public <T> T unwrap(final Class<T> iface) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("unwrap(Class) not supported");
	}

	/**
	 * この実装は常に {@link SQLFeatureNotSupportedException} をスローします。
	 */
	@Override
	public boolean isWrapperFor(final Class<?> iface) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("isWrapperFor(Class) not supported");
	}

}