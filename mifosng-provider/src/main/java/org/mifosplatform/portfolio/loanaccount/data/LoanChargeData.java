/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.data;

import java.math.BigDecimal;
import java.util.Collection;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.portfolio.charge.data.ChargeData;
import org.mifosplatform.portfolio.charge.domain.ChargePaymentMode;
import org.mifosplatform.portfolio.charge.domain.ChargeTimeType;

/**
 * Immutable data object for loan charge data.
 */
public class LoanChargeData {

    private final Long id;
    private final Long chargeId;
    private final String name;
    private final EnumOptionData chargeTimeType;

    private final LocalDate dueDate;

    private final EnumOptionData chargeCalculationType;

    private final BigDecimal percentage;

    private final BigDecimal amountPercentageAppliedTo;

    private final CurrencyData currency;

    private final BigDecimal amount;

    private final BigDecimal amountPaid;
    private final BigDecimal amountWaived;
    private final BigDecimal amountWrittenOff;

    private final BigDecimal amountOutstanding;

    private final BigDecimal amountOrPercentage;

    private final Collection<ChargeData> chargeOptions;

    private final boolean penalty;

    private final EnumOptionData chargePaymentMode;

    private final boolean paid;

    private final boolean waived;

    private final boolean chargePayable;

    private final Long loanId;

    private final BigDecimal minCap;

    private final BigDecimal maxCap;
    
    @SuppressWarnings("unused")
    private final Collection<LoanInstallmentChargeData> installmentChargeData;

    public static LoanChargeData template(final Collection<ChargeData> chargeOptions) {
        return new LoanChargeData(null, null, null, null, null, null, null, null, chargeOptions, false, null, false, false, null, null,
                null,null,null);
    }

    /**
     * used when populating with details from charge definition (for crud on
     * charges)
     */
    public static LoanChargeData newLoanChargeDetails(final Long chargeId, final String name, final CurrencyData currency,
            final BigDecimal amount, final BigDecimal percentage, final EnumOptionData chargeTimeType,
            final EnumOptionData chargeCalculationType, final boolean penalty, final EnumOptionData chargePaymentMode,
            final BigDecimal minCap, final BigDecimal maxCap) {
        return new LoanChargeData(null, chargeId, name, currency, amount, percentage, chargeTimeType, chargeCalculationType, null, penalty,
                chargePaymentMode, false, false, null, minCap, maxCap,null, null);
    }

    public LoanChargeData(final Long id, final Long chargeId, final String name, final CurrencyData currency, final BigDecimal amount,
            final BigDecimal amountPaid, final BigDecimal amountWaived, final BigDecimal amountWrittenOff,
            final BigDecimal amountOutstanding, final EnumOptionData chargeTimeType, final LocalDate dueDate,
            final EnumOptionData chargeCalculationType, final BigDecimal percentage, final BigDecimal amountPercentageAppliedTo,
            final boolean penalty, final EnumOptionData chargePaymentMode, final boolean paid, final boolean waived, final Long loanId,
            final BigDecimal minCap, final BigDecimal maxCap,final BigDecimal amountOrPercentage, Collection<LoanInstallmentChargeData> installmentChargeData) {
        this.id = id;
        this.chargeId = chargeId;
        this.name = name;
        this.currency = currency;
        this.amount = amount;
        this.amountPaid = amountPaid;
        this.amountWaived = amountWaived;
        this.amountWrittenOff = amountWrittenOff;
        this.amountOutstanding = amountOutstanding;
        this.chargeTimeType = chargeTimeType;
        this.dueDate = dueDate;
        this.chargeCalculationType = chargeCalculationType;
        this.percentage = percentage;
        this.amountPercentageAppliedTo = amountPercentageAppliedTo;
        this.penalty = penalty;
        this.chargePaymentMode = chargePaymentMode;
        this.paid = paid;
        this.waived = waived;
        this.minCap = minCap;
        this.maxCap = maxCap;
        if(amountOrPercentage == null){
            if (chargeCalculationType != null && chargeCalculationType.getId().intValue() > 1) {
                this.amountOrPercentage = this.percentage;
            } else {
                this.amountOrPercentage = amount;
            }
        }else{
            this.amountOrPercentage = amountOrPercentage;
        }

        this.chargeOptions = null;
        this.chargePayable = isChargePayable();
        this.loanId = loanId;
        this.installmentChargeData = installmentChargeData;
    }

