/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.charge.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.portfolio.loanaccount.data.LoanChargeData;
import org.mifosplatform.portfolio.savings.data.SavingsAccountChargeData;

/**
 * Immutable data object for charge data.
 */
public class ChargeData implements Comparable<ChargeData>, Serializable {

    private final Long id;
    private final String name;
    private final boolean active;
    private final boolean penalty;
    private final CurrencyData currency;
    private final BigDecimal amount;
    private final EnumOptionData chargeTimeType;
    private final EnumOptionData chargeAppliesTo;
    private final EnumOptionData chargeCalculationType;
    private final EnumOptionData chargePaymentMode;

    private final Collection<CurrencyData> currencyOptions;
    private final List<EnumOptionData> chargeCalculationTypeOptions;//
    private final List<EnumOptionData> chargeAppliesToOptions;//
    private final List<EnumOptionData> chargeTimeTypeOptions;//
    private final List<EnumOptionData> chargePaymetModeOptions;//
    
    private final List<EnumOptionData> loanChargeCalculationTypeOptions;
    private final List<EnumOptionData> loanChargeTimeTypeOptions;    
    private final List<EnumOptionData> savingsChargeCalculationTypeOptions;
    private final List<EnumOptionData> savingsChargeTimeTypeOptions;

    public static ChargeData template(final Collection<CurrencyData> currencyOptions,
            final List<EnumOptionData> chargeCalculationTypeOptions, final List<EnumOptionData> chargeAppliesToOptions,
            final List<EnumOptionData> chargeTimeTypeOptions, final List<EnumOptionData> chargePaymentModeOptions,
            final List<EnumOptionData> loansChargeCalculationTypeOptions, final List<EnumOptionData> loansChargeTimeTypeOptions,
            final List<EnumOptionData> savingsChargeCalculationTypeOptions, final List<EnumOptionData> savingsChargeTimeTypeOptions) {

        return new ChargeData(null, null, null, null, null, null, null, null, false, false, currencyOptions, chargeCalculationTypeOptions,
                chargeAppliesToOptions, chargeTimeTypeOptions, chargePaymentModeOptions, loansChargeCalculationTypeOptions,
                loansChargeTimeTypeOptions, savingsChargeCalculationTypeOptions, savingsChargeTimeTypeOptions);
    }

    public static ChargeData withTemplate(final ChargeData charge, final ChargeData template) {
        return new ChargeData(charge.id, charge.name, charge.amount, charge.currency, charge.chargeTimeType, charge.chargeAppliesTo,
                charge.chargeCalculationType, charge.chargePaymentMode, charge.penalty, charge.active, template.currencyOptions,
                template.chargeCalculationTypeOptions, template.chargeAppliesToOptions, template.chargeTimeTypeOptions,
                template.chargePaymetModeOptions, template.loanChargeCalculationTypeOptions, template.loanChargeTimeTypeOptions,
                template.savingsChargeCalculationTypeOptions, template.savingsChargeTimeTypeOptions);
    }

    public static ChargeData instance(final Long id, final String name, final BigDecimal amount, final CurrencyData currency,
            final EnumOptionData chargeTimeType, final EnumOptionData chargeAppliesTo, final EnumOptionData chargeCalculationType,
            final EnumOptionData chargePaymentMode, final boolean penalty, final boolean active) {
        
        final Collection<CurrencyData> currencyOptions = null;
        final List<EnumOptionData> chargeCalculationTypeOptions = null;
        final List<EnumOptionData> chargeAppliesToOptions = null;
        final List<EnumOptionData> chargeTimeTypeOptions = null;
        final List<EnumOptionData> chargePaymentModeOptions = null;
        final List<EnumOptionData> loansChargeCalculationTypeOptions = null;
        final List<EnumOptionData> loansChargeTimeTypeOptions = null;
        final List<EnumOptionData> savingsChargeCalculationTypeOptions = null;
        final List<EnumOptionData> savingsChargeTimeTypeOptions = null;
        
        return new ChargeData(id, name, amount, currency, chargeTimeType, chargeAppliesTo, chargeCalculationType, chargePaymentMode,
                penalty, active, currencyOptions, chargeCalculationTypeOptions, chargeAppliesToOptions, chargeTimeTypeOptions,
                chargePaymentModeOptions, loansChargeCalculationTypeOptions, loansChargeTimeTypeOptions,
                savingsChargeCalculationTypeOptions, savingsChargeTimeTypeOptions);
    }

