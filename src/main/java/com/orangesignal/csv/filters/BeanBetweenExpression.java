/*
 * Copyright 2009-2013 the original author or authors.
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

package com.orangesignal.csv.filters;

import java.io.IOException;
import java.util.Comparator;

/**
 * 指定された Java プログラム要素のフィールド値が下限値から上限値の範囲かどうかでフィルタを適用する Java プログラム要素フィルタの実装です。
 * 
 * @author Koji Sugisawa
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