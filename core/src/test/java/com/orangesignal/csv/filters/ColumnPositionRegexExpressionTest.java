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

import com.orangesignal.csv.filters.ColumnPositionRegexExpression;

/**
 * {@link ColumnPositionRegexExpression} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class ColumnPositionRegexExpressionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testColumnPositionRegexExpressionIllegalArgumentException1() {
		final String pattern = null;
		new ColumnPositionRegexExpression(0, pattern);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColumnPositionRegexExpressionIllegalArgumentException2() {
		final String pattern = null;
		new ColumnPositionRegexExpression(0, pattern, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColumnPositionRegexExpressionIllegalArgumentException3() {
		final String pattern = null;
		new ColumnPositionRegexExpression(0, pattern, Pattern.CASE_INSENSITIVE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColumnPositionRegexExpressionIllegalArgumentException4() {
		final Pattern pattern = null;
		new ColumnPositionRegexExpression(0, pattern);
	}

	@Test
	public void testColumnPositionRegexExpression() {
		new ColumnPositionRegexExpression(0, "^.*$");
		new ColumnPositionRegexExpression(0, "^.*$", false);
		new ColumnPositionRegexExpression(0, "^.*$", true);
		new ColumnPositionRegexExpression(0, "^.*$", Pattern.CASE_INSENSITIVE);
		new ColumnPositionRegexExpression(0, Pattern.compile("^.*$"));
	}

	@Test
	public void testAcceptListOfString() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(new ColumnPositionRegexExpression(0, "^[a]+$").accept(values));
		assertTrue(new ColumnPositionRegexExpression(1, "^[a]+$").accept(values));
		assertFalse(new ColumnPositionRegexExpression(2, "^[a]+$").accept(values));
		assertFalse(new ColumnPositionRegexExpression(0, "^[a]+$", false).accept(values));
		assertTrue(new ColumnPositionRegexExpression(1, "^[a]+$", false).accept(values));
		assertFalse(new ColumnPositionRegexExpression(2, "^[a]+$", false).accept(values));
		assertFalse(new ColumnPositionRegexExpression(0, "^[A]+$", true).accept(values));
		assertTrue(new ColumnPositionRegexExpression(1, "^[A]+$", true).accept(values));
		assertFalse(new ColumnPositionRegexExpression(2, "^[A]+$", true).accept(values));
		assertFalse(new ColumnPositionRegexExpression(0, "^[A]+$", Pattern.CASE_INSENSITIVE).accept(values));
		assertTrue(new ColumnPositionRegexExpression(1, "^[A]+$", Pattern.CASE_INSENSITIVE).accept(values));
		assertFalse(new ColumnPositionRegexExpression(2, "^[A]+$", Pattern.CASE_INSENSITIVE).accept(values));
		assertFalse(new ColumnPositionRegexExpression(0, Pattern.compile("^[a]+$")).accept(values));
		assertTrue(new ColumnPositionRegexExpression(1, Pattern.compile("^[a]+$")).accept(values));
		assertFalse(new ColumnPositionRegexExpression(2, Pattern.compile("^[a]+$")).accept(values));
	}

	@Test
	public void testAcceptListOfStringListOfString() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(new ColumnPositionRegexExpression(0, "^[a]+$").accept(null, values));
		assertTrue(new ColumnPositionRegexExpression(1, "^[a]+$").accept(null, values));
		assertFalse(new ColumnPositionRegexExpression(2, "^[a]+$").accept(null, values));
		assertFalse(new ColumnPositionRegexExpression(0, "^[a]+$", false).accept(null, values));
		assertTrue(new ColumnPositionRegexExpression(1, "^[a]+$", false).accept(null, values));
		assertFalse(new ColumnPositionRegexExpression(2, "^[a]+$", false).accept(null, values));
		assertFalse(new ColumnPositionRegexExpression(0, "^[A]+$", true).accept(null, values));
		assertTrue(new ColumnPositionRegexExpression(1, "^[A]+$", true).accept(null, values));
		assertFalse(new ColumnPositionRegexExpression(2, "^[A]+$", true).accept(null, values));
		assertFalse(new ColumnPositionRegexExpression(0, "^[A]+$", Pattern.CASE_INSENSITIVE).accept(null, values));
		assertTrue(new ColumnPositionRegexExpression(1, "^[A]+$", Pattern.CASE_INSENSITIVE).accept(null, values));
		assertFalse(new ColumnPositionRegexExpression(2, "^[A]+$", Pattern.CASE_INSENSITIVE).accept(null, values));
		assertFalse(new ColumnPositionRegexExpression(0, Pattern.compile("^[a]+$")).accept(null, values));
		assertTrue(new ColumnPositionRegexExpression(1, Pattern.compile("^[a]+$")).accept(null, values));
		assertFalse(new ColumnPositionRegexExpression(2, Pattern.compile("^[a]+$")).accept(null, values));
	}

	@Test
	public void testToString() {
		assertThat(new ColumnPositionRegexExpression(0, "^.*$").toString(), is("ColumnPositionRegexExpression"));
		
	}

}
