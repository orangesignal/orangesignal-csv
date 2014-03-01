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

import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.orangesignal.csv.filters.CsvValueFilter;

/**
 * 区切り文字形式データの項目位置を基準として Java プログラム要素の操作を簡素化するヘルパークラスを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public class CsvColumnPositionMappingBeanTemplate<T> extends AbstractCsvBeanTemplate<T, CsvColumnPositionMappingBeanTemplate<T>> implements CsvColumnPositionMappingBeanOperation<CsvColumnPositionMappingBeanTemplate<T>> {

	/**
	 * 項目位置と Java プログラム要素のフィールド名のマップを保持します。
	 */
	private SortedMap<Integer, String> columnMapping = new TreeMap<Integer, String>();

	/**
	 * 区切り文字形式データフィルタを保持します。
	 */
	private CsvValueFilter filter;

	// ------------------------------------------------------------------------
	// 利便性のための静的メソッド

	/**
	 * 新しい {@link CsvColumnPositionMappingBeanTemplate} のインスタンスを返します。
	 * 
	 * @param type Java プログラム要素の型
	 * @return 新しい {@link CsvColumnPositionMappingBeanTemplate} のインスタンス
	 * @throws IllegalArgumentException {@code type} が {@code null} の場合。
	 */
	public static <T> CsvColumnPositionMappingBeanTemplate<T> newInstance(final Class<T> type) {
		return new CsvColumnPositionMappingBeanTemplate<T>(type);
	}

	// -----------------------------------------------------------------------
	// コンストラクタ

	/**
	 * コンストラクタです。
	 * 
	 * @param type Java プログラム要素の型
	 * @throws IllegalArgumentException {@code type} が {@code null} の場合。
	 */
	public CsvColumnPositionMappingBeanTemplate(final Class<T> type) {
		super(type);
	}

	// -----------------------------------------------------------------------
	// オーバーライド メソッド

	@Override
	public CsvColumnPositionMappingBeanTemplate<T> column(final String field) {
		return column(field, null);
	}

	@Override
	public CsvColumnPositionMappingBeanTemplate<T> column(final String field, final Format format) {
		return column(getMaxColumnPosition() + 1, field, format);
	}

	@Override
	public CsvColumnPositionMappingBeanTemplate<T> column(final int position, final String field) {
		return column(position, field, null);
	}

	@Override
	public CsvColumnPositionMappingBeanTemplate<T> column(final int position, final String field, final Format format) {
		columnMapping.put(position, field);
		if (format != null) {
			setValueParser(field, format);
			setValueFormatter(Integer.valueOf(position), format);
		}
		return this;
	}

	@Override
	public void setColumnMapping(final Map<Integer, String> columnMapping) {
		if (columnMapping == null) {
			throw new IllegalArgumentException("Column mapping must not be null");
		}
		this.columnMapping = new TreeMap<Integer, String>(columnMapping);
	}

	@Override
	public CsvColumnPositionMappingBeanTemplate<T> columnMapping(final Map<Integer, String> columnMapping) {
		setColumnMapping(columnMapping);
		return this;
	}

	@Override
	public CsvColumnPositionMappingBeanTemplate<T> filter(final CsvValueFilter filter) {
		this.filter = filter;
		return this;
	}

	// ------------------------------------------------------------------------
	// パブリック メソッド

	/**
	 * 最も大きい項目位置を返します。不明な場合は {@code -1} を返します。
	 * 
	 * @return 最も大きい項目位置。または {@code -1}
	 */
	public int getMaxColumnPosition() {
		if (columnMapping.size() > 0) {
			return columnMapping.lastKey();
		}
		return -1;
	}

	/**
	 * 項目名のリストを作成して返します。
	 * 
	 * @return 項目名のリスト
	 */
	public List<String> createColumnNames() {
		final List<String> columnNames = new ArrayList<String>(columnMapping.size());
		for (final Map.Entry<Integer, String> e : columnMapping.entrySet()) {
			columnNames.add(e.getValue());
		}
		return columnNames;
	}

	/**
	 * 指定された区切り文字形式データの値リストが含まれる必要があるかどうかを判定します。
	 * 
	 * @param values 区切り文字形式データの項目値のリスト
	 * @return {@code values} が含まれる必要がある場合は {@code true}
	 */
	public boolean isAccept(final List<String> values) {
		return filter != null && !filter.accept(values);
	}

	// 入力

	public Map<String, Object[]> createFieldAndColumnsMap() {
		return super.createFieldAndColumnsMap(columnMapping);
	}

	// 出力

	public Set<Map.Entry<Integer, String>> columnMappingEntrySet() {
		return columnMapping.entrySet();
	}

}