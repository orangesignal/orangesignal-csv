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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * 区切り文字形式データの値リストでフィルタする区切り文字形式データフィルタを論理演算する区切り文字形式データフィルタの基底クラスを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public abstract class CsvValueLogicalExpression implements CsvValueFilter {

	/**
	 * 区切り文字形式データフィルタのコレクションを保持します。
	 */
	protected final Collection<CsvValueFilter> filters;

	/**
	 * デフォルトコンストラクタです。
	 */
	protected CsvValueLogicalExpression() {
		filters = new ArrayList<CsvValueFilter>();
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param filters 区切り文字形式データフィルタ群
	 * @throws IllegalArgumentException <code>filters</code> が <code>null</code> の場合
	 */
	protected CsvValueLogicalExpression(final CsvValueFilter... filters) {
		if (filters == null) {
			throw new IllegalArgumentException(String.format("%s must not be null", CsvValueFilter.class.getSimpleName()));
		}
		this.filters = Arrays.asList(filters);
	}

	/**
	 * 指定された区切り文字形式データフィルタを追加します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 */
	public void add(final CsvValueFilter filter) {
		filters.add(filter);
	}

	@Override
	public String toString() {
		final String name = getClass().getName();
		final int period = name.lastIndexOf('.');
		return period > 0 ? name.substring(period + 1) : name;
	}

}
