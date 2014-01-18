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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import com.orangesignal.csv.annotation.CsvColumn;
import com.orangesignal.csv.annotation.CsvColumns;
import com.orangesignal.csv.annotation.CsvEntity;

@CsvEntity(header = true)
public class Price {

	public Price() {}

	public Price(String symbol, String name, Number price, Number volume, Date date) {
		this.symbol = symbol;
		this.name = name;
		this.date = date;
		this.price = price;
		this.volume = volume;
	}

	@CsvColumn(position = 0, name = "シンボル")
	@NotNull
	public String symbol;

	@CsvColumns({
		@CsvColumn(position = 4, name = "日付", format = "yyyy/MM/dd", language = "ja", country = "JP", timezone = "Asia/Tokyo"),
		@CsvColumn(position = 5, name = "時刻", format = "HH:mm:ss", language = "ja", country = "JP", timezone = "Asia/Tokyo")
	})
	@NotNull
	@Past
	public Date date;

	@CsvColumns({ @CsvColumn(position = 1, name = "名称") })
	@NotNull
	public String name;

	@CsvColumn(position = 2, name = "価格", format = "#,##0", language = "ja", country = "JP")
	@NotNull
	public Number price;

	@CsvColumn(position = 3, name = "出来高")
	@NotNull
	public Number volume;

}
