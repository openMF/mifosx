/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.service;

import java.util.Collection;

import org.mifosplatform.portfolio.loanaccount.data.LoanAccountData;
import org.mifosplatform.portfolio.loanaccount.data.LoanTransactionData;
import org.mifosplatform.portfolio.loanaccount.data.RepaymentScheduleRelatedLoanData;
import org.mifosplatform.portfolio.loanaccount.loanschedule.data.LoanScheduleData;

public interface LoanReadPlatformService {

    LoanAccountData retrieveLoanAccountDetails(Long loanId);

    LoanScheduleData retrieveRepaymentSchedule(Long loanId, RepaymentScheduleRelatedLoanData repaymentScheduleRelatedData);

    Collection<LoanTransactionData> retrieveLoanTransactions(Long loanId);

    LoanAccountData retrieveTemplateWithClientAndProductDetails(Long clientId, Long productId);

    LoanAccountData retrieveTemplateWithGroupAndProductDetails(Long groupId, Long productId);

    LoanTransactionData retrieveLoanTransactionTemplate(Long loanId);

    LoanTransactionData retrieveWaiveInterestDetails(Long loanId);

    LoanTransactionData retrieveLoanTransaction(Long loanId, Long transactionId);

    LoanTransactionData retrieveNewClosureDetails();
    
    LoanTransactionData retrieveDisbursalTemplate(Long loanId);

    LoanAccountData retrieveTemplateWithCompleteGroupAndProductDetails(Long groupId, Long productId);
}