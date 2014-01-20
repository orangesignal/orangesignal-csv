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

/**
 * 区切り文字形式データ注釈要素 {@link com.orangesignal.csv.annotation.CsvEntity} で注釈付けされた Java プログラム要素のリストと区切り文字形式データの統合アクセスインタフェースの実装クラスを提供します。
 *
 * @author Koji Sugisawa
 */
public class CsvEntityManager implements CsvManager {

	/**
	 * 区切り文字形式情報を保持します。
	 */
	private CsvConfig csvConfig;

	/**
	 * デフォルトコンストラクタです。
	 */
	public CsvEntityManager() {
		this(new CsvConfig());
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param cfg 区切り文字形式情報
	 * @throws IllegalArgumentException {@code cfg} が {@code null} の場合
	 */
	public CsvEntityManager(final CsvConfig cfg) {
		if (cfg == null) {
			throw new IllegalArgumentException("CsvConfig must not be null");
		}
		this.csvConfig = cfg;
	}

	@Override
	public CsvEntityManager config(final CsvConfig cfg) {
		if (cfg == null) {
			throw new IllegalArgumentException("CsvConfig must not be null");
		}
		this.csvConfig = cfg;
		return this;
	}

	@Override
	public <T> CsvEntityLoader<T> load(final Class<T> entityClass) {
		return new CsvEntityLoader<T>(csvConfig, entityClass);
	}

	@Override
	public <T> CsvEntitySaver<T> save(final List<T> entities, final Class<T> entityClass) {
		return new CsvEntitySaver<T>(csvConfig, entities, entityClass);
	}

}