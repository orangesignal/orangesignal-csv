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
import java.util.regex.Pattern;

/**
 * 指定された Java プログラム要素のフィールド値が正規表現パターンとマッチするかどうかでフィルタを適用する Java プログラム要素フィルタの実装です。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public class BeanRegexExpression extends BeanExpression {

	/**
	 * 正規表現パターンを保持します。
	 */
	private Pattern pattern;

	/**
	 * コンストラクタです。
	 * 
	 * @param name 項目名
	 * @param pattern 正規表現パターン
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected BeanRegexExpression(final String name, final String pattern) {
		this(name, pattern, 0);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param name 項目名
	 * @param pattern 正規表現パターン
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected BeanRegexExpression(final String name, final String pattern, final boolean ignoreCase) {
		this(name, pattern, ignoreCase ? Pattern.CASE_INSENSITIVE : 0);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param name 項目名
	 * @param pattern 正規表現パターン
	 * @param flags マッチフラグ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected BeanRegexExpression(final String name, final String pattern, final int flags) {
		super(name);
		if (pattern == null) {
			throw new IllegalArgumentException("Pattern must not be null");
		}
		this.pattern = Pattern.compile(pattern, flags);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param name 項目名
	 * @param pattern 正規表現パターン
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected BeanRegexExpression(final String name, final Pattern pattern) {
		super(name);
		if (pattern == null) {
			throw new IllegalArgumentException("Pattern must not be null");
		}
		this.pattern = pattern;
	}

	@Override
	public boolean accept(final Object bean) throws IOException {
		return BeanExpressionUtils.regex(bean, name, pattern);
	}

}
