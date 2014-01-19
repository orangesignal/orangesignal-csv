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

import com.orangesignal.csv.entryfilters.PrefixEntryNameFilter;
import com.orangesignal.jlha.LhaHeader;

/**
 * {@link PrefixEntryNameFilter} の単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class PrefixEntryNameFilterTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testPrefixEntryNameFilterStringArrayBooleanIllegalArgumentException() {
		// Arrange
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Prefixes must not be null");
		final String[] prefixes = null;
		// Act
		new PrefixEntryNameFilter(prefixes, false);
	}

	@Test
	public void testPrefixEntryNameFilterCollectionOfStringBooleanIllegalArgumentException() {
		// Arrange
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Prefixes must not be null");
		final Collection<String> prefixes = null;
		// Act
		new PrefixEntryNameFilter(prefixes, false);
	}

	@Test
	public void testAcceptZipEntry() {
		final String s = "foo/bar/test";

		final PrefixEntryNameFilter filter1 = new PrefixEntryNameFilter(s);
		// Act + Assert
		assertFalse(filter1.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter1.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter1.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter1.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter1.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/Test.txt")));
		assertTrue(filter1.accept(new ZipEntry("foo/bar/test/")));

		final PrefixEntryNameFilter filter2 = new PrefixEntryNameFilter(s, false);
		// Act + Assert
		assertFalse(filter2.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter2.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter2.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter2.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter2.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/Test.txt")));
		assertTrue(filter2.accept(new ZipEntry("foo/bar/test/")));

		final PrefixEntryNameFilter filter3 = new PrefixEntryNameFilter(s, true);
		// Act + Assert
		assertFalse(filter3.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter3.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter3.accept(new ZipEntry("foo/bar/test.csv")));
		assertTrue(filter3.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter3.accept(new ZipEntry("foo/bar/test.tsv")));
		assertTrue(filter3.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter3.accept(new ZipEntry("foo/bar/test.txt")));
		assertTrue(filter3.accept(new ZipEntry("foo/bar/Test.txt")));
		assertTrue(filter3.accept(new ZipEntry("foo/bar/test/")));

		final String[] strings = new String[]{ "foo/bar/test", "foo/test" };

		final PrefixEntryNameFilter filter4 = new PrefixEntryNameFilter(strings);
		// Act + Assert
		assertTrue(filter4.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter4.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter4.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter4.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter4.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter4.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter4.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter4.accept(new ZipEntry("foo/bar/Test.txt")));
		assertTrue(filter4.accept(new ZipEntry("foo/bar/test/")));

		final PrefixEntryNameFilter filter5 = new PrefixEntryNameFilter(strings, false);
		// Act + Assert
		assertTrue(filter5.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter5.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter5.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter5.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter5.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter5.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter5.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter5.accept(new ZipEntry("foo/bar/Test.txt")));
		assertTrue(filter5.accept(new ZipEntry("foo/bar/test/")));

		final PrefixEntryNameFilter filter6 = new PrefixEntryNameFilter(strings, true);
		// Act + Assert
		assertTrue(filter6.accept(new ZipEntry("foo/test.csv")));
		assertTrue(filter6.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/test.csv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/test.tsv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/test.txt")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/Test.txt")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/test/")));

		final List<String> list = new ArrayList<String>();
		list.add("foo/bar/test");
		list.add("foo/test");

		final PrefixEntryNameFilter filter7 = new PrefixEntryNameFilter(list);
		// Act + Assert
		assertTrue(filter7.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter7.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter7.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter7.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter7.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter7.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter7.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter7.accept(new ZipEntry("foo/bar/Test.txt")));
		assertTrue(filter7.accept(new ZipEntry("foo/bar/test/")));

		final PrefixEntryNameFilter filter8 = new PrefixEntryNameFilter(list, false);
		// Act + Assert
		assertTrue(filter8.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter8.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter8.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter8.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter8.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter8.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter8.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter8.accept(new ZipEntry("foo/bar/Test.txt")));
		assertTrue(filter8.accept(new ZipEntry("foo/bar/test/")));

		final PrefixEntryNameFilter filter9 = new PrefixEntryNameFilter(list, true);
		// Act + Assert
		assertTrue(filter9.accept(new ZipEntry("foo/test.csv")));
		assertTrue(filter9.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter9.accept(new ZipEntry("foo/bar/test.csv")));
		assertTrue(filter9.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter9.accept(new ZipEntry("foo/bar/test.tsv")));
		assertTrue(filter9.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertTrue(filter9.accept(new ZipEntry("foo/bar/test.txt")));
		assertTrue(filter9.accept(new ZipEntry("foo/bar/Test.txt")));
		assertTrue(filter9.accept(new ZipEntry("foo/bar/test/")));
	}

	@Test
	public void testAcceptLhaHeader() {
		final String s = "foo/bar/test";

		final PrefixEntryNameFilter filter1 = new PrefixEntryNameFilter(s);
		// Act + Assert
		assertFalse(filter1.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter1.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter1.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter1.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter1.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/Test.txt")));
		assertTrue(filter1.accept(new LhaHeader("foo/bar/test/")));

		final PrefixEntryNameFilter filter2 = new PrefixEntryNameFilter(s, false);
		// Act + Assert
		assertFalse(filter2.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter2.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter2.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter2.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter2.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/Test.txt")));
		assertTrue(filter2.accept(new LhaHeader("foo/bar/test/")));

		final PrefixEntryNameFilter filter3 = new PrefixEntryNameFilter(s, true);
		// Act + Assert
		assertFalse(filter3.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter3.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter3.accept(new LhaHeader("foo/bar/test.csv")));
		assertTrue(filter3.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter3.accept(new LhaHeader("foo/bar/test.tsv")));
		assertTrue(filter3.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter3.accept(new LhaHeader("foo/bar/test.txt")));
		assertTrue(filter3.accept(new LhaHeader("foo/bar/Test.txt")));
		assertTrue(filter3.accept(new LhaHeader("foo/bar/test/")));

		final String[] strings = new String[]{ "foo/bar/test", "foo/test" };

		final PrefixEntryNameFilter filter4 = new PrefixEntryNameFilter(strings);
		// Act + Assert
		assertTrue(filter4.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter4.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter4.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter4.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter4.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter4.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter4.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter4.accept(new LhaHeader("foo/bar/Test.txt")));
		assertTrue(filter4.accept(new LhaHeader("foo/bar/test/")));

		final PrefixEntryNameFilter filter5 = new PrefixEntryNameFilter(strings, false);
		// Act + Assert
		assertTrue(filter5.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter5.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter5.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter5.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter5.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter5.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter5.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter5.accept(new LhaHeader("foo/bar/Test.txt")));
		assertTrue(filter5.accept(new LhaHeader("foo/bar/test/")));

		final PrefixEntryNameFilter filter6 = new PrefixEntryNameFilter(strings, true);
		// Act + Assert
		assertTrue(filter6.accept(new LhaHeader("foo/test.csv")));
		assertTrue(filter6.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/test.csv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/test.tsv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/test.txt")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/Test.txt")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/test/")));

		final List<String> list = new ArrayList<String>();
		list.add("foo/bar/test");
		list.add("foo/test");

		final PrefixEntryNameFilter filter7 = new PrefixEntryNameFilter(list);
		// Act + Assert
		assertTrue(filter7.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter7.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter7.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter7.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter7.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter7.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter7.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter7.accept(new LhaHeader("foo/bar/Test.txt")));
		assertTrue(filter7.accept(new LhaHeader("foo/bar/test/")));

		final PrefixEntryNameFilter filter8 = new PrefixEntryNameFilter(list, false);
		// Act + Assert
		assertTrue(filter8.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter8.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter8.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter8.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter8.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter8.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter8.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter8.accept(new LhaHeader("foo/bar/Test.txt")));
		assertTrue(filter8.accept(new LhaHeader("foo/bar/test/")));

		final PrefixEntryNameFilter filter9 = new PrefixEntryNameFilter(list, true);
		// Act + Assert
		assertTrue(filter9.accept(new LhaHeader("foo/test.csv")));
		assertTrue(filter9.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter9.accept(new LhaHeader("foo/bar/test.csv")));
		assertTrue(filter9.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter9.accept(new LhaHeader("foo/bar/test.tsv")));
		assertTrue(filter9.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertTrue(filter9.accept(new LhaHeader("foo/bar/test.txt")));
		assertTrue(filter9.accept(new LhaHeader("foo/bar/Test.txt")));
		assertTrue(filter9.accept(new LhaHeader("foo/bar/test/")));
	}

	@Test
	public void testToString() {
		// Act + Assert
		assertThat(new PrefixEntryNameFilter(new String[]{ "foo", "bar" }).toString(), is("PrefixEntryNameFilter(foo,bar)"));
	}

}
