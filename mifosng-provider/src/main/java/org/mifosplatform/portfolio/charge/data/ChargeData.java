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

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.portfolio.loanaccount.data.LoanChargeData;

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

    private final Collection<CurrencyData> currencyOptions;
    private final List<EnumOptionData> chargeCalculationTypeOptions;
    private final List<EnumOptionData> chargeAppliesToOptions;
    private final List<EnumOptionData> chargeTimeTypeOptions;

    public static ChargeData template(final Collection<CurrencyData> currencyOptions,
            final List<EnumOptionData> chargeCalculationTypeOptions, final List<EnumOptionData> chargeAppliesToOptions,
            final List<EnumOptionData> chargeTimeTypeOptions) {
        return new ChargeData(null, null, null, null, null, null, null, false, false, currencyOptions, chargeCalculationTypeOptions,
                chargeAppliesToOptions, chargeTimeTypeOptions);
    }

    public static ChargeData withTemplate(final ChargeData charge, final ChargeData template) {
        return new ChargeData(charge.id, charge.name, charge.amount, charge.currency, charge.chargeTimeType, charge.chargeAppliesTo,
                charge.chargeCalculationType, charge.penalty, charge.active, template.currencyOptions,
                template.chargeCalculationTypeOptions, template.chargeAppliesToOptions, template.chargeTimeTypeOptions);
    }

    public static ChargeData instance(final Long id, final String name, final BigDecimal amount, final CurrencyData currency,
            final EnumOptionData chargeTimeType, final EnumOptionData chargeAppliesTo, final EnumOptionData chargeCalculationType,
            final boolean penalty, final boolean active) {
        return new ChargeData(id, name, amount, currency, chargeTimeType, chargeAppliesTo, chargeCalculationType, penalty, active, null,
                null, null, null);
    }

    public static ChargeData lookup(final Long id, final String name, final boolean isPenalty) {
        BigDecimal amount = null;
        CurrencyData currency = null;
        EnumOptionData chargeTimeType = null;
        EnumOptionData chargeAppliesTo = null;
        EnumOptionData chargeCalculationType = null;
        Boolean penalty = isPenalty;
        Boolean active = false;
        Collection<CurrencyData> currencyOptions = null;
        List<EnumOptionData> chargeCalculationTypeOptions = null;
        List<EnumOptionData> chargeAppliesToOptions = null;
        List<EnumOptionData> chargeTimeTypeOptions = null;
        return new ChargeData(id, name, amount, currency, chargeTimeType, chargeAppliesTo, chargeCalculationType, penalty, active,
                currencyOptions, chargeCalculationTypeOptions, chargeAppliesToOptions, chargeTimeTypeOptions);
    }

    private ChargeData(final Long id, final String name, final BigDecimal amount, final CurrencyData currency,
            final EnumOptionData chargeTimeType, final EnumOptionData chargeAppliesTo, final EnumOptionData chargeCalculationType,
            final boolean penalty, final boolean active, final Collection<CurrencyData> currencyOptions,
            final List<EnumOptionData> chargeCalculationTypeOptions, final List<EnumOptionData> chargeAppliesToOptions,
            final List<EnumOptionData> chargeTimeTypeOptions) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.currency = currency;
        this.chargeTimeType = chargeTimeType;
        this.chargeAppliesTo = chargeAppliesTo;
        this.chargeCalculationType = chargeCalculationType;
        this.penalty = penalty;
        this.active = active;
        this.currencyOptions = currencyOptions;
        this.chargeCalculationTypeOptions = chargeCalculationTypeOptions;
        this.chargeAppliesToOptions = chargeAppliesToOptions;
        this.chargeTimeTypeOptions = chargeTimeTypeOptions;
    }

    @Override
    public boolean equals(final Object obj) {
        ChargeData chargeData = (ChargeData) obj;
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
                this.chargeCalculationType, this.penalty);
    }
}