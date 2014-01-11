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

import org.junit.Test;

import com.orangesignal.csv.filters.ColumnPositionNotInExpression;

/**
 * {@link ColumnPositionNotInExpression} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class ColumnPositionNotInExpressionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testColumnPositionNotInExpressionIllegalArgumentException() {
		final String[] criterias = null;
		new ColumnPositionNotInExpression(0, criterias);
	}

	@Test
	public void testAcceptListOfString() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertTrue(new ColumnPositionNotInExpression(0, "a", "aa", "aaa").accept(values));
		assertFalse(new ColumnPositionNotInExpression(1, "a", "aa", "aaa").accept(values));
		assertTrue(new ColumnPositionNotInExpression(2, "a", "aa", "aaa").accept(values));
		assertTrue(new ColumnPositionNotInExpression(0, new String[]{ "a", "aa", "aaa" }, false).accept(values));
		assertFalse(new ColumnPositionNotInExpression(1, new String[]{ "a", "aa", "aaa" }, false).accept(values));
		assertTrue(new ColumnPositionNotInExpression(2, new String[]{ "a", "aa", "aaa" }, false).accept(values));
		assertTrue(new ColumnPositionNotInExpression(0, new String[]{ "A", "AA", "AAA" }, true).accept(values));
		assertFalse(new ColumnPositionNotInExpression(1, new String[]{ "A", "AA", "AAA" }, true).accept(values));
		assertTrue(new ColumnPositionNotInExpression(2, new String[]{ "A", "AA", "AAA" }, true).accept(values));
	}

	@Test
	public void testAcceptListOfStringListOfString() {
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertTrue(new ColumnPositionNotInExpression(0, "a", "aa", "aaa").accept(null, values));
		assertFalse(new ColumnPositionNotInExpression(1, "a", "aa", "aaa").accept(null, values));
		assertTrue(new ColumnPositionNotInExpression(2, "a", "aa", "aaa").accept(null, values));
		assertTrue(new ColumnPositionNotInExpression(0, new String[]{ "a", "aa", "aaa" }, false).accept(null, values));
		assertFalse(new ColumnPositionNotInExpression(1, new String[]{ "a", "aa", "aaa" }, false).accept(null, values));
		assertTrue(new ColumnPositionNotInExpression(2, new String[]{ "a", "aa", "aaa" }, false).accept(null, values));
		assertTrue(new ColumnPositionNotInExpression(0, new String[]{ "A", "AA", "AAA" }, true).accept(null, values));
		assertFalse(new ColumnPositionNotInExpression(1, new String[]{ "A", "AA", "AAA" }, true).accept(null, values));
		assertTrue(new ColumnPositionNotInExpression(2, new String[]{ "A", "AA", "AAA" }, true).accept(null, values));
	}

	@Test
	public void testToString() {
		assertThat(new ColumnPositionNotInExpression(0, "a", "aa", "aaa").toString(), is("ColumnPositionNotInExpression"));
		
	}

}
