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

import java.util.zip.ZipEntry;

import jp.gr.java_conf.dangan.util.lha.LhaHeader;

import org.junit.Test;

import com.orangesignal.csv.entryfilters.DirectoryEntryFilter;

/**
 * {@link DirectoryEntryFilter} の単体テストです。
 * 
 * @author 杉澤 浩二
 */
public class DirectoryEntryFilterTest {

	@Test
	public void testAcceptZipEntry() {
		// Arrange
		final DirectoryEntryFilter filter = new DirectoryEntryFilter();
		// Act + Assert
		assertTrue(filter.accept(new ZipEntry("foo/bar/test.csv")));
		assertFalse(filter.accept(new ZipEntry("foo/bar/test/")));
	}

	@Test
	public void testAcceptLhaHeader() {
		// Arrange
		final DirectoryEntryFilter filter = new DirectoryEntryFilter();
		// Act + Assert
		assertTrue(filter.accept(new LhaHeader("foo/bar/test.csv")));
		assertFalse(filter.accept(new LhaHeader("foo/bar/test/")));
	}

	@Test
	public void testToString() {
		// Act + Assert
		assertThat(new DirectoryEntryFilter().toString(), is("DirectoryEntryFilter"));
	}

}
