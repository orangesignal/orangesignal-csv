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

package com.orangesignal.csv.handlers;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Java プログラム要素の並び替え条件を使用して比較を行うコンパレータです。
 * 
 * @author 杉澤 浩二
 * @since 1.2.8
 */
public class BeanOrderComparator implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = -6296794293732267936L;

	/**
	 * 並び替え条件を保持します。
	 */
	private final List<BeanOrder> orders;

	/**
	 * コンストラクタです。
	 * 
	 * @param orders 並び替え条件
	 * @throws IllegalArgumentException {@code orders} が {@code null} の場合
	 */
	protected BeanOrderComparator(final List<BeanOrder> orders) {
		if (orders == null) {
			throw new IllegalArgumentException("orders must not be null");
		}
		this.orders = orders;
	}

	@Override
	public int compare(final Object o1, final Object o2) {
		if (o1 == null || o2 == null) {
			throw new IllegalArgumentException("bean must not be null");
		}
		for (final BeanOrder order : orders) {
			final int c = order.compare(o1, o2);
			if (c == 0) {
				continue;
			}
			return c;
		}
		return 0;
	}

	/**
	 * 指定された並び替え条件が示す順序に従って、指定されたリストをソートします。
	 * 
	 * @param beans ソートされるリスト
	 * @param orders 並び替え条件
	 * @throws IllegalArgumentException {@code orders} が {@code null} の場合
	 * @see Collections#sort(List, Comparator)
	 */
	public static void sort(List<?> beans, final List<BeanOrder> orders) {
		if (beans.size() > 1) {
			Collections.sort(beans, new BeanOrderComparator(orders));
		}
	}

}