/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.joda.time.MonthDay;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;

@SuppressWarnings("unused")
public class ClientRecurringChargeData {

    private final Long id;
    private final Long clientId;
    private final Long chargeId;
    private final String name;
    private final boolean active;
    private final boolean penalty;
    private final CurrencyData currencydata;
    private final BigDecimal amount;
    private final LocalDate chargeDueDate;
    private final EnumOptionData chargeTimeType;
    private final EnumOptionData chargeAppliesTo;
    private final EnumOptionData chargeCalculationType;
    private final EnumOptionData chargePaymentMode;
    private final MonthDay feeOnMonthDay;
    private final Integer feeInterval;
    private final BigDecimal minCap;
    private final BigDecimal maxCap;
    private final EnumOptionData feeFrequencyType;
    private final LocalDate inactivationDate;

    public static ClientRecurringChargeData instance(Long id, Long clientId, Long chargeId, String name, boolean active, boolean penalty,CurrencyData currencydata,
      BigDecimal amount, LocalDate chargeDueDate, EnumOptionData chargeTimeType,
            EnumOptionData chargeCalculationType, EnumOptionData chargeAppliesTo,EnumOptionData chargePaymentMode, MonthDay feeOnMonthDay, Integer feeInterval,
            BigDecimal minCap,BigDecimal maxCap,EnumOptionData feeFrequencyType,LocalDate inactivationDate) {

        return new ClientRecurringChargeData(id, clientId, chargeId, name, active, penalty, currencydata, amount, chargeDueDate,
                chargeTimeType, chargeCalculationType, chargeAppliesTo,chargePaymentMode, feeOnMonthDay, feeInterval, minCap, minCap, feeFrequencyType,inactivationDate);
    }

    private ClientRecurringChargeData(Long id, Long clientId, Long chargeId, String name, boolean active, boolean penalty,
            CurrencyData currencydata, BigDecimal amount, LocalDate chargeDueDate, EnumOptionData chargeTimeType,
            EnumOptionData chargeCalculationType, EnumOptionData chargeAppliesTo,EnumOptionData chargePaymentMode, MonthDay feeOnMonthDay, Integer feeInterval,
            BigDecimal minCap, BigDecimal maxCap, EnumOptionData feeFrequencyType,LocalDate inactivationDate) {
        this.id = id;
        this.clientId = clientId;
        this.chargeId = chargeId;
        this.name = name;
        this.amount = amount;
        this.chargePaymentMode=chargePaymentMode;
        this.currencydata = currencydata;
        this.chargeDueDate = chargeDueDate;
        this.penalty = penalty;
        this.active = active;
        this.feeOnMonthDay = feeOnMonthDay;
        this.chargeAppliesTo = chargeAppliesTo;
        this.chargeTimeType = chargeTimeType;
        this.chargeCalculationType = chargeCalculationType;
        this.feeFrequencyType = feeFrequencyType;
        this.feeInterval = feeInterval;
        this.minCap = minCap;
        this.maxCap = maxCap;
        this.inactivationDate=inactivationDate;

    }

}
