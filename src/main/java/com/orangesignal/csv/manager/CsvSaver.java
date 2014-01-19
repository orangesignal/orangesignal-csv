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
 * @author 杉澤 浩二
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