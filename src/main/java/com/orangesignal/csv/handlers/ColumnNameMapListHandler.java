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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.filters.CsvNamedValueFilter;
import com.orangesignal.csv.io.CsvColumnNameMapReader;
import com.orangesignal.csv.io.CsvColumnNameMapWriter;

/**
 * 項目名と項目値のマップのリストで区切り文字形式データアクセスを行うハンドラを提供します。
 *
 * @author Koji Sugisawa
 * @since 1.1
 */
public class ColumnNameMapListHandler extends AbstractCsvListHandler<Map<String, String>, ColumnNameMapListHandler> {

	/**
	 * 項目名のリストを保持します。
	 */
	private List<String> columnNames;

	/**
	 * 区切り文字形式データの列見出し (ヘッダ) 行を出力するかどうかを保持します。
	 * 
	 * @since 2.1
	 */
	private boolean header = true;

	/**
	 * 区切り文字形式データフィルタを保持します。
	 */
	private CsvNamedValueFilter valueFilter;

	/**
	 * デフォルトコンストラクタです。
	 */
	public ColumnNameMapListHandler() {}

	/**
	 * 指定された項目名を項目名の一覧へ追加します。
	 * 
	 * @param columnName 項目名
	 * @return このオブジェクトへの参照
	 */
	public ColumnNameMapListHandler addColumn(final String columnName) {
		if (columnNames == null) {
			columnNames = new ArrayList<String>();
		}
		columnNames.add(columnName);
		return this;
	}

	/**
	 * 指定された項目名の一覧を設定します。
	 * 
	 * @param columnNames 項目名の一覧
	 * @return このオブジェクトへの参照
	 */
	public ColumnNameMapListHandler columnNames(final Collection<String> columnNames) {
		this.columnNames = new ArrayList<String>(columnNames);
		return this;
	}

	/**
	 * 区切り文字形式データフィルタを設定します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 * @return このオブジェクトへの参照
	 * @since 1.2.3
	 */
	public ColumnNameMapListHandler filter(final CsvNamedValueFilter filter) {
		this.valueFilter = filter;
		return this;
	}

	/**
	 * 区切り文字形式データの列見出し (ヘッダ) 行を出力するかどうかを設定します。
	 * 
	 * @param header 区切り文字形式データの列見出し (ヘッダ) 行を出力するかどうか
	 * @return このオブジェクトへの参照
	 * @since 2.1
	 */
	public ColumnNameMapListHandler header(final boolean header) {
		this.header = header;
		return this;
	}

	@Override
	public List<Map<String, String>> load(final CsvReader reader, final boolean ignoreScalar) throws IOException {
		@SuppressWarnings("resource")
		final CsvColumnNameMapReader r = new CsvColumnNameMapReader(reader, columnNames);
		r.setFilter(valueFilter);

		// データ部を処理します。
		final List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		int offset = 0;

		List<String> values;
		while ((values = r.readValues()) != null && (ignoreScalar || limit <= 0 || results.size() < limit)) {
			if (!ignoreScalar && offset < this.offset) {
				offset++;
				continue;
			}
			results.add(r.toMap(values));
		}
		return results;
	}

	@Override
	public void save(final List<Map<String, String>> list, final CsvWriter writer) throws IOException {
		@SuppressWarnings("resource")
		final CsvColumnNameMapWriter w = new CsvColumnNameMapWriter(writer, columnNames, header);
		w.setFilter(valueFilter);

		// データ部を処理します。
		for (final Map<String, String> map : list) {
			w.write(map);
		}
	}

}