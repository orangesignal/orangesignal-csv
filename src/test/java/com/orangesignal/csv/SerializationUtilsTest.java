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

package com.orangesignal.csv;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.orangesignal.csv.entryfilters.DirectoryEntryFilter;
import com.orangesignal.csv.entryfilters.EntryNameFilter;
import com.orangesignal.csv.entryfilters.PrefixEntryNameFilter;
import com.orangesignal.csv.entryfilters.RegexEntryNameFilter;
import com.orangesignal.csv.entryfilters.SuffixEntryNameFilter;

/**
 * {@link SerializationUtils} クラスの単体テストです。
 * 
 * @author Koji Sugisawa
 */
public class SerializationUtilsTest {

	@Test
	public void test() {
		assertThat(SerializationUtils.deserialize(SerializationUtils.serialize(new CsvConfig())), notNullValue());
		assertThat(SerializationUtils.deserialize(SerializationUtils.serialize(new SimpleCsvToken("a\nb\nc", 1, 3, true))), notNullValue());
		assertThat(SerializationUtils.deserialize(SerializationUtils.serialize(new DirectoryEntryFilter())), notNullValue());
		assertThat(SerializationUtils.deserialize(SerializationUtils.serialize(new EntryNameFilter("test"))), notNullValue());
		assertThat(SerializationUtils.deserialize(SerializationUtils.serialize(new PrefixEntryNameFilter("test"))), notNullValue());
		assertThat(SerializationUtils.deserialize(SerializationUtils.serialize(new RegexEntryNameFilter("^.*$"))), notNullValue());
		assertThat(SerializationUtils.deserialize(SerializationUtils.serialize(new SuffixEntryNameFilter("test"))), notNullValue());
	}

}