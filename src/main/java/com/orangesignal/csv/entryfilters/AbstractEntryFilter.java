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

package com.orangesignal.csv.entryfilters;

import java.io.Serializable;

import com.orangesignal.csv.LhaEntryFilter;
import com.orangesignal.csv.ZipEntryFilter;

/**
 * エントリフィルタの基底クラスを提供します。
 * 
 * @author Koji Sugisawa
 * @since 1.2.2
 */
public abstract class AbstractEntryFilter implements ZipEntryFilter, LhaEntryFilter, Serializable {

	private static final long serialVersionUID = -1492900359515513779L;

	/**
	 * デフォルトコンストラクタです。
	 */
	protected AbstractEntryFilter() {
	}

	@Override
	public String toString() {
		final String name = getClass().getName();
		final int period = name.lastIndexOf('.');
		return period > 0 ? name.substring(period + 1) : name;
	}

}