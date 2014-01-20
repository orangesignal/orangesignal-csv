/*
 * Copyright 2009 the original author or authors.
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

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * {@link CsvManagerFactory} クラスの単体テストです。
 *
 * @author Koji Sugisawa
 */
public class CsvManagerFactoryTest {

	@Test
	public void testNewCsvManager() {
		final CsvManager csvManager = CsvManagerFactory.newCsvManager();
		assertNotNull(csvManager);
		assertThat(csvManager, instanceOf(CsvBeanManager.class));
	}

}