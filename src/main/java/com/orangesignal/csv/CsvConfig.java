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

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * 区切り文字形式情報を提供します。<p>
 * このクラスは、区切り文字や囲み文字、エスケープ文字など CSV 形式に関する設定情報を管理します。
 * 
 * @author Koji Sugisawa
 */
public class CsvConfig implements Serializable, Cloneable {

	private static final long serialVersionUID = -7531286991159010295L;

	/**
	 * デフォルトの区切り文字 (,) です。
	 */
	public static final char DEFAULT_SEPARATOR = ',';

	/**
	 * デフォルトの囲み文字 (") です。
	 */
	public static final char DEFAULT_QUOTE = '"';

	/**
	 * デフォルトのエスケープ文字 (\) です。
	 */
	public static final char DEFAULT_ESCAPE = '\\';

	/**
	 * デフォルトの先頭から読飛ばす論理行数 ({@value}) です。
	 */
	public static final int DEFAULT_SKIP_LINES = 0;

	/**
	 * デフォルトの囲み文字出力方法の種類 ({@link QuotePolicy#ALL}) です。
	 * 
	 * @since 1.1
	 */
	public static final QuotePolicy DEFAULT_QUOTE_POLICY = QuotePolicy.ALL;

	// ------------------------------------------------------------------------

	/**
	 * 区切り文字を保持します。
	 */
	private char separator;

	/**
	 * 囲み文字を保持します。
	 */
	private char quote;

	/**
	 * エスケープ文字を保持します。
	 */
	private char escape;

	/**
	 * 囲み文字を無効にするかどうかを保持します。
	 */
	private boolean quoteDisabled;

	/**
	 * エスケープ文字を無効にするかどうかを保持します。
	 */
	private boolean escapeDisabled;

	/**
	 * 値の改行文字列を置換える文字列を保持します。
	 */
	private String breakString;

	/**
	 * 値がないことを表す文字列を保持します。
	 */
	private String nullString;

	/**
	 * 値がないことを表す文字列の大文字と小文字を区別するかどうかを保持します。
	 */
	private boolean ignoreCaseNullString;

	/**
	 * 値より前のホワイトスペースを除去するかどうかを保持します。
	 */
	private boolean ignoreLeadingWhitespaces;

	/**
	 * 値より後ろのホワイトスペースを除去するかどうかを保持します。
	 */
	private boolean ignoreTrailingWhitespaces;

	/**
	 * 空行を無視するかどうかを保持します。
	 */
	private boolean ignoreEmptyLines;

	/**
	 * 無視する行の正規表現パターン群を保持します。
	 */
	private Pattern[] ignoreLinePatterns;

	/**
	 * 先頭から読飛ばす論理行数を保持します。
	 */
	private int skipLines = DEFAULT_SKIP_LINES;

	/**
	 * 囲み文字出力方法の種類を保持します。
	 */
	private QuotePolicy quotePolicy = DEFAULT_QUOTE_POLICY;

	/**
	 * UTF-8 エンコーディングでの出力時に BOM (Byte Order Mark) を付与するかどうかを保持します。
	 */
	private boolean utf8bomPolicy;

	/**
	 * 可変項目数を許可するかどうかを保持します。
	 * 
	 * @since 2.1
	 */
	private boolean variableColumns = true;

	/**
	 * データ出力時の改行文字列を保持します。
	 */
	private String lineSeparator = System.getProperty("line.separator");

	// ------------------------------------------------------------------------
	// コンストラクタ

	/**
	 * デフォルトコンストラクタです。
	 */
	public CsvConfig() {
		this(DEFAULT_SEPARATOR, DEFAULT_QUOTE, DEFAULT_ESCAPE, true, true);
	}

	/**
	 * コンストラクタです。
	 *
	 * @param separator 区切り文字
	 */
	public CsvConfig(final char separator) {
		this(separator, DEFAULT_QUOTE, DEFAULT_ESCAPE, true, true);
	}

	/**
	 * コンストラクタです。
	 *
	 * @param separator 区切り文字
	 * @param quote 囲み文字
	 * @param escape エスケープ文字
	 * @throws IllegalArgumentException {@code quote} または {@code escape} が {@code separator} と同一文字の場合
	 */
	public CsvConfig(final char separator, final char quote, final char escape) {
		this(separator, quote, escape, false, false);
	}

