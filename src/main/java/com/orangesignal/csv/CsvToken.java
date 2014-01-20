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
 * 区切り文字形式データトークンのインタフェースです。
 *
 * @author Koji Sugisawa
 */
public interface CsvToken {

	/**
	 * トークンの値を保持します、
	 *
	 * @return トークンの値
	 */
	String getValue();

	/**
	 * トークンの開始物理行番号を取得します。
	 *
	 * @return トークンの開始物理行番号
	 */
	int getStartLineNumber();

	/**
	 * トークンの終了物理行番号を取得します。
	 *
	 * @return トークンの終了物理行番号
	 */
	int getEndLineNumber();

	/**
	 * トークンが囲み文字で囲まれていたかどうかを返します。
	 *
	 * @return トークンが囲み文字で囲まれていたかどうか
	 */
	boolean isEnclosed();

}