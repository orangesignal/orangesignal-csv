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

import com.orangesignal.csv.filters.CsvNamedValueFilter;
import com.orangesignal.csv.filters.CsvNamedValueOrExpression;

/**
 * {@link CsvNamedValueOrExpression} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class CsvNamedValueOrExpressionTest {

	@Test
	public void testCsvNamedValueOrExpression() {
		new CsvNamedValueOrExpression();
		new CsvNamedValueOrExpression(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } });
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCsvNameOrExpressionCsvNameFilterArrayIllegalArgumentException() {
		final CsvNamedValueFilter[] filters = null;
		new CsvNamedValueOrExpression(filters);
	}

	@Test
	public void testAccept() {
		assertFalse(new CsvNamedValueOrExpression(
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } },
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } }
			).accept(null, null));

		assertTrue(new CsvNamedValueOrExpression(
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } },
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } }
			).accept(null, null));

		assertTrue(new CsvNamedValueOrExpression(
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } },
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } }
			).accept(null, null));

		assertTrue(new CsvNamedValueOrExpression(
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } },
				new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } }
			).accept(null, null));

		final CsvNamedValueOrExpression expr1 = new CsvNamedValueOrExpression();
		expr1.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } });
		expr1.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } });
		assertFalse(expr1.accept(null, null));

		final CsvNamedValueOrExpression expr2 = new CsvNamedValueOrExpression();
		expr2.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } });
		expr2.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } });
		assertTrue(expr2.accept(null, null));

		final CsvNamedValueOrExpression expr3 = new CsvNamedValueOrExpression();
		expr3.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return false; } });
		expr3.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } });
		assertTrue(expr3.accept(null, null));

		final CsvNamedValueOrExpression expr4 = new CsvNamedValueOrExpression();
		expr4.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } });
		expr4.add(new CsvNamedValueFilter() { @Override public boolean accept(final List<String> header, final List<String> values) { return true; } });
		assertTrue(expr4.accept(null, null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAcceptIllegalArgumentException() {
		new CsvNamedValueOrExpression().accept(null, null);
	}

	@Test
	public void testToString() {
		assertThat(new CsvNamedValueOrExpression().toString(), is("CsvNamedValueOrExpression"));
		
	}

}
