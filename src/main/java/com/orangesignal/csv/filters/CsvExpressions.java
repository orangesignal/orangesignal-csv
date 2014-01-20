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

import java.util.regex.Pattern;

/**
 * 区切り文字形式データフィルタを構築するファクトリクラスを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public abstract class CsvExpressions {

	/**
	 * デフォルトコンストラクタです。
	 */
	protected CsvExpressions() {}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が <code>null</code> であるかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @return 区切り文字形式データフィルタ
	 */
	public static ColumnPositionNullExpression isNull(final int position) {
		return new ColumnPositionNullExpression(position);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が <code>null</code> であるかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>name</code> が <code>null</code> の場合
	 */
	public static ColumnNameNullExpression isNull(final String name) {
		return new ColumnNameNullExpression(name);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が <code>null</code> でないかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @return 区切り文字形式データフィルタ
	 */
	public static ColumnPositionNotNullExpression isNotNull(final int position) {
		return new ColumnPositionNotNullExpression(position);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が <code>null</code> でないかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>name</code> が <code>null</code> の場合
	 */
	public static ColumnNameNotNullExpression isNotNull(final String name) {
		return new ColumnNameNotNullExpression(name);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が空かどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @return 区切り文字形式データフィルタ
	 */
	public static ColumnPositionEmptyExpression isEmpty(final int position) {
		return new ColumnPositionEmptyExpression(position);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が空かどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @return 区切り文字形式データフィルタ
	 */
	public static ColumnNameEmptyExpression isEmpty(final String name) {
		return new ColumnNameEmptyExpression(name);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が空でないかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @return 区切り文字形式データフィルタ
	 */
	public static ColumnPositionNotEmptyExpression isNotEmpty(final int position) {
		return new ColumnPositionNotEmptyExpression(position);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が空でないかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @return 区切り文字形式データフィルタ
	 */
	public static ColumnNameNotEmptyExpression isNotEmpty(final String name) {
		return new ColumnNameNotEmptyExpression(name);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値と等しいかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @param criteria 判定基準値
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>criteria</code> が <code>null</code> の場合
	 */
	public static ColumnPositionEqualExpression eq(final int position, final String criteria) {
		return new ColumnPositionEqualExpression(position, criteria);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が判定基準値と等しいかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param criteria 判定基準値
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static ColumnNameEqualExpression eq(final String name, final String criteria) {
		return new ColumnNameEqualExpression(name, criteria);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値と等しいかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @param criteria 判定基準値
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>criteria</code> が <code>null</code> の場合
	 */
	public static ColumnPositionEqualExpression eq(final int position, final String criteria, final boolean ignoreCase) {
		return new ColumnPositionEqualExpression(position, criteria, ignoreCase);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が判定基準値と等しいかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param criteria 判定基準値
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static ColumnNameEqualExpression eq(final String name, final String criteria, final boolean ignoreCase) {
		return new ColumnNameEqualExpression(name, criteria, ignoreCase);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値と等しくないかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @param criteria 判定基準値
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>criteria</code> が <code>null</code> の場合
	 */
	public static ColumnPositionNotEqualExpression ne(final int position, final String criteria) {
		return new ColumnPositionNotEqualExpression(position, criteria);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が判定基準値と等しくないかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param criteria 判定基準値
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static ColumnNameNotEqualExpression ne(final String name, final String criteria) {
		return new ColumnNameNotEqualExpression(name, criteria);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値と等しくないかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @param criteria 判定基準値
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>criteria</code> が <code>null</code> の場合
	 */
	public static ColumnPositionNotEqualExpression ne(final int position, final String criteria, final boolean ignoreCase) {
		return new ColumnPositionNotEqualExpression(position, criteria, ignoreCase);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が判定基準値と等しくないかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param criteria 判定基準値
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static ColumnNameNotEqualExpression ne(final String name, final String criteria, final boolean ignoreCase) {
		return new ColumnNameNotEqualExpression(name, criteria, ignoreCase);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値群のいずれかと等しいかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @param criterias 判定基準値群
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>criterias</code> が <code>null</code> の場合
	 */
	public static ColumnPositionInExpression in(final int position, final String... criterias) {
		return new ColumnPositionInExpression(position, criterias);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が判定基準値群のいずれかと等しいかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param criterias 判定基準値群
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static ColumnNameInExpression in(final String name, final String... criterias) {
		return new ColumnNameInExpression(name, criterias);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値群のいずれかと等しいかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>criterias</code> が <code>null</code> の場合
	 */
	public static ColumnPositionInExpression in(final int position, final String[] criterias, final boolean ignoreCase) {
		return new ColumnPositionInExpression(position, criterias, ignoreCase);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が判定基準値群のいずれかと等しいかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static ColumnNameInExpression in(final String name, final String[] criterias, final boolean ignoreCase) {
		return new ColumnNameInExpression(name, criterias, ignoreCase);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値群のいずれとも等しくないかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @param criterias 判定基準値群
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>criterias</code> が <code>null</code> の場合
	 */
	public static ColumnPositionNotInExpression notIn(final int position, final String... criterias) {
		return new ColumnPositionNotInExpression(position, criterias);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が判定基準値群のいずれとも等しくないかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param criterias 判定基準値群
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static ColumnNameNotInExpression notIn(final String name, final String... criterias) {
		return new ColumnNameNotInExpression(name, criterias);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値群のいずれとも等しくないかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>criterias</code> が <code>null</code> の場合
	 */
	public static ColumnPositionNotInExpression notIn(final int position, final String[] criterias, final boolean ignoreCase) {
		return new ColumnPositionNotInExpression(position, criterias, ignoreCase);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が判定基準値群のいずれとも等しくないかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static ColumnNameNotInExpression notIn(final String name, final String[] criterias, final boolean ignoreCase) {
		return new ColumnNameNotInExpression(name, criterias, ignoreCase);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @param pattern 正規表現パターン
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>pattern</code> が <code>null</code> の場合
	 */
	public static ColumnPositionRegexExpression regex(final int position, final String pattern) {
		return new ColumnPositionRegexExpression(position, pattern);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @param pattern 正規表現パターン
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>pattern</code> が <code>null</code> の場合
	 */
	public static ColumnPositionRegexExpression regex(final int position, final String pattern, final boolean ignoreCase) {
		return new ColumnPositionRegexExpression(position, pattern, ignoreCase);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @param pattern 正規表現パターン
	 * @param flags マッチフラグ
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>pattern</code> が <code>null</code> の場合
	 */
	public static ColumnPositionRegexExpression regex(final int position, final String pattern, final int flags) {
		return new ColumnPositionRegexExpression(position, pattern, flags);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @param pattern 正規表現パターン
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>pattern</code> が <code>null</code> の場合
	 */
	public static ColumnPositionRegexExpression regex(final int position, final Pattern pattern) {
		return new ColumnPositionRegexExpression(position, pattern);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param pattern 正規表現パターン
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static ColumnNameRegexExpression regex(final String name, final String pattern) {
		return new ColumnNameRegexExpression(name, pattern);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param pattern 正規表現パターン
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static ColumnNameRegexExpression regex(final String name, final String pattern, final boolean ignoreCase) {
		return new ColumnNameRegexExpression(name, pattern, ignoreCase);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param pattern 正規表現パターン
	 * @param flags マッチフラグ
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static ColumnNameRegexExpression regex(final String name, final String pattern, final int flags) {
		return new ColumnNameRegexExpression(name, pattern, flags);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param pattern 正規表現パターン
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static ColumnNameRegexExpression regex(final String name, final Pattern pattern) {
		return new ColumnNameRegexExpression(name, pattern);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値より大きいかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @param criteria 判定基準値
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>criteria</code> が <code>null</code> の場合
	 */
	public static ColumnPositionGreaterThanExpression gt(final int position, final String criteria) {
		return new ColumnPositionGreaterThanExpression(position, criteria);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が判定基準値より大きいかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param criteria 判定基準値
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static ColumnNameGreaterThanExpression gt(final String name, final String criteria) {
		return new ColumnNameGreaterThanExpression(name, criteria);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値より小さいかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @param criteria 判定基準値
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>criteria</code> が <code>null</code> の場合
	 */
	public static ColumnPositionLessThanExpression lt(final int position, final String criteria) {
		return new ColumnPositionLessThanExpression(position, criteria);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が判定基準値より小さいかどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param criteria 判定基準値
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static ColumnNameLessThanExpression lt(final String name, final String criteria) {
		return new ColumnNameLessThanExpression(name, criteria);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値以上かどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @param criteria 判定基準値
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>criteria</code> が <code>null</code> の場合
	 */
	public static ColumnPositionGreaterThanOrEqualExpression ge(final int position, final String criteria) {
		return new ColumnPositionGreaterThanOrEqualExpression(position, criteria);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が判定基準値以上かどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param criteria 判定基準値
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static ColumnNameGreaterThanOrEqualExpression ge(final String name, final String criteria) {
		return new ColumnNameGreaterThanOrEqualExpression(name, criteria);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が判定基準値以下かどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @param criteria 判定基準値
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>criteria</code> が <code>null</code> の場合
	 */
	public static ColumnPositionLessThanOrEqualExpression le(final int position, final String criteria) {
		return new ColumnPositionLessThanOrEqualExpression(position, criteria);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が判定基準値以下かどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param criteria 判定基準値
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static ColumnNameLessThanOrEqualExpression le(final String name, final String criteria) {
		return new ColumnNameLessThanOrEqualExpression(name, criteria);
	}

	/**
	 * 指定された項目位置に対応する区切り文字形式データの値が下限値から上限値の範囲かどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param position 項目位置
	 * @param low 下限値
	 * @param high 上限値
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>low</code> または <code>high</code> が <code>null</code> の場合
	 */
	public static ColumnPositionBetweenExpression between(final int position, final String low, final String high) {
		return new ColumnPositionBetweenExpression(position, low, high);
	}

	/**
	 * 指定された項目名に対応する区切り文字形式データの値が下限値から上限値の範囲かどうかでフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param name 項目名
	 * @param low 下限値
	 * @param high 上限値
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	public static ColumnNameBetweenExpression between(final String name, final String low, final String high) {
		return new ColumnNameBetweenExpression(name, low, high);
	}

	/**
	 * 指定された区切り文字形式データの値リストでフィルタする区切り文字形式データフィルタ群の論理積でフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param filters 論理積する区切り文字形式データフィルタ群
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>filters</code> が <code>null</code> の場合
	 */
	public static CsvValueLogicalExpression and(final CsvValueFilter... filters) {
		return new CsvValueAndExpression(filters);
	}

	/**
	 * 指定された区切り文字形式データの項目名リストと値リストでフィルタする区切り文字形式データフィルタ群の論理積でフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param filters 論理積する区切り文字形式データフィルタ群
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>filters</code> が <code>null</code> の場合
	 */
	public static CsvNamedValueLogicalExpression and(final CsvNamedValueFilter... filters) {
		return new CsvNamedValueAndExpression(filters);
	}

	/**
	 * 指定された区切り文字形式データの値リストでフィルタする区切り文字形式データフィルタ群の論理和でフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param filters 論理和する区切り文字形式データフィルタ群
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>filters</code> が <code>null</code> の場合
	 */
	public static CsvValueLogicalExpression or(final CsvValueFilter... filters) {
		return new CsvValueOrExpression(filters);
	}

	/**
	 * 指定された区切り文字形式データの項目名リストと値リストでフィルタする区切り文字形式データフィルタ群の論理和でフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param filters 論理和する区切り文字形式データフィルタ群
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>filters</code> が <code>null</code> の場合
	 */
	public static CsvNamedValueLogicalExpression or(final CsvNamedValueFilter... filters) {
		return new CsvNamedValueOrExpression(filters);
	}

	/**
	 * 指定された区切り文字形式データの値リストでフィルタする区切り文字形式データフィルタの論理否定でフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param filter 論理否定する区切り文字形式データフィルタ
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>filter</code> が <code>null</code> の場合
	 */
	public static CsvValueNotExpression not(final CsvValueFilter filter) {
		return new CsvValueNotExpression(filter);
	}

	/**
	 * 指定された区切り文字形式データの項目名リストと値リストでフィルタする区切り文字形式データフィルタの論理否定でフィルタを適用する区切り文字形式データフィルタを構築して返します。
	 * 
	 * @param filter 論理否定する区切り文字形式データフィルタ
	 * @return 区切り文字形式データフィルタ
	 * @throws IllegalArgumentException <code>filter</code> が <code>null</code> の場合
	 */
	public static CsvNamedValueNotExpression not(final CsvNamedValueFilter filter) {
		return new CsvNamedValueNotExpression(filter);
	}

}
