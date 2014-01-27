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

package com.orangesignal.csv;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.orangesignal.jlha.LhaFile;
import com.orangesignal.jlha.LhaHeader;

/**
 * Benchmark for OrangeSignal CSV / opencsv / Super CSV
 * 
 * @author Koji Sugisawa
 * @since 2.1
 */
public class CsvReaderBenchmarks extends AbstractBenchmark {

	@Test
	public void testOrangeSignalCSV() throws IOException {
		final CsvConfig cfg = new CsvConfig(CsvConfig.DEFAULT_SEPARATOR);
		cfg.setQuoteDisabled(false);
		cfg.setIgnoreEmptyLines(true);
		cfg.setLineSeparator(Constants.CRLF);

		final LhaFile lhaFile = new LhaFile(new File("src/test/resources/", "ken_all.lzh"));
		try {
			final LhaHeader[] entries = lhaFile.getEntries();
			for (final LhaHeader entry : entries) {
				final CsvReader reader = new CsvReader(new InputStreamReader(lhaFile.getInputStream(entry), "Windows-31J"), cfg);
				try {
					List<String> values;
					while ((values = reader.readValues()) != null) {
						continue;
					}
				} finally {
					reader.close();
				}
			}
		} finally {
			lhaFile.close();
		}
	}

	@Test
	public void testOpenCSV() throws IOException {
		final LhaFile lhaFile = new LhaFile(new File("src/test/resources/", "ken_all.lzh"));
		try {
			final LhaHeader[] entries = lhaFile.getEntries();
			for (final LhaHeader entry : entries) {
				final au.com.bytecode.opencsv.CSVReader reader =
						new au.com.bytecode.opencsv.CSVReader(new InputStreamReader(lhaFile.getInputStream(entry), "Windows-31J"), ',', '"');
				try {
					String[] values;
					while ((values = reader.readNext()) != null) {
						continue;
					}
				} finally {
					reader.close();
				}
			}
		} finally {
			lhaFile.close();
		}
	}

	@Test
	public void testSuperCSV() throws IOException {
		final LhaFile lhaFile = new LhaFile(new File("src/test/resources/", "ken_all.lzh"));
		try {
			final LhaHeader[] entries = lhaFile.getEntries();
			for (final LhaHeader entry : entries) {
				final CsvListReader reader = new CsvListReader(new InputStreamReader(lhaFile.getInputStream(entry), "Windows-31J"), CsvPreference.STANDARD_PREFERENCE);
				try {
					List<String> values;
					while ((values = reader.read()) != null) {
						continue;
					}
				} finally {
					reader.close();
				}
			}
		} finally {
			lhaFile.close();
		}
	}

}