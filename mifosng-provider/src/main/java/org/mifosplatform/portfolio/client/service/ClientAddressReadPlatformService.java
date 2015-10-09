/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.service;

import java.util.Collection;
import org.mifosplatform.portfolio.client.data.ClientAddressData;

public interface ClientAddressReadPlatformService {
	
	Collection<ClientAddressData> retrieveClientAddresses(Long clientId);
	
	ClientAddressData retrieveClientAddress(Long clientId, Long clientAddressId);

}
