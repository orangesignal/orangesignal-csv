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
 * 指定された区切り文字形式データの値リストでフィルタする区切り文字形式データフィルタ群の論理積でフィルタを適用する区切り文字形式データフィルタの実装です。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public class CsvValueAndExpression extends CsvValueLogicalExpression {

	/**
	 * デフォルトコンストラクタです。
	 */
	public CsvValueAndExpression() {
		super();
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param filters 区切り文字形式データフィルタ群
	 * @throws IllegalArgumentException <code>filters</code> が <code>null</code> の場合
	 */
	protected CsvValueAndExpression(final CsvValueFilter... filters) {
		super(filters);
	}

	@Override
	public boolean accept(final List<String> values) {
		if (filters.isEmpty()) {
			throw new IllegalArgumentException("Filters must not be empty");
		}
		for (final CsvValueFilter filter : filters) {
			if (!filter.accept(values)) {
				return false;
			}
		}
		return true;
	}

}
