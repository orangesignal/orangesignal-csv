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
 * 区切り文字形式データフィルタの条件式ユーティリティを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
abstract class CsvExpressionUtils {

	/**
	 * デフォルトコンストラクタです。
	 */
	protected CsvExpressionUtils() {}

	// ------------------------------------------------------------------------

	/**
	 * 指定された項目位置のデータが <code>null</code> であるかどうかを返します。
	 * 
	 * @param values 区切り文字形式データの値リスト
	 * @param position 項目位置
	 * @return 指定された項目位置のデータが <code>null</code> の場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IllegalArgumentException 
	 */
	public static boolean isNull(final List<String> values, final int position) {
		validate(values, position);
		return values.get(position) == null;
	}

	/**
	 * 指定された項目位置のデータが <code>null</code> でないかどうかを返します。
	 * 
	 * @param values 区切り文字形式データの値リスト
	 * @param position 項目位置
	 * @return 指定された項目位置のデータが <code>null</code> でない場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IllegalArgumentException 
	 */
	public static boolean isNotNull(final List<String> values, final int position) {
		return !isNull(values, position);
	}

	/**
	 * 指定された項目位置のデータが空かどうかを返します。
	 * 
	 * @param values 区切り文字形式データの値リスト
	 * @param position 項目位置
	 * @return 指定された項目位置のデータが空の場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IllegalArgumentException 
	 */
	public static boolean isEmpty(final List<String> values, final int position) {
		validate(values, position);
		final String value = values.get(position);
		return value == null || value.isEmpty();
	}

	/**
	 * 指定された項目位置のデータが空でないかどうかを返します。
	 * 
	 * @param values 区切り文字形式データの値リスト
	 * @param position 項目位置
	 * @return 指定された項目位置のデータが空でない場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IllegalArgumentException 
	 */
	public static boolean isNotEmpty(final List<String> values, final int position) {
		return !isEmpty(values, position);
	}

	// ------------------------------------------------------------------------

	/**
	 * 指定された項目位置のデータが判定基準値と等しいかどうかを返します。
	 * 
	 * @param values 区切り文字形式データの値リスト
	 * @param position 項目位置
	 * @param criteria 判定基準値
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 指定された項目位置のデータが判定基準値と等しい場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IllegalArgumentException 
	 */
	public static boolean eq(final List<String> values, final int position, final String criteria, final boolean ignoreCase) {
		validate(values, position);
		if (criteria == null) {
			throw new IllegalArgumentException("Criteria must not be null");
		}
		final String value = values.get(position);
		return value != null && ignoreCase ? criteria.equalsIgnoreCase(value) : criteria.equals(value);
	}

	/**
	 * 指定された項目位置のデータが判定基準値と等しくないかどうかを返します。
	 * 
	 * @param values 区切り文字形式データの値リスト
	 * @param position 項目位置
	 * @param criteria 判定基準値
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 指定された項目位置のデータが判定基準値と等しくない場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IllegalArgumentException 
	 */
	public static boolean ne(final List<String> values, final int position, final String criteria, final boolean ignoreCase) {
		return !eq(values, position, criteria, ignoreCase);
	}

