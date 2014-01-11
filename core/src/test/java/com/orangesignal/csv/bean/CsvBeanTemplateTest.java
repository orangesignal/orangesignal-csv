/*
 * Copyright (c) 2013 OrangeSignal.com All rights reserved.
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

package com.orangesignal.csv.bean;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.orangesignal.csv.bean.CsvBeanTemplate;
import com.orangesignal.csv.model.SampleBean;

/**
 * {@link CsvBeanTemplate} クラスの単体テストです。
 *
 * @author 杉澤 浩二
 * @since 1.4.0
 */
public class CsvBeanTemplateTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testIncludesIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Only includes or excludes may be specified.");
		new CsvBeanTemplate<SampleBean>(SampleBean.class).excludes("aaa").includes("bbb");
	}

	@Test
	public void testExcludesIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Only includes or excludes may be specified.");
		new CsvBeanTemplate<SampleBean>(SampleBean.class).includes("aaa").excludes("bbb");
	}

}