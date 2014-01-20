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

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.bean.CsvEntityOperation;
import com.orangesignal.csv.bean.CsvEntityTemplate;
import com.orangesignal.csv.filters.CsvNamedValueFilter;
import com.orangesignal.csv.handlers.CsvEntityListHandler;

/**
 * 区切り文字形式データ注釈要素 {@link com.orangesignal.csv.annotation.CsvEntity} で注釈付けされた Java プログラム要素のリストと区切り文字形式データの統合入力インタフェースの実装クラスを提供します。
 *
 * @author Koji Sugisawa
 */
public class CsvEntityLoader<T> extends AbstractCsvLoader<T, CsvEntityTemplate<T>, CsvEntityListHandler<T>, CsvEntityLoader<T>> implements CsvEntityOperation<CsvEntityLoader<T>> {

	/**
	 * コンストラクタです。
	 *
	 * @param cfg 区切り文字形式情報
	 * @param entityClass Java プログラム要素の型
	 * @throws IllegalArgumentException パラメータが不正な場合
	 */
	protected CsvEntityLoader(final CsvConfig cfg, final Class<T> entityClass) {
		super(cfg, new CsvEntityListHandler<T>(entityClass));
	}

	@Override
	public CsvEntityLoader<T> filter(final CsvNamedValueFilter filter) {
		getCsvListHandler().filter(filter);
		return this;
	}

}