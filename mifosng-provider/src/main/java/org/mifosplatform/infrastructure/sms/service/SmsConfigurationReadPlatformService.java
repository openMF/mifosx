/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.service;

import java.util.Collection;

import org.mifosplatform.infrastructure.sms.data.SmsConfigurationData;

public interface SmsConfigurationReadPlatformService {
	
	Collection<SmsConfigurationData> retrieveAll();
	
	SmsConfigurationData retrieveOne(String name);
}
