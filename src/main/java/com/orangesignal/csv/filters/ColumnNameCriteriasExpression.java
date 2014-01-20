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

import java.util.Collection;

/**
 * 指定された項目名に対応する区切り文字形式データの値を指定された判定基準値群と比較してフィルタを適用する区切り文字形式データフィルタの基底クラスを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public abstract class ColumnNameCriteriasExpression extends ColumnNameExpression {

	/**
	 * 判定基準値群を保持します。
	 */
	protected String[] criterias;

	/**
	 * 大文字と小文字を区別するかどうかを保持します。
	 */
	protected boolean ignoreCase;

	/**
	 * コンストラクタです。
	 * 
	 * @param name 項目名
	 * @param criterias 判定基準値群
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected ColumnNameCriteriasExpression(final String name, final Collection<String> criterias) {
		this(name, criterias, false);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param name 項目名
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected ColumnNameCriteriasExpression(final String name, final Collection<String> criterias, final boolean ignoreCase) {
		super(name);
		if (criterias == null) {
			throw new IllegalArgumentException("Criterias must not be null");
		}
		this.criterias = criterias.toArray(new String[0]);
		this.ignoreCase = ignoreCase;
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param name 項目名
	 * @param criterias 判定基準値群
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected ColumnNameCriteriasExpression(final String name, final String... criterias) {
		this(name, criterias, false);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param name 項目名
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected ColumnNameCriteriasExpression(final String name, final String[] criterias, final boolean ignoreCase) {
		super(name);
		if (criterias == null) {
			throw new IllegalArgumentException("Criterias must not be null");
		}
		final String[] copy = new String[criterias.length];
		System.arraycopy(criterias, 0, copy, 0, copy.length);
		this.criterias = copy;
		this.ignoreCase = ignoreCase;
	}

}
