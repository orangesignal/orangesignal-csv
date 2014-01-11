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

package com.orangesignal.csv;

import java.io.IOException;

/**
 * 区切り文字形式データのデータアクセスインターフェースです。
 * 
 * @param <T> 区切り文字形式データの型
 * @author 杉澤 浩二
 */
public interface CsvHandler<T> {

	/**
	 * 区切り文字形式入力ストリームを読込んで区切り文字形式データを返します。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @return 区切り文字形式データ
	 * @throws IOException 入出力例外が発生した場合
	 * @throws IllegalArgumentException パラメータが不正な場合
	 */
	T load(CsvReader reader) throws IOException;

	/**
	 * 指定された区切り文字形式データを区切り文字形式出力ストリームへ書込みます。
	 * 
	 * @param obj 区切り文字形式データ
	 * @param writer 区切り文字形式出力ストリーム
	 * @throws IOException 入出力例外が発生した場合
	 * @throws IllegalArgumentException パラメータが不正な場合
	 */
	void save(T obj, CsvWriter writer) throws IOException;

}