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

import java.io.IOException;

/**
 * 区切り文字形式データのデータアクセスインターフェースです。
 * 
 * @param <T> 区切り文字形式データの型
 * @author Koji Sugisawa
 */
public interface CsvHandler<T> {

	/**
	 * 区切り文字形式入力ストリームを読込んで区切り文字形式データを返します。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @return 区切り文字形式データ
	 * @throws IOException 入出力例外が発生した場合
	 * @throws IllegalArgumentException パラメータが不正な場合
	 */
	T load(CsvReader reader) throws IOException;

	/**
	 * 指定された区切り文字形式データを区切り文字形式出力ストリームへ書込みます。
	 * 
	 * @param obj 区切り文字形式データ
	 * @param writer 区切り文字形式出力ストリーム
	 * @throws IOException 入出力例外が発生した場合
	 * @throws IllegalArgumentException パラメータが不正な場合
	 */
	void save(T obj, CsvWriter writer) throws IOException;

}