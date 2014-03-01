/*
 * Copyright 2013 the original author or authors.
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

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.regex.Pattern;

import com.orangesignal.csv.bean.FieldUtils;

/**
 * Java プログラム要素フィルタの条件式ユーティリティを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.2.3
 */
abstract class BeanExpressionUtils {

	/**
	 * デフォルトコンストラクタです。
	 */
	protected BeanExpressionUtils() {}

	// ------------------------------------------------------------------------

	/**
	 * 指定された Java プログラム要素のフィールド値が <code>null</code> であるかどうかを返します。
	 * 
	 * @param bean Java プログラム要素
	 * @param name フィールド名
	 * @return 指定された Java プログラム要素のフィールド値が <code>null</code> の場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IOException フィールド操作で例外が発生した場合
	 */
	public static boolean isNull(final Object bean, final String name) throws IOException {
		return getFieldValue(bean, name) == null;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が <code>null</code> でないかどうかを返します。
	 * 
	 * @param bean Java プログラム要素
	 * @param name フィールド名
	 * @return 指定された Java プログラム要素のフィールド値が <code>null</code> でない場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IOException フィールド操作で例外が発生した場合
	 */
	public static boolean isNotNull(final Object bean, final String name) throws IOException {
		return !isNull(bean, name);
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が空かどうかを返します。
	 * 
	 * @param bean Java プログラム要素
	 * @param name フィールド名
	 * @return 指定された Java プログラム要素のフィールド値が空の場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IOException フィールド操作で例外が発生した場合
	 */
	public static boolean isEmpty(final Object bean, final String name) throws IOException {
		final Field field = FieldUtils.getField(bean.getClass(), name);
		final Object value = FieldUtils.getFieldValue(bean, field);
		if (value == null) {
			return true;
		}
		if (field.getType().isArray()) {
			return ((Object[]) value).length == 0;
		}
		if (value instanceof String) {
			return ((String) value).isEmpty();
		} else if (value instanceof Collection) {
			return ((Collection<?>) value).isEmpty();
		} else if (value instanceof Map) {
			return ((Map<?, ?>) value).isEmpty();
		}
		return false;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が空でないかどうかを返します。
	 * 
	 * @param bean Java プログラム要素
	 * @param name フィールド名
	 * @return 指定された Java プログラム要素のフィールド値が空でない場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IOException フィールド操作で例外が発生した場合
	 */
	public static boolean isNotEmpty(final Object bean, final String name) throws IOException {
		return !isEmpty(bean, name);
	}

	// ------------------------------------------------------------------------

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値と等しいかどうかを返します。
	 * 
	 * @param bean Java プログラム要素
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 指定された Java プログラム要素のフィールド値が判定基準値と等しい場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IOException フィールド操作で例外が発生した場合
	 */
	public static boolean eq(final Object bean, final String name, final Object criteria, final boolean ignoreCase) throws IOException {
		final Object value = getFieldValue(bean, name);
		if (criteria == null) {
			throw new IllegalArgumentException("Criteria must not be null");
		}
		if (ignoreCase) {
			return value != null && ((String) criteria).equalsIgnoreCase((String) value);
		}
		return criteria.equals(value);
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値と等しくないかどうかを返します。
	 * 
	 * @param bean Java プログラム要素
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 指定された Java プログラム要素のフィールド値が判定基準値と等しくない場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IOException フィールド操作で例外が発生した場合
	 */
	public static boolean ne(final Object bean, final String name, final Object criteria, final boolean ignoreCase) throws IOException {
		return !eq(bean, name, criteria, ignoreCase);
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値群のいずれかと等しいかどうかを返します。
	 * 
	 * @param bean Java プログラム要素
	 * @param name フィールド名
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 指定された Java プログラム要素のフィールド値が判定基準値群のいずれかと等しい場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IOException フィールド操作で例外が発生した場合
	 */
	public static boolean in(final Object bean, final String name, final Object[] criterias, final boolean ignoreCase) throws IOException {
		if (criterias == null) {
			throw new IllegalArgumentException("Criterias must not be null");
		}
		for (final Object criteria : criterias) {
			if (eq(bean, name, criteria, ignoreCase)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値群のいずれとも等しくないかどうかを返します。
	 * 
	 * @param bean Java プログラム要素
	 * @param name フィールド名
	 * @param criterias 判定基準値群
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 指定された Java プログラム要素のフィールド値が判定基準値群のいずれとも等しくない場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IOException フィールド操作で例外が発生した場合
	 */
	public static boolean notIn(final Object bean, final String name, final Object[] criterias, final boolean ignoreCase) throws IOException {
		return !in(bean, name, criterias, ignoreCase);
	}

	// ------------------------------------------------------------------------

	/**
	 * 指定された Java プログラム要素のフィールド値が正規表現パターンとマッチするかどうかを返します。
	 * 
	 * @param bean Java プログラム要素
	 * @param name フィールド名
	 * @param pattern 判定する正規表現パターン
	 * @return 指定された Java プログラム要素のフィールド値が正規表現パターンとマッチする場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IOException フィールド操作で例外が発生した場合
	 */
	public static boolean regex(final Object bean, final String name, final Pattern pattern) throws IOException {
		final Object value = getFieldValue(bean, name);
		return value != null && pattern.matcher((CharSequence) value).matches();
	}

	// ------------------------------------------------------------------------

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値よりも大きいかどうかを返します。<p>
	 * この実装は、コンパレータが指定されている場合はコンパレータを使用して比較を行います。
	 * 
	 * @param bean Java プログラム要素
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param comparator コンパレータ (オプション)
	 * @return 指定された Java プログラム要素のフィールド値が判定基準値よりも大きい場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IOException フィールド操作で例外が発生した場合
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean gt(final Object bean, final String name, final Object criteria, final Comparator comparator) throws IOException {
		final Object value = getFieldValue(bean, name);
		if (comparator != null) {
			return value != null && criteria != null && comparator.compare(value, criteria) > 0;
		}
		return value != null && criteria != null && ((Comparable) value).compareTo(criteria) > 0;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値よりも小さいかどうかを返します。<p>
	 * この実装は、コンパレータが指定されている場合はコンパレータを使用して比較を行います。
	 * 
	 * @param bean Java プログラム要素
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param comparator コンパレータ (オプション)
	 * @return 指定された Java プログラム要素のフィールド値が判定基準値よりも小さい場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IOException フィールド操作で例外が発生した場合
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean lt(final Object bean, final String name, final Object criteria, final Comparator comparator) throws IOException {
		final Object value = getFieldValue(bean, name);
		if (comparator != null) {
			return value != null && criteria != null && comparator.compare(value, criteria) < 0;
		}
		return value != null && criteria != null && ((Comparable) value).compareTo(criteria) < 0;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値以上かどうかを返します。<p>
	 * この実装は、コンパレータが指定されている場合はコンパレータを使用して比較を行います。
	 * 
	 * @param bean Java プログラム要素
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param comparator コンパレータ (オプション)
	 * @return 指定された Java プログラム要素のフィールド値が判定基準値以上の場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IOException フィールド操作で例外が発生した場合
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean ge(final Object bean, final String name, final Object criteria, final Comparator comparator) throws IOException {
		final Object value = getFieldValue(bean, name);
		if (comparator != null) {
			return value != null && criteria != null && comparator.compare(value, criteria) >= 0;
		}
		return value != null && criteria != null && ((Comparable) value).compareTo(criteria) >= 0;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が判定基準値以下かどうかを返します。<p>
	 * この実装は、コンパレータが指定されている場合はコンパレータを使用して比較を行います。
	 * 
	 * @param bean Java プログラム要素
	 * @param name フィールド名
	 * @param criteria 判定基準値
	 * @param comparator コンパレータ (オプション)
	 * @return 指定された Java プログラム要素のフィールド値が判定基準値以下の場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IOException フィールド操作で例外が発生した場合
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean le(final Object bean, final String name, final Object criteria, final Comparator comparator) throws IOException {
		final Object value = getFieldValue(bean, name);
		if (comparator != null) {
			return value != null && criteria != null && comparator.compare(value, criteria) <= 0;
		}
		return value != null && criteria != null && ((Comparable) value).compareTo(criteria) <= 0;
	}

	/**
	 * 指定された Java プログラム要素のフィールド値が下限値から上限値の範囲かどうかを返します。<p>
	 * この実装は、コンパレータが指定されている場合はコンパレータを使用して比較を行います。
	 * 
	 * @param bean Java プログラム要素
	 * @param name フィールド名
	 * @param low 下限値
	 * @param high 上限値
	 * @param comparator コンパレータ (オプション)
	 * @return 指定された Java プログラム要素のフィールド値が下限値から上限値の範囲の場合は <code>true</code> それ以外の場合は <code>false</code>
	 * @throws IOException フィールド操作で例外が発生した場合
	 */
	@SuppressWarnings("rawtypes")
	public static boolean between(final Object bean, final String name, final Object low, final Object high, final Comparator comparator) throws IOException {
		return ge(bean, name, low, comparator) && le(bean, name, high, comparator);
	}

	/**
	 * 指定された Java プログラム要素について、指定されたフィールド名によって表されるフィールドの値を返します。
	 * プリミティブ型の場合、オブジェクト内に自動的に格納されてから返されます。
	 * 
	 * @param bean Java プログラム要素
	 * @param name フィールド名
	 * @return Java プログラム要素 <code>bean</code> 内で表現される値。プリミティブ値は適切なオブジェクト内にラップされてから返される
	 * @throws IOException 基本となるフィールドにアクセスできない場合。指定されたオブジェクトが基本となるフィールド (またはそのサブクラスか実装側) を宣言するクラスまたはインタフェースのインスタンスではない場合
	 * @throws NullPointerException 指定されたオブジェクトが null で、フィールドがインスタンスフィールドの場合
	 * @throws SecurityException 
	 */
	private static Object getFieldValue(final Object bean, final String name) throws IOException {
		return FieldUtils.getFieldValue(bean, FieldUtils.getField(bean.getClass(), name));
	}

}
