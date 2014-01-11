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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import com.orangesignal.csv.entity.Price;
import com.orangesignal.csv.filters.BeanLessThanOrEqualExpression;

/**
 * {@link BeanLessThanOrEqualExpression} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class BeanLessThanOrEqualExpressionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testBeanLessThanOrEqualExpressionIllegalArgumentException1() {
		new BeanLessThanOrEqualExpression(null, 1098.00);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBeanLessThanOrEqualExpressionIllegalArgumentException2() {
		new BeanLessThanOrEqualExpression("price", null);
	}

	@Test
	public void testBeanLessThanOrEqualExpression() {
		new BeanLessThanOrEqualExpression("price", 1098.00);
		new BeanLessThanOrEqualExpression("price", 1098.00, null);
		new BeanLessThanOrEqualExpression("date", new Date());
		new BeanLessThanOrEqualExpression("date", new Date(), null);
	}

	@Test
	public void testAccept() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertTrue(new BeanLessThanOrEqualExpression("price", 1098.00).accept(price));
		assertTrue(new BeanLessThanOrEqualExpression("price", 1088.70).accept(price));
		assertFalse(new BeanLessThanOrEqualExpression("price", 1088.00).accept(price));
		assertTrue(new BeanLessThanOrEqualExpression("date", df.parse("2009/12/06")).accept(price));
		assertTrue(new BeanLessThanOrEqualExpression("date", df.parse("2009/11/06")).accept(price));
		assertFalse(new BeanLessThanOrEqualExpression("date", df.parse("2009/10/06")).accept(price));
		assertTrue(new BeanLessThanOrEqualExpression("price", 1098.00, null).accept(price));
		assertTrue(new BeanLessThanOrEqualExpression("price", 1088.70, null).accept(price));
		assertFalse(new BeanLessThanOrEqualExpression("price", 1088.00, null).accept(price));
		assertTrue(new BeanLessThanOrEqualExpression("date", df.parse("2009/12/06"), null).accept(price));
		assertTrue(new BeanLessThanOrEqualExpression("date", df.parse("2009/11/06"), null).accept(price));
		assertFalse(new BeanLessThanOrEqualExpression("date", df.parse("2009/10/06"), null).accept(price));
	}

	@Test
	public void testToString() {
		assertThat(new BeanLessThanOrEqualExpression("price", 1098.00).toString(), is("BeanLessThanOrEqualExpression"));
		
	}

}
