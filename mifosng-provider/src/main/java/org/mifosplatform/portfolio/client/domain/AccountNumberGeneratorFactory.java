/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.domain;

import org.mifosplatform.infrastructure.configuration.domain.ConfigurationDomainService;
import org.springframework.stereotype.Component;

@Component
public class AccountNumberGeneratorFactory {
	
	public static final int DEFAULT_PADDING_SIZE = 9;

    private final ConfigurationDomainService configurationDomainService;
    
    public AccountNumberGeneratorFactory(ConfigurationDomainService configurationDomainService) {
    	this.configurationDomainService = configurationDomainService;
    }

    public AccountNumberGenerator determineClientAccountNoGenerator() {
    	if (configurationDomainService.hasAccountNumberFormatSpecifier()) {
    		return new CustomAccountNumberGenerator(configurationDomainService.accountNumberFormatSpecifier());
    	}
		return new ZeroPaddedAccountNumberGenerator(DEFAULT_PADDING_SIZE);
    }

    public AccountNumberGenerator determineLoanAccountNoGenerator() {
        return new ZeroPaddedAccountNumberGenerator(DEFAULT_PADDING_SIZE);
    }

    public AccountNumberGenerator determineSavingsAccountNoGenerator() {
        return new ZeroPaddedAccountNumberGenerator(DEFAULT_PADDING_SIZE);
    }
}