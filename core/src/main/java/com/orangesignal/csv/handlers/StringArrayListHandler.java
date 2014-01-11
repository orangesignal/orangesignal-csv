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
import java.util.Arrays;
import java.util.List;

import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.filters.CsvValueFilter;

/**
 * 文字列配列のリストで区切り文字形式データアクセスを行うハンドラを提供します。
 *
 * @author 杉澤 浩二
 */
public class StringArrayListHandler extends AbstractCsvListHandler<String[], StringArrayListHandler> {

	/**
	 * 区切り文字形式データフィルタを保持します。
	 */
	private CsvValueFilter valueFilter;

	/**
	 * デフォルトコンストラクタです。
	 */
	public StringArrayListHandler() {}

	/**
	 * 区切り文字形式データフィルタを設定します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 * @return このオブジェクトへの参照
	 * @since 1.2.3
	 */
	public StringArrayListHandler filter(final CsvValueFilter filter) {
		this.valueFilter = filter;
		return this;
	}

	@Override
	public List<String[]> load(final CsvReader reader, final boolean ignoreScalar) throws IOException {
		final List<String[]> results = new ArrayList<String[]>();
		int offset = 0;
		List<String> values;
		while ((values = reader.readValues()) != null && (ignoreScalar || limit <= 0 || results.size() < limit)) {
			if (valueFilter != null && !valueFilter.accept(values)) {
				continue;
			}
			if (!ignoreScalar && offset < this.offset) {
				offset++;
				continue;
			}
			results.add(values.toArray(new String[0]));
		}
		return results;
	}

	@Override
	public void save(final List<String[]> list, final CsvWriter writer) throws IOException {
		for (final String[] values : list) {
			final List<String> _values = Arrays.asList(values);
			if (valueFilter != null && !valueFilter.accept(_values)) {
				continue;
			}
			writer.writeValues(_values);
		}
	}

}