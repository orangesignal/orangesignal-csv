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

import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * Java プログラム要素フィルタを構築するファクトリクラスを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public abstract class BeanExpressions {

	/**
	 * デフォルトコンストラクタです。
	 */
	protected BeanExpressions() {}

	/**
	 * Java プログラム要素のフィールド値が <code>null</code> であるかどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException <code>name</code> が <code>null</code> の場合
	 */
	public static BeanNullExpression isNull(final String name) {
		return new BeanNullExpression(name);
	}

	/**
	 * Java プログラム要素のフィールド値が <code>null</code> でないかどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException <code>name</code> が <code>null</code> の場合
	 */
	public static BeanNotNullExpression isNotNull(final String name) {
		return new BeanNotNullExpression(name);
	}

	/**
	 * Java プログラム要素のフィールド値が空かどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException <code>name</code> が <code>null</code> の場合
	 */
	public static BeanEmptyExpression isEmpty(final String name) {
		return new BeanEmptyExpression(name);
	}

	/**
	 * Java プログラム要素のフィールド値が空でないかどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException <code>name</code> が <code>null</code> の場合
	 */
	public static BeanNotEmptyExpression isNotEmpty(final String name) {
		return new BeanNotEmptyExpression(name);
	}

	/**
	 * Java プログラム要素のフィールド値が判定基準値と等しいかどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanEqualExpression eq(final String name, final Object criteria) {
		return new BeanEqualExpression(name, criteria);
	}

	/**
	 * Java プログラム要素のフィールド値が判定基準値と等しいかどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanEqualExpression eq(final String name, final String criteria, final boolean ignoreCase) {
		return new BeanEqualExpression(name, criteria, ignoreCase);
	}

	/**
	 * Java プログラム要素のフィールド値が判定基準値と等しくないかどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanNotEqualExpression ne(final String name, final Object criteria) {
		return new BeanNotEqualExpression(name, criteria);
	}

	/**
	 * Java プログラム要素のフィールド値が判定基準値と等しくないかどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanNotEqualExpression ne(final String name, final String criteria, final boolean ignoreCase) {
		return new BeanNotEqualExpression(name, criteria, ignoreCase);
	}

	/**
	 * Java プログラム要素のフィールド値が判定基準値群のいずれかと等しいかどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param criterias 判定基準値群
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanInExpression in(final String name, final Object... criterias) {
		return new BeanInExpression(name, criterias);
	}

	/**
	 * Java プログラム要素のフィールド値が判定基準値群のいずれかと等しいかどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param criterias 判定基準値群
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanInExpression in(final String name, final String... criterias) {
		return new BeanInExpression(name, criterias);
	}

	/**
	 * Java プログラム要素のフィールド値が判定基準値群のいずれかと等しいかどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanInExpression in(final String name, final String[] criterias, final boolean ignoreCase) {
		return new BeanInExpression(name, criterias, ignoreCase);
	}

	/**
	 * Java プログラム要素のフィールド値が判定基準値群のいずれとも等しくないかどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param criterias 判定基準値群
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanNotInExpression notIn(final String name, final Object... criterias) {
		return new BeanNotInExpression(name, criterias);
	}

	/**
	 * Java プログラム要素のフィールド値が判定基準値群のいずれとも等しくないかどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param criterias 判定基準値群
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanNotInExpression notIn(final String name, final String... criterias) {
		return new BeanNotInExpression(name, criterias);
	}

	/**
	 * Java プログラム要素のフィールド値が判定基準値群のいずれとも等しくないかどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanNotInExpression notIn(final String name, final String[] criterias, final boolean ignoreCase) {
		return new BeanNotInExpression(name, criterias, ignoreCase);
	}

	/**
	 * Java プログラム要素のフィールド値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param pattern 正規表現パターン
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanRegexExpression regex(final String name, final String pattern) {
		return new BeanRegexExpression(name, pattern);
	}

	/**
	 * Java プログラム要素のフィールド値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param pattern 正規表現パターン
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanRegexExpression regex(final String name, final String pattern, final boolean ignoreCase) {
		return new BeanRegexExpression(name, pattern, ignoreCase);
	}

	/**
	 * Java プログラム要素のフィールド値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param pattern 正規表現パターン
	 * @param flags マッチフラグ
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanRegexExpression regex(final String name, final String pattern, final int flags) {
		return new BeanRegexExpression(name, pattern, flags);
	}

	/**
	 * Java プログラム要素のフィールド値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param pattern 正規表現パターン
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanRegexExpression regex(final String name, final Pattern pattern) {
		return new BeanRegexExpression(name, pattern);
	}

	/**
	 * Java プログラム要素のフィールド値が判定基準値より大きいかどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanGreaterThanExpression gt(final String name, final Object criteria) {
		return new BeanGreaterThanExpression(name, criteria);
	}

	/**
	 * Java プログラム要素のフィールド値が判定基準値より大きいかどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param comparator コンパレータ (オプション)
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanGreaterThanExpression gt(final String name, final Object criteria, final Comparator<Object> comparator) {
		return new BeanGreaterThanExpression(name, criteria, comparator);
	}

	/**
	 * Java プログラム要素のフィールド値が判定基準値より小さいかどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanLessThanExpression lt(final String name, final Object criteria) {
		return new BeanLessThanExpression(name, criteria);
	}

	/**
	 * Java プログラム要素のフィールド値が判定基準値より小さいかどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param comparator コンパレータ (オプション)
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanLessThanExpression lt(final String name, final Object criteria, @SuppressWarnings("rawtypes") final Comparator comparator) {
		return new BeanLessThanExpression(name, criteria, comparator);
	}

	/**
	 * Java プログラム要素のフィールド値が判定基準値以上かどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanGreaterThanOrEqualExpression ge(final String name, final Object criteria) {
		return new BeanGreaterThanOrEqualExpression(name, criteria);
	}

	/**
	 * Java プログラム要素のフィールド値が判定基準値以上かどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param comparator コンパレータ (オプション)
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanGreaterThanOrEqualExpression ge(final String name, final Object criteria, @SuppressWarnings("rawtypes") final Comparator comparator) {
		return new BeanGreaterThanOrEqualExpression(name, criteria, comparator);
	}

	/**
	 * Java プログラム要素のフィールド値が判定基準値以下かどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanLessThanOrEqualExpression le(final String name, final Object criteria) {
		return new BeanLessThanOrEqualExpression(name, criteria);
	}

	/**
	 * Java プログラム要素のフィールド値が判定基準値以下かどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param comparator コンパレータ (オプション)
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanLessThanOrEqualExpression le(final String name, final Object criteria, @SuppressWarnings("rawtypes") final Comparator comparator) {
		return new BeanLessThanOrEqualExpression(name, criteria, comparator);
	}

	/**
	 * Java プログラム要素のフィールド値が下限値から上限値の範囲かどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param low 下限値
	 * @param high 上限値
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanBetweenExpression between(final String name, final Object low, final Object high) {
		return new BeanBetweenExpression(name, low, high);
	}

	/**
	 * Java プログラム要素のフィールド値が下限値から上限値の範囲かどうかでフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param name フィールド名
	 * @param low 下限値
	 * @param high 上限値
	 * @param comparator コンパレータ (オプション)
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static BeanBetweenExpression between(final String name, final Object low, final Object high, @SuppressWarnings("rawtypes") final Comparator comparator) {
		return new BeanBetweenExpression(name, low, high, comparator);
	}

	/**
	 * Java プログラム要素フィルタ群の論理積でフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param filters 論理積する Java プログラム要素フィルタ群
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException <code>filters</code> が <code>null</code> の場合
	 */
	public static BeanLogicalExpression and(final BeanFilter... filters) {
		return new BeanAndExpression(filters);
	}

	/**
	 * Java プログラム要素フィルタ群の論理和でフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param filters 論理和する Java プログラム要素フィルタ群
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException <code>filters</code> が <code>null</code> の場合
	 */
	public static BeanLogicalExpression or(final BeanFilter... filters) {
		return new BeanOrExpression(filters);
	}

	/**
	 * Java プログラム要素フィルタの論理否定でフィルタを適用する Java プログラム要素フィルタを構築して返します。
	 * 
	 * @param filter 論理否定する Java プログラム要素フィルタ
	 * @return Java プログラム要素フィルタ
	 * @throws IllegalArgumentException <code>filter</code> が <code>null</code> の場合
	 */
	public static BeanNotExpression not(final BeanFilter filter) {
		return new BeanNotExpression(filter);
	}

}
