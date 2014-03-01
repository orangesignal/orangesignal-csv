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
import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * DSL (Domain Specific Language) 形式でスマートなフィルタ条件の定義が可能な {@link BeanFilter} の実装クラスを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public class SimpleBeanFilter implements BeanFilter {

	private BeanLogicalExpression expr;

	/**
	 * デフォルトコンストラクタです。
	 */
	public SimpleBeanFilter() {
		this(new BeanAndExpression());
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param expr 論理演算 Java プログラム要素フィルタ
	 * @throws IllegalArgumentException <code>expr</code> が <code>null</code> の場合
	 */
	public SimpleBeanFilter(final BeanLogicalExpression expr) {
		if (expr == null) {
			throw new IllegalArgumentException(String.format("%s must not be null", BeanLogicalExpression.class.getSimpleName()));
		}
		this.expr = expr;
	}

	/**
	 * 指定された Java プログラム要素フィルタを追加します。
	 * 
	 * @param filter Java プログラム要素フィルタ
	 * @return このオブジェクトへの参照
	 */
	public SimpleBeanFilter add(final BeanFilter filter) {
		expr.add(filter);
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が <code>null</code> であるかどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>name</code> が <code>null</code> の場合
	 */
	public SimpleBeanFilter isNull(final String name) {
		expr.add(BeanExpressions.isNull(name));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が <code>null</code> でないかどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>name</code> が <code>null</code> の場合
	 */
	public SimpleBeanFilter isNotNull(final String name) {
		expr.add(BeanExpressions.isNotNull(name));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が空かどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>name</code> が <code>null</code> の場合
	 */
	public SimpleBeanFilter isEmpty(final String name) {
		expr.add(BeanExpressions.isEmpty(name));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が空でないかどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>name</code> が <code>null</code> の場合
	 */
	public SimpleBeanFilter isNotEmpty(final String name) {
		expr.add(BeanExpressions.isNotEmpty(name));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値と等しいかどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter eq(final String name, final Object criteria) {
		expr.add(BeanExpressions.eq(name, criteria));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値と等しいかどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter eq(final String name, final String criteria, final boolean ignoreCase) {
		expr.add(BeanExpressions.eq(name, criteria, ignoreCase));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値と等しくないかどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter ne(final String name, final Object criteria) {
		expr.add(BeanExpressions.ne(name, criteria));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値と等しくないかどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter ne(final String name, final String criteria, final boolean ignoreCase) {
		expr.add(BeanExpressions.ne(name, criteria, ignoreCase));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値群のいずれかと等しいかどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param criterias 判定基準値群
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter in(final String name, final Object... criterias) {
		expr.add(BeanExpressions.in(name, criterias));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値群のいずれかと等しいかどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param criterias 判定基準値群
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter in(final String name, final String... criterias) {
		expr.add(BeanExpressions.in(name, criterias));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値群のいずれかと等しいかどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter in(final String name, final String[] criterias, final boolean ignoreCase) {
		expr.add(BeanExpressions.in(name, criterias, ignoreCase));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値群のいずれとも等しくないかどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param criterias 判定基準値群
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter notIn(final String name, final Object... criterias) {
		expr.add(BeanExpressions.notIn(name, criterias));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値群のいずれとも等しくないかどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param criterias 判定基準値群
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter notIn(final String name, final String[] criterias) {
		expr.add(BeanExpressions.notIn(name, criterias));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値群のいずれとも等しくないかどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter notIn(final String name, final String[] criterias, final boolean ignoreCase) {
		expr.add(BeanExpressions.notIn(name, criterias, ignoreCase));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param name 項目名
	 * @param pattern 正規表現パターン
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter regex(final String name, final String pattern) {
		expr.add(BeanExpressions.regex(name, pattern));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param name 項目名
	 * @param pattern 正規表現パターン
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter regex(final String name, final String pattern, final boolean ignoreCase) {
		expr.add(BeanExpressions.regex(name, pattern, ignoreCase));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param name 項目名
	 * @param pattern 正規表現パターン
	 * @param flags マッチフラグ
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter regex(final String name, final String pattern, final int flags) {
		expr.add(BeanExpressions.regex(name, pattern, flags));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを追加します。
	 * 
	 * @param name 項目名
	 * @param pattern 正規表現パターン
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter regex(final String name, final Pattern pattern) {
		expr.add(BeanExpressions.regex(name, pattern));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値より大きいかどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter gt(final String name, final Object criteria) {
		expr.add(BeanExpressions.gt(name, criteria));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値より大きいかどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param comparator コンパレータ (オプション)
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	@SuppressWarnings("unchecked")
	public SimpleBeanFilter gt(final String name, final Object criteria, @SuppressWarnings("rawtypes") final Comparator comparator) {
		expr.add(BeanExpressions.gt(name, criteria, comparator));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値より小さいかどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter lt(final String name, final Object criteria) {
		expr.add(BeanExpressions.lt(name, criteria));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値より小さいかどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param comparator コンパレータ (オプション)
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter lt(final String name, final Object criteria, @SuppressWarnings("rawtypes") final Comparator comparator) {
		expr.add(BeanExpressions.lt(name, criteria, comparator));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値以上かどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter ge(final String name, final Object criteria) {
		expr.add(BeanExpressions.ge(name, criteria));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値以上かどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param comparator コンパレータ (オプション)
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter ge(final String name, final Object criteria, @SuppressWarnings("rawtypes") final Comparator comparator) {
		expr.add(BeanExpressions.ge(name, criteria, comparator));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値以下かどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter le(final String name, final Object criteria) {
		expr.add(BeanExpressions.le(name, criteria));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値以下かどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param comparator コンパレータ (オプション)
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter le(final String name, final Object criteria, @SuppressWarnings("rawtypes") final Comparator comparator) {
		expr.add(BeanExpressions.le(name, criteria, comparator));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が下限値から上限値の範囲かどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param low 下限値
	 * @param high 上限値
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter between(final String name, final Object low, final Object high) {
		expr.add(BeanExpressions.between(name, low, high));
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が下限値から上限値の範囲かどうかでフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param name フィールド名
	 * @param low 下限値
	 * @param high 上限値
	 * @param comparator コンパレータ (オプション)
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public SimpleBeanFilter between(final String name, final Object low, final Object high, @SuppressWarnings("rawtypes") final Comparator comparator) {
		expr.add(BeanExpressions.between(name, low, high, comparator));
		return this;
	}

	/**
	 * 指定された Java プログラム要素フィルタの論理否定でフィルタを適用する Java プログラム要素フィルタを追加します。
	 * 
	 * @param filter 論理否定する Java プログラム要素フィルタ
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException <code>filter</code> が <code>null</code> の場合
	 */
	public SimpleBeanFilter not(final BeanFilter filter) {
		expr.add(BeanExpressions.not(filter));
		return this;
	}

	@Override
	public boolean accept(final Object bean) throws IOException {
		return expr.accept(bean);
	}

	@Override
	public String toString() {
		final String name = getClass().getName();
		final int period = name.lastIndexOf('.');
		return period > 0 ? name.substring(period + 1) : name;
	}

}
