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
 * 指定された Java プログラム要素のフィールド値が空でないかどうかでフィルタを適用する Java プログラム要素フィルタの実装です。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public class BeanNotEmptyExpression extends BeanExpression {

	/**
	 * コンストラクタです。
	 * 
	 * @param name フィールド名
	 * @throws IllegalArgumentException <code>name</code> が <code>null</code> の場合
	 */
	protected BeanNotEmptyExpression(final String name) {
		super(name);
	}

	@Override
	public boolean accept(final Object bean) throws IOException {
		return BeanExpressionUtils.isNotEmpty(bean, name);
	}

}