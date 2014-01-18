/*
 * Copyright (c) 2013 OrangeSignal.com All rights reserved.
 *
 * これは Apache ライセンス Version 2.0 (以下、このライセンスと記述) に
 * 従っています。このライセンスに準拠する場合以外、このファイルを使用
 * してはなりません。このライセンスのコピーは以下から入手できます。
 *
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * 適用可能な法律がある、あるいは文書によって明記されている場合を除き、
 * このライセンスの下で配布されているソフトウェアは、明示的であるか暗黙の
 * うちであるかを問わず、「保証やあらゆる種類の条件を含んでおらず」、
 * 「あるがまま」の状態で提供されるものとします。
 * このライセンスが適用される特定の許諾と制限については、このライセンス
 * を参照してください。
 */

package com.orangesignal.csv;

import java.io.Serializable;

/**
 * {@link CsvToken} を実装したデフォルトの実装クラスを提供します。
 *
 * @author 杉澤 浩二
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
	protected SimpleCsvToken() {}

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