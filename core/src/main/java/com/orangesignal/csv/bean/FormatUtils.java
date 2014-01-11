/*
 * Copyright (c) 2013 OrangeSignal.com All rights reserved.
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

package com.orangesignal.csv.bean;

import java.lang.reflect.InvocationTargetException;
import java.text.Format;

/**
 * {@link Format} に関するユーティリティを提供します。
 * 
 * @author 杉澤 浩二
 * @since 1.4.0
 */
abstract class FormatUtils {

	/**
	 * インスタンス化できない事を強制します。
	 */
	protected FormatUtils() {}

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