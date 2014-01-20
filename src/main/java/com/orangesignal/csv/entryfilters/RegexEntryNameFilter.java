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

package com.orangesignal.csv.entryfilters;

import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import com.orangesignal.jlha.LhaHeader;

/**
 * 指定されたエントリ名の正規表現パターンを使ってフィルタを適用するエントリフィルタの実装です。
 * 
 * @author Koji Sugisawa
 */
public class RegexEntryNameFilter extends AbstractEntryFilter {

	private static final long serialVersionUID = 4468604519613025495L;

	/**
	 * 受け入れるエントリ名のエントリ名の正規表現パターンを保持します。
	 */
	private Pattern pattern;

	/**
	 * 指定された受け入れるエントリ名の正規表現パターンで大文字と小文字を区別する、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param pattern 受け入れるエントリ名の正規表現パターン
	 */
	public RegexEntryNameFilter(final String pattern) {
		this(pattern, 0);
	}

	/**
	 * 指定された受け入れるエントリ名の正規表現パターンで、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param pattern 受け入れるエントリ名の正規表現パターン
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 */
	public RegexEntryNameFilter(final String pattern, final boolean ignoreCase) {
		this(pattern, ignoreCase ? Pattern.CASE_INSENSITIVE : 0);
	}

	/**
	 * 指定された受け入れるエントリ名の正規表現パターンで、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param pattern 受け入れるエントリ名の正規表現パターン
	 * @param flags マッチフラグ
	 */
	public RegexEntryNameFilter(final String pattern, final int flags) {
		if (pattern == null) {
			throw new IllegalArgumentException("Pattern must not be null");
		}
		this.pattern = Pattern.compile(pattern, flags);
	}

	/**
	 * 指定された受け入れるエントリ名の正規表現パターンで、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param pattern 受け入れるエントリ名の正規表現パターン
	 */
	public RegexEntryNameFilter(final Pattern pattern) {
		if (pattern == null) {
			throw new IllegalArgumentException("Pattern must not be null");
		}
		this.pattern = pattern;
	}

	/**
	 * 指定された ZIP エントリをテストし、エントリが受け入れられる場合は {@code true} そうでない場合は {@code false} を返します。
	 * 
	 * @param entry テストする ZIP エントリ
	 * @return エントリが受け入れられる場合は {@code true}、そうでない場合は {@code false}
	 */
	@Override
	public boolean accept(final ZipEntry entry) {
		return pattern.matcher(entry.getName()).matches();
	}

	/**
	 * 指定された LHA エントリをテストし、エントリが受け入れられる場合は {@code true} そうでない場合は {@code false} を返します。
	 * 
	 * @param entry テストする LHA エントリ
	 * @return エントリが受け入れられる場合は {@code true}、そうでない場合は {@code false}
	 */
	@Override
	public boolean accept(final LhaHeader entry) {
		return pattern.matcher(entry.getPath()).matches();
	}

}