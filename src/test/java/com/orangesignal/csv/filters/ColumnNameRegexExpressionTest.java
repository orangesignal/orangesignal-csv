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

import com.orangesignal.csv.filters.ColumnNameRegexExpression;

/**
 * {@link ColumnNameRegexExpression} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class ColumnNameRegexExpressionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testColumnNameRegexExpressionIllegalArgumentException1() {
		final String pattern = null;
		new ColumnNameRegexExpression("col", pattern);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColumnNameRegexExpressionIllegalArgumentException2() {
		final String pattern = null;
		new ColumnNameRegexExpression("col", pattern, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColumnNameRegexExpressionIllegalArgumentException3() {
		final String pattern = null;
		new ColumnNameRegexExpression("col", pattern, Pattern.CASE_INSENSITIVE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColumnNameRegexExpressionIllegalArgumentException4() {
		final Pattern pattern = null;
		new ColumnNameRegexExpression("col", pattern);
	}

	@Test
	public void testColumnNameRegexExpression() {
		new ColumnNameRegexExpression("col", "^.*$");
		new ColumnNameRegexExpression("col", "^.*$", false);
		new ColumnNameRegexExpression("col", "^.*$", true);
		new ColumnNameRegexExpression("col", "^.*$", Pattern.CASE_INSENSITIVE);
		new ColumnNameRegexExpression("col", Pattern.compile("^.*$"));
	}

	@Test
	public void testAccept() {
		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2" });
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(new ColumnNameRegexExpression("col0", "^[a]+$").accept(header, values));
		assertTrue(new ColumnNameRegexExpression("col1", "^[a]+$").accept(header, values));
		assertFalse(new ColumnNameRegexExpression("col2", "^[a]+$").accept(header, values));
		assertFalse(new ColumnNameRegexExpression("col0", "^[a]+$", false).accept(header, values));
		assertTrue(new ColumnNameRegexExpression("col1", "^[a]+$", false).accept(header, values));
		assertFalse(new ColumnNameRegexExpression("col2", "^[a]+$", false).accept(header, values));
		assertFalse(new ColumnNameRegexExpression("col0", "^[A]+$", true).accept(header, values));
		assertTrue(new ColumnNameRegexExpression("col1", "^[A]+$", true).accept(header, values));
		assertFalse(new ColumnNameRegexExpression("col2", "^[A]+$", true).accept(header, values));
		assertFalse(new ColumnNameRegexExpression("col0", "^[A]+$", Pattern.CASE_INSENSITIVE).accept(header, values));
		assertTrue(new ColumnNameRegexExpression("col1", "^[A]+$", Pattern.CASE_INSENSITIVE).accept(header, values));
		assertFalse(new ColumnNameRegexExpression("col2", "^[A]+$", Pattern.CASE_INSENSITIVE).accept(header, values));
		assertFalse(new ColumnNameRegexExpression("col0", Pattern.compile("^[a]+$")).accept(header, values));
		assertTrue(new ColumnNameRegexExpression("col1", Pattern.compile("^[a]+$")).accept(header, values));
		assertFalse(new ColumnNameRegexExpression("col2", Pattern.compile("^[a]+$")).accept(header, values));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAcceptIllegalArgumentException() {
		new ColumnNameRegexExpression("col", "^.*$").accept(Arrays.asList(new String[]{ "col0", "col1", "col2" }), null);
	}

	@Test
	public void testToString() {
		assertThat(new ColumnNameRegexExpression("col", "^.*$").toString(), is("ColumnNameRegexExpression"));
		
	}

}