	/**
	 * コンストラクタです。
	 *
	 * @param separator 区切り文字
	 * @param quote 囲み文字
	 * @param escape エスケープ文字
	 * @param quoteDisabled 囲み文字を無効にするかどうか
	 * @param escapeDisabled エスケープ文字を無効にするかどうか
	 * @throws IllegalArgumentException {@code quote} または {@code escape} が {@code separator} と同一文字の場合
	 */
	public CsvConfig(final char separator, final char quote, final char escape, final boolean quoteDisabled, final boolean escapeDisabled) {
		this.separator = separator;
		this.quote = quote;
		this.escape = escape;
		this.quoteDisabled = quoteDisabled;
		this.escapeDisabled = escapeDisabled;
	}

	// ------------------------------------------------------------------------

	/**
	 * 区切り文字および囲み文字、エスケープ文字の組合せを検証します。
	 *
	 * @throws IllegalArgumentException 区切り文字および囲み文字、エスケープ文字の組合せが不正な場合
	 */
	public void validate() {
		// 区切り文字に改行文字が指定された場合は例外をスローします。
		if (separator == '\r' || separator == '\n') {
			throw new IllegalArgumentException("Invalid separator character");
		}
		// 囲み文字に区切り文字と同じ文字や改行文字が指定された場合は例外をスローします。
		if (!quoteDisabled && (quote == separator || quote == '\r' || quote == '\n')) {
			throw new IllegalArgumentException("Invalid quote character");
		}
		// エスケープ文字文字に区切り文字と同じ文字や改行文字が指定された場合は例外をスローします。
		if (!escapeDisabled && (escape == separator || escape == '\r' || escape == '\n')) {
			throw new IllegalArgumentException("Invalid escape character");
		}
	}

	// ------------------------------------------------------------------------
	// セッター/ゲッター

	/**
	 * 区切り文字を返します。
	 *
	 * @return 区切り文字
	 */
	public char getSeparator() { return separator; }

	/**
	 * 区切り文字を設定します。
	 *
	 * @param separator 区切り文字
	 */
	public void setSeparator(final char separator) { this.separator = separator; }

	/**
	 * 区切り文字を設定します。
	 *
	 * @param separator 区切り文字
	 */
	public CsvConfig withSeparator(final char separator) {
		this.separator = separator;
		return this;
	}

	/**
	 * 囲み文字を返します。
	 *
	 * @return 囲み文字
	 */
	public char getQuote() { return quote; }

	/**
	 * 囲み文字を設定します。
	 *
	 * @param quote 囲み文字
	 */
	public void setQuote(final char quote) { this.quote = quote; }

	/**
	 * 囲み文字を設定します。
	 *
	 * @param quote 囲み文字
	 */
	public CsvConfig withQuote(final char quote) {
		this.quote = quote;
		return this;
	}

	/**
	 * エスケープ文字を返します。
	 *
	 * @return エスケープ文字
	 */
	public char getEscape() { return escape; }

	/**
	 * エスケープ文字を設定します。
	 *
	 * @param escape エスケープ文字
	 */
	public void setEscape(final char escape) { this.escape = escape; }

	/**
	 * エスケープ文字を設定します。
	 *
	 * @param escape エスケープ文字
	 */
	public CsvConfig withEscape(final char escape) {
		this.escape = escape;
		return this;
	}

	/**
	 * 囲み文字を無効にするかどうかを返します。
	 *
	 * @return 囲み文字を無効にするかどうか
	 */
	public boolean isQuoteDisabled() { return quoteDisabled; }

	/**
	 * 囲み文字を無効にするかどうかを設定します。
	 *
	 * @param disabled 囲み文字を無効にするかどうか
	 */
	public void setQuoteDisabled(final boolean disabled) { this.quoteDisabled = disabled; }

	/**
	 * 囲み文字を無効にするかどうかを設定します。
	 *
	 * @param disabled 囲み文字を無効にするかどうか
	 */
	public CsvConfig withQuoteDisabled(final boolean disabled) {
		this.quoteDisabled = disabled;
		return this;
	}

