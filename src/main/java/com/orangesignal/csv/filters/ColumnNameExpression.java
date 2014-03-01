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

package com.orangesignal.csv.filters;

/**
 * 指定された項目名に対応する区切り文字形式データの値を判定してフィルタを適用する区切り文字形式データフィルタの基底クラスを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public abstract class ColumnNameExpression implements CsvNamedValueFilter {

	/**
	 * 項目名を保持します。
	 */
	protected String name;

	/**
	 * コンストラクタです。
	 * 
	 * @param name 項目名
	 * @throws IllegalArgumentException <code>name</code> が <code>null</code> の場合
	 */
	protected ColumnNameExpression(final String name) {
		if (name == null) {
			throw new IllegalArgumentException("Column name must not be null");
		}
		this.name = name;
	}

	@Override
	public String toString() {
		final String className = getClass().getName();
		final int period = className.lastIndexOf('.');
		return period > 0 ? className.substring(period + 1) : className;
	}

}