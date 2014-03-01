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

import java.io.Serializable;

/**
 * {@link CsvToken} を実装したデフォルトの実装クラスを提供します。
 *
 * @author Koji Sugisawa
 */
public class SimpleCsvToken implements Serializable, CsvToken {

	private static final long serialVersionUID = 9180143360016401191L;

	/**
	 * トークンの値を保持します。
	 */
	private String value;

	/**
	 * トークンの開始物理行番号を保持します。
	 */
	private int startLineNumber;

	/**
	 * トークンの終了物理行番号を保持します。
	 */
	private int endLineNumber;

	/**
	 * トークンが囲み文字で囲まれていたかどうかを保持します。
	 */
	private boolean enclosed;

	/**
	 * デフォルトコンストラクタです。
	 */
	protected SimpleCsvToken() {
	}

	/**
	 * コンストラクタです。
	 *
	 * @param value トークンの値
	 * @param start トークンの開始物理行番号
	 * @param end トークンの終了物理行番号
	 * @param enclosed トークンが囲み文字で囲まれていたかどうか
	 */
	public SimpleCsvToken(final String value, final int start, final int end, final boolean enclosed) {
		this.value = value;
		this.startLineNumber = start;
		this.endLineNumber = end;
		this.enclosed = enclosed;
	}

	@Override public String getValue() { return value; }
	@Override public int getStartLineNumber() { return startLineNumber; }
	@Override public int getEndLineNumber() { return endLineNumber; }
	@Override public boolean isEnclosed() { return enclosed; }

}