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

import java.util.List;

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.bean.CsvEntityOperation;
import com.orangesignal.csv.filters.BeanFilter;
import com.orangesignal.csv.filters.CsvNamedValueFilter;
import com.orangesignal.csv.handlers.CsvEntityListHandler;

/**
 * 区切り文字形式データ注釈要素 {@link com.orangesignal.csv.annotation.CsvEntity} で注釈付けされた Java プログラム要素のリストと区切り文字形式データの統合出力インタフェースの実装クラスを提供します。
 *
 * @author Koji Sugisawa
 */
public class CsvEntitySaver<T> extends AbstractCsvSaver<T, CsvEntityListHandler<T>> implements CsvEntityOperation<CsvEntitySaver<T>> {

	/**
	 * データアクセスハンドラを保持します。
	 */
	private final CsvEntityListHandler<T> handler;

	/**
	 * コンストラクタです。
	 * 
	 * @param cfg 区切り文字形式情報
	 * @param entities Java プログラム要素のリスト
	 * @param entityClass Java プログラム要素の型
	 * @throws IllegalArgumentException パラメータが不正な場合
	 */
	protected CsvEntitySaver(final CsvConfig cfg, final List<T> entities, final Class<T> entityClass) {
		super(cfg, entities);
		this.handler = new CsvEntityListHandler<T>(entityClass);
	}

	/**
	 * 区切り文字形式データの列見出し (ヘッダ) 行の出力を無効化するかどうかを設定します。
	 * 
	 * @param disableWriteHeader 区切り文字形式データの列見出し (ヘッダ) 行の出力を無効化するかどうか
	 * @return このオブジェクトへの参照
	 * @since 2.2
	 */
	public CsvEntitySaver<T> disableWriteHeader(final boolean disableWriteHeader) {
		handler.disableWriteHeader(disableWriteHeader);
		return this;
	}

	@Override
	public CsvEntitySaver<T> filter(final CsvNamedValueFilter filter) {
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
	public CsvEntitySaver<T> filter(final BeanFilter filter) {
		handler.filter(filter);
		return this;
	}

	@Override protected CsvEntityListHandler<T> getCsvListHandler() { return handler; }

}