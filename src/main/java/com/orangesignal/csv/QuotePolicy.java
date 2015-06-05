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

package com.orangesignal.csv;

/**
 * 囲み文字出力方法の種類を表す列挙型を提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.1
 */
public enum QuotePolicy {

	/**
	 * 全ての項目を囲み文字で囲むようにします。
	 */
	ALL,

	/**
	 * 項目内に区切り文字、囲み文字または改行文字が含まれる場合にだけ項目を囲み文字で囲むようにします。
	 */
	MINIMAL,

	/**
	 * 列全体の項目を囲み文字で囲むようにします。<p>
	 * {@link com.orangesignal.csv.annotation.CsvColumn#columnQuote} を true にして使用して下さい。
	 * @since 2.2
	 */
	COLUMN;

//	NON_NUMERIC

}