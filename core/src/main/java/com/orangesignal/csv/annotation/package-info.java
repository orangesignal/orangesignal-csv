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

/**
 * Java プログラム要素から区切り文字形式データを使用するための注釈を定義します。
 *
 * <pre>
 * 例:
 * 
 * &#064;CsvEntity(header = true)
 * public class Price {
 *
 *     &#064;CsvColumn(position = 0, name = "シンボル")
 *     String symbol;
 *
 *     &#064;CsvColumn(position = 1, name = "名称")
 *     String name;
 *
 *     &#064;CsvColumns({
 *         &#064;CsvColumn(position = 7, name = "日付", format = "yyyy/MM/dd"),
 *         &#064;CsvColumn(position = 8, name = "時刻", format = "HH:mm:ss")
 *     })
 *     Date date;
 *
 *     &#064;CsvColumn(position = 2, name = "始値")
 *     Number open;
 *
 *     &#064;CsvColumn(position = 3, name = "高値")
 *     Number high;
 *
 *     &#064;CsvColumn(position = 4, name = "安値")
 *     Number low;
 *
 *     &#064;CsvColumn(position = 5, name = "終値")
 *     Number close;
 *
 *     &#064;CsvColumn(position = 6, name = "出来高")
 *     Number volume;
 *
 * }
 * </pre>
 * 
 * @author 杉澤 浩二
 */
package com.orangesignal.csv.annotation;