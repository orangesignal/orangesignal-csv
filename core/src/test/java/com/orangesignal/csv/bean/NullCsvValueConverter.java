/*
 * Copyright (c) 2009 OrangeSignal.com All rights reserved.
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

package com.orangesignal.csv.bean;

import com.orangesignal.csv.annotation.CsvColumn;
import com.orangesignal.csv.bean.CsvValueConverter;

/**
 * 常に <code>null</code> を返す項目値コンバーターの実装クラスを提供します。
 *
 * @author 杉澤 浩二
 * @since 1.2.2
 * @see CsvColumn
 */
public final class NullCsvValueConverter implements CsvValueConverter {

	/**
	 * デフォルトコンストラクタです。
	 */
	public NullCsvValueConverter() {}

	/**
	 * この実装は常に <code>null</code> を返します。
	 */
	@Override
	public Object convert(final String str, final Class<?> type) {
		return null;
	}

	/**
	 * この実装は常に <code>null</code> を返します。
	 */
	@Override
	public String convert(final Object value) {
		return null;
	}

}
