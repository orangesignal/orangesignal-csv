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
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * {@link ColumnNameRegexExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
public class ColumnNameRegexExpressionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testColumnNameRegexExpressionIllegalArgumentException1() {
		final String pattern = null;
		new ColumnNameRegexExpression("col", pattern);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColumnNameRegexExpressionIllegalArgumentException2() {
		final String pattern = null;
		new ColumnNameRegexExpression("col", pattern, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColumnNameRegexExpressionIllegalArgumentException3() {
		final String pattern = null;
		new ColumnNameRegexExpression("col", pattern, Pattern.CASE_INSENSITIVE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColumnNameRegexExpressionIllegalArgumentException4() {
		final Pattern pattern = null;
		new ColumnNameRegexExpression("col", pattern);
	}

	@Test
	public void testColumnNameRegexExpression() {
		new ColumnNameRegexExpression("col", "^.*$");
		new ColumnNameRegexExpression("col", "^.*$", false);
		new ColumnNameRegexExpression("col", "^.*$", true);
		new ColumnNameRegexExpression("col", "^.*$", Pattern.CASE_INSENSITIVE);
		new ColumnNameRegexExpression("col", Pattern.compile("^.*$"));
	}

	@Test
	public void testAccept() {
		final List<String> header = Arrays.asList(new String[]{ "col0", "col1", "col2" });
		final List<String> values = Arrays.asList(new String[]{ null, "aaa", "bbb" });
		assertFalse(new ColumnNameRegexExpression("col0", "^[a]+$").accept(header, values));
		assertTrue(new ColumnNameRegexExpression("col1", "^[a]+$").accept(header, values));
		assertFalse(new ColumnNameRegexExpression("col2", "^[a]+$").accept(header, values));
		assertFalse(new ColumnNameRegexExpression("col0", "^[a]+$", false).accept(header, values));
		assertTrue(new ColumnNameRegexExpression("col1", "^[a]+$", false).accept(header, values));
		assertFalse(new ColumnNameRegexExpression("col2", "^[a]+$", false).accept(header, values));
		assertFalse(new ColumnNameRegexExpression("col0", "^[A]+$", true).accept(header, values));
		assertTrue(new ColumnNameRegexExpression("col1", "^[A]+$", true).accept(header, values));
		assertFalse(new ColumnNameRegexExpression("col2", "^[A]+$", true).accept(header, values));
		assertFalse(new ColumnNameRegexExpression("col0", "^[A]+$", Pattern.CASE_INSENSITIVE).accept(header, values));
		assertTrue(new ColumnNameRegexExpression("col1", "^[A]+$", Pattern.CASE_INSENSITIVE).accept(header, values));
		assertFalse(new ColumnNameRegexExpression("col2", "^[A]+$", Pattern.CASE_INSENSITIVE).accept(header, values));
		assertFalse(new ColumnNameRegexExpression("col0", Pattern.compile("^[a]+$")).accept(header, values));
		assertTrue(new ColumnNameRegexExpression("col1", Pattern.compile("^[a]+$")).accept(header, values));
		assertFalse(new ColumnNameRegexExpression("col2", Pattern.compile("^[a]+$")).accept(header, values));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAcceptIllegalArgumentException() {
		new ColumnNameRegexExpression("col", "^.*$").accept(Arrays.asList(new String[]{ "col0", "col1", "col2" }), null);
	}

	@Test
	public void testToString() {
		assertThat(new ColumnNameRegexExpression("col", "^.*$").toString(), is("ColumnNameRegexExpression"));
		
	}

}