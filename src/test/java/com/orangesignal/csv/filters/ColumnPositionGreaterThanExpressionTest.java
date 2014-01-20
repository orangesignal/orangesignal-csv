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
 * {@link ColumnPositionGreaterThanExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
public class ColumnPositionGreaterThanExpressionTest {

	@Test
	public void testColumnPositionGreaterThanExpression() {
		new ColumnPositionGreaterThanExpression(0, "100");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testColumnPositionGreaterThanExpressionIllegalArgumentException() {
		new ColumnPositionGreaterThanExpression(0, null);
	}

	@Test
	public void testAcceptListOfString() {
		assertFalse(new ColumnPositionGreaterThanExpression(0, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new ColumnPositionGreaterThanExpression(1, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new ColumnPositionGreaterThanExpression(2, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertTrue(new ColumnPositionGreaterThanExpression(3, "100").accept(Arrays.asList(new String[]{ null, "099", "100", "101" })));
	}

	@Test
	public void testAcceptListOfStringListOfString() {
		assertFalse(new ColumnPositionGreaterThanExpression(0, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new ColumnPositionGreaterThanExpression(1, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertFalse(new ColumnPositionGreaterThanExpression(2, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
		assertTrue(new ColumnPositionGreaterThanExpression(3, "100").accept(null, Arrays.asList(new String[]{ null, "099", "100", "101" })));
	}

	@Test
	public void testToString() {
		assertThat(new ColumnPositionGreaterThanExpression(0, "100").toString(), is("ColumnPositionGreaterThanExpression"));
		
	}

}