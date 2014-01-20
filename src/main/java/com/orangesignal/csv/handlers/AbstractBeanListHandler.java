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

package com.orangesignal.csv.handlers;

import java.text.Format;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.orangesignal.csv.bean.AbstractCsvBeanTemplate;
import com.orangesignal.csv.bean.CsvValueConverter;
import com.orangesignal.csv.filters.BeanFilter;

/**
 * Java プログラム要素のリストと区切り文字形式データアクセスを行うハンドラの基底クラスを提供します。
 * 
 * @param <T> 区切り文字形式データの型
 * @param <O> Java プログラム要素の操作を簡素化するヘルパークラスの型
 * @param <H> 区切り文字形式データリストのデータアクセスハンドラの型
 * @author Koji Sugisawa
 */
public abstract class AbstractBeanListHandler<T, O extends AbstractCsvBeanTemplate<T, O>, H extends AbstractBeanListHandler<T, O, H>> extends AbstractCsvListHandler<T, H> {

	/**
	 * Java プログラム要素操作の簡素化ヘルパーを保持します。
	 */
	protected final O template;

	/**
	 * Java プログラム要素フィルタを保持します。
	 */
	protected BeanFilter beanFilter;

	/**
	 * 並び替え条件を保持します。
	 */
	protected List<BeanOrder> orders;

	// ------------------------------------------------------------------------

	/**
	 * コンストラクタです。
	 * 
	 * @param template Java プログラム要素の操作を簡素化するヘルパークラスの型
	 * @throws IllegalArgumentException {@code template} が {@code null} の場合
	 */
	protected AbstractBeanListHandler(final O template) {
		if (template == null) {
			throw new IllegalArgumentException("AbstractCsvBeanTemplate must not be null");
		}
		this.template = template;
	}

	/**
	 * この実装は <code>orders</code> が指定されている場合、並び替えを行ってから {@link AbstractCsvListHandler#processScalar(List)} を呼出します。
	 */
	@Override
	public List<T> processScalar(final List<T> beans) {
		if (orders != null) {
			BeanOrderComparator.sort(beans, orders);
		}
		return super.processScalar(beans);
	}

	// ------------------------------------------------------------------------

	/**
	 * Java プログラム要素の型を返します。
	 * 
	 * @return Java プログラム要素の型
	 */
	public Class<T> getType() { return template.getType(); }

	/**
	 * Java プログラム要素のフィールド名と項目値を解析するオブジェクトのマップを設定します。
	 * 
	 * @param valueParserMapping Java プログラム要素のフィールド名と項目値を解析するオブジェクトのマップ
	 * @throws IllegalArgumentException {@code valueParserMapping} が {@code null} の場合
	 * @since 1.2.4
	 */
	public void setValueParserMapping(final Map<String, Format> valueParserMapping) {
		template.setValueParserMapping(valueParserMapping);
	}

	/**
	 * Java プログラム要素のフィールド名と項目値を解析するオブジェクトのマップを設定します。
	 * 
	 * @param valueParserMapping Java プログラム要素のフィールド名と項目値を解析するオブジェクトのマップ
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException {@code valueParserMapping} が {@code null} の場合
	 * @since 1.2
	 */
	@SuppressWarnings("unchecked")
	public H valueParserMapping(final Map<String, Format> valueParserMapping) {
		setValueParserMapping(valueParserMapping);
		return (H) this;
	}

	/**
	 * 項目名 (または項目位置) と項目値へ書式化するオブジェクトのマップを設定します。
	 * 
	 * @param valueFormatterMapping 項目名 (または項目位置) と項目値へ書式化するオブジェクトのマップ
	 * @throws IllegalArgumentException {@code valueFormaterMapping} が {@code null} の場合
	 * @since 1.2.4
	 */
	public void setValueFormatterMapping(final Map<Object, Format> valueFormatterMapping) {
		template.setValueFormatterMapping(valueFormatterMapping);
	}

	/**
	 * 項目名 (または項目位置) と項目値へ書式化するオブジェクトのマップを設定します。
	 * 
	 * @param valueFormatterMapping 項目名 (または項目位置) と項目値へ書式化するオブジェクトのマップ
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException {@code valueFormaterMapping} が {@code null} の場合
	 * @since 1.2
	 */
	@SuppressWarnings("unchecked")
	public H valueFormatterMapping(final Map<Object, Format> valueFormatterMapping) {
		setValueFormatterMapping(valueFormatterMapping);
		return (H) this;
	}

	/**
	 * 区切り文字形式データの項目値コンバータを設定します。
	 * 
	 * @param valueConverter 区切り文字形式データの項目値コンバータ
	 * @throws IllegalArgumentException {@code valueConverter} が {@code null} の場合
	 * @since 1.2.4
	 */
	public void setValueConverter(final CsvValueConverter valueConverter) {
		template.setValueConverter(valueConverter);
	}

	/**
	 * 区切り文字形式データの項目値コンバータを設定します。
	 * 
	 * @param valueConverter 区切り文字形式データの項目値コンバータ
	 * @return このオブジェクトへの参照
	 * @throws IllegalArgumentException {@code valueConverter} が {@code null} の場合
	 * @since 1.2
	 */
	@SuppressWarnings("unchecked")
	public H valueConverter(final CsvValueConverter valueConverter) {
		setValueConverter(valueConverter);
		return (H) this;
	}

	/**
	 * Java プログラム要素フィルタを設定します。
	 * 
	 * @param beanFilter Java プログラム要素フィルタ
	 * @return このオブジェクトへの参照
	 * @since 1.2.3
	 */
	@SuppressWarnings("unchecked")
	public H filter(final BeanFilter beanFilter) {
		this.beanFilter = beanFilter;
		return (H) this;
	}

	/**
	 * 並び替え条件を設定します。
	 * 
	 * @param orders 並び替え条件
	 * @since 1.2.8
	 */
	public void setOrder(final List<BeanOrder> orders) {
		this.orders = orders;
	}

	/**
	 * 並び替え条件を設定します。
	 * 
	 * @param orders 並び替え条件
	 * @return このオブジェクトへの参照
	 * @since 1.2.8
	 */
	@SuppressWarnings("unchecked")
	public H order(final BeanOrder... orders) {
		setOrder(orders != null ? Arrays.asList(orders) : null);
		return (H) this;
	}

}