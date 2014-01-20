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

import org.junit.Test;

/**
 * {@link ColumnPositionNotEqualExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
public class ColumnPositionNotEqualExpressionTest {

	@Test
	public void testColumnPositionNotEqualExpression() {
		new ColumnPositionNotEqualExpression(0, "test");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColumnPositionNotEqualExpressionIllegalArgumentException() {
		new ColumnPositionNotEqualExpression(0, null);
	}

	@Test
	public void testAcceptListOfString() {
		assertTrue(new ColumnPositionNotEqualExpression(0, "aaa").accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new ColumnPositionNotEqualExpression(1, "aaa").accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEqualExpression(2, "aaa").accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEqualExpression(0, "aaa", false).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new ColumnPositionNotEqualExpression(1, "aaa", false).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEqualExpression(2, "aaa", false).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEqualExpression(0, "AAA", true).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new ColumnPositionNotEqualExpression(1, "AAA", true).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEqualExpression(2, "AAA", true).accept(Arrays.asList(new String[]{ null, "aaa", "bbb" })));
	}

	@Test
	public void testAcceptListOfStringListOfString() {
		assertTrue(new ColumnPositionNotEqualExpression(0, "aaa").accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new ColumnPositionNotEqualExpression(1, "aaa").accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEqualExpression(2, "aaa").accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEqualExpression(0, "aaa", false).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new ColumnPositionNotEqualExpression(1, "aaa", false).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEqualExpression(2, "aaa", false).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEqualExpression(0, "AAA", true).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertFalse(new ColumnPositionNotEqualExpression(1, "AAA", true).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
		assertTrue(new ColumnPositionNotEqualExpression(2, "AAA", true).accept(null, Arrays.asList(new String[]{ null, "aaa", "bbb" })));
	}

	@Test
	public void testToString() {
		assertThat(new ColumnPositionNotEqualExpression(0, "aaa").toString(), is("ColumnPositionNotEqualExpression"));
		
	}

}