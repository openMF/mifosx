/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.journalentry.service;

import java.math.BigDecimal;
import java.util.Date;

import org.mifosplatform.accounting.closure.domain.GLClosure;
import org.mifosplatform.accounting.common.AccountingConstants.CASH_ACCOUNTS_FOR_SAVINGS;
import org.mifosplatform.accounting.journalentry.data.SavingsDTO;
import org.mifosplatform.accounting.journalentry.data.SavingsTransactionDTO;
import org.mifosplatform.organisation.office.domain.Office;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CashBasedAccountingProcessorForSavings implements AccountingProcessorForSavings {

    private final AccountingProcessorHelper helper;

    @Autowired
    public CashBasedAccountingProcessorForSavings(final AccountingProcessorHelper accountingProcessorHelper) {
        this.helper = accountingProcessorHelper;
    }

    @Override
    public void createJournalEntriesForSavings(SavingsDTO savingsDTO) {
        final GLClosure latestGLClosure = this.helper.getLatestClosureByBranch(savingsDTO.getOfficeId());
        final Office office = this.helper.getOfficeById(savingsDTO.getOfficeId());
        final Long savingsProductId = savingsDTO.getSavingsProductId();
        final Long savingsId = savingsDTO.getSavingsId();
        for (final SavingsTransactionDTO savingsTransactionDTO : savingsDTO.getNewSavingsTransactions()) {
            final Date transactionDate = savingsTransactionDTO.getTransactionDate();
            final String transactionId = savingsTransactionDTO.getTransactionId();
            final Long paymentTypeId = savingsTransactionDTO.getPaymentTypeId();
            final boolean isReversal = savingsTransactionDTO.isReversed();
            final BigDecimal amount = savingsTransactionDTO.getAmount();

            helper.checkForBranchClosures(latestGLClosure, transactionDate);

            /** Handle Deposits and reversals of deposits **/
            if (savingsTransactionDTO.getTransactionType().isDeposit()) {
                helper.createCashBasedJournalEntriesAndReversalsForSavings(office, CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_REFERENCE,
                        CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL, savingsProductId, paymentTypeId, savingsId, transactionId,
                        transactionDate, amount, isReversal);
            }

            /** Handle withdrawals and reversals of withdrawals **/
            else if (savingsTransactionDTO.getTransactionType().isWithdrawal()) {
                helper.createCashBasedJournalEntriesAndReversalsForSavings(office, CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL,
                        CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_REFERENCE, savingsProductId, paymentTypeId, savingsId, transactionId,
                        transactionDate, amount, isReversal);
            }

            /**
             * Handle Interest Applications and reversals of Interest
             * Applications
             **/
            else if (savingsTransactionDTO.getTransactionType().isInterestPosting()) {
                helper.createCashBasedJournalEntriesAndReversalsForSavings(office, CASH_ACCOUNTS_FOR_SAVINGS.INTEREST_ON_SAVINGS,
                        CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL, savingsProductId, paymentTypeId, savingsId, transactionId,
                        transactionDate, amount, isReversal);
            }

            /** Handle Fees Deductions and reversals of Fees Deductions **/
            else if (savingsTransactionDTO.getTransactionType().isFeeDeduction()) {
                helper.createCashBasedJournalEntriesAndReversalsForSavings(office, CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL,
                        CASH_ACCOUNTS_FOR_SAVINGS.INCOME_FROM_FEES, savingsProductId, paymentTypeId, savingsId, transactionId,
                        transactionDate, amount, isReversal);
            }

        }
    }
}
