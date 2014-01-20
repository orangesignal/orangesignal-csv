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

package com.orangesignal.csv.manager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.zip.ZipOutputStream;

import com.orangesignal.jlha.LhaOutputStream;

/**
 * 区切り文字形式データの統合出力インタフェースです。
 *
 * @param <T> 区切り文字形式データの型
 * @author Koji Sugisawa
 */
public interface CsvSaver<T> {

	/**
	 * 区切り文字形式データのインスタンスを
	 * 変換して指定された文字出力ストリームへ書込みます。
	 *
	 * @param writer 文字出力ストリーム
	 * @throws IOException 入出力エラーが発生した場合
	 */
	void to(Writer writer) throws IOException;

	/**
	 * 区切り文字形式データのインスタンスを
	 * 変換して指定された出力ストリームへ指定されたエンコーディングで書込みます。
	 *
	 * @param out 出力ストリーム
	 * @param encoding エンコーディング
	 * @throws IOException 入出力エラーが発生した場合
	 */
	void to(OutputStream out, String encoding) throws IOException;

	/**
	 * 区切り文字形式データのインスタンスを
	 * 変換して指定された出力ストリームへプラットフォームのデフォルトエンコーディングで書込みます。
	 *
	 * @param out 出力ストリーム
	 * @throws IOException 入出力エラーが発生した場合
	 */
	void to(OutputStream out) throws IOException;

	/**
	 * 区切り文字形式データのインスタンスを
	 * 変換して指定されたファイルへ指定されたエンコーディングで書込みます。
	 *
	 * @param file 出力ファイル
	 * @param encoding エンコーディング
	 * @throws IOException 入出力エラーが発生した場合
	 */
	void to(File file, String encoding) throws IOException;

	/**
	 * 区切り文字形式データのインスタンスを
	 * 変換して指定されたファイルへプラットフォームのデフォルトエンコーディングで書込みます。
	 *
	 * @param file 出力ファイル
	 * @throws IOException 入出力エラーが発生した場合
	 */
	void to(File file) throws IOException;

	// ------------------------------------------------------------------------
	// static save (compress support)

	/**
	 * 区切り文字形式データのインスタンスを
	 * 変換して指定された LHA 出力ストリームへ指定されたエンコーディングで指定された LHA エントリを書込みます。
	 * 
	 * @param out LHA 出力ストリーム
	 * @param encoding エンコーディング
	 * @param entryName 作成する LHA エントリ名
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	void to(LhaOutputStream out, String encoding, String entryName) throws IOException;

	/**
	 * 区切り文字形式データのインスタンスを
	 * 変換して指定された LHA 出力ストリームへプラットフォームのデフォルトエンコーディングで指定された LHA エントリを書込みます。
	 * 
	 * @param out LHA 出力ストリーム
	 * @param entryName 作成する LHA エントリ名
	 * @throws IOException 入出力エラーが発生した場合
	 */
	void to(LhaOutputStream out, String entryName) throws IOException;

	/**
	 * 区切り文字形式データのインスタンスを
	 * 変換して指定された ZIP 出力ストリームへ指定されたエンコーディングで指定された ZIP エントリを書込みます。
	 * 
	 * @param out ZIP 出力ストリーム
	 * @param encoding エンコーディング
	 * @param entryName 作成する ZIP エントリ名
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	void to(ZipOutputStream out, String encoding, String entryName) throws IOException;

	/**
	 * 区切り文字形式データのインスタンスを
	 * 変換して指定された ZIP 出力ストリームへプラットフォームのデフォルトエンコーディングで指定された ZIP エントリを書込みます。
	 * 
	 * @param out ZIP 出力ストリーム
	 * @param entryName 作成する ZIP エントリ名
	 * @throws IOException 入出力エラーが発生した場合
	 */
	void to(ZipOutputStream out, String entryName) throws IOException;

}