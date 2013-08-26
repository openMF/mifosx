/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.journalentry.service;

import java.math.BigDecimal;
import java.util.Date;

import org.mifosplatform.accounting.closure.domain.GLClosure;
import org.mifosplatform.accounting.common.AccountingConstants.CASH_ACCOUNTS_FOR_LOAN;
import org.mifosplatform.accounting.journalentry.data.LoanDTO;
import org.mifosplatform.accounting.journalentry.data.LoanTransactionDTO;
import org.mifosplatform.organisation.office.domain.Office;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CashBasedAccountingProcessorForLoan implements AccountingProcessorForLoan {

    private final AccountingProcessorHelper helper;

    @Autowired
    public CashBasedAccountingProcessorForLoan(final AccountingProcessorHelper accountingProcessorHelper) {
        this.helper = accountingProcessorHelper;
    }

    @Override
    public void createJournalEntriesForLoan(LoanDTO loanDTO) {
        final GLClosure latestGLClosure = this.helper.getLatestClosureByBranch(loanDTO.getOfficeId());
        // final Office office =
        // this.helper.getOfficeById(loanDTO.getOfficeId());
        final Long loanProductId = loanDTO.getLoanProductId();
        for (final LoanTransactionDTO loanTransactionDTO : loanDTO.getNewLoanTransactions()) {
            final Date transactionDate = loanTransactionDTO.getTransactionDate();
            final String transactionId = loanTransactionDTO.getTransactionId();
            final Office office = this.helper.getOfficeById(loanTransactionDTO.getOfficeId());
            final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();
            final Long loanId = loanDTO.getLoanId();

            helper.checkForBranchClosures(latestGLClosure, transactionDate);

            /** Handle Disbursements and reversals of disbursements **/
            if (loanTransactionDTO.getTransactionType().isDisbursement()) {
                createJournalEntriesForDisbursements(loanDTO, loanTransactionDTO, office);
            }
            /***
             * Logic for repayments, repayments at disbursement and reversal of
             * Repayments and Repayments at disbursement
             ***/
            else if (loanTransactionDTO.getTransactionType().isRepayment()
                    || loanTransactionDTO.getTransactionType().isRepaymentAtDisbursement()) {
                createJournalEntriesForRepayments(loanDTO, loanTransactionDTO, office);
            }
            /***
             * Only principal write off affects cash based accounting (interest
             * and fee write off need not be considered). Debit losses written
             * off and credit Loan Portfolio
             **/
            else if (loanTransactionDTO.getTransactionType().isWriteOff() && !loanTransactionDTO.isReversed()) {
                final BigDecimal principalAmount = loanTransactionDTO.getPrincipal();
                helper.createCashBasedJournalEntriesAndReversalsForLoan(office, CASH_ACCOUNTS_FOR_LOAN.LOSSES_WRITTEN_OFF,
                        CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO, loanProductId, paymentTypeId, loanId, transactionId, transactionDate,
                        principalAmount, false);
            } else if (loanTransactionDTO.getTransactionType().isInitiateTransfer()
                    || loanTransactionDTO.getTransactionType().isApproveTransfer()) {
                createJournalEntriesForTransfers(loanDTO, loanTransactionDTO, office);
            }
        }
    }

    /**
     * Debit loan Portfolio and credit Fund source for a Disbursement <br/>
     * 
     * All debits are turned into credits and vice versa in case of disbursement
     * reversals
     * 
     * 
     * @param loanDTO
     * @param loanTransactionDTO
     * @param office
     */
    private void createJournalEntriesForDisbursements(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final Office office) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();
        final Long loanId = loanDTO.getLoanId();

        // transaction properties
        final String transactionId = loanTransactionDTO.getTransactionId();
        final Date transactionDate = loanTransactionDTO.getTransactionDate();
        final BigDecimal disbursalAmount = loanTransactionDTO.getAmount();
        final boolean isReversal = loanTransactionDTO.isReversed();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        helper.createCashBasedJournalEntriesAndReversalsForLoan(office, CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO,
                CASH_ACCOUNTS_FOR_LOAN.FUND_SOURCE, loanProductId, paymentTypeId, loanId, transactionId, transactionDate, disbursalAmount,
                isReversal);
    }

    /**
     * Create a single Debit to fund source and multiple credits if applicable
     * (loan portfolio for principal repayments, Interest on loans for interest
     * repayments, Income from fees for fees payment and Income from penalties
     * for penalty payment)
     * 
     * In case the loan transaction is a reversal, all debits are turned into
     * credits and vice versa
     */
    private void createJournalEntriesForRepayments(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO, final Office office) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();
        final Long loanId = loanDTO.getLoanId();

        // transaction properties
        final String transactionId = loanTransactionDTO.getTransactionId();
        final Date transactionDate = loanTransactionDTO.getTransactionDate();
        final BigDecimal principalAmount = loanTransactionDTO.getPrincipal();
        final BigDecimal interestAmount = loanTransactionDTO.getInterest();
        final BigDecimal feesAmount = loanTransactionDTO.getFees();
        final BigDecimal penaltiesAmount = loanTransactionDTO.getPenalties();
        final boolean isReversal = loanTransactionDTO.isReversed();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        BigDecimal totalDebitAmount = new BigDecimal(0);

        if (principalAmount != null && !(principalAmount.compareTo(BigDecimal.ZERO) == 0)) {
            totalDebitAmount = totalDebitAmount.add(principalAmount);
            helper.createCreditJournalEntryOrReversalForLoan(office, CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO, loanProductId, paymentTypeId,
                    loanId, transactionId, transactionDate, principalAmount, isReversal);
        }

        if (interestAmount != null && !(interestAmount.compareTo(BigDecimal.ZERO) == 0)) {
            totalDebitAmount = totalDebitAmount.add(interestAmount);
            helper.createCreditJournalEntryOrReversalForLoan(office, CASH_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS, loanProductId,
                    paymentTypeId, loanId, transactionId, transactionDate, interestAmount, isReversal);
        }

        if (feesAmount != null && !(feesAmount.compareTo(BigDecimal.ZERO) == 0)) {
            totalDebitAmount = totalDebitAmount.add(feesAmount);
            helper.createCreditJournalEntryOrReversalForLoanCharges(office, CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.getValue(),
                    loanProductId, loanId, transactionId, transactionDate, feesAmount, isReversal, loanTransactionDTO.getFeePayments());
        }

        if (penaltiesAmount != null && !(penaltiesAmount.compareTo(BigDecimal.ZERO) == 0)) {
            totalDebitAmount = totalDebitAmount.add(penaltiesAmount);
            helper.createCreditJournalEntryOrReversalForLoanCharges(office, CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.getValue(),
                    loanProductId, loanId, transactionId, transactionDate, penaltiesAmount, isReversal,
                    loanTransactionDTO.getPenaltyPayments());
        }

        /*** create a single debit entry (or reversal) for the entire amount **/
        helper.createDebitJournalEntryOrReversalForLoan(office, CASH_ACCOUNTS_FOR_LOAN.FUND_SOURCE, loanProductId, paymentTypeId, loanId,
                transactionId, transactionDate, totalDebitAmount, isReversal);
    }

    /**
     * Credit loan Portfolio and Debit Suspense Account for a Transfer
     * Initiation. A Transfer acceptance would be treated the opposite i.e Debit
     * Loan Portfolio and Credit Suspense Account <br/>
     * 
     * All debits are turned into credits and vice versa in case of Transfer
     * Initiation disbursals
     * 
     * 
     * @param loanDTO
     * @param loanTransactionDTO
     * @param office
     */
    private void createJournalEntriesForTransfers(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO, final Office office) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();
        final Long loanId = loanDTO.getLoanId();

        // transaction properties
        final String transactionId = loanTransactionDTO.getTransactionId();
        final Date transactionDate = loanTransactionDTO.getTransactionDate();
        final BigDecimal principalAmount = loanTransactionDTO.getPrincipal();
        final boolean isReversal = loanTransactionDTO.isReversed();
        // final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        if (loanTransactionDTO.getTransactionType().isInitiateTransfer()) {
            helper.createCashBasedJournalEntriesAndReversalsForLoan(office, CASH_ACCOUNTS_FOR_LOAN.TRANSFERS_SUSPENSE,
                    CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO, loanProductId, null, loanId, transactionId, transactionDate, principalAmount,
                    isReversal);
        } else if (loanTransactionDTO.getTransactionType().isApproveTransfer()) {
            helper.createCashBasedJournalEntriesAndReversalsForLoan(office, CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO,
                    CASH_ACCOUNTS_FOR_LOAN.TRANSFERS_SUSPENSE, loanProductId, null, loanId, transactionId, transactionDate,
                    principalAmount, isReversal);
        }
    }
}
