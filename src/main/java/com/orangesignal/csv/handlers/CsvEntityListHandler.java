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
 * @author Koji Sugisawa
 * @see com.orangesignal.csv.annotation.CsvEntity
 * @see com.orangesignal.csv.annotation.CsvColumn
 * @see com.orangesignal.csv.annotation.CsvColumns
 * @see com.orangesignal.csv.manager.CsvEntityManager
 */
public class CsvEntityListHandler<T> extends AbstractBeanListHandler<T, CsvEntityTemplate<T>, CsvEntityListHandler<T>> implements CsvEntityOperation<CsvEntityListHandler<T>> {

	/**
	 * 区切り文字形式データの列見出し (ヘッダ) 行の出力を無効化するかどうかを保持します。
	 * 
	 * @since 2.2
	 */
	private boolean disableWriteHeader;

	/**
	 * コンストラクタです。
	 * 
	 * @param entityClass 区切り文字形式データ注釈要素 {@link com.orangesignal.csv.annotation.CsvEntity} で注釈付けされた Java プログラム要素の型
	 * @throws IllegalArgumentException {@code entityClass} が {@code null} または不正な場合
	 */
	public CsvEntityListHandler(final Class<T> entityClass) {
		super(CsvEntityTemplate.newInstance(entityClass));
	}

	/**
	 * 区切り文字形式データの列見出し (ヘッダ) 行の出力を無効化するかどうかを設定します。
	 * 
	 * @param disableWriteHeader 区切り文字形式データの列見出し (ヘッダ) 行の出力を無効化するかどうか
	 * @return このオブジェクトへの参照
	 * @since 2.2
	 */
	public CsvEntityListHandler<T> disableWriteHeader(final boolean disableWriteHeader) {
		setDisableWriteHeader(disableWriteHeader);
		return this;
	}

	/**
	 * 区切り文字形式データの列見出し (ヘッダ) 行の出力を無効化するかどうかを設定します。
	 * 
	 * @param disableWriteHeader 区切り文字形式データの列見出し (ヘッダ) 行の出力を無効化するかどうか
	 * @since 2.2
	 */
	public void setDisableWriteHeader(final boolean disableWriteHeader) {
		this.disableWriteHeader = disableWriteHeader;
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
		final boolean order = ignoreScalar || orders != null && !orders.isEmpty();
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
		final CsvEntityWriter<T> w = new CsvEntityWriter<T>(writer, template, disableWriteHeader);

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