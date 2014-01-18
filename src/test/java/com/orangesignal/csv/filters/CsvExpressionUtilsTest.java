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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import com.orangesignal.csv.filters.CsvExpressionUtils;

/**
 * {@link CsvExpressionUtils} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class CsvExpressionUtilsTest {

	@Test(expected = IllegalArgumentException.class)
	public void testIsNullIllegalArgumentException1() {
		CsvExpressionUtils.isNull(null, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsNullIllegalArgumentException2() {
		CsvExpressionUtils.isNull(Arrays.asList(new String[]{ null, "", "" }), -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsNullIllegalArgumentException3() {
		CsvExpressionUtils.isNull(Arrays.asList(new String[]{ null, "", "" }), 3);
	}

	@Test
	public void testIsNull() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertTrue(CsvExpressionUtils.isNull(values, 0));
		assertFalse(CsvExpressionUtils.isNull(values, 1));
	}

	@Test
	public void testIsNotNull() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertTrue(CsvExpressionUtils.isNotNull(values, 1));
		assertFalse(CsvExpressionUtils.isNotNull(values, 0));
	}

	@Test
	public void testIsEmpty() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "" });
		assertTrue(CsvExpressionUtils.isEmpty(values, 0));
		assertFalse(CsvExpressionUtils.isEmpty(values, 1));
		assertTrue(CsvExpressionUtils.isEmpty(values, 2));
	}

	@Test
	public void testIsNotEmpty() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "" });
		assertFalse(CsvExpressionUtils.isNotEmpty(values, 0));
		assertTrue(CsvExpressionUtils.isNotEmpty(values, 1));
		assertFalse(CsvExpressionUtils.isNotEmpty(values, 2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEqIllegalArgumentException() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		CsvExpressionUtils.eq(values, 0, null, false);
	}

	@Test
	public void testEq() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(CsvExpressionUtils.eq(values, 0, "aaa", false));
		assertTrue(CsvExpressionUtils.eq(values, 1, "aaa", false));
		assertFalse(CsvExpressionUtils.eq(values, 2, "aaa", false));
		assertFalse(CsvExpressionUtils.eq(values, 0, "AAA", true));
		assertTrue(CsvExpressionUtils.eq(values, 1, "AAA", true));
		assertFalse(CsvExpressionUtils.eq(values, 2, "AAA", true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNeIllegalArgumentException() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		CsvExpressionUtils.ne(values, 0, null, false);
	}

	@Test
	public void testNe() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertTrue(CsvExpressionUtils.ne(values, 0, "aaa", false));
		assertFalse(CsvExpressionUtils.ne(values, 1, "aaa", false));
		assertTrue(CsvExpressionUtils.ne(values, 2, "aaa", false));
		assertTrue(CsvExpressionUtils.ne(values, 0, "AAA", true));
		assertFalse(CsvExpressionUtils.ne(values, 1, "AAA", true));
		assertTrue(CsvExpressionUtils.ne(values, 2, "AAA", true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInIllegalArgumentException1() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		final String[] criterias = null;
		CsvExpressionUtils.in(values, 0, criterias, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInIllegalArgumentException2() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		final String[] criterias = new String[]{ null };
		CsvExpressionUtils.in(values, 0, criterias, false);
	}

	@Test
	public void testIn() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(CsvExpressionUtils.in(values, 0, new String[]{ "a", "aa", "aaa" }, false));
		assertTrue(CsvExpressionUtils.in(values, 1, new String[]{ "a", "aa", "aaa" }, false));
		assertFalse(CsvExpressionUtils.in(values, 2, new String[]{ "a", "aa", "aaa" }, false));
		assertFalse(CsvExpressionUtils.in(values, 0, new String[]{ "A", "AA", "AAA" }, true));
		assertTrue(CsvExpressionUtils.in(values, 1, new String[]{ "A", "AA", "AAA" }, true));
		assertFalse(CsvExpressionUtils.in(values, 2, new String[]{ "A", "AA", "AAA" }, true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotInIllegalArgumentException1() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		final String[] criterias = null;
		CsvExpressionUtils.notIn(values, 0, criterias, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotInIllegalArgumentException2() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		final String[] criterias = new String[]{ null };
		CsvExpressionUtils.notIn(values, 0, criterias, false);
	}

	@Test
	public void testNotIn() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertTrue(CsvExpressionUtils.notIn(values, 0, new String[]{ "a", "aa", "aaa" }, false));
		assertFalse(CsvExpressionUtils.notIn(values, 1, new String[]{ "a", "aa", "aaa" }, false));
		assertTrue(CsvExpressionUtils.notIn(values, 2, new String[]{ "a", "aa", "aaa" }, false));
		assertTrue(CsvExpressionUtils.notIn(values, 0, new String[]{ "A", "AA", "AAA" }, true));
		assertFalse(CsvExpressionUtils.notIn(values, 1, new String[]{ "A", "AA", "AAA" }, true));
		assertTrue(CsvExpressionUtils.notIn(values, 2, new String[]{ "A", "AA", "AAA" }, true));
	}

	@Test
	public void testRegex() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(CsvExpressionUtils.regex(values, 0, Pattern.compile("^[a]+$")));
		assertTrue(CsvExpressionUtils.regex(values, 1, Pattern.compile("^[a]+$")));
		assertFalse(CsvExpressionUtils.regex(values, 2, Pattern.compile("^[a]+$")));
	}

	@Test
	public void testGt() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(CsvExpressionUtils.gt(values, 0, "aaa"));
		assertFalse(CsvExpressionUtils.gt(values, 1, "aaa"));
		assertTrue(CsvExpressionUtils.gt(values, 2, "aaa"));
	}

	@Test
	public void testLt() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(CsvExpressionUtils.lt(values, 0, "bbb"));
		assertTrue(CsvExpressionUtils.lt(values, 1, "bbb"));
		assertFalse(CsvExpressionUtils.lt(values, 2, "bbb"));
	}

	@Test
	public void testGe() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(CsvExpressionUtils.ge(values, 0, "aaa"));
		assertTrue(CsvExpressionUtils.ge(values, 1, "aaa"));
		assertTrue(CsvExpressionUtils.ge(values, 2, "aaa"));
	}

	@Test
	public void testLe() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(CsvExpressionUtils.le(values, 0, "bbb"));
		assertTrue(CsvExpressionUtils.le(values, 1, "bbb"));
		assertTrue(CsvExpressionUtils.le(values, 2, "bbb"));
	}

	@Test
	public void testBetween() {
		final List<String> values = Arrays.asList(new String[]{ null, "100", "10000" });
		assertFalse(CsvExpressionUtils.between(values, 0, "10", "1000"));
		assertTrue(CsvExpressionUtils.between(values, 1, "10", "1000"));
		assertFalse(CsvExpressionUtils.between(values, 2, "10", "1000"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidate1() {
		CsvExpressionUtils.validate(null, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidate2() {
		CsvExpressionUtils.validate(Arrays.asList(new String[]{ "", "", "" }), -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateListOfStringInt3() {
		CsvExpressionUtils.validate(Arrays.asList(new String[]{ "", "", "" }), 3);
	}

}
