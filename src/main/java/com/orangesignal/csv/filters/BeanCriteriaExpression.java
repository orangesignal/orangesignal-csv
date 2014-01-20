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

import java.util.Comparator;

/**
 * 指定された Java プログラム要素のフィールド値を指定された判定基準値と比較してフィルタを適用する Java プログラム要素フィルタの基底クラスを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public abstract class BeanCriteriaExpression extends BeanExpression {

	/**
	 * 判定基準値を保持します。
	 */
	protected Object criteria;

	/**
	 * コンパレータを保持します。
	 */
	@SuppressWarnings("rawtypes")
	protected Comparator comparator;

	/**
	 * 大文字と小文字を区別するかどうかを保持します。
	 */
	protected boolean ignoreCase;

	/**
	 * コンストラクタです。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected BeanCriteriaExpression(final String name, final Object criteria) {
		this(name, criteria, null);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param comparator コンパレータ (オプション)
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected BeanCriteriaExpression(final String name, final Object criteria, @SuppressWarnings("rawtypes") final Comparator comparator) {
		super(name);
		if (criteria == null) {
			throw new IllegalArgumentException("Criteria must not be null");
		}
		this.criteria = criteria;
		this.comparator = comparator;
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected BeanCriteriaExpression(final String name, final String criteria, final boolean ignoreCase) {
		super(name);
		if (criteria == null) {
			throw new IllegalArgumentException("Criteria must not be null");
		}
		this.criteria = criteria;
		this.ignoreCase = ignoreCase;
	}

}
