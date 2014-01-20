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
 * {@link ColumnNameInExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
public class ColumnNameInExpressionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testColumnNameInExpressionIllegalArgumentException2() {
		final String[] criterias = null;
		new ColumnNameInExpression("col", criterias);
	}

	@Test
	public void testAccept() {
		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2", "col3", "col4" });
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb", "ccc", "ddd" });
		assertFalse(new ColumnNameInExpression("col0", "a", "aa", "aaa").accept(header, values));
		assertTrue(new ColumnNameInExpression("col1", "a", "aa", "aaa").accept(header, values));
		assertFalse(new ColumnNameInExpression("col2", "a", "aa", "aaa").accept(header, values));
		assertFalse(new ColumnNameInExpression("col0", new String[]{ "a", "aa", "aaa" }, false).accept(header, values));
		assertTrue(new ColumnNameInExpression("col1", new String[]{ "a", "aa", "aaa" }, false).accept(header, values));
		assertFalse(new ColumnNameInExpression("col2", new String[]{ "a", "aa", "aaa" }, false).accept(header, values));
		assertFalse(new ColumnNameInExpression("col0", new String[]{ "A", "AA", "AAA" }, true).accept(header, values));
		assertTrue(new ColumnNameInExpression("col1", new String[]{ "A", "AA", "AAA" }, true).accept(header, values));
		assertFalse(new ColumnNameInExpression("col2", new String[]{ "A", "AA", "AAA" }, true).accept(header, values));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAcceptIllegalArgumentException() {
		new ColumnNameInExpression("col", "a", "aa", "aaa").accept(Arrays.asList(new String[]{ "col0", "col1", "col2" }), null);
	}

	@Test
	public void testToString() {
		assertThat(new ColumnNameInExpression("col", "a", "aa", "aaa").toString(), is("ColumnNameInExpression"));
		
	}

}