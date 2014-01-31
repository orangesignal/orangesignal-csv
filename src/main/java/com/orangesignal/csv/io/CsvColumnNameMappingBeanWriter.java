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
import java.util.Arrays;
import java.util.List;

import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.bean.CsvColumnNameMappingBeanTemplate;
import com.orangesignal.csv.bean.FieldUtils;

/**
 * 区切り文字形式データの項目名を基準として Java プログラム要素と区切り文字形式データアクセスを行う区切り文字形式出力ストリームを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public class CsvColumnNameMappingBeanWriter<T> implements Closeable, Flushable {

	/**
	 * 区切り文字形式出力ストリームを保持します。
	 */
	private CsvWriter writer;

	/**
	 * Java プログラム要素操作の簡素化ヘルパーを保持します。
	 */
	private final CsvColumnNameMappingBeanTemplate<T> template;

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

	/**
	 * 項目名の数を保存します。
	 */
	private int columnCount = -1;

	// ------------------------------------------------------------------------
	// 利便性のための静的メソッド

	/**
	 * 新しい {@link CsvColumnNameMappingBeanWriter} のインスタンスを返します。
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param type Java プログラム要素の型
	 * @return 新しい {@link CsvColumnNameMappingBeanWriter} のインスタンス
	 * @throws IllegalArgumentException {@code writer} または {@code type} が {@code null} の場合。
	 */
	public static <T> CsvColumnNameMappingBeanWriter<T> newInstance(final CsvWriter writer, final Class<T> type) {
		return new CsvColumnNameMappingBeanWriter<T>(writer, type);
	}

	/**
	 * 新しい {@link CsvColumnNameMappingBeanWriter} のインスタンスを返します。
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param type Java プログラム要素の型
	 * @param header 区切り文字形式データの列見出し (ヘッダ) 行を出力するかどうか
	 * @return 新しい {@link CsvColumnNameMappingBeanWriter} のインスタンス
	 * @throws IllegalArgumentException {@code writer} または {@code type} が {@code null} の場合。
	 * @since 2.1
	 */
	public static <T> CsvColumnNameMappingBeanWriter<T> newInstance(final CsvWriter writer, final Class<T> type, final boolean header) {
		return new CsvColumnNameMappingBeanWriter<T>(writer, type, header);
	}

	/**
	 * 新しい {@link CsvColumnNameMappingBeanWriter} のインスタンスを返します。
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @return 新しい {@link CsvColumnNameMappingBeanWriter} のインスタンス
	 * @throws IllegalArgumentException {@code writer} または {@code template} が {@code null} の場合。
	 */
	public static <T> CsvColumnNameMappingBeanWriter<T> newInstance(final CsvWriter writer, final CsvColumnNameMappingBeanTemplate<T> template) {
		return new CsvColumnNameMappingBeanWriter<T>(writer, template);
	}

	/**
	 * 新しい {@link CsvColumnNameMappingBeanWriter} のインスタンスを返します。
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @param header 区切り文字形式データの列見出し (ヘッダ) 行を出力するかどうか
	 * @return 新しい {@link CsvColumnNameMappingBeanWriter} のインスタンス
	 * @throws IllegalArgumentException {@code writer} または {@code template} が {@code null} の場合。
	 * @since 2.1
	 */
	public static <T> CsvColumnNameMappingBeanWriter<T> newInstance(final CsvWriter writer, final CsvColumnNameMappingBeanTemplate<T> template, final boolean header) {
		return new CsvColumnNameMappingBeanWriter<T>(writer, template, header);
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
	public CsvColumnNameMappingBeanWriter(final CsvWriter writer, final Class<T> type) {
		this(writer, new CsvColumnNameMappingBeanTemplate<T>(type), true);
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
	public CsvColumnNameMappingBeanWriter(final CsvWriter writer, final Class<T> type, final boolean header) {
		this(writer, new CsvColumnNameMappingBeanTemplate<T>(type), header);
	}

	/**
	 * 指定された区切り文字形式出力ストリームと Java プログラム要素操作の簡素化ヘルパーを使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @throws IllegalArgumentException {@code writer} または {@code template} が {@code null} の場合。
	 */
	public CsvColumnNameMappingBeanWriter(final CsvWriter writer, final CsvColumnNameMappingBeanTemplate<T> template) {
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
	public CsvColumnNameMappingBeanWriter(final CsvWriter writer, final CsvColumnNameMappingBeanTemplate<T> template, final boolean header) {
		if (writer == null) {
			throw new IllegalArgumentException("CsvWriter must not be null");
		}
		if (template == null) {
			throw new IllegalArgumentException("CsvColumnNameMappingBeanTemplate must not be null");
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
				template.setupColumnMappingIfNeed();
				final List<String> names = template.createColumnNames();
				if (header) {
					writer.writeValues(names);
				}
				columnNames = names;
				columnCount = names.size();
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
			columnCount = -1;
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
		final String[] values = new String[columnCount];
		for (int i = 0; i < columnCount; i++) {
			final String columnName = columnNames.get(i);
			if (columnName == null) {
				continue;
			}
			final String fieldName = template.getFieldName(columnName);
			if (fieldName == null) {
				continue;
			}
			final Field f = FieldUtils.getField(bean.getClass(), fieldName);
			values[i] = template.objectToString(columnName, FieldUtils.getFieldValue(bean, f));
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
	public CsvColumnNameMappingBeanTemplate<T> getTemplate() {
		return template;
	}

}