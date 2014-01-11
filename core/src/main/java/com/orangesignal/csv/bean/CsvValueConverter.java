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

/**
 * オブジェクトと文字列を変換する区切り文字形式データの項目値変換インターフェースです。
 * 
 * @author 杉澤 浩二
 */
public interface CsvValueConverter {

	/**
	 * 指定された文字列を指定された型のオブジェクトへ変換して返します。<p>
	 * 指定された文字列が {@code null} や空文字列の場合に、どのような値が返されるかは実装に依存します。
	 * 
	 * @param str 変換する文字列
	 * @param type 変換する型
	 * @return 変換されたオブジェクト
	 * @throws IllegalArgumentException 変換に失敗した場合
	 */
	Object convert(String str, Class<?> type);

	/**
	 * 指定されたオブジェクトを文字列へ変換して返します。
	 * 
	 * @param value 変換するオブジェクト
	 * @return 変換された文字列
	 * @throws IllegalArgumentException 変換に失敗した場合
	 */
	String convert(Object value);

}