	/**
	 * 指定された項目位置のデータが判定基準値群のいずれかと等しいかどうかを返します。
	 * 
	 * @param values 区切り文字形式データの値リスト
	 * @param position 項目位置
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 指定された項目位置のデータが判定基準値群のいずれかと等しい場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IllegalArgumentException 
	 */
	public static boolean in(final List<String> values, final int position, final String[] criterias, final boolean ignoreCase) {
		if (criterias == null) {
			throw new IllegalArgumentException("Criterias must not be null");
		}
		for (final String criteria : criterias) {
			if (eq(values, position, criteria, ignoreCase)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 指定された項目位置のデータが判定基準値群のいずれとも等しくないかどうかを返します。
	 * 
	 * @param values 区切り文字形式データの値リスト
	 * @param position 項目位置
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 指定された項目位置のデータが判定基準値群のいずれとも等しくない場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IllegalArgumentException 
	 */
	public static boolean notIn(final List<String> values, final int position, final String[] criterias, final boolean ignoreCase) {
		return !in(values, position, criterias, ignoreCase);
	}

	// ------------------------------------------------------------------------

	/**
	 * 指定された項目位置のデータが正規表現パターンとマッチするかどうかを返します。
	 * 
	 * @param values 区切り文字形式データの値リスト
	 * @param position 項目位置
	 * @param pattern 判定する正規表現パターン
	 * @return 指定された項目位置のデータが正規表現パターンとマッチする場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IllegalArgumentException 
	 */
	public static boolean regex(final List<String> values, final int position, final Pattern pattern) {
		validate(values, position);
		final String value = values.get(position);
		return value != null && pattern.matcher(value).matches();
	}

	// ------------------------------------------------------------------------

	/**
	 * 指定された項目位置のデータが判定基準値よりも大きいかどうかを返します。
	 * 
	 * @param values 区切り文字形式データの値リスト
	 * @param position 項目位置
	 * @param criteria 判定基準値
	 * @return 指定された項目位置のデータが判定基準値よりも大きい場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IllegalArgumentException 
	 */
	public static boolean gt(final List<String> values, final int position, final String criteria) {
		validate(values, position);
		final String value = values.get(position);
		return value != null && criteria != null && value.compareTo(criteria) > 0;
	}

	/**
	 * 指定された項目位置のデータが判定基準値よりも小さいかどうかを返します。
	 * 
	 * @param values 区切り文字形式データの値リスト
	 * @param position 項目位置
	 * @param criteria 判定基準値
	 * @return 指定された項目位置のデータが判定基準値よりも小さい場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IllegalArgumentException 
	 */
	public static boolean lt(final List<String> values, final int position, final String criteria) {
		validate(values, position);
		final String value = values.get(position);
		return value != null && criteria != null && value.compareTo(criteria) < 0;
	}

	/**
	 * 指定された項目位置のデータが判定基準値以上かどうかを返します。
	 * 
	 * @param values 区切り文字形式データの値リスト
	 * @param position 項目位置
	 * @param criteria 判定基準値
	 * @return 指定された項目位置のデータが判定基準値以上の場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IllegalArgumentException 
	 */
	public static boolean ge(final List<String> values, final int position, final String criteria) {
		validate(values, position);
		final String value = values.get(position);
		return value != null && criteria != null && value.compareTo(criteria) >= 0;
	}

	/**
	 * 指定された項目位置のデータが判定基準値以下かどうかを返します。
	 * 
	 * @param values 区切り文字形式データの値リスト
	 * @param position 項目位置
	 * @param criteria 判定基準値
	 * @return 指定された項目位置のデータが判定基準値以下の場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IllegalArgumentException 
	 */
	public static boolean le(final List<String> values, final int position, final String criteria) {
		validate(values, position);
		final String value = values.get(position);
		return value != null && criteria != null && value.compareTo(criteria) <= 0;
	}

	/**
	 * 指定された項目位置のデータが下限値から上限値の範囲かどうかを返します。
	 * 
	 * @param values 区切り文字形式データの値リスト
	 * @param position 項目位置
	 * @param low 下限値
	 * @param high 上限値
	 * @return 指定された項目位置のデータが下限値から上限値の範囲の場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IllegalArgumentException 
	 */
	public static boolean between(final List<String> values, final int position, final String low, final String high) {
		return ge(values, position, low) && le(values, position, high);
	}

	// ------------------------------------------------------------------------

	/**
	 * 指定されたパラメータを検証します。
	 * 
	 * @param values 区切り文字形式データの値リスト
	 * @param position 項目位置
	 * @throws IllegalArgumentException パラメータが不正な場合
	 */
	protected static void validate(final List<String> values, final int position) {
		if (values == null) {
			throw new IllegalArgumentException("Values must not be null");
		}
		if (position < 0 || position >= values.size()) {
			throw new IllegalArgumentException(String.format("Invalid column position %d", position));
		}
	}

}
