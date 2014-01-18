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

package com.orangesignal.csv.entryfilters;

import java.io.Serializable;

import com.orangesignal.csv.LhaEntryFilter;
import com.orangesignal.csv.ZipEntryFilter;

/**
 * エントリフィルタの基底クラスを提供します。
 * 
 * @author 杉澤 浩二
 * @since 1.2.2
 */
public abstract class AbstractEntryFilter implements ZipEntryFilter, LhaEntryFilter, Serializable {

	private static final long serialVersionUID = -1492900359515513779L;

	/**
	 * デフォルトコンストラクタです。
	 */
	protected AbstractEntryFilter() {}

	@Override
	public String toString() {
		final String name = getClass().getName();
		final int period = name.lastIndexOf('.');
		return (period > 0 ? name.substring(period + 1) : name);
	}

}