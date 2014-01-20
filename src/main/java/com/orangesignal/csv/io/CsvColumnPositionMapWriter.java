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
import java.io.Flushable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.filters.CsvValueFilter;

/**
 * 項目位置と項目値のマップで区切り文字形式データアクセスを行う区切り文字形式出力ストリームを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public class CsvColumnPositionMapWriter implements Closeable, Flushable {

	/**
	 * 区切り文字形式出力ストリームを保持します。
	 */
	private CsvWriter writer;

	/**
	 * 区切り文字形式データフィルタを保持します。
	 */
	private CsvValueFilter filter;

	// ------------------------------------------------------------------------
	// コンストラクタ

	/**
	 * 指定された区切り文字形式出力ストリームを使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @throws IllegalArgumentException {@code writer} が {@code null} の場合。
	 */
	public CsvColumnPositionMapWriter(final CsvWriter writer) {
		if (writer == null) {
			throw new IllegalArgumentException("CsvWriter must not be null");
		}
		this.writer = writer;
	}

	// ------------------------------------------------------------------------
	// プライベート メソッド

	/**
	 * Checks to make sure that the stream has not been closed
	 */
	private void ensureOpen() throws IOException {
		if (writer == null) {
			throw new IOException("CsvWriter closed");
		}
	}

	// ------------------------------------------------------------------------
	// オーバーライド メソッド

	@Override
	public void flush() throws IOException {
		synchronized (this) {
			ensureOpen();
			writer.flush();
		}
	}

	@Override
	public void close() throws IOException {
		synchronized (this) {
			ensureOpen();
			writer.close();
			writer = null;
		}
	}

	// ------------------------------------------------------------------------
	// パブリック メソッド

	/**
	 * 指定された項目名と項目値のマップを書き込みます。項目名の書き込みが必要な場合は自動的に書き込みが行われます。
	 * 
	 * @param map 項目名と項目値のマップ
	 * @return 区切り文字形式データフィルタによって書き込みが行われなかった場合は {@code false} それ以外の場合は {@code true}
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public boolean write(final Map<Integer, String> map) throws IOException {
		synchronized (this) {
			ensureOpen();

			// 要素が null の場合は null 出力します。
			if (map == null) {
				writer.writeValues(null);
				return true;
			}

			final List<String> values = convert(map);
			if (filter != null && !filter.accept(values)) {
				return false;
			}
			writer.writeValues(values);
			return true;
		}
	}

	private List<String> convert(final Map<Integer, String> map) {
		final String[] values = new String[getMaxColumnPosition(map) + 1];
		for (final Map.Entry<Integer, String> e : map.entrySet()) {
			final int pos = e.getKey();
			if (pos >= 0) {
				values[pos] = e.getValue();
			}
		}
		return Arrays.asList(values);
	}

	private static int getMaxColumnPosition(final Map<Integer, String> map) {
		final SortedMap<Integer, String> sortedMap;
		if (map instanceof SortedMap) {
			sortedMap = (SortedMap<Integer, String>) map;
		} else {
			sortedMap = new TreeMap<Integer, String>(map);
		}
		return Math.max(sortedMap.lastKey().intValue(), 0);
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