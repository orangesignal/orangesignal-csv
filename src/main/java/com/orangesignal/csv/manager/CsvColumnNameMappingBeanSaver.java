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
import java.util.List;

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.filters.BeanFilter;
import com.orangesignal.csv.filters.CsvNamedValueFilter;
import com.orangesignal.csv.handlers.ColumnNameMappingBeanListHandler;

/**
 * 区切り文字形式データの項目名を基準とする Java プログラム要素のリストと区切り文字形式データの統合出力インタフェースの実装クラスを提供します。
 *
 * @author Koji Sugisawa
 * @since 1.2
 */
public class CsvColumnNameMappingBeanSaver<T> extends AbstractCsvSaver<T, ColumnNameMappingBeanListHandler<T>> {

	/**
	 * データアクセスハンドラを保持します。
	 */
	private final ColumnNameMappingBeanListHandler<T> handler;

	/**
	 * コンストラクタです。
	 * 
	 * @param cfg 区切り文字形式情報
	 * @param beans Java プログラム要素のリスト
	 * @param beanClass Java プログラム要素の型
	 * @throws IllegalArgumentException パラメータが不正な場合
	 */
	protected CsvColumnNameMappingBeanSaver(final CsvConfig cfg, final List<T> beans, final Class<T> beanClass) {
		super(cfg, beans);
		this.handler = new ColumnNameMappingBeanListHandler<T>(beanClass);
	}

	/**
	 * 指定された項目名と Java プログラム要素のフィールド名をマップへ追加します。
	 * 
	 * @param column 項目名
	 * @param field Java プログラム要素のフィールド名
	 * @return このオブジェクトへの参照
	 */
	public CsvColumnNameMappingBeanSaver<T> column(final String column, final String field) {
		handler.column(column, field);
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
	public CsvColumnNameMappingBeanSaver<T> column(final String column, final String field, final Format format) {
		handler.column(column, field, format);
		return this;
	}

	/**
	 * 区切り文字形式データフィルタを設定します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 * @return このオブジェクトへの参照
	 * @since 1.2.3
	 */
	public CsvColumnNameMappingBeanSaver<T> filter(final CsvNamedValueFilter filter) {
		handler.filter(filter);
		return this;
	}

	/**
	 * Java プログラム要素フィルタを設定します。
	 * 
	 * @param filter Java プログラム要素フィルタ
	 * @return このオブジェクトへの参照
	 * @since 1.2.3
	 */
	public CsvColumnNameMappingBeanSaver<T> filter(final BeanFilter filter) {
		handler.filter(filter);
		return this;
	}

	@Override protected ColumnNameMappingBeanListHandler<T> getCsvListHandler() { return handler; }

}