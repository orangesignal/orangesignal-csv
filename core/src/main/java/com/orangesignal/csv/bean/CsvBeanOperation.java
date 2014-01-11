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

import com.orangesignal.csv.filters.CsvNamedValueFilter;

/**
 * Java プログラム要素と区切り文字形式データアクセス操作のインタフェースを提供します。
 * 
 * @param <H> Java プログラム要素と区切り文字形式データアクセス操作クラスの型
 * @author 杉澤 浩二
 * @since 1.4.0
 */
public interface CsvBeanOperation<H> {

	/**
	 * Java プログラム要素へデータを設定する名前群を設定します。
	 * 
	 * @param names Java プログラム要素へデータを設定する名前群
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException Java プログラム要素へデータを設定しない名前群が存在する場合
	 */
	H includes(String... names);

	/**
	 * Java プログラム要素へデータを設定しない名前群を設定します。
	 * 
	 * @param names Java プログラム要素へデータを設定しない名前群
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException Java プログラム要素へデータを設定する名前群が存在する場合
	 */
	H excludes(String... names);

	/**
	 * 区切り文字形式データフィルタを設定します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 * @return このオブジェクトへの参照
	 */
	H filter(CsvNamedValueFilter filter);

}