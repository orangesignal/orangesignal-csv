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

package com.orangesignal.csv.manager;

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.bean.CsvEntityOperation;
import com.orangesignal.csv.bean.CsvEntityTemplate;
import com.orangesignal.csv.filters.CsvNamedValueFilter;
import com.orangesignal.csv.handlers.CsvEntityListHandler;

/**
 * 区切り文字形式データ注釈要素 {@link com.orangesignal.csv.annotation.CsvEntity} で注釈付けされた Java プログラム要素のリストと区切り文字形式データの統合入力インタフェースの実装クラスを提供します。
 *
 * @author 杉澤 浩二
 */
public class CsvEntityLoader<T> extends AbstractCsvLoader<T, CsvEntityTemplate<T>, CsvEntityListHandler<T>, CsvEntityLoader<T>> implements CsvEntityOperation<CsvEntityLoader<T>> {

	/**
	 * コンストラクタです。
	 *
	 * @param cfg 区切り文字形式情報
	 * @param entityClass Java プログラム要素の型
	 * @throws IllegalArgumentException パラメータが不正な場合
	 */
	protected CsvEntityLoader(final CsvConfig cfg, final Class<T> entityClass) {
		super(cfg, new CsvEntityListHandler<T>(entityClass));
	}

	@Override
	public CsvEntityLoader<T> filter(final CsvNamedValueFilter filter) {
		getCsvListHandler().filter(filter);
		return this;
	}

}