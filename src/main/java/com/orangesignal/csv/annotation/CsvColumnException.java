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

package com.orangesignal.csv.annotation;

import java.io.IOException;

/**
 * 区切り文字形式データの Java プログラム要素に関しての例外が発生した場合にスローされる例外を提供します。
 * 
 * @author Koji Sugisawa
 * @since 2.2
 */
public class CsvColumnException extends IOException {

	private static final long serialVersionUID = -4822064613042281469L;

	/**
	 * Java プログラム要素を保持します。
	 */
	private final Object entity;

	/**
	 * 指定された詳細メッセージと区切り文字形式データの Java プログラム要素を持つ {@link CsvColumnException} を構築します。
	 * 
	 * @param message 詳細メッセージ
	 * @param entity 区切り文字形式データの Java プログラム要素
	 */
	public CsvColumnException(final String message, final Object entity) {
		super(message);
		this.entity = entity;
	}

	/**
	 * 区切り文字形式データの Java プログラム要素を返します。
	 * 
	 * @return 区切り文字形式データの Java プログラム要素
	 */
	public Object getEntity() {
		return entity;
	}

}