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
import java.util.List;
import java.util.Map;

import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.filters.CsvValueFilter;
import com.orangesignal.csv.io.CsvColumnPositionMapReader;
import com.orangesignal.csv.io.CsvColumnPositionMapWriter;

/**
 * 項目位置と項目値のマップのリストで区切り文字形式データアクセスを行うハンドラを提供します。
 *
 * @author Koji Sugisawa
 * @since 1.1
 */
public class ColumnPositionMapListHandler extends AbstractCsvListHandler<Map<Integer, String>, ColumnPositionMapListHandler> {

	/**
	 * 区切り文字形式データフィルタを保持します。
	 */
	private CsvValueFilter valueFilter;

	/**
	 * デフォルトコンストラクタです。
	 */
	public ColumnPositionMapListHandler() {}

	/**
	 * 区切り文字形式データフィルタを設定します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 * @return このオブジェクトへの参照
	 * @since 1.2.3
	 */
	public ColumnPositionMapListHandler filter(final CsvValueFilter filter) {
		this.valueFilter = filter;
		return this;
	}

	@Override
	public List<Map<Integer, String>> load(final CsvReader reader, final boolean ignoreScalar) throws IOException {
		@SuppressWarnings("resource")
		final CsvColumnPositionMapReader r = new CsvColumnPositionMapReader(reader);
		r.setFilter(valueFilter);

		// データ部を処理します。
		final List<Map<Integer, String>> results = new ArrayList<Map<Integer, String>>();
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
	public void save(final List<Map<Integer, String>> list, final CsvWriter writer) throws IOException {
		@SuppressWarnings("resource")
		final CsvColumnPositionMapWriter w = new CsvColumnPositionMapWriter(writer);
		w.setFilter(valueFilter);

		// データ部を処理します。
		for (final Map<Integer, String> map : list) {
			w.write(map);
		}
	}

}