/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.data;

import java.util.List;

import com.google.gson.Gson;

/** 
 * Immutable data object representing the API request body sent in the POST request to
 * the "/report" resource 
 **/
public class SmsMessageApiReportResourceData {
	private List<Long> externalIds;
	private String mifosTenantIdentifier;
	
	/** 
	 * SmsMessageApiReportResourceData constructor 
	 **/
	private SmsMessageApiReportResourceData(List<Long> externalIds, String mifosTenantIdentifier) {
		this.externalIds = externalIds;
		this.mifosTenantIdentifier = mifosTenantIdentifier;
	}
	
	/** 
	 * SmsMessageApiReportResourceData constructor 
	 **/
	protected SmsMessageApiReportResourceData() {}
	
	/** 
	 * @return new instance of the SmsMessageApiReportResourceData class
	 **/
	public static final SmsMessageApiReportResourceData instance(List<Long> externalIds, String mifosTenantIdentifier) {
		return new SmsMessageApiReportResourceData(externalIds, mifosTenantIdentifier);
	}

	/**
	 * @return the externalIds
	 */
	public List<Long> getExternalIds() {
		return externalIds;
	}

	/**
	 * @return the mifosTenantIdentifier
	 */
	public String getMifosTenantIdentifier() {
		return mifosTenantIdentifier;
	}
	
	/** 
	 * @return JSON representation of the object 
	 **/
	public String toJsonString() {
		Gson gson = new Gson();
		
		return gson.toJson(this);
	}
}
