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
 * {@link CsvValueAndExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
public class CsvValueAndExpressionTest {

	@Test
	public void testCsvValueAndExpression() {
		new CsvValueAndExpression();
		new CsvValueAndExpression(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } });
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCsvAndExpressionIllegalArgumentException() {
		final CsvValueFilter[] filters = null;
		new CsvValueAndExpression(filters);
	}

	@Test
	public void testAccept() {
		assertFalse(new CsvValueAndExpression(
				new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } },
				new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } }
			).accept(null));

		assertFalse(new CsvValueAndExpression(
				new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } },
				new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } }
			).accept(null));

		assertFalse(new CsvValueAndExpression(
				new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } },
				new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } }
			).accept(null));

		assertTrue(new CsvValueAndExpression(
				new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } },
				new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } }
			).accept(null));

		final CsvValueAndExpression expr1 = new CsvValueAndExpression();
		expr1.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } });
		expr1.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } });
		assertFalse(expr1.accept(null));

		final CsvValueAndExpression expr2 = new CsvValueAndExpression();
		expr2.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } });
		expr2.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } });
		assertFalse(expr2.accept(null));

		final CsvValueAndExpression expr3 = new CsvValueAndExpression();
		expr3.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } });
		expr3.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } });
		assertFalse(expr3.accept(null));

		final CsvValueAndExpression expr4 = new CsvValueAndExpression();
		expr4.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } });
		expr4.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } });
		assertTrue(expr4.accept(null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAcceptIllegalArgumentException() {
		new CsvValueAndExpression().accept(Arrays.asList(new String[]{ null, "aaa", "bbb" }));
	}

	@Test
	public void testToString() {
		assertThat(new CsvValueAndExpression().toString(), is("CsvValueAndExpression"));
		
	}

}