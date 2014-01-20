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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import com.orangesignal.csv.entity.Price;

/**
 * {@link BeanEqualExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
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