/*
 * Copyright (c) 2009-2013 OrangeSignal.com All rights reserved.
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

package com.orangesignal.csv.filters;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 指定された項目位置に対応する区切り文字形式データの値が正規表現パターンとマッチするかどうかでフィルタを適用する区切り文字形式データフィルタの実装です。
 * 
 * @author 杉澤 浩二
 * @since 1.2.3
 */
public class ColumnPositionRegexExpression extends ColumnPositionExpression {

	/**
	 * 正規表現パターンを保持します。
	 */
	private Pattern pattern;

	/**
	 * コンストラクタです。
	 * 
	 * @param position 項目位置
	 * @param pattern 正規表現パターン
	 * @throws IllegalArgumentException <code>pattern</code> が <code>null</code> の場合
	 */
	protected ColumnPositionRegexExpression(final int position, final String pattern) {
		this(position, pattern, 0);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param position 項目位置
	 * @param pattern 正規表現パターン
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException <code>pattern</code> が <code>null</code> の場合
	 */
	protected ColumnPositionRegexExpression(final int position, final String pattern, final boolean ignoreCase) {
		this(position, pattern, ignoreCase ? Pattern.CASE_INSENSITIVE : 0);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param position 項目位置
	 * @param pattern 正規表現パターン
	 * @param flags マッチフラグ
	 * @throws IllegalArgumentException <code>pattern</code> が <code>null</code> の場合
	 */
	protected ColumnPositionRegexExpression(final int position, final String pattern, final int flags) {
		super(position);
		if (pattern == null) {
			throw new IllegalArgumentException("Pattern must not be null");
		}
		this.pattern = Pattern.compile(pattern, flags);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param position 項目位置
	 * @param pattern 正規表現パターン
	 * @throws IllegalArgumentException <code>pattern</code> が <code>null</code> の場合
	 */
	protected ColumnPositionRegexExpression(final int position, final Pattern pattern) {
		super(position);
		if (pattern == null) {
			throw new IllegalArgumentException("Pattern must not be null");
		}
		this.pattern = pattern;
	}

	@Override
	public boolean accept(final List<String> values) {
		return CsvExpressionUtils.regex(values, position, pattern);
	}

	@Override
	public boolean accept(final List<String> header, final List<String> values) {
		return CsvExpressionUtils.regex(values, position, pattern);
	}

}
