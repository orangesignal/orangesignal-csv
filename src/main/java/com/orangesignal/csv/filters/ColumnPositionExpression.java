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
 * 指定された項目位置に対応する区切り文字形式データの値を判定してフィルタを適用する区切り文字形式データフィルタの基底クラスを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public abstract class ColumnPositionExpression implements CsvValueFilter, CsvNamedValueFilter {

	/**
	 * 項目位置を保持します。
	 */
	protected int position;

	/**
	 * コンストラクタです。
	 * 
	 * @param position 項目位置
	 */
	protected ColumnPositionExpression(final int position) {
		this.position = position;
	}

	@Override
	public String toString() {
		final String name = getClass().getName();
		final int period = name.lastIndexOf('.');
		return period > 0 ? name.substring(period + 1) : name;
	}

}
