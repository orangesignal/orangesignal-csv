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
import com.orangesignal.csv.bean.CsvColumnPositionMappingBeanTemplate;
import com.orangesignal.csv.filters.CsvValueFilter;
import com.orangesignal.csv.handlers.ColumnPositionMappingBeanListHandler;

/**
 * 区切り文字形式データの項目位置を基準とする Java プログラム要素のリストと区切り文字形式データの統合入力インタフェースの実装クラスを提供します。
 *
 * @author 杉澤 浩二
 * @since 1.2
 */
public class CsvColumnPositionMappingBeanLoader<T> extends AbstractCsvLoader<T, CsvColumnPositionMappingBeanTemplate<T>, ColumnPositionMappingBeanListHandler<T>, CsvColumnPositionMappingBeanLoader<T>> {

	/**
	 * コンストラクタです。
	 *
	 * @param cfg 区切り文字形式情報
	 * @param beanClass JavaBean の型
	 * @throws IllegalArgumentException パラメータが不正な場合
	 */
	protected CsvColumnPositionMappingBeanLoader(final CsvConfig cfg, final Class<T> beanClass) {
		super(cfg, new ColumnPositionMappingBeanListHandler<T>(beanClass));
	}

	/**
	 * 指定された Java プログラム要素のフィールド名を現在の最後の項目位置としてマップへ追加します。
	 * 
	 * @param field Java プログラム要素のフィールド名
	 * @return このオブジェクトへの参照
	 */
	public CsvColumnPositionMappingBeanLoader<T> column(final String field) {
		getCsvListHandler().addColumn(field);
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド名を現在の最後の項目位置としてマップへ追加します。
	 * 
	 * @param field Java プログラム要素のフィールド名
	 * @param format フィールドを処理するフォーマットオブジェクト (オプション)
	 * @return このオブジェクトへの参照
	 */
	public CsvColumnPositionMappingBeanLoader<T> column(final String field, final Format format) {
		getCsvListHandler().addColumn(field, format);
		return this;
	}

	/**
	 * 指定された項目位置と Java プログラム要素のフィールド名をマップへ追加します。
	 * 
	 * @param position 項目位置
	 * @param field Java プログラム要素のフィールド名
	 * @return このオブジェクトへの参照
	 */
	public CsvColumnPositionMappingBeanLoader<T> column(final int position, final String field) {
		getCsvListHandler().addColumn(position, field);
		return this;
	}

	/**
	 * 指定された項目位置と Java プログラム要素のフィールド名をマップへ追加します。
	 * 
	 * @param position 項目名
	 * @param field Java プログラム要素のフィールド名
	 * @param format フィールドを処理するフォーマットオブジェクト (オプション)
	 * @return このオブジェクトへの参照
	 */
	public CsvColumnPositionMappingBeanLoader<T> column(final int position, final String field, final Format format) {
		getCsvListHandler().addColumn(position, field, format);
		return this;
	}

	/**
	 * 区切り文字形式データフィルタを設定します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 * @return このオブジェクトへの参照
	 * @since 1.2.3
	 */
	public CsvColumnPositionMappingBeanLoader<T> filter(final CsvValueFilter filter) {
		getCsvListHandler().filter(filter);
		return this;
	}

}