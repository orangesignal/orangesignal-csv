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

import com.orangesignal.csv.filters.CsvValueAndExpression;
import com.orangesignal.csv.filters.CsvValueFilter;
import com.orangesignal.csv.filters.CsvValueOrExpression;
import com.orangesignal.csv.filters.SimpleCsvValueFilter;

/**
 * {@link SimpleCsvValueFilter} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class SimpleCsvValueFilterTest {

	@Test
	public void testSimpleCsvValueFilter() {
		assertFalse(new SimpleCsvValueFilter()
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } })
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } })
				.accept(null)
			);
		assertFalse(new SimpleCsvValueFilter()
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } })
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } })
				.accept(null)
			);
		assertFalse(new SimpleCsvValueFilter()
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } })
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } })
				.accept(null)
			);
		assertTrue(new SimpleCsvValueFilter()
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } })
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } })
				.accept(null)
			);

		assertFalse(new SimpleCsvValueFilter(new CsvValueAndExpression())
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } })
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } })
				.accept(null)
			);
		assertFalse(new SimpleCsvValueFilter(new CsvValueAndExpression())
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } })
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } })
				.accept(null)
			);
		assertFalse(new SimpleCsvValueFilter(new CsvValueAndExpression())
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } })
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } })
				.accept(null)
			);
		assertTrue(new SimpleCsvValueFilter(new CsvValueAndExpression())
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } })
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } })
				.accept(null)
			);

		assertFalse(new SimpleCsvValueFilter(new CsvValueOrExpression())
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } })
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } })
				.accept(null)
			);
		assertTrue(new SimpleCsvValueFilter(new CsvValueOrExpression())
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } })
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } })
				.accept(null)
			);
		assertTrue(new SimpleCsvValueFilter(new CsvValueOrExpression())
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } })
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } })
				.accept(null)
			);
		assertTrue(new SimpleCsvValueFilter(new CsvValueOrExpression())
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } })
				.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } })
				.accept(null)
			);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSimpleCsvValueFilterIllegalArgumentException() {
		new SimpleCsvValueFilter(null);
	}

	@Test
	public void testIsNull() {
		assertTrue(new SimpleCsvValueFilter().isNull(0).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().isNull(1).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().isNull(2).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
	}

	@Test
	public void testNotNull() {
		assertFalse(new SimpleCsvValueFilter().isNotNull(0).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().isNotNull(1).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().isNotNull(2).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
	}

	@Test
	public void testIsEmpty() {
		assertTrue(new SimpleCsvValueFilter().isEmpty(0).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().isEmpty(1).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().isEmpty(2).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().isEmpty(0).accept(Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().isEmpty(1).accept(Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().isEmpty(2).accept(Arrays.asList(new String[]{ "", "aaa", "bbb" })));
	}

	@Test
	public void testIsNotEmpty() {
		assertFalse(new SimpleCsvValueFilter().isNotEmpty(0).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().isNotEmpty(1).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().isNotEmpty(2).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().isNotEmpty(0).accept(Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().isNotEmpty(1).accept(Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().isNotEmpty(2).accept(Arrays.asList(new String[]{ "", "aaa", "bbb" })));
	}

	@Test
	public void testEq() {
		assertFalse(new SimpleCsvValueFilter().eq(0, "aaa").accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().eq(1, "aaa").accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().eq(2, "aaa").accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().eq(0, "aaa", false).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().eq(1, "aaa", false).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().eq(2, "aaa", false).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().eq(0, "AAA", true).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().eq(1, "AAA", true).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().eq(2, "AAA", true).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
	}

	@Test
	public void testNe() {
		assertTrue(new SimpleCsvValueFilter().ne(0, "aaa").accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().ne(1, "aaa").accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().ne(2, "aaa").accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().ne(0, "aaa", false).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().ne(1, "aaa", false).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().ne(2, "aaa", false).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().ne(0, "AAA", true).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().ne(1, "AAA", true).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().ne(2, "AAA", true).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
	}

	@Test
	public void testIn() {
		assertFalse(new SimpleCsvValueFilter().in(0, "a", "aa", "aaa").accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().in(1, "a", "aa", "aaa").accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().in(2, "a", "aa", "aaa").accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().in(0, new String[]{ "a", "aa", "aaa" }, false).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().in(1, new String[]{ "a", "aa", "aaa" }, false).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().in(2, new String[]{ "a", "aa", "aaa" }, false).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().in(0, new String[]{ "A", "AA", "AAA" }, true).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().in(1, new String[]{ "A", "AA", "AAA" }, true).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().in(2, new String[]{ "A", "AA", "AAA" }, true).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
	}

	@Test
	public void testNotIn() {
		assertTrue(new SimpleCsvValueFilter().notIn(0, "a", "aa", "aaa").accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().notIn(1, "a", "aa", "aaa").accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().notIn(2, "a", "aa", "aaa").accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().notIn(0, new String[]{ "a", "aa", "aaa" }, false).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().notIn(1, new String[]{ "a", "aa", "aaa" }, false).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().notIn(2, new String[]{ "a", "aa", "aaa" }, false).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().notIn(0, new String[]{ "A", "AA", "AAA" }, true).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new SimpleCsvValueFilter().notIn(1, new String[]{ "A", "AA", "AAA" }, true).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new SimpleCsvValueFilter().notIn(2, new String[]{ "A", "AA", "AAA" }, true).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
	}

	@Test
	public void testRegex() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(new SimpleCsvValueFilter().regex(0, "^[a]+$").accept(values));
		assertTrue(new SimpleCsvValueFilter().regex(1, "^[a]+$").accept(values));
		assertFalse(new SimpleCsvValueFilter().regex(2, "^[a]+$").accept(values));
		assertFalse(new SimpleCsvValueFilter().regex(0, "^[a]+$", false).accept(values));
		assertTrue(new SimpleCsvValueFilter().regex(1, "^[a]+$", false).accept(values));
		assertFalse(new SimpleCsvValueFilter().regex(2, "^[a]+$", false).accept(values));
		assertFalse(new SimpleCsvValueFilter().regex(0, "^[A]+$", true).accept(values));
		assertTrue(new SimpleCsvValueFilter().regex(1, "^[A]+$", true).accept(values));
		assertFalse(new SimpleCsvValueFilter().regex(2, "^[A]+$", true).accept(values));
		assertFalse(new SimpleCsvValueFilter().regex(0, "^[A]+$", Pattern.CASE_INSENSITIVE).accept(values));
		assertTrue(new SimpleCsvValueFilter().regex(1, "^[A]+$", Pattern.CASE_INSENSITIVE).accept(values));
		assertFalse(new SimpleCsvValueFilter().regex(2, "^[A]+$", Pattern.CASE_INSENSITIVE).accept(values));
		assertFalse(new SimpleCsvValueFilter().regex(0, Pattern.compile("^[a]+$")).accept(values));
		assertTrue(new SimpleCsvValueFilter().regex(1, Pattern.compile("^[a]+$")).accept(values));
		assertFalse(new SimpleCsvValueFilter().regex(2, Pattern.compile("^[a]+$")).accept(values));
	}

	@Test
	public void testGt() {
		assertFalse(new SimpleCsvValueFilter().gt(0, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new SimpleCsvValueFilter().gt(1, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new SimpleCsvValueFilter().gt(2, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertTrue(new SimpleCsvValueFilter().gt(3, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
	}

	@Test
	public void testLt() {
		assertFalse(new SimpleCsvValueFilter().lt(0, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertTrue(new SimpleCsvValueFilter().lt(1, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new SimpleCsvValueFilter().lt(2, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new SimpleCsvValueFilter().lt(3, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
	}

	@Test
	public void testGe() {
		assertFalse(new SimpleCsvValueFilter().ge(0, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new SimpleCsvValueFilter().ge(1, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertTrue(new SimpleCsvValueFilter().ge(2, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertTrue(new SimpleCsvValueFilter().ge(3, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
	}

	@Test
	public void testLe() {
		assertFalse(new SimpleCsvValueFilter().le(0, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertTrue(new SimpleCsvValueFilter().le(1, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertTrue(new SimpleCsvValueFilter().le(2, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new SimpleCsvValueFilter().le(3, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
	}

	@Test
	public void testBetween() {
		assertFalse(new SimpleCsvValueFilter().between(0, "x002", "x003").accept(Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertFalse(new SimpleCsvValueFilter().between(1, "x002", "x003").accept(Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertTrue(new SimpleCsvValueFilter().between(2, "x002", "x003").accept(Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertTrue(new SimpleCsvValueFilter().between(3, "x002", "x003").accept(Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertFalse(new SimpleCsvValueFilter().between(4, "x002", "x003").accept(Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
	}

	@Test
	public void testNot() {
		assertTrue(new SimpleCsvValueFilter().not(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } }).accept(null));
		assertFalse(new SimpleCsvValueFilter().not(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } }).accept(null));
	}

	@Test
	public void testToString() {
		assertThat(new SimpleCsvValueFilter().toString(), is("SimpleCsvValueFilter"));
		
	}

}
