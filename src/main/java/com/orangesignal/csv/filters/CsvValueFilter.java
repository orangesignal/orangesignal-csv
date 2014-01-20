/*
 * Copyright 2009-2013 the original author or authors.
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

package com.orangesignal.csv.filters;

import java.util.List;

/**
 * 区切り文字形式データの値リストでフィルタする区切り文字形式データフィルタのインタフェースです。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
public interface CsvValueFilter {

	/**
	 * 指定された区切り文字形式データの値リストが含まれる必要があるかどうかを判定します。
	 * 
	 * @param values 区切り文字形式データの値リスト
	 * @return {@code values} が含まれる必要がある場合は {@code true}
	 */
	boolean accept(List<String> values);

}