	/**
	 * エスケープ文字を無効にするかどうかを返します。
	 *
	 * @return エスケープ文字を無効にするかどうか
	 */
	public boolean isEscapeDisabled() { return escapeDisabled; }

	/**
	 * エスケープ文字を無効にするかどうかを設定します。
	 *
	 * @param disabled エスケープ文字を無効にするかどうか
	 */
	public void setEscapeDisabled(final boolean disabled) { this.escapeDisabled = disabled; }

	/**
	 * エスケープ文字を無効にするかどうかを設定します。
	 *
	 * @param disabled エスケープ文字を無効にするかどうか
	 */
	public CsvConfig withEscapeDisabled(final boolean disabled) {
		this.escapeDisabled = disabled;
		return this;
	}

	/**
	 * 値の改行文字列を置換える文字列を返します。
	 * 
	 * @return 値の改行文字列を置換える文字列
	 */
	public String getBreakString() { return breakString; }

	/**
	 * 値の改行文字列を置換える文字列を設定します。
	 * 
	 * @param breakString 値の改行文字列を置換える文字列
	 */
	public void setBreakString(final String breakString) { this.breakString = breakString; }

	/**
	 * 値の改行文字列を置換える文字列を設定します。
	 * 
	 * @param breakString 値の改行文字列を置換える文字列
	 */
	public CsvConfig withBreakString(final String breakString) {
		this.breakString = breakString;
		return this;
	}

	/**
	 * 値がないことを表す文字列を返します。
	 *
	 * @return 値がないことを表す文字列
	 */
	public String getNullString() { return nullString; }

	/**
	 * 値がないことを表す文字列を設定します。
	 *
	 * @param nullString 値がないことを表す文字列
	 */
	public void setNullString(final String nullString) {
		setNullString(nullString, false);
	}

	/**
	 * 値がないことを表す文字列を設定します。
	 *
	 * @param nullString 値がないことを表す文字列
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 */
	public void setNullString(final String nullString, final boolean ignoreCase) {
		this.nullString = nullString;
		this.ignoreCaseNullString = ignoreCase;
	}

	/**
	 * 値がないことを表す文字列を設定します。
	 *
	 * @param nullString 値がないことを表す文字列
	 */
	public CsvConfig withNullString(final String nullString) {
		return withNullString(nullString, false);
	}

	/**
	 * 値がないことを表す文字列を設定します。
	 *
	 * @param nullString 値がないことを表す文字列
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 */
	public CsvConfig withNullString(final String nullString, final boolean ignoreCase) {
		this.nullString = nullString;
		this.ignoreCaseNullString = ignoreCase;
		return this;
	}

	/**
	 * 値がないことを表す文字列の大文字と小文字を区別するかどうかを返します。
	 * 
	 * @return 値がないことを表す文字列の大文字と小文字を区別するかどうか
	 */
	public boolean isIgnoreCaseNullString() { return ignoreCaseNullString; }

	/**
	 * 値がないことを表す文字列の大文字と小文字を区別するかどうかを設定します。
	 * 
	 * @param ignoreCaseNullString 値がないことを表す文字列の大文字と小文字を区別するかどうか
	 * @since 1.2.2
	 */
	public void setIgnoreCaseNullString(final boolean ignoreCaseNullString) {
		this.ignoreCaseNullString = ignoreCaseNullString;
	}

	/**
	 * 値がないことを表す文字列の大文字と小文字を区別するかどうかを設定します。
	 * 
	 * @param ignoreCaseNullString 値がないことを表す文字列の大文字と小文字を区別するかどうか
	 * @since 1.2.2
	 */
	public CsvConfig withIgnoreCaseNullString(final boolean ignoreCaseNullString) {
		this.ignoreCaseNullString = ignoreCaseNullString;
		return this;
	}

	/**
	 * 値より前のホワイトスペースを除去するかどうかを返します。
	 *
	 * @return 値より前のホワイトスペースを除去するかどうか
	 */
	public boolean isIgnoreLeadingWhitespaces() { return ignoreLeadingWhitespaces; }

