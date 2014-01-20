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

import java.util.Date;

import org.junit.Test;

import com.orangesignal.csv.entity.Price;

/**
 * {@link BeanNotNullExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
public class BeanNotNullExpressionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testBeanNotNullExpressionIllegalArgumentException() {
		new BeanNotNullExpression(null);
	}

	@Test
	public void testAccep() throws Exception {
		assertFalse(new BeanNotNullExpression("symbol").accept(new Price(null, "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertTrue(new BeanNotNullExpression("symbol").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertFalse(new BeanNotNullExpression("name").accept(new Price("GCX09", null, 1088.70, 100, new Date())));
		assertTrue(new BeanNotNullExpression("name").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertFalse(new BeanNotNullExpression("price").accept(new Price("GCX09", "COMEX 金 2009年11月限", null, 100, new Date())));
		assertTrue(new BeanNotNullExpression("price").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertFalse(new BeanNotNullExpression("date").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, null)));
		assertTrue(new BeanNotNullExpression("date").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
	}

	@Test
	public void testToString() {
		assertThat(new BeanNotNullExpression("symbol").toString(), is("BeanNotNullExpression"));
		
	}

}