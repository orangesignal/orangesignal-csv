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

import java.util.Collection;
import java.util.List;

/**
 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値群のいずれとも等しくないかどうかでフィルタを適用する区切り文字形式データフィルタの実装です。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public class ColumnPositionNotInExpression extends ColumnPositionCriteriasExpression {

	/**
	 * コンストラクタです。
	 * 
	 * @param position 項目位置
	 * @param criterias 判定基準値群
	 * @throws IllegalArgumentException <code>criterias</code> が <code>null</code> の場合
	 */
	protected ColumnPositionNotInExpression(final int position, final Collection<String> criterias) {
		super(position, criterias);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param position 項目位置
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException <code>criterias</code> が <code>null</code> の場合
	 */
	protected ColumnPositionNotInExpression(final int position, final Collection<String> criterias, final boolean ignoreCase) {
		super(position, criterias, ignoreCase);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param position 項目位置
	 * @param criterias 判定基準値群
	 * @throws IllegalArgumentException <code>criterias</code> が <code>null</code> の場合
	 */
	protected ColumnPositionNotInExpression(final int position, final String... criterias) {
		super(position, criterias);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param position 項目位置
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException <code>criterias</code> が <code>null</code> の場合
	 */
	protected ColumnPositionNotInExpression(final int position, final String[] criterias, final boolean ignoreCase) {
		super(position, criterias, ignoreCase);
	}

	@Override
	public boolean accept(final List<String> values) {
		return CsvExpressionUtils.notIn(values, position, criterias, ignoreCase);
	}

	@Override
	public boolean accept(final List<String> header, final List<String> values) {
		return CsvExpressionUtils.notIn(values, position, criterias, ignoreCase);
	}

}
