/*
 * Copyright (c) 2010 OrangeSignal.com All rights reserved.
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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Test;

import com.orangesignal.csv.handlers.BeanOrder;
import com.orangesignal.csv.model.SamplePrice;

/**
 * {@link BeanOrder} クラスの単体テストです。
 *
 * @author 杉澤 浩二
 */
public class BeanOrderTest {

	@Test
	public void testBeanOrder() {
		new BeanOrder("date", false, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBeanOrderIllegalArgumentException() {
		new BeanOrder(null, false, true);
	}

	@Test
	public void testAscString() {
		BeanOrder.asc("date");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAscStringIllegalArgumentException() {
		BeanOrder.asc(null);
	}

	@Test
	public void testAscStringBoolean() {
		BeanOrder.asc("symbol", true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAscStringBooleanIllegalArgumentException() {
		BeanOrder.asc(null, true);
	}

	@Test
	public void testDescString() {
		BeanOrder.desc("date");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDescStringIllegalArgumentException() {
		BeanOrder.desc(null);
	}

	@Test
	public void testDescStringBoolean() {
		BeanOrder.desc("symbol", true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDescStringBooleanIllegalArgumentException() {
		BeanOrder.desc(null, true);
	}

	@Test
	public void testCompare() {
		final Date now = new Date();
		final SamplePrice p1 = new SamplePrice();
		p1.setName("Y");
		p1.setDate(now);
		p1.setPrice(100);

		final SamplePrice p2 = new SamplePrice();
		p2.setName("X");
		p2.setDate(now);
		p2.setPrice(200);

		assertThat(BeanOrder.asc("name").compare(p1, p2), is(1));
		assertThat(BeanOrder.asc("date").compare(p1, p2), is(0));
		assertThat(BeanOrder.asc("price").compare(p1, p2), is(-1));
	}

	@Test
	public void testToString() {
		assertThat(BeanOrder.asc("date").toString(), is("date asc"));
		assertThat(BeanOrder.asc("symbol", true).toString(), is("symbol asc"));
		assertThat(BeanOrder.desc("date").toString(), is("date desc"));
		assertThat(BeanOrder.desc("symbol", true).toString(), is("symbol desc"));
	}

}
