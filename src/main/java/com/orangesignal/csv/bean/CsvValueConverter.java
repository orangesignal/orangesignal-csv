/*
 * Copyright 2013 the original author or authors.
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

/**
 * オブジェクトと文字列を変換する区切り文字形式データの項目値変換インターフェースです。
 * 
 * @author Koji Sugisawa
 */
public interface CsvValueConverter {

	/**
	 * 指定された文字列を指定された型のオブジェクトへ変換して返します。<p>
	 * 指定された文字列が {@code null} や空文字列の場合に、どのような値が返されるかは実装に依存します。
	 * 
	 * @param str 変換する文字列
	 * @param type 変換する型
	 * @return 変換されたオブジェクト
	 * @throws IllegalArgumentException 変換に失敗した場合
	 */
	Object convert(String str, Class<?> type);

	/**
	 * 指定されたオブジェクトを文字列へ変換して返します。
	 * 
	 * @param value 変換するオブジェクト
	 * @return 変換された文字列
	 * @throws IllegalArgumentException 変換に失敗した場合
	 */
	String convert(Object value);

}