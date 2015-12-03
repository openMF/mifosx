/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.service;

import java.util.Collection;

import org.mifosplatform.portfolio.loanaccount.domain.LoanCreditCheck;

public interface LoanCreditCheckWritePlatformService {
    Collection<LoanCreditCheck> triggerLoanCreditChecks(Long loanId);
    void runLoanCreditChecks(Long loanId);
}
