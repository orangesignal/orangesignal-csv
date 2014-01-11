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
import java.util.List;

import com.orangesignal.csv.CsvListHandler;
import com.orangesignal.csv.CsvReader;

/**
 * 区切り文字形式データリストのデータアクセスを行うハンドラの基底クラスを提供します。
 *
 * @param <T> 区切り文字形式データの型
 * @param <H> 区切り文字形式データリストのデータアクセスハンドラの型
 * @author 杉澤 浩二
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
	 * この実装は単に {@code offset} と {@code limit} を使用して処理します。
	 */
	@Override
	public List<T> processScalar(final List<T> list) {
		final int fromIndex = Math.max(this.offset, 0);
		final int toIndex = this.limit <= 0 ? list.size() : Math.min(fromIndex + this.limit, list.size());
		return list.subList(fromIndex, toIndex);
	}

}