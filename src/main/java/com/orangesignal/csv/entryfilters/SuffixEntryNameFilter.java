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

import java.util.Collection;
import java.util.zip.ZipEntry;

import com.orangesignal.jlha.LhaHeader;

/**
 * 指定されたエントリ名の接尾辞セットを使ってフィルタを適用するエントリフィルタの実装です。
 * 
 * @author Koji Sugisawa
 */
public class SuffixEntryNameFilter extends AbstractEntryFilter {

	private static final long serialVersionUID = -2053518605167417363L;

	/**
	 * 受け入れるエントリ名の接尾辞セットを保持します。
	 */
	private String[] suffixes;

	/**
	 * 大文字と小文字を区別するかどうかを保持します。
	 */
	private boolean ignoreCase;

	/**
	 * 指定された受け入れるエントリ名の接尾辞で大文字と小文字を区別する、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param suffix 受け入れるエントリ名の接尾辞
	 */
	public SuffixEntryNameFilter(final String suffix) {
		this(new String[]{ suffix }, false);
	}

	/**
	 * 指定された受け入れるエントリ名の接尾辞で、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param suffix 受け入れるエントリ名の接尾辞
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 */
	public SuffixEntryNameFilter(final String suffix, final boolean ignoreCase) {
		this(new String[]{ suffix }, ignoreCase);
	}

	/**
	 * 指定された受け入れるエントリ名の接尾辞セットで大文字と小文字を区別する、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param suffixes 受け入れるエントリ名の接尾辞セット
	 * @throws IllegalArgumentException {@code names} が {@code null} の場合
	 */
	public SuffixEntryNameFilter(final String[] suffixes) {
		this(suffixes, false);
	}

	/**
	 * 指定された受け入れるエントリ名の接尾辞セットで、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param suffixes 受け入れるエントリ名の接尾辞セット
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException {@code names} が {@code null} の場合
	 */
	public SuffixEntryNameFilter(final String[] suffixes, final boolean ignoreCase) {
		if (suffixes == null) {
			throw new IllegalArgumentException("Suffixes must not be null");
		}
		final String[] copy = new String[suffixes.length];
		System.arraycopy(suffixes, 0, copy, 0, copy.length);
		this.suffixes = copy;
		this.ignoreCase = ignoreCase;
	}

	/**
	 * 指定された受け入れるエントリ名の接尾辞セットで大文字と小文字を区別する、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param suffixes 受け入れるエントリ名の接尾辞セット
	 * @throws IllegalArgumentException {@code names} が {@code null} の場合
	 */
	public SuffixEntryNameFilter(final Collection<String> suffixes) {
		this(suffixes, false);
	}

	/**
	 * 指定された受け入れるエントリ名の接尾辞セットで、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param suffixes 受け入れるエントリ名の接尾辞セット
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException {@code names} が {@code null} の場合
	 */
	public SuffixEntryNameFilter(final Collection<String> suffixes, final boolean ignoreCase) {
		if (suffixes == null) {
			throw new IllegalArgumentException("Suffixes must not be null");
		}
		this.suffixes = suffixes.toArray(new String[0]);
		this.ignoreCase = ignoreCase;
	}

	/**
	 * 指定された ZIP エントリをテストし、エントリが受け入れられる場合は {@code true} そうでない場合は {@code false} を返します。
	 * 
	 * @param entry テストする ZIP エントリ
	 * @return エントリが受け入れられる場合は {@code true}、そうでない場合は {@code false}
	 */
	@Override
	public boolean accept(final ZipEntry entry) {
		return accept(entry.getName());
	}

	/**
	 * 指定された LHA エントリをテストし、エントリが受け入れられる場合は {@code true} そうでない場合は {@code false} を返します。
	 * 
	 * @param entry テストする LHA エントリ
	 * @return エントリが受け入れられる場合は {@code true}、そうでない場合は {@code false}
	 */
	@Override
	public boolean accept(final LhaHeader entry) {
		return accept(entry.getPath());
	}

	/**
	 * 指定されたエントリ名をテストし、エントリ名が受け入れられる場合は {@code true} そうでない場合は {@code false} を返します。
	 * 
	 * @param path テストするエントリ名
	 * @return エントリが受け入れられる場合は {@code true}、そうでない場合は {@code false}
	 */
	private boolean accept(final String path) {
		for (final String suffix : suffixes) {
			final int len = suffix.length();
			final boolean b = path.regionMatches(ignoreCase, path.length() - len, suffix, 0, len);
			if (b) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append('(');
		if (suffixes != null) {
			final int length = suffixes.length;
			for (int i = 0; i < length; i++) {
				if (i > 0) {
					sb.append(',');
				}
				sb.append(suffixes[i]);
			}
		}
		sb.append(')');
		return sb.toString();
	}

}