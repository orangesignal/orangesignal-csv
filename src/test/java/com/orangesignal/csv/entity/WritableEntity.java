/*
 * Copyright 2014 the original author or authors.
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

import com.orangesignal.csv.annotation.CsvColumn;
import com.orangesignal.csv.annotation.CsvColumnAccess;
import com.orangesignal.csv.annotation.CsvColumns;
import com.orangesignal.csv.annotation.CsvEntity;

/**
 * @author Koji Sugisawa
 */
@CsvEntity(header = true)
public class WritableEntity {

	@CsvColumns({
		@CsvColumn(name = "col1", access = CsvColumnAccess.READ),
		@CsvColumn(name = "col2"),
		@CsvColumn(name = "col3")
	})
	public String[] array;

	@CsvColumn(name = "col4", access = CsvColumnAccess.READ)
	public String str;

}