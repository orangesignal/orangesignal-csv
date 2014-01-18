/*
 * Copyright (c) 2009-2013 OrangeSignal.com All rights reserved.
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

package com.orangesignal.csv.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 杉澤 浩二
 */
public final class SamplePrice implements Serializable {

	private static final long serialVersionUID = 3583832835243335107L;

	public SamplePrice() {}

	private Date date;
	private String name;
	private Number price;

	public Date getDate() { return date; }
	public void setDate(Date date) { this.date = date; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public Number getPrice() { return price; }
	public void setPrice(Number price) { this.price = price; }

}