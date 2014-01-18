/*
 * Copyright (c) 2009-2013 OrangeSignal.com All rights reserved.
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

package com.orangesignal.csv.filters;

import java.util.List;

/**
 * 区切り文字形式データの項目名リストと値リストでフィルタする区切り文字形式データフィルタのインタフェースです。
 * 
 * @author 杉澤 浩二
 * @since 1.2.3
 */
public interface CsvNamedValueFilter {

	/**
	 * 指定された区切り文字形式データの値リストが含まれる必要があるかどうかを判定します。
	 * 
	 * @param header 区切り文字形式データの項目名リスト
	 * @param values 区切り文字形式データの値リスト
	 * @return {@code values} が含まれる必要がある場合は {@code true}
	 */
	boolean accept(List<String> header, List<String> values);

}