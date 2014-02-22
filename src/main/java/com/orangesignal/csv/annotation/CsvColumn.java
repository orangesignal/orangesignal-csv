/*
 * Copyright 2014 the original author or authors.
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

package com.orangesignal.csv.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>フィールドが区切り文字形式のデータ項目であることを示します。</p>
 * <pre>
 * 項目との対応付けの設定例:
 * 
 * &#064;CsvColumn(position = 0)
 * &#064;CsvColumn(name = "価格")
 * &#064;CsvColumn(position = 0, name = "価格")
 * &#064;CsvColumn(name = "価格", access = CsvColumnAccessType.READ)
 * &#064;CsvColumn(name = "銘柄", required = true, defaultValue = "日経225")
 * 
 * 
 * 書式形式文字列の設定例:
 * 
 * &#064;CsvColumn(format = "yyyy/MM/dd")
 * &#064;CsvColumn(format = "yyyy/MM/dd", language = "ja")
 * &#064;CsvColumn(format = "yyyy/MM/dd", language = "ja", country = "JP")
 * &#064;CsvColumn(format = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Tokyo")
 * &#064;CsvColumn(format = "yyyy/MM/dd HH:mm:ss", language = "ja", country = "JP", timezone = "Asia/Tokyo")
 * 
 * &#064;CsvColumn(format = "#,##0.0000")
 * &#064;CsvColumn(format = "#,##0.0000", country = "JP")
 * &#064;CsvColumn(format = "#,##0.0000", language = "ja", country = "JP")
 * &#064;CsvColumn(format = "\u00A4\u00A4 #,##0.0000", currency = "USD")
 * &#064;CsvColumn(format = "\u00A4\u00A4 #,##0.0000", language = "ja", country = "JP", currency = "USD")
 * </pre>
 * 
 * @author Koji Sugisawa
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CsvColumn {

	/**
	 * 項目位置を返します。
	 *
	 * @return 項目位置
	 */
	int position() default -1;

	/**
	 * 項目名を返します。項目名が設定されていない場合は、フィールド名が使用されます。
	 * 項目名はヘッダ行を使用する場合に使用されます。
	 *
	 * @return 項目名
	 */
	String name() default "";

	/**
	 * 書式形式文字列を返します。
	 * 
	 * @return 書式形式文字列
	 * @since 1.2.2
	 */
	String format() default "";

	/**
	 * 書式形式文字列の {@link java.util.Locale} として使用する言語コード (2 桁の小文字からなる ISO-639 コード) を返します。
	 * 
	 * @return 言語コード (2 桁の小文字からなる ISO-639 コード)
	 * @since 1.2.2
	 */
	String language() default "";

	/**
	 * 書式形式文字列の {@link java.util.Locale} として使用する国コード (2 桁の大文字からなる ISO-3166 コード) を返します。
	 * 
	 * @return 国コード (2 桁の大文字からなる ISO-3166 コード)
	 * @since 1.2.2
	 */
	String country() default "";

	/**
	 * 書式形式文字列の {@link java.util.TimeZone} として使用するタイムゾーン ID を返します。
	 * 
	 * @return タイムゾーン ID
	 * @since 1.2.2
	 */
	String timezone() default "";

	/**
	 * 書式形式文字列の {@link java.util.Currency} として使用する通貨コード (ISO 4217 コード) を返します。
	 * 
	 * @return 通貨コード (ISO 4217 コード)
	 * @since 1.2.2
	 */
	String currency() default "";

	/**
	 * この項目のアクセスモードを返します。
	 * 
	 * @return この項目へのアクセスモード
	 * @since 2.2
	 */
	CsvColumnAccessType access() default CsvColumnAccessType.READ_WRITE;

	/**
	 * 必須項目かどうかを返します。
	 * 
	 * @return 必須項目かどうか
	 * @since 2.2
	 */
	boolean required() default false;

	/**
	 * この項目のデフォルト値を返します。
	 * 
	 * @return この項目のデフォルト値
	 * @since 2.2
	 */
	String defaultValue() default "";

}