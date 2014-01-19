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

import java.util.zip.ZipEntry;

import com.orangesignal.jlha.LhaHeader;

/**
 * ディレクトリエントリをフィルタするエントリフィルタの実装です。
 * ディレクトリエントリはエントリ名の後ろがスラッシュ「/」となっているエントリです。
 * 
 * @author 杉澤 浩二
 */
public class DirectoryEntryFilter extends AbstractEntryFilter {

	private static final long serialVersionUID = 7352823190771258451L;

	/**
	 * デフォルトコンストラクタです。
	 */
	public DirectoryEntryFilter() {}

	/**
	 * 指定された ZIP エントリをテストし、エントリがディレクトリエントリの場合は {@code false} そうでない場合は {@code true} を返します。
	 * 
	 * @param entry テストする ZIP エントリ
	 * @return エントリがディレクトリエントリの場合は {@code false}、そうでない場合は {@code true}
	 */
	@Override
	public boolean accept(final ZipEntry entry) {
		return !entry.isDirectory();
	}

	/**
	 * 指定された LHA エントリをテストし、エントリがディレクトリエントリの場合は {@code false} そうでない場合は {@code true} を返します。
	 * 
	 * @param entry テストする LHA エントリ
	 * @return エントリがディレクトリエントリの場合は {@code false}、そうでない場合は {@code true}
	 */
	@Override
	public boolean accept(final LhaHeader entry) {
		return !entry.getPath().endsWith("/");
	}

}