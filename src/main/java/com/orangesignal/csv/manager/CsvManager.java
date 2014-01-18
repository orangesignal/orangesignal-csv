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

import java.util.List;

import com.orangesignal.csv.CsvConfig;

/**
 * 区切り文字形式データの統合アクセスインタフェースです。
 *
 * @author 杉澤 浩二
 */
public interface CsvManager {

	/**
	 * 区切り文字形式情報を設定します。
	 *
	 * @param cfg 区切り文字形式情報
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException {@code cfg} が {@code null} の場合
	 */
	CsvManager config(CsvConfig cfg);

	/**
	 * 区切り文字形式データ統合入力インタフェースを構築して返します。
	 * 
	 * @param type 区切り文字形式データの型
	 * @return 区切り文字形式データの統合入力インタフェース
	 * @throws IllegalArgumentException {@code type} が {@code null} または不正な場合
	 */
	<T> CsvLoader<T> load(Class<T> type);

	/**
	 * 区切り文字形式データ統合出力インタフェースを構築して返します。
	 * 
	 * @param list 区切り文字形式データのリスト
	 * @param type 区切り文字形式データの型
	 * @return 区切り文字形式データの統合出力インタフェース
	 * @throws IllegalArgumentException {@code list} または {@code type} が {@code null} または不正な場合
	 */
	<T> CsvSaver<T> save(List<T> list, Class<T> type);

}