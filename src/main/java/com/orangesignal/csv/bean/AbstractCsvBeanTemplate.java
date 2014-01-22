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

package com.orangesignal.csv.bean;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.Format;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java プログラム要素の操作を簡素化するヘルパーの基底クラスを提供します。
 * 
 * @param <T> Java プログラム要素の型
 * @param <O> Java プログラム要素の操作を簡素化するヘルパークラスの型
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public abstract class AbstractCsvBeanTemplate<T, O extends AbstractCsvBeanTemplate<T, O>> {

	/**
	 * Java プログラム要素の型を保持します。
	 */
	private Class<T> type;

	/**
	 * Java プログラム要素のフィールド名と項目値を解析するオブジェクトのマップを保持します。
	 */
	private Map<String, Format> valueParserMapping = new HashMap<String, Format>();

	/**
	 * 項目名 (または項目位置) と項目値へ書式化するオブジェクトのマップを保持します。
	 */
	private Map<Object, Format> valueFormatterMapping = new HashMap<Object, Format>();

	/**
	 * 区切り文字形式データの項目値コンバータを保持します。
	 */
	private CsvValueConverter valueConverter = new SimpleCsvValueConverter();

	// ------------------------------------------------------------------------
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param type Java プログラム要素の型
	 * @throws IllegalArgumentException {@code type} が {@code null} の場合。
	 */
	protected AbstractCsvBeanTemplate(final Class<T> type) {
		if (type == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		this.type = type;
	}

	// ------------------------------------------------------------------------
	// ゲッター / セッター

	/**
	 * Java プログラム要素の型を返します。
	 * 
	 * @return Java プログラム要素の型
	 */
	public Class<T> getType() {
		return type;
	}

	/**
	 * Java プログラム要素のフィールド名と項目値を解析するオブジェクトのマップを設定します。
	 * 
	 * @param valueParserMapping Java プログラム要素のフィールド名と項目値を解析するオブジェクトのマップ
	 * @throws IllegalArgumentException {@code valueParserMapping} が {@code null} の場合
	 */
	public void setValueParserMapping(final Map<String, Format> valueParserMapping) {
		if (valueParserMapping == null) {
			throw new IllegalArgumentException("CSV value parser mapping must not be null");
		}
		this.valueParserMapping = valueParserMapping;
	}

	/**
	 * Java プログラム要素のフィールド名と項目値を解析するオブジェクトのマップを設定します。
	 * 
	 * @param valueParserMapping Java プログラム要素のフィールド名と項目値を解析するオブジェクトのマップ
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException {@code valueParserMapping} が {@code null} の場合
	 */
	@SuppressWarnings("unchecked")
	public O valueParserMapping(final Map<String, Format> valueParserMapping) {
		setValueParserMapping(valueParserMapping);
		return (O) this;
	}

	/**
	 * 項目名 (または項目位置) と項目値へ書式化するオブジェクトのマップを設定します。
	 * 
	 * @param valueFormatterMapping 項目名 (または項目位置) と項目値へ書式化するオブジェクトのマップ
	 * @throws IllegalArgumentException {@code valueFormaterMapping} が {@code null} の場合
	 */
	public void setValueFormatterMapping(final Map<Object, Format> valueFormatterMapping) {
		if (valueFormatterMapping == null) {
			throw new IllegalArgumentException("CSV value formatter mapping must not be null");
		}
		this.valueFormatterMapping = valueFormatterMapping;
	}

	/**
	 * 項目名 (または項目位置) と項目値へ書式化するオブジェクトのマップを設定します。
	 * 
	 * @param valueFormatterMapping 項目名 (または項目位置) と項目値へ書式化するオブジェクトのマップ
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException {@code valueFormaterMapping} が {@code null} の場合
	 */
	@SuppressWarnings("unchecked")
	public O valueFormatterMapping(final Map<Object, Format> valueFormatterMapping) {
		setValueFormatterMapping(valueFormatterMapping);
		return (O) this;
	}

	/**
	 * 区切り文字形式データの項目値コンバータを設定します。
	 * 
	 * @param valueConverter 区切り文字形式データの項目値コンバータ
	 * @throws IllegalArgumentException {@code valueConverter} が {@code null} の場合
	 */
	public void setValueConverter(final CsvValueConverter valueConverter) {
		if (valueConverter == null) {
			throw new IllegalArgumentException("CsvValueConverter must not be null");
		}
		this.valueConverter = valueConverter;
	}

	/**
	 * 区切り文字形式データの項目値コンバータを設定します。
	 * 
	 * @param valueConverter 区切り文字形式データの項目値コンバータ
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException {@code valueConverter} が {@code null} の場合
	 */
	@SuppressWarnings("unchecked")
	public O valueConverter(final CsvValueConverter valueConverter) {
		setValueConverter(valueConverter);
		return (O) this;
	}

	// ------------------------------------------------------------------------

	/**
	 * 指定された Java プログラム要素のフィールドを処理するフォーマットオブジェクトを設定します。
	 * 
	 * @param name Java プログラム要素のフィールド名
	 * @param format フィールドを処理するフォーマットオブジェクト
	 * @return このオブジェクトへの参照
	 */
	@SuppressWarnings("unchecked")
	public O format(final String name, final Format format) {
		setValueParser(name, format);
		setValueFormatter(name, format);
		return (O) this;
	}

	/**
	 * <p>指定された Java プログラム要素のフィールド名と項目値を解析するオブジェクトをマップへ追加します。</p>
	 * <p>
	 * 指定されたフィールド名に既に項目値を解析するオブジェクトが設定されている場合、
	 * 既存の項目値解析オブジェクトへパラメータで指定された項目値解析オブジェクトのパターン文字列を追加します。
	 * </p>
	 * 
	 * @param field Java プログラム要素のフィールド名
	 * @param parser 項目値を解析するオブジェクト
	 */
	public void setValueParser(final String field, final Format parser) {
		final Format src = valueParserMapping.get(field);
		if (src != null) {
			valueParserMapping.put(field, FormatUtils.mergeFormatPattern(src, parser));
		} else {
			valueParserMapping.put(field, parser);
		}
	}

	/**
	 * 指定された項目名 (または項目位置) と項目値へ書式化するオブジェクトをマップへ追加します。
	 * 
	 * @param column 項目名 (または項目位置)
	 * @param formatter 項目値へ書式化するオブジェクト
	 */
	public void setValueFormatter(final Object column, final Format formatter) {
		valueFormatterMapping.put(column, formatter);
	}

	/**
	 * Java プログラム要素の型が表すクラスの新しいインスタンスを生成します。
	 * 
	 * @return Java プログラム要素の型が表す、クラスの新しく割り当てられたインスタンス
	 * @throws IOException Java プログラム要素のインスタンス化に失敗した場合
	 */
	public T createBean() throws IOException {
		try {
			return type.newInstance();
		} catch (final IllegalAccessException e) {
			throw new IOException("Cannot create " + type.getName() + ": " + e.getMessage(), e);
		} catch (final InstantiationException e) {
			throw new IOException("Cannot create " + type.getName() + ": " + e.getMessage(), e);
		}
	}

	/**
	 * 指定された項目名 (または項目位置) と Java プログラム要素のフィールド名のマップと Java プログラム要素の型から、
	 * Java プログラム要素のフィールド名と項目名群のマップを構築して返します。
	 * 
	 * @param map 項目名 (または項目位置) と Java プログラム要素のフィールド名のマップ
	 * @return Java プログラム要素のフィールド名と項目名群のマップ
	 */
	public Map<String, Object[]> createFieldAndColumnsMap(final Map<?, String> map) {
		final Map<String, Object[]> results = new HashMap<String, Object[]>();
		for (final Field f : type.getDeclaredFields()) {
			final String fieldName = f.getName();
			final List<Object> list = new ArrayList<Object>();
			for (final Map.Entry<?, String> e : map.entrySet()) {
				if (fieldName.equals(e.getValue())) {
					list.add(e.getKey());
				}
			}
			if (list.size() > 0) {
				results.put(fieldName, list.toArray());
			}
		}
		return results;
	}

	/**
	 * 指定された項目値を指定されたフィールドのオブジェクトへ変換して返します。
	 * この実装は、指定されたフィールドに対応する項目値を解析するオブジェクトが存在する場合は、{@link Format#parseObject(String)} で得られたオブジェクトを返します。
	 * それ以外の場合は、項目値コンバータを使用して得られたオブジェクトを返します。
	 * 
	 * @param field フィールド
	 * @param value 項目値
	 * @return 変換された項目値
	 */
	public Object stringToObject(final Field field, final String value) {
		final Format format = valueParserMapping.get(field.getName());
		if (format != null) {
			if (value == null || value.isEmpty()) {
				return null;
			}
			try {
				return format.parseObject(value);
			} catch (final ParseException e) {
				throw new IllegalArgumentException(String.format("Unable to parse the %s: %s", field.getName(), value), e);
			}
		}
		final Class<?> type = field.getType();
		return valueConverter.convert(value, type.isArray() ? type.getComponentType() : type);
	}

	/**
	 * 指定されたオブジェクトを項目値へ変換して返します。
	 * この実装は、指定された項目に対応する項目値へ書式化するオブジェクトが存在する場合は、{@link Format#format(Object)} で得られた文字列を返します。
	 * それ以外の場合は、項目値コンバータを使用して得られた文字列を返します。
	 * 
	 * @param column 項目名 (または項目位置)
	 * @param obj オブジェクト
	 * @return 文字列の項目値
	 */
	public String objectToString(final Object column, final Object obj) {
		final Format format = valueFormatterMapping.get(column);
		if (format != null) {
			if (obj == null) {
				return null;
			}
			return format.format(obj);
		}
		return valueConverter.convert(obj);
	}

}