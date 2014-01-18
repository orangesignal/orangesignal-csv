/*
 * Copyright (c) 2013 OrangeSignal.com All rights reserved.
 * 
 * これは Apache ライセンス Version 2.0 (以下、このライセンスと記述) に
 * 従っています。このライセンスに準拠する場合以外、このファイルを使用
 * してはなりません。このライセンスのコピーは以下から入手できます。
 * 
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 * 
 * 適用可能な法律がある、あるいは文書によって明記されている場合を除き、
 * このライセンスの下で配布されているソフトウェアは、明示的であるか暗黙の
 * うちであるかを問わず、「保証やあらゆる種類の条件を含んでおらず」、
 * 「あるがまま」の状態で提供されるものとします。
 * このライセンスが適用される特定の許諾と制限については、このライセンス
 * を参照してください。
 */

package com.orangesignal.csv.handlers;

import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.bean.CsvColumnPositionMappingBeanTemplate;
import com.orangesignal.csv.filters.CsvValueFilter;
import com.orangesignal.csv.io.CsvColumnPositionMappingBeanReader;
import com.orangesignal.csv.io.CsvColumnPositionMappingBeanWriter;

/**
 * 区切り文字形式データの項目位置を基準として Java プログラム要素のリストと区切り文字形式データアクセスを行うハンドラを提供します。
 * 
 * @author 杉澤 浩二
 * @see {@link com.orangesignal.csv.manager.CsvColumnPositionMappingBeanManager}
 */
public class ColumnPositionMappingBeanListHandler<T> extends AbstractBeanListHandler<T, CsvColumnPositionMappingBeanTemplate<T>, ColumnPositionMappingBeanListHandler<T>> {

	// ------------------------------------------------------------------------

	/**
	 * コンストラクタです。
	 * 
	 * @param type Java プログラム要素の型
	 * @throws IllegalArgumentException {@code type} が {@code null} の場合
	 */
	public ColumnPositionMappingBeanListHandler(final Class<T> type) {
		super(CsvColumnPositionMappingBeanTemplate.newInstance(type));
	}

	// ------------------------------------------------------------------------

	/**
	 * 指定された Java プログラム要素のフィールド名を現在の最後の項目位置としてマップへ追加します。
	 * 
	 * @param field Java プログラム要素のフィールド名
	 * @return このオブジェクトへの参照
	 */
	public ColumnPositionMappingBeanListHandler<T> addColumn(final String field) {
		return addColumn(field, null);
	}

	/**
	 * 指定された Java プログラム要素のフィールド名を現在の最後の項目位置としてマップへ追加します。
	 * 
	 * @param field Java プログラム要素のフィールド名
	 * @param format フィールドを処理するフォーマットオブジェクト (オプション)
	 * @return このオブジェクトへの参照
	 * @since 1.2
	 */
	public ColumnPositionMappingBeanListHandler<T> addColumn(final String field, final Format format) {
		template.column(field, format);
		return this;
	}

	/**
	 * 指定された項目位置と Java プログラム要素のフィールド名をマップへ追加します。
	 * 
	 * @param position 項目位置
	 * @param field Java プログラム要素のフィールド名
	 * @return このオブジェクトへの参照
	 */
	public ColumnPositionMappingBeanListHandler<T> addColumn(final int position, final String field) {
		return addColumn(position, field, null);
	}

	/**
	 * 指定された項目位置と Java プログラム要素のフィールド名をマップへ追加します。
	 * 
	 * @param position 項目名
	 * @param field Java プログラム要素のフィールド名
	 * @param format フィールドを処理するフォーマットオブジェクト (オプション)
	 * @return このオブジェクトへの参照
	 * @since 1.2
	 */
	public ColumnPositionMappingBeanListHandler<T> addColumn(final int position, final String field, final Format format) {
		template.column(position, field, format);
		return this;
	}

	/**
	 * 項目位置と Java プログラム要素のフィールド名のマップを設定します。
	 * 
	 * @param columnMapping 項目位置と Java プログラム要素のフィールド名のマップ
	 * @throws IllegalArgumentException {@code columnMapping} が {@code null} の場合
	 * @since 1.2.4
	 */
	public void setColumnMapping(final Map<Integer, String> columnMapping) {
		template.setColumnMapping(columnMapping);
	}

	/**
	 * 項目位置と Java プログラム要素のフィールド名のマップを設定します。
	 * 
	 * @param columnMapping 項目位置と Java プログラム要素のフィールド名のマップ
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException {@code columnMapping} が {@code null} の場合
	 */
	public ColumnPositionMappingBeanListHandler<T> columnMapping(final Map<Integer, String> columnMapping) {
		setColumnMapping(columnMapping);
		return this;
	}

	/**
	 * 区切り文字形式データフィルタを設定します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 * @return このオブジェクトへの参照
	 * @since 1.2.3
	 */
	public ColumnPositionMappingBeanListHandler<T> filter(final CsvValueFilter filter) {
		template.filter(filter);
		return this;
	}

	// ------------------------------------------------------------------------

	@Override
	public List<T> load(final CsvReader reader, final boolean ignoreScalar) throws IOException {
		@SuppressWarnings("resource")
		final CsvColumnPositionMappingBeanReader<T> r = new CsvColumnPositionMappingBeanReader<T>(reader, template);

		// データ部を処理します。
		final List<T> results = new ArrayList<T>();
		final boolean order = ignoreScalar || (orders != null && !orders.isEmpty());
		int offset = 0;

		List<String> values;
		while ((values = r.readValues()) != null && (order || limit <= 0 || results.size() < limit)) {
			if (beanFilter == null && !order && offset < this.offset) {
				offset++;
				continue;
			}
			final T bean = r.toBean(values);
			if (beanFilter != null) {
				if (!beanFilter.accept(bean)) {
					continue;
				}
				if (!order && offset < this.offset) {
					offset++;
					continue;
				}
			}
			results.add(bean);
		}

		if (ignoreScalar || !order) {
			return results;
		}
		return processScalar(results);
	}

	@Override
	public void save(final List<T> list, final CsvWriter writer) throws IOException {
		@SuppressWarnings("resource")
		final CsvColumnPositionMappingBeanWriter<T> w = new CsvColumnPositionMappingBeanWriter<T>(writer, template);

		// データ部を処理します。
		for (final T bean : list) {
			// 要素が null の場合は null 出力します。
			if (bean == null) {
				w.write(null);
				continue;
			} else if (beanFilter != null && !beanFilter.accept(bean)) {
				continue;
			}

			w.write(bean);
		}
	}

}