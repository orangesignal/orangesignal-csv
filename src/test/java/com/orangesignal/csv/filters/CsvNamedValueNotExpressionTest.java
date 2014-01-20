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
 * {@link CsvNamedValueNotExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
public class CsvNamedValueNotExpressionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testCsvNamedValueNotExpressionIllegalArgumentException() {
		new CsvNamedValueNotExpression(null);
	}

	@Test
	public void testAccept() {
		assertTrue(new CsvNamedValueNotExpression(
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } }
			).accept(null, null));
		assertFalse(new CsvNamedValueNotExpression(
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } }
			).accept(null, null));
	}

	@Test
	public void testToString() {
		assertThat(new CsvNamedValueNotExpression(
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } }
			).toString(), is("CsvNamedValueNotExpression"));
		
	}

}