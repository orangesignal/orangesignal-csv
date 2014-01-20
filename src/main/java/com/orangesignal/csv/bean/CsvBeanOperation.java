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

import com.orangesignal.csv.filters.CsvNamedValueFilter;

/**
 * Java プログラム要素と区切り文字形式データアクセス操作のインタフェースを提供します。
 * 
 * @param <H> Java プログラム要素と区切り文字形式データアクセス操作クラスの型
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public interface CsvBeanOperation<H> {

	/**
	 * Java プログラム要素へデータを設定する名前群を設定します。
	 * 
	 * @param names Java プログラム要素へデータを設定する名前群
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException Java プログラム要素へデータを設定しない名前群が存在する場合
	 */
	H includes(String... names);

	/**
	 * Java プログラム要素へデータを設定しない名前群を設定します。
	 * 
	 * @param names Java プログラム要素へデータを設定しない名前群
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException Java プログラム要素へデータを設定する名前群が存在する場合
	 */
	H excludes(String... names);

	/**
	 * 区切り文字形式データフィルタを設定します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 * @return このオブジェクトへの参照
	 */
	H filter(CsvNamedValueFilter filter);

}