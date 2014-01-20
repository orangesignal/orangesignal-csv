/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.orangesignal.csv.entity;

import java.util.Date;

import com.orangesignal.csv.annotation.CsvColumn;
import com.orangesignal.csv.annotation.CsvEntity;

/**
 * @author Koji Sugisawa
 */
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
