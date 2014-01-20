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

import java.util.regex.Pattern;

import org.junit.Test;

/**
 * {@link CsvExpressions} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
public class CsvExpressionsTest {

	@Test
	public void testIsNull() {
		assertThat(CsvExpressions.isNull(0), instanceOf(ColumnPositionNullExpression.class));
		assertThat(CsvExpressions.isNull("col"), instanceOf(ColumnNameNullExpression.class));
	}

	@Test
	public void testNotNull() {
		assertThat(CsvExpressions.isNotNull(0), instanceOf(ColumnPositionNotNullExpression.class));
		assertThat(CsvExpressions.isNotNull("col"), instanceOf(ColumnNameNotNullExpression.class));
	}

	@Test
	public void testIsEmpty() {
		assertThat(CsvExpressions.isEmpty(0), instanceOf(ColumnPositionEmptyExpression.class));
		assertThat(CsvExpressions.isEmpty("col"), instanceOf(ColumnNameEmptyExpression.class));
	}

	@Test
	public void testIsNotEmpty() {
		assertThat(CsvExpressions.isNotEmpty(0), instanceOf(ColumnPositionNotEmptyExpression.class));
		assertThat(CsvExpressions.isNotEmpty("col"), instanceOf(ColumnNameNotEmptyExpression.class));
	}

	@Test
	public void testEq() {
		assertThat(CsvExpressions.eq(0, "val"), instanceOf(ColumnPositionEqualExpression.class));
		assertThat(CsvExpressions.eq(0, "val", false), instanceOf(ColumnPositionEqualExpression.class));
		assertThat(CsvExpressions.eq(0, "val", true), instanceOf(ColumnPositionEqualExpression.class));
		assertThat(CsvExpressions.eq("col", "val"), instanceOf(ColumnNameEqualExpression.class));
		assertThat(CsvExpressions.eq("col", "val", false), instanceOf(ColumnNameEqualExpression.class));
		assertThat(CsvExpressions.eq("col", "val", true), instanceOf(ColumnNameEqualExpression.class));
	}

	@Test
	public void testNe() {
		assertThat(CsvExpressions.ne(0, "val"), instanceOf(ColumnPositionNotEqualExpression.class));
		assertThat(CsvExpressions.ne(0, "val", false), instanceOf(ColumnPositionNotEqualExpression.class));
		assertThat(CsvExpressions.ne(0, "val", true), instanceOf(ColumnPositionNotEqualExpression.class));
		assertThat(CsvExpressions.ne("col", "val"), instanceOf(ColumnNameNotEqualExpression.class));
		assertThat(CsvExpressions.ne("col", "val", false), instanceOf(ColumnNameNotEqualExpression.class));
		assertThat(CsvExpressions.ne("col", "val", true), instanceOf(ColumnNameNotEqualExpression.class));
	}

	@Test
	public void testIn() {
		assertThat(CsvExpressions.in(0, "val1", "val2", "val3"), instanceOf(ColumnPositionInExpression.class));
		assertThat(CsvExpressions.in(0, new String[]{ "val1", "val2", "val3" }, false), instanceOf(ColumnPositionInExpression.class));
		assertThat(CsvExpressions.in(0, new String[]{ "val1", "val2", "val3" }, true), instanceOf(ColumnPositionInExpression.class));
		assertThat(CsvExpressions.in("col", "val1", "val2", "val3"), instanceOf(ColumnNameInExpression.class));
		assertThat(CsvExpressions.in("col", new String[]{ "val1", "val2", "val3" }, false), instanceOf(ColumnNameInExpression.class));
		assertThat(CsvExpressions.in("col", new String[]{ "val1", "val2", "val3" }, true), instanceOf(ColumnNameInExpression.class));
	}

	@Test
	public void testNotIn() {
		assertThat(CsvExpressions.notIn(0, "val1", "val2", "val3"), instanceOf(ColumnPositionNotInExpression.class));
		assertThat(CsvExpressions.notIn(0, new String[]{ "val1", "val2", "val3" }, false), instanceOf(ColumnPositionNotInExpression.class));
		assertThat(CsvExpressions.notIn(0, new String[]{ "val1", "val2", "val3" }, true), instanceOf(ColumnPositionNotInExpression.class));
		assertThat(CsvExpressions.notIn("col", "val1", "val2", "val3"), instanceOf(ColumnNameNotInExpression.class));
		assertThat(CsvExpressions.notIn("col", new String[]{ "val1", "val2", "val3" }, false), instanceOf(ColumnNameNotInExpression.class));
		assertThat(CsvExpressions.notIn("col", new String[]{ "val1", "val2", "val3" }, true), instanceOf(ColumnNameNotInExpression.class));
	}

	@Test
	public void testRegex() {
		assertThat(CsvExpressions.regex(0, "^.*$"), instanceOf(ColumnPositionRegexExpression.class));
		assertThat(CsvExpressions.regex(0, "^.*$", false), instanceOf(ColumnPositionRegexExpression.class));
		assertThat(CsvExpressions.regex(0, "^.*$", Pattern.CASE_INSENSITIVE), instanceOf(ColumnPositionRegexExpression.class));
		assertThat(CsvExpressions.regex(0, Pattern.compile("^.*$")), instanceOf(ColumnPositionRegexExpression.class));
		assertThat(CsvExpressions.regex("col", "^.*$"), instanceOf(ColumnNameRegexExpression.class));
		assertThat(CsvExpressions.regex("col", "^.*$", false), instanceOf(ColumnNameRegexExpression.class));
		assertThat(CsvExpressions.regex("col", "^.*$", Pattern.CASE_INSENSITIVE), instanceOf(ColumnNameRegexExpression.class));
		assertThat(CsvExpressions.regex("col", Pattern.compile("^.*$")), instanceOf(ColumnNameRegexExpression.class));
	}

	@Test
	public void testGt() {
		assertThat(CsvExpressions.gt(0, "val"), instanceOf(ColumnPositionGreaterThanExpression.class));
		assertThat(CsvExpressions.gt("col", "val"), instanceOf(ColumnNameGreaterThanExpression.class));
	}

	@Test
	public void testLt() {
		assertThat(CsvExpressions.lt(0, "val"), instanceOf(ColumnPositionLessThanExpression.class));
		assertThat(CsvExpressions.lt("col", "val"), instanceOf(ColumnNameLessThanExpression.class));
	}

	@Test
	public void testGe() {
		assertThat(CsvExpressions.ge(0, "val"), instanceOf(ColumnPositionGreaterThanOrEqualExpression.class));
		assertThat(CsvExpressions.ge("col", "val"), instanceOf(ColumnNameGreaterThanOrEqualExpression.class));
	}

	@Test
	public void testLe() {
		assertThat(CsvExpressions.le(0, "val"), instanceOf(ColumnPositionLessThanOrEqualExpression.class));
		assertThat(CsvExpressions.le("col", "val"), instanceOf(ColumnNameLessThanOrEqualExpression.class));
	}

	@Test
	public void testBetween() {
		assertThat(CsvExpressions.between(0, "val1", "val2"), instanceOf(ColumnPositionBetweenExpression.class));
		assertThat(CsvExpressions.between("col", "val1", "val2"), instanceOf(ColumnNameBetweenExpression.class));
	}

	@Test
	public void testAnd() {
		assertThat(CsvExpressions.and((CsvValueFilter) CsvExpressions.isNull(0), (CsvValueFilter) CsvExpressions.isNull(1)), instanceOf(CsvValueAndExpression.class));
		assertThat(CsvExpressions.and((CsvNamedValueFilter) CsvExpressions.isNull(0), (CsvNamedValueFilter) CsvExpressions.isNull(1)), instanceOf(CsvNamedValueAndExpression.class));
	}

	@Test
	public void testOr() {
		assertThat(CsvExpressions.or((CsvValueFilter) CsvExpressions.isNull(0), (CsvValueFilter) CsvExpressions.isNull(1)), instanceOf(CsvValueOrExpression.class));
		assertThat(CsvExpressions.or((CsvNamedValueFilter) CsvExpressions.isNull(0), (CsvNamedValueFilter) CsvExpressions.isNull(1)), instanceOf(CsvNamedValueOrExpression.class));
	}

	@Test
	public void testNot() {
		assertThat(CsvExpressions.not((CsvValueFilter) CsvExpressions.isNull(0)), instanceOf(CsvValueNotExpression.class));
		assertThat(CsvExpressions.not((CsvNamedValueFilter) CsvExpressions.isNull(0)), instanceOf(CsvNamedValueNotExpression.class));
	}

}