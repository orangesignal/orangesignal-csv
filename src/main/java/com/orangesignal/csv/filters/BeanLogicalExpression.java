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
 * Java プログラム要素フィルタを論理演算する Java プログラム要素フィルタの基底クラスを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public abstract class BeanLogicalExpression implements BeanFilter {

	/**
	 * Java プログラム要素フィルタのコレクションを保持します。
	 */
	protected final Collection<BeanFilter> filters;

	/**
	 * デフォルトコンストラクタです。
	 */
	protected BeanLogicalExpression() {
		filters = new ArrayList<BeanFilter>();
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param filters Java プログラム要素フィルタ群
	 * @throws IllegalArgumentException <code>filters</code> が <code>null</code> の場合
	 */
	protected BeanLogicalExpression(final BeanFilter... filters) {
		if (filters == null) {
			throw new IllegalArgumentException(String.format("%s must not be null", BeanFilter.class.getSimpleName()));
		}
		this.filters = Arrays.asList(filters);
	}

	/**
	 * 指定された Java プログラム要素フィルタを追加します。
	 * 
	 * @param filter Java プログラム要素フィルタ
	 */
	public void add(final BeanFilter filter) {
		filters.add(filter);
	}

	@Override
	public String toString() {
		final String name = getClass().getName();
		final int period = name.lastIndexOf('.');
		return period > 0 ? name.substring(period + 1) : name;
	}

}
