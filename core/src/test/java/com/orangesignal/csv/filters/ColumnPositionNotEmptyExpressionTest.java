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

import org.junit.Test;

import com.orangesignal.csv.filters.ColumnPositionNotEmptyExpression;

/**
 * {@link ColumnPositionNotEmptyExpression} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class ColumnPositionNotEmptyExpressionTest {

	@Test
	public void testColumnPositionNotEmptyExpression() {
		new ColumnPositionNotEmptyExpression(0);
	}

	@Test
	public void testAcceptListOfString() {
		assertFalse(new ColumnPositionNotEmptyExpression(0).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEmptyExpression(1).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEmptyExpression(2).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new ColumnPositionNotEmptyExpression(0).accept(Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEmptyExpression(1).accept(Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEmptyExpression(2).accept(Arrays.asList(new String[]{ "", "aaa", "bbb" })));
	}

	@Test
	public void testAcceptListOfStringListOfString() {
		assertFalse(new ColumnPositionNotEmptyExpression(0).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEmptyExpression(1).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEmptyExpression(2).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new ColumnPositionNotEmptyExpression(0).accept(null, Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEmptyExpression(1).accept(null, Arrays.asList(new String[]{ "", "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEmptyExpression(2).accept(null, Arrays.asList(new String[]{ "", "aaa", "bbb" })));
	}

	@Test
	public void testToString() {
		assertThat(new ColumnPositionNotEmptyExpression(0).toString(), is("ColumnPositionNotEmptyExpression"));
		
	}

}
