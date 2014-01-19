/*
 * Copyright (c) 2009 OrangeSignal.com All rights reserved.
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

import com.orangesignal.csv.entryfilters.SuffixEntryNameFilter;
import com.orangesignal.jlha.LhaHeader;

/**
 * {@link SuffixEntryNameFilter} の単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class SuffixEntryNameFilterTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testSuffixEntryNameFilterStringArrayBooleanIllegalArgumentException() {
		// Arrange
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Suffixes must not be null");
		final String[] suffixes = null;
		// Act
		new SuffixEntryNameFilter(suffixes, false);
	}

	@Test
	public void testSuffixEntryNameFilterCollectionOfStringBooleanIllegalArgumentException() {
		// Arrange
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Suffixes must not be null");
		final Collection<String> suffixes = null;
		// Act
		new SuffixEntryNameFilter(suffixes, false);
	}

	@Test
	public void testAcceptZipEntry() {
		final String s = "test.csv";

		final SuffixEntryNameFilter filter1 = new SuffixEntryNameFilter(s);
		// Act + Assert
		assertTrue(filter1.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter1.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter1.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/Test.csv")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/test/")));

		final SuffixEntryNameFilter filter2 = new SuffixEntryNameFilter(s, false);
		// Act + Assert
		assertTrue(filter2.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter2.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter2.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/Test.csv")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/test/")));

		final SuffixEntryNameFilter filter3 = new SuffixEntryNameFilter(s, true);
		// Act + Assert
		assertTrue(filter3.accept(new ZipEntry("foo/test.csv")));
		assertTrue(filter3.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter3.accept(new ZipEntry("foo/bar/test.csv")));
		assertTrue(filter3.accept(new ZipEntry("foo/bar/Test.csv")));
		assertFalse(filter3.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter3.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertFalse(filter3.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter3.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter3.accept(new ZipEntry("foo/bar/test/")));

		final String[] strings = new String[]{ "test.csv", "test.tsv", "test.txt" };

		final SuffixEntryNameFilter filter4 = new SuffixEntryNameFilter(strings);
		// Act + Assert
		assertTrue(filter4.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter4.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter4.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter4.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter4.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter4.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter4.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter4.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter4.accept(new ZipEntry("foo/bar/test/")));

		final SuffixEntryNameFilter filter5 = new SuffixEntryNameFilter(strings, false);
		// Act + Assert
		assertTrue(filter5.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter5.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter5.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter5.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter5.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter5.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter5.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter5.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter5.accept(new ZipEntry("foo/bar/test/")));

		final SuffixEntryNameFilter filter6 = new SuffixEntryNameFilter(strings, true);
		// Act + Assert
		assertTrue(filter6.accept(new ZipEntry("foo/test.csv")));
		assertTrue(filter6.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/test.csv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/test.tsv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/test.txt")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter6.accept(new ZipEntry("foo/bar/test/")));

		final List<String> list = new ArrayList<String>();
		list.add("test.csv");
		list.add("test.tsv");
		list.add("test.txt");

		final SuffixEntryNameFilter filter7 = new SuffixEntryNameFilter(list);
		// Act + Assert
		assertTrue(filter7.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter7.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter7.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter7.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter7.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter7.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter7.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter7.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter7.accept(new ZipEntry("foo/bar/test/")));

		final SuffixEntryNameFilter filter8 = new SuffixEntryNameFilter(list, false);
		// Act + Assert
		assertTrue(filter8.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter8.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter8.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter8.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter8.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter8.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter8.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter8.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter8.accept(new ZipEntry("foo/bar/test/")));

		final SuffixEntryNameFilter filter9 = new SuffixEntryNameFilter(list, true);
		// Act + Assert
		assertTrue(filter9.accept(new ZipEntry("foo/test.csv")));
		assertTrue(filter9.accept(new ZipEntry("foo/Test.csv")));
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
		final String s = "test.csv";

		final SuffixEntryNameFilter filter1 = new SuffixEntryNameFilter(s);
		// Act + Assert
		assertTrue(filter1.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter1.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter1.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/Test.csv")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/test/")));

		final SuffixEntryNameFilter filter2 = new SuffixEntryNameFilter(s, false);
		// Act + Assert
		assertTrue(filter2.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter2.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter2.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/Test.csv")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/test/")));

		final SuffixEntryNameFilter filter3 = new SuffixEntryNameFilter(s, true);
		// Act + Assert
		assertTrue(filter3.accept(new LhaHeader("foo/test.csv")));
		assertTrue(filter3.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter3.accept(new LhaHeader("foo/bar/test.csv")));
		assertTrue(filter3.accept(new LhaHeader("foo/bar/Test.csv")));
		assertFalse(filter3.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter3.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertFalse(filter3.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter3.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter3.accept(new LhaHeader("foo/bar/test/")));

		final String[] strings = new String[]{ "test.csv", "test.tsv", "test.txt" };

		final SuffixEntryNameFilter filter4 = new SuffixEntryNameFilter(strings);
		// Act + Assert
		assertTrue(filter4.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter4.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter4.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter4.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter4.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter4.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter4.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter4.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter4.accept(new LhaHeader("foo/bar/test/")));

		final SuffixEntryNameFilter filter5 = new SuffixEntryNameFilter(strings, false);
		// Act + Assert
		assertTrue(filter5.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter5.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter5.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter5.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter5.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter5.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter5.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter5.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter5.accept(new LhaHeader("foo/bar/test/")));

		final SuffixEntryNameFilter filter6 = new SuffixEntryNameFilter(strings, true);
		// Act + Assert
		assertTrue(filter6.accept(new LhaHeader("foo/test.csv")));
		assertTrue(filter6.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/test.csv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/test.tsv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/test.txt")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter6.accept(new LhaHeader("foo/bar/test/")));

		final List<String> list = new ArrayList<String>();
		list.add("test.csv");
		list.add("test.tsv");
		list.add("test.txt");

		final SuffixEntryNameFilter filter7 = new SuffixEntryNameFilter(list);
		// Act + Assert
		assertTrue(filter7.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter7.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter7.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter7.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter7.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter7.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter7.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter7.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter7.accept(new LhaHeader("foo/bar/test/")));

		final SuffixEntryNameFilter filter8 = new SuffixEntryNameFilter(list, false);
		// Act + Assert
		assertTrue(filter8.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter8.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter8.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter8.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter8.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter8.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter8.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter8.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter8.accept(new LhaHeader("foo/bar/test/")));

		final SuffixEntryNameFilter filter9 = new SuffixEntryNameFilter(list, true);
		// Act + Assert
		assertTrue(filter9.accept(new LhaHeader("foo/test.csv")));
		assertTrue(filter9.accept(new LhaHeader("foo/Test.csv")));
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
		assertThat(new SuffixEntryNameFilter(new String[]{ ".csv", ".tsv" }).toString(), is("SuffixEntryNameFilter(.csv,.tsv)"));
	}

}
