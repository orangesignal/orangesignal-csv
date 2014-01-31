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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.bean.CsvBeanTemplate;
import com.orangesignal.csv.bean.FieldUtils;

/**
 * Java プログラム要素で区切り文字形式データアクセスを行う区切り文字形式出力ストリームを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public class CsvBeanWriter<T> implements Closeable, Flushable {

	/**
	 * 区切り文字形式出力ストリームを保持します。
	 */
	private CsvWriter writer;

	/**
	 * Java プログラム要素操作の簡素化ヘルパーを保持します。
	 */
	private final CsvBeanTemplate<T> template;

	/**
	 * 区切り文字形式データの列見出し (ヘッダ) 行を出力するかどうかを保持します。
	 * 
	 * @since 2.1
	 */
	private final boolean header;

	/**
	 * 項目名のリストを保持します。
	 */
	private List<String> columnNames;

	// ------------------------------------------------------------------------
	// 利便性のための静的メソッド

	/**
	 * 新しい {@link CsvBeanWriter} のインスタンスを返します。
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param type Java プログラム要素の型
	 * @return 新しい {@link CsvBeanWriter} のインスタンス
	 * @throws IllegalArgumentException {@code writer} または {@code type} が {@code null} の場合。
	 */
	public static <T> CsvBeanWriter<T> newInstance(final CsvWriter writer, final Class<T> type) {
		return new CsvBeanWriter<T>(writer, type);
	}

	/**
	 * 新しい {@link CsvBeanWriter} のインスタンスを返します。
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param type Java プログラム要素の型
	 * @param header 区切り文字形式データの列見出し (ヘッダ) 行を出力するかどうか
	 * @return 新しい {@link CsvBeanWriter} のインスタンス
	 * @throws IllegalArgumentException {@code writer} または {@code type} が {@code null} の場合。
	 * @since 2.1
	 */
	public static <T> CsvBeanWriter<T> newInstance(final CsvWriter writer, final Class<T> type, final boolean header) {
		return new CsvBeanWriter<T>(writer, type, header);
	}

	/**
	 * 新しい {@link CsvBeanWriter} のインスタンスを返します。
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @return 新しい {@link CsvBeanWriter} のインスタンス
	 * @throws IllegalArgumentException {@code writer} または {@code template} が {@code null} の場合。
	 */
	public static <T> CsvBeanWriter<T> newInstance(final CsvWriter writer, final CsvBeanTemplate<T> template) {
		return new CsvBeanWriter<T>(writer, template);
	}

	/**
	 * 新しい {@link CsvBeanWriter} のインスタンスを返します。
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @param header 区切り文字形式データの列見出し (ヘッダ) 行を出力するかどうか
	 * @return 新しい {@link CsvBeanWriter} のインスタンス
	 * @throws IllegalArgumentException {@code writer} または {@code template} が {@code null} の場合。
	 * @since 2.1
	 */
	public static <T> CsvBeanWriter<T> newInstance(final CsvWriter writer, final CsvBeanTemplate<T> template, final boolean header) {
		return new CsvBeanWriter<T>(writer, template, header);
	}

	// ------------------------------------------------------------------------
	// コンストラクタ

	/**
	 * 指定された区切り文字形式出力ストリームと Java プログラム要素の型を使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param type Java プログラム要素の型
	 * @throws IllegalArgumentException {@code writer} または {@code type} が {@code null} の場合。
	 */
	public CsvBeanWriter(final CsvWriter writer, final Class<T> type) {
		this(writer, new CsvBeanTemplate<T>(type), true);
	}

	/**
	 * 指定された区切り文字形式出力ストリームと Java プログラム要素の型を使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param type Java プログラム要素の型
	 * @param header 区切り文字形式データの列見出し (ヘッダ) 行を出力するかどうか
	 * @throws IllegalArgumentException {@code writer} または {@code type} が {@code null} の場合。
	 * @since 2.1
	 */
	public CsvBeanWriter(final CsvWriter writer, final Class<T> type, final boolean header) {
		this(writer, new CsvBeanTemplate<T>(type), header);
	}

	/**
	 * 指定された区切り文字形式出力ストリームと Java プログラム要素操作の簡素化ヘルパーを使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @throws IllegalArgumentException {@code writer} または {@code template} が {@code null} の場合。
	 */
	public CsvBeanWriter(final CsvWriter writer, final CsvBeanTemplate<T> template) {
		this(writer, template, true);
	}

	/**
	 * 指定された区切り文字形式出力ストリームと Java プログラム要素操作の簡素化ヘルパーを使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @param header 区切り文字形式データの列見出し (ヘッダ) 行を出力するかどうか
	 * @throws IllegalArgumentException {@code writer} または {@code template} が {@code null} の場合。
	 * @since 2.1
	 */
	public CsvBeanWriter(final CsvWriter writer, final CsvBeanTemplate<T> template, final boolean header) {
		if (writer == null) {
			throw new IllegalArgumentException("CsvWriter must not be null");
		}
		if (template == null) {
			throw new IllegalArgumentException("CsvBeanTemplate must not be null");
		}
		this.writer   = writer;
		this.template = template;
		this.header   = header;
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

	private void ensureHeader() throws IOException {
		synchronized (this) {
			if (columnNames == null) {
				final List<String> names = new ArrayList<String>();
				for (final Field f : template.getType().getDeclaredFields()) {
					final String name = f.getName();
					if (template.isTargetName(name)) {
						names.add(name);
					}
				}
				if (header) {
					writer.writeValues(names);
				}
				columnNames = Collections.unmodifiableList(names);
			}
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
			columnNames = null;
		}
	}

	// ------------------------------------------------------------------------
	// パブリック メソッド

	/**
	 * 可能であれば項目名を書き込みます。項目名が既に書き込まれている場合、このメソッドは何も行いません。
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void writeHeader() throws IOException {
		synchronized (this) {
			ensureOpen();
			ensureHeader();
		}
	}

	/**
	 * 指定された Java プログラム要素を区切り文字形式で書き込みます。
	 * {@code null} が指定された場合は空行が書き込まれます。
	 * 
	 * @param bean 書き込む Java プログラム要素。または {@code null}
	 * @return データの出力を行った場合は {@code true} それ以外の場合 (フィルタにより書き込みがスキップされた場合) は {@code false}
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public boolean write(final T bean) throws IOException {
		synchronized (this) {
			ensureOpen();
			ensureHeader();

			// 要素が null の場合は null 出力します。
			if (bean == null) {
				writer.writeValues(null);
				return true;
			}

			final List<String> values = toValues(bean);
			if (template.isAccept(columnNames, values)) {
				return false;
			}
			writer.writeValues(values);
			return true;
		}
	}

	private List<String> toValues(final T bean) throws IOException {
		final int columnCount = columnNames.size();
		final String[] values = new String[columnCount];
		for (int i = 0; i < columnCount; i++) {
			final String name = columnNames.get(i);
			if (name == null) {
				continue;
			}
			final Field f = FieldUtils.getField(bean.getClass(), name);
			values[i] = template.objectToString(name, FieldUtils.getFieldValue(bean, f));
		}
		return Arrays.asList(values);
	}

	// ------------------------------------------------------------------------
	// getter / setter

	/**
	 * Java プログラム要素操作の簡素化ヘルパーを返します。
	 * 
	 * @return Java プログラム要素操作の簡素化ヘルパー
	 * @since 2.1
	 */
	public CsvBeanTemplate<T> getTemplate() {
		return template;
	}

}