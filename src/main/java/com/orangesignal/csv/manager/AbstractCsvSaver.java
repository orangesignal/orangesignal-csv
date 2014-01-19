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
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import java.util.zip.ZipOutputStream;

import com.orangesignal.csv.Csv;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvListHandler;
import com.orangesignal.jlha.LhaOutputStream;

/**
 * 区切り文字形式データの統合出力インタフェースの実装を容易にするための抽象クラスを提供します。
 *
 * @author 杉澤 浩二
 * @since 1.2.1
 */
public abstract class AbstractCsvSaver<T, H extends CsvListHandler<T>> implements CsvSaver<T> {

	/**
	 * 区切り文字形式情報を保持します。
	 */
	private final CsvConfig cfg;

	/**
	 * Java プログラム要素のリストを保持します。
	 */
	private final List<T> beans;

	/**
	 * コンストラクタです。
	 * 
	 * @param cfg 区切り文字形式情報
	 * @param beans Java プログラム要素のリスト
	 * @throws IllegalArgumentException {@code cfg} が {@code null} の場合
	 */
	protected AbstractCsvSaver(final CsvConfig cfg, final List<T> beans) {
		if (cfg == null) {
			throw new IllegalArgumentException("CsvConfig must not be null");
		}
		this.cfg = cfg;
		this.beans = beans;
	}

	/**
	 * 実装は区切り文字形式データリストのデータアクセスハンドラを返します。
	 * 
	 * @return 区切り文字形式データリストのデータアクセスハンドラ
	 */
	protected abstract H getCsvListHandler();

	@Override
	public void to(final Writer writer) throws IOException {
		Csv.save(beans, writer, cfg, getCsvListHandler());
	}

	@Override
	public void to(final OutputStream out, final String encoding) throws IOException {
		Csv.save(beans, out, encoding, cfg, getCsvListHandler());
	}

	@Override
	public void to(final OutputStream out) throws IOException {
		Csv.save(beans, out, cfg, getCsvListHandler());
	}

	@Override
	public void to(final File file, final String encoding) throws IOException {
		Csv.save(beans, file, encoding, cfg, getCsvListHandler());
	}

	@Override
	public void to(final File file) throws IOException {
		Csv.save(beans, file, cfg, getCsvListHandler());
	}

	@Override
	public void to(final LhaOutputStream out, final String encoding, final String entryName) throws IOException {
		Csv.save(beans, out, encoding, cfg, getCsvListHandler(), entryName);
	}

	@Override
	public void to(final LhaOutputStream out, final String entryName) throws IOException {
		Csv.save(beans, out, cfg, getCsvListHandler(), entryName);
	}

	@Override
	public void to(final ZipOutputStream out, final String encoding, final String entryName) throws IOException {
		Csv.save(beans, out, encoding, cfg, getCsvListHandler(), entryName);
	}

	@Override
	public void to(final ZipOutputStream out, final String entryName) throws IOException {
		Csv.save(beans, out, cfg, getCsvListHandler(), entryName);
	}

}