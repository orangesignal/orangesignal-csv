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

package com.orangesignal.csv.handlers;

import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.bean.CsvBeanOperation;
import com.orangesignal.csv.bean.CsvBeanTemplate;
import com.orangesignal.csv.filters.CsvNamedValueFilter;
import com.orangesignal.csv.io.CsvBeanReader;
import com.orangesignal.csv.io.CsvBeanWriter;

/**
 * Java プログラム要素のリストと区切り文字形式データアクセスを行うハンドラを提供します。
 * 
 * @author 杉澤 浩二
 * @see com.orangesignal.csv.manager.CsvBeanManager
 */
public class BeanListHandler<T> extends AbstractBeanListHandler<T, CsvBeanTemplate<T>, BeanListHandler<T>> implements CsvBeanOperation<BeanListHandler<T>> {

	// ------------------------------------------------------------------------

	/**
	 * コンストラクタです。
	 * 
	 * @param type Java プログラム要素の型
	 * @throws IllegalArgumentException <code>type</code> が <code>null</code> の場合
	 */
	public BeanListHandler(final Class<T> type) {
		super(CsvBeanTemplate.newInstance(type));
	}

	// ------------------------------------------------------------------------

	@Override
	public BeanListHandler<T> includes(final String... names) {
		template.includes(names);
		return this;
	}

	@Override
	public BeanListHandler<T> excludes(final String... names) {
		template.excludes(names);
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
	public BeanListHandler<T> format(final String name, final Format format) {
		template.format(name, format);
		return this;
	}

	@Override
	public BeanListHandler<T> filter(final CsvNamedValueFilter filter) {
		template.filter(filter);
		return this;
	}

	// ------------------------------------------------------------------------

	@Override
	public List<T> load(final CsvReader reader, final boolean ignoreScalar) throws IOException {
		@SuppressWarnings("resource")
		final CsvBeanReader<T> r = new CsvBeanReader<T>(reader, template);

		// データ部を処理します。
		final List<T> results = new ArrayList<T>();
		final boolean order = ignoreScalar || (orders != null && !orders.isEmpty());
		int offset = 0;

		List<String> values;
		while ((values = r.readValues()) != null && (order || limit <= 0 || results.size() < limit)) {
			if (beanFilter == null && !order && offset < this.offset) {
				offset++;
				continue;
			}
			final T bean = r.toBean(values);
			if (beanFilter != null) {
				if (!beanFilter.accept(bean)) {
					continue;
				}
				if (!order && offset < this.offset) {
					offset++;
					continue;
				}
			}
			results.add(bean);
		}

		if (ignoreScalar || !order) {
			return results;
		}
		return processScalar(results);
	}

	@Override
	public void save(final List<T> list, final CsvWriter writer) throws IOException {
		@SuppressWarnings("resource")
		final CsvBeanWriter<T> w = new CsvBeanWriter<T>(writer, template);

		// データ部を処理します。
		for (final T bean : list) {
			// 要素が null の場合は null 出力します。
			if (bean == null) {
				w.write(null);
				continue;
			} else if (beanFilter != null && !beanFilter.accept(bean)) {
				continue;
			}
			w.write(bean);
		}
	}

}