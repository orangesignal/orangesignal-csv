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

package com.orangesignal.csv.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * フィールドが区切り文字形式の複数のデータ項目であることを示します。
 *
 * @author 杉澤 浩二
 * @since 1.2.1
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CsvColumns {

	/**
	 * {@link CsvColumn} の列挙を返します。
	 *
	 * @return {@link CsvColumn} の列挙
	 */
	CsvColumn[] value();

//	String separator() default "";		// XXX - 後で検討する

}