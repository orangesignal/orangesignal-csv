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
 * 区切り文字形式データの項目位置を基準とする Java プログラム要素のリストと区切り文字形式データの統合アクセスインタフェースの実装クラスを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.2
 */
public class CsvColumnPositionMappingBeanManager implements CsvManager {

	/**
	 * 区切り文字形式情報を保持します。
	 */
	private CsvConfig csvConfig;

	/**
	 * デフォルトコンストラクタです。
	 */
	public CsvColumnPositionMappingBeanManager() {
		this(new CsvConfig());
	}

	/**
	 * コンストラクタです。
	 * 
	 * @param cfg 区切り文字形式情報
	 * @throws IllegalArgumentException {@code cfg} が {@code null} の場合
	 */
	public CsvColumnPositionMappingBeanManager(final CsvConfig cfg) {
		config(cfg);
	}

	@Override
	public CsvColumnPositionMappingBeanManager config(final CsvConfig cfg) {
		if (cfg == null) {
			throw new IllegalArgumentException("CsvConfig must not be null");
		}
		this.csvConfig = cfg;
		return this;
	}

	@Override
	public <T> CsvColumnPositionMappingBeanLoader<T> load(final Class<T> beanClass) {
		return new CsvColumnPositionMappingBeanLoader<T>(csvConfig, beanClass);
	}

	@Override
	public <T> CsvColumnPositionMappingBeanSaver<T> save(final List<T> beans, final Class<T> beanClass) {
		return new CsvColumnPositionMappingBeanSaver<T>(csvConfig, beans, beanClass);
	}

}