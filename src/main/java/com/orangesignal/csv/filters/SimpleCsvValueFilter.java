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
import java.util.regex.Pattern;

/**
 * DSL (Domain Specific Language) 形式でスマートなフィルタ条件の定義が可能な {@link CsvValueFilter} の実装クラスを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public class SimpleCsvValueFilter implements CsvValueFilter {

	private CsvValueLogicalExpression expr;

	/**
	 * デフォルトコンストラクタです。
	 */
	public SimpleCsvValueFilter() {
		this(new CsvValueAndExpression());
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param expr 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>expr</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter(final CsvValueLogicalExpression expr) {
		if (expr == null) {
			throw new IllegalArgumentException(String.format("%s must not be null", CsvValueLogicalExpression.class.getSimpleName()));
		}
		this.expr = expr;
	}

	/**
	 * 指定された区切り文字形式データの値リストでフィルタする区切り文字形式データフィルタを追加します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 * @return このオブジェクトへの参照
	 */
	public SimpleCsvValueFilter add(final CsvValueFilter filter) {
		expr.add(filter);
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が <code>null</code> であるかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目位置
	 * @return このオブジェクトへの参照
	 */
	public SimpleCsvValueFilter isNull(final int position) {
		expr.add(CsvExpressions.isNull(position));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が <code>null</code> でないかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目位置
	 * @return このオブジェクトへの参照
	 */
	public SimpleCsvValueFilter isNotNull(final int position) {
		expr.add(CsvExpressions.isNotNull(position));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が空かどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目位置
	 * @return このオブジェクトへの参照
	 */
	public SimpleCsvValueFilter isEmpty(final int position) {
		expr.add(CsvExpressions.isEmpty(position));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が空でないかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目位置
	 * @return このオブジェクトへの参照
	 */
	public SimpleCsvValueFilter isNotEmpty(final int position) {
		expr.add(CsvExpressions.isNotEmpty(position));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値と等しいかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目位置
	 * @param criteria 判定基準値
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>criteria</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter eq(final int position, final String criteria) {
		expr.add(CsvExpressions.eq(position, criteria));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値と等しいかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目位置
	 * @param criteria 判定基準値
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>criteria</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter eq(final int position, final String criteria, final boolean ignoreCase) {
		expr.add(CsvExpressions.eq(position, criteria, ignoreCase));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値と等しくないかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目値
	 * @param criteria 判定基準値
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>criteria</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter ne(final int position, final String criteria) {
		expr.add(CsvExpressions.ne(position, criteria));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値と等しくないかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目値
	 * @param criteria 判定基準値
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>criteria</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter ne(final int position, final String criteria, final boolean ignoreCase) {
		expr.add(CsvExpressions.ne(position, criteria, ignoreCase));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値群のいずれかと等しいかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目値
	 * @param criterias 判定基準値群
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>criterias</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter in(final int position, final String... criterias) {
		expr.add(CsvExpressions.in(position, criterias));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値群のいずれかと等しいかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目値
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>criterias</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter in(final int position, final String[] criterias, final boolean ignoreCase) {
		expr.add(CsvExpressions.in(position, criterias, ignoreCase));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値群のいずれとも等しくないかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目値
	 * @param criterias 判定基準値群
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>criterias</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter notIn(final int position, final String... criterias) {
		expr.add(CsvExpressions.notIn(position, criterias));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値群のいずれとも等しくないかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目値
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>criterias</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter notIn(final int position, final String[] criterias, final boolean ignoreCase) {
		expr.add(CsvExpressions.notIn(position, criterias, ignoreCase));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目位置
	 * @param pattern 正規表現パターン
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>pattern</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter regex(final int position, final String pattern) {
		expr.add(CsvExpressions.regex(position, pattern));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目位置
	 * @param pattern 正規表現パターン
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>pattern</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter regex(final int position, final String pattern, final boolean ignoreCase) {
		expr.add(CsvExpressions.regex(position, pattern, ignoreCase));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目位置
	 * @param pattern 正規表現パターン
	 * @param flags マッチフラグ
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>pattern</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter regex(final int position, final String pattern, final int flags) {
		expr.add(CsvExpressions.regex(position, pattern, flags));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目位置
	 * @param pattern 正規表現パターン
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>pattern</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter regex(final int position, final Pattern pattern) {
		expr.add(CsvExpressions.regex(position, pattern));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値より大きいかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目値
	 * @param criteria 判定基準値
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>criteria</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter gt(final int position, final String criteria) {
		expr.add(CsvExpressions.gt(position, criteria));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値より小さいかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目値
	 * @param criteria 判定基準値
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>criteria</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter lt(final int position, final String criteria) {
		expr.add(CsvExpressions.lt(position, criteria));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値以上かどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目値
	 * @param criteria 判定基準値
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>criteria</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter ge(final int position, final String criteria) {
		expr.add(CsvExpressions.ge(position, criteria));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値以下かどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目値
	 * @param criteria 判定基準値
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>criteria</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter le(final int position, final String criteria) {
		expr.add(CsvExpressions.le(position, criteria));
		return this;
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が下限値から上限値の範囲かどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param position 項目値
	 * @param low 下限値
	 * @param high 上限値
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>low</code> または <code>high</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter between(final int position, final String low, final String high) {
		expr.add(CsvExpressions.between(position, low, high));
		return this;
	}

	/**
	 * 指定された区切り文字形式データの値リストでフィルタする区切り文字形式データフィルタの論理否定でフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param filter 論理否定する区切り文字形式データフィルタ
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>filter</code> が <code>null</code> の場合
	 */
	public SimpleCsvValueFilter not(final CsvValueFilter filter) {
		expr.add(CsvExpressions.not(filter));
		return this;
	}

	@Override
	public boolean accept(final List<String> values) {
		return expr.accept(values);
	}

	@Override
	public String toString() {
		final String name = getClass().getName();
		final int period = name.lastIndexOf('.');
		return period > 0 ? name.substring(period + 1) : name;
	}

}
