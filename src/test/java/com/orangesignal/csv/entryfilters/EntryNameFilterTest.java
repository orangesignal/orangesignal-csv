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

package com.orangesignal.csv.entryfilters;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.orangesignal.jlha.LhaHeader;

/**
 * {@link EntryNameFilter} の単体テストです。
 * 
 * @author Koji Sugisawa
 */
public class EntryNameFilterTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testEntryNameFilterStringArrayBooleanIllegalArgumentException() {
		// Arrange
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Names must not be null");
		final String[] names = null;
		// Act
		new EntryNameFilter(names, false);
	}

	@Test
	public void testEntryNameFilterCollectionOfStringBooleanIllegalArgumentException() {
		// Arrange
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Names must not be null");
		final Collection<String> names = null;
		// Act
		new EntryNameFilter(names, false);
	}

	@Test
	public void testAcceptZipEntry() {
		final String s = "foo/bar/test.csv";

		final EntryNameFilter filter1 = new EntryNameFilter(s);
		// Act + Assert
		assertFalse(filter1.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter1.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter1.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/Test.csv")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/test/")));

		final EntryNameFilter filter2 = new EntryNameFilter(s, false);
		// Act + Assert
		assertFalse(filter2.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter2.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter2.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/Test.csv")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/test/")));

		final EntryNameFilter filter3 = new EntryNameFilter(s, true);
		// Act + Assert
		assertFalse(filter3.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter3.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter3.accept(new ZipEntry("foo/bar/test.csv")));
		assertTrue(filter3.accept(new ZipEntry("foo/bar/Test.csv")));
		assertFalse(filter3.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter3.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertFalse(filter3.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter3.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter3.accept(new ZipEntry("foo/bar/test/")));

		final String[] strings = new String[]{ "foo/bar/test.csv", "foo/bar/test.tsv", "foo/bar/test.txt" };

		final EntryNameFilter filter4 = new EntryNameFilter(strings);
		// Act + Assert
		assertFalse(filter4.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter4.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter4.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter4.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter4.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter4.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter4.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter4.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter4.accept(new ZipEntry("foo/bar/test/")));

		final EntryNameFilter filter5 = new EntryNameFilter(strings, false);
		// Act + Assert
		assertFalse(filter5.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter5.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter5.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter5.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter5.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter5.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter5.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter5.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter5.accept(new ZipEntry("foo/bar/test/")));

		final EntryNameFilter filter6 = new EntryNameFilter(strings, true);
		// Act + Assert
		assertFalse(filter6.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter6.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/test.csv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/test.tsv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/test.txt")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter6.accept(new ZipEntry("foo/bar/test/")));

		final List<String> list = new ArrayList<String>();
		list.add("foo/bar/test.csv");
		list.add("foo/bar/test.tsv");
		list.add("foo/bar/test.txt");

		final EntryNameFilter filter7 = new EntryNameFilter(list);
		// Act + Assert
		assertFalse(filter7.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter7.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter7.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter7.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter7.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter7.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter7.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter7.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter7.accept(new ZipEntry("foo/bar/test/")));

		final EntryNameFilter filter8 = new EntryNameFilter(list, false);
		// Act + Assert
		assertFalse(filter8.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter8.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter8.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter8.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter8.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter8.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter8.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter8.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter8.accept(new ZipEntry("foo/bar/test/")));

		final EntryNameFilter filter9 = new EntryNameFilter(list, true);
		// Act + Assert
		assertFalse(filter9.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter9.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter9.accept(new ZipEntry("foo/bar/test.csv")));
		assertTrue(filter9.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter9.accept(new ZipEntry("foo/bar/test.tsv")));
		assertTrue(filter9.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter9.accept(new ZipEntry("foo/bar/test.txt")));
		assertTrue(filter9.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter9.accept(new ZipEntry("foo/bar/test/")));
	}

	@Test
	public void testAcceptLhaHeader() {
		final String s = "foo/bar/test.csv";

		final EntryNameFilter filter1 = new EntryNameFilter(s);
		// Act + Assert
		assertFalse(filter1.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter1.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter1.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/Test.csv")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/test/")));

		final EntryNameFilter filter2 = new EntryNameFilter(s, false);
		// Act + Assert
		assertFalse(filter2.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter2.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter2.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/Test.csv")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/test/")));

		final EntryNameFilter filter3 = new EntryNameFilter(s, true);
		// Act + Assert
		assertFalse(filter3.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter3.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter3.accept(new LhaHeader("foo/bar/test.csv")));
		assertTrue(filter3.accept(new LhaHeader("foo/bar/Test.csv")));
		assertFalse(filter3.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter3.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertFalse(filter3.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter3.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter3.accept(new LhaHeader("foo/bar/test/")));

		final String[] strings = new String[]{ "foo/bar/test.csv", "foo/bar/test.tsv", "foo/bar/test.txt" };

		final EntryNameFilter filter4 = new EntryNameFilter(strings);
		// Act + Assert
		assertFalse(filter4.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter4.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter4.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter4.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter4.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter4.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter4.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter4.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter4.accept(new LhaHeader("foo/bar/test/")));

		final EntryNameFilter filter5 = new EntryNameFilter(strings, false);
		// Act + Assert
		assertFalse(filter5.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter5.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter5.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter5.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter5.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter5.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter5.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter5.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter5.accept(new LhaHeader("foo/bar/test/")));

		final EntryNameFilter filter6 = new EntryNameFilter(strings, true);
		// Act + Assert
		assertFalse(filter6.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter6.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/test.csv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/test.tsv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/test.txt")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter6.accept(new LhaHeader("foo/bar/test/")));

		final List<String> list = new ArrayList<String>();
		list.add("foo/bar/test.csv");
		list.add("foo/bar/test.tsv");
		list.add("foo/bar/test.txt");

		final EntryNameFilter filter7 = new EntryNameFilter(list);
		// Act + Assert
		assertFalse(filter7.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter7.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter7.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter7.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter7.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter7.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter7.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter7.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter7.accept(new LhaHeader("foo/bar/test/")));

		final EntryNameFilter filter8 = new EntryNameFilter(list, false);
		// Act + Assert
		assertFalse(filter8.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter8.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter8.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter8.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter8.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter8.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter8.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter8.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter8.accept(new LhaHeader("foo/bar/test/")));

		final EntryNameFilter filter9 = new EntryNameFilter(list, true);
		// Act + Assert
		assertFalse(filter9.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter9.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter9.accept(new LhaHeader("foo/bar/test.csv")));
		assertTrue(filter9.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter9.accept(new LhaHeader("foo/bar/test.tsv")));
		assertTrue(filter9.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter9.accept(new LhaHeader("foo/bar/test.txt")));
		assertTrue(filter9.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter9.accept(new LhaHeader("foo/bar/test/")));
	}

	@Test
	public void testToString() {
		// Act + Assert
		assertThat(new EntryNameFilter(new String[]{ "foo.csv", "bar.csv" }).toString(), is("EntryNameFilter(foo.csv,bar.csv)"));
	}

}