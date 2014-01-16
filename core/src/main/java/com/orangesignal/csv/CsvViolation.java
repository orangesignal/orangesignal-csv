/*
 * Copyright (c) 2014 OrangeSignal.com All rights reserved.
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
 * 区切り文字形式データ違反の説明情報を提供します。
 *
 * @author 杉澤 浩二
 * @since 2.0.0
 */
public interface CsvViolation {

	/**
	 * 違反の詳細な説明を返します。
	 * 
	 * @return 違反の詳細な説明
	 */
	String getMessage();

	/**
	 * 違反した区切り文字形式データトークンを返します。
	 * 
	 * @return 違反した区切り文字形式データトークン
	 */
	CsvToken getInvalidValue();

}