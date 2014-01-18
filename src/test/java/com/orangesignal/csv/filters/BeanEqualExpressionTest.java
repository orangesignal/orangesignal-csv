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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import com.orangesignal.csv.entity.Price;
import com.orangesignal.csv.filters.BeanEqualExpression;

/**
 * {@link BeanEqualExpression} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class BeanEqualExpressionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testBeanEqualExpressionIllegalArgumentException1() {
		new BeanEqualExpression(null, "aaa");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBeanEqualExpressionIllegalArgumentException2() {
		new BeanEqualExpression("col", null);
	}

	@Test
	public void testAccep() throws Exception {
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2009/11/06 19:14:46"));
		assertFalse(new BeanEqualExpression("symbol", "SIX09").accept(price));
		assertTrue(new BeanEqualExpression("symbol", "GCX09").accept(price));
		assertFalse(new BeanEqualExpression("symbol", "SIX09", false).accept(price));
		assertTrue(new BeanEqualExpression("symbol", "GCX09", false).accept(price));
		assertFalse(new BeanEqualExpression("symbol", "six09", true).accept(price));
		assertTrue(new BeanEqualExpression("symbol", "gcx09", true).accept(price));
		assertFalse(new BeanEqualExpression("name", "COMEX 銀 2009年11月限").accept(price));
		assertTrue(new BeanEqualExpression("name", "COMEX 金 2009年11月限").accept(price));
		assertFalse(new BeanEqualExpression("price", 1088.00).accept(price));
		assertTrue(new BeanEqualExpression("price", 1088.70).accept(price));
		assertFalse(new BeanEqualExpression("date", new Date()).accept(price));
		assertTrue(new BeanEqualExpression("date", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2009/11/06 19:14:46")).accept(price));
	}

	@Test
	public void testToString() {
		assertThat(new BeanEqualExpression("symbol", "GCX09").toString(), is("BeanEqualExpression"));
		
	}

}
