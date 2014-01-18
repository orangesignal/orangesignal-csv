/*
 * Copyright (c) 2009-2013 OrangeSignal.com All rights reserved.
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

import java.io.IOException;
import java.util.Comparator;

/**
 * 指定された Java プログラム要素のフィールド値が下限値から上限値の範囲かどうかでフィルタを適用する Java プログラム要素フィルタの実装です。
 * 
 * @author 杉澤 浩二
 * @since 1.2.3
 */
public class BeanBetweenExpression extends BeanExpression {

	/**
	 * 下限値を保持します。
	 */
	private Object low;

	/**
	 * 上限値を保持します。
	 */
	private Object high;

	/**
	 * コンパレータを保持します。
	 */
	@SuppressWarnings("rawtypes")
	private Comparator comparator;

	/**
	 * コンストラクタです。
	 * 
	 * @param name フィールド名
	 * @param low 下限値
	 * @param high 上限値
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected BeanBetweenExpression(final String name, final Object low, final Object high) {
		this(name, low, high, null);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param name フィールド名
	 * @param low 下限値
	 * @param high 上限値
	 * @param comparator コンパレータ (オプション)
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected BeanBetweenExpression(final String name, final Object low, final Object high, @SuppressWarnings("rawtypes") final Comparator comparator) {
		super(name);
		if (low == null || high == null) {
			throw new IllegalArgumentException("Low or High must not be null");
		}
		this.low = low;
		this.high = high;
		this.comparator = comparator;
	}

	@Override
	public boolean accept(final Object bean) throws IOException {
		return BeanExpressionUtils.between(bean, name, low, high, comparator);
	}

}