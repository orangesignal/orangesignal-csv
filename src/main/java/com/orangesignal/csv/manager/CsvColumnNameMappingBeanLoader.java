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

package com.orangesignal.csv.manager;

import java.text.Format;

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.bean.CsvColumnNameMappingBeanTemplate;
import com.orangesignal.csv.filters.CsvNamedValueFilter;
import com.orangesignal.csv.handlers.ColumnNameMappingBeanListHandler;

/**
 * 区切り文字形式データの項目名を基準とする Java プログラム要素のリストと区切り文字形式データの統合入力インタフェースの実装クラスを提供します。
 *
 * @author Koji Sugisawa
 * @since 1.2
 */
public class CsvColumnNameMappingBeanLoader<T> extends AbstractCsvLoader<T, CsvColumnNameMappingBeanTemplate<T>, ColumnNameMappingBeanListHandler<T>, CsvColumnNameMappingBeanLoader<T>> {

	/**
	 * コンストラクタです。
	 *
	 * @param cfg 区切り文字形式情報
	 * @param beanClass JavaBean の型
	 * @throws IllegalArgumentException パラメータが不正な場合
	 */
	protected CsvColumnNameMappingBeanLoader(final CsvConfig cfg, final Class<T> beanClass) {
		super(cfg, new ColumnNameMappingBeanListHandler<T>(beanClass));
	}

	/**
	 * 指定された項目名と Java プログラム要素のフィールド名をマップへ追加します。
	 * 
	 * @param column 項目名
	 * @param field Java プログラム要素のフィールド名
	 * @return このオブジェクトへの参照
	 */
	public CsvColumnNameMappingBeanLoader<T> column(final String column, final String field) {
		getCsvListHandler().column(column, field);
		return this;
	}

	/**
	 * 指定された項目名と Java プログラム要素のフィールド名をマップへ追加します。
	 * 
	 * @param column 項目名
	 * @param field Java プログラム要素のフィールド名
	 * @param format フィールドを処理するフォーマットオブジェクト (オプション)
	 * @return このオブジェクトへの参照
	 * @since 1.2
	 */
	public CsvColumnNameMappingBeanLoader<T> column(final String column, final String field, final Format format) {
		getCsvListHandler().column(column, field, format);
		return this;
	}

	/**
	 * 区切り文字形式データフィルタを設定します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 * @return このオブジェクトへの参照
	 * @since 1.2.3
	 */
	public CsvColumnNameMappingBeanLoader<T> filter(final CsvNamedValueFilter filter) {
		getCsvListHandler().filter(filter);
		return this;
	}

}