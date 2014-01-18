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
import java.util.List;

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.filters.BeanFilter;
import com.orangesignal.csv.filters.CsvValueFilter;
import com.orangesignal.csv.handlers.ColumnPositionMappingBeanListHandler;

/**
 * 区切り文字形式データの項目位置を基準とする Java プログラム要素のリストと区切り文字形式データの統合出力インタフェースの実装クラスを提供します。
 *
 * @author 杉澤 浩二
 * @since 1.2
 */
public class CsvColumnPositionMappingBeanSaver<T> extends AbstractCsvSaver<T, ColumnPositionMappingBeanListHandler<T>> {

	/**
	 * データアクセスハンドラを保持します。
	 */
	private final ColumnPositionMappingBeanListHandler<T> handler;

	/**
	 * コンストラクタです。
	 * 
	 * @param cfg 区切り文字形式情報
	 * @param beans Java プログラム要素のリスト
	 * @param beanClass Java プログラム要素の型
	 * @throws IllegalArgumentException パラメータが不正な場合
	 */
	protected CsvColumnPositionMappingBeanSaver(final CsvConfig cfg, final List<T> beans, final Class<T> beanClass) {
		super(cfg, beans);
		this.handler = new ColumnPositionMappingBeanListHandler<T>(beanClass);
	}

	/**
	 * 指定された Java プログラム要素のフィールド名を現在の最後の項目位置としてマップへ追加します。
	 * 
	 * @param field Java プログラム要素のフィールド名
	 * @return このオブジェクトへの参照
	 */
	public CsvColumnPositionMappingBeanSaver<T> column(final String field) {
		handler.addColumn(field);
		return this;
	}

	/**
	 * 指定された Java プログラム要素のフィールド名を現在の最後の項目位置としてマップへ追加します。
	 * 
	 * @param field Java プログラム要素のフィールド名
	 * @param format フィールドを処理するフォーマットオブジェクト (オプション)
	 * @return このオブジェクトへの参照
	 */
	public CsvColumnPositionMappingBeanSaver<T> column(final String field, final Format format) {
		handler.addColumn(field, format);
		return this;
	}

	/**
	 * 指定された項目位置と Java プログラム要素のフィールド名をマップへ追加します。
	 * 
	 * @param position 項目位置
	 * @param field Java プログラム要素のフィールド名
	 * @return このオブジェクトへの参照
	 */
	public CsvColumnPositionMappingBeanSaver<T> column(final int position, final String field) {
		handler.addColumn(position, field);
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
	public CsvColumnPositionMappingBeanSaver<T> column(final int position, final String field, final Format format) {
		handler.addColumn(position, field, format);
		return this;
	}

	/**
	 * 区切り文字形式データフィルタを設定します。
	 * 
	 * @param filter 区切り文字形式データフィルタ
	 * @return このオブジェクトへの参照
	 * @since 1.2.3
	 */
	public CsvColumnPositionMappingBeanSaver<T> filter(final CsvValueFilter filter) {
		handler.filter(filter);
		return this;
	}

	/**
	 * Java プログラム要素フィルタを設定します。
	 * 
	 * @param filter Java プログラム要素フィルタ
	 * @return このオブジェクトへの参照
	 * @since 1.2.3
	 */
	public CsvColumnPositionMappingBeanSaver<T> filter(final BeanFilter filter) {
		handler.filter(filter);
		return this;
	}

	@Override protected ColumnPositionMappingBeanListHandler<T> getCsvListHandler() { return handler; }

}