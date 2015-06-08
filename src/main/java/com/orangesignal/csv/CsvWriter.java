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

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 区切り文字形式出力ストリームを提供します。
 *
 * @author Koji Sugisawa
 */
public class CsvWriter implements Closeable, Flushable {

	/**
	 * 文字出力ストリームを保持します。
	 */
	private Writer out;

	/**
	 * 区切り文字形式情報を保持します。
	 */
	private CsvConfig cfg;

	/**
	 * BOM (Byte Order Mark) を出力する必要があるかどうかを保持します。
	 */
	private boolean utf8bom;

	/**
	 * 項目数チェックの為に直前の行の項目数を保持します。
	 */
	private int countNumberOfColumns = -1;

	private static final int DEFAULT_CHAR_BUFFER_SIZE = 8192;

	// ------------------------------------------------------------------------
	// コンストラクタ

	/**
	 * 指定されたバッファーサイズと指定された区切り文字形式情報を使用して、このクラスを構築するコンストラクタです。
	 *
	 * @param out 文字出力ストリーム
	 * @param sz 出力バッファのサイズ
	 * @param cfg 区切り文字形式情報
	 * @throws IllegalArgumentException {@code sz} が {@code 0} 以下の場合。または、{@code cfg} が {@code null} の場合
	 * または、{@code cfg} の区切り文字および囲み文字、エスケープ文字の組合せが不正な場合
	 */
	public CsvWriter(final Writer out, final int sz, final CsvConfig cfg) {
		if (cfg == null) {
			throw new IllegalArgumentException("CsvConfig must not be null");
		}
		cfg.validate();
		this.out = new BufferedWriter(out, sz);
		this.cfg = cfg;

		if (cfg.isUtf8bomPolicy()) {
			final String s;
			if (out instanceof OutputStreamWriter) {
				s = ((OutputStreamWriter) out).getEncoding();
			} else {
				s = Charset.defaultCharset().name();
			}
			this.utf8bom = s.toLowerCase().matches("^utf\\-{0,1}8$");
		}
	}

	/**
	 * デフォルトのバッファーサイズと指定された区切り文字形式情報を使用して、このクラスを構築するコンストラクタです。
	 *
	 * @param out 文字出力ストリーム
	 * @param cfg 区切り文字形式情報
	 * @throws IllegalArgumentException {@code cfg} が {@code null} の場合
	 * または、{@code cfg} の区切り文字および囲み文字、エスケープ文字の組合せが不正な場合
	 */
	public CsvWriter(final Writer out, final CsvConfig cfg) {
		this(out, DEFAULT_CHAR_BUFFER_SIZE, cfg);
	}

	/**
	 * 指定されたバッファーサイズとデフォルトの区切り文字形式情報を使用して、このクラスを構築するコンストラクタです。
	 *
	 * @param out 文字出力ストリーム
	 * @param sz 出力バッファのサイズ
	 * @throws IllegalArgumentException {@code sz} が {@code 0} 以下の場合
	 */
	public CsvWriter(final Writer out, final int sz) {
		this(out, sz, new CsvConfig());
	}

	/**
	 * デフォルトのバッファーサイズとデフォルトの区切り文字形式情報を使用して、このクラスを構築するコンストラクタです。
	 *
	 * @param out 文字出力ストリーム
	 */
	public CsvWriter(final Writer out) {
		this(out, DEFAULT_CHAR_BUFFER_SIZE, new CsvConfig());
	}

	// ------------------------------------------------------------------------

	/**
	 * Checks to make sure that the stream has not been closed
	 */
	private void ensureOpen() throws IOException {
		if (out == null) {
			throw new IOException("Stream closed");
		}
	}

	private static final int BOM = 0xFEFF;

	/**
	 * 指定された CSV トークンの値リストを書き込みます。
	 *
	 * @param values 書き込む CSV トークンの値リスト
	 * @throws CsvValueException 可変項目数が禁止されている場合に項目数が一致しない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void writeValues(final List<String> values) throws IOException {
		writeValuesCore(values, null);
	}

	/**
	 * 指定された CSV トークンの値リストを書き込みます。
	 *
	 * @param values 書き込む CSV トークンの値リスト
	 * @param quotes 列全体を囲み文字で囲むかどうかのリスト
	 * @throws CsvValueException 可変項目数が禁止されている場合に項目数が一致しない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 2.2
	 */
	public void writeValues(final List<String> values, final List<Boolean> quotes) throws IOException {
		writeValuesCore(values, quotes);
	}

