/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.organisation.monetary.domain.Money;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "m_mandatory_savings_schedule")
public class RecurringDepositScheduleInstallment extends AbstractAuditableCustom<AppUser, Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "savings_account_id")
    private RecurringDepositAccount account;

    @Column(name = "installment", nullable = false)
    private final Integer installmentNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "fromdate", nullable = true)
    private final Date fromDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "duedate", nullable = false)
    private final Date dueDate;

    @Column(name = "deposit_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal depositAmount;

    @Column(name = "deposit_amount_completed_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal depositAmountCompleted;

    @Column(name = "total_paid_in_advance_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal totalPaidInAdvance;

    @Column(name = "total_paid_late_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal totalPaidLate;

    @Column(name = "completed_derived", nullable = false)
    private boolean obligationsMet;

    @Temporal(TemporalType.DATE)
    @Column(name = "obligations_met_on_date")
    private Date obligationsMetOnDate;

    /**
     * 
     */
    protected RecurringDepositScheduleInstallment() {
        this.installmentNumber = null;
        this.fromDate = null;
        this.dueDate = null;
        this.obligationsMet = false;
    }

    /**
     * @param account
     * @param installmentNumber
     * @param fromDate
     * @param dueDate
     * @param depositAmount
     * @param depositAmountCompleted
     * @param totalPaidInAdvance
     * @param totalPaidLate
     * @param obligationsMet
     * @param obligationsMetOnDate
     */
    private RecurringDepositScheduleInstallment(final RecurringDepositAccount account, final Integer installmentNumber,
            final Date fromDate, final Date dueDate, final BigDecimal depositAmount, final BigDecimal depositAmountCompleted,
            final BigDecimal totalPaidInAdvance, final BigDecimal totalPaidLate, final boolean obligationsMet,
            final Date obligationsMetOnDate) {
        this.account = account;
        this.installmentNumber = installmentNumber;
        this.fromDate = fromDate;
        this.dueDate = dueDate;
        this.depositAmount = defaultToNullIfZero(depositAmount);
        this.depositAmountCompleted = depositAmountCompleted;
        this.totalPaidInAdvance = totalPaidInAdvance;
        this.totalPaidLate = totalPaidLate;
        this.obligationsMet = obligationsMet;
        this.obligationsMetOnDate = obligationsMetOnDate;
    }

    public static RecurringDepositScheduleInstallment from(final RecurringDepositAccount account, final Integer installmentNumber,
            final Date fromDate, final Date dueDate, final BigDecimal depositAmount, final BigDecimal depositAmountCompleted,
            final BigDecimal totalPaidInAdvance, final BigDecimal totalPaidLate, final boolean obligationsMet,
            final Date obligationsMetOnDate) {
        return new RecurringDepositScheduleInstallment(account, installmentNumber, fromDate, dueDate, depositAmount,
                depositAmountCompleted, totalPaidInAdvance, totalPaidLate, obligationsMet, obligationsMetOnDate);
    }

    public static RecurringDepositScheduleInstallment installment(final RecurringDepositAccount account, final Integer installmentNumber,
            final Date dueDate, final BigDecimal depositAmount) {

        final Date fromDate = null;
        final BigDecimal depositAmountCompleted = null;
        final BigDecimal totalPaidInAdvance = null;
        final BigDecimal totalPaidLate = null;
        final boolean obligationsMet = false;
        final Date obligationsMetOnDate = null;

        return new RecurringDepositScheduleInstallment(account, installmentNumber, fromDate, dueDate, depositAmount,
                depositAmountCompleted, totalPaidInAdvance, totalPaidLate, obligationsMet, obligationsMetOnDate);
    }

    private BigDecimal defaultToNullIfZero(final BigDecimal value) {
        BigDecimal result = value;
        if (BigDecimal.ZERO.compareTo(value) == 0) {
            result = null;
        }
        return result;
    }

    public boolean isObligationsMet() {
        return this.obligationsMet;
    }

    public boolean isNotFullyPaidOff() {
        return !this.obligationsMet;
    }

    public boolean isPrincipalNotCompleted(final MonetaryCurrency currency) {
        return !isPrincipalCompleted(currency);
    }

    public boolean isPrincipalCompleted(final MonetaryCurrency currency) {
        return getDepositAmountOutstanding(currency).isZero();
    }

    public Money getDepositAmountOutstanding(final MonetaryCurrency currency) {
        final Money depositAmountAccountedFor = getDepositAmountCompleted(currency);
        return getDepositAmount(currency).minus(depositAmountAccountedFor);
    }

    public Money getDepositAmountCompleted(final MonetaryCurrency currency) {
        return Money.of(currency, this.depositAmountCompleted);
    }

    public Money getDepositAmount(final MonetaryCurrency currency) {
        return Money.of(currency, this.depositAmount);
    }

    public LocalDate dueDate() {
        return (this.dueDate == null) ? null : new LocalDate(this.dueDate);
    }

    public Money payInstallment(final LocalDate transactionDate, final Money transactionAmountRemaining) {

        final MonetaryCurrency currency = transactionAmountRemaining.getCurrency();
        Money depositAmountPortionOfTransaction = Money.zero(currency);

        final Money depositAmount = getDepositAmountOutstanding(currency);
        if (transactionAmountRemaining.isGreaterThanOrEqualTo(depositAmount)) {
            this.depositAmountCompleted = getDepositAmountCompleted(currency).plus(depositAmount).getAmount();
            depositAmountPortionOfTransaction = depositAmountPortionOfTransaction.plus(depositAmount);
        } else {
            this.depositAmountCompleted = getDepositAmountCompleted(currency).plus(transactionAmountRemaining).getAmount();
            depositAmountPortionOfTransaction = depositAmountPortionOfTransaction.plus(transactionAmountRemaining);
        }

        this.depositAmountCompleted = defaultToNullIfZero(this.depositAmountCompleted);

        checkIfInstallmentObligationsAreMet(transactionDate, currency);

        trackAdvanceAndLateTotalsForInstallment(transactionDate, currency, depositAmountPortionOfTransaction);

        return depositAmountPortionOfTransaction;
    }

    private void checkIfInstallmentObligationsAreMet(final LocalDate transactionDate, final MonetaryCurrency currency) {
        this.obligationsMet = getTotalOutstanding(currency).isZero();
        if (this.obligationsMet) {
            this.obligationsMetOnDate = transactionDate.toDate();
        }
    }

    public Money getTotalOutstanding(final MonetaryCurrency currency) {
        return getDepositAmountOutstanding(currency);
    }

    private void trackAdvanceAndLateTotalsForInstallment(final LocalDate transactionDate, final MonetaryCurrency currency,
            final Money amountPaidInInstallment) {
        if (isInAdvance(transactionDate)) {
            this.totalPaidInAdvance = asMoney(this.totalPaidInAdvance, currency).plus(amountPaidInInstallment).getAmount();
        } else if (isLatePayment(transactionDate)) {
            this.totalPaidLate = asMoney(this.totalPaidLate, currency).plus(amountPaidInInstallment).getAmount();
        }
    }

    private boolean isInAdvance(final LocalDate transactionDate) {
        return transactionDate.isBefore(dueDate());
    }

    private boolean isLatePayment(final LocalDate transactionDate) {
        return transactionDate.isAfter(dueDate());
    }

    private Money asMoney(final BigDecimal decimal, final MonetaryCurrency currency) {
        return Money.of(currency, decimal);
    }

    public void resetDerivedFields() {
        this.depositAmountCompleted = null;
        this.totalPaidInAdvance = null;
        this.totalPaidLate = null;
        this.obligationsMet = false;
        this.obligationsMetOnDate = null;
    }

    public void updateDepositAmountAndResetDerivedFields(BigDecimal newDepositAmount) {
        this.depositAmount = newDepositAmount;
        this.resetDerivedFields();
    }
}