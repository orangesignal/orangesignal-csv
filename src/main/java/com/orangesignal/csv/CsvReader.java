/*
 * Copyright 2014 the original author or authors.
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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 区切り文字形式入力ストリームを提供します。
 *
 * @author Koji Sugisawa
 * @see <a href="http://www.ietf.org/rfc/rfc4180.txt">RFC-4180 Common Format and MIME Type for Comma-Separated Values (CSV) Files</a>
 */
public class CsvReader implements Closeable {

	/**
	 * 文字入力ストリームを保持します。
	 */
	private Reader in;

	/**
	 * 区切り文字形式情報を保持します。
	 */
	private CsvConfig cfg;

	/**
	 * 終端文字を含む行バッファを保持します。
	 */
	private final StringBuilder line = new StringBuilder();

	/**
	 * 次行の先頭文字を保持します。
	 */
	private int nextChar = -1;

	/**
	 * 行バッファの位置を保持します。
	 */
	private int pos;

	/**
	 * 行読込みのスキップを行ったかどうかを保持します。
	 */
	private boolean skiped;

	/**
	 * トークンの開始物理行番号の現在値を保持します。
	 */
	private int startTokenLineNumber = 0;

	/**
	 * トークンの終了物理行番号の現在値を保持します。
	 */
	private int endTokenLineNumber = 0;

	/**
	 * 開始物理行番号の現在値を保持します。
	 */
	private int startLineNumber = 0;

	/**
	 * 終了物理行番号の現在値を保持します。
	 */
	private int endLineNumber = 0;

	/**
	 * 論理行番号の現在値を保持します。
	 */
	private int lineNumber = 0;

	/**
	 * ファイルの終わりに達したかどうかを保持します。
	 */
	private boolean endOfFile;

	/**
	 * 論理行の終わりに達したかどうかを保持します。
	 */
	private boolean endOfLine;

	/**
	 * 直前の文字が復帰文字かどうかを保持します。
	 */
	private boolean cr = false;

	/**
	 * BOM (Byte Order Mark) を除去するかどうかを保持します。
	 */
	private final boolean utf8bom;

	/**
	 * 項目数チェックの為に直前の行の項目数を保持します。
	 */
	private int countNumberOfColumns = -1;

	/**
	 * 復帰文字です。
	 */
	private static final char CR = '\r';

	/**
	 * 改行文字です。
	 */
	private static final char LF = '\n';

	/**
	 * BOM (Byte Order Mark)
	 */
	private static final int BOM = 0xFEFF;

	private static final int DEFAULT_CHAR_BUFFER_SIZE = 8192;

	// ------------------------------------------------------------------------
	// コンストラクタ

	/**
	 * 指定されたバッファーサイズと指定された区切り文字形式情報を使用して、このクラスを構築するコンストラクタです。
	 *
	 * @param in 文字入力ストリーム
	 * @param sz 入力バッファのサイズ
	 * @param cfg 区切り文字形式情報
	 * @throws IllegalArgumentException {@code sz} が {@code 0} 以下の場合。または、{@code cfg} が {@code null} の場合。
	 * または、{@code cfg} の区切り文字および囲み文字、エスケープ文字の組合せが不正な場合
	 */
	public CsvReader(final Reader in, final int sz, final CsvConfig cfg) {
		if (cfg == null) {
			throw new IllegalArgumentException("CsvConfig must not be null");
		}
		cfg.validate();
		this.in = new BufferedReader(in, sz);
		this.cfg = cfg;
		final String s;
		if (in instanceof InputStreamReader) {
			s = ((InputStreamReader) in).getEncoding();
		} else {
			s = Charset.defaultCharset().name();
		}
		this.utf8bom = s.toLowerCase().matches("^utf\\-{0,1}8$");
	}

