package com.orangesignal.csv.validation;

import java.util.List;
import java.util.Set;

import com.orangesignal.csv.CsvReader;
import com.orangesignal.csv.CsvToken;

/**
 * 項目数チェック
 * 最小項目数チェック
 * 最大項目数チェック
 * 
 * row 数チェック
 * 最小 row 数チェック
 * 最大 row 数チェック
 * 
 * 型チェック
 * URL, email, etc...
 * 
 * @author orangesignal
 *
 */
public interface CsvTokenValidator {

//	Set<CsvViolation> validate(List<String> values);
	Set<CsvViolation> validate(List<CsvToken> tokens, CsvReader reader);

}