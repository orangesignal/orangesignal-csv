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

package com.orangesignal.csv.bean;

import com.orangesignal.csv.annotation.CsvColumn;

/**
 * 常に {@code null} を返す項目値コンバーターの実装クラスを提供します。
 *
 * @author Koji Sugisawa
 * @since 1.2.2
 * @see CsvColumn
 */
public final class NullCsvValueConverter implements CsvValueConverter {

	/**
	 * デフォルトコンストラクタです。
	 */
	public NullCsvValueConverter() {}

	/**
	 * この実装は常に {@code null} を返します。
	 */
	@Override
	public Object convert(final String str, final Class<?> type) {
		return null;
	}

	/**
	 * この実装は常に {@code null} を返します。
	 */
	@Override
	public String convert(final Object value) {
		return null;
	}

}