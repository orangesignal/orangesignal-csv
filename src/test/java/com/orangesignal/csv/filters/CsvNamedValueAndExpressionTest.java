/*
 * Copyright (c) 2009 OrangeSignal.com All rights reserved.
 * 
 * これは Apache ライセンス Version 2.0 (以下、このライセンスと記述) に
 * 従っています。このライセンスに準拠する場合以外、このファイルを使用
 * してはなりません。このライセンスのコピーは以下から入手できます。
 * 
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 * 
 * 適用可能な法律がある、あるいは文書によって明記されている場合を除き、
 * このライセンスの下で配布されているソフトウェアは、明示的であるか暗黙の
 * うちであるかを問わず、「保証やあらゆる種類の条件を含んでおらず」、
 * 「あるがまま」の状態で提供されるものとします。
 * このライセンスが適用される特定の許諾と制限については、このライセンス
 * を参照してください。
 */

package com.orangesignal.csv.filters;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.orangesignal.csv.filters.CsvNamedValueAndExpression;
import com.orangesignal.csv.filters.CsvNamedValueFilter;

/**
 * {@link CsvNamedValueAndExpression} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
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
