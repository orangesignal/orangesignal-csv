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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link CsvValueConverter} を実装したデフォルトのシンプルな実装クラスを提供します。
 *
 * @author Koji Sugisawa
 */
public class SimpleCsvValueConverter implements CsvValueConverter {

	/**
	 * プリミティブ型とプリミティブ型デフォルト値のマップです。
	 */
	private static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS;

	private static final Map<String, Boolean> BOOLEAN_DEFAULTS;

	static {
		final Map<Class<?>, Object> primitiveMap = new HashMap<Class<?>, Object>();
		primitiveMap.put(Boolean.TYPE, Boolean.FALSE);
		primitiveMap.put(Byte.TYPE, Byte.valueOf((byte) 0));
		primitiveMap.put(Character.TYPE, Character.valueOf('\u0000'));
		primitiveMap.put(Short.TYPE, Short.valueOf((short) 0));
		primitiveMap.put(Integer.TYPE, Integer.valueOf(0));
		primitiveMap.put(Long.TYPE, Long.valueOf(0L));
		primitiveMap.put(Float.TYPE, Float.valueOf(0F));
		primitiveMap.put(Double.TYPE, Double.valueOf(0D));
		PRIMITIVE_DEFAULTS = Collections.unmodifiableMap(primitiveMap);

		final Map<String, Boolean> booleanMap = new HashMap<String, Boolean>();
		putBooleanMap(booleanMap, "0", "1");
		putBooleanMap(booleanMap, "false", "true");
		putBooleanMap(booleanMap, "f", "t");
		putBooleanMap(booleanMap, "no", "yes");
		putBooleanMap(booleanMap, "n", "y");
		putBooleanMap(booleanMap, "off", "on");
		putBooleanMap(booleanMap, "x", "o");
		BOOLEAN_DEFAULTS = Collections.unmodifiableMap(booleanMap);
	}

	private static void putBooleanMap(final Map<String, Boolean> map, final String falseValue, final String trueValue) {
		map.put(falseValue, Boolean.FALSE);
		map.put(trueValue, Boolean.TRUE);
	}

	/**
	 * 日時書式を保持します。
	 */
	private DateFormat dateFormat;

	/**
	 * デフォルトコンストラクタです。
	 */
	public SimpleCsvValueConverter() {
	}

	/**
	 * 日時書式を返します。
	 * 
	 * @return 日時書式
	 */
	public DateFormat getDateFormat() { return dateFormat; }

	/**
	 * 日時書式を設定します。
	 * 
	 * @param dateFormat 日時書式
	 */
	public void setDateFormat(final DateFormat dateFormat) { this.dateFormat = dateFormat; }

	@Override
	public Object convert(final String str, final Class<?> type) {
		// 入力パラメータを検証します。
		if (type == null) {
			throw new IllegalArgumentException("Class must not be null");
		}

		if (type.equals(String.class)) {
			return str;
		}

		if (str == null || str.length() == 0) {
			if (type.isPrimitive()) {
				return PRIMITIVE_DEFAULTS.get(type);
			}
			return null;
		}
		
		if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
			for (final Map.Entry<String, Boolean> entry : BOOLEAN_DEFAULTS.entrySet()) {
				if (entry.getKey().equalsIgnoreCase(str)) {
					return entry.getValue();
				}
			}
		} else if (type.equals(Byte.TYPE) || type.equals(Byte.class)) {
			return Byte.valueOf(str);
//		} else if (type.equals(Character.TYPE) || type.equals(Character.class)) {
//			return Character.valueOf(str);
		} else if (type.equals(Short.TYPE) || type.equals(Short.class)) {
			return Short.valueOf(str);
		} else if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
			return Integer.valueOf(str);
		} else if (type.equals(Long.TYPE) || type.equals(Long.class)) {
			return Long.valueOf(str);
		} else if (type.equals(Float.TYPE) || type.equals(Float.class)) {
			return Float.valueOf(str);
		} else if (type.equals(Double.TYPE) || type.equals(Double.class)) {
			return Double.valueOf(str);
		} else if (type.equals(BigInteger.class)) {
			return new BigInteger(str);
		} else if (type.equals(Number.class) || type.equals(BigDecimal.class)) {
			return new BigDecimal(str);
		} else if (dateFormat != null && Date.class.isAssignableFrom(type)) {
			try {
				return type.getConstructor(Long.TYPE).newInstance(dateFormat.parse(str).getTime());
			} catch (Exception e) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		} else if (Enum.class.isAssignableFrom(type)) {
			try {
				return type.getMethod("valueOf", String.class).invoke(null, str);
			} catch (final NoSuchMethodException e) {
				throw new IllegalArgumentException(String.format("Unknown convert type %s", type.getName()), e);
			} catch (final IllegalAccessException e) {
				throw new IllegalArgumentException(String.format("Unknown convert type %s", type.getName()), e);
			} catch (final InvocationTargetException e) {
				throw new IllegalArgumentException(String.format("Unknown convert type %s", type.getName()), e);
			}
		}

		throw new IllegalArgumentException(String.format("Unknown convert type %s", type.getName()));
	}

	@Override
	public String convert(final Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Date && dateFormat != null) {
			return dateFormat.format(value);
		}
		return value.toString();
	}

}
