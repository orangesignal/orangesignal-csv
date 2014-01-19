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
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.orangesignal.csv.LhaEntryFilter;
import com.orangesignal.csv.ZipEntryFilter;
import com.orangesignal.csv.filters.BeanFilter;
import com.orangesignal.csv.handlers.BeanOrder;
import com.orangesignal.jlha.LhaFile;
import com.orangesignal.jlha.LhaInputStream;

/**
 * 区切り文字形式データの統合入力インタフェースです。
 *
 * @param <T> 区切り文字形式データの型
 * @author 杉澤 浩二
 */
public interface CsvLoader<T> {

	/**
	 * Java プログラム要素フィルタを設定します。
	 * 
	 * @param filter Java プログラム要素フィルタ
	 * @return このオブジェクトへの参照
	 * @since 1.3.0
	 */
	CsvLoader<T> filter(BeanFilter filter);

	/**
	 * 並び替え条件を設定します。
	 * 
	 * @param orders 並び替え条件
	 * @return このオブジェクトへの参照
	 * @since 1.3.0
	 */
	CsvLoader<T> order(BeanOrder... orders);

	/**
	 * 取得データの開始位置を設定します。
	 * 
	 * @param offset 取得データの開始位置
	 * @return このオブジェクトへの参照
	 * @since 1.3.0
	 */
	CsvLoader<T> offset(int offset);

	/**
	 * 取得データの限度数を設定します。
	 * 
	 * @param limit 取得データの限度数
	 * @return このオブジェクトへの参照
	 * @since 1.3.0
	 */
	CsvLoader<T> limit(int limit);

	/**
	 * 指定された文字入力ストリームを読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param reader 文字入力ストリーム
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	List<T> from(Reader reader) throws IOException;

	/**
	 * 指定された入力ストリームを指定されたエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in 入力ストリーム
	 * @param encoding エンコーディング
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	List<T> from(InputStream in, String encoding) throws IOException;

	/**
	 * 指定された入力ストリームをプラットフォームのデフォルトエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in 入力ストリーム
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	List<T> from(InputStream in) throws IOException;

	/**
	 * 指定されたファイルを指定されたエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param file 入力ファイル
	 * @param encoding エンコーディング
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	List<T> from(File file, String encoding) throws IOException;

	/**
	 * 指定されたファイルをプラットフォームのデフォルトエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param file 入力ファイル
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	List<T> from(File file) throws IOException;

	// ------------------------------------------------------------------------
	// static load (compress support)

	/**
	 * 指定された LHA 入力ストリームから指定されたフィルタの基準を満たす LHA エントリを指定されたエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in LHA 入力ストリーム
	 * @param encoding エンコーディング
	 * @param filter LHA エントリフィルタ
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	List<T> from(LhaInputStream in, String encoding, LhaEntryFilter filter) throws IOException;

	/**
	 * 指定された LHA 入力ストリームからすべての LHA エントリを指定されたエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in LHA 入力ストリーム
	 * @param encoding エンコーディング
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	List<T> from(LhaInputStream in, String encoding) throws IOException;

	/**
	 * 指定された LHA 入力ストリームから指定されたフィルタの基準を満たす LHA エントリをプラットフォームのデフォルトエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in LHA 入力ストリーム
	 * @param filter LHA エントリフィルタ
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	List<T> from(LhaInputStream in, LhaEntryFilter filter) throws IOException;

	/**
	 * 指定された LHA 入力ストリームからすべての LHA エントリをプラットフォームのデフォルトエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in LHA 入力ストリーム
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	List<T> from(LhaInputStream in) throws IOException;

	/**
	 * 指定された LHA ファイルから指定されたフィルタの基準を満たす LHA エントリを指定されたエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param lhaFile LHA ファイル
	 * @param encoding エンコーディング
	 * @param filter LHA エントリフィルタ
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	List<T> from(LhaFile lhaFile, String encoding, LhaEntryFilter filter) throws IOException;

	/**
	 * 指定された LHA ファイルから指定されたすべての LHA エントリを指定されたエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param lhaFile LHA ファイル
	 * @param encoding エンコーディング
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	List<T> from(LhaFile lhaFile, String encoding) throws IOException;

	/**
	 * 指定された LHA ファイルから指定されたフィルタの基準を満たす LHA エントリをプラットフォームのデフォルトエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param lhaFile LHA ファイル
	 * @param filter LHA エントリフィルタ
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	List<T> from(LhaFile lhaFile, LhaEntryFilter filter) throws IOException;

	/**
	 * 指定された LHA ファイルからすべての LHA エントリをプラットフォームのデフォルトエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param lhaFile LHA ファイル
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	List<T> from(LhaFile lhaFile) throws IOException;

	/**
	 * 指定された ZIP 入力ストリームから指定されたフィルタの基準を満たす ZIP エントリを指定されたエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in ZIP 入力ストリーム
	 * @param encoding エンコーディング
	 * @param filter ZIP エントリフィルタ
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	List<T> from(ZipInputStream in, String encoding, ZipEntryFilter filter) throws IOException;

	/**
	 * 指定された ZIP 入力ストリームからすべての ZIP エントリを指定されたエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in ZIP 入力ストリーム
	 * @param encoding エンコーディング
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	List<T> from(ZipInputStream in, String encoding) throws IOException;

	/**
	 * 指定された ZIP 入力ストリームから指定されたフィルタの基準を満たす ZIP エントリをプラットフォームのデフォルトエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in ZIP 入力ストリーム
	 * @param filter ZIP エントリフィルタ
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	List<T> from(ZipInputStream in, ZipEntryFilter filter) throws IOException;

	/**
	 * 指定された ZIP 入力ストリームからすべての ZIP エントリをプラットフォームのデフォルトエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param in ZIP 入力ストリーム
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	List<T> from(ZipInputStream in) throws IOException;

	/**
	 * 指定された ZIP ファイルから指定されたフィルタの基準を満たす ZIP エントリを指定されたエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param zipFile ZIP ファイル
	 * @param encoding エンコーディング
	 * @param filter ZIP エントリフィルタ
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	List<T> from(ZipFile zipFile, String encoding, ZipEntryFilter filter) throws IOException;

	/**
	 * 指定された ZIP ファイルからすべての ZIP エントリを指定されたエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param zipFile ZIP ファイル
	 * @param encoding エンコーディング
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 * @since 1.2.1
	 */
	List<T> from(ZipFile zipFile, String encoding) throws IOException;

	/**
	 * 指定された ZIP ファイルから指定されたフィルタの基準を満たす ZIP エントリをプラットフォームのデフォルトエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param zipFile ZIP ファイル
	 * @param filter ZIP エントリフィルタ
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	List<T> from(ZipFile zipFile, ZipEntryFilter filter) throws IOException;

	/**
	 * 指定された ZIP ファイルからすべての ZIP エントリをプラットフォームのデフォルトエンコーディングで読込み、
	 * 変換された区切り文字形式データのインスタンスを返します。
	 *
	 * @param zipFile ZIP ファイル
	 * @return 変換された区切り文字形式データのインスタンス
	 * @throws IOException 入出力エラーが発生した場合
	 */
	List<T> from(ZipFile zipFile) throws IOException;

}