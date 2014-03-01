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

import java.lang.reflect.Field;
import java.text.Format;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.orangesignal.csv.filters.CsvNamedValueFilter;

/**
 * 区切り文字形式データの項目名を基準として Java プログラム要素の操作を簡素化するヘルパークラスを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public class CsvColumnNameMappingBeanTemplate<T> extends AbstractCsvBeanTemplate<T, CsvColumnNameMappingBeanTemplate<T>> implements CsvColumnNameMappingBeanOperation<CsvColumnNameMappingBeanTemplate<T>> {

	/**
	 * 項目名と Java プログラム要素のフィールド名のマップを保持します。
	 */
	private Map<String, String> columnMapping = new LinkedHashMap<String, String>();

	/**
	 * 区切り文字形式データフィルタを保持します。
	 */
	private CsvNamedValueFilter filter;

	// ------------------------------------------------------------------------
	// 利便性のための静的メソッド

	/**
	 * 新しい {@link CsvColumnNameMappingBeanTemplate} のインスタンスを返します。
	 * 
	 * @param type Java プログラム要素の型
	 * @return 新しい {@link CsvColumnNameMappingBeanTemplate} のインスタンス
	 * @throws IllegalArgumentException {@code type} が {@code null} の場合。
	 */
	public static <T> CsvColumnNameMappingBeanTemplate<T> newInstance(final Class<T> type) {
		return new CsvColumnNameMappingBeanTemplate<T>(type);
	}

	// -----------------------------------------------------------------------
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param type Java プログラム要素の型
	 * @throws IllegalArgumentException {@code type} が {@code null} の場合。
	 */
	public CsvColumnNameMappingBeanTemplate(final Class<T> type) {
		super(type);
	}

	// -----------------------------------------------------------------------
	// オーバーライド メソッド

	@Override
	public CsvColumnNameMappingBeanTemplate<T> column(final String column, final String field) {
		return column(column, field, null);
	}

	@Override
	public CsvColumnNameMappingBeanTemplate<T> column(final String column, final String field, final Format format) {
		columnMapping.put(column, field);
		if (format != null) {
			setValueParser(field, format);
			setValueFormatter(column, format);
		}
		return this;
	}

	@Override
	public void setColumnMapping(final Map<String, String> columnMapping) {
		if (columnMapping == null) {
			throw new IllegalArgumentException("Column mapping must not be null");
		}
		this.columnMapping = new LinkedHashMap<String, String>(columnMapping);
	}

	@Override
	public CsvColumnNameMappingBeanTemplate<T> columnMapping(final Map<String, String> columnMapping) {
		setColumnMapping(columnMapping);
		return this;
	}

	@Override
	public CsvColumnNameMappingBeanTemplate<T> filter(final CsvNamedValueFilter filter) {
		this.filter = filter;
		return this;
	}

	// ------------------------------------------------------------------------
	// パブリック メソッド

	/**
	 * 項目名とフィールド名のマップが指定されていない場合、フィールド名からマップを作成して準備します。
	 */
	public void setupColumnMappingIfNeed() {
		if (columnMapping.size() == 0) {
			for (final Field f : getType().getDeclaredFields()) {
				final String name = f.getName();
				column(name, name);
			}
		}
	}

	/**
	 * 指定された区切り文字形式データの値リストが含まれる必要があるかどうかを判定します。
	 * 
	 * @param columnNames 区切り文字形式データの項目名リスト
	 * @param values 区切り文字形式データの項目値のリスト
	 * @return {@code values} が含まれる必要がある場合は {@code true}
	 */
	public boolean isAccept(final List<String> columnNames, final List<String> values) {
		return filter != null && !filter.accept(columnNames, values);
	}

	// 入力

	public Map<String, Object[]> createFieldAndColumnsMap() {
		return super.createFieldAndColumnsMap(columnMapping);
	}

	// 出力

	/**
	 * 項目名のリストを作成して返します。
	 * 
	 * @return 項目名のリスト
	 */
	public List<String> createColumnNames() {
		final List<String> columnNames = new ArrayList<String>();
		for (final Map.Entry<String, String> entry : columnMapping.entrySet()) {
			columnNames.add(entry.getKey());
		}
		return columnNames;
	}

	public String getFieldName(final String columnName) {
		return columnMapping.get(columnName);
	}

}