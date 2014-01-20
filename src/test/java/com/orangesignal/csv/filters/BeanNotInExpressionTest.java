/*
 * Copyright 2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.orangesignal.csv.filters;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.junit.Test;

import com.orangesignal.csv.entity.Price;

/**
 * {@link BeanNotInExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
public class BeanNotInExpressionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testBeanNotInExpressionIllegalArgumentException2() {
		final Object[] criterias = null;
		new BeanNotInExpression("col", criterias);
	}

	@Test
	public void testAccept() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertTrue(new BeanNotInExpression("symbol", new String[]{ "SIU09", "SIV09", "SIX09" }).accept(price));
		assertFalse(new BeanNotInExpression("symbol", new String[]{ "GCU09", "GCV09", "GCX09" }).accept(price));
		assertTrue(new BeanNotInExpression("symbol", new String[]{ "SIU09", "SIV09", "SIX09" }, false).accept(price));
		assertFalse(new BeanNotInExpression("symbol", new String[]{ "GCU09", "GCV09", "GCX09" }, false).accept(price));
		assertTrue(new BeanNotInExpression("symbol", new String[]{ "siu09", "siv09", "six09" }, true).accept(price));
		assertFalse(new BeanNotInExpression("symbol", new String[]{ "gcu09", "gcv09", "gcx09" }, true).accept(price));
		assertTrue(new BeanNotInExpression("name", "COMEX 銀 2009年9月限", "COMEX 銀 2009年10月限", "COMEX 銀 2009年11月限").accept(price));
		assertFalse(new BeanNotInExpression("name", "COMEX 金 2009年9月限", "COMEX 金 2009年10月限", "COMEX 金 2009年11月限").accept(price));
		assertTrue(new BeanNotInExpression("price",1068.00, 1078.00, 1088.00).accept(price));
		assertFalse(new BeanNotInExpression("price", 1068.70, 1078.70, 1088.70).accept(price));
		assertTrue(new BeanNotInExpression("date", df.parse("2008/09/06"), df.parse("2008/10/06"), df.parse("2008/11/06")).accept(price));
		assertFalse(new BeanNotInExpression("date", df.parse("2009/09/06"), df.parse("2009/10/06"), df.parse("2009/11/06")).accept(price));
	}

	@Test
	public void testToString() {
		assertThat(new BeanNotInExpression("symbol", new String[]{ "SIU09", "SIV09", "SIX09" }).toString(), is("BeanNotInExpression"));
		
	}

}