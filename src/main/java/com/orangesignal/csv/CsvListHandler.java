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
import java.util.List;

/**
 * 区切り文字形式データリストのデータアクセスインターフェースです。
 * 
 * @param <T> 区切り文字形式データの型
 * @author Koji Sugisawa
 */
public interface CsvListHandler<T> extends CsvHandler<List<T>> {

	/**
	 * 取得データの開始位置を設定します。
	 * 
	 * @param offset 取得データの開始位置
	 * @since 1.3.0
	 */
	void setOffset(int offset);

	/**
	 * 取得データの開始位置を設定します。
	 * 
	 * @param offset 取得データの開始位置
	 * @return このオブジェクトへの参照
	 * @since 1.3.0
	 */
	CsvListHandler<T> offset(int offset);

	/**
	 * 取得データの限度数を設定します。
	 * 
	 * @param limit 取得データの限度数
	 * @since 1.3.0
	 */
	void setLimit(int limit);

	/**
	 * 取得データの限度数を設定します。
	 * 
	 * @param limit 取得データの限度数
	 * @return このオブジェクトへの参照
	 * @since 1.3.0
	 */
	CsvListHandler<T> limit(int limit);

	/**
	 * <p>区切り文字形式入力ストリームを読込んで区切り文字形式データのリストを返します。</p>
	 * このメソッドは利便性の為に提供しています。<br>
	 * 実装は {@link #load(CsvReader, boolean)} をスカラー副問い合わせを行うとして呼出すだけです。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @return 区切り文字形式データのリスト
	 * @throws IOException 入出力例外が発生した場合
	 * @throws IllegalArgumentException パラメータが不正な場合
	 * @see #load(CsvReader, boolean)
	 */
	@Override List<T> load(CsvReader reader) throws IOException;

	/**
	 * 区切り文字形式入力ストリームを読込んで区切り文字形式データのリストを返します。
	 * 
	 * @param reader 区切り文字形式入力ストリーム
	 * @param ignoreScalar スカラー副問い合わせを行うかどうか
	 * @return 区切り文字形式データのリスト
	 * @throws IOException 入出力例外が発生した場合
	 * @throws IllegalArgumentException パラメータが不正な場合
	 * @see #processScalar(List)
	 * @since 1.3.0
	 */
	List<T> load(CsvReader reader, boolean ignoreScalar) throws IOException;

	/**
	 * 指定された区切り文字形式データのリストにスカラー副問い合わせを行った結果のリストを返します。
	 * 
	 * @param list スカラー副問い合わせを行う区切り文字形式データのリスト
	 * @return スカラー副問い合わせされた区切り文字形式データのリスト
	 * @since 1.3.0
	 */
	List<T> processScalar(List<T> list);

	/**
	 * 指定された区切り文字形式データのリストを区切り文字形式出力ストリームへ書込みます。
	 * 
	 * @param list 区切り文字形式データのリスト
	 * @param writer 区切り文字形式出力ストリーム
	 * @throws IOException 入出力例外が発生した場合
	 * @throws IllegalArgumentException パラメータが不正な場合
	 */
	@Override void save(List<T> list, CsvWriter writer) throws IOException;

}