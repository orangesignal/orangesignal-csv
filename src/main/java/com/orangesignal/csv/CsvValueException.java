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
 * 区切り文字形式データトークンの値に関しての例外が発生した場合にスローされる例外を提供します。
 * 
 * @author Koji Sugisawa
 * @since 2.1
 */
public class CsvValueException extends IOException {

	private static final long serialVersionUID = -2047098167770188272L;

	/**
	 * 区切り文字形式データトークンの値リストを保持します。
	 */
	private final List<String> values;

	/**
	 * 指定された詳細メッセージと区切り文字形式データトークンの値リストを持つ {@link CsvValueException} を構築します。
	 * 
	 * @param message 詳細メッセージ
	 * @param values 区切り文字形式データトークンの値リスト
	 */
	public CsvValueException(final String message, final List<String> values) {
		super(message);
		this.values = values;
	}

	/**
	 * 区切り文字形式データトークンの値リストを返します。
	 * 
	 * @return 区切り文字形式データトークンの値リスト
	 */
	public List<String> getValues() {
		return values;
	}

}