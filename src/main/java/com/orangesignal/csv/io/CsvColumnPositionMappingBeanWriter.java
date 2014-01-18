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
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.bean.CsvColumnPositionMappingBeanTemplate;
import com.orangesignal.csv.bean.FieldUtils;

/**
 * 区切り文字形式データの項目位置を基準として Java プログラム要素と区切り文字形式データアクセスを行う区切り文字形式出力ストリームを提供します。
 * 
 * @author 杉澤 浩二
 * @since 1.4.0
 */
public class CsvColumnPositionMappingBeanWriter<T> implements Closeable, Flushable {

	/**
	 * 区切り文字形式出力ストリームを保持します。
	 */
	private CsvWriter writer;

	/**
	 * Java プログラム要素操作の簡素化ヘルパーを保持します。
	 */
	private final CsvColumnPositionMappingBeanTemplate<T> template;

	/**
	 * 項目名のリストを保持します。
	 */
	private List<String> columnNames;

	/**
	 * 項目名の数を保存します。
	 */
	private int columnCount = -1;

	/**
	 * ヘッダを使用するかどうかを保持します。
	 */
	private boolean useHeader = true;

	// ------------------------------------------------------------------------
	// 利便性のための静的メソッド

	/**
	 * 新しい {@link CsvColumnPositionMappingBeanWriter} のインスタンスを返します。
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param type Java プログラム要素の型
	 * @return 新しい {@link CsvColumnPositionMappingBeanWriter} のインスタンス
	 * @throws IllegalArgumentException {@code writer} または {@code type} が {@code null} の場合。
	 */
	public static <T> CsvColumnPositionMappingBeanWriter<T> newInstance(final CsvWriter writer, final Class<T> type) {
		return new CsvColumnPositionMappingBeanWriter<T>(writer, type);
	}

	/**
	 * 新しい {@link CsvColumnPositionMappingBeanWriter} のインスタンスを返します。
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @return 新しい {@link CsvColumnPositionMappingBeanWriter} のインスタンス
	 * @throws IllegalArgumentException {@code writer} または {@code template} が {@code null} の場合。
	 */
	public static <T> CsvColumnPositionMappingBeanWriter<T> newInstance(final CsvWriter writer, final CsvColumnPositionMappingBeanTemplate<T> template) {
		return new CsvColumnPositionMappingBeanWriter<T>(writer, template);
	}

	// ------------------------------------------------------------------------
	// コンストラクタ

	/**
	 * 指定された区切り文字形式出力ストリームと Java プログラム要素の型を使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param type Java プログラム要素の型
	 * @throws IllegalArgumentException {@code writer} または {@code type} が {@code null} の場合。
	 */
	public CsvColumnPositionMappingBeanWriter(final CsvWriter writer, final Class<T> type) {
		this(writer, new CsvColumnPositionMappingBeanTemplate<T>(type));
	}

	/**
	 * 指定された区切り文字形式出力ストリームと Java プログラム要素操作の簡素化ヘルパーを使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @throws IllegalArgumentException {@code writer} または {@code template} が {@code null} の場合。
	 */
	public CsvColumnPositionMappingBeanWriter(final CsvWriter writer, final CsvColumnPositionMappingBeanTemplate<T> template) {
		if (writer == null) {
			throw new IllegalArgumentException("CsvWriter must not be null");
		}
		if (template == null) {
			throw new IllegalArgumentException("CsvColumnPositionMappingBeanTemplate must not be null");
		}
		this.writer = writer;
		this.template = template;
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

	private void ensureHeader() throws IOException {
		synchronized (this) {
			if (columnNames == null) {
				// 項目位置とフィールド名のマップが指定されていない場合は、フィールドからマップを作成します。
				if (template.getMaxColumnPosition() == -1) {
					for (final Field f : template.getType().getDeclaredFields()) {
						template.column(f.getName());
					}
				}
				columnCount = template.getMaxColumnPosition() + 1;

				// ヘッダ部を処理します。
				final List<String> names = Collections.unmodifiableList(template.createColumnNames());
				if (useHeader) {
					writer.writeValues(names);
				}
				columnNames = names;
			}
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
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void writeHeader() throws IOException {
		synchronized (this) {
			ensureOpen();
			ensureHeader();
		}
	}

	/**
	 * 指定された Java プログラム要素を区切り文字形式で書き込みます。
	 * {@code null} が指定された場合は空行が書き込まれます。
	 * 
	 * @param bean 書き込む Java プログラム要素。または {@code null}
	 * @return データの出力を行った場合は {@code true} それ以外の場合 (フィルタにより書き込みがスキップされた場合) は {@code false}
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public boolean write(final T bean) throws IOException {
		synchronized (this) {
			ensureOpen();
			ensureHeader();

			// 要素が null の場合は null 出力します。
			if (bean == null) {
				writer.writeValues(null);
				return true;
			}

			final List<String> values = toValues(bean);
			if (template.isAccept(values)) {
				return false;
			}
			writer.writeValues(values);
			return true;
		}
	}

	private List<String> toValues(final T bean) throws IOException {
		final Class<?> type = bean.getClass();
		final String[] values = new String[columnCount];
		for (final Map.Entry<Integer, String> e : template.columnMappingEntrySet()) {
			final int pos = e.getKey();
			if (pos == -1) {
				continue;
			}
			final Field f = FieldUtils.getField(type, e.getValue());
			values[pos] = template.objectToString(Integer.valueOf(pos), FieldUtils.getFieldValue(bean, f));
		}
		return Arrays.asList(values);
	}

}