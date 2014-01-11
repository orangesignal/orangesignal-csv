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

package com.orangesignal.csv.manager;

import java.util.ServiceLoader;

/**
 * アプリケーションで区切り文字形式データの統合アクセスインタフェースを取得できるファクトリ API を定義します。
 * 
 * @author 杉澤 浩二
 */
public abstract class CsvManagerFactory {

	/**
	 * デフォルトコンストラクタです。
	 */
	protected CsvManagerFactory() {}

	/**
	 * {@link CsvManager} の新しいインスタンスを取得します。
	 * この static メソッドは新しい統合アクセスインスタンスを作成します。
	 * このメソッドは次の順序の検索手順で、ロードする {@link CsvManager} 実装クラスを決定します。 
	 * <ul>
	 * <li>
	 * 可能であれば、JAR 仕様で詳細に説明されているサービス API を使用して、クラス名を判定する。
	 * Services API は、実行時に使用できる jar 内の META-INF/services/com.orangesignal.csv.manager.CsvManager ファイルからクラス名を検索する
	 * </li>
	 * <li>
	 * プラットフォームのデフォルトの {@link CsvManager} インスタンス
	 * </li>
	 * </ul>
	 * 
	 * @return {@link CsvManager} の新しいインスタンス
	 */
	public static CsvManager newCsvManager() {
		for (final CsvManager manager : ServiceLoader.load(CsvManager.class)) {
			return manager;
		}
		return new CsvBeanManager();
	}

}