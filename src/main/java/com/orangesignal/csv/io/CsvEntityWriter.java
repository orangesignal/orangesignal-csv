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

import static com.orangesignal.csv.bean.CsvEntityTemplate.defaultIfEmpty;
import static com.orangesignal.csv.bean.FieldUtils.getFieldValue;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.annotation.CsvColumn;
import com.orangesignal.csv.annotation.CsvColumns;
import com.orangesignal.csv.annotation.CsvEntity;
import com.orangesignal.csv.bean.CsvEntityTemplate;

/**
 * 区切り文字形式データ注釈要素 {@link CsvEntity} で注釈付けされた Java プログラム要素のリストで区切り文字形式データアクセスを行う区切り文字形式出力ストリームを提供します。
 * 
 * @author 杉澤 浩二
 * @since 1.4.0
 */
public class CsvEntityWriter<T> implements Closeable, Flushable {

	/**
	 * 区切り文字形式出力ストリームを保持します。
	 */
	private CsvWriter writer;

	/**
	 * Java プログラム要素操作の簡素化ヘルパーを保持します。
	 */
	private final CsvEntityTemplate<T> template;

	/**
	 * 項目名のリストを保持します。
	 */
	private List<String> columnNames;

	private int columnCount = -1;

	// ------------------------------------------------------------------------
	// 利便性のための静的メソッド

	/**
	 * 新しい {@link CsvEntityWriter} のインスタンスを返します。
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param entityClass 区切り文字形式データ注釈要素 {@link CsvEntity} で注釈付けされた Java プログラム要素の型
	 * @return 新しい {@link CsvEntityWriter} のインスタンス
	 * @throws IllegalArgumentException {@code writer} または {@code type} が {@code null} の場合。
	 */
	public static <T> CsvEntityWriter<T> newInstance(final CsvWriter writer, final Class<T> entityClass) {
		return new CsvEntityWriter<T>(writer, entityClass);
	}

	/**
	 * 新しい {@link CsvEntityWriter} のインスタンスを返します。
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @return 新しい {@link CsvEntityWriter} のインスタンス
	 * @throws IllegalArgumentException {@code writer} または {@code template} が {@code null} の場合。
	 */
	public static <T> CsvEntityWriter<T> newInstance(final CsvWriter writer, final CsvEntityTemplate<T> template) {
		return new CsvEntityWriter<T>(writer, template);
	}

	// ------------------------------------------------------------------------
	// コンストラクタ

	/**
	 * 指定された区切り文字形式出力ストリームと Java プログラム要素の型を使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param entityClass 区切り文字形式データ注釈要素 {@link CsvEntity} で注釈付けされた Java プログラム要素の型
	 * @throws IllegalArgumentException {@code writer} または {@code type} が {@code null} の場合。
	 */
	public CsvEntityWriter(final CsvWriter writer, final Class<T> entityClass) {
		this(writer, new CsvEntityTemplate<T>(entityClass));
	}

	/**
	 * 指定された区切り文字形式出力ストリームと Java プログラム要素操作の簡素化ヘルパーを使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param writer 区切り文字形式出力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @throws IllegalArgumentException {@code writer} または {@code template} が {@code null} の場合。
	 */
	public CsvEntityWriter(final CsvWriter writer, final CsvEntityTemplate<T> template) {
		if (writer == null) {
			throw new IllegalArgumentException("CsvWriter must not be null");
		}
		if (template == null) {
			throw new IllegalArgumentException("CsvEntityTemplate must not be null");
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
				final List<String> names = template.createColumnNames();
				// ヘッダ行が有効な場合は項目名の一覧を出力します。
				if (template.getType().getAnnotation(CsvEntity.class).header()) {
					writer.writeValues(names);
				}

				template.prepare(names, template.getType().getDeclaredFields());
				columnNames = Collections.unmodifiableList(names);
				columnCount = names.size();
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
	 * @param entity 書き込む Java プログラム要素。または {@code null}
	 * @return データの出力を行った場合は {@code true} それ以外の場合 (フィルタにより書き込みがスキップされた場合) は {@code false}
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public boolean write(final T entity) throws IOException {
		synchronized (this) {
			ensureOpen();
			ensureHeader();

			// 要素が null の場合は null 出力します。
			if (entity == null || entity.getClass().getAnnotation(CsvEntity.class) == null) {
				writer.writeValues(null);
				return true;
			}

			final List<String> values = toValues(entity);
			if (template.isAccept(columnNames, values)) {
				return false;
			}
			writer.writeValues(values);
			return true;
		}
	}

	private List<String> toValues(final T entity) throws IOException {
		final String[] values = new String[columnCount];
		for (final Field f : entity.getClass().getDeclaredFields()) {
			final CsvColumns columns = f.getAnnotation(CsvColumns.class);
			if (columns != null) {
				for (final CsvColumn column : columns.value()) {
					int pos = column.position();
					if (pos < 0) {
						pos = columnNames.indexOf(defaultIfEmpty(column.name(), f.getName()));
					}
					if (pos == -1) {
						throw new IOException(String.format("Invalid CsvColumn field %s", f.getName()));
					}
					values[pos] = template.objectToString(pos, getFieldValue(entity, f));
				}
			}
			final CsvColumn column = f.getAnnotation(CsvColumn.class);
			if (column != null) {
				int pos = column.position();
				if (pos < 0) {
					pos = columnNames.indexOf(defaultIfEmpty(column.name(), f.getName()));
				}
				if (pos == -1) {
					throw new IOException(String.format("Invalid CsvColumn field %s", f.getName()));
				}
				values[pos] = template.objectToString(pos, getFieldValue(entity, f));
			}
		}
		return Arrays.asList(values);
	}

}