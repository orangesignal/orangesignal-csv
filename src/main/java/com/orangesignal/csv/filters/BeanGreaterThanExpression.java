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
 * 指定された Java プログラム要素のフィールド値が判定基準値より大きいかどうかでフィルタを適用する Java プログラム要素フィルタの実装です。
 * 
 * @author 杉澤 浩二
 * @since 1.2.3
 */
public class BeanGreaterThanExpression extends BeanCriteriaExpression {

	/**
	 * コンストラクタです。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected BeanGreaterThanExpression(final String name, final Object criteria) {
		super(name, criteria);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param comparator コンパレータ (オプション)
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected BeanGreaterThanExpression(final String name, final Object criteria, @SuppressWarnings("rawtypes") final Comparator comparator) {
		super(name, criteria, comparator);
	}

	@Override
	public boolean accept(final Object bean) throws IOException {
		return BeanExpressionUtils.gt(bean, name, criteria, comparator);
	}

}
