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

package com.orangesignal.csv.manager;

import java.util.List;

import com.orangesignal.csv.CsvConfig;

/**
 * 区切り文字形式データの統合アクセスインタフェースです。
 *
 * @author Koji Sugisawa
 */
public interface CsvManager {

	/**
	 * 区切り文字形式情報を設定します。
	 *
	 * @param cfg 区切り文字形式情報
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException {@code cfg} が {@code null} の場合
	 */
	CsvManager config(CsvConfig cfg);

	/**
	 * 区切り文字形式データ統合入力インタフェースを構築して返します。
	 * 
	 * @param type 区切り文字形式データの型
	 * @return 区切り文字形式データの統合入力インタフェース
	 * @throws IllegalArgumentException {@code type} が {@code null} または不正な場合
	 */
	<T> CsvLoader<T> load(Class<T> type);

	/**
	 * 区切り文字形式データ統合出力インタフェースを構築して返します。
	 * 
	 * @param list 区切り文字形式データのリスト
	 * @param type 区切り文字形式データの型
	 * @return 区切り文字形式データの統合出力インタフェース
	 * @throws IllegalArgumentException {@code list} または {@code type} が {@code null} または不正な場合
	 */
	<T> CsvSaver<T> save(List<T> list, Class<T> type);

}