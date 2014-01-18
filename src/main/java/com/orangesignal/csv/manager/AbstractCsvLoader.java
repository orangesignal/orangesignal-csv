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

package com.orangesignal.csv.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import jp.gr.java_conf.dangan.util.lha.LhaFile;
import jp.gr.java_conf.dangan.util.lha.LhaInputStream;

import com.orangesignal.csv.Csv;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.LhaEntryFilter;
import com.orangesignal.csv.ZipEntryFilter;
import com.orangesignal.csv.bean.AbstractCsvBeanTemplate;
import com.orangesignal.csv.filters.BeanFilter;
import com.orangesignal.csv.handlers.AbstractBeanListHandler;
import com.orangesignal.csv.handlers.BeanOrder;

/**
 * 区切り文字形式データの統合入力インタフェースの実装を容易にするための抽象クラスを提供します。
 *
 * @author 杉澤 浩二
 * @since 1.2.1
 */
public abstract class AbstractCsvLoader<T, O extends AbstractCsvBeanTemplate<T, O>, H extends AbstractBeanListHandler<T, O, H>, L extends AbstractCsvLoader<T, O, H, L>> implements CsvLoader<T> {

	/**
	 * 区切り文字形式情報を保持します。
	 */
	private final CsvConfig cfg;

	/**
	 * データアクセスハンドラを保持します。
	 */
	private final H handler;

	/**
	 * コンストラクタです。
	 *
	 * @param cfg 区切り文字形式情報
	 * @return 区切り文字形式データリストのデータアクセスハンドラ
	 * @throws IllegalArgumentException {@code cfg} または {@code handler} が {@code null} の場合
	 * @since 1.3.0
	 */
	protected AbstractCsvLoader(final CsvConfig cfg, final H handler) {
		if (cfg == null) {
			throw new IllegalArgumentException("CsvConfig must not be null");
		}
		if (handler == null) {
			throw new IllegalArgumentException("CsvListHandler must not be null");
		}
		this.cfg = cfg;
		this.handler = handler;
	}

	/**
	 * 区切り文字形式データリストのデータアクセスハンドラを返します。
	 * 
	 * @return 区切り文字形式データリストのデータアクセスハンドラ
	 */
	protected H getCsvListHandler() { return handler; }

	@SuppressWarnings("unchecked")
	@Override
	public L filter(final BeanFilter filter) {
		getCsvListHandler().filter(filter);
		return (L) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public L order(final BeanOrder... orders) {
		getCsvListHandler().order(orders);
		return (L) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public L offset(final int offset) {
		getCsvListHandler().setOffset(offset);
		return (L) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public L limit(final int limit) {
		getCsvListHandler().setLimit(limit);
		return (L) this;
	}

	@Override
	public List<T> from(final Reader reader) throws IOException {
		return Csv.load(reader, cfg, getCsvListHandler());
	}

	@Override
	public List<T> from(final InputStream in, final String encoding) throws IOException {
		return Csv.load(in, encoding, cfg, getCsvListHandler());
	}

	@Override
	public List<T> from(final InputStream in) throws IOException {
		return Csv.load(in, cfg, getCsvListHandler());
	}

	@Override
	public List<T> from(final File file, final String encoding) throws IOException {
		return Csv.load(file, encoding, cfg, getCsvListHandler());
	}

	@Override
	public List<T> from(final File file) throws IOException {
		return Csv.load(file, cfg, getCsvListHandler());
	}

	@Override
	public List<T> from(final LhaInputStream in, final String encoding, final LhaEntryFilter filter) throws IOException {
		return Csv.load(in, encoding, cfg, getCsvListHandler(), filter);
	}

	@Override
	public List<T> from(final LhaInputStream in, final String encoding) throws IOException {
		return Csv.load(in, encoding, cfg, getCsvListHandler());
	}

	@Override
	public List<T> from(final LhaInputStream in, final LhaEntryFilter filter) throws IOException {
		return Csv.load(in, cfg, getCsvListHandler(), filter);
	}

	@Override
	public List<T> from(final LhaInputStream in) throws IOException {
		return Csv.load(in, cfg, getCsvListHandler());
	}

	@Override
	public List<T> from(final LhaFile lhaFile, final String encoding, final LhaEntryFilter filter) throws IOException {
		return Csv.load(lhaFile, encoding, cfg, getCsvListHandler(), filter);
	}

	@Override
	public List<T> from(final LhaFile lhaFile, final String encoding) throws IOException {
		return Csv.load(lhaFile, encoding, cfg, getCsvListHandler());
	}

	@Override
	public List<T> from(final LhaFile lhaFile, final LhaEntryFilter filter) throws IOException {
		return Csv.load(lhaFile, cfg, getCsvListHandler(), filter);
	}

	@Override
	public List<T> from(final LhaFile lhaFile) throws IOException {
		return Csv.load(lhaFile, cfg, getCsvListHandler());
	}

	@Override
	public List<T> from(final ZipInputStream in, final String encoding, final ZipEntryFilter filter) throws IOException {
		return Csv.load(in, encoding, cfg, getCsvListHandler(), filter);
	}

	@Override
	public List<T> from(final ZipInputStream in, final String encoding) throws IOException {
		return Csv.load(in, encoding, cfg, getCsvListHandler());
	}

	@Override
	public List<T> from(final ZipInputStream in, final ZipEntryFilter filter) throws IOException {
		return Csv.load(in, cfg, getCsvListHandler(), filter);
	}

	@Override
	public List<T> from(final ZipInputStream in) throws IOException {
		return Csv.load(in, cfg, getCsvListHandler());
	}

	@Override
	public List<T> from(final ZipFile zipFile, final String encoding, final ZipEntryFilter filter) throws IOException {
		return Csv.load(zipFile, encoding, cfg, getCsvListHandler(), filter);
	}

	@Override
	public List<T> from(final ZipFile zipFile, final String encoding) throws IOException {
		return Csv.load(zipFile, encoding, cfg, getCsvListHandler());
	}

	@Override
	public List<T> from(final ZipFile zipFile, final ZipEntryFilter filter) throws IOException {
		return Csv.load(zipFile, cfg, getCsvListHandler(), filter);
	}

	@Override
	public List<T> from(final ZipFile zipFile) throws IOException {
		return Csv.load(zipFile, cfg, getCsvListHandler());
	}

}