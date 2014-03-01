/*
 * Copyright 2013 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.orangesignal.csv.handlers;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Java プログラム要素の並び替え条件を使用して比較を行うコンパレータです。
 * 
 * @author Koji Sugisawa
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
			if (c != 0) {
				return c;
			}
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
	public static void sort(final List<?> beans, final List<BeanOrder> orders) {
		if (beans.size() > 1) {
			Collections.sort(beans, new BeanOrderComparator(orders));
		}
	}

}