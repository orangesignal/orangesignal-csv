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

import static com.orangesignal.csv.bean.CsvEntityTemplate.getPosition;
import static com.orangesignal.csv.bean.FieldUtils.setFieldValue;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.annotation.CsvColumn;
import com.orangesignal.csv.annotation.CsvColumnException;
import com.orangesignal.csv.annotation.CsvColumns;
import com.orangesignal.csv.annotation.CsvEntity;
import com.orangesignal.csv.bean.CsvEntityTemplate;

/**
 * 区切り文字形式データ注釈要素 {@link CsvEntity} で注釈付けされた Java プログラム要素で区切り文字形式データアクセスを行う区切り文字形式入力ストリームを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public class CsvEntityReader<T> implements Closeable {

	/**
	 * 区切り文字形式入力ストリームを保持します。
	 */
	private CsvReader reader;

	/**
	 * Java プログラム要素操作の簡素化ヘルパーを保持します。
	 */
	private final CsvEntityTemplate<T> template;

	/**
	 * 項目名のリストを保持します。
	 */
	private List<String> columnNames;

	private Field[] fields;

	// ------------------------------------------------------------------------
	// 利便性のための静的メソッド

	/**
	 * 新しい {@link CsvEntityReader} のインスタンスを返します。
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @param entityClass 区切り文字形式データ注釈要素 {@link CsvEntity} で注釈付けされた Java プログラム要素の型
	 * @return 新しい {@link CsvEntityReader} のインスタンス
	 * @throws IllegalArgumentException {@code reader} または {@code entityClass} が {@code null} の場合。
	 */
	public static <T> CsvEntityReader<T> newInstance(final CsvReader reader, final Class<T> entityClass) {
		return new CsvEntityReader<T>(reader, entityClass);
	}

	/**
	 * 新しい {@link CsvEntityReader} のインスタンスを返します。
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @return 新しい {@link CsvEntityReader} のインスタンス
	 * @throws IllegalArgumentException {@code reader} または {@code template} が {@code null} の場合。
	 */
	public static <T> CsvEntityReader<T> newInstance(final CsvReader reader, final CsvEntityTemplate<T> template) {
		return new CsvEntityReader<T>(reader, template);
	}

	// ------------------------------------------------------------------------
	// コンストラクタ

	/**
	 * 指定された区切り文字形式入力ストリームと Java プログラム要素の型を使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @param entityClass 区切り文字形式データ注釈要素 {@link CsvEntity} で注釈付けされた Java プログラム要素の型
	 * @throws IllegalArgumentException {@code reader} または {@code entityClass} が {@code null} の場合。
	 */
	public CsvEntityReader(final CsvReader reader, final Class<T> entityClass) {
		this(reader, new CsvEntityTemplate<T>(entityClass));
	}

	/**
	 * 指定された区切り文字形式入力ストリームと Java プログラム要素操作の簡素化ヘルパーを使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @throws IllegalArgumentException {@code reader} または {@code template} が {@code null} の場合。
	 */
	public CsvEntityReader(final CsvReader reader, final CsvEntityTemplate<T> template) {
		if (reader == null) {
			throw new IllegalArgumentException("CsvReader must not be null");
		}
		if (template == null) {
			throw new IllegalArgumentException("CsvEntityTemplate must not be null");
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
				// ヘッダ行が有効な場合は項目名の一覧を取得します。
				final List<String> names;
				if (template.getType().getAnnotation(CsvEntity.class).header()) {
					names = reader.readValues();
				} else {
					names = template.createColumnNames();
				}

				fields = template.getType().getDeclaredFields();
				template.prepare(names, fields);
				columnNames = Collections.unmodifiableList(names);
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
	 * @throws CsvColumnException 区切り文字形式のデータ項目の検証操作実行中にエラーが発生した場合
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
	 * @throws CsvColumnException 区切り文字形式のデータ項目の検証操作実行中にエラーが発生した場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public T toEntity(final List<String> values) throws IOException {
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

	private T convert(final List<String> values) throws IOException {
		final T entity = template.createBean();
		for (final Field field : fields) {
			Object object = null;
			final CsvColumns columns = field.getAnnotation(CsvColumns.class);
			if (columns != null) {
				if (field.getType().isArray()) {
					object = Array.newInstance(field.getType().getComponentType(), columns.value().length);
					int arrayIndex = 0;
					for (final CsvColumn column : columns.value()) {
						if (!column.access().isReadable()) {
							continue;
						}
						String value;
						final int pos = getPosition(column, field, columnNames);
						if (pos != -1) {
							value = values.get(pos);
						} else {
							value = null;
						}
						if (value == null && !column.defaultValue().isEmpty()) {
							// デフォルト値が指定されていて、値がない場合はデフォルト値を代入します。
							value = column.defaultValue();
						}
						if (value == null && column.required()) {
							// 必須項目の場合に、値がない場合は例外をスローします。
							throw new CsvColumnException(String.format("[line: %d] %s must not be null", reader.getStartLineNumber(), columnNames.get(pos)), values);
						}
						Array.set(object, arrayIndex++, template.stringToObject(field, value));
					}
				} else {
					final StringBuilder sb = new StringBuilder();
					for (final CsvColumn column : columns.value()) {
						if (!column.access().isReadable()) {
							continue;
						}
						final int pos = getPosition(column, field, columnNames);
						if (pos != -1) {
							final String s = values.get(pos);
							if (s != null) {
								sb.append(s);
							} else if (!column.defaultValue().isEmpty()) {
								// デフォルト値が指定されていて、値がない場合はデフォルト値を代入します。
								sb.append(column.defaultValue());
							} else if (column.required()) {
								// 必須項目の場合に、値がない場合は例外をスローします。
								throw new CsvColumnException(String.format("[line: %d] %s must not be null", reader.getStartLineNumber(), columnNames.get(pos)), values);
							}
						}
					}
					object = template.stringToObject(field, sb.toString());
				}
			}
			final CsvColumn column = field.getAnnotation(CsvColumn.class);
			if (column != null && column.access().isReadable()) {
				final int pos = getPosition(column, field, columnNames);
				if (pos != -1) {
					String value = values.get(pos);
					if (value == null && !column.defaultValue().isEmpty()) {
						// デフォルト値が指定されていて、値がない場合はデフォルト値を代入します。
						value = column.defaultValue();
					}
					if (value == null && column.required()) {
						// 必須項目の場合に、値がない場合は例外をスローします。
						throw new CsvColumnException(String.format("[line: %d] %s must not be null", reader.getStartLineNumber(), columnNames.get(pos)), values);
					}
					object = template.stringToObject(field, value);
				}
			}
			if (object != null) {
				setFieldValue(entity, field, object);
			}
		}
		return entity;
	}

	// ------------------------------------------------------------------------
	// getter / setter

	/**
	 * Java プログラム要素操作の簡素化ヘルパーを返します。
	 * 
	 * @return Java プログラム要素操作の簡素化ヘルパー
	 * @since 2.1
	 */
	public CsvEntityTemplate<T> getTemplate() {
		return template;
	}

}