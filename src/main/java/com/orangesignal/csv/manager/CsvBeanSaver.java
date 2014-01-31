/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.orangesignal.csv.manager;

import java.text.Format;
import java.util.List;

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.bean.CsvBeanOperation;
import com.orangesignal.csv.filters.BeanFilter;
import com.orangesignal.csv.filters.CsvNamedValueFilter;
import com.orangesignal.csv.handlers.BeanListHandler;

/**
 * Java プログラム要素のリストと区切り文字形式データの統合出力インタフェースの実装クラスを提供します。
 *
 * @author Koji Sugisawa
 */
public class CsvBeanSaver<T> extends AbstractCsvSaver<T, BeanListHandler<T>> implements CsvBeanOperation<CsvBeanSaver<T>> {

	/**
	 * データアクセスハンドラを保持します。
	 */
	private final BeanListHandler<T> handler;

	/**
	 * コンストラクタです。
	 * 
	 * @param cfg 区切り文字形式情報
	 * @param beans Java プログラム要素のリスト
	 * @param beanClass Java プログラム要素の型
	 * @throws IllegalArgumentException パラメータが不正な場合
	 */
	protected CsvBeanSaver(final CsvConfig cfg, final List<T> beans, final Class<T> beanClass) {
		super(cfg, beans);
		this.handler = new BeanListHandler<T>(beanClass);
	}

	@Override
	public CsvBeanSaver<T> includes(final String...names) {
		handler.includes(names);
		return this;
	}

	@Override
	public CsvBeanSaver<T> excludes(final String...names) {
		handler.excludes(names);
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
	public CsvBeanSaver<T> format(final String name, final Format format) {
		handler.format(name, format);
		return this;
	}

	@Override
	public CsvBeanSaver<T> filter(final CsvNamedValueFilter filter) {
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
	public CsvBeanSaver<T> filter(final BeanFilter filter) {
		handler.filter(filter);
		return this;
	}

	/**
	 * 区切り文字形式データの列見出し (ヘッダ) 行を出力するかどうかを設定します。
	 * 
	 * @param header 区切り文字形式データの列見出し (ヘッダ) 行を出力するかどうか
	 * @return このオブジェクトへの参照
	 * @since 2.1
	 */
	public CsvBeanSaver<T> header(final boolean header) {
		handler.header(header);
		return this;
	}

	@Override protected BeanListHandler<T> getCsvListHandler() { return handler; }

}