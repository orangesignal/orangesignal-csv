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

/**
 * 指定された項目名に対応する区切り文字形式データの値を指定された判定基準値と比較してフィルタを適用する区切り文字形式データフィルタの基底クラスを提供します。
 * 
 * @author 杉澤 浩二
 * @since 1.2.3
 */
public abstract class ColumnNameCriteriaExpression extends ColumnNameExpression {

	/**
	 * 判定基準値を保持します。
	 */
	protected String criteria;

	/**
	 * 大文字と小文字を区別するかどうかを保持します。
	 */
	protected boolean ignoreCase;

	/**
	 * コンストラクタです。
	 * 
	 * @param name 項目名
	 * @param criteria 判定基準値
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected ColumnNameCriteriaExpression(final String name, final String criteria) {
		this(name, criteria, false);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param name 項目名
	 * @param criteria 判定基準値
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected ColumnNameCriteriaExpression(final String name, final String criteria, final boolean ignoreCase) {
		super(name);
		if (criteria == null) {
			throw new IllegalArgumentException("Criteria must not be null");
		}
		this.criteria = criteria;
		this.ignoreCase = ignoreCase;
	}

}
