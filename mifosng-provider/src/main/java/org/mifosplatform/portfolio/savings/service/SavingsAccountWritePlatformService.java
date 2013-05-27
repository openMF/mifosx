/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface SavingsAccountWritePlatformService {

    CommandProcessingResult createSavingAccount(JsonCommand command);

    CommandProcessingResult updateSavingAccount(Long accountId, JsonCommand command);

    CommandProcessingResult deleteSavingAccount(Long accountId);

    CommandProcessingResult activate(Long savingsId, JsonCommand command);

    CommandProcessingResult deposit(Long savingsId, JsonCommand command);

    CommandProcessingResult withdrawal(Long savingsId, JsonCommand command);
    
    CommandProcessingResult addAnnualFee(Long savingsId);

    CommandProcessingResult calculateInterest(Long savingsId, JsonCommand command);

    CommandProcessingResult postInterest(Long savingsId, JsonCommand command);
}