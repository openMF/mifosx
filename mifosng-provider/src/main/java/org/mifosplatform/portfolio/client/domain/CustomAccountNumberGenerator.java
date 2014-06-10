/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.domain;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Responsible for generating unique account number based on some rules or
 * patterns.
 * 
 * @see ZeroPaddedAccountNumberGenerator
 */
public abstract class CustomAccountNumberGenerator implements AccountNumberGenerator {
	
	private static final String digits = "0123456789";
	
	private static final String lowercase = "abcdefghijklmnopqrstuvwxyz";
	
	private static final String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	private static final String alphanum_lowercase = digits + lowercase;
	
	private static final String alphanum_uppercase = digits + uppercase;
	
	private static final String FIELDTYPE_STRING_LOWERCASE = "s";
	private static final String FIELDTYPE_STRING_UPPERCASE = "S";
	private static final String FIELDTYPE_DIGITS = "d";
	private static final String FIELDTYPE_ALPHANUM_LOWERCASE = "a";
	private static final String FIELDTYPE_ALPHANUM_UPPERCASE = "A";
	
	protected final String formatString;
	
	private static final Pattern formatPattern = Pattern.compile("%([0-9]*)(s|S|d|a|A)");
	
	private final Random random = new Random();
	
	private String generateRandomString(Integer fieldSize, String fieldType) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < fieldSize; ++i) {
			if (fieldType.equals(FIELDTYPE_STRING_LOWERCASE)) { 
				builder.append(lowercase.charAt(random.nextInt(lowercase.length())));
			} else if (fieldType.equals(FIELDTYPE_STRING_UPPERCASE)) {
				builder.append(uppercase.charAt(random.nextInt(uppercase.length())));
			} else if (fieldType.equals(FIELDTYPE_DIGITS)) {
				builder.append(digits.charAt(random.nextInt(digits.length())));
			} else if (fieldType.equals(FIELDTYPE_ALPHANUM_LOWERCASE)) {
				builder.append(alphanum_lowercase.charAt(random.nextInt(alphanum_lowercase.length())));
			} else if (fieldType.equals(FIELDTYPE_ALPHANUM_UPPERCASE)) {
				builder.append(alphanum_uppercase.charAt(random.nextInt(alphanum_uppercase.length())));
			}
		}
		return builder.toString();
	}

	/**
	 * Field language:
	 * 	%NNs - lower case letters
	 *  %NNS - Uppercase letters
	 *  %NNd - Decimal digits
	 *  %NNa - Alphanumeric lowercase
	 *  %NNA - Alphanumberic uppercase
	 *  The NN specifies the field width.
	 *  Everything else is left as it is, including unknown % specifiers.
	 * 
	 */
    public String generate() {
    	String generatedString = formatString;
    	Matcher formatMatcher = formatPattern.matcher(generatedString);
    	while (formatMatcher.find()) {
    		Integer fieldSize;
    		try {
    			fieldSize = Integer.parseInt(formatMatcher.group(1));
    		} catch (NumberFormatException e) {
    			fieldSize = 1;
    		}
    		String fieldType = formatMatcher.group(2);
    		generatedString = formatMatcher.replaceFirst(generateRandomString(fieldSize, fieldType));
    		formatMatcher.reset(generatedString);
    	}
		return generatedString;
    }
    
    public CustomAccountNumberGenerator(String formatString) {
    	this.formatString = formatString;
    }
}
