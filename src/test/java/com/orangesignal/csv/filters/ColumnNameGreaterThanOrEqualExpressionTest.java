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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * {@link ColumnNameGreaterThanOrEqualExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
public class ColumnNameGreaterThanOrEqualExpressionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testColumnNameGreaterThanOrEqualExpressionIllegalArgumentException1() {
		new ColumnNameGreaterThanOrEqualExpression(null, "x002");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColumnNameGreaterThanOrEqualExpressionIllegalArgumentException2() {
		new ColumnNameGreaterThanOrEqualExpression("col", null);
	}

	@Test
	public void testAccept() {
		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2", "col3", "col4" });
		final List<String> values = Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" });
		assertFalse(new ColumnNameGreaterThanOrEqualExpression("col0", "x002").accept(header, values));
		assertFalse(new ColumnNameGreaterThanOrEqualExpression("col1", "x002").accept(header, values));
		assertTrue(new ColumnNameGreaterThanOrEqualExpression("col2", "x002").accept(header, values));
		assertTrue(new ColumnNameGreaterThanOrEqualExpression("col3", "x002").accept(header, values));
		assertTrue(new ColumnNameGreaterThanOrEqualExpression("col4", "x002").accept(header, values));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAcceptIllegalArgumentException() {
		new ColumnNameGreaterThanOrEqualExpression("col", "x001").accept(Arrays.asList(new String[]{ "col0", "col1", "col2" }), null);
	}

	@Test
	public void testToString() {
		assertThat(new ColumnNameGreaterThanOrEqualExpression("col", "x001").toString(), is("ColumnNameGreaterThanOrEqualExpression"));
		
	}

}