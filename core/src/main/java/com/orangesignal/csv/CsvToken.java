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

/**
 * 区切り文字形式データトークンのインタフェースです。
 *
 * @author 杉澤 浩二
 */
public interface CsvToken {

	/**
	 * トークンの値を保持します、
	 *
	 * @return トークンの値
	 */
	String getValue();

	/**
	 * トークンの開始物理行番号を取得します。
	 *
	 * @return トークンの開始物理行番号
	 */
	int getStartLineNumber();

	/**
	 * トークンの終了物理行番号を取得します。
	 *
	 * @return トークンの終了物理行番号
	 */
	int getEndLineNumber();

	/**
	 * トークンが囲み文字で囲まれていたかどうかを返します。
	 *
	 * @return トークンが囲み文字で囲まれていたかどうか
	 */
	boolean isEnclosed();

}