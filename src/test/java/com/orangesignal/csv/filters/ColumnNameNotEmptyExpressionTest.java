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
 * {@link ColumnNameNotEmptyExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
public class ColumnNameNotEmptyExpressionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testColumnNameNotEmptyExpressionIllegalArgumentException() {
		new ColumnNameNotEmptyExpression(null);
	}

	@Test
	public void testAccep() {
		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2", "col3", "col4" });
		assertFalse(new ColumnNameNotEmptyExpression("col0").accept(header, Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertTrue(new ColumnNameNotEmptyExpression("col1").accept(header, Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertTrue(new ColumnNameNotEmptyExpression("col2").accept(header, Arrays.asList(new String[]{ null, "x001", "x002", "x003", "x004" })));
		assertFalse(new ColumnNameNotEmptyExpression("col0").accept(header, Arrays.asList(new String[]{ "", "x001", "x002", "x003", "x004" })));
		assertTrue(new ColumnNameNotEmptyExpression("col1").accept(header, Arrays.asList(new String[]{ "", "x001", "x002", "x003", "x004" })));
		assertTrue(new ColumnNameNotEmptyExpression("col2").accept(header, Arrays.asList(new String[]{ "", "x001", "x002", "x003", "x004" })));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAcceptIllegalArgumentException() {
		new ColumnNameNotEmptyExpression("col").accept(Arrays.asList(new String[]{ "col0", "col1", "col2" }), null);
	}

	@Test
	public void testToString() {
		assertThat(new ColumnNameNotEmptyExpression("col").toString(), is("ColumnNameNotEmptyExpression"));
		
	}

}