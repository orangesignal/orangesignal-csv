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

package com.orangesignal.csv.entryfilters;

import java.util.Collection;
import java.util.zip.ZipEntry;

import com.orangesignal.jlha.LhaHeader;

/**
 * 指定されたエントリ名の接頭辞セットを使ってフィルタを適用するエントリフィルタの実装です。
 * 
 * @author 杉澤 浩二
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