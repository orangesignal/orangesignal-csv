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

package com.orangesignal.csv.handlers;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Comparator;

/**
 * Java プログラム要素の並び替え条件情報を提供します。
 *
 * @author Koji Sugisawa
 * @since 1.2.8
 */
public class BeanOrder implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = 4724322731264848338L;

	/**
	 * フィールド名を保持します。
	 */
	private String name;

	/**
	 * 大文字と小文字を区別するかどうかを保持します。
	 */
	private boolean ignoreCase;

	/**
	 * 昇順に並び替えるかどうかを保持します。
	 */
	private boolean ascending;

	/**
	 * コンストラクタです。
	 * 
	 * @param name フィールド名
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @param ascending 昇順に並び替えるかどうか
	 * @throws IllegalArgumentException {@code name} が {@code null} または空の場合
	 */
	protected BeanOrder(final String name, final boolean ignoreCase, final boolean ascending) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("propertyName must not be null or empty");
		}
		this.name = name;
		this.ignoreCase = ignoreCase;
		this.ascending = ascending;
	}

	/**
	 * 昇順の並び替え条件を構築して返します。
	 *
	 * @param name フィールド名
	 * @return 並び替え条件
	 * @throws IllegalArgumentException {@code name} が {@code null} または空の場合
	 */
	public static BeanOrder asc(final String name) {
		return new BeanOrder(name, false, true);
	}

	/**
	 * 昇順の並び替え条件を構築して返します。
	 *
	 * @param name フィールド名
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 並び替え条件
	 * @throws IllegalArgumentException {@code name} が {@code null} または空の場合
	 */
	public static BeanOrder asc(final String name, final boolean ignoreCase) {
		return new BeanOrder(name, ignoreCase, true);
	}

	/**
	 * 降順の並び替え条件を構築して返します。
	 *
	 * @param name フィールド名
	 * @return 並び替え条件
	 * @throws IllegalArgumentException {@code name} が {@code null} または空の場合
	 */
	public static BeanOrder desc(final String name) {
		return new BeanOrder(name, false, false);
	}

	/**
	 * 降順の並び替え条件を構築して返します。
	 *
	 * @param name フィールド名
	 * @param ignoreCase 大文字と小文字を区別するかどうか
	 * @return 並び替え条件
	 * @throws IllegalArgumentException {@code name} が {@code null} または空の場合
	 */
	public static BeanOrder desc(final String name, final boolean ignoreCase) {
		return new BeanOrder(name, ignoreCase, false);
	}

	@Override
	public int compare(final Object o1, final Object o2) {
		Object v1 = getValue(o1);
		Object v2 = getValue(o2);
		if (ignoreCase && v1 instanceof String && v2 instanceof String) {
			v1 = ((String) v1).toLowerCase();
			v2 = ((String) v2).toLowerCase();
		}
		return compareValue(v1, v2) * (ascending ? 1 : -1);
	}

	/**
	 * 指定された Java プログラム要素について、フィールド名によって表されるフィールドの値を返します。
	 * プリミティブ型の場合、オブジェクト内に自動的に格納されてから返されます。
	 * 
	 * @param bean Java プログラム要素
	 * @return Java プログラム要素 {@code bean} 内で表現される値。プリミティブ値は適切なオブジェクト内にラップされてから返される
	 * @throws IllegalArgumentException 基本となるフィールドにアクセスできない場合。指定されたオブジェクトが基本となるフィールド (またはそのサブクラスか実装側) を宣言するクラスまたはインタフェースのインスタンスではない場合
	 * @throws NullPointerException 指定されたオブジェクトが null で、フィールドがインスタンスフィールドの場合
	 * @throws SecurityException 
	 */
	private Object getValue(final Object bean) {
		try {
			final Field field = bean.getClass().getDeclaredField(name);
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			try {
				return field.get(bean);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException("Cannot get " + field.getName() + ": " + e.getMessage(), e);
			}
		} catch (NoSuchFieldException e) {
			throw new IllegalArgumentException("Field " + name + " not found in " + bean.getClass().getName() + ": " + e.getMessage(), e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private int compareValue(final Object v1, final Object v2) {
		if (v1 == null && v2 == null) {
			return 0;
		}
		if (v1 == null) {
			return -1;
		}
		if (v2 == null) {
			return 1;
		}
		return ((Comparable) v1).compareTo(v2);
	}

	@Override
	public String toString() {
		return name + ' ' + (ascending ? "asc" : "desc");
	}

}