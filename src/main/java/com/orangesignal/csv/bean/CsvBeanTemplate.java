/*
 * Copyright 2013 the original author or authors.
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

package com.orangesignal.csv.bean;

import java.util.List;

import com.orangesignal.csv.filters.CsvNamedValueFilter;

/**
 * Java プログラム要素と区切り文字形式データアクセスの操作を簡素化するヘルパークラスを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public class CsvBeanTemplate<T> extends AbstractCsvBeanTemplate<T, CsvBeanTemplate<T>> implements CsvBeanOperation<CsvBeanTemplate<T>> {

	/**
	 * Java プログラム要素へデータを設定する名前群を保持します。
	 */
	private String[] includes;

	/**
	 * Java プログラム要素へデータを設定しない名前群を保持します。
	 */
	private String[] excludes;

	/**
	 * 区切り文字形式データフィルタを保持します。
	 */
	private CsvNamedValueFilter filter;

	// ------------------------------------------------------------------------
	// 利便性のための静的メソッド

	/**
	 * 新しい {@link CsvBeanTemplate} のインスタンスを返します。
	 * 
	 * @param type Java プログラム要素の型
	 * @return 新しい {@link CsvBeanTemplate} のインスタンス
	 * @throws IllegalArgumentException {@code type} が {@code null} の場合。
	 */
	public static <T> CsvBeanTemplate<T> newInstance(final Class<T> type) {
		return new CsvBeanTemplate<T>(type);
	}

	// -----------------------------------------------------------------------
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param type Java プログラム要素の型
	 * @throws IllegalArgumentException {@code type} が {@code null} の場合。
	 */
	public CsvBeanTemplate(final Class<T> type) {
		super(type);
	}

	// -----------------------------------------------------------------------
	// オーバーライド メソッド

	@Override
	public CsvBeanTemplate<T> includes(final String... names) {
		if (excludes != null && excludes.length > 0) {
			throw new IllegalArgumentException("Only includes or excludes may be specified.");
		}
		this.includes = names;
		return this;
	}

	@Override
	public CsvBeanTemplate<T> excludes(final String... names) {
		if (includes != null && includes.length > 0) {
			throw new IllegalArgumentException("Only includes or excludes may be specified.");
		}
		this.excludes = names;
		return this;
	}

	@Override
	public CsvBeanTemplate<T> filter(final CsvNamedValueFilter filter) {
		this.filter = filter;
		return this;
	}

	// -----------------------------------------------------------------------
	// パブリック メソッド

	/**
	 * 指定された区切り文字形式データの値リストが含まれる必要があるかどうかを判定します。
	 * 
	 * @param columnNames 区切り文字形式データの項目名リスト
	 * @param values 区切り文字形式データの項目値のリスト
	 * @return {@code values} が含まれる必要がある場合は {@code true}
	 */
	public boolean isAccept(final List<String> columnNames, final List<String> values) {
		return filter != null && !filter.accept(columnNames, values);
	}

	/**
	 * 指定された名前が Java プログラム要素としてデータを設定すべき名前かどうかを返します。
	 * 
	 * @param name 名前
	 * @return Java プログラム要素としてデータを設定すべき名前の場合は {@code true} それ以外の場合は {@code false}
	 */
	public boolean isTargetName(final String name) {
		if (excludes != null && excludes.length > 0) {
			for (final String propertyName : excludes) {
				if (propertyName.equals(name)) {
					return false;
				}
			}
			return true;
		}
		if (includes != null && includes.length > 0) {
			for (final String propertyName : includes) {
				if (propertyName.equals(name)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

}