/*
 * Copyright (c) 2009 OrangeSignal.com All rights reserved.
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

package com.orangesignal.csv.entity;

import java.util.Date;

import com.orangesignal.csv.annotation.CsvColumn;
import com.orangesignal.csv.annotation.CsvEntity;

@CsvEntity(header = true)
public class Price2 {

	public Price2() {}

	public Price2(String symbol, String name, Number price, Number volume, Date date) {
		this.symbol = symbol;
		this.name = name;
		this.date = date;
		this.price = price;
		this.volume = volume;
	}

	public String symbol;

	public Date date;

	public String name;

	@CsvColumn(position = 2, name = "価格", format = "#,##0", language = "ja", country = "JP")
	public Number price;

	@CsvColumn(position = 3, name = "出来高")
	public Number volume;

}
