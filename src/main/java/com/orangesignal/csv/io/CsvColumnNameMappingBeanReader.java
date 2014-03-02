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
import java.util.Map;

import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.bean.CsvColumnNameMappingBeanTemplate;
import com.orangesignal.csv.bean.FieldUtils;

/**
 * 区切り文字形式データの項目名を基準として Java プログラム要素と区切り文字形式データアクセスを行う区切り文字形式入力ストリームを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public class CsvColumnNameMappingBeanReader<T> implements Closeable {

	/**
	 * 区切り文字形式入力ストリームを保持します。
	 */
	private CsvReader reader;

	/**
	 * Java プログラム要素操作の簡素化ヘルパーを保持します。
	 */
	private final CsvColumnNameMappingBeanTemplate<T> template;

	/**
	 * 項目名のリストを保持します。
	 */
	private List<String> columnNames;

	private Field[] fields;
	private Map<String, Object[]> fieldColumnsMap;

	// ------------------------------------------------------------------------
	// 利便性のための静的メソッド

	/**
	 * 新しい {@link CsvColumnNameMappingBeanReader} のインスタンスを返します。
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @param type Java プログラム要素の型
	 * @return 新しい {@link CsvColumnNameMappingBeanReader} のインスタンス
	 * @throws IllegalArgumentException {@code reader} または {@code type} が {@code null} の場合。
	 */
	public static <T> CsvColumnNameMappingBeanReader<T> newInstance(final CsvReader reader, final Class<T> type) {
		return new CsvColumnNameMappingBeanReader<T>(reader, type);
	}

	/**
	 * 新しい {@link CsvColumnNameMappingBeanReader} のインスタンスを返します。
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @return 新しい {@link CsvColumnNameMappingBeanReader} のインスタンス
	 * @throws IllegalArgumentException {@code reader} または {@code template} が {@code null} の場合。
	 */
	public static <T> CsvColumnNameMappingBeanReader<T> newInstance(final CsvReader reader, final CsvColumnNameMappingBeanTemplate<T> template) {
		return new CsvColumnNameMappingBeanReader<T>(reader, template);
	}

	// ------------------------------------------------------------------------
	// コンストラクタ

	/**
	 * 指定された区切り文字形式入力ストリームと Java プログラム要素操作の簡素化ヘルパーを使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @param type Java プログラム要素の型
	 * @throws IllegalArgumentException {@code reader} または {@code type} が {@code null} の場合。
	 */
	public CsvColumnNameMappingBeanReader(final CsvReader reader, final Class<T> type) {
		this(reader, new CsvColumnNameMappingBeanTemplate<T>(type));
	}

	/**
	 * 指定された区切り文字形式入力ストリームと Java プログラム要素操作の簡素化ヘルパーを使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @throws IllegalArgumentException {@code reader} または {@code template} が {@code null} の場合。
	 */
	public CsvColumnNameMappingBeanReader(final CsvReader reader, final CsvColumnNameMappingBeanTemplate<T> template) {
		if (reader == null) {
			throw new IllegalArgumentException("CsvReader must not be null");
		}
		if (template == null) {
			throw new IllegalArgumentException("CsvColumnNameMappingBeanTemplate must not be null");
		}
		this.reader = reader;
		this.template = template;
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

	private void ensureHeader() throws IOException {
		synchronized (this) {
			if (columnNames == null) {
				columnNames = Collections.unmodifiableList(reader.readValues());
				if (columnNames == null) {
					// ヘッダがない場合は例外をスローします。
					throw new IOException("No header is available");
				}
				template.setupColumnMappingIfNeed();
				fields = template.getType().getDeclaredFields();
				fieldColumnsMap = template.createFieldAndColumnsMap();
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
			fields = null;
			fieldColumnsMap = null;
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
			if (template.isAccept(columnNames, values)) {
				continue;
			}
			return values;
		}
		return null;
	}

	@SuppressWarnings("null")
	private T convert(final List<String> values) throws IOException {
		final T bean = template.createBean();
		for (final Field f : fields) {
			final Object[] columns = fieldColumnsMap.get(f.getName());
			final int count = columns == null ? 0 : columns.length;

			Object o = null;
			if (count == 1) {
				final int pos = columnNames.indexOf(columns[0]);
				if (pos != -1) {
					o = template.stringToObject(f, values.get(pos));
				}
			} else if (count > 1) {
				final StringBuilder sb = new StringBuilder();
				for (final Object column : columns) {
					final int pos = columnNames.indexOf(column);
					if (pos != -1) {
						final String s = values.get(pos);
						if (s != null) {
							sb.append(s);
						}
					}
				}
				o = template.stringToObject(f, sb.toString());
			}
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
	public CsvColumnNameMappingBeanTemplate<T> getTemplate() {
		return template;
	}

}