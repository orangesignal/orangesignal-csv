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

package com.orangesignal.csv.manager;

import java.util.ServiceLoader;

/**
 * アプリケーションで区切り文字形式データの統合アクセスインタフェースを取得できるファクトリ API を定義します。
 * 
 * @author Koji Sugisawa
 */
public abstract class CsvManagerFactory {

	/**
	 * デフォルトコンストラクタです。
	 */
	protected CsvManagerFactory() {
	}

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