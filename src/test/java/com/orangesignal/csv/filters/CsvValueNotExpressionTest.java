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

import java.util.List;

import org.junit.Test;

/**
 * {@link CsvValueNotExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
public class CsvValueNotExpressionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testCsvValueNotExpressionIllegalArgumentException() {
		new CsvValueNotExpression(null);
	}

	@Test
	public void testAccept() {
		assertTrue(new CsvValueNotExpression(
				new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } }
			).accept(null));
		assertFalse(new CsvValueNotExpression(
				new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } }
			).accept(null));
	}

	@Test
	public void testToString() {
		assertThat(new CsvValueNotExpression(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } }).toString(), is("CsvValueNotExpression"));
		
	}

}