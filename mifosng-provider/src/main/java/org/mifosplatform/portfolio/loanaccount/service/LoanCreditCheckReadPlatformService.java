/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.service;

import java.util.Collection;

import org.mifosplatform.portfolio.creditcheck.data.CreditCheckData;
import org.mifosplatform.portfolio.loanaccount.data.LoanCreditCheckData;

public interface LoanCreditCheckReadPlatformService {
    Collection<LoanCreditCheckData> retrieveLoanCreditChecks(Long loanId);
    LoanCreditCheckData retrieveLoanCreditCheckEnumOptions();
    LoanCreditCheckData retrieveLoanCreditCheck(Long loanId, Long loanCreditCheckId);
}
