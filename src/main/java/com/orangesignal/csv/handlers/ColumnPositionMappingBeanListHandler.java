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

package com.orangesignal.csv.handlers;

import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.bean.CsvColumnPositionMappingBeanTemplate;
import com.orangesignal.csv.filters.CsvValueFilter;
import com.orangesignal.csv.io.CsvColumnPositionMappingBeanReader;
import com.orangesignal.csv.io.CsvColumnPositionMappingBeanWriter;

/**
 * 区切り文字形式データの項目位置を基準として Java プログラム要素のリストと区切り文字形式データアクセスを行うハンドラを提供します。
 * 
 * @author Koji Sugisawa
 * @see com.orangesignal.csv.manager.CsvColumnPositionMappingBeanManager
 */
public class ColumnPositionMappingBeanListHandler<T> extends AbstractBeanListHandler<T, CsvColumnPositionMappingBeanTemplate<T>, ColumnPositionMappingBeanListHandler<T>> {

	/**
	 * 区切り文字形式データの列見出し (ヘッダ) 行を出力するかどうかを保持します。
	 * 
	 * @since 2.1
	 */
	private boolean header = true;

	// ------------------------------------------------------------------------

	/**
	 * コンストラクタです。
	 * 
	 * @param type Java プログラム要素の型
	 * @throws IllegalArgumentException {@code type} が {@code null} の場合
	 */
	public ColumnPositionMappingBeanListHandler(final Class<T> type) {
		super(CsvColumnPositionMappingBeanTemplate.newInstance(type));
	}

	// ------------------------------------------------------------------------

	/**
	 * 指定された Java プログラム要素のフィールド名を現在の最後の項目位置としてマップへ追加します。
	 * 
	 * @param field Java プログラム要素のフィールド名
	 * @return このオブジェクトへの参照
	 */
	public ColumnPositionMappingBeanListHandler<T> addColumn(final String field) {
		return addColumn(field, null);
	}

	/**
	 * 指定された Java プログラム要素のフィールド名を現在の最後の項目位置としてマップへ追加します。
	 * 
	 * @param field Java プログラム要素のフィールド名
	 * @param format フィールドを処理するフォーマットオブジェクト (オプション)
	 * @return このオブジェクトへの参照
	 * @since 1.2
	 */
	public ColumnPositionMappingBeanListHandler<T> addColumn(final String field, final Format format) {
		template.column(field, format);
		return this;
	}

	/**
	 * 指定された項目位置と Java プログラム要素のフィールド名をマップへ追加します。
	 * 
	 * @param position 項目位置
	 * @param field Java プログラム要素のフィールド名
	 * @return このオブジェクトへの参照
	 */
	public ColumnPositionMappingBeanListHandler<T> addColumn(final int position, final String field) {
		return addColumn(position, field, null);
	}

	/**
	 * 指定された項目位置と Java プログラム要素のフィールド名をマップへ追加します。
	 * 
	 * @param position 項目名
	 * @param field Java プログラム要素のフィールド名
	 * @param format フィールドを処理するフォーマットオブジェクト (オプション)
	 * @return このオブジェクトへの参照
	 * @since 1.2
	 */
	public ColumnPositionMappingBeanListHandler<T> addColumn(final int position, final String field, final Format format) {
		template.column(position, field, format);
		return this;
	}

	/**
	 * 項目位置と Java プログラム要素のフィールド名のマップを設定します。
	 * 
	 * @param columnMapping 項目位置と Java プログラム要素のフィールド名のマップ
	 * @throws IllegalArgumentException {@code columnMapping} が {@code null} の場合
	 * @since 1.2.4
	 */
	public void setColumnMapping(final Map<Integer, String> columnMapping) {
		template.setColumnMapping(columnMapping);
	}

	/**
	 * 項目位置と Java プログラム要素のフィールド名のマップを設定します。
	 * 
	 * @param columnMapping 項目位置と Java プログラム要素のフィールド名のマップ
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException {@code columnMapping} が {@code null} の場合
	 */
	public ColumnPositionMappingBeanListHandler<T> columnMapping(final Map<Integer, String> columnMapping) {
		setColumnMapping(columnMapping);
		return this;
	}

	/**
	 * 区切り文字形式データフィルタを設定します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 * @return このオブジェクトへの参照
	 * @since 1.2.3
	 */
	public ColumnPositionMappingBeanListHandler<T> filter(final CsvValueFilter filter) {
		template.filter(filter);
		return this;
	}

	/**
	 * 区切り文字形式データの列見出し (ヘッダ) 行を出力するかどうかを設定します。
	 * 
	 * @param header 区切り文字形式データの列見出し (ヘッダ) 行を出力するかどうか
	 * @return このオブジェクトへの参照
	 * @since 2.1
	 */
	public ColumnPositionMappingBeanListHandler<T> header(final boolean header) {
		this.header = header;
		return this;
	}

	// ------------------------------------------------------------------------

	@Override
	public List<T> load(final CsvReader reader, final boolean ignoreScalar) throws IOException {
		@SuppressWarnings("resource")
		final CsvColumnPositionMappingBeanReader<T> r = new CsvColumnPositionMappingBeanReader<T>(reader, template);

		// データ部を処理します。
		final List<T> results = new ArrayList<T>();
		final boolean order = ignoreScalar || orders != null && !orders.isEmpty();
		int offset = 0;

		List<String> values;
		while ((values = r.readValues()) != null && (order || limit <= 0 || results.size() < limit)) {
			if (beanFilter == null && !order && offset < this.offset) {
				offset++;
				continue;
			}
			final T bean = r.toBean(values);
			if (beanFilter != null) {
				if (!beanFilter.accept(bean)) {
					continue;
				}
				if (!order && offset < this.offset) {
					offset++;
					continue;
				}
			}
			results.add(bean);
		}

		if (ignoreScalar || !order) {
			return results;
		}
		return processScalar(results);
	}

	@Override
	public void save(final List<T> list, final CsvWriter writer) throws IOException {
		@SuppressWarnings("resource")
		final CsvColumnPositionMappingBeanWriter<T> w = new CsvColumnPositionMappingBeanWriter<T>(writer, template, header);

		// データ部を処理します。
		for (final T bean : list) {
			// 要素が null の場合は null 出力します。
			if (bean == null) {
				w.write(null);
				continue;
			} else if (beanFilter != null && !beanFilter.accept(bean)) {
				continue;
			}

			w.write(bean);
		}
	}

}