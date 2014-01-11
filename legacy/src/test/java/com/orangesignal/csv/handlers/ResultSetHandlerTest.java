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
import com.orangesignal.csv.handlers.ResultSetHandler;

/**
 * {@link ResultSetHandler} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
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
