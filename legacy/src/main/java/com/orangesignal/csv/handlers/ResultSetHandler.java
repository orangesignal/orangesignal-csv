/*
 * Copyright (c) 2009-2013 OrangeSignal.com All rights reserved.
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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.orangesignal.csv.CsvHandler;
import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvResultSet;
import com.orangesignal.csv.CsvWriter;

/**
 * データベースの結果セットで区切り文字形式データアクセスを行うハンドラを提供します。
 *
 * @author 杉澤 浩二
 */
public class ResultSetHandler implements CsvHandler<ResultSet> {

	/**
	 * デフォルトコンストラクタです。
	 */
	public ResultSetHandler() {}

	@Override
	public ResultSet load(final CsvReader reader) throws IOException {
		return new CsvResultSet(reader);
	}

	@Override
	public void save(final ResultSet rs, final CsvWriter writer) throws IOException {
		try {
			final int count = writeHeader(rs.getMetaData(), writer);
			while (rs.next()) {
				final List<String> list = new ArrayList<String>(count);
				for (int i = 1; i <= count; i++) {
					final Object o = rs.getObject(i);
					if (rs.wasNull()) {
						list.add(null);
					} else {
						list.add(o.toString());
					}
				}
				writer.writeValues(list);
			}
		} catch (SQLException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	private static int writeHeader(final ResultSetMetaData meta, final CsvWriter writer) throws IOException {
		try {
			final int count = meta.getColumnCount();
			final List<String> list = new ArrayList<String>(count);
			for (int i = 1; i <= count; i++) {
				list.add(meta.getColumnLabel(i));
			}
			writer.writeValues(list);
			return count;
		} catch (SQLException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

}