    private LoanChargeData(final Long id, final Long chargeId, final String name, final CurrencyData currency, final BigDecimal amount,
            final BigDecimal percentage, final EnumOptionData chargeTimeType, final EnumOptionData chargeCalculationType,
            final Collection<ChargeData> chargeOptions, final boolean penalty, final EnumOptionData chargePaymentMode, final boolean paid,
            final boolean waived, final Long loanId, final BigDecimal minCap, final BigDecimal maxCap,final BigDecimal amountOrPercentage, Collection<LoanInstallmentChargeData> installmentChargeData) {
        this.id = id;
        this.chargeId = chargeId;
        this.name = name;
        this.currency = currency;
        this.amount = amount;
        this.amountPaid = BigDecimal.ZERO;
        this.amountWaived = BigDecimal.ZERO;
        this.amountWrittenOff = BigDecimal.ZERO;
        this.amountOutstanding = amount;
        this.chargeTimeType = chargeTimeType;
        this.dueDate = null;
        this.chargeCalculationType = chargeCalculationType;
        this.percentage = percentage;
        this.amountPercentageAppliedTo = null;
        this.penalty = penalty;
        this.chargePaymentMode = chargePaymentMode;
        this.paid = paid;
        this.waived = waived;

        if(amountOrPercentage == null){
            if (chargeCalculationType != null && chargeCalculationType.getId().intValue() > 1) {
                this.amountOrPercentage = this.percentage;
            } else {
                this.amountOrPercentage = amount;
            }
        }else{
            this.amountOrPercentage = amountOrPercentage;
        }

        this.chargeOptions = chargeOptions;
        this.chargePayable = isChargePayable();
        this.loanId = loanId;
        this.minCap = minCap;
        this.maxCap = maxCap;
        this.installmentChargeData = installmentChargeData;
    }

    public LoanChargeData(final Long id, final LocalDate dueAsOfDate, final BigDecimal amountOutstanding, EnumOptionData chargeTimeType, final Long loanId, Collection<LoanInstallmentChargeData> installmentChargeData) {
        this.id = id;
        this.chargeId = null;
        this.name = null;
        this.currency = null;
        this.amount = null;
        this.amountPaid = null;
        this.amountWaived = null;
        this.amountWrittenOff = null;
        this.amountOutstanding = amountOutstanding;
        this.chargeTimeType = chargeTimeType;
        this.dueDate = dueAsOfDate;
        this.chargeCalculationType = null;
        this.percentage = null;
        this.amountPercentageAppliedTo = null;
        this.penalty = false;
        this.chargePaymentMode = null;
        this.paid = false;
        this.waived = false;
        this.amountOrPercentage = null;
        this.chargeOptions = null;
        this.chargePayable = false;
        this.loanId = loanId;
        this.minCap = null;
        this.maxCap = null;
        this.installmentChargeData = installmentChargeData;
    }
    
    public LoanChargeData(LoanChargeData chargeData, Collection<LoanInstallmentChargeData> installmentChargeData) {
        this.id = chargeData.id;
        this.chargeId = chargeData.chargeId;
        this.name = chargeData.name;
        this.currency = chargeData.currency;
        this.amount = chargeData.amount;
        this.amountPaid = chargeData.amountPaid;
        this.amountWaived = chargeData.amountWaived;
        this.amountWrittenOff = chargeData.amountWrittenOff;
        this.amountOutstanding = chargeData.amountOutstanding;
        this.chargeTimeType = chargeData.chargeTimeType;
        this.dueDate = chargeData.dueDate;
        this.chargeCalculationType = chargeData.chargeCalculationType;
        this.percentage = chargeData.percentage;
        this.amountPercentageAppliedTo = chargeData.amountPercentageAppliedTo;
        this.penalty = chargeData.penalty;
        this.chargePaymentMode = chargeData.chargePaymentMode;
        this.paid = chargeData.paid;
        this.waived = chargeData.waived;
        this.minCap = chargeData.minCap;
        this.maxCap = chargeData.maxCap;
        this.amountOrPercentage = chargeData.amountOrPercentage;
        this.chargeOptions = chargeData.chargeOptions;
        this.chargePayable = chargeData.chargePayable;
        this.loanId = chargeData.loanId;
        this.installmentChargeData = installmentChargeData;
    }


    public boolean isChargePayable() {
        boolean isAccountTransfer = false;
        if (this.chargePaymentMode != null) {
            isAccountTransfer = ChargePaymentMode.fromInt(this.chargePaymentMode.getId().intValue()).isPaymentModeAccountTransfer();
        }
        return isAccountTransfer && !this.paid && !this.waived;
    }

    public Long getId() {
        return this.id;
    }

    public LocalDate getDueDate() {
        return this.dueDate;
    }

    public Long getLoanId() {
        return this.loanId;
    }

    public BigDecimal getAmountOutstanding() {
        return this.amountOutstanding;
    }
    
    public boolean isInstallmentFee() {
        boolean isInstalmentFee = false;
        if (this.chargeTimeType != null) {
            isInstalmentFee = ChargeTimeType.fromInt(this.chargeTimeType.getId().intValue()).isInstalmentFee();
        }
        return isInstalmentFee;
    }
}