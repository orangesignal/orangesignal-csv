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
import java.util.Arrays;
import java.util.List;

import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.filters.CsvValueFilter;

/**
 * 文字列配列のリストで区切り文字形式データアクセスを行うハンドラを提供します。
 *
 * @author Koji Sugisawa
 */
public class StringArrayListHandler extends AbstractCsvListHandler<String[], StringArrayListHandler> {

	/**
	 * 区切り文字形式データフィルタを保持します。
	 */
	private CsvValueFilter valueFilter;

	/**
	 * デフォルトコンストラクタです。
	 */
	public StringArrayListHandler() {
	}

	/**
	 * 区切り文字形式データフィルタを設定します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 * @return このオブジェクトへの参照
	 * @since 1.2.3
	 */
	public StringArrayListHandler filter(final CsvValueFilter filter) {
		this.valueFilter = filter;
		return this;
	}

	@Override
	public List<String[]> load(final CsvReader reader, final boolean ignoreScalar) throws IOException {
		final List<String[]> results = new ArrayList<String[]>();
		int offset = 0;
		List<String> values;
		while ((values = reader.readValues()) != null && (ignoreScalar || limit <= 0 || results.size() < limit)) {
			if (valueFilter != null && !valueFilter.accept(values)) {
				continue;
			}
			if (!ignoreScalar && offset < this.offset) {
				offset++;
				continue;
			}
			results.add(values.toArray(new String[0]));
		}
		return results;
	}

	@Override
	public void save(final List<String[]> list, final CsvWriter writer) throws IOException {
		for (final String[] values : list) {
			final List<String> _values = Arrays.asList(values);
			if (valueFilter != null && !valueFilter.accept(_values)) {
				continue;
			}
			writer.writeValues(_values);
		}
	}

}