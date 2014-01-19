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

import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.orangesignal.csv.entryfilters.RegexEntryNameFilter;
import com.orangesignal.jlha.LhaHeader;

/**
 * {@link RegexEntryNameFilter} の単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class RegexEntryNameFilterTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testRegexEntryNameFilterStringIntIllegalArgumentException() {
		// Arrange
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Pattern must not be null");
		final String pattern = null;
		// Act
		new RegexEntryNameFilter(pattern, Pattern.CASE_INSENSITIVE);
	}

	@Test
	public void testRegexEntryNameFilterPatternIllegalArgumentException() {
		// Arrange
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Pattern must not be null");
		final Pattern  pattern = null;
		// Act
		new RegexEntryNameFilter(pattern);
	}

	@Test
	public void testAcceptZipEntry() {
		final String regex = "^foo/.+/test\\.(csv|tsv){1}$";

		final RegexEntryNameFilter filter1 = new RegexEntryNameFilter(regex);
		// Act + Assert
		assertFalse(filter1.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter1.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter1.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter1.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter1.accept(new ZipEntry("foo/bar/test/")));

		final RegexEntryNameFilter filter2 = new RegexEntryNameFilter(regex, false);
		// Act + Assert
		assertFalse(filter2.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter2.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter2.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter2.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter2.accept(new ZipEntry("foo/bar/test/")));

		final RegexEntryNameFilter filter3 = new RegexEntryNameFilter(regex, true);
		// Act + Assert
		assertFalse(filter3.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter3.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter3.accept(new ZipEntry("foo/bar/test.csv")));
		assertTrue(filter3.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter3.accept(new ZipEntry("foo/bar/test.tsv")));
		assertTrue(filter3.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertFalse(filter3.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter3.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter3.accept(new ZipEntry("foo/bar/test/")));

		final RegexEntryNameFilter filter4 = new RegexEntryNameFilter(regex, 0);
		// Act + Assert
		assertFalse(filter4.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter4.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter4.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter4.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter4.accept(new ZipEntry("foo/bar/test.tsv")));
		assertFalse(filter4.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertFalse(filter4.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter4.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter4.accept(new ZipEntry("foo/bar/test/")));

		final RegexEntryNameFilter filter5 = new RegexEntryNameFilter(regex, Pattern.CASE_INSENSITIVE);
		// Act + Assert
		assertFalse(filter5.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter5.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter5.accept(new ZipEntry("foo/bar/test.csv")));
		assertTrue(filter5.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter5.accept(new ZipEntry("foo/bar/test.tsv")));
		assertTrue(filter5.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertFalse(filter5.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter5.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter5.accept(new ZipEntry("foo/bar/test/")));

		final RegexEntryNameFilter filter6 = new RegexEntryNameFilter(Pattern.compile("^foo/.+/test\\.(csv|tsv){1}$", Pattern.CASE_INSENSITIVE));
		// Act + Assert
		assertFalse(filter6.accept(new ZipEntry("foo/test.csv")));
		assertFalse(filter6.accept(new ZipEntry("foo/Test.csv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/test.csv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/Test.csv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/test.tsv")));
		assertTrue(filter6.accept(new ZipEntry("foo/bar/Test.tsv")));
		assertFalse(filter6.accept(new ZipEntry("foo/bar/test.txt")));
		assertFalse(filter6.accept(new ZipEntry("foo/bar/Test.txt")));
		assertFalse(filter6.accept(new ZipEntry("foo/bar/test/")));
	}

	@Test
	public void testAcceptLhaHeader() {
		final String regex = "^foo/.+/test\\.(csv|tsv){1}$";

		final RegexEntryNameFilter filter1 = new RegexEntryNameFilter(regex);
		// Act + Assert
		assertFalse(filter1.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter1.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter1.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter1.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter1.accept(new LhaHeader("foo/bar/test/")));

		final RegexEntryNameFilter filter2 = new RegexEntryNameFilter(regex, false);
		// Act + Assert
		assertFalse(filter2.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter2.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter2.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter2.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter2.accept(new LhaHeader("foo/bar/test/")));

		final RegexEntryNameFilter filter3 = new RegexEntryNameFilter(regex, true);
		// Act + Assert
		assertFalse(filter3.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter3.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter3.accept(new LhaHeader("foo/bar/test.csv")));
		assertTrue(filter3.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter3.accept(new LhaHeader("foo/bar/test.tsv")));
		assertTrue(filter3.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertFalse(filter3.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter3.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter3.accept(new LhaHeader("foo/bar/test/")));

		final RegexEntryNameFilter filter4 = new RegexEntryNameFilter(regex, 0);
		// Act + Assert
		assertFalse(filter4.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter4.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter4.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter4.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter4.accept(new LhaHeader("foo/bar/test.tsv")));
		assertFalse(filter4.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertFalse(filter4.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter4.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter4.accept(new LhaHeader("foo/bar/test/")));

		final RegexEntryNameFilter filter5 = new RegexEntryNameFilter(regex, Pattern.CASE_INSENSITIVE);
		// Act + Assert
		assertFalse(filter5.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter5.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter5.accept(new LhaHeader("foo/bar/test.csv")));
		assertTrue(filter5.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter5.accept(new LhaHeader("foo/bar/test.tsv")));
		assertTrue(filter5.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertFalse(filter5.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter5.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter5.accept(new LhaHeader("foo/bar/test/")));

		final RegexEntryNameFilter filter6 = new RegexEntryNameFilter(Pattern.compile("^foo/.+/test\\.(csv|tsv){1}$", Pattern.CASE_INSENSITIVE));
		// Act + Assert
		assertFalse(filter6.accept(new LhaHeader("foo/test.csv")));
		assertFalse(filter6.accept(new LhaHeader("foo/Test.csv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/test.csv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/Test.csv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/test.tsv")));
		assertTrue(filter6.accept(new LhaHeader("foo/bar/Test.tsv")));
		assertFalse(filter6.accept(new LhaHeader("foo/bar/test.txt")));
		assertFalse(filter6.accept(new LhaHeader("foo/bar/Test.txt")));
		assertFalse(filter6.accept(new LhaHeader("foo/bar/test/")));
	}

	@Test
	public void testToString() {
		// Act + Assert
		assertThat(new RegexEntryNameFilter(Pattern.compile("^.*$")).toString(), is("RegexEntryNameFilter"));
	}

}
