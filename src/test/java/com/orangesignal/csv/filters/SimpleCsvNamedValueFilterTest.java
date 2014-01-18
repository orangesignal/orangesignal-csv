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

package com.orangesignal.csv.filters;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import com.orangesignal.csv.filters.CsvNamedValueAndExpression;
import com.orangesignal.csv.filters.CsvNamedValueFilter;
import com.orangesignal.csv.filters.CsvNamedValueOrExpression;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;

/**
 * {@link SimpleCsvNamedValueFilter} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class SimpleCsvNamedValueFilterTest {

	@Test
	public void testSimpleCsvNamedValueFilter() {
		assertFalse(new SimpleCsvNamedValueFilter()
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } })
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } })
				.accept(null, null)
			);
		assertFalse(new SimpleCsvNamedValueFilter()
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } })
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } })
				.accept(null, null)
			);
		assertFalse(new SimpleCsvNamedValueFilter()
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } })
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } })
				.accept(null, null)
			);
		assertTrue(new SimpleCsvNamedValueFilter()
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } })
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } })
				.accept(null, null)
			);
		
		assertFalse(new SimpleCsvNamedValueFilter(new CsvNamedValueAndExpression())
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } })
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } })
				.accept(null, null)
			);
		assertFalse(new SimpleCsvNamedValueFilter(new CsvNamedValueAndExpression())
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } })
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } })
				.accept(null, null)
			);
		assertFalse(new SimpleCsvNamedValueFilter(new CsvNamedValueAndExpression())
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } })
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } })
				.accept(null, null)
			);
		assertTrue(new SimpleCsvNamedValueFilter(new CsvNamedValueAndExpression())
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } })
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } })
				.accept(null, null)
			);
		
		assertFalse(new SimpleCsvNamedValueFilter(new CsvNamedValueOrExpression())
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } })
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } })
				.accept(null, null)
			);
		assertTrue(new SimpleCsvNamedValueFilter(new CsvNamedValueOrExpression())
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } })
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } })
				.accept(null, null)
			);
		assertTrue(new SimpleCsvNamedValueFilter(new CsvNamedValueOrExpression())
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } })
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } })
				.accept(null, null)
			);
		assertTrue(new SimpleCsvNamedValueFilter(new CsvNamedValueOrExpression())
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } })
				.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } })
				.accept(null, null)
			);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSimpleCsvNamedValueFilterIllegalArgumentException() {
		new SimpleCsvNamedValueFilter(null);
	}

	@Test
	public void testIsNull() {
		assertTrue(new SimpleCsvNamedValueFilter().isNull(0).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().isNull(1).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().isNull(2).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));

		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2", "col3", "col4" });
		final List<String> values = Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" });
		assertTrue(new SimpleCsvNamedValueFilter().isNull("col0").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().isNull("col1").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().isNull("col2").accept(header, values));
	}

	@Test
	public void testNotNull() {
		assertFalse(new SimpleCsvNamedValueFilter().isNotNull(0).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().isNotNull(1).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().isNotNull(2).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));

		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2", "col3", "col4" });
		final List<String> values = Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" });
		assertFalse(new SimpleCsvNamedValueFilter().isNotNull("col0").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().isNotNull("col1").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().isNotNull("col2").accept(header, values));
	}

	@Test
	public void testIsEmpty() {
		assertTrue(new SimpleCsvNamedValueFilter().isEmpty(0).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().isEmpty(1).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().isEmpty(2).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().isEmpty(0).accept(null, Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().isEmpty(1).accept(null, Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().isEmpty(2).accept(null, Arrays.asList(new String[]{ "", "aaa", "bbb" })));

		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2" });
		assertTrue(new SimpleCsvNamedValueFilter().isEmpty("col0").accept(header, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().isEmpty("col1").accept(header, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().isEmpty("col2").accept(header, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().isEmpty("col0").accept(header, Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().isEmpty("col1").accept(header, Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().isEmpty("col2").accept(header, Arrays.asList(new String[]{ "", "aaa", "bbb" })));
	}

	@Test
	public void testIsNotEmpty() {
		assertFalse(new SimpleCsvNamedValueFilter().isNotEmpty(0).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().isNotEmpty(1).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().isNotEmpty(2).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().isNotEmpty(0).accept(null, Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().isNotEmpty(1).accept(null, Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().isNotEmpty(2).accept(null, Arrays.asList(new String[]{ "", "aaa", "bbb" })));

		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2" });
		assertFalse(new SimpleCsvNamedValueFilter().isNotEmpty("col0").accept(header, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().isNotEmpty("col1").accept(header, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().isNotEmpty("col2").accept(header, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().isNotEmpty("col0").accept(header, Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().isNotEmpty("col1").accept(header, Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().isNotEmpty("col2").accept(header, Arrays.asList(new String[]{ "", "aaa", "bbb" })));
	}

	@Test
	public void testEq() {
		assertFalse(new SimpleCsvNamedValueFilter().eq(0, "aaa").accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().eq(1, "aaa").accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().eq(2, "aaa").accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().eq(0, "aaa", false).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().eq(1, "aaa", false).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().eq(2, "aaa", false).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().eq(0, "AAA", true).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().eq(1, "AAA", true).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().eq(2, "AAA", true).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));

		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2", "col3", "col4" });
		final List<String> values = Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" });
		assertFalse(new SimpleCsvNamedValueFilter().eq("col0", "x001").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().eq("col1", "x001").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().eq("col2", "x001").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().eq("col0", "x001", false).accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().eq("col1", "x001", false).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().eq("col2", "x001", false).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().eq("col0", "X001", true).accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().eq("col1", "X001", true).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().eq("col2", "X001", true).accept(header, values));
	}

	@Test
	public void testNe() {
		assertTrue(new SimpleCsvNamedValueFilter().ne(0, "aaa").accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().ne(1, "aaa").accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().ne(2, "aaa").accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().ne(0, "aaa", false).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().ne(1, "aaa", false).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().ne(2, "aaa", false).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().ne(0, "AAA", true).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().ne(1, "AAA", true).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().ne(2, "AAA", true).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));

		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2", "col3", "col4" });
		final List<String> values = Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" });
		assertTrue(new SimpleCsvNamedValueFilter().ne("col0", "x001").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().ne("col1", "x001").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().ne("col2", "x001").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().ne("col0", "x001", false).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().ne("col1", "x001", false).accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().ne("col2", "x001", false).accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().ne("col0", "X001", true).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().ne("col1", "X001", true).accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().ne("col2", "X001", true).accept(header, values));
	}

	@Test
	public void testIn() {
		assertFalse(new SimpleCsvNamedValueFilter().in(0, "a", "aa", "aaa").accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().in(1, "a", "aa", "aaa").accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().in(2, "a", "aa", "aaa").accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().in(0, new String[]{ "a", "aa", "aaa" }, false).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().in(1, new String[]{ "a", "aa", "aaa" }, false).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().in(2, new String[]{ "a", "aa", "aaa" }, false).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().in(0, new String[]{ "A", "AA", "AAA" }, true).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().in(1, new String[]{ "A", "AA", "AAA" }, true).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().in(2, new String[]{ "A", "AA", "AAA" }, true).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));

		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2", "col3", "col4" });
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb", "ccc", "ddd" });
		assertFalse(new SimpleCsvNamedValueFilter().in("col0", "a", "aa", "aaa").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().in("col1", "a", "aa", "aaa").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().in("col2", "a", "aa", "aaa").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().in("col0", new String[]{ "a", "aa", "aaa" }, false).accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().in("col1", new String[]{ "a", "aa", "aaa" }, false).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().in("col2", new String[]{ "a", "aa", "aaa" }, false).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().in("col0", new String[]{ "A", "AA", "AAA" }, true).accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().in("col1", new String[]{ "A", "AA", "AAA" }, true).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().in("col2", new String[]{ "A", "AA", "AAA" }, true).accept(header, values));
	}

	@Test
	public void testNotIn() {
		assertTrue(new SimpleCsvNamedValueFilter().notIn(0, "a", "aa", "aaa").accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().notIn(1, "a", "aa", "aaa").accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().notIn(2, "a", "aa", "aaa").accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().notIn(0, new String[]{ "a", "aa", "aaa" }, false).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().notIn(1, new String[]{ "a", "aa", "aaa" }, false).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().notIn(2, new String[]{ "a", "aa", "aaa" }, false).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().notIn(0, new String[]{ "A", "AA", "AAA" }, true).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvNamedValueFilter().notIn(1, new String[]{ "A", "AA", "AAA" }, true).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvNamedValueFilter().notIn(2, new String[]{ "A", "AA", "AAA" }, true).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));

		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2", "col3", "col4" });
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb", "ccc", "ddd" });
		assertTrue(new SimpleCsvNamedValueFilter().notIn("col0", "a", "aa", "aaa").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().notIn("col1", "a", "aa", "aaa").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().notIn("col2", "a", "aa", "aaa").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().notIn("col0", new String[]{ "a", "aa", "aaa" }, false).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().notIn("col1", new String[]{ "a", "aa", "aaa" }, false).accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().notIn("col2", new String[]{ "a", "aa", "aaa" }, false).accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().notIn("col0", new String[]{ "A", "AA", "AAA" }, true).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().notIn("col1", new String[]{ "A", "AA", "AAA" }, true).accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().notIn("col2", new String[]{ "A", "AA", "AAA" }, true).accept(header, values));
	}

	@Test
	public void testRegex() {
		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2" });
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });

		assertFalse(new SimpleCsvNamedValueFilter().regex(0, "^[a]+$").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().regex(1, "^[a]+$").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().regex(2, "^[a]+$").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().regex(0, "^[a]+$", false).accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().regex(1, "^[a]+$", false).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().regex(2, "^[a]+$", false).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().regex(0, "^[A]+$", true).accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().regex(1, "^[A]+$", true).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().regex(2, "^[A]+$", true).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().regex(0, "^[A]+$", Pattern.CASE_INSENSITIVE).accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().regex(1, "^[A]+$", Pattern.CASE_INSENSITIVE).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().regex(2, "^[A]+$", Pattern.CASE_INSENSITIVE).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().regex(0, Pattern.compile("^[a]+$")).accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().regex(1, Pattern.compile("^[a]+$")).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().regex(2, Pattern.compile("^[a]+$")).accept(header, values));

		assertFalse(new SimpleCsvNamedValueFilter().regex("col0", "^[a]+$").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().regex("col1", "^[a]+$").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().regex("col2", "^[a]+$").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().regex("col0", "^[a]+$", false).accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().regex("col1", "^[a]+$", false).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().regex("col2", "^[a]+$", false).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().regex("col0", "^[A]+$", true).accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().regex("col1", "^[A]+$", true).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().regex("col2", "^[A]+$", true).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().regex("col0", "^[A]+$", Pattern.CASE_INSENSITIVE).accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().regex("col1", "^[A]+$", Pattern.CASE_INSENSITIVE).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().regex("col2", "^[A]+$", Pattern.CASE_INSENSITIVE).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().regex("col0", Pattern.compile("^[a]+$")).accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().regex("col1", Pattern.compile("^[a]+$")).accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().regex("col2", Pattern.compile("^[a]+$")).accept(header, values));
	}

	@Test
	public void testGt() {
		assertFalse(new SimpleCsvNamedValueFilter().gt(0, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new SimpleCsvNamedValueFilter().gt(1, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new SimpleCsvNamedValueFilter().gt(2, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertTrue(new SimpleCsvNamedValueFilter().gt(3, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));

		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2", "col3", "col4" });
		final List<String> values = Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" });
		assertFalse(new SimpleCsvNamedValueFilter().gt("col0", "x002").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().gt("col1", "x002").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().gt("col2", "x002").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().gt("col3", "x002").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().gt("col4", "x002").accept(header, values));
	}

	@Test
	public void testLt() {
		assertFalse(new SimpleCsvNamedValueFilter().lt(0, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertTrue(new SimpleCsvNamedValueFilter().lt(1, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new SimpleCsvNamedValueFilter().lt(2, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new SimpleCsvNamedValueFilter().lt(3, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));

		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2", "col3", "col4" });
		final List<String> values = Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" });
		assertFalse(new SimpleCsvNamedValueFilter().lt("col0", "x002").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().lt("col1", "x002").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().lt("col2", "x002").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().lt("col3", "x002").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().lt("col4", "x002").accept(header, values));
	}

	@Test
	public void testGe() {
		assertFalse(new SimpleCsvNamedValueFilter().ge(0, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new SimpleCsvNamedValueFilter().ge(1, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertTrue(new SimpleCsvNamedValueFilter().ge(2, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertTrue(new SimpleCsvNamedValueFilter().ge(3, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));

		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2", "col3", "col4" });
		final List<String> values = Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" });
		assertFalse(new SimpleCsvNamedValueFilter().ge("col0", "x002").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().ge("col1", "x002").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().ge("col2", "x002").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().ge("col3", "x002").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().ge("col4", "x002").accept(header, values));
	}

	@Test
	public void testLe() {
		assertFalse(new SimpleCsvNamedValueFilter().le(0, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertTrue(new SimpleCsvNamedValueFilter().le(1, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertTrue(new SimpleCsvNamedValueFilter().le(2, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new SimpleCsvNamedValueFilter().le(3, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));

		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2", "col3", "col4" });
		final List<String> values = Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" });
		assertFalse(new SimpleCsvNamedValueFilter().le("col0", "x002").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().le("col1", "x002").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().le("col2", "x002").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().le("col3", "x002").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().le("col4", "x002").accept(header, values));
	}

	@Test
	public void testBetween() {
		assertFalse(new SimpleCsvNamedValueFilter().between(0, "x002", "x003").accept(null, Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertFalse(new SimpleCsvNamedValueFilter().between(1, "x002", "x003").accept(null, Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertTrue(new SimpleCsvNamedValueFilter().between(2, "x002", "x003").accept(null, Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertTrue(new SimpleCsvNamedValueFilter().between(3, "x002", "x003").accept(null, Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertFalse(new SimpleCsvNamedValueFilter().between(4, "x002", "x003").accept(null, Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));

		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2", "col3", "col4" });
		final List<String> values = Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" });
		assertFalse(new SimpleCsvNamedValueFilter().between("col0", "x002", "x003").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().between("col1", "x002", "x003").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().between("col2", "x002", "x003").accept(header, values));
		assertTrue(new SimpleCsvNamedValueFilter().between("col3", "x002", "x003").accept(header, values));
		assertFalse(new SimpleCsvNamedValueFilter().between("col4", "x002", "x003").accept(header, values));
	}

	@Test
	public void testNot() {
		assertTrue(new SimpleCsvNamedValueFilter().not(
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } }
			).accept(null, null));
		assertFalse(new SimpleCsvNamedValueFilter().not(
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } }
			).accept(null, null));
	}

	@Test
	public void testToString() {
		assertThat(new SimpleCsvNamedValueFilter().toString(), is("SimpleCsvNamedValueFilter"));
		
	}

}