	/**
	 * デフォルトのバッファーサイズと指定された区切り文字形式情報を使用して、このクラスを構築するコンストラクタです。
	 *
	 * @param in 文字入力ストリーム
	 * @param cfg 区切り文字形式情報
	 * @throws IllegalArgumentException {@code cfg} が {@code null} の場合
	 * または、{@code cfg} の区切り文字および囲み文字、エスケープ文字の組合せが不正な場合
	 */
	public CsvReader(final Reader in, final CsvConfig cfg) {
		this(in, DEFAULT_CHAR_BUFFER_SIZE, cfg);
	}

	/**
	 * 指定されたバッファーサイズとデフォルトの区切り文字形式情報を使用して、このクラスを構築するコンストラクタです。
	 *
	 * @param in 文字入力ストリーム
	 * @param sz 入力バッファのサイズ
	 * @throws IllegalArgumentException {@code sz} が {@code 0} 以下の場合
	 */
	public CsvReader(final Reader in, final int sz) {
		this(in, sz, new CsvConfig());
	}

	/**
	 * デフォルトのバッファーサイズとデフォルトの区切り文字形式情報を使用して、このクラスを構築するコンストラクタです。
	 *
	 * @param in 文字入力ストリーム
	 */
	public CsvReader(final Reader in) {
		this(in, DEFAULT_CHAR_BUFFER_SIZE, new CsvConfig());
	}

	// ------------------------------------------------------------------------

	/**
	 * 開始物理行番号の現在値を取得します。
	 *
	 * @return 現在の開始物理行番号
	 */
	public int getStartLineNumber() { return startLineNumber; }

	/**
	 * 終了物理行番号の現在値を取得します。
	 *
	 * @return 現在の終了物理行番号
	 */
	public int getEndLineNumber() { return endLineNumber; }

	/**
	 * 論理行番号の現在値を取得します。
	 *
	 * @return 現在の論理行番号
	 */
	public int getLineNumber() { return lineNumber; }

	/**
	 * ファイルの終わりに達したかどうかを取得します。
	 * 
	 * @return ファイルの終わりに達したかどうか
	 * @since 2.2.1
	 */
	public boolean isEndOfFile() { return endOfFile; }

	/**
	 * Checks to make sure that the stream has not been closed
	 */
	private void ensureOpen() throws IOException {
		if (in == null) {
			throw new IOException("Reader closed");
		}
	}

	/**
	 * 物理行を読込んで行バッファへセットします。
	 *
	 * @return 行の終端文字を含まない行文字列
	 * @throws IOException 入出力例外が発生した場合
	 */
	private int cacheLine() throws IOException {
		// 行バッファを構築します。
		line.setLength(0);
		int c;
		if (nextChar != -1) {
			c = nextChar;
			nextChar = -1;
		} else {
			c = in.read();
			// BOM (Byte Order Mark) を除去する場合は BOM を読み飛ばします。
			if (lineNumber == 0 /* && line == null */ && utf8bom && c == BOM) {
				c = in.read();
			}
		}

		int result = -1;	// CR または LF の出現位置
		while (c != -1) {
			line.append((char) c);
			if (c == CR) {
				result = line.length();
				nextChar = in.read();
				if (nextChar == LF) {
					line.append((char) nextChar);
					nextChar = -1;
				}
				break;
			} else if (c == LF) {
				result = line.length();
				break;
			}
			c = in.read();
		}
		pos = 0;

		return result;
	}

	/**
	 * 単一の文字を読み込みます。
	 *
	 * @return 読み込まれた文字。ストリームの終わりに達した場合は {@code -1}
	 * @throws IOException 入出力エラーが発生した場合
	 */
	private int read() throws IOException {
		synchronized (this) {
			ensureOpen();
			if (endOfFile) {
				return -1;
			}
			if (line.length() == 0 || line.length() <= pos) {
				cacheLine();
			}
			if (line.length() == 0) {
				return -1;
			}
			return line.charAt(pos++);
		}
	}

