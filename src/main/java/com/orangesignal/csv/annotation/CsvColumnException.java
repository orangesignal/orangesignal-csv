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
 * この例外は、区切り文字形式のデータ項目の検証操作実行中にエラーが発生したことを示します。
 * 
 * @author Koji Sugisawa
 * @since 2.2
 */
public class CsvColumnException extends IOException {

	private static final long serialVersionUID = -4822064613042281469L;

	/**
	 * 原因となったオブジェクトを保持します。
	 */
	private final Object object;

	/**
	 * 指定された詳細メッセージと原因となったオブジェクトを持つ {@link CsvColumnException} を構築します。
	 * 
	 * @param message 詳細メッセージ
	 * @param object 原因となったオブジェクト
	 */
	public CsvColumnException(final String message, final Object object) {
		super(message);
		this.object = object;
	}

	/**
	 * 原因となったオブジェクトを返します。
	 * 
	 * @return 原因となったオブジェクトまたは {@code null}
	 */
	public Object getObject() {
		return object;
	}

}