	/**
	 * 指定された CSV トークンの値リストを書き込みます。
	 *
	 * @param values 書き込む CSV トークンの値リスト
	 * @param quotes 列全体を囲み文字で囲むかどうかのリスト
	 * @throws CsvValueException 可変項目数が禁止されている場合に項目数が一致しない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	private void writeValuesCore(final List<String> values, List<Boolean> quotes) throws IOException {
		synchronized (this) {
			ensureOpen();

			if (utf8bom) {
				out.write(BOM);
				utf8bom = false;
			}

			final StringBuilder buf = new StringBuilder();
			if (values != null) {
				final int max = values.size();

				if (quotes == null) {
					quotes = new ArrayList<Boolean>();
					for (int i = 0; i < max; i++) {
						quotes.add(false);
					}
				}

				for (int i = 0; i < max; i++) {
					if (i > 0) {
						buf.append(cfg.getSeparator());
					}

					String value = values.get(i);
					boolean enclose = false;	// 項目を囲み文字で囲むかどうか
					if (value == null || "".equals(value)) {
						// 項目値が null もしくは空白の場合に NULL 文字列が有効であれば NULL 文字列へ置換えます。
						if (cfg.getNullString() == null) {
							continue;
						}
						value = cfg.getNullString();
					} else if (!cfg.isQuoteDisabled()) {
						// 囲み文字が有効な場合は、囲み文字で囲むべきかどうか判断します。
						switch (cfg.getQuotePolicy()) {
							case ALL:
								enclose = true;
								break;

							case COLUMN:
								// CsvColumn の columnQuote が true の場合に列全体を囲み文字で囲みます。
								if (quotes.get(i)) {
									enclose = true;
								} else {
									// 項目値に区切り文字、囲み文字、改行文字のいずれかを含む場合は囲み文字で囲むべきと判断します。
									enclose = value.indexOf(cfg.getSeparator()) != -1
											|| value.indexOf(cfg.getQuote()) != -1
											|| value.indexOf('\r') != -1 || value.indexOf('\n') != -1;
								}
								break;

							case MINIMAL:
							default:
								// 項目値に区切り文字、囲み文字、改行文字のいずれかを含む場合は囲み文字で囲むべきと判断します。
								enclose = value.indexOf(cfg.getSeparator()) != -1
										|| value.indexOf(cfg.getQuote()) != -1
										|| value.indexOf('\r') != -1 || value.indexOf('\n') != -1;
								break;
						}
					} else {
						// 囲み文字が無効な場合に、項目値に区切り文字がある場合、エスケープします。
						final String s = escapeSeparator(value);
						if (!value.equals(s) && cfg.isEscapeDisabled()) {
							throw new IOException();
						}
						value = s;
					}
	
					if (enclose) {
						buf.append(cfg.getQuote());
						final String s = escapeQuote(value);
						if (!value.equals(s) && cfg.isEscapeDisabled()) {
							throw new IOException();
						}
						buf.append(s);
						buf.append(cfg.getQuote());
					} else {
						buf.append(value);
					}
				}
			}
			if (values != null || !cfg.isIgnoreEmptyLines()) {
				buf.append(cfg.getLineSeparator());
				out.write(buf.toString());
			}
			if (!cfg.isVariableColumns() && values != null) {
				if (countNumberOfColumns >= 0 && countNumberOfColumns != values.size()) {
					throw new CsvValueException(String.format("Invalid column count."), values);
				}
				countNumberOfColumns = values.size();
			}
		}
	}

	/**
	 * 指定された CSV トークンのリストを書き込みます。
	 * 
	 * @param tokens 書き込む CSV トークンのリスト
	 * @throws CsvValueException 可変項目数が禁止されている場合に項目数が一致しない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void writeTokens(final List<CsvToken> tokens) throws IOException {
		if (tokens != null) {
			final List<String> values = new ArrayList<String>(tokens.size());
			for (final CsvToken token : tokens) {
				if (token == null) {
					values.add(null);
				} else {
					values.add(token.getValue());
				}
			}
			writeValues(values);
		} else {
			writeValues(null);
		}
	}

	/**
	 * 指定された文字列中の区切り文字をエスケープ化して返します。
	 * 
	 * @param value 文字列
	 * @return エスケープされた文字列
	 */
	private String escapeSeparator(final String value) {
		return value.replace(
				new StringBuilder(1).append(cfg.getSeparator()),
				new StringBuilder(2).append(cfg.getEscape()).append(cfg.getSeparator())
			);
	}

	/**
	 * 指定された文字列中の囲み文字をエスケープ化して返します。
	 *
	 * @param value 文字列
	 * @return エスケープされた文字列
	 */
	private String escapeQuote(final String value) {
		return value.replace(
				new StringBuilder(1).append(cfg.getQuote()),
				new StringBuilder(2).append(cfg.getEscape()).append(cfg.getQuote())
			);
	}

	/**
	 * 区切り文字形式情報を返します。
	 * @return 区切り文字形式情報
	 * @since 2.2
	 */
	public CsvConfig getCfg() {
		return cfg;
	}

	@Override
	public void flush() throws IOException {
		synchronized (this) {
			ensureOpen();
			out.flush();
		}
	}

	@Override
	public void close() throws IOException {
		if (out != null) {
			out.close();
			out = null;
			cfg = null;
		}
	}

}