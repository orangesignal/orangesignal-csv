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
import java.util.List;

import com.orangesignal.csv.CsvListHandler;
import com.orangesignal.csv.CsvReader;

/**
 * 区切り文字形式データリストのデータアクセスを行うハンドラの基底クラスを提供します。
 *
 * @param <T> 区切り文字形式データの型
 * @param <H> 区切り文字形式データリストのデータアクセスハンドラの型
 * @author Koji Sugisawa
 * @since 1.3.0
 */
public abstract class AbstractCsvListHandler<T, H extends AbstractCsvListHandler<T, H>> implements CsvListHandler<T> {

	/**
	 * 取得データの開始位置を保持します。
	 */
	protected int offset;

	/**
	 * 取得データの限度数を保持します。
	 */
	protected int limit;

	/**
	 * デフォルトコンストラクタです。
	 */
	public AbstractCsvListHandler() {}

	@Override
	public void setOffset(final int offset) {
		this.offset = offset;
	}

	@SuppressWarnings("unchecked")
	@Override
	public H offset(final int offset) {
		setOffset(offset);
		return (H) this;
	}

	@Override
	public void setLimit(final int limit) {
		this.limit = limit;
	}

	@SuppressWarnings("unchecked")
	@Override
	public H limit(final int limit) {
		setLimit(limit);
		return (H) this;
	}

	@Override
	public List<T> load(final CsvReader reader) throws IOException {
		return load(reader, false);
	}

	/**
	 * {@inheritDoc}
	 * この実装は単に {@code offset} と {@code limit} を使用して処理します。
	 */
	@Override
	public List<T> processScalar(final List<T> list) {
		final int fromIndex = Math.max(this.offset, 0);
		final int toIndex = this.limit <= 0 ? list.size() : Math.min(fromIndex + this.limit, list.size());
		return list.subList(fromIndex, toIndex);
	}

}