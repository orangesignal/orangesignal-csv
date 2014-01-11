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
import java.io.Flushable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.filters.CsvNamedValueFilter;

/**
 * 項目名と項目値のマップで区切り文字形式データアクセスを行う区切り文字形式出力ストリームを提供します。
 * 
 * @author 杉澤 浩二
 * @since 1.4.0
 */
public class CsvColumnNameMapWriter implements Closeable, Flushable {

	/**
	 * 区切り文字形式出力ストリームを保持します。
	 */
	private CsvWriter writer;

	/**
	 * 項目名のリストを保持します。
	 */
	private List<String> columnNames;

	/**
	 * 項目名の数を保存します。
	 */
	private int columnCount = -1;

	/**
	 * 区切り文字形式データフィルタを保持します。
	 */
	private CsvNamedValueFilter filter;

	// ------------------------------------------------------------------------
	// コンストラクタ

	/**
	 * 指定された区切り文字形式出力ストリームを使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @throws IllegalArgumentException {@code writer} が {@code null} の場合。
	 */
	public CsvColumnNameMapWriter(final CsvWriter writer) {
		this(writer, null);
	}

	/**
	 * 指定された区切り文字形式出力ストリームと項目名のリストを使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param columnNames 項目名のリスト
	 * @throws IllegalArgumentException {@code writer} が {@code null} の場合。
	 */
	public CsvColumnNameMapWriter(final CsvWriter writer, final List<String> columnNames) {
		if (writer == null) {
			throw new IllegalArgumentException("CsvWriter must not be null");
		}
		this.writer = writer;

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
		if (writer == null) {
			throw new IOException("CsvWriter closed");
		}
	}

	private void ensureHeader(final Map<String, String> map) throws IOException {
		if (columnNames == null && map != null) {
			columnNames = new ArrayList<String>(map.keySet());
		}
		if (columnNames == null) {
			// ヘッダがない場合は例外をスローします。
			throw new IOException("No header is available");
		}
		if (columnCount == -1) {
			// ヘッダ部を処理します。
			writer.writeValues(columnNames);
			columnCount = columnNames.size();
		}
	}

	// ------------------------------------------------------------------------
	// オーバーライド メソッド

	@Override
	public void flush() throws IOException {
		synchronized (this) {
			ensureOpen();
			writer.flush();
		}
	}

	@Override
	public void close() throws IOException {
		synchronized (this) {
			ensureOpen();
			writer.close();
			writer = null;
			columnNames = null;
			columnCount = -1;
		}
	}

	// ------------------------------------------------------------------------
	// パブリック メソッド

	/**
	 * 可能であれば項目名を書き込みます。項目名が既に書き込まれている場合、このメソッドは何も行いません。
	 * 
	 * @param map 項目名と項目値のマップ
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void writeHeader(final Map<String, String> map) throws IOException {
		synchronized (this) {
			ensureOpen();
			ensureHeader(map);
		}
	}

	/**
	 * 指定された項目名と項目値のマップを書き込みます。項目名の書き込みが必要な場合は自動的に書き込みが行われます。
	 * 
	 * @param map 項目名と項目値のマップ
	 * @return 区切り文字形式データフィルタによって書き込みが行われなかった場合は {@code false} それ以外の場合は {@code true}
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public boolean write(final Map<String, String> map) throws IOException {
		synchronized (this) {
			ensureOpen();
			ensureHeader(map);

			// 要素が null の場合は null 出力します。
			if (map == null) {
				writer.writeValues(null);
				return true;
			}

			final List<String> values = toValues(map);
			if (filter != null && !filter.accept(columnNames, values)) {
				return false;
			}
			writer.writeValues(values);
			return true;
		}
	}

	private List<String> toValues(final Map<String, String> map) {
		final String[] values = new String[columnCount];
		for (int i = 0; i < columnCount; i++) {
			values[i] = map.get(columnNames.get(i));
		}
		return Arrays.asList(values);
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