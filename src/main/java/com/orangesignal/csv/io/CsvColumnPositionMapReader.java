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

package com.orangesignal.csv.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.filters.CsvValueFilter;

/**
 * 項目位置と項目値のマップで区切り文字形式データアクセスを行う区切り文字形式入力ストリームを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public class CsvColumnPositionMapReader implements Closeable {

	/**
	 * 区切り文字形式入力ストリームを保持します。
	 */
	private CsvReader reader;

	/**
	 * 区切り文字形式データフィルタを保持します。
	 */
	private CsvValueFilter filter;

	// ------------------------------------------------------------------------
	// コンストラクタ

	/**
	 * 指定された区切り文字形式入力ストリームを使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @throws IllegalArgumentException {@code reader} が {@code null} の場合。
	 */
	public CsvColumnPositionMapReader(final CsvReader reader) {
		if (reader == null) {
			throw new IllegalArgumentException("CsvReader must not be null");
		}
		this.reader = reader;
	}

	// ------------------------------------------------------------------------
	// プライベート メソッド

	/**
	 * Checks to make sure that the stream has not been closed
	 */
	private void ensureOpen() throws IOException {
		if (reader == null) {
			throw new IOException("CsvReader closed");
		}
	}

	// ------------------------------------------------------------------------
	// オーバーライド メソッド

	@Override
	public void close() throws IOException {
		synchronized (this) {
			ensureOpen();
			reader.close();
			reader = null;
		}
	}

	// ------------------------------------------------------------------------
	// パブリック メソッド

	/**
	 * 論理行を読込み項目名と項目値のマップとして返します。
	 *
	 * @return 項目名と項目値のマップ。ストリームの終わりに達した場合は {@code null}
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public SortedMap<Integer, String> read() throws IOException {
		synchronized (this) {
			ensureOpen();

			final List<String> values = nextValues();
			if (values == null) {
				return null;
			}
			return convert(values);
		}
	}

	/**
	 * 論理行を読込み CSV トークンの値をリストとして返します。
	 * 
	 * @return CSV トークンの値をリスト。ストリームの終わりに達している場合は {@code null}
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public List<String> readValues() throws IOException {
		synchronized (this) {
			ensureOpen();
			return nextValues();
		}
	}

	/**
	 * 指定された CSV トークンの値をリストを項目名と項目値のマップへ変換して返します。
	 * 
	 * @param values CSV トークンの値をリスト
	 * @return 変換された項目名と項目値のマップ
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public SortedMap<Integer, String> toMap(final List<String> values) throws IOException {
		synchronized (this) {
			ensureOpen();
			return convert(values);
		}
	}

	private List<String> nextValues() throws IOException {
		List<String> values;
		while ((values = reader.readValues()) != null) {
			if (filter != null && !filter.accept(values)) {
				continue;
			}
			return values;
		}
		return null;
	}

	private SortedMap<Integer, String> convert(final List<String> values) {
		final SortedMap<Integer, String> map = new TreeMap<Integer, String>();
		final int len = values.size();
		for (int pos = 0; pos < len; pos++) {
			map.put(pos, values.get(pos));
		}
		return map;
	}

	// ------------------------------------------------------------------------
	// セッター / ゲッター

	/**
	 * 区切り文字形式データフィルタを返します。
	 * 
	 * @return 区切り文字形式データフィルタ。または {@code null}
	 */
	public CsvValueFilter getFilter() {
		return filter;
	}

	/**
	 * 区切り文字形式データフィルタを設定します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 */
	public void setFilter(final CsvValueFilter filter) {
		synchronized (this) {
			this.filter = filter;
		}
	}

}