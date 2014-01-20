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
 * @author Koji Sugisawa
 */
package com.orangesignal.csv.annotation;