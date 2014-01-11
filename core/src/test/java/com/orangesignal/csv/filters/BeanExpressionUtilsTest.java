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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.junit.Test;

import com.orangesignal.csv.entity.Price;
import com.orangesignal.csv.filters.BeanExpressionUtils;

/**
 * {@link BeanExpressionUtils} クラスの単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class BeanExpressionUtilsTest {

	@Test
	public void testIsNull() throws IOException {
		assertTrue(BeanExpressionUtils.isNull(new Price(null, "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "symbol"));
		assertFalse(BeanExpressionUtils.isNull(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "symbol"));
		assertTrue(BeanExpressionUtils.isNull(new Price("GCX09", null, 1088.70, 100, new Date()), "name"));
		assertFalse(BeanExpressionUtils.isNull(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "name"));
		assertTrue(BeanExpressionUtils.isNull(new Price("GCX09", "COMEX 金 2009年11月限", null, 100, new Date()), "price"));
		assertFalse(BeanExpressionUtils.isNull(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "price"));
		assertTrue(BeanExpressionUtils.isNull(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, null), "date"));
		assertFalse(BeanExpressionUtils.isNull(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "date"));
	}

	@Test
	public void testNotNull() throws IOException {
		assertFalse(BeanExpressionUtils.isNotNull(new Price(null, "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "symbol"));
		assertTrue(BeanExpressionUtils.isNotNull(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "symbol"));
		assertFalse(BeanExpressionUtils.isNotNull(new Price("GCX09", null, 1088.70, 100, new Date()), "name"));
		assertTrue(BeanExpressionUtils.isNotNull(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "name"));
		assertFalse(BeanExpressionUtils.isNotNull(new Price("GCX09", "COMEX 金 2009年11月限", null, 100, new Date()), "price"));
		assertTrue(BeanExpressionUtils.isNotNull(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "price"));
		assertFalse(BeanExpressionUtils.isNotNull(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, null), "date"));
		assertTrue(BeanExpressionUtils.isNotNull(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "date"));
	}

	@Test
	public void testIsEmpty() throws IOException {
		assertTrue(BeanExpressionUtils.isEmpty(new Price(null, "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "symbol"));
		assertTrue(BeanExpressionUtils.isEmpty(new Price("", "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "symbol"));
		assertFalse(BeanExpressionUtils.isEmpty(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "symbol"));
		assertTrue(BeanExpressionUtils.isEmpty(new Price("GCX09", null, 1088.70, 100, new Date()), "name"));
		assertTrue(BeanExpressionUtils.isEmpty(new Price("GCX09", "", 1088.70, 100, new Date()), "name"));
		assertFalse(BeanExpressionUtils.isEmpty(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "name"));
		assertTrue(BeanExpressionUtils.isEmpty(new Price("GCX09", "COMEX 金 2009年11月限", null, 100, new Date()), "price"));
		assertFalse(BeanExpressionUtils.isEmpty(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "price"));
		assertTrue(BeanExpressionUtils.isEmpty(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, null), "date"));
		assertFalse(BeanExpressionUtils.isEmpty(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "date"));
	}

	@Test
	public void testIsNotEmpty() throws IOException {
		assertFalse(BeanExpressionUtils.isNotEmpty(new Price(null, "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "symbol"));
		assertFalse(BeanExpressionUtils.isNotEmpty(new Price("", "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "symbol"));
		assertTrue(BeanExpressionUtils.isNotEmpty(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "symbol"));
		assertFalse(BeanExpressionUtils.isNotEmpty(new Price("GCX09", null, 1088.70, 100, new Date()), "name"));
		assertFalse(BeanExpressionUtils.isNotEmpty(new Price("GCX09", "", 1088.70, 100, new Date()), "name"));
		assertTrue(BeanExpressionUtils.isNotEmpty(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "name"));
		assertFalse(BeanExpressionUtils.isNotEmpty(new Price("GCX09", "COMEX 金 2009年11月限", null, 100, new Date()), "price"));
		assertTrue(BeanExpressionUtils.isNotEmpty(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "price"));
		assertFalse(BeanExpressionUtils.isNotEmpty(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, null), "date"));
		assertTrue(BeanExpressionUtils.isNotEmpty(new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new Date()), "date"));
	}

	@Test
	public void testEq() throws Exception {
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2009/11/06 19:14:46"));
		assertFalse(BeanExpressionUtils.eq(price, "symbol", "SIX09", false));
		assertTrue(BeanExpressionUtils.eq(price, "symbol", "GCX09", false));
		assertFalse(BeanExpressionUtils.eq(price, "name", "COMEX 銀 2009年11月限", false));
		assertTrue(BeanExpressionUtils.eq(price, "name", "COMEX 金 2009年11月限", false));
		assertFalse(BeanExpressionUtils.eq(price, "price", 1088.00, false));
		assertTrue(BeanExpressionUtils.eq(price, "price", 1088.70, false));
		assertFalse(BeanExpressionUtils.eq(price, "date", new Date(), false));
		assertTrue(BeanExpressionUtils.eq(price, "date", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2009/11/06 19:14:46"), false));
	}

	@Test
	public void testNe() throws Exception {
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2009/11/06 19:14:46"));
		assertTrue(BeanExpressionUtils.ne(price, "symbol", "SIX09", false));
		assertFalse(BeanExpressionUtils.ne(price, "symbol", "GCX09", false));
		assertTrue(BeanExpressionUtils.ne(price, "name", "COMEX 銀 2009年11月限", false));
		assertFalse(BeanExpressionUtils.ne(price, "name", "COMEX 金 2009年11月限", false));
		assertTrue(BeanExpressionUtils.ne(price, "price", 1088.00, false));
		assertFalse(BeanExpressionUtils.ne(price, "price", 1088.70, false));
		assertTrue(BeanExpressionUtils.ne(price, "date", new Date(), false));
		assertFalse(BeanExpressionUtils.ne(price, "date", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2009/11/06 19:14:46"), false));
	}

	@Test
	public void testIn() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertFalse(BeanExpressionUtils.in(price, "symbol", new Object[]{ "SIU09", "SIV09", "SIX09" }, false));
		assertTrue(BeanExpressionUtils.in(price, "symbol", new Object[]{ "GCU09", "GCV09", "GCX09" }, false));
		assertFalse(BeanExpressionUtils.in(price, "name", new Object[]{ "COMEX 銀 2009年9月限", "COMEX 銀 2009年10月限", "COMEX 銀 2009年11月限" }, false));
		assertTrue(BeanExpressionUtils.in(price, "name", new Object[]{ "COMEX 金 2009年9月限", "COMEX 金 2009年10月限", "COMEX 金 2009年11月限" }, false));
		assertFalse(BeanExpressionUtils.in(price, "price", new Object[]{ 1068.00, 1078.00, 1088.00 }, false));
		assertTrue(BeanExpressionUtils.in(price, "price", new Object[]{ 1068.70, 1078.70, 1088.70 }, false));
		assertFalse(BeanExpressionUtils.in(price, "date", new Object[]{ df.parse("2008/09/06"), df.parse("2008/10/06"), df.parse("2008/11/06") }, false));
		assertTrue(BeanExpressionUtils.in(price, "date", new Object[]{ df.parse("2009/09/06"), df.parse("2009/10/06"), df.parse("2009/11/06") }, false));
	}

	@Test
	public void testNotIn() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertTrue(BeanExpressionUtils.notIn(price, "symbol", new Object[]{ "SIU09", "SIV09", "SIX09" }, false));
		assertFalse(BeanExpressionUtils.notIn(price, "symbol", new Object[]{ "GCU09", "GCV09", "GCX09" }, false));
		assertTrue(BeanExpressionUtils.notIn(price, "name", new Object[]{ "COMEX 銀 2009年9月限", "COMEX 銀 2009年10月限", "COMEX 銀 2009年11月限" }, false));
		assertFalse(BeanExpressionUtils.notIn(price, "name", new Object[]{ "COMEX 金 2009年9月限", "COMEX 金 2009年10月限", "COMEX 金 2009年11月限" }, false));
		assertTrue(BeanExpressionUtils.notIn(price, "price", new Object[]{ 1068.00, 1078.00, 1088.00 }, false));
		assertFalse(BeanExpressionUtils.notIn(price, "price", new Object[]{ 1068.70, 1078.70, 1088.70 }, false));
		assertTrue(BeanExpressionUtils.notIn(price, "date", new Object[]{ df.parse("2008/09/06"), df.parse("2008/10/06"), df.parse("2008/11/06") }, false));
		assertFalse(BeanExpressionUtils.notIn(price, "date", new Object[]{ df.parse("2009/09/06"), df.parse("2009/10/06"), df.parse("2009/11/06") }, false));
	}

	@Test
	public void testRegex() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertTrue(BeanExpressionUtils.regex(price, "symbol", Pattern.compile("^GCX[0-9]{1,2}$")));
		assertFalse(BeanExpressionUtils.regex(price, "name", Pattern.compile("^GCX[0-9]{1,2}$")));
	}

	@Test
	public void testGt() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertFalse(BeanExpressionUtils.gt(price, "price", 1098.00, null));
		assertFalse(BeanExpressionUtils.gt(price, "price", 1088.70, null));
		assertTrue(BeanExpressionUtils.gt(price, "price", 1088.00, null));
		assertFalse(BeanExpressionUtils.gt(price, "date", df.parse("2009/12/06"), null));
		assertFalse(BeanExpressionUtils.gt(price, "date", df.parse("2009/11/06"), null));
		assertTrue(BeanExpressionUtils.gt(price, "date", df.parse("2009/10/06"), null));
	}

	@Test
	public void testLt() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertTrue(BeanExpressionUtils.lt(price, "price", 1098.00, null));
		assertFalse(BeanExpressionUtils.lt(price, "price", 1088.70, null));
		assertFalse(BeanExpressionUtils.lt(price, "price", 1088.00, null));
		assertTrue(BeanExpressionUtils.lt(price, "date", df.parse("2009/12/06"), null));
		assertFalse(BeanExpressionUtils.lt(price, "date", df.parse("2009/11/06"), null));
		assertFalse(BeanExpressionUtils.lt(price, "date", df.parse("2009/10/06"), null));
	}

	@Test
	public void testGe() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertFalse(BeanExpressionUtils.ge(price, "price", 1098.00, null));
		assertTrue(BeanExpressionUtils.ge(price, "price", 1088.70, null));
		assertTrue(BeanExpressionUtils.ge(price, "price", 1088.00, null));
		assertFalse(BeanExpressionUtils.ge(price, "date", df.parse("2009/12/06"), null));
		assertTrue(BeanExpressionUtils.ge(price, "date", df.parse("2009/11/06"), null));
		assertTrue(BeanExpressionUtils.ge(price, "date", df.parse("2009/10/06"), null));
	}

	@Test
	public void testLe() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertTrue(BeanExpressionUtils.le(price, "price", 1098.00, null));
		assertTrue(BeanExpressionUtils.le(price, "price", 1088.70, null));
		assertFalse(BeanExpressionUtils.le(price, "price", 1088.00, null));
		assertTrue(BeanExpressionUtils.le(price, "date", df.parse("2009/12/06"), null));
		assertTrue(BeanExpressionUtils.le(price, "date", df.parse("2009/11/06"), null));
		assertFalse(BeanExpressionUtils.le(price, "date", df.parse("2009/10/06"), null));
	}

	@Test
	public void testBetween() throws Exception {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		final Price price = new Price("GCX09", "COMEX 金 2009年11月限", 1088.70, 100, df.parse("2009/11/06"));
		assertTrue(BeanExpressionUtils.between(price, "price", 1000.00, 1098.00, null));
		assertTrue(BeanExpressionUtils.between(price, "price", 1000.00, 1088.70, null));
		assertFalse(BeanExpressionUtils.between(price, "price", 1000.00, 1088.00, null));
		assertTrue(BeanExpressionUtils.between(price, "date", df.parse("2009/09/06"), df.parse("2009/12/06"), null));
		assertTrue(BeanExpressionUtils.between(price, "date", df.parse("2009/09/06"), df.parse("2009/11/06"), null));
		assertFalse(BeanExpressionUtils.between(price, "date", df.parse("2009/09/06"), df.parse("2009/10/06"), null));
	}

}
