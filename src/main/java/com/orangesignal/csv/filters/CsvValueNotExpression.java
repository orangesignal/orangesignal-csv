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

import java.util.List;

/**
 * 指定された区切り文字形式データの値リストでフィルタする区切り文字形式データフィルタの論理否定でフィルタを適用する区切り文字形式データフィルタの実装です。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public class CsvValueNotExpression implements CsvValueFilter {

	/**
	 * 区切り文字形式データフィルタを保持します。
	 */
	private CsvValueFilter filter;

	/**
	 * コンストラクタです。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>filter</code> が <code>null</code> の場合
	 */
	protected CsvValueNotExpression(final CsvValueFilter filter) {
		if (filter == null) {
			throw new IllegalArgumentException(String.format("%s must not be null", CsvValueFilter.class.getSimpleName()));
		}
		this.filter = filter;
	}

	@Override
	public boolean accept(final List<String> values) {
		return !filter.accept(values);
	}

	@Override
	public String toString() {
		final String name = getClass().getName();
		final int period = name.lastIndexOf('.');
		return period > 0 ? name.substring(period + 1) : name;
	}

}
