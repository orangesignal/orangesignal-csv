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

import java.util.zip.ZipEntry;

import com.orangesignal.jlha.LhaHeader;

/**
 * ディレクトリエントリをフィルタするエントリフィルタの実装です。
 * ディレクトリエントリはエントリ名の後ろがスラッシュ「/」となっているエントリです。
 * 
 * @author Koji Sugisawa
 */
public class DirectoryEntryFilter extends AbstractEntryFilter {

	private static final long serialVersionUID = 7352823190771258451L;

	/**
	 * デフォルトコンストラクタです。
	 */
	public DirectoryEntryFilter() {
	}

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