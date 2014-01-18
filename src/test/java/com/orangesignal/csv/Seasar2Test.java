/*
 * Copyright (c) 2009 OrangeSignal.com All rights reserved.
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

package com.orangesignal.csv;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.Reader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.seasar.extension.unit.S2TestCase;

import com.orangesignal.csv.Csv;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvListHandler;
import com.orangesignal.csv.QuotePolicy;
import com.orangesignal.csv.entity.Price;
import com.orangesignal.csv.filters.SimpleBeanFilter;
import com.orangesignal.csv.filters.SimpleCsvNamedValueFilter;
import com.orangesignal.csv.manager.CsvBeanManager;
import com.orangesignal.csv.model.SampleBean;

/**
 * Seasar2 対応テストです。
 * 
 * @author 杉澤 浩二
 */
public class Seasar2Test extends S2TestCase {

	@Resource
	private CsvConfig cfg;

	@Resource
	private CsvListHandler<Price> handler;

	@Resource
	private CsvBeanManager manager;

	@Override
	protected void setUp() throws Exception {
		include("app.dicon");
	}

	@Test
	public void testCsvConfig() {
		assertThat(cfg.getSeparator(), is(','));
		assertThat(cfg.getQuote(), is('"'));
		assertThat(cfg.getEscape(), is('\\'));
		assertThat(cfg.isQuoteDisabled(), is(false));
		assertThat(cfg.isEscapeDisabled(), is(false));
		assertThat(cfg.getBreakString(), is("\n"));
		assertThat(cfg.getNullString(), is("NULL"));
		assertThat(cfg.isIgnoreCaseNullString(), is(true));
		assertThat(cfg.isIgnoreLeadingWhitespaces(), is(true));
		assertThat(cfg.isIgnoreTrailingWhitespaces(), is(true));
		assertThat(cfg.isIgnoreEmptyLines(), is(true));
		assertThat(cfg.getSkipLines(), is(0));
		assertThat(cfg.getQuotePolicy(), is(QuotePolicy.MINIMAL));
		assertThat(cfg.getLineSeparator(), is("\r\n"));
	}

	@Test
	public void testCsvListHandler() throws Exception {
		final Reader reader = new StringReader(
				"シンボル,名称,価格,出来高,日付\r\n" +
				"GCQ09,COMEX 金 2009年08月限,1058.70,10,2008/08/06\r\n" +
				"GCU09,COMEX 金 2009年09月限,1068.70,10,2008/09/06\r\n" +
				"GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\n" +
				"GCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n" +
				"GCZ09,COMEX 金 2009年12月限,1098.70,13,2008/12/06\r\n"
			);
		try {
			final List<Price> list = Csv.load(reader, cfg, handler);
			assertThat(list.size(), is(5));
			assertThat(list.get(0).symbol, is("GCQ09"));
			assertThat(list.get(1).symbol, is("GCU09"));
			assertThat(list.get(2).symbol, is("GCV09"));
			assertThat(list.get(3).symbol, is("GCX09"));
			assertThat(list.get(4).symbol, is("GCZ09"));
			assertThat(list.get(0).name, is("COMEX 金 2009年08月限"));
			assertThat(list.get(1).name, is("COMEX 金 2009年09月限"));
			assertThat(list.get(2).name, is("COMEX 金 2009年10月限"));
			assertThat(list.get(3).name, is("COMEX 金 2009年11月限"));
			assertThat(list.get(4).name, is("COMEX 金 2009年12月限"));
			assertThat(list.get(0).price.doubleValue(), is(1058.70D));
			assertThat(list.get(1).price.doubleValue(), is(1068.70D));
			assertThat(list.get(2).price.doubleValue(), is(1078.70D));
			assertThat(list.get(3).price.doubleValue(), is(1088.70D));
			assertThat(list.get(4).price.doubleValue(), is(1098.70D));
			assertThat(list.get(0).date, is(new SimpleDateFormat("yyyy/MM/dd").parse("2008/08/06")));
			assertThat(list.get(1).date, is(new SimpleDateFormat("yyyy/MM/dd").parse("2008/09/06")));
			assertThat(list.get(2).date, is(new SimpleDateFormat("yyyy/MM/dd").parse("2008/10/06")));
			assertThat(list.get(3).date, is(new SimpleDateFormat("yyyy/MM/dd").parse("2008/11/06")));
			assertThat(list.get(4).date, is(new SimpleDateFormat("yyyy/MM/dd").parse("2008/12/06")));
		} finally {
			Csv.closeQuietly(reader);
		}
	}

	@Test
	public void testCsvBeanManager() throws Exception {
		assertNotNull(manager);
		final Reader reader = new StringReader(
				"symbol,name,price,volume,date\r\n" +
				"GCQ09,COMEX 金 2009年08月限,1058.70,10,2008/08/06\r\n" +
				"GCU09,COMEX 金 2009年09月限,1068.70,10,2008/09/06\r\n" +
				"GCV09,COMEX 金 2009年10月限,1078.70,11,2008/10/06\r\n" +
				"GCX09,COMEX 金 2009年11月限,1088.70,12,2008/11/06\r\n" +
				"GCZ09,COMEX 金 2009年12月限,1098.70,13,2008/12/06\r\n"
			);

		try {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			final List<SampleBean> list = manager
					.load(SampleBean.class)
					.format("date", new SimpleDateFormat("yyyy/MM/dd"))
					.filter(new SimpleCsvNamedValueFilter().ne("symbol", "gcu09", true))
					.filter(new SimpleBeanFilter().ne("date", df.parse("2008/11/06")))
					.offset(1).limit(1)
					.from(reader);

			assertThat(list.size(), is(1));
			final SampleBean o1 = list.get(0);
			assertThat(o1.symbol, is("GCV09"));
			assertThat(o1.name, is("COMEX 金 2009年10月限"));
			assertThat(o1.price.doubleValue(), is(1078.70D));
			assertThat(o1.volume.longValue(), is(11L));
			assertThat(o1.date, is(df.parse("2008/10/06")));
		} finally {
			reader.close();
		}
	}

}
