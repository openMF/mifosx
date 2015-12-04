/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.scheduler;

/** 
 * Scheduled Job service interface for SMS message 
 **/
public interface SmsMessageScheduledJobService {
	
	/** 
	 * sends a batch of SMS messages to the SMS gateway 
	 **/
	public void sendMessages();
	
	/** 
	 * get delivery report from the SMS gateway 
	 **/
	public void getDeliveryReports();
}