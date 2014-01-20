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

/**
 * 指定された Java プログラム要素フィルタ群の論理和でフィルタを適用する Java プログラム要素フィルタの実装です。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public class BeanOrExpression extends BeanLogicalExpression {

	/**
	 * デフォルトコンストラクタです。
	 */
	public BeanOrExpression() {
		super();
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param filters Java プログラム要素フィルタ群
	 * @throws IllegalArgumentException <code>filters</code> が <code>null</code> の場合
	 */
	protected BeanOrExpression(final BeanFilter... filters) {
		super(filters);
	}

	@Override
	public boolean accept(final Object bean) throws IOException {
		if (filters.isEmpty()) {
			throw new IllegalArgumentException("Filters must not be empty");
		}
		for (final BeanFilter filter : filters) {
			if (filter.accept(bean)) {
				return true;
			}
		}
		return false;
	}

}
