/*
 * Copyright 2010 the original author or authors.
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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.orangesignal.csv.model.SamplePrice;

/**
 * {@link BeanOrderComparator} クラスの単体テストです。
 *
 * @author Koji Sugisawa
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