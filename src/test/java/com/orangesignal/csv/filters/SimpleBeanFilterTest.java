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

package com.orangesignal.csv.filters;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.junit.Test;

import com.orangesignal.csv.entity.Price;
import com.orangesignal.csv.filters.BeanAndExpression;
import com.orangesignal.csv.filters.BeanFilter;
import com.orangesignal.csv.filters.BeanOrExpression;
import com.orangesignal.csv.filters.SimpleBeanFilter;

/**
 * {@link SimpleBeanFilter} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class SimpleBeanFilterTest {

	@Test
	public void testSimpleBeanFilter() throws IOException {
		assertFalse(new SimpleBeanFilter()
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } })
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } })
				.accept(null)
			);
		assertFalse(new SimpleBeanFilter()
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } })
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } })
				.accept(null)
			);
		assertFalse(new SimpleBeanFilter()
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } })
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } })
				.accept(null)
			);
		assertTrue(new SimpleBeanFilter()
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } })
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } })
				.accept(null)
			);
		
		assertFalse(new SimpleBeanFilter(new BeanAndExpression())
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } })
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } })
				.accept(null)
			);
		assertFalse(new SimpleBeanFilter(new BeanAndExpression())
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } })
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } })
				.accept(null)
			);
		assertFalse(new SimpleBeanFilter(new BeanAndExpression())
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } })
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } })
				.accept(null)
			);
		assertTrue(new SimpleBeanFilter(new BeanAndExpression())
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } })
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } })
				.accept(null)
			);
		
		assertFalse(new SimpleBeanFilter(new BeanOrExpression())
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } })
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } })
				.accept(null)
			);
		assertTrue(new SimpleBeanFilter(new BeanOrExpression())
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } })
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } })
				.accept(null)
			);
		assertTrue(new SimpleBeanFilter(new BeanOrExpression())
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } })
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } })
				.accept(null)
			);
		assertTrue(new SimpleBeanFilter(new BeanOrExpression())
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } })
				.add(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } })
				.accept(null)
			);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSimpleBeanFilterIllegalArgumentException() {
		new SimpleBeanFilter(null);
	}

	@Test
	public void testIsNull() throws IOException {
		assertTrue(new SimpleBeanFilter().isNull("symbol").accept(new Price(null, "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertFalse(new SimpleBeanFilter().isNull("symbol").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertTrue(new SimpleBeanFilter().isNull("name").accept(new Price("GCX09", null, 1088.70, 100, new Date())));
		assertFalse(new SimpleBeanFilter().isNull("name").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertTrue(new SimpleBeanFilter().isNull("price").accept(new Price("GCX09", "COMEX 金 2009年11月限", null, 100, new Date())));
		assertFalse(new SimpleBeanFilter().isNull("price").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertTrue(new SimpleBeanFilter().isNull("date").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, null)));
		assertFalse(new SimpleBeanFilter().isNull("date").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
	}

	@Test
	public void testIsNotNull() throws IOException {
		assertFalse(new SimpleBeanFilter().isNotNull("symbol").accept(new Price(null, "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertTrue(new SimpleBeanFilter().isNotNull("symbol").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertFalse(new SimpleBeanFilter().isNotNull("name").accept(new Price("GCX09", null, 1088.70, 100, new Date())));
		assertTrue(new SimpleBeanFilter().isNotNull("name").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertFalse(new SimpleBeanFilter().isNotNull("price").accept(new Price("GCX09", "COMEX 金 2009年11月限", null, 100, new Date())));
		assertTrue(new SimpleBeanFilter().isNotNull("price").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertFalse(new SimpleBeanFilter().isNotNull("date").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, null)));
		assertTrue(new SimpleBeanFilter().isNotNull("date").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
	}

	@Test
	public void testIsEmpty() throws IOException {
		assertTrue(new SimpleBeanFilter().isEmpty("symbol").accept(new Price(null, "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertTrue(new SimpleBeanFilter().isEmpty("symbol").accept(new Price("", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertFalse(new SimpleBeanFilter().isEmpty("symbol").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertTrue(new SimpleBeanFilter().isEmpty("name").accept(new Price("GCX09", null, 1088.70, 100, new Date())));
		assertTrue(new SimpleBeanFilter().isEmpty("name").accept(new Price("GCX09", "", 1088.70, 100, new Date())));
		assertFalse(new SimpleBeanFilter().isEmpty("name").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertTrue(new SimpleBeanFilter().isEmpty("price").accept(new Price("GCX09", "COMEX 金 2009年11月限", null, 100, new Date())));
		assertFalse(new SimpleBeanFilter().isEmpty("price").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertTrue(new SimpleBeanFilter().isEmpty("date").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, null)));
		assertFalse(new SimpleBeanFilter().isEmpty("date").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
	}

	@Test
	public void testIsNotEmpty() throws IOException {
		assertFalse(new SimpleBeanFilter().isNotEmpty("symbol").accept(new Price(null, "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertFalse(new SimpleBeanFilter().isNotEmpty("symbol").accept(new Price("", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertTrue(new SimpleBeanFilter().isNotEmpty("symbol").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertFalse(new SimpleBeanFilter().isNotEmpty("name").accept(new Price("GCX09", null, 1088.70, 100, new Date())));
		assertFalse(new SimpleBeanFilter().isNotEmpty("name").accept(new Price("GCX09", "", 1088.70, 100, new Date())));
		assertTrue(new SimpleBeanFilter().isNotEmpty("name").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertFalse(new SimpleBeanFilter().isNotEmpty("price").accept(new Price("GCX09", "COMEX 金 2009年11月限", null, 100, new Date())));
		assertTrue(new SimpleBeanFilter().isNotEmpty("price").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
		assertFalse(new SimpleBeanFilter().isNotEmpty("date").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, null)));
		assertTrue(new SimpleBeanFilter().isNotEmpty("date").accept(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date())));
	}

	@Test
	public void testEq() throws Exception {
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2009/11/06 19:14:46"));
		assertFalse(new SimpleBeanFilter().eq("symbol", "SIX09").accept(price));
		assertTrue(new SimpleBeanFilter().eq("symbol", "GCX09").accept(price));
		assertFalse(new SimpleBeanFilter().eq("symbol", "SIX09", false).accept(price));
		assertTrue(new SimpleBeanFilter().eq("symbol", "GCX09", false).accept(price));
		assertFalse(new SimpleBeanFilter().eq("symbol", "six09", true).accept(price));
		assertTrue(new SimpleBeanFilter().eq("symbol", "gcx09", true).accept(price));
		assertFalse(new SimpleBeanFilter().eq("name", "COMEX 銀 2009年11月限").accept(price));
		assertTrue(new SimpleBeanFilter().eq("name", "COMEX 金 2009年11月限").accept(price));
		assertFalse(new SimpleBeanFilter().eq("price", 1088.00).accept(price));
		assertTrue(new SimpleBeanFilter().eq("price", 1088.70).accept(price));
		assertFalse(new SimpleBeanFilter().eq("date", new Date()).accept(price));
		assertTrue(new SimpleBeanFilter().eq("date", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2009/11/06 19:14:46")).accept(price));
	}

	@Test
	public void testNe() throws Exception {
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2009/11/06 19:14:46"));
		assertTrue(new SimpleBeanFilter().ne("symbol", "SIX09").accept(price));
		assertFalse(new SimpleBeanFilter().ne("symbol", "GCX09").accept(price));
		assertTrue(new SimpleBeanFilter().ne("symbol", "SIX09", false).accept(price));
		assertFalse(new SimpleBeanFilter().ne("symbol", "GCX09", false).accept(price));
		assertTrue(new SimpleBeanFilter().ne("symbol", "six09", true).accept(price));
		assertFalse(new SimpleBeanFilter().ne("symbol", "gcx09", true).accept(price));
		assertTrue(new SimpleBeanFilter().ne("name", "COMEX 銀 2009年11月限").accept(price));
		assertFalse(new SimpleBeanFilter().ne("name", "COMEX 金 2009年11月限").accept(price));
		assertTrue(new SimpleBeanFilter().ne("price", 1088.00).accept(price));
		assertFalse(new SimpleBeanFilter().ne("price", 1088.70).accept(price));
		assertTrue(new SimpleBeanFilter().ne("date", new Date()).accept(price));
		assertFalse(new SimpleBeanFilter().ne("date", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2009/11/06 19:14:46")).accept(price));
	}

	@Test
	public void testIn() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertFalse(new SimpleBeanFilter().in("symbol", "SIU09", "SIV09", "SIX09").accept(price));
		assertTrue(new SimpleBeanFilter().in("symbol", "GCU09", "GCV09", "GCX09").accept(price));
		assertFalse(new SimpleBeanFilter().in("symbol", new String[]{ "SIU09", "SIV09", "SIX09" }, false).accept(price));
		assertTrue(new SimpleBeanFilter().in("symbol", new String[]{ "GCU09", "GCV09", "GCX09" }, false).accept(price));
		assertFalse(new SimpleBeanFilter().in("symbol", new String[]{ "siu09", "siv09", "six09" }, true).accept(price));
		assertTrue(new SimpleBeanFilter().in("symbol", new String[]{ "gcu09", "gcv09", "gcx09" }, true).accept(price));
		assertFalse(new SimpleBeanFilter().in("name", "COMEX 銀 2009年9月限", "COMEX 銀 2009年10月限", "COMEX 銀 2009年11月限").accept(price));
		assertTrue(new SimpleBeanFilter().in("name", "COMEX 金 2009年9月限", "COMEX 金 2009年10月限", "COMEX 金 2009年11月限").accept(price));
		assertFalse(new SimpleBeanFilter().in("price",1068.00, 1078.00, 1088.00).accept(price));
		assertTrue(new SimpleBeanFilter().in("price", 1068.70, 1078.70, 1088.70).accept(price));
		assertFalse(new SimpleBeanFilter().in("date", df.parse("2008/09/06"), df.parse("2008/10/06"), df.parse("2008/11/06")).accept(price));
		assertTrue(new SimpleBeanFilter().in("date", df.parse("2009/09/06"), df.parse("2009/10/06"), df.parse("2009/11/06")).accept(price));
	}

	@Test
	public void testNotIn() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertTrue(new SimpleBeanFilter().notIn("symbol", new String[]{ "SIU09", "SIV09", "SIX09" }).accept(price));
		assertFalse(new SimpleBeanFilter().notIn("symbol", new String[]{ "GCU09", "GCV09", "GCX09" }).accept(price));
		assertTrue(new SimpleBeanFilter().notIn("symbol", new String[]{ "SIU09", "SIV09", "SIX09" }, false).accept(price));
		assertFalse(new SimpleBeanFilter().notIn("symbol", new String[]{ "GCU09", "GCV09", "GCX09" }, false).accept(price));
		assertTrue(new SimpleBeanFilter().notIn("symbol", new String[]{ "siu09", "siv09", "six09" }, true).accept(price));
		assertFalse(new SimpleBeanFilter().notIn("symbol", new String[]{ "gcu09", "gcv09", "gcx09" }, true).accept(price));
		assertTrue(new SimpleBeanFilter().notIn("name", "COMEX 銀 2009年9月限", "COMEX 銀 2009年10月限", "COMEX 銀 2009年11月限").accept(price));
		assertFalse(new SimpleBeanFilter().notIn("name", "COMEX 金 2009年9月限", "COMEX 金 2009年10月限", "COMEX 金 2009年11月限").accept(price));
		assertTrue(new SimpleBeanFilter().notIn("price",1068.00, 1078.00, 1088.00).accept(price));
		assertFalse(new SimpleBeanFilter().notIn("price", 1068.70, 1078.70, 1088.70).accept(price));
		assertTrue(new SimpleBeanFilter().notIn("date", df.parse("2008/09/06"), df.parse("2008/10/06"), df.parse("2008/11/06")).accept(price));
		assertFalse(new SimpleBeanFilter().notIn("date", df.parse("2009/09/06"), df.parse("2009/10/06"), df.parse("2009/11/06")).accept(price));
	}

	@Test
	public void testRegex() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertTrue(new SimpleBeanFilter().regex("symbol", "^GCX[0-9]{1,2}$").accept(price));
		assertFalse(new SimpleBeanFilter().regex("name", "^GCX[0-9]{1,2}$").accept(price));
		assertTrue(new SimpleBeanFilter().regex("symbol", "^GCX[0-9]{1,2}$", false).accept(price));
		assertFalse(new SimpleBeanFilter().regex("name", "^GCX[0-9]{1,2}$", false).accept(price));
		assertTrue(new SimpleBeanFilter().regex("symbol", "^gcx[0-9]{1,2}$", true).accept(price));
		assertFalse(new SimpleBeanFilter().regex("name", "^gcx[0-9]{1,2}$", true).accept(price));
		assertTrue(new SimpleBeanFilter().regex("symbol", "^gcx[0-9]{1,2}$", Pattern.CASE_INSENSITIVE).accept(price));
		assertFalse(new SimpleBeanFilter().regex("name", "^gcx[0-9]{1,2}$", Pattern.CASE_INSENSITIVE).accept(price));
		assertTrue(new SimpleBeanFilter().regex("symbol", Pattern.compile("^GCX[0-9]{1,2}$")).accept(price));
		assertFalse(new SimpleBeanFilter().regex("name", Pattern.compile("^GCX[0-9]{1,2}$")).accept(price));
	}

	@Test
	public void testGt() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertFalse(new SimpleBeanFilter().gt("price", 1098.00).accept(price));
		assertFalse(new SimpleBeanFilter().gt("price", 1088.70).accept(price));
		assertTrue(new SimpleBeanFilter().gt("price", 1088.00).accept(price));
		assertFalse(new SimpleBeanFilter().gt("date", df.parse("2009/12/06")).accept(price));
		assertFalse(new SimpleBeanFilter().gt("date", df.parse("2009/11/06")).accept(price));
		assertTrue(new SimpleBeanFilter().gt("date", df.parse("2009/10/06")).accept(price));
		assertFalse(new SimpleBeanFilter().gt("price", 1098.00, null).accept(price));
		assertFalse(new SimpleBeanFilter().gt("price", 1088.70, null).accept(price));
		assertTrue(new SimpleBeanFilter().gt("price", 1088.00, null).accept(price));
		assertFalse(new SimpleBeanFilter().gt("date", df.parse("2009/12/06"), null).accept(price));
		assertFalse(new SimpleBeanFilter().gt("date", df.parse("2009/11/06"), null).accept(price));
		assertTrue(new SimpleBeanFilter().gt("date", df.parse("2009/10/06"), null).accept(price));
	}

	@Test
	public void testLt() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertTrue(new SimpleBeanFilter().lt("price", 1098.00).accept(price));
		assertFalse(new SimpleBeanFilter().lt("price", 1088.70).accept(price));
		assertFalse(new SimpleBeanFilter().lt("price", 1088.00).accept(price));
		assertTrue(new SimpleBeanFilter().lt("date", df.parse("2009/12/06")).accept(price));
		assertFalse(new SimpleBeanFilter().lt("date", df.parse("2009/11/06")).accept(price));
		assertFalse(new SimpleBeanFilter().lt("date", df.parse("2009/10/06")).accept(price));
		assertTrue(new SimpleBeanFilter().lt("price", 1098.00, null).accept(price));
		assertFalse(new SimpleBeanFilter().lt("price", 1088.70, null).accept(price));
		assertFalse(new SimpleBeanFilter().lt("price", 1088.00, null).accept(price));
		assertTrue(new SimpleBeanFilter().lt("date", df.parse("2009/12/06"), null).accept(price));
		assertFalse(new SimpleBeanFilter().lt("date", df.parse("2009/11/06"), null).accept(price));
		assertFalse(new SimpleBeanFilter().lt("date", df.parse("2009/10/06"), null).accept(price));
	}

	@Test
	public void testGe() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertFalse(new SimpleBeanFilter().ge("price", 1098.00).accept(price));
		assertTrue(new SimpleBeanFilter().ge("price", 1088.70).accept(price));
		assertTrue(new SimpleBeanFilter().ge("price", 1088.00).accept(price));
		assertFalse(new SimpleBeanFilter().ge("date", df.parse("2009/12/06")).accept(price));
		assertTrue(new SimpleBeanFilter().ge("date", df.parse("2009/11/06")).accept(price));
		assertTrue(new SimpleBeanFilter().ge("date", df.parse("2009/10/06")).accept(price));
		assertFalse(new SimpleBeanFilter().ge("price", 1098.00, null).accept(price));
		assertTrue(new SimpleBeanFilter().ge("price", 1088.70, null).accept(price));
		assertTrue(new SimpleBeanFilter().ge("price", 1088.00, null).accept(price));
		assertFalse(new SimpleBeanFilter().ge("date", df.parse("2009/12/06"), null).accept(price));
		assertTrue(new SimpleBeanFilter().ge("date", df.parse("2009/11/06"), null).accept(price));
		assertTrue(new SimpleBeanFilter().ge("date", df.parse("2009/10/06"), null).accept(price));
	}

	@Test
	public void testLe() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertTrue(new SimpleBeanFilter().le("price", 1098.00).accept(price));
		assertTrue(new SimpleBeanFilter().le("price", 1088.70).accept(price));
		assertFalse(new SimpleBeanFilter().le("price", 1088.00).accept(price));
		assertTrue(new SimpleBeanFilter().le("date", df.parse("2009/12/06")).accept(price));
		assertTrue(new SimpleBeanFilter().le("date", df.parse("2009/11/06")).accept(price));
		assertFalse(new SimpleBeanFilter().le("date", df.parse("2009/10/06")).accept(price));
		assertTrue(new SimpleBeanFilter().le("price", 1098.00, null).accept(price));
		assertTrue(new SimpleBeanFilter().le("price", 1088.70, null).accept(price));
		assertFalse(new SimpleBeanFilter().le("price", 1088.00, null).accept(price));
		assertTrue(new SimpleBeanFilter().le("date", df.parse("2009/12/06"), null).accept(price));
		assertTrue(new SimpleBeanFilter().le("date", df.parse("2009/11/06"), null).accept(price));
		assertFalse(new SimpleBeanFilter().le("date", df.parse("2009/10/06"), null).accept(price));
	}

	@Test
	public void testBetween() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertTrue(new SimpleBeanFilter().between("price", 1000.00, 1098.00).accept(price));
		assertTrue(new SimpleBeanFilter().between("price", 1000.00, 1088.70).accept(price));
		assertFalse(new SimpleBeanFilter().between("price", 1000.00, 1088.00).accept(price));
		assertTrue(new SimpleBeanFilter().between("price", 1000.00, 1098.00, null).accept(price));
		assertTrue(new SimpleBeanFilter().between("price", 1000.00, 1088.70, null).accept(price));
		assertFalse(new SimpleBeanFilter().between("price", 1000.00, 1088.00, null).accept(price));
		assertTrue(new SimpleBeanFilter().between("date", df.parse("2009/09/06"), df.parse("2009/12/06"), null).accept(price));
		assertTrue(new SimpleBeanFilter().between("date", df.parse("2009/09/06"), df.parse("2009/11/06"), null).accept(price));
		assertFalse(new SimpleBeanFilter().between("date", df.parse("2009/09/06"), df.parse("2009/10/06"), null).accept(price));
		assertTrue(new SimpleBeanFilter().between("date", df.parse("2009/09/06"), df.parse("2009/12/06"), null).accept(price));
		assertTrue(new SimpleBeanFilter().between("date", df.parse("2009/09/06"), df.parse("2009/11/06"), null).accept(price));
		assertFalse(new SimpleBeanFilter().between("date", df.parse("2009/09/06"), df.parse("2009/10/06"), null).accept(price));
	}

	@Test
	public void testNot() throws IOException {
		assertTrue(new SimpleBeanFilter().not(new BeanFilter() { @Override public boolean accept(final Object bean) { return false; } }).accept(null));
		assertFalse(new SimpleBeanFilter().not(new BeanFilter() { @Override public boolean accept(final Object bean) { return true; } }).accept(null));
	}

	@Test
	public void testToString() {
		assertThat(new SimpleBeanFilter().toString(), is("SimpleBeanFilter"));
		
	}

}
