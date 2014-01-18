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

import java.text.Format;

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.bean.CsvBeanOperation;
import com.orangesignal.csv.bean.CsvBeanTemplate;
import com.orangesignal.csv.filters.CsvNamedValueFilter;
import com.orangesignal.csv.handlers.BeanListHandler;

/**
 * Java プログラム要素のリストと区切り文字形式データの統合入力インタフェースの実装クラスを提供します。
 *
 * @author 杉澤 浩二
 */
public class CsvBeanLoader<T> extends AbstractCsvLoader<T, CsvBeanTemplate<T>, BeanListHandler<T>, CsvBeanLoader<T>> implements CsvBeanOperation<CsvBeanLoader<T>> {

	/**
	 * コンストラクタです。
	 *
	 * @param cfg 区切り文字形式情報
	 * @param beanClass JavaBean の型
	 * @throws IllegalArgumentException パラメータが不正な場合
	 */
	protected CsvBeanLoader(final CsvConfig cfg, final Class<T> beanClass) {
		super(cfg, new BeanListHandler<T>(beanClass));
	}

	@Override
	public CsvBeanLoader<T> includes(final String... names) {
		getCsvListHandler().includes(names);
		return this;
	}

	@Override
	public CsvBeanLoader<T> excludes(final String... names) {
		getCsvListHandler().excludes(names);
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールドを処理するフォーマットオブジェクトを設定します。
	 * 
	 * @param name Java プログラム要素のフィールド名
	 * @param format フィールドを処理するフォーマットオブジェクト
	 * @return このオブジェクトへの参照
	 * @since 1.2
	 */
	public CsvBeanLoader<T> format(final String name, final Format format) {
		getCsvListHandler().format(name, format);
		return this;
	}

	@Override
	public CsvBeanLoader<T> filter(final CsvNamedValueFilter filter) {
		getCsvListHandler().filter(filter);
		return this;
	}

}