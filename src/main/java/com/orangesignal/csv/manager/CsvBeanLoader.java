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

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.bean.CsvBeanOperation;
import com.orangesignal.csv.bean.CsvBeanTemplate;
import com.orangesignal.csv.filters.CsvNamedValueFilter;
import com.orangesignal.csv.handlers.BeanListHandler;

/**
 * Java プログラム要素のリストと区切り文字形式データの統合入力インタフェースの実装クラスを提供します。
 *
 * @author Koji Sugisawa
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