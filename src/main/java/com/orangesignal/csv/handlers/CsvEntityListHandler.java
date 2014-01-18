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
import java.util.ArrayList;
import java.util.List;

import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvWriter;
import com.orangesignal.csv.annotation.CsvEntity;
import com.orangesignal.csv.bean.CsvEntityOperation;
import com.orangesignal.csv.bean.CsvEntityTemplate;
import com.orangesignal.csv.filters.CsvNamedValueFilter;
import com.orangesignal.csv.io.CsvEntityReader;
import com.orangesignal.csv.io.CsvEntityWriter;

/**
 * 区切り文字形式データ注釈要素 {@link com.orangesignal.csv.annotation.CsvEntity} で注釈付けされた Java プログラム要素のリストで区切り文字形式データアクセスを行うハンドラを提供します。
 * 
 * @author 杉澤 浩二
 * @see {@link com.orangesignal.csv.annotation.CsvEntity}
 * @see {@link com.orangesignal.csv.annotation.CsvColumn}
 * @see {@link com.orangesignal.csv.annotation.CsvColumns}
 * @see {@link com.orangesignal.csv.manager.CsvEntityManager}
 */
public class CsvEntityListHandler<T> extends AbstractBeanListHandler<T, CsvEntityTemplate<T>, CsvEntityListHandler<T>> implements CsvEntityOperation<CsvEntityListHandler<T>> {

	/**
	 * コンストラクタです。
	 * 
	 * @param entityClass 区切り文字形式データ注釈要素 {@link com.orangesignal.csv.annotation.CsvEntity} で注釈付けされた Java プログラム要素の型
	 * @throws IllegalArgumentException {@code entityClass} が {@code null} または不正な場合
	 */
	public CsvEntityListHandler(final Class<T> entityClass) {
		super(CsvEntityTemplate.newInstance(entityClass));
	}

	@Override
	public CsvEntityListHandler<T> filter(final CsvNamedValueFilter filter) {
		template.filter(filter);
		return this;
	}

	@Override
	public List<T> load(final CsvReader reader, final boolean ignoreScalar) throws IOException {
		@SuppressWarnings("resource")
		final CsvEntityReader<T> r = new CsvEntityReader<T>(reader, template);

		// すべてのデータを読取って繰返し処理します。
		final List<T> results = new ArrayList<T>();
		final boolean order = ignoreScalar || (orders != null && !orders.isEmpty());
		int offset = 0;

		List<String> values;
		while ((values = r.readValues()) != null && (order || limit <= 0 || results.size() < limit)) {
			if (beanFilter == null && !order && offset < this.offset) {
				offset++;
				continue;
			}
			final T entity = r.toEntity(values);
			if (beanFilter != null) {
				if (!beanFilter.accept(entity)) {
					continue;
				}
				if (!order && offset < this.offset) {
					offset++;
					continue;
				}
			}
			results.add(entity);
		}

		if (ignoreScalar || !order) {
			return results;
		}
		return processScalar(results);
	}

	@Override
	public void save(final List<T> entities, final CsvWriter writer) throws IOException {
		if (entities == null) {
			throw new IllegalArgumentException("CsvEntities must not be null");
		}

		@SuppressWarnings("resource")
		final CsvEntityWriter<T> w = new CsvEntityWriter<T>(writer, template);

		// データ出力
		for (final T entity : entities) {
			if (entity == null || entity.getClass().getAnnotation(CsvEntity.class) == null) {
				w.write(null);
				continue;
			} else if (beanFilter != null && !beanFilter.accept(entity)) {
				continue;
			}

			w.write(entity);
		}
	}

}