    public static ChargeData lookup(final Long id, final String name, final boolean isPenalty) {
        final BigDecimal amount = null;
        final CurrencyData currency = null;
        final EnumOptionData chargeTimeType = null;
        final EnumOptionData chargeAppliesTo = null;
        final EnumOptionData chargeCalculationType = null;
        final EnumOptionData chargePaymentMode = null;
        final Boolean penalty = isPenalty;
        final Boolean active = false;
        final Collection<CurrencyData> currencyOptions = null;
        final List<EnumOptionData> chargeCalculationTypeOptions = null;
        final List<EnumOptionData> chargeAppliesToOptions = null;
        final List<EnumOptionData> chargeTimeTypeOptions = null;
        final List<EnumOptionData> chargePaymentModeOptions = null;
        final List<EnumOptionData> loansChargeCalculationTypeOptions = null;
        final List<EnumOptionData> loansChargeTimeTypeOptions = null;
        final List<EnumOptionData> savingsChargeCalculationTypeOptions = null;
        final List<EnumOptionData> savingsChargeTimeTypeOptions = null;
        
        return new ChargeData(id, name, amount, currency, chargeTimeType, chargeAppliesTo, chargeCalculationType, chargePaymentMode,
                penalty, active, currencyOptions, chargeCalculationTypeOptions, chargeAppliesToOptions, chargeTimeTypeOptions,
                chargePaymentModeOptions, loansChargeCalculationTypeOptions, loansChargeTimeTypeOptions,
                savingsChargeCalculationTypeOptions, savingsChargeTimeTypeOptions);
    }

    private ChargeData(final Long id, final String name, final BigDecimal amount, final CurrencyData currency,
            final EnumOptionData chargeTimeType, final EnumOptionData chargeAppliesTo, final EnumOptionData chargeCalculationType,
            final EnumOptionData chargePaymentMode, final boolean penalty, final boolean active,
            final Collection<CurrencyData> currencyOptions, final List<EnumOptionData> chargeCalculationTypeOptions,
            final List<EnumOptionData> chargeAppliesToOptions, final List<EnumOptionData> chargeTimeTypeOptions,
            final List<EnumOptionData> chargePaymentModeOptions, final List<EnumOptionData> loansChargeCalculationTypeOptions,
            final List<EnumOptionData> loansChargeTimeTypeOptions, final List<EnumOptionData> savingsChargeCalculationTypeOptions,
            final List<EnumOptionData> savingsChargeTimeTypeOptions) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.currency = currency;
        this.chargeTimeType = chargeTimeType;
        this.chargeAppliesTo = chargeAppliesTo;
        this.chargeCalculationType = chargeCalculationType;
        this.chargePaymentMode = chargePaymentMode;
        this.penalty = penalty;
        this.active = active;
        this.currencyOptions = currencyOptions;
        this.chargeCalculationTypeOptions = chargeCalculationTypeOptions;
        this.chargeAppliesToOptions = chargeAppliesToOptions;
        this.chargeTimeTypeOptions = chargeTimeTypeOptions;
        this.chargePaymetModeOptions = chargePaymentModeOptions;
        this.savingsChargeCalculationTypeOptions = savingsChargeCalculationTypeOptions;
        this.savingsChargeTimeTypeOptions = savingsChargeTimeTypeOptions;
        this.loanChargeCalculationTypeOptions = loansChargeCalculationTypeOptions;
        this.loanChargeTimeTypeOptions = loansChargeTimeTypeOptions;
    }

    @Override
    public boolean equals(final Object obj) {
        final ChargeData chargeData = (ChargeData) obj;
        return this.id.equals(chargeData.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public int compareTo(final ChargeData obj) {
        if (obj == null) { return -1; }

        return obj.id.compareTo(this.id);
    }

    public LoanChargeData toLoanChargeData() {

        BigDecimal percentage = null;
        if (this.chargeCalculationType.getId() == 2) {
            percentage = this.amount;
        }

        return LoanChargeData.newLoanChargeDetails(this.id, this.name, this.currency, this.amount, percentage, this.chargeTimeType,
                this.chargeCalculationType, this.penalty, this.chargePaymentMode);
    }

    public SavingsAccountChargeData toSavingsAccountChargeData() {

        final Long savingsChargeId = null;
        final BigDecimal amountPaid = BigDecimal.ZERO;
        final BigDecimal amountWaived = BigDecimal.ZERO;
        final BigDecimal amountWrittenOff = BigDecimal.ZERO;
        final BigDecimal amountOutstanding = BigDecimal.ZERO;
        final BigDecimal percentage = BigDecimal.ZERO;
        final BigDecimal amountPercentageAppliedTo = BigDecimal.ZERO;
        final Collection<ChargeData> chargeOptions = null;
        final LocalDate dueAsOfDate = null;

        return SavingsAccountChargeData.instance(savingsChargeId, this.id, this.name, this.currency, this.amount, amountPaid, amountWaived,
                amountWrittenOff, amountOutstanding, this.chargeTimeType, dueAsOfDate, this.chargeCalculationType, percentage,
                amountPercentageAppliedTo, chargeOptions, this.penalty);
    }
}