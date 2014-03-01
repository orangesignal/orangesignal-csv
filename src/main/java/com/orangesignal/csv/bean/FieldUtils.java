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

package com.orangesignal.csv.bean;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Java プログラム要素のフィールド操作に関するユーティリティを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.4.0
 */
public abstract class FieldUtils {

	/**
	 * インスタンス化できない事を強制します。
	 */
	protected FieldUtils() {
	}

	// ------------------------------------------------------------------------
	// フィールド操作用静的メソッド群

	/**
	 * 指定された Java プログラム要素の型が表すクラスの指定された宣言フィールドをリフレクトする {@link Field} オブジェクトを返します。
	 * 
	 * @param type Java プログラム要素の型
	 * @param name フィールド名
	 * @return 指定された Java プログラム要素の {@link Field} オブジェクト
	 * @throws IOException 指定された名前のフィールドが見つからない場合
	 * @throws NullPointerException {@code name} が {@code null} の場合
	 * @throws SecurityException 
	 */
	public static final Field getField(final Class<?> type, final String name) throws IOException {
		try {
			return type.getDeclaredField(name);
		} catch (final NoSuchFieldException e) {
			throw new IOException("Field " + name + " not found in " + type.getName() + ": " + e.getMessage(), e);
		}
	}

	/**
	 * 指定された Java プログラム要素の指定されたフィールドを、指定された新しい値に設定します。
	 * 基本となるフィールドにプリミティブ型が指定されている場合、新しい値は自動的にラップ解除されます。
	 * 
	 * @param bean フィールドを変更する Java プログラム要素
	 * @param field フィールド
	 * @param value 変更中の Java プログラム要素の新しいフィールド値
	 * @throws IOException 基本となるフィールドにアクセスできない場合。または指定されたオブジェクトが基本となるフィールド (またはそのサブクラスか実装側) を宣言するクラスまたはインタフェースのインスタンスではない場合、あるいはラップ解除変換が失敗した場合
	 * @throws NullPointerException 指定されたオブジェクトが {@code null} で、フィールドがインスタンスフィールドの場合
	 * @throws SecurityException 
	 */
	public static final void setFieldValue(final Object bean, final Field field, final Object value) throws IOException {
		if (!field.isAccessible()) {
			field.setAccessible(true);
		}
		try {
			field.set(bean, value);
		} catch (final IllegalAccessException e) {
			throw new IOException("Cannot set " + field.getName() + ": " + e.getMessage(), e);
		} catch (final IllegalArgumentException e) {
			throw new IOException("Cannot set " + field.getName() + ": " + e.getMessage(), e);
		}
	}

	/**
	 * 指定された Java プログラム要素について、指定された {@link Field} によって表されるフィールドの値を返します。
	 * プリミティブ型の場合、オブジェクト内に自動的に格納されてから返されます。
	 * 
	 * @param bean Java プログラム要素
	 * @param field フィールド
	 * @return Java プログラム要素 {@code bean} 内で表現される値。プリミティブ値は適切なオブジェクト内にラップされてから返される
	 * @throws IOException 基本となるフィールドにアクセスできない場合。指定されたオブジェクトが基本となるフィールド (またはそのサブクラスか実装側) を宣言するクラスまたはインタフェースのインスタンスではない場合
	 * @throws NullPointerException 指定されたオブジェクトが {@code null} で、フィールドがインスタンスフィールドの場合
	 * @throws SecurityException 
	 */
	public static final Object getFieldValue(final Object bean, final Field field) throws IOException {
		if (!field.isAccessible()) {
			field.setAccessible(true);
		}
		try {
			return field.get(bean);
		} catch (final IllegalAccessException e) {
			throw new IOException("Cannot get " + field.getName() + ": " + e.getMessage(), e);
		} catch (final IllegalArgumentException e) {
			throw new IOException("Cannot get " + field.getName() + ": " + e.getMessage(), e);
		}
	}

}