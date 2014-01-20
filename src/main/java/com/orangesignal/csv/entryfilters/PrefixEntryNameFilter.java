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
 * 指定されたエントリ名の接頭辞セットを使ってフィルタを適用するエントリフィルタの実装です。
 * 
 * @author Koji Sugisawa
 */
public class PrefixEntryNameFilter extends AbstractEntryFilter {

	private static final long serialVersionUID = -8252513785505493289L;

	/**
	 * 受け入れるエントリ名の接頭辞セットを保持します。
	 */
	private String[] prefixes;

	/**
	 * 大文字と小文字を区別するかどうかを保持します。
	 */
	private boolean ignoreCase;

	/**
	 * 指定された受け入れるエントリ名の接頭辞で大文字と小文字を区別する、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param prefix 受け入れるエントリ名の接頭辞
	 */
	public PrefixEntryNameFilter(final String prefix) {
		this(new String[]{ prefix }, false);
	}

	/**
	 * 指定された受け入れるエントリ名の接頭辞で、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param prefix 受け入れるエントリ名の接頭辞
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 */
	public PrefixEntryNameFilter(final String prefix, final boolean ignoreCase) {
		this(new String[]{ prefix }, ignoreCase);
	}

	/**
	 * 指定された受け入れるエントリ名の接頭辞セットで大文字と小文字を区別する、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param prefixes 受け入れるエントリ名の接頭辞セット
	 * @throws IllegalArgumentException {@code names} が {@code null} の場合
	 */
	public PrefixEntryNameFilter(final String[] prefixes) {
		this(prefixes, false);
	}

	/**
	 * 指定された受け入れるエントリ名の接頭辞セットで、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param prefixes 受け入れるエントリ名の接頭辞セット
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException {@code names} が {@code null} の場合
	 */
	public PrefixEntryNameFilter(final String[] prefixes, final boolean ignoreCase) {
		if (prefixes == null) {
			throw new IllegalArgumentException("Prefixes must not be null");
		}
		final String[] copy = new String[prefixes.length];
		System.arraycopy(prefixes, 0, copy, 0, copy.length);
		this.prefixes = copy;
		this.ignoreCase = ignoreCase;
	}

	/**
	 * 指定された受け入れるエントリ名の接頭辞セットで大文字と小文字を区別する、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param prefixes 受け入れるエントリ名の接頭辞セット
	 * @throws IllegalArgumentException {@code names} が {@code null} の場合
	 */
	public PrefixEntryNameFilter(final Collection<String> prefixes) {
		this(prefixes, false);
	}

	/**
	 * 指定された受け入れるエントリ名の接頭辞セットで、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param prefixes 受け入れるエントリ名の接頭辞セット
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException {@code names} が {@code null} の場合
	 */
	public PrefixEntryNameFilter(final Collection<String> prefixes, final boolean ignoreCase) {
		if (prefixes == null) {
			throw new IllegalArgumentException("Prefixes must not be null");
		}
		this.prefixes = prefixes.toArray(new String[0]);
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
		for (final String prefix : prefixes) {
			final boolean b = path.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
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
		if (prefixes != null) {
			final int length = prefixes.length;
			for (int i = 0; i < length; i++) {
				if (i > 0) {
					sb.append(',');
				}
				sb.append(prefixes[i]);
			}
		}
		sb.append(')');
		return sb.toString();
	}

}