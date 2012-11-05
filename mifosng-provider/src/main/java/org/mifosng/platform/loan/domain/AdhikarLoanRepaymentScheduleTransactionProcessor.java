package org.mifosng.platform.loan.domain;

import java.util.List;

import org.joda.time.LocalDate;
import org.mifosng.platform.currency.domain.MonetaryCurrency;
import org.mifosng.platform.currency.domain.Money;

/**
 * Adhikar style {@link LoanRepaymentScheduleTransactionProcessor}.
 * 
 * From https://mifosforge.jira.com/browse/MIFOS-5636:
 * 
 * Per RBI regulations, all interest must be paid (both current and overdue) before principal is paid.
 * 
 * For example on a loan with two installments due (one current and one overdue) of 220 each (200 principal + 20 interest):
 * 
 * Partial Payment of 40
 * 20 Payment to interest on Installment #1 (200 principal remaining)
 * 20 Payment to interest on Installment #2 (200 principal remaining)
 */
public class AdhikarLoanRepaymentScheduleTransactionProcessor extends AbstractLoanRepaymentScheduleTransactionProcessor {

	/**
	 * For creocore, early is defined as any date before the installment due
	 * date
	 */
	@SuppressWarnings("unused")
	@Override
	protected boolean isTransactionInAdvanceOfInstallment(
			final int currentInstallmentIndex,
			final List<LoanRepaymentScheduleInstallment> installments,
			final LocalDate transactionDate, final Money transactionAmount) {

		LoanRepaymentScheduleInstallment currentInstallment = installments.get(currentInstallmentIndex);

		return transactionDate.isBefore(currentInstallment.getDueDate());
	}

	/**
	 * For early/'in advance' repayments, pays off principal component only.
	 */
	@SuppressWarnings("unused")
	@Override
	protected Money handleTransactionThatIsPaymentInAdvanceOfInstallment(
			final LoanRepaymentScheduleInstallment currentInstallment,
			final List<LoanRepaymentScheduleInstallment> installments,
			final LoanTransaction loanTransaction,
			final LocalDate transactionDate, final Money paymentInAdvance) {
		
		return handleTransactionThatIsOnTimePaymentOfInstallment(currentInstallment, loanTransaction, paymentInAdvance);
	}

	/**
	 * For late repayments, pay off in the same way as on-time payments,
	 * interest first then principal.
	 */
	@Override
	protected Money handleTransactionThatIsALateRepaymentOfInstallment(
			final LoanRepaymentScheduleInstallment currentInstallment,
			final List<LoanRepaymentScheduleInstallment> installments,
			final LoanTransaction loanTransaction,
			final Money transactionAmountUnprocessed) {
		
		// pay of overdue and current interest due given transaction date
		final MonetaryCurrency currency = transactionAmountUnprocessed.getCurrency();
		Money transactionAmountRemaining = transactionAmountUnprocessed;
		Money interestWaivedPortion = Money.zero(currency);
		Money feeChargesPortion = Money.zero(currency);
		Money penaltyChargesPortion = Money.zero(currency);
		
		if (loanTransaction.isInterestWaiver()) {
			interestWaivedPortion = currentInstallment.waiveInterestComponent(transactionAmountRemaining);
			transactionAmountRemaining = transactionAmountRemaining.minus(interestWaivedPortion);
			
			final Money principalPortion = Money.zero(transactionAmountRemaining.getCurrency());
			loanTransaction.updateComponents(principalPortion, interestWaivedPortion, feeChargesPortion, penaltyChargesPortion);
		} else {
		
			LoanRepaymentScheduleInstallment currentInstallmentBasedOnTransactionDate = nearestInstallment(loanTransaction.getTransactionDate(), installments);
			
			for (LoanRepaymentScheduleInstallment installment : installments) {
				if (
					installment.isInterestDue(currency) && 
					(installment.isOverdueOn(loanTransaction.getTransactionDate()) || installment.getInstallmentNumber().equals(currentInstallmentBasedOnTransactionDate.getInstallmentNumber()))
				   ) {	
					Money interestPortion = installment.payInterestComponent(transactionAmountRemaining);
					transactionAmountRemaining = transactionAmountRemaining.minus(interestPortion);
					
					Money principalPortion = Money.zero(currency);
					loanTransaction.updateComponents(principalPortion, interestPortion, Money.zero(currency), Money.zero(currency));
				}
			}
			
			// With whatever is remaining, pay off principal components of installments
			for (LoanRepaymentScheduleInstallment installment : installments) {
				if (installment.isPrincipalNotCompleted(currency) && transactionAmountRemaining.isGreaterThanZero()) {
					Money principalPortion = installment.payPrincipalComponent(transactionAmountRemaining);
					transactionAmountRemaining = transactionAmountRemaining.minus(principalPortion);
					
					Money interestPortion = Money.zero(currency);
					loanTransaction.updateComponents(principalPortion, interestPortion, Money.zero(currency), Money.zero(currency));
				}
			}
		}
		
		return transactionAmountRemaining;
	}

	private LoanRepaymentScheduleInstallment nearestInstallment(final LocalDate transactionDate, List<LoanRepaymentScheduleInstallment> installments) {
		
		LoanRepaymentScheduleInstallment nearest = installments.get(0);
		
		for (LoanRepaymentScheduleInstallment installment : installments) {
			if (installment.getDueDate().isAfter(transactionDate) || installment.getDueDate().isEqual(transactionDate)) {
				nearest = installment;
				break;
			}
		}
		return nearest;
	}

	/**
	 * For normal on-time repayments, pays off interest first, then principal.
	 */
	@Override
	protected Money handleTransactionThatIsOnTimePaymentOfInstallment(
			final LoanRepaymentScheduleInstallment currentInstallment,
			final LoanTransaction loanTransaction,
			final Money transactionAmountUnprocessed) {
		
		Money transactionAmountRemaining = transactionAmountUnprocessed;
		Money principalPortion = Money.zero(transactionAmountRemaining.getCurrency());
		Money interestPortion = Money.zero(transactionAmountRemaining.getCurrency());
		Money feeChargesPortion = Money.zero(transactionAmountRemaining.getCurrency());
		Money penaltyChargesPortion = Money.zero(transactionAmountRemaining.getCurrency());
		
		if (loanTransaction.isInterestWaiver()) {
			interestPortion = currentInstallment.waiveInterestComponent(transactionAmountRemaining);
			transactionAmountRemaining = transactionAmountRemaining.minus(interestPortion);
		} else {
		
			interestPortion = currentInstallment.payInterestComponent(transactionAmountRemaining);
			transactionAmountRemaining = transactionAmountRemaining.minus(interestPortion);
	
			principalPortion = currentInstallment.payPrincipalComponent(transactionAmountRemaining);
			transactionAmountRemaining = transactionAmountRemaining.minus(principalPortion);
		}
		
		loanTransaction.updateComponents(principalPortion, interestPortion, feeChargesPortion, penaltyChargesPortion);
		return transactionAmountRemaining;
	}

	@SuppressWarnings("unused")
	@Override
	protected void onLoanOverpayment(final LoanTransaction loanTransaction,
			final Money loanOverPaymentAmount) {
		// dont do anything for with loan over-payment
	}
}