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

import java.util.List;

import com.orangesignal.csv.CsvConfig;

/**
 * 区切り文字形式データの項目名を基準とする Java プログラム要素のリストと区切り文字形式データの統合アクセスインタフェースの実装クラスを提供します。
 * 
 * @author 杉澤 浩二
 * @since 1.2
 */
public class CsvColumnNameMappingBeanManager implements CsvManager {

	/**
	 * 区切り文字形式情報を保持します。
	 */
	private CsvConfig csvConfig;

	/**
	 * デフォルトコンストラクタです。
	 */
	public CsvColumnNameMappingBeanManager() {
		this(new CsvConfig());
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param cfg 区切り文字形式情報
	 * @throws IllegalArgumentException {@code cfg} が {@code null} の場合
	 */
	public CsvColumnNameMappingBeanManager(final CsvConfig cfg) {
		config(cfg);
	}

	@Override
	public CsvColumnNameMappingBeanManager config(final CsvConfig cfg) {
		if (cfg == null) {
			throw new IllegalArgumentException("CsvConfig must not be null");
		}
		this.csvConfig = cfg;
		return this;
	}

	@Override
	public <T> CsvColumnNameMappingBeanLoader<T> load(final Class<T> beanClass) {
		return new CsvColumnNameMappingBeanLoader<T>(csvConfig, beanClass);
	}

	@Override
	public <T> CsvColumnNameMappingBeanSaver<T> save(final List<T> beans, final Class<T> beanClass) {
		return new CsvColumnNameMappingBeanSaver<T>(csvConfig, beans, beanClass);
	}

}