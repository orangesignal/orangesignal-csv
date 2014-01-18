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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.orangesignal.csv.handlers.BeanOrder;
import com.orangesignal.csv.handlers.BeanOrderComparator;
import com.orangesignal.csv.model.SamplePrice;

/**
 * {@link BeanOrderComparator} クラスの単体テストです。
 *
 * @author 杉澤 浩二
 */
public class BeanOrderComparatorTest {

	@Test
	public void testBeanOrderComparator() {
		new BeanOrderComparator(new ArrayList<BeanOrder>(0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBeanOrderComparatorIllegalArgumentException() {
		new BeanOrderComparator(null);
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

		final List<BeanOrder> orders = new ArrayList<BeanOrder>();
		orders.add(BeanOrder.asc("date"));
		assertThat(new BeanOrderComparator(orders).compare(p1, p2), is(0));
		orders.add(BeanOrder.asc("price"));
		assertThat(new BeanOrderComparator(orders).compare(p1, p2), is(-1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCompareIllegalArgumentException1() {
		new BeanOrderComparator(new ArrayList<BeanOrder>(0)).compare(null, new Object());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCompareIllegalArgumentException2() {
		new BeanOrderComparator(new ArrayList<BeanOrder>(0)).compare(new Object(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCompareIllegalArgumentException3() {
		new BeanOrderComparator(new ArrayList<BeanOrder>(0)).compare(null, null);
	}

	@Test
	public void testSort() {
		final Date now = new Date();
		final SamplePrice p1 = new SamplePrice();
		p1.setName("Y");
		p1.setDate(now);
		p1.setPrice(100);

		final SamplePrice p2 = new SamplePrice();
		p2.setName("X");
		p2.setDate(now);
		p2.setPrice(200);

		final List<SamplePrice> beans = new ArrayList<SamplePrice>(2);
		beans.add(p1);
		beans.add(p2);

		final List<BeanOrder> orders = new ArrayList<BeanOrder>();
		orders.add(BeanOrder.asc("date"));
		orders.add(BeanOrder.asc("name"));

		BeanOrderComparator.sort(beans, orders);

		assertThat(beans.get(0), is(p2));
		assertThat(beans.get(1), is(p1));
	}

}
