/*
 * Copyright 2014 the original author or authors.
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
 * 区切り文字形式データトークンに関しての例外が発生した場合にスローされる例外を提供します。
 * 
 * @author Koji Sugisawa
 * @since 2.1
 */
public class CsvTokenException extends IOException {

	private static final long serialVersionUID = 3908133388773744080L;

	/**
	 * 区切り文字形式データトークンのリストを保持します。
	 */
	private final List<CsvToken> tokens;

	/**
	 * 指定された詳細メッセージと区切り文字形式データトークンのリストを持つ {@link CsvTokenException} を構築します。
	 * 
	 * @param message 詳細メッセージ
	 * @param tokens 区切り文字形式データトークンの値リスト
	 */
	public CsvTokenException(final String message, final List<CsvToken> tokens) {
		super(message);
		this.tokens = tokens;
	}

	/**
	 * 区切り文字形式データトークンのリストを返します。
	 * 
	 * @return 区切り文字形式データトークンのリスト
	 */
	public List<CsvToken> getTokens() {
		return tokens;
	}

}