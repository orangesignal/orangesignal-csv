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

package com.orangesignal.csv;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.orangesignal.jlha.LhaFile;
import com.orangesignal.jlha.LhaHeader;
import com.orangesignal.jlha.LhaInputStream;
import com.orangesignal.jlha.LhaOutputStream;

/**
 * 区切り文字形式データの統合アクセスユーティリティを提供します。
 *
 * @author Koji Sugisawa
 */
public abstract class Csv {

	/**
	 * デフォルトコンストラクタです。
	 */
	protected Csv() {
	}

	// ------------------------------------------------------------------------
	// static load

	/**
	 * 指定された区切り文字形式入力ストリームを読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param reader 区切り文字形式入力ストリーム
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> T load(final CsvReader reader, final CsvHandler<T> handler) throws IOException {
		return handler.load(reader);
	}

	/**
	 * 指定された文字入力ストリームを読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param reader 文字入力ストリーム
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> T load(final Reader reader, final CsvConfig cfg, final CsvHandler<T> handler) throws IOException {
		return load(new CsvReader(reader, cfg), handler);
	}

	/**
	 * 指定された入力ストリームを指定されたエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in 入力ストリーム
	 * @param encoding エンコーディング
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> T load(final InputStream in, final String encoding, final CsvConfig cfg, final CsvHandler<T> handler) throws IOException {
		return load(new InputStreamReader(in, encoding), cfg, handler);
	}

	/**
	 * 指定された入力ストリームをプラットフォームのデフォルトエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in 入力ストリーム
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> T load(final InputStream in, final CsvConfig cfg, final CsvHandler<T> handler) throws IOException {
		return load(new InputStreamReader(in), cfg, handler);
	}

	/**
	 * 指定されたファイルを指定されたエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param file 入力ファイル
	 * @param encoding エンコーディング
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> T load(final File file, final String encoding, final CsvConfig cfg, final CsvHandler<T> handler) throws IOException {
		final InputStream in = new FileInputStream(file);
		try {
			return load(in, encoding, cfg, handler);
		} finally {
			closeQuietly(in);
		}
	}

	/**
	 * 指定されたファイルをプラットフォームのデフォルトエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param file 入力ファイル
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> T load(final File file, final CsvConfig cfg, final CsvHandler<T> handler) throws IOException {
		final InputStream in = new FileInputStream(file);
		try {
			return load(in, cfg, handler);
		} finally {
			closeQuietly(in);
		}
	}

	// ------------------------------------------------------------------------
	// static load (compress support)

	/**
	 * 指定された LHA 入力ストリームから指定されたフィルタの基準を満たす LHA エントリを指定されたエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in LHA 入力ストリーム
	 * @param encoding エンコーディング
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @param filter LHA エントリフィルタ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	public static <T> List<T> load(final LhaInputStream in, final String encoding, final CsvConfig cfg, final CsvListHandler<T> handler, final LhaEntryFilter filter) throws IOException {
		final List<T> list = new ArrayList<T>();
		LhaHeader entry;
		while ((entry = in.getNextEntry()) != null) {
			try {
				if (filter != null && !filter.accept(entry)) {
					continue;
				}
				list.addAll(handler.load(new CsvReader(new InputStreamReader(in, encoding), cfg), true));
			} finally {
				in.closeEntry();
			}
		}
		return handler.processScalar(list);
	}

	/**
	 * 指定された LHA 入力ストリームからすべての LHA エントリを指定されたエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in LHA 入力ストリーム
	 * @param encoding エンコーディング
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	public static <T> List<T> load(final LhaInputStream in, final String encoding, final CsvConfig cfg, final CsvListHandler<T> handler) throws IOException {
		return load(in, encoding, cfg, handler, null);
	}

	/**
	 * 指定された LHA 入力ストリームから指定されたフィルタの基準を満たす LHA エントリをプラットフォームのデフォルトエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in LHA 入力ストリーム
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @param filter LHA エントリフィルタ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> List<T> load(final LhaInputStream in, final CsvConfig cfg, final CsvListHandler<T> handler, final LhaEntryFilter filter) throws IOException {
		final List<T> list = new ArrayList<T>();
		LhaHeader entry;
		while ((entry = in.getNextEntry()) != null) {
			try {
				if (filter != null && !filter.accept(entry)) {
					continue;
				}
				list.addAll(handler.load(new CsvReader(new InputStreamReader(in), cfg), true));
			} finally {
				in.closeEntry();
			}
		}
		return handler.processScalar(list);
	}

	/**
	 * 指定された LHA 入力ストリームからすべての LHA エントリをプラットフォームのデフォルトエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in LHA 入力ストリーム
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> List<T> load(final LhaInputStream in, final CsvConfig cfg, final CsvListHandler<T> handler) throws IOException {
		return load(in, cfg, handler, null);
	}

	/**
	 * 指定された LHA ファイルから指定されたフィルタの基準を満たす LHA エントリを指定されたエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param lhaFile LHA ファイル
	 * @param encoding エンコーディング
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @param filter LHA エントリフィルタ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	public static <T> List<T> load(final LhaFile lhaFile, final String encoding, final CsvConfig cfg, final CsvListHandler<T> handler, final LhaEntryFilter filter) throws IOException {
		final List<T> list = new ArrayList<T>();
		final LhaHeader[] entries = lhaFile.getEntries();
		for (final LhaHeader entry : entries) {
			if (filter != null && !filter.accept(entry)) {
				continue;
			}
			list.addAll(handler.load(new CsvReader(new InputStreamReader(lhaFile.getInputStream(entry), encoding), cfg), true));
		}
		return handler.processScalar(list);
	}

	/**
	 * 指定された LHA ファイルからすべての LHA エントリを指定されたエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param lhaFile LHA ファイル
	 * @param encoding エンコーディング
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	public static <T> List<T> load(final LhaFile lhaFile, final String encoding, final CsvConfig cfg, final CsvListHandler<T> handler) throws IOException {
		return load(lhaFile, encoding, cfg, handler, null);
	}

	/**
	 * 指定された LHA ファイルから指定されたフィルタの基準を満たす LHA エントリをプラットフォームのデフォルトエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param lhaFile LHA ファイル
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @param filter LHA エントリフィルタ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> List<T> load(final LhaFile lhaFile, final CsvConfig cfg, final CsvListHandler<T> handler, final LhaEntryFilter filter) throws IOException {
		final List<T> list = new ArrayList<T>();
		final LhaHeader[] entries = lhaFile.getEntries();
		for (final LhaHeader entry : entries) {
			if (filter != null && !filter.accept(entry)) {
				continue;
			}
			list.addAll(handler.load(new CsvReader(new InputStreamReader(lhaFile.getInputStream(entry)), cfg), true));
		}
		return handler.processScalar(list);
	}

	/**
	 * 指定された LHA ファイルからすべての LHA エントリをプラットフォームのデフォルトエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param lhaFile LHA ファイル
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> List<T> load(final LhaFile lhaFile, final CsvConfig cfg, final CsvListHandler<T> handler) throws IOException {
		return load(lhaFile, cfg, handler, null);
	}

	/**
	 * 指定された ZIP 入力ストリームから指定されたフィルタの基準を満たす ZIP エントリを指定されたエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in ZIP 入力ストリーム
	 * @param encoding エンコーディング
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @param filter ZIP エントリフィルタ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	public static <T> List<T> load(final ZipInputStream in, final String encoding, final CsvConfig cfg, final CsvListHandler<T> handler, final ZipEntryFilter filter) throws IOException {
		final List<T> list = new ArrayList<T>();
		ZipEntry entry;
		while ((entry = in.getNextEntry()) != null) {
			try {
				if (filter != null && !filter.accept(entry)) {
					continue;
				}
				list.addAll(handler.load(new CsvReader(new InputStreamReader(in, encoding), cfg), true));
			} finally {
				in.closeEntry();
			}
		}
		return handler.processScalar(list);
	}

	/**
	 * 指定された ZIP 入力ストリームからすべての ZIP エントリを指定されたエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in ZIP 入力ストリーム
	 * @param encoding エンコーディング
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	public static <T> List<T> load(final ZipInputStream in, final String encoding, final CsvConfig cfg, final CsvListHandler<T> handler) throws IOException {
		return load(in, encoding, cfg, handler, null);
	}

	/**
	 * 指定された ZIP 入力ストリームから指定されたフィルタの基準を満たす ZIP エントリをプラットフォームのデフォルトエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in ZIP 入力ストリーム
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @param filter ZIP エントリフィルタ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> List<T> load(final ZipInputStream in, final CsvConfig cfg, final CsvListHandler<T> handler, final ZipEntryFilter filter) throws IOException {
		final List<T> list = new ArrayList<T>();
		ZipEntry entry;
		while ((entry = in.getNextEntry()) != null) {
			try {
				if (filter != null && !filter.accept(entry)) {
					continue;
				}
				list.addAll(handler.load(new CsvReader(new InputStreamReader(in), cfg), true));
			} finally {
				in.closeEntry();
			}
		}
		return handler.processScalar(list);
	}

	/**
	 * 指定された ZIP 入力ストリームからすべての ZIP エントリをプラットフォームのデフォルトエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in ZIP 入力ストリーム
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> List<T> load(final ZipInputStream in, final CsvConfig cfg, final CsvListHandler<T> handler) throws IOException {
		return load(in, cfg, handler, null);
	}

	/**
	 * 指定された ZIP ファイルから指定されたフィルタの基準を満たす ZIP エントリを指定されたエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param zipFile ZIP ファイル
	 * @param encoding エンコーディング
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @param filter ZIP エントリフィルタ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	public static <T> List<T> load(final ZipFile zipFile, final String encoding, final CsvConfig cfg, final CsvListHandler<T> handler, final ZipEntryFilter filter) throws IOException {
		final List<T> list = new ArrayList<T>();
		final Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			final ZipEntry entry = entries.nextElement();
			if (filter != null && !filter.accept(entry)) {
				continue;
			}
			final InputStream in = zipFile.getInputStream(entry);
			try {
				list.addAll(handler.load(new CsvReader(new InputStreamReader(in, encoding), cfg), true));
			} finally {
				closeQuietly(in);
			}
		}
		return handler.processScalar(list);
	}

	/**
	 * 指定された ZIP ファイルからすべての ZIP エントリを指定されたエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param zipFile ZIP ファイル
	 * @param encoding エンコーディング
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	public static <T> List<T> load(final ZipFile zipFile, final String encoding, final CsvConfig cfg, final CsvListHandler<T> handler) throws IOException {
		return load(zipFile, encoding, cfg, handler, null);
	}

	/**
	 * 指定された ZIP ファイルから指定されたフィルタの基準を満たす ZIP エントリをプラットフォームのデフォルトエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param zipFile ZIP ファイル
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @param filter ZIP エントリフィルタ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> List<T> load(final ZipFile zipFile, final CsvConfig cfg, final CsvListHandler<T> handler, final ZipEntryFilter filter) throws IOException {
		final List<T> list = new ArrayList<T>();
		final Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			final ZipEntry entry = entries.nextElement();
			if (filter != null && !filter.accept(entry)) {
				continue;
			}
			final InputStream in = zipFile.getInputStream(entry);
			try {
				list.addAll(handler.load(new CsvReader(new InputStreamReader(in), cfg), true));
			} finally {
				closeQuietly(in);
			}
		}
		return handler.processScalar(list);
	}

	/**
	 * 指定された ZIP ファイルからすべての ZIP エントリをプラットフォームのデフォルトエンコーディングで読込み、
	 * ハンドラによって変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param zipFile ZIP ファイル
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @return ハンドラによって変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> List<T> load(final ZipFile zipFile, final CsvConfig cfg, final CsvListHandler<T> handler) throws IOException {
		return load(zipFile, cfg, handler, null);
	}

	// ------------------------------------------------------------------------
	// static save

	/**
	 * 指定された区切り文字形式データのインスタンスを
	 * ハンドラによって変換して指定された区切り文字形式出力ストリームへ書込みます。
	 *
	 * @param obj 区切り文字形式データのインスタンス
	 * @param writer 区切り文字形式出力ストリーム
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> void save(final T obj, final CsvWriter writer, final CsvHandler<T> handler) throws IOException {
		handler.save(obj, writer);
		writer.flush();
	}

	/**
	 * 指定された区切り文字形式データのインスタンスを
	 * ハンドラによって変換して指定された文字出力ストリームへ書込みます。
	 *
	 * @param obj 区切り文字形式データのインスタンス
	 * @param writer 文字出力ストリーム
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> void save(final T obj, final Writer writer, final CsvConfig cfg, final CsvHandler<T> handler) throws IOException {
		save(obj, new CsvWriter(writer, cfg), handler);
	}

	/**
	 * 指定された区切り文字形式データのインスタンスを
	 * ハンドラによって変換して指定された出力ストリームへ指定されたエンコーディングで書込みます。
	 *
	 * @param obj 区切り文字形式データのインスタンス
	 * @param out 出力ストリーム
	 * @param encoding エンコーディング
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> void save(final T obj, final OutputStream out, final String encoding, final CsvConfig cfg, final CsvHandler<T> handler) throws IOException {
		save(obj, new OutputStreamWriter(out, encoding), cfg, handler);
	}

	/**
	 * 指定された区切り文字形式データのインスタンスを
	 * ハンドラによって変換して指定された出力ストリームへプラットフォームのデフォルトエンコーディングで書込みます。
	 *
	 * @param obj 区切り文字形式データのインスタンス
	 * @param out 出力ストリーム
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> void save(final T obj, final OutputStream out, final CsvConfig cfg, final CsvHandler<T> handler) throws IOException {
		save(obj, new OutputStreamWriter(out), cfg, handler);
	}

	/**
	 * 指定された区切り文字形式データのインスタンスを
	 * ハンドラによって変換して指定されたファイルへ指定されたエンコーディングで書込みます。
	 *
	 * @param obj 区切り文字形式データのインスタンス
	 * @param file 出力ファイル
	 * @param encoding エンコーディング
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> void save(final T obj, final File file, final String encoding, final CsvConfig cfg, final CsvHandler<T> handler) throws IOException {
		final OutputStream out = new FileOutputStream(file);
		try {
			save(obj, out, encoding, cfg, handler);
		} finally {
			closeQuietly(out);
		}
	}

	/**
	 * 指定された区切り文字形式データのインスタンスを
	 * ハンドラによって変換して指定されたファイルへプラットフォームのデフォルトエンコーディングで書込みます。
	 *
	 * @param obj 区切り文字形式データのインスタンス
	 * @param file 出力ファイル
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> void save(final T obj, final File file, final CsvConfig cfg, final CsvHandler<T> handler) throws IOException {
		final OutputStream out = new FileOutputStream(file);
		try {
			save(obj, out, cfg, handler);
		} finally {
			closeQuietly(out);
		}
	}

	// ------------------------------------------------------------------------
	// static save (compress support)

	/**
	 * 指定された区切り文字形式データのインスタンスを
	 * ハンドラによって変換して指定された LHA 出力ストリームへ指定されたエンコーディングで指定された LHA エントリを書込みます。
	 * 
	 * @param obj 区切り文字形式データのインスタンス
	 * @param out LHA 出力ストリーム
	 * @param encoding エンコーディング
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @param entryName 作成する LHA エントリ名
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	public static <T> void save(final List<T> obj, final LhaOutputStream out, final String encoding, final CsvConfig cfg, final CsvListHandler<T> handler, final String entryName) throws IOException {
		out.putNextEntry(new LhaHeader(entryName));
		try {
			save(obj, out, encoding, cfg, handler);
		} finally {
			out.closeEntry();
		}
	}

	/**
	 * 指定された区切り文字形式データのインスタンスを
	 * ハンドラによって変換して指定された LHA 出力ストリームへプラットフォームのデフォルトエンコーディングで指定された LHA エントリを書込みます。
	 * 
	 * @param obj 区切り文字形式データのインスタンス
	 * @param out LHA 出力ストリーム
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @param entryName 作成する LHA エントリ名
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> void save(final List<T> obj, final LhaOutputStream out, final CsvConfig cfg, final CsvListHandler<T> handler, final String entryName) throws IOException {
		out.putNextEntry(new LhaHeader(entryName));
		try {
			save(obj, out, cfg, handler);
		} finally {
			out.closeEntry();
		}
	}

	/**
	 * 指定された区切り文字形式データのインスタンスを
	 * ハンドラによって変換して指定された ZIP 出力ストリームへ指定されたエンコーディングで指定された ZIP エントリを書込みます。
	 * 
	 * @param obj 区切り文字形式データのインスタンス
	 * @param out ZIP 出力ストリーム
	 * @param encoding エンコーディング
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @param entryName 作成する ZIP エントリ名
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	public static <T> void save(final List<T> obj, final ZipOutputStream out, final String encoding, final CsvConfig cfg, final CsvListHandler<T> handler, final String entryName) throws IOException {
		out.putNextEntry(new ZipEntry(entryName));
		try {
			save(obj, out, encoding, cfg, handler);
		} finally {
			out.closeEntry();
		}
	}

	/**
	 * 指定された区切り文字形式データのインスタンスを
	 * ハンドラによって変換して指定された ZIP 出力ストリームへプラットフォームのデフォルトエンコーディングで指定された ZIP エントリを書込みます。
	 * 
	 * @param obj 区切り文字形式データのインスタンス
	 * @param out ZIP 出力ストリーム
	 * @param cfg 区切り文字形式情報
	 * @param handler 区切り文字形式データアクセスハンドラ
	 * @param entryName 作成する ZIP エントリ名
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public static <T> void save(final List<T> obj, final ZipOutputStream out, final CsvConfig cfg, final CsvListHandler<T> handler, final String entryName) throws IOException {
		out.putNextEntry(new ZipEntry(entryName));
		try {
			save(obj, out, cfg, handler);
		} finally {
			out.closeEntry();
		}
	}

	// ------------------------------------------------------------------------
	// closeQuietly

	/**
	 * 無条件に、<code>Closeable</code> を閉じます。
	 *
	 * @param closeable Closeable オブジェクト
	 */
	protected static void closeQuietly(final Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (final IOException e) {
			// 無視する
		}
	}

}