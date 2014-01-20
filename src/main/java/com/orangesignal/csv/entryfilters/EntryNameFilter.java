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
 * 指定されたエントリ名のセットを使ってフィルタを適用するエントリフィルタの実装です。
 * 
 * @author Koji Sugisawa
 */
public class EntryNameFilter extends AbstractEntryFilter {

	private static final long serialVersionUID = -147779242205375587L;

	/**
	 * 受け入れるエントリ名のセットを保持します。
	 */
	private String[] names;

	/**
	 * 大文字と小文字を区別するかどうかを保持します。
	 */
	private boolean ignoreCase;

	/**
	 * 指定された受け入れるエントリ名で大文字と小文字を区別する、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param name 受け入れるエントリ名
	 */
	public EntryNameFilter(final String name) {
		this(new String[]{ name }, false);
	}

	/**
	 * 指定された受け入れるエントリ名で、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param name 受け入れるエントリ名
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 */
	public EntryNameFilter(final String name, final boolean ignoreCase) {
		this(new String[]{ name }, ignoreCase);
	}

	/**
	 * 指定された受け入れるエントリ名のセットで大文字と小文字を区別する、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param names 受け入れるエントリ名のセット
	 * @throws IllegalArgumentException {@code names} が {@code null} の場合
	 */
	public EntryNameFilter(final String[] names) {
		this(names, false);
	}

	/**
	 * 指定された受け入れるエントリ名のセットで、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param names 受け入れるエントリ名のセット
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException {@code names} が {@code null} の場合
	 */
	public EntryNameFilter(final String[] names, final boolean ignoreCase) {
		if (names == null) {
			throw new IllegalArgumentException("Names must not be null");
		}
		final String[] copy = new String[names.length];
		System.arraycopy(names, 0, copy, 0, copy.length);
		this.names = copy;
		this.ignoreCase = ignoreCase;
	}

	/**
	 * 指定された受け入れるエントリ名のセットで大文字と小文字を区別する、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param names 受け入れるエントリ名のセット
	 * @throws IllegalArgumentException {@code names} が {@code null} の場合
	 */
	public EntryNameFilter(final Collection<String> names) {
		this(names, false);
	}

	/**
	 * 指定された受け入れるエントリ名のセットで、このクラスのインスタンスを構築するコンストラクタです。
	 * 
	 * @param names 受け入れるエントリ名のセット
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException {@code names} が {@code null} の場合
	 */
	public EntryNameFilter(final Collection<String> names, final boolean ignoreCase) {
		if (names == null) {
			throw new IllegalArgumentException("Names must not be null");
		}
		this.names = names.toArray(new String[0]);
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
		for (final String name : names) {
			final boolean b = ignoreCase ? path.equalsIgnoreCase(name) : path.equals(name);
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
		if (names != null) {
			final int length = names.length;
			for (int i = 0; i < length; i++) {
				if (i > 0) {
					sb.append(',');
				}
				sb.append(names[i]);
			}
		}
		sb.append(')');
		return sb.toString();
	}

}