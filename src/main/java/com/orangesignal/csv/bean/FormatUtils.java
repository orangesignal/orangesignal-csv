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

import java.lang.reflect.InvocationTargetException;
import java.text.Format;

/**
 * {@link Format} に関するユーティリティを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.4.0
 */
abstract class FormatUtils {

	/**
	 * インスタンス化できない事を強制します。
	 */
	protected FormatUtils() {
	}

	public static Format mergeFormatPattern(final Format format, final Format... formats) {
		final StringBuilder buf = new StringBuilder();
		buf.append(getFormatPattern(format));
		for (final Format fmt : formats) {
			buf.append(getFormatPattern(fmt));
		}

		final Format result = (Format) format.clone();
		try {
			result.getClass().getMethod("applyPattern", String.class).invoke(result, buf.toString());
		} catch (final NoSuchMethodException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (final IllegalAccessException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (final InvocationTargetException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		return result;
	}

	private static String getFormatPattern(final Format format) {
		try {
			return (String) format.getClass().getMethod("toPattern").invoke(format);
		} catch (final NoSuchMethodException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (final IllegalAccessException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (final InvocationTargetException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

}