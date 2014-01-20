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
 * 指定された Java プログラム要素のフィールド値がが判定基準値以下かどうかでフィルタを適用する Java プログラム要素フィルタの実装です。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public class BeanLessThanOrEqualExpression extends BeanCriteriaExpression {

	/**
	 * コンストラクタです。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected BeanLessThanOrEqualExpression(final String name, final Object criteria) {
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
	protected BeanLessThanOrEqualExpression(final String name, final Object criteria, @SuppressWarnings("rawtypes") final Comparator comparator) {
		super(name, criteria, comparator);
	}

	@Override
	public boolean accept(final Object bean) throws IOException {
		return BeanExpressionUtils.le(bean, name, criteria, comparator);
	}

}