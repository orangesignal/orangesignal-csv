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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.orangesignal.csv.filters.CsvValueFilter;
import com.orangesignal.csv.filters.CsvValueOrExpression;

/**
 * {@link CsvValueOrExpression} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class CsvValueOrExpressionTest {

	@Test
	public void testCsvValueOrExpression() {
		new CsvValueOrExpression();
		new CsvValueOrExpression(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } });
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCsvOrExpressionIllegalArgumentException() {
		final CsvValueFilter[] filters = null;
		new CsvValueOrExpression(filters);
	}

	@Test
	public void testAccept() {
		assertFalse(new CsvValueOrExpression(
				new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } },
				new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } }
			).accept(null));

		assertTrue(new CsvValueOrExpression(
				new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } },
				new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } }
			).accept(null));

		assertTrue(new CsvValueOrExpression(
				new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } },
				new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } }
			).accept(null));

		assertTrue(new CsvValueOrExpression(
				new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } },
				new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } }
			).accept(null));

		final CsvValueOrExpression expr1 = new CsvValueOrExpression();
		expr1.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } });
		expr1.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } });
		assertFalse(expr1.accept(null));

		final CsvValueOrExpression expr2 = new CsvValueOrExpression();
		expr2.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } });
		expr2.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } });
		assertTrue(expr2.accept(null));

		final CsvValueOrExpression expr3 = new CsvValueOrExpression();
		expr3.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return false; } });
		expr3.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } });
		assertTrue(expr3.accept(null));

		final CsvValueOrExpression expr4 = new CsvValueOrExpression();
		expr4.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } });
		expr4.add(new CsvValueFilter() { @Override public boolean accept(final List<String> values) { return true; } });
		assertTrue(expr4.accept(null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAcceptIllegalArgumentException() {
		new CsvValueOrExpression().accept(Arrays.asList(new String[]{ null, "aaa", "bbb" }));
	}

	@Test
	public void testToString() {
		assertThat(new CsvValueOrExpression().toString(), is("CsvValueOrExpression"));
		
	}

}
