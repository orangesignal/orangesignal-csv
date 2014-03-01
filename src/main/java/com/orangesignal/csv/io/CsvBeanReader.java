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
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.bean.CsvBeanTemplate;
import com.orangesignal.csv.bean.FieldUtils;

/**
 * Java プログラム要素で区切り文字形式データアクセスを行う区切り文字形式入力ストリームを提供します。
 * 
 * @param <T> Java プログラム要素の型
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public class CsvBeanReader<T> implements Closeable {

	/**
	 * 区切り文字形式入力ストリームを保持します。
	 */
	private CsvReader reader;

	/**
	 * Java プログラム要素操作の簡素化ヘルパーを保持します。
	 */
	private final CsvBeanTemplate<T> template;

	/**
	 * 項目名のリストを保持します。
	 */
	private List<String> columnNames;

	// ------------------------------------------------------------------------
	// 利便性のための静的メソッド

	/**
	 * 新しい {@link CsvBeanReader} のインスタンスを返します。
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @param type Java プログラム要素の型
	 * @return 新しい {@link CsvBeanReader} のインスタンス
	 * @throws IllegalArgumentException {@code reader} または {@code type} が {@code null} の場合。
	 */
	public static <T> CsvBeanReader<T> newInstance(final CsvReader reader, final Class<T> type) {
		return new CsvBeanReader<T>(reader, type);
	}

	/**
	 * 新しい {@link CsvBeanReader} のインスタンスを返します。
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @return 新しい {@link CsvBeanReader} のインスタンス
	 * @throws IllegalArgumentException {@code reader} または {@code template} が {@code null} の場合。
	 */
	public static <T> CsvBeanReader<T> newInstance(final CsvReader reader, final CsvBeanTemplate<T> template) {
		return new CsvBeanReader<T>(reader, template);
	}

	// ------------------------------------------------------------------------
	// コンストラクタ

	/**
	 * 指定された区切り文字形式入力ストリームと Java プログラム要素の型を使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @param type Java プログラム要素の型
	 * @throws IllegalArgumentException {@code reader} または {@code type} が {@code null} の場合。
	 */
	public CsvBeanReader(final CsvReader reader, final Class<T> type) {
		this(reader, new CsvBeanTemplate<T>(type));
	}

	/**
	 * 指定された区切り文字形式入力ストリームと Java プログラム要素操作の簡素化ヘルパーを使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @throws IllegalArgumentException {@code reader} または {@code template} が {@code null} の場合。
	 */
	public CsvBeanReader(final CsvReader reader, final CsvBeanTemplate<T> template) {
		if (reader == null) {
			throw new IllegalArgumentException("CsvReader must not be null");
		}
		if (template == null) {
			throw new IllegalArgumentException("CsvBeanTemplate must not be null");
		}
		this.reader = reader;
		this.template = template;
	}

	// ------------------------------------------------------------------------
	// プライベート メソッド

	/**
	 * 区切り文字形式入力ストリームが閉じられていないことを確認するためにチェックします。
	 * 
	 * @throws IOException 区切り文字形式入力ストリームが閉じられている場合
	 */
	private void ensureOpen() throws IOException {
		if (reader == null) {
			throw new IOException("CsvReader closed");
		}
	}

	private void ensureHeader() throws IOException {
		synchronized (this) {
			if (columnNames == null) {
				columnNames = Collections.unmodifiableList(reader.readValues());
				if (columnNames == null) {
					// ヘッダがない場合は例外をスローします。
					throw new IOException("No header is available");
				}
			}
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
			columnNames = null;
		}
	}

	// ------------------------------------------------------------------------
	// パブリック メソッド

	/**
	 * 項目名のリストを返します。
	 * 
	 * @return 項目名のリスト
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public List<String> getHeader() throws IOException {
		synchronized (this) {
			ensureOpen();
			ensureHeader();
			return columnNames;
		}
	}

	/**
	 * 論理行を読込み Java プログラム要素として返します。
	 *
	 * @return Java プログラム要素。ストリームの終わりに達した場合は {@code null}
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public T read() throws IOException {
		synchronized (this) {
			ensureOpen();
			ensureHeader();
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
			ensureHeader();
			return nextValues();
		}
	}

	/**
	 * 指定された CSV トークンの値をリストを Java プログラム要素へ変換して返します。
	 * 
	 * @param values CSV トークンの値をリスト
	 * @return 変換された Java プログラム要素
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public T toBean(final List<String> values) throws IOException {
		synchronized (this) {
			ensureOpen();
			ensureHeader();
			return convert(values);
		}
	}

	private List<String> nextValues() throws IOException {
		List<String> values;
		while ((values = reader.readValues()) != null) {
			if (!template.isAccept(columnNames, values)) {
				return values;
			}
		}
		return null;
	}

	private T convert(final List<String> values) throws IOException {
		final T bean = template.createBean();
		final int len = Math.min(columnNames.size(), values.size());
		for (int pos = 0; pos < len; pos++) {
			final String name = columnNames.get(pos);
			if (!template.isTargetName(name)) {
				continue;
			}
			final Field f = FieldUtils.getField(template.getType(), name);
			final Object o = template.stringToObject(f, values.get(pos));
			if (o != null) {
				FieldUtils.setFieldValue(bean, f, o);
			}
		}
		return bean;
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