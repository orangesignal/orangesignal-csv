/*
 * Copyright 2009 the original author or authors.
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

package com.orangesignal.csv.filters;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * {@link BeanExpressions} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
public class BeanExpressionsTest {

	@Test
	public void testIsNull() {
		assertThat(BeanExpressions.isNull("field"), instanceOf(BeanNullExpression.class));
	}

	@Test
	public void testNotNull() {
		assertThat(BeanExpressions.isNotNull("field"), instanceOf(BeanNotNullExpression.class));
	}

	@Test
	public void testIsEmpty() {
		assertThat(BeanExpressions.isEmpty("col"), instanceOf(BeanEmptyExpression.class));
	}

	@Test
	public void testIsNotEmpty() {
		assertThat(BeanExpressions.isNotEmpty("col"), instanceOf(BeanNotEmptyExpression.class));
	}

	@Test
	public void testEq() {
		assertThat(BeanExpressions.eq("field", "val", false), instanceOf(BeanEqualExpression.class));
		assertThat(BeanExpressions.eq("field", "val", true), instanceOf(BeanEqualExpression.class));
		assertThat(BeanExpressions.eq("field", "val"), instanceOf(BeanEqualExpression.class));
		assertThat(BeanExpressions.eq("field", 100.02), instanceOf(BeanEqualExpression.class));
		assertThat(BeanExpressions.eq("field", new Date()), instanceOf(BeanEqualExpression.class));
	}

	@Test
	public void testNe() {
		assertThat(BeanExpressions.ne("field", "val", false), instanceOf(BeanNotEqualExpression.class));
		assertThat(BeanExpressions.ne("field", "val", true), instanceOf(BeanNotEqualExpression.class));
		assertThat(BeanExpressions.ne("field", "val"), instanceOf(BeanNotEqualExpression.class));
		assertThat(BeanExpressions.ne("field", 100.02), instanceOf(BeanNotEqualExpression.class));
		assertThat(BeanExpressions.ne("field", new Date()), instanceOf(BeanNotEqualExpression.class));
	}

	@Test
	public void testIn() {
		assertThat(BeanExpressions.in("field", new String[]{ "val1", "val2", "val3" }, false), instanceOf(BeanInExpression.class));
		assertThat(BeanExpressions.in("field", new String[]{ "val1", "val2", "val3" }, true), instanceOf(BeanInExpression.class));
		assertThat(BeanExpressions.in("field", "val1", "val2", "val3"), instanceOf(BeanInExpression.class));
		assertThat(BeanExpressions.in("field", 100.01, 100.02, 100.03), instanceOf(BeanInExpression.class));
		assertThat(BeanExpressions.in("field", new Date(), new Date(), new Date()), instanceOf(BeanInExpression.class));
	}

	@Test
	public void testNotIn() {
		assertThat(BeanExpressions.notIn("field", new String[]{ "val1", "val2", "val3" }, false), instanceOf(BeanNotInExpression.class));
		assertThat(BeanExpressions.notIn("field", new String[]{ "val1", "val2", "val3" }, true), instanceOf(BeanNotInExpression.class));
		assertThat(BeanExpressions.notIn("field", "val1", "val2", "val3"), instanceOf(BeanNotInExpression.class));
		assertThat(BeanExpressions.notIn("field", 100.01, 100.02, 100.03), instanceOf(BeanNotInExpression.class));
		assertThat(BeanExpressions.notIn("field", new Date(), new Date(), new Date()), instanceOf(BeanNotInExpression.class));
	}

	@Test
	public void testRegex() {
		assertThat(BeanExpressions.regex("col", "^.*$"), instanceOf(BeanRegexExpression.class));
		assertThat(BeanExpressions.regex("col", "^.*$", false), instanceOf(BeanRegexExpression.class));
		assertThat(BeanExpressions.regex("col", "^.*$", Pattern.CASE_INSENSITIVE), instanceOf(BeanRegexExpression.class));
		assertThat(BeanExpressions.regex("col", Pattern.compile("^.*$")), instanceOf(BeanRegexExpression.class));
	}

	@Test
	public void testGt() {
		assertThat(BeanExpressions.gt("field", "val", null), instanceOf(BeanGreaterThanExpression.class));
		assertThat(BeanExpressions.gt("field", "val"), instanceOf(BeanGreaterThanExpression.class));
		assertThat(BeanExpressions.gt("field", 100.02, null), instanceOf(BeanGreaterThanExpression.class));
		assertThat(BeanExpressions.gt("field", 100.02), instanceOf(BeanGreaterThanExpression.class));
		assertThat(BeanExpressions.gt("field", new Date(), null), instanceOf(BeanGreaterThanExpression.class));
		assertThat(BeanExpressions.gt("field", new Date()), instanceOf(BeanGreaterThanExpression.class));
	}

	@Test
	public void testLt() {
		assertThat(BeanExpressions.lt("field", "val", null), instanceOf(BeanLessThanExpression.class));
		assertThat(BeanExpressions.lt("field", "val"), instanceOf(BeanLessThanExpression.class));
		assertThat(BeanExpressions.lt("field", 100.02, null), instanceOf(BeanLessThanExpression.class));
		assertThat(BeanExpressions.lt("field", 100.02), instanceOf(BeanLessThanExpression.class));
		assertThat(BeanExpressions.lt("field", new Date(), null), instanceOf(BeanLessThanExpression.class));
		assertThat(BeanExpressions.lt("field", new Date()), instanceOf(BeanLessThanExpression.class));
	}

	@Test
	public void testGe() {
		assertThat(BeanExpressions.ge("field", "val", null), instanceOf(BeanGreaterThanOrEqualExpression.class));
		assertThat(BeanExpressions.ge("field", "val"), instanceOf(BeanGreaterThanOrEqualExpression.class));
		assertThat(BeanExpressions.ge("field", 100.02, null), instanceOf(BeanGreaterThanOrEqualExpression.class));
		assertThat(BeanExpressions.ge("field", 100.02), instanceOf(BeanGreaterThanOrEqualExpression.class));
		assertThat(BeanExpressions.ge("field", new Date(), null), instanceOf(BeanGreaterThanOrEqualExpression.class));
		assertThat(BeanExpressions.ge("field", new Date()), instanceOf(BeanGreaterThanOrEqualExpression.class));
	}

	@Test
	public void testLe() {
		assertThat(BeanExpressions.le("field", "val", null), instanceOf(BeanLessThanOrEqualExpression.class));
		assertThat(BeanExpressions.le("field", "val"), instanceOf(BeanLessThanOrEqualExpression.class));
		assertThat(BeanExpressions.le("field", 100.02, null), instanceOf(BeanLessThanOrEqualExpression.class));
		assertThat(BeanExpressions.le("field", 100.02), instanceOf(BeanLessThanOrEqualExpression.class));
		assertThat(BeanExpressions.le("field", new Date(), null), instanceOf(BeanLessThanOrEqualExpression.class));
		assertThat(BeanExpressions.le("field", new Date()), instanceOf(BeanLessThanOrEqualExpression.class));
	}

	@Test
	public void testBetween() {
		assertThat(BeanExpressions.between("field", "val1", "val2", null), instanceOf(BeanBetweenExpression.class));
		assertThat(BeanExpressions.between("field", "val1", "val2"), instanceOf(BeanBetweenExpression.class));
		assertThat(BeanExpressions.between("field", 100.02, 1000.15, null), instanceOf(BeanBetweenExpression.class));
		assertThat(BeanExpressions.between("field", 100.02, 1000.15), instanceOf(BeanBetweenExpression.class));
		assertThat(BeanExpressions.between("field", new Date(), new Date(), null), instanceOf(BeanBetweenExpression.class));
		assertThat(BeanExpressions.between("field", new Date(), new Date()), instanceOf(BeanBetweenExpression.class));
	}

	@Test
	public void testAnd() {
		assertThat(BeanExpressions.and(BeanExpressions.isNull("field1"), BeanExpressions.isNull("field2")), instanceOf(BeanAndExpression.class));
	}

	@Test
	public void testOr() {
		assertThat(BeanExpressions.or(BeanExpressions.isNull("field1"), BeanExpressions.isNull("field2")), instanceOf(BeanOrExpression.class));
	}

	@Test
	public void testNot() {
		assertThat(BeanExpressions.not(BeanExpressions.isNull("field")), instanceOf(BeanNotExpression.class));
	}

}