	/**
	 * <p>論理行を読込み区切り文字形式データトークンの値をリストして返します。</p>
	 * このメソッドは利便性のために提供しています。
	 *
	 * @return 区切り文字形式データトークンの値をリスト。ストリームの終わりに達している場合は {@code null}
	 * @throws CsvTokenException 可変項目数が禁止されている場合に項目数が一致しない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public List<String> readValues() throws IOException {
		final List<CsvToken> tokens = readTokens();
		if (tokens == null) {
			return null;
		}
		final List<String> results = new ArrayList<String>(tokens.size());
		for (final CsvToken token : tokens) {
			results.add(token.getValue());
		}
		return results;
	}

	/**
	 * 論理行を読込み区切り文字形式データトークンをリストして返します。
	 *
	 * @return 区切り文字形式データトークンのリスト。ストリームの終わりに達している場合は {@code null}
	 * @throws CsvTokenException 可変項目数が禁止されている場合に項目数が一致しない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public List<CsvToken> readTokens() throws IOException {
		synchronized (this) {
			ensureOpen();
			if (endOfFile) {
				return null;
			}
			if (!skiped) {
				for (int i = 0; i < cfg.getSkipLines(); i++) {
					cacheLine();
					endTokenLineNumber++;
					lineNumber++;
				}
				line.setLength(0);
				skiped = true;
			}
			return readCsvTokens();
		}
	}

	private int arraySize = 3;

	/**
	 * 論理行を読込み、行カウンタを処理して CSV トークンのリストを返します。
	 *
	 * @return CSV トークンのリスト
	 * @throws CsvTokenException 可変項目数が禁止されている場合に項目数が一致しない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	private List<CsvToken> readCsvTokens() throws IOException {
		final List<CsvToken> results = new ArrayList<CsvToken>(arraySize);
		endTokenLineNumber++;
		startLineNumber = endTokenLineNumber;
		endOfLine = false;
		do {
			if (line.length() == 0 || line.length() <= pos) {
				int breakLine = cacheLine();

				// 空行を無視する場合の処理を行います。
				if (cfg.isIgnoreEmptyLines()) {
					boolean ignore = true;
					while (ignore && line.length() > 0) {
						ignore = false;
						if (isWhitespaces(breakLine == -1 ? line : line.substring(0, breakLine - 1))) {
							ignore = true;
							endTokenLineNumber++;
							startLineNumber = endTokenLineNumber;
							lineNumber++;
							breakLine = cacheLine();
						}
					}
				}

				// 無視する行パターンを処理します。
				if (cfg.getIgnoreLinePatterns() != null) {
					boolean ignore = true;
					while (ignore && line.length() > 0) {
						ignore = false;
						for (final Pattern p : cfg.getIgnoreLinePatterns()) {
							if (p != null && p.matcher(breakLine == -1 ? line : line.substring(0, breakLine - 1)).matches()) {
								ignore = true;
								endTokenLineNumber++;
								startLineNumber = endTokenLineNumber;
								lineNumber++;
								breakLine = cacheLine();
								break;
							}
						}
					}
				}
			}
			startTokenLineNumber = endTokenLineNumber;
			results.add(readCsvToken());
		} while (!endOfLine);
		endLineNumber = endTokenLineNumber;
		lineNumber++;

		arraySize = results.size();

		// XXX - 空行の場合に null を返すのではなく NullObject を返すべきなのでは？
		if (arraySize == 1) {
			if (endOfFile) {
				final String value = results.get(0).getValue();
				if (cfg.isIgnoreEmptyLines() && (value == null || isWhitespaces(value))) {
					return null;
				}
				if (cfg.getIgnoreLinePatterns() != null) {
					if (value == null) {
						return null;
					}
					for (final Pattern p : cfg.getIgnoreLinePatterns()) {
						if (p != null && p.matcher(value).matches()) {
							return null;
						}
					}
				}
			} else {
				if (cfg.isIgnoreEmptyLines() && (line.length() == 0 || isWhitespaces(line))) {
					return null;
				}
			}
		}
		if (!cfg.isVariableColumns()) {
			if (countNumberOfColumns >= 0 && countNumberOfColumns != arraySize) {
				throw new CsvTokenException(String.format("Invalid column count in CSV input on line %d.", startLineNumber), results);
			}
			countNumberOfColumns = arraySize;
		}

		return results;
	}

	private final StringBuilder buf = new StringBuilder();
	private boolean inQuote = false;	// 囲み項目を処理中であるかどうか
	private boolean enclosed = false;	// 囲み項目の可能性を示唆します。
	private boolean escaped = false;	// 直前の文字がエスケープ文字かどうか(囲み文字の中)
	private boolean _escaped = false;	// 直前の文字がエスケープ文字かどうか(囲み文字の外)

	/**
	 * CSV トークンを読込みます。
	 *
	 * @return CSV トークン
	 * @throws IOException 入出力エラーが発生した場合
	 */
	private CsvToken readCsvToken() throws IOException {
		buf.setLength(0);
		// 囲み文字設定が有効な場合
		inQuote = false;
		enclosed = false;
		escaped = false;
		_escaped = false;

		endTokenLineNumber = startTokenLineNumber;

		while (true) {
			final int c = read();
			if (cr) {
				cr = false;
				escaped = false;
				if (c == LF) {
					if (inQuote) {
						buf.append((char) c);
					}
					continue;
				}
			} else if (_escaped && c == cfg.getSeparator()) {
				buf.append((char) c);
				_escaped = false;
				continue;
			}
			_escaped = false;
			if (c == -1) {
				endOfLine = true;
//				if (!endOfFile) {
//					endLineNumber++;
//				}
				endOfFile = true;
				break;
			}

			// 囲み文字の外(外側)の場合
			if (!inQuote) {
				// 区切り文字
				if (c == cfg.getSeparator()) {
					break;
				// CR
				} else if (c == CR) {
					endOfLine = true;
					cr = true;
					break;
				// LF
				} else if (c == LF) {
					endOfLine = true;
					break;
				// 囲み文字
				} else if (!cfg.isQuoteDisabled() && !enclosed && c == cfg.getQuote()) {
					if (isWhitespaces(buf)) {
						inQuote = true;
					}
				// エスケープ文字
				} else if (cfg.isQuoteDisabled() && !cfg.isEscapeDisabled() && c == cfg.getEscape()) {
					_escaped = true;
				}
			// 囲み文字の中(内側)の場合
			} else {
				// 囲み文字とエスケープ文字が同一の場合
				if (!cfg.isEscapeDisabled() && cfg.getQuote() == cfg.getEscape()) {
					// 直前の文字がエスケープ文字の場合
					if (escaped) {
						// エスケープ文字直後が区切り文字の場合
						if (c == cfg.getSeparator()) {
							break;
						} else if (c == CR) {
							endOfLine = true;
							cr = true;
							break;
						} else if (c == LF) {
							endOfLine = true;
							break;
						} else if (c == cfg.getEscape()) {
							escaped = false;
							buf.append((char) c);
							continue;
						}
					// 直前の文字がない場合や直前の文字がエスケープ文字ではない場合に、現在の文字がエスケープ文字(囲み文字と同一)の場合
					} else if (c == cfg.getEscape()) {
						escaped = true;
						buf.append((char) c);
						continue;
					}
				}

				// 囲み文字
				if (c == cfg.getQuote()) {
					if (escaped) {
						// 直前がエスケープ文字の場合
						escaped = false;
					} else {
						inQuote = false;
						enclosed = true;
					}
				// CR
				} else if (c == CR) {
					cr = true;
					endTokenLineNumber++;
				// LF
				} else if (c == LF) {
					endTokenLineNumber++;
				}

				if (!cfg.isEscapeDisabled() && c == cfg.getEscape()) {
					escaped = true;
				} else {
					escaped = false;
				}
			}

			buf.append((char) c);
		}

		if (escaped) {
			enclosed = true;
		}

		String value = buf.toString();

		// 囲み項目かどうかの判定
		if (enclosed) {
			// 最後の " 以降にホワイトスペース以外の文字がある場合は囲み項目ではない
			final int i = value.lastIndexOf(cfg.getQuote()) + 1;
			assert i > 0;
			if (i < value.length() && !isWhitespaces(value.substring(i + 1))) {
				enclosed = false;
			}
		}

		if (cfg.isIgnoreLeadingWhitespaces() || enclosed) {
			value = removeLeadingWhitespaces(value);
		}
		if (cfg.isIgnoreTrailingWhitespaces() || enclosed) {
			value = removeTrailingWhitespaces(value);
		}
		if (enclosed) {
			// 囲み文字を除去します。
			value = value.substring(1, value.length() - 1);
			// テキスト内の改行文字列を置換する必要がある場合は置換を行います。
			if (cfg.getBreakString() != null) {
				value = value.replaceAll("\r\n|\r|\n", cfg.getBreakString());
			}
			// エスケープ文字が有効な場合は非エスケープ化します。
			if (!cfg.isEscapeDisabled()) {
				value = unescapeQuote(value);
			}
		} else {
			if (cfg.getNullString() != null) {
				if (cfg.isIgnoreCaseNullString()) {
					if (cfg.getNullString().equalsIgnoreCase(value)) {
						value = null;
					}
				} else {
					if (cfg.getNullString().equals(value)) {
						value = null;
					}
				}
			}
			if (value != null && !cfg.isEscapeDisabled()) {
				value = unescapeSeparator(value);
			}
		}
		if (cfg.isEmptyToNull()) {
			if (value == null || value.length() == 0) {
				value = null;
			}
		}

		return new SimpleCsvToken(value, startTokenLineNumber, endTokenLineNumber, enclosed);
	}

