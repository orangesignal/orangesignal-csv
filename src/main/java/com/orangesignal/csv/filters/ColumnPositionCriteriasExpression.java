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

import java.util.Collection;

/**
 * 指定された項目位置に対応する区切り文字形式データの値を指定された判定基準値群と比較してフィルタを適用する区切り文字形式データフィルタの基底クラスを提供します。
 * 
 * @author 杉澤 浩二
 * @since 1.2.3
 */
public abstract class ColumnPositionCriteriasExpression extends ColumnPositionExpression {

	/**
	 * 判定基準値群を保持します。
	 */
	protected String[] criterias;

	/**
	 * 大文字と小文字を区別するかどうかを保持します。
	 */
	protected boolean ignoreCase;

	/**
	 * コンストラクタです。
	 * 
	 * @param position 項目位置
	 * @param criterias 判定基準値群
	 * @throws IllegalArgumentException <code>criterias</code> が <code>null</code> の場合
	 */
	protected ColumnPositionCriteriasExpression(final int position, final Collection<String> criterias) {
		this(position, criterias, false);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param position 項目位置
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException <code>criterias</code> が <code>null</code> の場合
	 */
	protected ColumnPositionCriteriasExpression(final int position, final Collection<String> criterias, final boolean ignoreCase) {
		super(position);
		if (criterias == null) {
			throw new IllegalArgumentException("Criterias must not be null");
		}
		this.criterias = criterias.toArray(new String[0]);
		this.ignoreCase = ignoreCase;
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param position 項目位置
	 * @param criterias 判定基準値群
	 * @throws IllegalArgumentException <code>criterias</code> が <code>null</code> の場合
	 */
	protected ColumnPositionCriteriasExpression(final int position, final String... criterias) {
		this(position, criterias, false);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param position 項目位置
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException <code>criterias</code> が <code>null</code> の場合
	 */
	protected ColumnPositionCriteriasExpression(final int position, final String[] criterias, final boolean ignoreCase) {
		super(position);
		if (criterias == null) {
			throw new IllegalArgumentException("Criterias must not be null");
		}
		final String[] copy = new String[criterias.length];
		System.arraycopy(criterias, 0, copy, 0, copy.length);
		this.criterias = copy;
		this.ignoreCase = ignoreCase;
	}

}
