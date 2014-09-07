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
@CsvEntity
public class Travel {

	@CsvColumn(name = "CODE")
	public String cod;

	@CsvColumn(name = "DATE", format = "yyyy/MM/dd")
	public Date date;

	public Travel() {}

	public Travel(String cod, Date date) {
		this.cod = cod;
		this.date = date;
	}

}
