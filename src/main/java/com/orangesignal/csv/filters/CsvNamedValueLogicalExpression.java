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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * 区切り文字形式データの項目名リストと値リストでフィルタする区切り文字形式データフィルタを論理演算する区切り文字形式データフィルタの基底クラスを提供します。
 * 
 * @author 杉澤 浩二
 * @since 1.2.3
 */
public abstract class CsvNamedValueLogicalExpression implements CsvNamedValueFilter {

	/**
	 * 区切り文字形式データフィルタのコレクションを保持します。
	 */
	protected final Collection<CsvNamedValueFilter> filters;

	/**
	 * デフォルトコンストラクタです。
	 */
	protected CsvNamedValueLogicalExpression() {
		filters = new ArrayList<CsvNamedValueFilter>();
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param filters 区切り文字形式データフィルタ群
	 * @throws IllegalArgumentException <code>filters</code> が <code>null</code> の場合
	 */
	protected CsvNamedValueLogicalExpression(final CsvNamedValueFilter... filters) {
		if (filters == null) {
			throw new IllegalArgumentException(String.format("%s must not be null", CsvNamedValueFilter.class.getSimpleName()));
		}
		this.filters = Arrays.asList(filters);
	}

	/**
	 * 指定された区切り文字形式データフィルタを追加します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 */
	public void add(final CsvNamedValueFilter filter) {
		filters.add(filter);
	}

	@Override
	public String toString() {
		final String name = getClass().getName();
		final int period = name.lastIndexOf('.');
		return (period > 0 ? name.substring(period + 1) : name);
	}

}
