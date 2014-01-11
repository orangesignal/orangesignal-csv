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

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.orangesignal.csv.filters.BeanAndExpression;
import com.orangesignal.csv.filters.BeanFilter;

/**
 * {@link BeanAndExpression} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class BeanAndExpressionTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testBeanAndExpression() {
		// Act
		new BeanAndExpression();
		// Act
		new BeanAndExpression(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } });
	}

	@Test
	public void testBeanAndExpressionIllegalArgumentException() {
		// Arrange
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("BeanFilter must not be null");
		final BeanFilter[] filters = null;
		// Act
		new BeanAndExpression(filters);
	}

	@Test
	public void testAccept() throws IOException {
		assertFalse(new BeanAndExpression(
				new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } },
				new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } }
			).accept(null));

		assertFalse(new BeanAndExpression(
				new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } },
				new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } }
			).accept(null));

		assertFalse(new BeanAndExpression(
				new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } },
				new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } }
			).accept(null));

		assertTrue(new BeanAndExpression(
				new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } },
				new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } }
			).accept(null));

		final BeanAndExpression expr1 = new BeanAndExpression();
		expr1.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } });
		expr1.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } });
		assertFalse(expr1.accept(null));

		final BeanAndExpression expr2 = new BeanAndExpression();
		expr2.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } });
		expr2.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } });
		assertFalse(expr2.accept(null));

		final BeanAndExpression expr3 = new BeanAndExpression();
		expr3.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } });
		expr3.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } });
		assertFalse(expr3.accept(null));

		final BeanAndExpression expr4 = new BeanAndExpression();
		expr4.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } });
		expr4.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } });
		assertTrue(expr4.accept(null));
	}

	@Test
	public void testAcceptIllegalArgumentException() throws IOException {
		// Arrange
		exception.expect(IllegalArgumentException.class);
		// Act
		new BeanAndExpression().accept(null);
	}

	@Test
	public void testToString() {
		assertThat(new BeanAndExpression().toString(), is("BeanAndExpression"));
	}

}
