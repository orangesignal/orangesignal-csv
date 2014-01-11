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

import com.orangesignal.csv.filters.ColumnNameNotEqualExpression;

/**
 * {@link ColumnNameNotEqualExpression} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class ColumnNameNotEqualExpressionTest {

	@Test
	public void testColumnNameEqualExpression() {
		new ColumnNameNotEqualExpression("col", "aaa");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColumnNameNotEqualExpressionIllegalArgumentException1() {
		new ColumnNameNotEqualExpression(null, "aaa");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColumnNameNotEqualExpressionIllegalArgumentException2() {
		new ColumnNameNotEqualExpression("col", null);
	}

	@Test
	public void testAccep() {
		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2", "col3", "col4" });
		final List<String> values = Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" });
		assertTrue(new ColumnNameNotEqualExpression("col0", "x001").accept(header, values));
		assertFalse(new ColumnNameNotEqualExpression("col1", "x001").accept(header, values));
		assertTrue(new ColumnNameNotEqualExpression("col2", "x001").accept(header, values));
		assertTrue(new ColumnNameNotEqualExpression("col0", "x001", false).accept(header, values));
		assertFalse(new ColumnNameNotEqualExpression("col1", "x001", false).accept(header, values));
		assertTrue(new ColumnNameNotEqualExpression("col2", "x001", false).accept(header, values));
		assertTrue(new ColumnNameNotEqualExpression("col0", "X001", true).accept(header, values));
		assertFalse(new ColumnNameNotEqualExpression("col1", "X001", true).accept(header, values));
		assertTrue(new ColumnNameNotEqualExpression("col2", "X001", true).accept(header, values));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAcceptIllegalArgumentException() {
		new ColumnNameNotEqualExpression("col", "x001").accept(Arrays.asList(new String[]{ "col0", "col1", "col2" }), null);
	}

	@Test
	public void testToString() {
		assertThat(new ColumnNameNotEqualExpression("col", "x001").toString(), is("ColumnNameNotEqualExpression"));
		
	}

}
