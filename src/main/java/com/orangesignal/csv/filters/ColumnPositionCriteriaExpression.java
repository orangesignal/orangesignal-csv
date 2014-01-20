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

/**
 * 指定された項目位置に対応する区切り文字形式データの値を指定された判定基準値と比較してフィルタを適用する区切り文字形式データフィルタの基底クラスを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public abstract class ColumnPositionCriteriaExpression extends ColumnPositionExpression {

	/**
	 * 判定基準値を保持します。
	 */
	protected String criteria;

	/**
	 * 大文字と小文字を区別するかどうかを保持します。
	 */
	protected boolean ignoreCase;

	/**
	 * コンストラクタです。
	 * 
	 * @param position 項目位置
	 * @param criteria 判定基準値
	 * @throws IllegalArgumentException <code>criteria</code> が <code>null</code> の場合
	 */
	protected ColumnPositionCriteriaExpression(final int position, final String criteria) {
		this(position, criteria, false);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param position 項目位置
	 * @param criteria 判定基準値
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException <code>criteria</code> が <code>null</code> の場合
	 */
	protected ColumnPositionCriteriaExpression(final int position, final String criteria, final boolean ignoreCase) {
		super(position);
		if (criteria == null) {
			throw new IllegalArgumentException("Criteria must not be null");
		}
		this.criteria = criteria;
		this.ignoreCase = ignoreCase;
	}

}
