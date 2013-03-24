/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.LocalDate;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.organisation.monetary.domain.Money;
import org.mifosplatform.portfolio.loanaccount.data.LoanTransactionData;
import org.mifosplatform.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.mifosplatform.portfolio.loanproduct.service.LoanEnumerations;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * All monetary transactions against a loan are modelled through this entity.
 * Disbursements, Repayments, Waivers, Write-off etc
 */
@Entity
@Table(name = "m_loan_transaction")
public final class LoanTransaction extends AbstractPersistable<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(name = "transaction_type_enum", nullable = false)
    private final Integer typeOf;

    @Temporal(TemporalType.DATE)
    @Column(name = "transaction_date", nullable = false)
    private final Date dateOf;

    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;

    @Column(name = "principal_portion_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal principalPortion;

    @Column(name = "interest_portion_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal interestPortion;

    @Column(name = "fee_charges_portion_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal feeChargesPortion;

    @Column(name = "penalty_charges_portion_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal penaltyChargesPortion;

    @Column(name = "is_reversed", nullable = false)
    private boolean reversed;

    protected LoanTransaction() {
        this.loan = null;
        this.dateOf = null;
        this.typeOf = null;
    }

    public static LoanTransaction disbursement(final Money amount, final LocalDate disbursementDate) {
        return new LoanTransaction(null, LoanTransactionType.DISBURSEMENT, amount.getAmount(), disbursementDate);
    }

    public static LoanTransaction repayment(final Money amount, final LocalDate paymentDate) {
        return new LoanTransaction(null, LoanTransactionType.REPAYMENT, amount.getAmount(), paymentDate);
    }

    public static LoanTransaction repaymentAtDisbursement(final Money amount, final LocalDate paymentDate) {
        return new LoanTransaction(null, LoanTransactionType.REPAYMENT_AT_DISBURSEMENT, amount.getAmount(), paymentDate);
    }

    public static LoanTransaction waiver(final Loan loan, final Money waived, final LocalDate waiveDate) {
        return new LoanTransaction(loan, LoanTransactionType.WAIVE_INTEREST, waived.getAmount(), waiveDate);
    }

    public static LoanTransaction applyInterest(final Loan loan, final Money amount, final LocalDate interestAppliedDate) {
        return new LoanTransaction(loan, LoanTransactionType.APPLY_INTEREST, amount.getAmount(), interestAppliedDate);
    }

    public static LoanTransaction copyTransactionProperties(LoanTransaction loanTransaction) {
        return new LoanTransaction(loanTransaction.loan, loanTransaction.typeOf, loanTransaction.dateOf, loanTransaction.amount,
                loanTransaction.principalPortion, loanTransaction.interestPortion, loanTransaction.feeChargesPortion,
                loanTransaction.penaltyChargesPortion, loanTransaction.reversed);
    }

    public static LoanTransaction applyLoanCharge(final Loan loan, final Money amount, final LocalDate applyDate, final Money feeCharges,
            final Money penaltyCharges) {
        LoanTransaction applyCharge = new LoanTransaction(loan, LoanTransactionType.APPLY_CHARGES, amount.getAmount(), applyDate);
        applyCharge.updateChargesComponents(feeCharges, penaltyCharges);
        return applyCharge;
    }

    public static boolean transactionAmountsMatch(MonetaryCurrency currency, LoanTransaction loanTransaction,
            LoanTransaction newLoanTransaction) {
        if (loanTransaction.getAmount(currency).isEqualTo(newLoanTransaction.getAmount(currency))
                && (loanTransaction.getPrincipalPortion(currency).isEqualTo(newLoanTransaction.getPrincipalPortion(currency)))
                && (loanTransaction.getInterestPortion(currency).isEqualTo(newLoanTransaction.getInterestPortion(currency)))
                && (loanTransaction.getFeeChargesPortion(currency).isEqualTo(newLoanTransaction.getFeeChargesPortion(currency)))
                && (loanTransaction.getPenaltyChargesPortion(currency).isEqualTo(newLoanTransaction.getPenaltyChargesPortion(currency)))) { return true; }
        return false;
    }

    private LoanTransaction(Loan loan, Integer typeOf, Date dateOf, BigDecimal amount, BigDecimal principalPortion,
            BigDecimal interestPortion, BigDecimal feeChargesPortion, BigDecimal penaltyChargesPortion, boolean reversed) {
        super();
        this.loan = loan;
        this.typeOf = typeOf;
        this.dateOf = dateOf;
        this.amount = amount;
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
        this.feeChargesPortion = feeChargesPortion;
        this.penaltyChargesPortion = penaltyChargesPortion;
        this.reversed = reversed;
    }

    public static LoanTransaction waiveLoanCharge(final Loan loan, final Money waived, final LocalDate waiveDate,
            final Money feeChargesWaived, final Money penaltyChargesWaived) {
        LoanTransaction waiver = new LoanTransaction(loan, LoanTransactionType.WAIVE_CHARGES, waived.getAmount(), waiveDate);
        waiver.updateChargesComponents(feeChargesWaived, penaltyChargesWaived);

        return waiver;
    }

    public static LoanTransaction writeoff(final Loan loan, final LocalDate writeOffDate) {
        return new LoanTransaction(loan, LoanTransactionType.WRITEOFF, null, writeOffDate);
    }

    private LoanTransaction(final Loan loan, final LoanTransactionType type, final BigDecimal amount, final LocalDate date) {
        this.loan = loan;
        this.typeOf = type.getValue();
        this.amount = amount;
        this.dateOf = date.toDateMidnight().toDate();
    }

    public void reverse() {
        this.reversed = true;
    }

    public void resetDerivedComponents() {
        this.principalPortion = null;
        this.interestPortion = null;
        this.feeChargesPortion = null;
        this.penaltyChargesPortion = null;
    }

    public void updateLoan(final Loan loan) {
        this.loan = loan;
    }

    /**
     * This updates the derived fields of a loan transaction for the principal,
     * interest and interest waived portions.
     * 
     * This accumulates the values passed to the already existent values for
     * each of the portions.
     */
    public void updateComponents(final Money principal, final Money interest, final Money feeCharges, final Money penaltyCharges) {
        final MonetaryCurrency currency = principal.getCurrency();
        this.principalPortion = defaultToNullIfZero(getPrincipalPortion(currency).plus(principal).getAmount());
        this.interestPortion = defaultToNullIfZero(getInterestPortion(currency).plus(interest).getAmount());
        updateChargesComponents(feeCharges, penaltyCharges);
    }

    private void updateChargesComponents(final Money feeCharges, final Money penaltyCharges) {
        final MonetaryCurrency currency = feeCharges.getCurrency();
        this.feeChargesPortion = defaultToNullIfZero(getFeeChargesPortion(currency).plus(feeCharges).getAmount());
        this.penaltyChargesPortion = defaultToNullIfZero(getPenaltyChargesPortion(currency).plus(penaltyCharges).getAmount());
    }

    public void updateComponentsAndTotal(final Money principal, final Money interest, final Money feeCharges, final Money penaltyCharges) {
        updateComponents(principal, interest, feeCharges, penaltyCharges);

        final MonetaryCurrency currency = principal.getCurrency();
        this.amount = getPrincipalPortion(currency).plus(getInterestPortion(currency)).plus(getFeeChargesPortion(currency))
                .plus(getPenaltyChargesPortion(currency)).getAmount();
    }

    public Money getPrincipalPortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.principalPortion);
    }

    public BigDecimal getPrincipalPortion() {
        return this.principalPortion;
    }

    public Money getInterestPortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.interestPortion);
    }

    public Money getFeeChargesPortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.feeChargesPortion);
    }

    public Money getPenaltyChargesPortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.penaltyChargesPortion);
    }

    public Money getAmount(final MonetaryCurrency currency) {
        return Money.of(currency, this.amount);
    }

    public LocalDate getTransactionDate() {
        return new LocalDate(this.dateOf);
    }

    public Date getDateOf() {
        return dateOf;
    }

    public LoanTransactionType getTypeOf() {
        return LoanTransactionType.fromInt(this.typeOf);
    }

    public boolean isReversed() {
        return this.reversed;
    }

    public boolean isNotReversed() {
        return !isReversed();
    }

    public boolean isRepayment() {
        return LoanTransactionType.REPAYMENT.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isNotRepayment() {
        return !isRepayment();
    }

    public boolean isDisbursement() {
        return LoanTransactionType.DISBURSEMENT.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isRepaymentAtDisbursement() {
        return LoanTransactionType.REPAYMENT_AT_DISBURSEMENT.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isRecoveryRepayment() {
        return LoanTransactionType.RECOVERY_REPAYMENT.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isInterestWaiver() {
        return LoanTransactionType.WAIVE_INTEREST.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isChargesWaiver() {
        return LoanTransactionType.WAIVE_CHARGES.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isNotInterestWaiver() {
        return !isInterestWaiver();
    }

    public boolean isWaiver() {
        return isInterestWaiver() || isChargesWaiver();
    }

    public boolean isNotWaiver() {
        return !isInterestWaiver() && !isChargesWaiver();
    }

    public boolean isWriteOff() {
        return getTypeOf().isWriteOff() && isNotReversed();
    }

    public boolean isIdentifiedBy(final Long identifier) {
        return this.getId().equals(identifier);
    }

    public boolean isBelongingToLoanOf(final Loan check) {
        return this.loan.getId().equals(check.getId());
    }

    public boolean isNotBelongingToLoanOf(final Loan check) {
        return !isBelongingToLoanOf(check);
    }

    public boolean isNonZero() {
        return this.amount.subtract(BigDecimal.ZERO).doubleValue() > 0;
    }

    public boolean isGreaterThan(final Money monetaryAmount) {
        return getAmount(monetaryAmount.getCurrency()).isGreaterThan(monetaryAmount);
    }

    public boolean isGreaterThanZero(final MonetaryCurrency currency) {
        return getAmount(currency).isGreaterThanZero();
    }

    public boolean isNotZero(final MonetaryCurrency currency) {
        return !getAmount(currency).isZero();
    }

    private BigDecimal defaultToNullIfZero(final BigDecimal value) {
        BigDecimal result = value;
        if (BigDecimal.ZERO.compareTo(value) == 0) {
            result = null;
        }
        return result;
    }

    public LoanTransactionData toData(final CurrencyData currencyData) {
        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(this.typeOf);
        return new LoanTransactionData(this.getId(), transactionType, currencyData, getTransactionDate(), this.amount,
                this.principalPortion, this.interestPortion, this.feeChargesPortion, this.penaltyChargesPortion);
    }

    public Map<String, Object> toMapData(final CurrencyData currencyData) {
        final Map<String, Object> thisTransactionData = new LinkedHashMap<String, Object>();

        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(this.typeOf);

        thisTransactionData.put("id", this.getId());
        thisTransactionData.put("type", transactionType);
        thisTransactionData.put("reversed", Boolean.valueOf(this.isReversed()));
        thisTransactionData.put("date", this.getTransactionDate());
        thisTransactionData.put("currency", currencyData);
        thisTransactionData.put("amount", this.amount);
        thisTransactionData.put("principalPortion", this.principalPortion);
        thisTransactionData.put("interestPortion", this.interestPortion);
        thisTransactionData.put("feeChargesPortion", this.feeChargesPortion);
        thisTransactionData.put("penaltyChargesPortion", this.penaltyChargesPortion);

        return thisTransactionData;
    }

    public Loan getLoan() {
        return this.loan;
    }

}