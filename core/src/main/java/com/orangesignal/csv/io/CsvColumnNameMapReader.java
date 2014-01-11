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

package com.orangesignal.csv.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.filters.CsvNamedValueFilter;

/**
 * 項目名と項目値のマップで区切り文字形式データアクセスを行う区切り文字形式入力ストリームを提供します。
 * 
 * @author 杉澤 浩二
 * @since 1.4.0
 */
public class CsvColumnNameMapReader implements Closeable {

	/**
	 * 区切り文字形式入力ストリームを保持します。
	 */
	private CsvReader reader;

	/**
	 * 項目名のリストを保持します。
	 */
	private List<String> columnNames;

	/**
	 * 項目名の数を一時的に保存します。
	 */
	private int columnCount = -1;

	/**
	 * 項目名のマップを一時的に保存します。
	 */
	private Map<String, String> base;

	/**
	 * 区切り文字形式データフィルタを保持します。
	 */
	private CsvNamedValueFilter filter;

	// ------------------------------------------------------------------------
	// コンストラクタ

	/**
	 * 指定された区切り文字形式入力ストリームを使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @throws IllegalArgumentException {@code reader} が {@code null} の場合。
	 */
	public CsvColumnNameMapReader(final CsvReader reader) {
		this(reader, null);
	}

	/**
	 * 指定された区切り文字形式入力ストリームと項目名のリストを使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @param columnNames 項目名のリスト
	 * @throws IllegalArgumentException {@code reader} が {@code null} の場合。
	 */
	public CsvColumnNameMapReader(final CsvReader reader, final List<String> columnNames) {
		if (reader == null) {
			throw new IllegalArgumentException("CsvReader must not be null");
		}
		this.reader = reader;

		if (columnNames != null) {
			this.columnNames = Collections.unmodifiableList(columnNames);
		}
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
			}
			if (columnCount == -1) {
				// ヘッダ部を処理します。
				columnCount = columnNames.size();
				base = new LinkedHashMap<String, String>(columnCount);
				for (final String columnName : columnNames) {
					base.put(columnName, null);
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
			columnCount = -1;
			base = null;
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
	 * 論理行を読込み項目名と項目値のマップとして返します。
	 *
	 * @return 項目名と項目値のマップ。ストリームの終わりに達した場合は {@code null}
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public Map<String, String> read() throws IOException {
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
	 * 指定された CSV トークンの値をリストを項目名と項目値のマップへ変換して返します。
	 * 
	 * @param values CSV トークンの値をリスト
	 * @return 変換された項目名と項目値のマップ
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public Map<String, String> toMap(final List<String> values) throws IOException {
		synchronized (this) {
			ensureOpen();
			ensureHeader();
			return convert(values);
		}
	}

	private List<String> nextValues() throws IOException {
		List<String> values;
		while ((values = reader.readValues()) != null) {
			if (filter != null && !filter.accept(columnNames, values)) {
				continue;
			}
			return values;
		}
		return null;
	}

	private Map<String, String> convert(final List<String> values) {
		final Map<String, String> map = new LinkedHashMap<String, String>(base);
		final int len = Math.min(columnCount, values.size());
		for (int pos = 0; pos < len; pos++) {
			map.put(columnNames.get(pos), values.get(pos));
		}
		return map;
	}

	// ------------------------------------------------------------------------
	// セッター / ゲッター

	/**
	 * 区切り文字形式データフィルタを返します。
	 * 
	 * @return 区切り文字形式データフィルタ。または {@code null}
	 */
	public CsvNamedValueFilter getFilter() {
		return filter;
	}

	/**
	 * 区切り文字形式データフィルタを設定します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 */
	public void setFilter(final CsvNamedValueFilter filter) {
		synchronized (this) {
			this.filter = filter;
		}
	}

}