	/**
	 * 値より前のホワイトスペースを除去するかどうかを設定します。
	 *
	 * @param ignore 値より前のホワイトスペースを除去するかどうか
	 * @see Character#isWhitespace(char)
	 */
	public void setIgnoreLeadingWhitespaces(final boolean ignore) { this.ignoreLeadingWhitespaces = ignore; }

	/**
	 * 値より前のホワイトスペースを除去するかどうかを設定します。
	 *
	 * @param ignore 値より前のホワイトスペースを除去するかどうか
	 * @see Character#isWhitespace(char)
	 */
	public CsvConfig withIgnoreLeadingWhitespaces(final boolean ignore) {
		this.ignoreLeadingWhitespaces = ignore;
		return this;
	}

	/**
	 * 値より後ろのホワイトスペースを除去するかどうかを返します。
	 *
	 * @return 値より後ろのホワイトスペースを除去するかどうか
	 */
	public boolean isIgnoreTrailingWhitespaces() { return ignoreTrailingWhitespaces; }

	/**
	 * 値より後ろのホワイトスペースを除去するかどうかを設定します。
	 *
	 * @param ignore 値より後ろのホワイトスペースを除去するかどうか
	 * @see Character#isWhitespace(char)
	 */
	public void setIgnoreTrailingWhitespaces(final boolean ignore) { this.ignoreTrailingWhitespaces = ignore; }

	/**
	 * 値より後ろのホワイトスペースを除去するかどうかを設定します。
	 *
	 * @param ignore 値より後ろのホワイトスペースを除去するかどうか
	 * @see Character#isWhitespace(char)
	 */
	public CsvConfig withIgnoreTrailingWhitespaces(final boolean ignore) {
		this.ignoreTrailingWhitespaces = ignore;
		return this;
	}

	/**
	 * 空行を無視するかどうかを返します。
	 *
	 * @return 空行を無視するかどうか
	 */
	public boolean isIgnoreEmptyLines() { return ignoreEmptyLines; }

	/**
	 * 空行を無視するかどうかを設定します。
	 *
	 * @param ignore 空行を無視するかどうか
	 */
	public void setIgnoreEmptyLines(final boolean ignore) { this.ignoreEmptyLines = ignore; }

	/**
	 * 空行を無視するかどうかを設定します。
	 *
	 * @param ignore 空行を無視するかどうか
	 */
	public CsvConfig withIgnoreEmptyLines(final boolean ignore) {
		this.ignoreEmptyLines = ignore;
		return this;
	}

	/**
	 * 無視する行の正規表現パターン群を返します。
	 *
	 * @return 無視する行の正規表現パターン群
	 */
	public Pattern[] getIgnoreLinePatterns() { return ignoreLinePatterns; }

	/**
	 * 無視する行の正規表現パターン群を設定します。
	 *
	 * @param ignoreLinePatterns 無視する行の正規表現パターン群
	 */
	public void setIgnoreLinePatterns(final Pattern...ignoreLinePatterns) { this.ignoreLinePatterns = ignoreLinePatterns; }

	/**
	 * 無視する行の正規表現パターン群を設定します。
	 *
	 * @param ignoreLinePatterns 無視する行の正規表現パターン群
	 */
	public CsvConfig withIgnoreLinePatterns(final Pattern...ignoreLinePatterns) {
		this.ignoreLinePatterns = ignoreLinePatterns;
		return this;
	}

	/**
	 * ファイルの先頭から読飛ばす行数を返します。
	 *
	 * @return ファイルの先頭から読飛ばす行数
	 */
	public int getSkipLines() { return skipLines; }

	/**
	 * ファイルの先頭から読飛ばす行数を設定します。
	 *
	 * @param skipLines ファイルの先頭から読飛ばす行数
	 */
	public void setSkipLines(final int skipLines) { this.skipLines = skipLines; }

	/**
	 * ファイルの先頭から読飛ばす行数を設定します。
	 *
	 * @param skipLines ファイルの先頭から読飛ばす行数
	 */
	public CsvConfig withSkipLines(final int skipLines) {
		this.skipLines = skipLines;
		return this;
	}

	/**
	 * データ出力時の改行文字列を返します。
	 *
	 * @return データ出力時の改行文字列
	 */
	public String getLineSeparator() { return lineSeparator; }

