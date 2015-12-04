/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.data;

import java.util.List;

/** 
 * Immutable data object representing an outbound SMS message API response data 
 **/
public class SmsMessageApiResponseData {
	private Integer httpStatusCode;
	private List<SmsMessageDeliveryReportData> data;
	
	/** 
	 * SmsMessageApiResponseData constructor
	 * 
	 * @return void 
	 **/
	private SmsMessageApiResponseData(Integer httpStatusCode, List<SmsMessageDeliveryReportData> data) {
		this.httpStatusCode = httpStatusCode;
		this.data = data;
	}
	
	/** 
	 * Default SmsMessageApiResponseData constructor 
	 * 
	 * @return void
	 **/
	protected SmsMessageApiResponseData() {}
	
	/** 
	 * @return an instance of the SmsMessageApiResponseData class
	 **/
	public static SmsMessageApiResponseData getInstance(Integer httpStatusCode, List<SmsMessageDeliveryReportData> data) {
		return new SmsMessageApiResponseData(httpStatusCode, data);
	}

	/**
	 * @return the httpStatusCode
	 */
	public Integer getHttpStatusCode() {
		return httpStatusCode;
	}

	/**
	 * @return the data
	 */
	public List<SmsMessageDeliveryReportData> getData() {
		return data;
	}
}