	/**
	 * 指定された CSV トークンを非エスケープ化して返します。
	 *
	 * @param value CSV トークン
	 * @return 変換された CSV トークン
	 */
	private String unescapeQuote(final String value) {
		return value.replace(
				new StringBuilder(2).append(cfg.getEscape()).append(cfg.getQuote()),
				new StringBuilder(1).append(cfg.getQuote())
			);
	}

	private String unescapeSeparator(final String value) {
		return value.replace(
				new StringBuilder(2).append(cfg.getEscape()).append(cfg.getSeparator()),
				new StringBuilder(1).append(cfg.getSeparator())
			);
	}

	// ------------------------------------------------------------------------

	@Override
	public void close() throws IOException {
		synchronized (this) {
			in.close();
			in = null;
			cfg = null;
			line.setLength(0);
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * 指定された文字列がホワイトスペースのみで構成されているかどうかを返します。
	 *
	 * @param value 文字列
	 * @return 指定された文字列がホワイトスペースのみで構成されている場合は {@code true}。それ以外の場合は {@code false}
	 */
	private static boolean isWhitespaces(final CharSequence value) {
		final int len = value.length();
		for (int i = 0; i < len; i++) {
			if (!Character.isWhitespace(value.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	private static String removeLeadingWhitespaces(final String value) {
		final int len = value.length();
		int pos = -1;
		for (int i = 0; i < len; i++) {
			if (!Character.isWhitespace(value.charAt(i))) {
				pos = i;
				break;
			}
		}
		if (pos == -1) {
			return "";
		}
		if (pos > 0) {
			return value.substring(pos);
		}
		return value;
	}

	private static String removeTrailingWhitespaces(final String value) {
		final int start = value.length() - 1;
		int pos = -1;
		for (int i = start; i >= 0; i--) {
			if (!Character.isWhitespace(value.charAt(i))) {
				pos = i;
				break;
			}
		}
		if (pos == -1) {
			return "";
		}
		if (pos != start) {
			return value.substring(0, pos + 1);
		}
		return value;
	}

}