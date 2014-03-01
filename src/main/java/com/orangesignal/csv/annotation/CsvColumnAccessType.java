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

/**
 * 区切り文字形式のデータ項目へのアクセスモードを定義します。
 * 
 * @author Koji Sugisawa
 * @since 2.2
 */
public enum CsvColumnAccessType {

	/**
	 * 入力のみ (出力しない) をサポートするアクセスモードです。
	 */
	READ {

		/**
		 * {@inheritDoc}
		 * この実装は常に {@code true} を返します。
		 */
		@Override public boolean isReadable() { return true; }

		/**
		 * {@inheritDoc}
		 * この実装は常に {@code false} を返します。
		 */
		@Override public boolean isWriteable() { return false; }

	},

	/**
	 * 出力のみ (入力しない) をサポートするアクセスモードです。
	 */
	WRITE {

		/**
		 * {@inheritDoc}
		 * この実装は常に {@code false} を返します。
		 */
		@Override public boolean isReadable() { return false; }

		/**
		 * {@inheritDoc}
		 * この実装は常に {@code true} を返します。
		 */
		@Override public boolean isWriteable() { return true; }

	},

	/**
	 * 入出力両方をサポートするアクセスモードです。
	 */
	READ_WRITE {

		/**
		 * {@inheritDoc}
		 * この実装は常に {@code true} を返します。
		 */
		@Override public boolean isReadable() { return true; }

		/**
		 * {@inheritDoc}
		 * この実装は常に {@code true} を返します。
		 */
		@Override public boolean isWriteable() { return true; }

	};

	/**
	 * この列挙型が入力アクセスをサポートするかどうかを返します。
	 * 
	 * @return 入力アクセスをサポートするかどうか
	 */
	public abstract boolean isReadable();

	/**
	 * この列挙型が出力アクセスをサポートするかどうかを返します。
	 * 
	 * @return 出力アクセスをサポートするかどうか
	 */
	public abstract boolean isWriteable();

}