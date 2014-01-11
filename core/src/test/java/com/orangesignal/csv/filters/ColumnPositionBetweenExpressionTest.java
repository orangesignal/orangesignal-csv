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

import com.orangesignal.csv.filters.ColumnPositionBetweenExpression;

/**
 * {@link ColumnPositionBetweenExpression} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class ColumnPositionBetweenExpressionTest {

	@Test
	public void testColumnPositionBetweenExpression() {
		new ColumnPositionBetweenExpression(0, "x002", "x003");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColumnPositionBetweenExpressionIllegalArgumentException1() {
		new ColumnPositionBetweenExpression(0, null, "x003");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColumnPositionBetweenExpressionIllegalArgumentException2() {
		new ColumnPositionBetweenExpression(0, "x002", null);
	}

	@Test
	public void testAcceptListOfString() {
		assertFalse(new ColumnPositionBetweenExpression(0, "x002", "x003").accept(Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertFalse(new ColumnPositionBetweenExpression(1, "x002", "x003").accept(Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertTrue(new ColumnPositionBetweenExpression(2, "x002", "x003").accept(Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertTrue(new ColumnPositionBetweenExpression(3, "x002", "x003").accept(Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertFalse(new ColumnPositionBetweenExpression(4, "x002", "x003").accept(Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
	}

	@Test
	public void testAcceptListOfStringListOfString() {
		assertFalse(new ColumnPositionBetweenExpression(0, "x002", "x003").accept(null, Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertFalse(new ColumnPositionBetweenExpression(1, "x002", "x003").accept(null, Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertTrue(new ColumnPositionBetweenExpression(2, "x002", "x003").accept(null, Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertTrue(new ColumnPositionBetweenExpression(3, "x002", "x003").accept(null, Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertFalse(new ColumnPositionBetweenExpression(4, "x002", "x003").accept(null, Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
	}

	@Test
	public void testToString() {
		assertThat(new ColumnPositionBetweenExpression(0, "x002", "x003").toString(), is("ColumnPositionBetweenExpression"));
		
	}

}
