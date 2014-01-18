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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.filters.CsvValueFilter;
import com.orangesignal.csv.io.CsvColumnPositionMapReader;
import com.orangesignal.csv.io.CsvColumnPositionMapWriter;

/**
 * 項目位置と項目値のマップのリストで区切り文字形式データアクセスを行うハンドラを提供します。
 *
 * @author 杉澤 浩二
 * @since 1.1
 */
public class ColumnPositionMapListHandler extends AbstractCsvListHandler<Map<Integer, String>, ColumnPositionMapListHandler> {

	/**
	 * 区切り文字形式データフィルタを保持します。
	 */
	private CsvValueFilter valueFilter;

	/**
	 * デフォルトコンストラクタです。
	 */
	public ColumnPositionMapListHandler() {}

	/**
	 * 区切り文字形式データフィルタを設定します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 * @return このオブジェクトへの参照
	 * @since 1.2.3
	 */
	public ColumnPositionMapListHandler filter(final CsvValueFilter filter) {
		this.valueFilter = filter;
		return this;
	}

	@Override
	public List<Map<Integer, String>> load(final CsvReader reader, final boolean ignoreScalar) throws IOException {
		@SuppressWarnings("resource")
		final CsvColumnPositionMapReader r = new CsvColumnPositionMapReader(reader);
		r.setFilter(valueFilter);

		// データ部を処理します。
		final List<Map<Integer, String>> results = new ArrayList<Map<Integer, String>>();
		int offset = 0;

		List<String> values;
		while ((values = r.readValues()) != null && (ignoreScalar || limit <= 0 || results.size() < limit)) {
			if (!ignoreScalar && offset < this.offset) {
				offset++;
				continue;
			}
			results.add(r.toMap(values));
		}
		return results;
	}

	@Override
	public void save(final List<Map<Integer, String>> list, final CsvWriter writer) throws IOException {
		@SuppressWarnings("resource")
		final CsvColumnPositionMapWriter w = new CsvColumnPositionMapWriter(writer);
		w.setFilter(valueFilter);

		// データ部を処理します。
		for (final Map<Integer, String> map : list) {
			w.write(map);
		}
	}

}