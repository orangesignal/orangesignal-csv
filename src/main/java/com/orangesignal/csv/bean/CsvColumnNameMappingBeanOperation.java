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

package com.orangesignal.csv.bean;

import java.text.Format;
import java.util.Map;

import com.orangesignal.csv.filters.CsvNamedValueFilter;

/**
 * 区切り文字形式データの項目名を基準とする Java プログラム要素と区切り文字形式データアクセス操作のインタフェースを提供します。
 * 
 * @param <H> Java プログラム要素と区切り文字形式データアクセス操作クラスの型
 * @author 杉澤 浩二
 * @since 1.4.0
 */
public interface CsvColumnNameMappingBeanOperation<H> {

	/**
	 * 指定された項目名と Java プログラム要素のフィールド名をマップへ追加します。
	 * 
	 * @param column 項目名
	 * @param field Java プログラム要素のフィールド名
	 * @return このオブジェクトへの参照
	 */
	H column(String column, String field);

	/**
	 * 指定された項目名と Java プログラム要素のフィールド名をマップへ追加します。
	 * 
	 * @param column 項目名
	 * @param field Java プログラム要素のフィールド名
	 * @param format フィールドを処理するフォーマットオブジェクト (オプション)
	 * @return このオブジェクトへの参照
	 */
	H column(String column, String field, Format format);

	/**
	 * 項目名と Java プログラム要素のフィールド名のマップを設定します。
	 * 
	 * @param columnMapping 項目名と Java プログラム要素のフィールド名のマップ
	 * @throws IllegalArgumentException {@code columnMapping} が {@code null} の場合
	 */
	void setColumnMapping(Map<String, String> columnMapping);

	/**
	 * 項目名と Java プログラム要素のフィールド名のマップを設定します。
	 * 
	 * @param columnMapping 項目名と Java プログラム要素のフィールド名のマップ
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException {@code columnMapping} が {@code null} の場合
	 */
	H columnMapping(Map<String, String> columnMapping);

	/**
	 * 区切り文字形式データフィルタを設定します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 * @return このオブジェクトへの参照
	 */
	H filter(CsvNamedValueFilter filter);

}