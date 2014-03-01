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
import com.orangesignal.csv.bean.CsvColumnNameMappingBeanOperation;
import com.orangesignal.csv.bean.CsvColumnNameMappingBeanTemplate;
import com.orangesignal.csv.filters.CsvNamedValueFilter;
import com.orangesignal.csv.io.CsvColumnNameMappingBeanReader;
import com.orangesignal.csv.io.CsvColumnNameMappingBeanWriter;

/**
 * 区切り文字形式データの項目名を基準として Java プログラム要素のリストと区切り文字形式データアクセスを行うハンドラを提供します。
 * 
 * @author Koji Sugisawa
 * @see com.orangesignal.csv.manager.CsvColumnNameMappingBeanManager
 */
public class ColumnNameMappingBeanListHandler<T> extends AbstractBeanListHandler<T, CsvColumnNameMappingBeanTemplate<T>, ColumnNameMappingBeanListHandler<T>> implements CsvColumnNameMappingBeanOperation<ColumnNameMappingBeanListHandler<T>> {

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
	public ColumnNameMappingBeanListHandler(final Class<T> type) {
		super(CsvColumnNameMappingBeanTemplate.newInstance(type));
	}

	// ------------------------------------------------------------------------

	@Override
	public ColumnNameMappingBeanListHandler<T> column(final String column, final String field) {
		return column(column, field, null);
	}

	@Override
	public ColumnNameMappingBeanListHandler<T> column(final String column, final String field, final Format format) {
		template.column(column, field, format);
		return this;
	}

	@Override
	public void setColumnMapping(final Map<String, String> columnMapping) {
		template.setColumnMapping(columnMapping);
	}

	@Override
	public ColumnNameMappingBeanListHandler<T> columnMapping(final Map<String, String> columnMapping) {
		setColumnMapping(columnMapping);
		return this;
	}

	@Override
	public ColumnNameMappingBeanListHandler<T> filter(final CsvNamedValueFilter filter) {
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
	public ColumnNameMappingBeanListHandler<T> header(final boolean header) {
		this.header = header;
		return this;
	}

	// ------------------------------------------------------------------------

	@Override
	public List<T> load(final CsvReader reader, final boolean ignoreScalar) throws IOException {
		@SuppressWarnings("resource")
		final CsvColumnNameMappingBeanReader<T> r = new CsvColumnNameMappingBeanReader<T>(reader, template);

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
		final CsvColumnNameMappingBeanWriter<T> w = new CsvColumnNameMappingBeanWriter<T>(writer, template, header);

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