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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.filters.CsvValueOrExpression;
import com.orangesignal.csv.filters.SimpleCsvValueFilter;
import com.orangesignal.csv.handlers.StringArrayListHandler;

/**
 * {@link StringArrayListHandler} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class StringArrayListHandlerTest {

	@Test
	public void testLoad() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '\\');
		cfg.setNullString("NULL");
		cfg.setBreakString("\n");
		cfg.setIgnoreTrailingWhitespaces(true);
		cfg.setIgnoreLeadingWhitespaces(true);
		cfg.setIgnoreEmptyLines(true);
		cfg.setIgnoreLinePatterns(Pattern.compile("^#.*$"));

		final CsvReader reader = new CsvReader(new StringReader("# text/tab-separated-values   \r\n aaa , \"b\r\nb\\\\b\" , \"c\\\"cc\" \r\n zzz , yyy , NULL \r\n# Copyright 2009 OrangeSignal.   "), cfg);
		try {
			final List<String[]> list = new StringArrayListHandler().load(reader);
			assertThat(list.size(), is(2));

			final String[] values1 = list.get(0);
			assertThat(values1.length, is(3));
			assertThat(values1[0], is("aaa"));
			assertThat(values1[1], is("b\nb\\\\b"));
			assertThat(values1[2], is("c\"cc"));

			final String[] values2 = list.get(1);
			assertThat(values2.length, is(3));
			assertThat(values2[0], is("zzz"));
			assertThat(values2[1], is("yyy"));
			assertNull(values2[2]);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoadFilter() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '\\');
		cfg.setNullString("NULL");
		cfg.setBreakString("\n");
		cfg.setIgnoreTrailingWhitespaces(true);
		cfg.setIgnoreLeadingWhitespaces(true);
		cfg.setIgnoreEmptyLines(true);
		cfg.setIgnoreLinePatterns(Pattern.compile("^#.*$"));

		final CsvReader reader = new CsvReader(new StringReader("aaa,bbb,ccc \r\n ddd,eee,fff \r\n ggg,hhh,iii \r\n zzz,yyy,NULL"), cfg);
		try {
			final List<String[]> list = new StringArrayListHandler()
				.filter(new SimpleCsvValueFilter(new CsvValueOrExpression())
						.eq(0, "ddd")
						.eq(1, "yyy")
					)
				.load(reader);

			assertThat(list.size(), is(2));

			final String[] values1 = list.get(0);
			assertThat(values1.length, is(3));
			assertThat(values1[0], is("ddd"));
			assertThat(values1[1], is("eee"));
			assertThat(values1[2], is("fff"));

			final String[] values2 = list.get(1);
			assertThat(values2.length, is(3));
			assertThat(values2[0], is("zzz"));
			assertThat(values2[1], is("yyy"));
			assertNull(values2[2]);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoadOffsetLimit() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '\\');
		cfg.setNullString("NULL");
		cfg.setBreakString("\n");
		cfg.setIgnoreTrailingWhitespaces(true);
		cfg.setIgnoreLeadingWhitespaces(true);
		cfg.setIgnoreEmptyLines(true);
		cfg.setIgnoreLinePatterns(Pattern.compile("^#.*$"));

		final CsvReader reader = new CsvReader(new StringReader("# text/tab-separated-values   \r\n aaa , \"b\r\nb\\\\b\" , \"c\\\"cc\" \r\n zzz , yyy , NULL \r\n# Copyright 2009 OrangeSignal.   "), cfg);
		try {
			final List<String[]> list = new StringArrayListHandler().offset(1).limit(1).load(reader);
			assertThat(list.size(), is(1));

			final String[] values2 = list.get(0);
			assertThat(values2.length, is(3));
			assertThat(values2[0], is("zzz"));
			assertThat(values2[1], is("yyy"));
			assertNull(values2[2]);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testLoadFilterOffsetLimit() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '\\');
		cfg.setNullString("NULL");
		cfg.setBreakString("\n");
		cfg.setIgnoreTrailingWhitespaces(true);
		cfg.setIgnoreLeadingWhitespaces(true);
		cfg.setIgnoreEmptyLines(true);
		cfg.setIgnoreLinePatterns(Pattern.compile("^#.*$"));

		final CsvReader reader = new CsvReader(new StringReader("aaa,bbb,ccc \r\n ddd,eee,fff \r\n ggg,hhh,iii \r\n zzz,yyy,NULL"), cfg);
		try {
			final List<String[]> list = new StringArrayListHandler()
				.filter(new SimpleCsvValueFilter(new CsvValueOrExpression()).eq(0, "ddd").eq(1, "yyy"))
				.offset(1)
				.limit(1)
				.load(reader);

			assertThat(list.size(), is(1));

			final String[] values2 = list.get(0);
			assertThat(values2.length, is(3));
			assertThat(values2[0], is("zzz"));
			assertThat(values2[1], is("yyy"));
			assertNull(values2[2]);
		} finally {
			reader.close();
		}
	}

	@Test
	public void testSave() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '\\');
		cfg.setNullString("NULL");
		cfg.setLineSeparator("\r\n");

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			final List<String[]> list = new ArrayList<String[]>(2);
			list.add(new String[]{ "aaa", "b\nb\\\\b", "c\"cc" });
			list.add(new String[]{ "zzz", "yyy", null });
			new StringArrayListHandler().save(list, writer);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("\"aaa\",\"b\nb\\\\b\",\"c\\\"cc\"\r\n\"zzz\",\"yyy\",NULL\r\n"));
		} finally {
			writer.close();
		}
	}

	@Test
	public void testSaveFilter() throws IOException {
		final CsvConfig cfg = new CsvConfig(',', '"', '\\');
		cfg.setNullString("NULL");
		cfg.setLineSeparator("\r\n");

		final StringWriter sw = new StringWriter();
		final CsvWriter writer = new CsvWriter(sw, cfg);
		try {
			final List<String[]> list = new ArrayList<String[]>(2);
			list.add(new String[]{ "aaa", "b\nb\\\\b", "c\"cc" });
			list.add(new String[]{ "zzz", "yyy", null });
			new StringArrayListHandler()
				.filter(new SimpleCsvValueFilter().ne(0, "aaa"))
				.save(list, writer);
			writer.flush();
			assertThat(sw.getBuffer().toString(), is("\"zzz\",\"yyy\",NULL\r\n"));
		} finally {
			writer.close();
		}
	}

}
