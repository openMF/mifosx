/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.data;

/**
 * Immutable data object representing an SMS configuration.
 */
public class SmsConfigurationData {
	@SuppressWarnings("unused")
	private final Long id;
	
	private final String name;
	
    private final String value;
	
	/** 
	 * @return an instance of the SmsConfigurationData class 
	 **/
	public static SmsConfigurationData instance(Long id, String name, String value) {
		return new SmsConfigurationData(id, name, value);
	}
	
	/** 
	 * SmsConfigurationData constructor
	 **/
	private SmsConfigurationData(Long id, String name, String value) {
		this.id = id;
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
}
