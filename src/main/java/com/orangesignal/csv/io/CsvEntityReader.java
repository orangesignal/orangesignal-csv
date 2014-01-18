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

import static com.orangesignal.csv.bean.CsvEntityTemplate.getPosition;
import static com.orangesignal.csv.bean.FieldUtils.setFieldValue;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.annotation.CsvColumn;
import com.orangesignal.csv.annotation.CsvColumns;
import com.orangesignal.csv.annotation.CsvEntity;
import com.orangesignal.csv.bean.CsvEntityTemplate;

/**
 * 区切り文字形式データ注釈要素 {@link CsvEntity} で注釈付けされた Java プログラム要素のリストで区切り文字形式データアクセスを行う区切り文字形式入力ストリームを提供します。
 * 
 * @author 杉澤 浩二
 * @since 1.4.0
 */
public class CsvEntityReader<T> implements Closeable {

	/**
	 * 区切り文字形式入力ストリームを保持します。
	 */
	private CsvReader reader;

	/**
	 * Java プログラム要素操作の簡素化ヘルパーを保持します。
	 */
	private final CsvEntityTemplate<T> template;

	/**
	 * 項目名のリストを保持します。
	 */
	private List<String> columnNames;

	private Field[] fields;

	// ------------------------------------------------------------------------
	// 利便性のための静的メソッド

	/**
	 * 指定された区切り文字形式入力ストリームと Java プログラム要素の型を使用して、{@link CsvEntityReader} の新しいインスタンスを取得します。<p>
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @param entityClass 区切り文字形式データ注釈要素 {@link CsvEntity} で注釈付けされた Java プログラム要素の型
	 * @throws IllegalArgumentException {@code reader} または {@code type} が {@code null} の場合。
	 */
	public static <T> CsvEntityReader<T> newInstance(final CsvReader reader, final Class<T> entityClass) {
		return new CsvEntityReader<T>(reader, entityClass);
	}

	/**
	 * 指定された区切り文字形式入力ストリームと Java プログラム要素操作の簡素化ヘルパーを使用して、{@link CsvEntityReader} の新しいインスタンスを取得します。<p>
	 * このメソッドは利便性のために提供しています。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @throws IllegalArgumentException {@code reader} または {@code template} が {@code null} の場合。
	 */
	public static <T> CsvEntityReader<T> newInstance(final CsvReader reader, final CsvEntityTemplate<T> template) {
		return new CsvEntityReader<T>(reader, template);
	}

	// ------------------------------------------------------------------------
	// コンストラクタ

	/**
	 * 指定された区切り文字形式入力ストリームと Java プログラム要素の型を使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @param entityClass 区切り文字形式データ注釈要素 {@link CsvEntity} で注釈付けされた Java プログラム要素の型
	 * @throws IllegalArgumentException {@code reader} または {@code type} が {@code null} の場合。
	 */
	public CsvEntityReader(final CsvReader reader, final Class<T> entityClass) {
		this(reader, new CsvEntityTemplate<T>(entityClass));
	}

	/**
	 * 指定された区切り文字形式入力ストリームと Java プログラム要素操作の簡素化ヘルパーを使用して、このクラスを構築するコンストラクタです。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @param template Java プログラム要素操作の簡素化ヘルパー
	 * @throws IllegalArgumentException {@code reader} または {@code template} が {@code null} の場合。
	 */
	public CsvEntityReader(final CsvReader reader, final CsvEntityTemplate<T> template) {
		if (reader == null) {
			throw new IllegalArgumentException("CsvReader must not be null");
		}
		if (template == null) {
			throw new IllegalArgumentException("CsvEntityTemplate must not be null");
		}
		this.reader = reader;
		this.template = template;
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
				// ヘッダ行が有効な場合は項目名の一覧を取得します。
				final List<String> names;
				if (template.getType().getAnnotation(CsvEntity.class).header()) {
					names = reader.readValues();
				} else {
					names = template.createColumnNames();
				}

				fields = template.getType().getDeclaredFields();
				template.prepare(names, fields);
				columnNames = Collections.unmodifiableList(names);
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
			fields = null;
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
	 * 論理行を読込み Java プログラム要素として返します。
	 *
	 * @return Java プログラム要素。ストリームの終わりに達した場合は {@code null}
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public T read() throws IOException {
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
	 * 指定された CSV トークンの値をリストを Java プログラム要素へ変換して返します。
	 * 
	 * @param values CSV トークンの値をリスト
	 * @return 変換された Java プログラム要素
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public T toEntity(final List<String> values) throws IOException {
		synchronized (this) {
			ensureOpen();
			ensureHeader();
			return convert(values);
		}
	}

	private List<String> nextValues() throws IOException {
		List<String> values;
		while ((values = reader.readValues()) != null) {
			if (template.isAccept(columnNames, values)) {
				continue;
			}
			return values;
		}
		return null;
	}

	private T convert(final List<String> values) throws IOException {
		final T entity = template.createBean();
		for (final Field f : fields) {
			Object o = null;
			final CsvColumns columns = f.getAnnotation(CsvColumns.class);
			if (columns != null) {
				final StringBuilder sb = new StringBuilder();
				for (final CsvColumn column : columns.value()) {
					final int pos = getPosition(column, f, columnNames);
					if (pos != -1) {
						final String s = values.get(pos);
						if (s != null) {
							sb.append(s);
						}
					}
				}
				o = template.stringToObject(f, sb.toString());
			}
			final CsvColumn column = f.getAnnotation(CsvColumn.class);
			if (column != null) {
				final int pos = getPosition(column, f, columnNames);
				if (pos != -1) {
					o = template.stringToObject(f, values.get(pos));
				}
			}
			if (o != null) {
				setFieldValue(entity, f, o);
			}
		}
		return entity;
	}

}