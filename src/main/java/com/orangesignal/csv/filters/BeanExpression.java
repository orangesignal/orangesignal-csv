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

package com.orangesignal.csv.filters;

/**
 * 指定された Java プログラム要素のフィールド値を判定してフィルタを適用する Java プログラム要素フィルタの基底クラスを提供します。
 * 
 * @author 杉澤 浩二
 * @since 1.2.3
 */
public abstract class BeanExpression implements BeanFilter {

	/**
	 * フィールド名を保持します。
	 */
	protected String name;

	/**
	 * コンストラクタです。
	 * 
	 * @param name フィールド名
	 * @throws IllegalArgumentException <code>name</code> が <code>null</code> の場合
	 */
	protected BeanExpression(final String name) {
		if (name == null) {
			throw new IllegalArgumentException("Field name must not be null");
		}
		this.name = name;
	}

	@Override
	public String toString() {
		final String className = getClass().getName();
		final int period = className.lastIndexOf('.');
		return (period > 0 ? className.substring(period + 1) : className);
	}

}