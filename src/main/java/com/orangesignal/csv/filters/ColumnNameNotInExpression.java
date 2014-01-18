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
import java.util.List;

/**
 * 指定された項目名に対応する区切り文字形式データの値が判定基準値群のいずれとも等しくないかどうかでフィルタを適用する区切り文字形式データフィルタの実装です。
 * 
 * @author 杉澤 浩二
 * @since 1.2.3
 */
public class ColumnNameNotInExpression extends ColumnNameCriteriasExpression {

	/**
	 * コンストラクタです。
	 * 
	 * @param name 項目名
	 * @param criterias 判定基準値群
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected ColumnNameNotInExpression(final String name, final Collection<String> criterias) {
		super(name, criterias);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param name 項目名
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected ColumnNameNotInExpression(final String name, final Collection<String> criterias, final boolean ignoreCase) {
		super(name, criterias, ignoreCase);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param name 項目名
	 * @param criterias 判定基準値群
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected ColumnNameNotInExpression(final String name, final String... criterias) {
		super(name, criterias);
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param name 項目名
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @throws IllegalArgumentException パラメータが <code>null</code> の場合
	 */
	protected ColumnNameNotInExpression(final String name, final String[] criterias, final boolean ignoreCase) {
		super(name, criterias, ignoreCase);
	}

	@Override
	public boolean accept(final List<String> header, final List<String> values) {
		final int position = header.indexOf(name);
		if (position == -1) {
			throw new IllegalArgumentException(String.format("Invalid column name %s", name));
		}
		return CsvExpressionUtils.notIn(values, position, criterias, ignoreCase);
	}

}