	/**
	 * データ出力時の改行文字列を設定します。
	 *
	 * @param lineSeparator データ出力時の改行文字列
	 */
	public void setLineSeparator(final String lineSeparator) { this.lineSeparator = lineSeparator; }

	/**
	 * データ出力時の改行文字列を設定します。
	 *
	 * @param lineSeparator データ出力時の改行文字列
	 */
	public CsvConfig withLineSeparator(final String lineSeparator) {
		this.lineSeparator = lineSeparator;
		return this;
	}

	/**
	 * 囲み文字出力方法の種類を返します。
	 * 
	 * @return 囲み文字出力方法の種類
	 * @since 1.1
	 */
	public QuotePolicy getQuotePolicy() { return quotePolicy; }

	/**
	 * 囲み文字出力方法の種類を設定します。
	 * 
	 * @param quotePolicy 囲み文字出力方法の種類
	 * @throws IllegalArgumentException {@code quotePolicy} が {@code null} の場合
	 * @since 1.1
	 */
	public void setQuotePolicy(final QuotePolicy quotePolicy) {
		if (quotePolicy == null) {
			throw new IllegalArgumentException("QuotePolicy must not be null");
		}
		this.quotePolicy = quotePolicy;
	}

	/**
	 * 囲み文字出力方法の種類を設定します。
	 * 
	 * @param quotePolicy 囲み文字出力方法の種類
	 * @throws IllegalArgumentException {@code quotePolicy} が {@code null} の場合
	 * @since 1.1
	 */
	public CsvConfig withQuotePolicy(final QuotePolicy quotePolicy) {
		if (quotePolicy == null) {
			throw new IllegalArgumentException("QuotePolicy must not be null");
		}
		this.quotePolicy = quotePolicy;
		return this;
	}

	/**
	 * UTF-8 エンコーディングでの出力時に BOM (Byte Order Mark) を付与するかどうかを返します。
	 * 
	 * @return UTF-8 エンコーディングでの出力時に BOM (Byte Order Mark) を付与するかどうか
	 * @since 1.2.9
	 */
	public boolean isUtf8bomPolicy() { return utf8bomPolicy; }

	/**
	 * UTF-8 エンコーディングでの出力時に BOM (Byte Order Mark) を付与するかどうかを設定します。
	 * 
	 * @param utf8bomPolicy UTF-8 エンコーディングでの出力時に BOM (Byte Order Mark) を付与するかどうか
	 * @since 1.2.9
	 */
	public void setUtf8bomPolicy(final boolean utf8bomPolicy) { this.utf8bomPolicy = utf8bomPolicy; }
	
	/**
	 * UTF-8 エンコーディングでの出力時に BOM (Byte Order Mark) を付与するかどうかを設定します。
	 * 
	 * @param utf8bomPolicy UTF-8 エンコーディングでの出力時に BOM (Byte Order Mark) を付与するかどうか
	 * @since 1.2.9
	 */
	public CsvConfig withUtf8bomPolicy(final boolean utf8bomPolicy) {
		this.utf8bomPolicy = utf8bomPolicy;
		return this;
	}

	/**
	 * 可変項目数を許可するかどうかを返します。
	 * 
	 * @return 可変項目数を許可するかどうか
	 * @since 2.1
	 */
	public boolean isVariableColumns() { return variableColumns; }

	/**
	 * 可変項目数を許可するかどうかを設定します。
	 * 
	 * @param variableColumns 可変項目数を許可するかどうか
	 * @since 2.1
	 */
	public void setVariableColumns(final boolean variableColumns) { this.variableColumns = variableColumns; }

	/**
	 * 可変項目数を許可するかどうかを設定します。
	 * 
	 * @param variableColumns 可変項目数を許可するかどうか
	 * @since 2.1
	 */
	public CsvConfig withVariableColumns(final boolean variableColumns) {
		this.variableColumns = variableColumns;
		return this;
	}

	/**
	 * {@inheritDoc}
	 * @since 1.1
	 */
	@Override public CsvConfig clone() { return (CsvConfig) SerializationUtils.clone(this); }

}