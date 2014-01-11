/*
 * Copyright (c) 2009 OrangeSignal.com All rights reserved.
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
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvResultSet;

/**
 * {@link CsvResultSet} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class CsvResultSetTest {

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
	public void testCsvResultSet() throws IOException {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("field_name\r\nxxx")));
		rs.close();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCsvResultSetIllegalArgumentException() throws IOException {
		new CsvResultSet(null);
	}

	@Test(expected = IOException.class)
	public void testCsvResultSetIOException() throws IOException {
		new CsvResultSet(new CsvReader(new StringReader(""), cfg));
	}

	@Test(expected = SQLException.class)
	public void testEnsureOpen() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader(
				"code, market, name, price, date, time, datetime, active \r\n" +
				"9999, T1, OrangeSignal CSV test, NULL, 2009-01-01, 12:00:00, 2009-01-01 12:00:00, 0 \r\n" +
				"9999, T1, OrangeSignal CSV test, 500.05, 2009-01-01, 12:00:00, 2009-01-01 12:00:00, 1 \r\n"
			), cfg));
		rs.close();
		rs.next();
	}

	@Test
	public void test() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader(
				"code, market, name, price, date, time, datetime, active \r\n" +
				"9999, T1, OrangeSignal CSV test, NULL, 2009-01-01, 12:00:00, 2009-01-01 12:00:00, 0 \r\n" +
				"9999, T1, OrangeSignal CSV test, 500.05, 2009-01-01, 12:00:00, 2009-01-01 12:00:00, 1 \r\n"
			), cfg));
		try {
			assertThat(rs.getStatement(), nullValue());
			assertThat(rs.isClosed(), is(false));
			assertThat(rs.getFetchDirection(), is(ResultSet.FETCH_FORWARD));
			assertThat(rs.getFetchSize(), is(0));
			assertThat(rs.getType(), is(ResultSet.TYPE_FORWARD_ONLY));
			assertThat(rs.getConcurrency(), is(ResultSet.CONCUR_READ_ONLY));
			assertThat(rs.getHoldability(), is(ResultSet.HOLD_CURSORS_OVER_COMMIT));

			assertThat(rs.next(), is(true));
			assertThat(rs.getRow(), is(1));

			assertThat(rs.getString(1), is("9999"));
			assertThat(rs.wasNull(), is(false));
			assertThat(rs.getString("code"), is("9999"));
			assertThat(rs.wasNull(), is(false));

			assertThat(rs.getShort(1), is((short) 9999));
			assertThat(rs.getShort("code"), is((short) 9999));
			assertThat(rs.getInt(1), is(9999));
			assertThat(rs.getInt("code"), is(9999));

			assertThat(rs.getLong(4), is(0L));
			assertThat(rs.wasNull(), is(true));
			assertThat(rs.getLong("price"), is(0L));
			assertThat(rs.wasNull(), is(true));

			assertThat(rs.getBoolean(8), is(false));
			assertThat(rs.getBoolean("active"), is(false));

			assertThat(rs.getString(4), nullValue());
			assertThat(rs.wasNull(), is(true));
//			assertThat(rs.getString(1), is("aaa"));
//			assertThat(rs.getString("col1"), is("aaa"));

		} finally {
			rs.close();
			assertThat(rs.isClosed(), is(true));
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testGetBigDecimalIntInt() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getBigDecimal(1, 0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testGetUnicodeStreamInt() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getUnicodeStream(1);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testGetBigDecimalStringInt() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getBigDecimal("id", 0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testGetUnicodeStreamString() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getUnicodeStream("id");
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testGetCursorName() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getCursorName();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testIsBeforeFirst() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.isBeforeFirst();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testIsAfterLast() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.isAfterLast();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testIsFirst() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.isFirst();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testIsLast() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.isLast();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testBeforeFirst() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.beforeFirst();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testAfterLast() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.afterLast();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testFirst() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.first();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testLast() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.last();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testAbsolute() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.absolute(0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testRelative() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.relative(0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testPrevious() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.previous();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testSetFetchDirection() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.setFetchDirection(ResultSet.FETCH_FORWARD);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testSetFetchSize() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.setFetchSize(0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testRowUpdated() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.rowUpdated();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testRowInserted() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.rowInserted();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testRowDeleted() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.rowDeleted();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateNullInt() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNull(1);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateBooleanIntBoolean() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBoolean(1, false);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateByteIntByte() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateByte(1, (byte) 0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateShortIntShort() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateShort(1, (short) 0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateIntIntInt() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateInt(1, 0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateLongIntLong() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateLong(1, 0L);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateFloatIntFloat() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateFloat(1, 0F);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateDoubleIntDouble() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateDouble(1, 0D);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateBigDecimalIntBigDecimal() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBigDecimal(1, null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateStringIntString() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateString(1, null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateBytesIntByteArray() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBytes(1, null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateDateIntDate() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateDate(1, null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateTimeIntTime() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateTime(1, null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateTimestampIntTimestamp() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateTimestamp(1, null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateAsciiStreamIntInputStreamInt() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateAsciiStream(1, null, 0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateBinaryStreamIntInputStreamInt() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBinaryStream(1, null, 0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateCharacterStreamIntReaderInt() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateCharacterStream(1, null, 0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateObjectIntObjectInt() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateObject(1, null, 0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateObjectIntObject() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateObject(1, null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateNullString() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNull("id");
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateBooleanStringBoolean() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBoolean("id", false);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateByteStringByte() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateByte("id", (byte) 0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateShortStringShort() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateShort("id", (short) 0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateIntStringInt() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateInt("id", 0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateLongStringLong() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateLong("id", 0L);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateFloatStringFloat() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateFloat("id", 0F);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateDoubleStringDouble() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateDouble("id", 0D);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateBigDecimalStringBigDecimal() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBigDecimal("id", null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateStringStringString() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateString("id", null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateBytesStringByteArray() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBytes("id", null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateDateStringDate() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateDate("id", null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateTimeStringTime() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateTime("id", null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateTimestampStringTimestamp() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateTimestamp("id", null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateAsciiStreamStringInputStreamInt() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateAsciiStream("id", null, 0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateBinaryStreamStringInputStreamInt() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBinaryStream("id", null, 0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateCharacterStreamStringReaderInt() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateCharacterStream("id", new StringReader(""), 0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateObjectStringObjectInt() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateObject("id", null, 0);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateObjectStringObject() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateObject("id", null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testInsertRow() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.insertRow();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateRow() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateRow();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testDeleteRow() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.deleteRow();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testRefreshRow() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.refreshRow();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testCancelRowUpdates() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.cancelRowUpdates();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testMoveToInsertRow() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.moveToInsertRow();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testMoveToCurrentRow() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.moveToCurrentRow();
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testGetObjectIntMapOfStringClassOfQ() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getObject(1, null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testGetRefInt() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getRef(1);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testGetArrayInt() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getArray(1);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testGetObjectStringMapOfStringClassOfQ() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getObject("id", null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testGetRefString() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getRef("id");
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateRefIntRef() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateRef(1, null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateRefStringRef() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateRef("id", null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateBlobIntBlob() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			final Blob blob = null;
			rs.updateBlob(1, blob);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateBlobStringBlob() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			final Blob blob = null;
			rs.updateBlob("id", blob);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateClobIntClob() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			final Clob clob = null;
			rs.updateClob(1, clob);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateClobStringClob() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			final Clob clob = null;
			rs.updateClob("id", clob);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateArrayIntArray() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateArray(1, null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateArrayStringArray() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateArray("id", null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testGetRowIdInt() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getRowId(1);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testGetRowIdString() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getRowId("id");
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateRowIdIntRowId() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateRowId(1, null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateRowIdStringRowId() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateRowId("id", null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateNStringIntString() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNString(1, null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateNStringStringString() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNString("id", null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateNClobIntNClob() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			final NClob nclob = null;
			rs.updateNClob(1, nclob);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateNClobStringNClob() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			final NClob nclob = null;
			rs.updateNClob("id", nclob);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testGetSQLXMLInt() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getSQLXML(1);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testGetSQLXMLString() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.getSQLXML("id");
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateSQLXMLIntSQLXML() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateSQLXML(1, null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateSQLXMLStringSQLXML() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateSQLXML("id", null);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateNCharacterStreamIntReaderLong() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNCharacterStream(1, new StringReader(""), 0L);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateNCharacterStreamStringReaderLong() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNCharacterStream("id", new StringReader(""), 0L);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateAsciiStreamIntInputStreamLong() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateAsciiStream(1, new ByteArrayInputStream("".getBytes()), 0L);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateBinaryStreamIntInputStreamLong() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBinaryStream(1, new ByteArrayInputStream("".getBytes()), 0L);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateCharacterStreamIntReaderLong() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateCharacterStream(1, new StringReader(""), 0L);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateAsciiStreamStringInputStreamLong() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateAsciiStream("id", new ByteArrayInputStream("".getBytes()), 0L);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateBinaryStreamStringInputStreamLong() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBinaryStream("id", new ByteArrayInputStream("".getBytes()), 0L);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateCharacterStreamStringReaderLong() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateCharacterStream("id", new StringReader(""), 0L);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateBlobIntInputStreamLong() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBlob(1, new ByteArrayInputStream("".getBytes()), 0L);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateBlobStringInputStreamLong() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBlob("id", new ByteArrayInputStream("".getBytes()), 0L);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateClobIntReaderLong() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateClob(1, new StringReader(""), 0L);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateClobStringReaderLong() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateClob("id", new StringReader(""), 0L);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateNClobIntReaderLong() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNClob(1, new StringReader(""), 0L);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateNClobStringReaderLong() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNClob("id", new StringReader(""), 0L);
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateNCharacterStreamIntReader() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNCharacterStream(1, new StringReader(""));
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateNCharacterStreamStringReader() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNCharacterStream("id", new StringReader(""));
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateAsciiStreamIntInputStream() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateAsciiStream(1, new ByteArrayInputStream("".getBytes()));
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateBinaryStreamIntInputStream() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBinaryStream(1, new ByteArrayInputStream("".getBytes()));
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateCharacterStreamIntReader() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateCharacterStream(1, new StringReader(""));
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateAsciiStreamStringInputStream() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateAsciiStream("id", new ByteArrayInputStream("".getBytes()));
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateBinaryStreamStringInputStream() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBinaryStream("id", new ByteArrayInputStream("".getBytes()));
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateCharacterStreamStringReader() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateCharacterStream("id", new StringReader(""));
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateBlobIntInputStream() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBlob(1, new ByteArrayInputStream("".getBytes()));
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateBlobStringInputStream() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateBlob("id", new ByteArrayInputStream("".getBytes()));
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateClobIntReader() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateClob(1, new StringReader(""));
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateClobStringReader() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateClob("id", new StringReader(""));
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateNClobIntReader() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNClob(1, new StringReader(""));
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUpdateNClobStringReader() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.updateNClob("id", new StringReader(""));
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testUnwrap() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.unwrap(this.getClass());
		} finally {
			rs.close();
		}
	}

	@Test(expected = SQLFeatureNotSupportedException.class)
	public void testIsWrapperFor() throws Exception {
		final CsvResultSet rs = new CsvResultSet(new CsvReader(new StringReader("id\r\nNULL"), cfg));
		try {
			rs.isWrapperFor(this.getClass());
		} finally {
			rs.close();
		}
	}

}
