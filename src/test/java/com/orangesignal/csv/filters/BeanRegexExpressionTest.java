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
import java.util.regex.Pattern;

import org.junit.Test;

import com.orangesignal.csv.entity.Price;

/**
 * {@link BeanRegexExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
public class BeanRegexExpressionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testBeanRegexExpressionIllegalArgumentException1() {
		final String pattern = null;
		new BeanRegexExpression("symbol", pattern);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBeanRegexExpressionIllegalArgumentException2() {
		final String pattern = null;
		new BeanRegexExpression("symbol", pattern, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBeanRegexExpressionIllegalArgumentException3() {
		final String pattern = null;
		new BeanRegexExpression("symbol", pattern, Pattern.CASE_INSENSITIVE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBeanRegexExpressionIllegalArgumentException4() {
		final Pattern pattern = null;
		new BeanRegexExpression("symbol", pattern);
	}

	@Test
	public void testBeanRegexExpression() {
		new BeanRegexExpression("symbol", "^.*$");
		new BeanRegexExpression("symbol", "^.*$", false);
		new BeanRegexExpression("symbol", "^.*$", true);
		new BeanRegexExpression("symbol", "^.*$", Pattern.CASE_INSENSITIVE);
		new BeanRegexExpression("symbol", Pattern.compile("^.*$"));
	}

	@Test
	public void testAccept() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertTrue(new BeanRegexExpression("symbol", "^GCX[0-9]{1,2}$").accept(price));
		assertFalse(new BeanRegexExpression("name", "^GCX[0-9]{1,2}$").accept(price));
		assertTrue(new BeanRegexExpression("symbol", "^GCX[0-9]{1,2}$", false).accept(price));
		assertFalse(new BeanRegexExpression("name", "^GCX[0-9]{1,2}$", false).accept(price));
		assertTrue(new BeanRegexExpression("symbol", "^gcx[0-9]{1,2}$", true).accept(price));
		assertFalse(new BeanRegexExpression("name", "^gcx[0-9]{1,2}$", true).accept(price));
		assertTrue(new BeanRegexExpression("symbol", "^gcx[0-9]{1,2}$", Pattern.CASE_INSENSITIVE).accept(price));
		assertFalse(new BeanRegexExpression("name", "^gcx[0-9]{1,2}$", Pattern.CASE_INSENSITIVE).accept(price));
		assertTrue(new BeanRegexExpression("symbol", Pattern.compile("^GCX[0-9]{1,2}$")).accept(price));
		assertFalse(new BeanRegexExpression("name", Pattern.compile("^GCX[0-9]{1,2}$")).accept(price));
	}

	@Test
	public void testToString() {
		assertThat(new BeanRegexExpression("symbol", "^.*$").toString(), is("BeanRegexExpression"));
		
	}

}