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
import com.orangesignal.csv.filters.CsvNamedValueNotExpression;

/**
 * {@link CsvNamedValueNotExpression} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
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
