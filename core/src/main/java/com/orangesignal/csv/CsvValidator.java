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

import java.util.List;
import java.util.Set;

/**
 * 区切り文字形式データトークンを検証します。
 * 
 * @author 杉澤 浩二
 * @since 2.0.0
 */
public interface CsvValidator {
/*
 * 項目数チェック
 * 最小項目数チェック
 * 最大項目数チェック
 * 
 * row 数チェック
 * 最小 row 数チェック
 * 最大 row 数チェック
 * 
 * 型チェック
 * URL, email, etc...
 */
//	Set<CsvViolation> validate(List<String> values);
	Set<CsvViolation> validate(List<CsvToken> tokens, CsvReader reader);

}