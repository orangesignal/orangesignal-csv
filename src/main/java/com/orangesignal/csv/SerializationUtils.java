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

package com.orangesignal.csv;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * 直列化操作補助ユーティリティを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.1
 */
abstract class SerializationUtils {

	/**
	 * デフォルトコンストラクタです。
	 */
	protected SerializationUtils() {
	}

	/**
	 * <p>直列化を使用したディープクローンを行います。</p>
	 * 
	 * <p>
	 * このメソッドは各オブジェクトが実装する clone メソッドよりも処理に時間を要します。
	 * しかしディープクローンを実装していない構造の複雑なオブジェクトに対するシンプルな代用として使用することができます。
	 * もちろん全てのオブジェクトは  {@code Serializable} である必要があります。
	 * </p>
	 * 
	 * @param object クローンする <code>Serializable</code> オブジェクト
	 * @return クローンされたオブジェクト
	 * @throws RuntimeException 直列化に失敗した場合
	 */
	public static Object clone(final Serializable object) {
		return deserialize(serialize(object));
	}

	/**
	 * オブジェクトを指定されたストリームに対して直列化します。
	 * このストリームはオブジェクトが書き出された後一旦クローズされます。
	 * これによってアプリケーション内での finally 句、例外のハンドリングの実装を行う必要がなくなります。
	 * 
	 * @param obj バイトに直列化するオブジェクト
	 * @param outputStream 書込む null ではないストリーム
	 * @throws IllegalArgumentException {@code outputStream} が {@code null} の場合
	 * @throws IllegalStateException 直列化に失敗した場合
	 */
	public static void serialize(final Serializable obj, final OutputStream outputStream) {
		if (outputStream == null) {
			throw new IllegalArgumentException("The OutputStream must not be null");
		}
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(outputStream);
			out.writeObject(obj);
		} catch (final IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			Csv.closeQuietly(out);
		}
	}

	/**
	 * オブジェクトを直列化しバイト配列に格納します。
	 * 
	 * @param obj バイトに直列化するオブジェクト
	 * @return 直列化したバイト配列
	 * @throws RuntimeException 直列化に失敗した場合
	 */
	public static byte[] serialize(final Serializable obj) {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
		serialize(obj, baos);
		return baos.toByteArray();
	}

	/**
	 * 指定されたストリームからオブジェクトを直列化復元します。
	 * このストリームはオブジェクトが復元された後一旦クローズされます。
	 * これによってアプリケーション内での finally 句、例外のハンドリングの実装を行う必要がなくなります。
	 * 
	 * @param inputStream 直列化されたオブジェクトの入力ストリーム
	 * @return 直列化復元されたオブジェクト
	 * @throws IllegalArgumentException {@code inputStream} が {@code null} の場合
	 * @throws IllegalStateException 直列化復元に失敗した場合
	 */
	public static Object deserialize(final InputStream inputStream) {
		if (inputStream == null) {
			throw new IllegalArgumentException("The InputStream must not be null");
		}
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(inputStream);
			return in.readObject();
		} catch (final ClassNotFoundException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (final IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			Csv.closeQuietly(in);
		}
	}

	/**
	 * <p>Deserializes a single {@code Object} from an array of bytes.</p>
	 * 
	 * @param objectData 直列化されたオブジェクト
	 * @return 直列化復元されたオブジェクト
	 * @throws IllegalArgumentException {@code objectData} が {@code null} の場合
	 * @throws RuntimeException 直列化復元に失敗した場合
	 */
	public static Object deserialize(final byte[] objectData) {
		if (objectData == null) {
			throw new IllegalArgumentException("The byte[] must not be null");
		}
		return deserialize(new ByteArrayInputStream(objectData));
	}

}