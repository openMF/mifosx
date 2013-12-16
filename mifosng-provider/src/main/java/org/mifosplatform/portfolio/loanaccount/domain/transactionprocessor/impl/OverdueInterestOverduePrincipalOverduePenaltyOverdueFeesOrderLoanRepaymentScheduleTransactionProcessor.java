/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.domain.transactionprocessor.impl;

import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.organisation.monetary.domain.Money;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.mifosplatform.portfolio.loanaccount.domain.LoanTransaction;
import org.mifosplatform.portfolio.loanaccount.domain.transactionprocessor.AbstractLoanRepaymentScheduleTransactionProcessor;
import org.mifosplatform.portfolio.loanaccount.domain.transactionprocessor.LoanRepaymentScheduleTransactionProcessor;

/**
 * This {@link LoanRepaymentScheduleTransactionProcessor} defaults to having the
 * payment order of Interest first, then principal, penalties and fees.
 */
public class OverdueInterestOverduePrincipalOverduePenaltyOverdueFeesOrderLoanRepaymentScheduleTransactionProcessor extends
        AbstractLoanRepaymentScheduleTransactionProcessor {

    /**
     * For early/'in advance' repayments, pay off in the same way as on-time
     * payments, interest first, principal, penalties and charges.
     */
    @Override
    protected Money handleTransactionThatIsPaymentInAdvanceOfInstallment(final LoanRepaymentScheduleInstallment currentInstallment,
            final List<LoanRepaymentScheduleInstallment> installments, final LoanTransaction loanTransaction,
            final LocalDate transactionDate, final Money paymentInAdvance) {

        return handleTransactionThatIsOnTimePaymentOfInstallment(currentInstallment, loanTransaction, paymentInAdvance);
    }

    /**
     * For late repayments, pay off in the same way as on-time payments, overdue
     * interest first then overdue principal.
     */
    @Override
    protected Money handleTransactionThatIsALateRepaymentOfInstallment(final LoanRepaymentScheduleInstallment currentInstallment,
            final List<LoanRepaymentScheduleInstallment> installments, final LoanTransaction loanTransaction,
            final Money transactionAmountUnprocessed) {

        final LocalDate transactionDate = loanTransaction.getTransactionDate();
        final MonetaryCurrency currency = transactionAmountUnprocessed.getCurrency();
        Money transactionAmountRemaining = transactionAmountUnprocessed;
        Money principalPortion = Money.zero(transactionAmountRemaining.getCurrency());
        Money interestPortion = Money.zero(transactionAmountRemaining.getCurrency());
        Money feeChargesPortion = Money.zero(transactionAmountRemaining.getCurrency());
        Money penaltyChargesPortion = Money.zero(transactionAmountRemaining.getCurrency());

        if (loanTransaction.isChargesWaiver()) {
            // zero this type of transaction and ignore it for now.
            transactionAmountRemaining = Money.zero(currency);
        } else if (loanTransaction.isInterestWaiver()) {
            interestPortion = currentInstallment.waiveInterestComponent(transactionDate, transactionAmountRemaining);
            transactionAmountRemaining = transactionAmountRemaining.minus(interestPortion);

            loanTransaction.updateComponents(principalPortion, interestPortion, feeChargesPortion, penaltyChargesPortion);
        } else if (loanTransaction.isChargePayment()) {
            if (loanTransaction.isPenaltyPayment()) {
                penaltyChargesPortion = currentInstallment.payPenaltyChargesComponent(transactionDate, transactionAmountRemaining);
                transactionAmountRemaining = transactionAmountRemaining.minus(penaltyChargesPortion);
            } else {
                feeChargesPortion = currentInstallment.payFeeChargesComponent(transactionDate, transactionAmountRemaining);
                transactionAmountRemaining = transactionAmountRemaining.minus(feeChargesPortion);
            }
            loanTransaction.updateComponents(principalPortion, interestPortion, feeChargesPortion, penaltyChargesPortion);
        } else {
            // 1) Iterate through all overdue installments to pay interest first
            for (final LoanRepaymentScheduleInstallment installment : installments) {
                if (transactionAmountRemaining.isGreaterThanZero()) {
                    if (installment.getInstallmentNumber() >= currentInstallment.getInstallmentNumber()) {
                        if (loanTransaction.getTransactionDate().isAfter(installment.getDueDate()) || 
                        		loanTransaction.getTransactionDate().isEqual(installment.getDueDate())) {
                            if (installment.isInterestDue(currency)) {

                                // Make sure only interest is updating.
                                principalPortion = Money.zero(currency);
                                feeChargesPortion = Money.zero(currency);
                                penaltyChargesPortion = Money.zero(currency);

                                interestPortion = installment.payInterestComponent(transactionDate, transactionAmountRemaining);
                                transactionAmountRemaining = transactionAmountRemaining.minus(interestPortion);
                                loanTransaction.updateComponents(principalPortion, interestPortion, feeChargesPortion,
                                        penaltyChargesPortion);
                            }
                        }
                    }
                }
            }

            // 2) Iterate through all overdue installments to pay principal
            for (final LoanRepaymentScheduleInstallment installment : installments) {
                if (transactionAmountRemaining.isGreaterThanZero()) {
                    if (installment.getInstallmentNumber() >= currentInstallment.getInstallmentNumber()) {
                    	if (loanTransaction.getTransactionDate().isAfter(installment.getDueDate()) || 
                        		loanTransaction.getTransactionDate().isEqual(installment.getDueDate())) {
                            if (installment.isPrincipalNotCompleted(currency)) {

                                // Make sure only principal is updating.
                                interestPortion = Money.zero(currency);
                                feeChargesPortion = Money.zero(currency);
                                penaltyChargesPortion = Money.zero(currency);

                                principalPortion = installment.payPrincipalComponent(transactionDate, transactionAmountRemaining);
                                transactionAmountRemaining = transactionAmountRemaining.minus(principalPortion);
                                loanTransaction.updateComponents(principalPortion, interestPortion, feeChargesPortion,
                                        penaltyChargesPortion);
                            }
                        }
                    }
                }
            }

            // 3) Iterate through all overdue installments to pay penalties
            for (final LoanRepaymentScheduleInstallment installment : installments) {
                if (transactionAmountRemaining.isGreaterThanZero()) {
                    if (installment.getInstallmentNumber() >= currentInstallment.getInstallmentNumber()) {
                    	if (loanTransaction.getTransactionDate().isAfter(installment.getDueDate()) || 
                        		loanTransaction.getTransactionDate().isEqual(installment.getDueDate())) {
                            if (installment.isPenaltyDue(currency)) {

                                // Make sure only penalty is updating.
                                principalPortion = Money.zero(currency);
                                interestPortion = Money.zero(currency);
                                feeChargesPortion = Money.zero(currency);

                                penaltyChargesPortion = installment.payPenaltyChargesComponent(transactionDate, transactionAmountRemaining);
                                transactionAmountRemaining = transactionAmountRemaining.minus(penaltyChargesPortion);
                                loanTransaction.updateComponents(principalPortion, interestPortion, feeChargesPortion,
                                        penaltyChargesPortion);
                            }
                        }
                    }
                }
            }

            // 4) Iterate through all overdue installments to pay Fees
            for (final LoanRepaymentScheduleInstallment installment : installments) {
                if (transactionAmountRemaining.isGreaterThanZero()) {
                    if (installment.getInstallmentNumber() >= currentInstallment.getInstallmentNumber()) {
                    	if (loanTransaction.getTransactionDate().isAfter(installment.getDueDate()) || 
                        		loanTransaction.getTransactionDate().isEqual(installment.getDueDate())) {
                            if (installment.isChargesDue(currency)) {

                                // Make sure only Fee is updating.
                                principalPortion = Money.zero(currency);
                                interestPortion = Money.zero(currency);
                                penaltyChargesPortion = Money.zero(currency);

                                feeChargesPortion = installment.payFeeChargesComponent(transactionDate, transactionAmountRemaining);
                                transactionAmountRemaining = transactionAmountRemaining.minus(feeChargesPortion);
                                loanTransaction.updateComponents(principalPortion, interestPortion, feeChargesPortion,
                                        penaltyChargesPortion);
                            }
                        }
                    }
                }
            }
        }

        return transactionAmountRemaining;
    }

    /**
     * For normal on-time repayments, pays off interest first, then principal.
     */
    @Override
    protected Money handleTransactionThatIsOnTimePaymentOfInstallment(final LoanRepaymentScheduleInstallment currentInstallment,
            final LoanTransaction loanTransaction, final Money transactionAmountUnprocessed) {

        final LocalDate transactionDate = loanTransaction.getTransactionDate();
        final MonetaryCurrency currency = transactionAmountUnprocessed.getCurrency();
        Money transactionAmountRemaining = transactionAmountUnprocessed;
        Money principalPortion = Money.zero(transactionAmountRemaining.getCurrency());
        Money interestPortion = Money.zero(transactionAmountRemaining.getCurrency());
        Money feeChargesPortion = Money.zero(transactionAmountRemaining.getCurrency());
        Money penaltyChargesPortion = Money.zero(transactionAmountRemaining.getCurrency());

        if (loanTransaction.isChargesWaiver()) {
            // zero this type of transaction and ignore it for now.
            transactionAmountRemaining = Money.zero(currency);
        } else if (loanTransaction.isInterestWaiver()) {
            interestPortion = currentInstallment.waiveInterestComponent(transactionDate, transactionAmountRemaining);
            transactionAmountRemaining = transactionAmountRemaining.minus(interestPortion);

            loanTransaction.updateComponents(principalPortion, interestPortion, feeChargesPortion, penaltyChargesPortion);
        } else if (loanTransaction.isChargePayment()) {
            if (loanTransaction.isPenaltyPayment()) {
                penaltyChargesPortion = currentInstallment.payPenaltyChargesComponent(transactionDate, transactionAmountRemaining);
                transactionAmountRemaining = transactionAmountRemaining.minus(penaltyChargesPortion);
            } else {
                feeChargesPortion = currentInstallment.payFeeChargesComponent(transactionDate, transactionAmountRemaining);
                transactionAmountRemaining = transactionAmountRemaining.minus(feeChargesPortion);
            }
            loanTransaction.updateComponents(principalPortion, interestPortion, feeChargesPortion, penaltyChargesPortion);
        } else {
            interestPortion = currentInstallment.payInterestComponent(transactionDate, transactionAmountRemaining);
            transactionAmountRemaining = transactionAmountRemaining.minus(interestPortion);

            principalPortion = currentInstallment.payPrincipalComponent(transactionDate, transactionAmountRemaining);
            transactionAmountRemaining = transactionAmountRemaining.minus(principalPortion);

            penaltyChargesPortion = currentInstallment.payPenaltyChargesComponent(transactionDate, transactionAmountRemaining);
            transactionAmountRemaining = transactionAmountRemaining.minus(penaltyChargesPortion);

            feeChargesPortion = currentInstallment.payFeeChargesComponent(transactionDate, transactionAmountRemaining);
            transactionAmountRemaining = transactionAmountRemaining.minus(feeChargesPortion);

            loanTransaction.updateComponents(principalPortion, interestPortion, feeChargesPortion, penaltyChargesPortion);
        }

        return transactionAmountRemaining;
    }
}