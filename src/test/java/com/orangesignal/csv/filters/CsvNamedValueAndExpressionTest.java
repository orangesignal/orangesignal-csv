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
 * {@link CsvNamedValueAndExpression} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
public class CsvNamedValueAndExpressionTest {

	@Test
	public void testCsvNamedValueAndExpression() {
		new CsvNamedValueAndExpression();
		new CsvNamedValueAndExpression(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } });
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCsvNamedValueAndExpressionIllegalArgumentException() {
		final CsvNamedValueFilter[] filters = null;
		new CsvNamedValueAndExpression(filters);
	}

	@Test
	public void testAccept() {
		assertFalse(new CsvNamedValueAndExpression(
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } },
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } }
			).accept(null, null));

		assertFalse(new CsvNamedValueAndExpression(
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } },
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } }
			).accept(null, null));

		assertFalse(new CsvNamedValueAndExpression(
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } },
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } }
			).accept(null, null));

		assertTrue(new CsvNamedValueAndExpression(
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } },
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } }
			).accept(null, null));

		final CsvNamedValueAndExpression expr1 = new CsvNamedValueAndExpression();
		expr1.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } });
		expr1.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } });
		assertFalse(expr1.accept(null, null));

		final CsvNamedValueAndExpression expr2 = new CsvNamedValueAndExpression();
		expr2.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } });
		expr2.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } });
		assertFalse(expr2.accept(null, null));

		final CsvNamedValueAndExpression expr3 = new CsvNamedValueAndExpression();
		expr3.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } });
		expr3.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } });
		assertFalse(expr3.accept(null, null));

		final CsvNamedValueAndExpression expr4 = new CsvNamedValueAndExpression();
		expr4.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } });
		expr4.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } });
		assertTrue(expr4.accept(null, null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAcceptIllegalArgumentException() {
		new CsvNamedValueAndExpression().accept(null, null);
	}

	@Test
	public void testToString() {
		assertThat(new CsvNamedValueAndExpression().toString(), is("CsvNamedValueAndExpression"));
		
	}

}