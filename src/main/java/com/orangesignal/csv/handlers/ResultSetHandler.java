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

package com.orangesignal.csv.handlers;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.orangesignal.csv.CsvHandler;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvResultSet;
import com.orangesignal.csv.CsvWriter;

/**
 * データベースの結果セットで区切り文字形式データアクセスを行うハンドラを提供します。
 *
 * @author Koji Sugisawa
 */
public class ResultSetHandler implements CsvHandler<ResultSet> {

	/**
	 * 区切り文字形式データの列見出し (ヘッダ) 行を出力するかどうかを保持します。
	 * 
	 * @since 2.1
	 */
	private boolean header = true;

	/**
	 * デフォルトコンストラクタです。
	 */
	public ResultSetHandler() {
	}

	/**
	 * 区切り文字形式データの列見出し (ヘッダ) 行を出力するかどうかを設定します。
	 * 
	 * @param header 区切り文字形式データの列見出し (ヘッダ) 行を出力するかどうか
	 * @return このオブジェクトへの参照
	 * @since 2.1
	 */
	public ResultSetHandler header(final boolean header) {
		this.header = header;
		return this;
	}

	@Override
	public ResultSet load(final CsvReader reader) throws IOException {
		return new CsvResultSet(reader);
	}

	@Override
	public void save(final ResultSet rs, final CsvWriter writer) throws IOException {
		try {
			final int count = writeHeader(rs.getMetaData(), writer);
			while (rs.next()) {
				final List<String> list = new ArrayList<String>(count);
				for (int i = 1; i <= count; i++) {
					final Object o = rs.getObject(i);
					if (rs.wasNull()) {
						list.add(null);
					} else {
						list.add(o.toString());
					}
				}
				writer.writeValues(list);
			}
		} catch (SQLException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	private int writeHeader(final ResultSetMetaData meta, final CsvWriter writer) throws IOException {
		try {
			final int count = meta.getColumnCount();
			final List<String> list = new ArrayList<String>(count);
			for (int i = 1; i <= count; i++) {
				list.add(meta.getColumnLabel(i));
			}
			if (header) {
				writer.writeValues(list);
			}
			return count;
		} catch (SQLException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

}
