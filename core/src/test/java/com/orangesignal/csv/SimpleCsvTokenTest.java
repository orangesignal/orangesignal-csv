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

package com.orangesignal.csv;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.orangesignal.csv.SimpleCsvToken;

/**
 * {@link SimpleCsvToken} クラスの単体テストです。
 *
 * @author 杉澤 浩二
 */
public class SimpleCsvTokenTest {

	@Test
	public void testSimpleCsvToken() {
		new SimpleCsvToken();
		new SimpleCsvToken("a\nb\nc", 1, 3, true);
	}

	@Test
	public void testGetValue() {
		assertThat(new SimpleCsvToken("a\nb\nc", 1, 3, true).getValue(), is("a\nb\nc"));
	}

	@Test
	public void testGetStartLineNumber() {
		assertThat(new SimpleCsvToken("a\nb\nc", 1, 3, true).getStartLineNumber(), is(1));
	}

	@Test
	public void testGetEndLineNumber() {
		assertThat(new SimpleCsvToken("a\nb\nc", 1, 3, true).getEndLineNumber(), is(3));
	}

	@Test
	public void testIsEnclosed() {
		assertThat(new SimpleCsvToken("a\nb\nc", 1, 3, true).isEnclosed(), is(true));
	}

}
