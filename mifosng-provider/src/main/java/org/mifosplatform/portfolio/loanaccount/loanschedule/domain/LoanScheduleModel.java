/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.loanschedule.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrency;
import org.mifosplatform.organisation.monetary.domain.Money;
import org.mifosplatform.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.mifosplatform.portfolio.loanaccount.loanschedule.data.LoanSchedulePeriodData;

/**
 * Domain representation of a Loan Schedule (not used for persistence)
 */
public final class LoanScheduleModel {

    private final Collection<LoanScheduleModelPeriod> periods;
    private final ApplicationCurrency applicationCurrency;
    private final int loanTermInDays;
    private final Money totalPrincipalDisbursed;
    private final BigDecimal totalPrincipalExpected;
    private final BigDecimal totalPrincipalPaid;
    private final BigDecimal totalInterestCharged;
    private final BigDecimal totalFeeChargesCharged;
    private final BigDecimal totalPenaltyChargesCharged;
    private final BigDecimal totalRepaymentExpected;
    private final BigDecimal totalOutstanding;

    public static LoanScheduleModel from(final Collection<LoanScheduleModelPeriod> periods, final ApplicationCurrency applicationCurrency,
            final int loanTermInDays, final Money principalDisbursed, final BigDecimal totalPrincipalExpected,
            final BigDecimal totalPrincipalPaid, final BigDecimal totalInterestCharged, final BigDecimal totalFeeChargesCharged,
            final BigDecimal totalPenaltyChargesCharged, final BigDecimal totalRepaymentExpected, final BigDecimal totalOutstanding) {

        return new LoanScheduleModel(periods, applicationCurrency, loanTermInDays, principalDisbursed, totalPrincipalExpected,
                totalPrincipalPaid, totalInterestCharged, totalFeeChargesCharged, totalPenaltyChargesCharged, totalRepaymentExpected,
                totalOutstanding);
    }

    private LoanScheduleModel(final Collection<LoanScheduleModelPeriod> periods, final ApplicationCurrency applicationCurrency,
            final int loanTermInDays, final Money principalDisbursed, final BigDecimal totalPrincipalExpected,
            final BigDecimal totalPrincipalPaid, final BigDecimal totalInterestCharged, final BigDecimal totalFeeChargesCharged,
            final BigDecimal totalPenaltyChargesCharged, final BigDecimal totalRepaymentExpected, final BigDecimal totalOutstanding) {
        this.periods = periods;
        this.applicationCurrency = applicationCurrency;
        this.loanTermInDays = loanTermInDays;
        this.totalPrincipalDisbursed = principalDisbursed;
        this.totalPrincipalExpected = totalPrincipalExpected;
        this.totalPrincipalPaid = totalPrincipalPaid;
        this.totalInterestCharged = totalInterestCharged;
        this.totalFeeChargesCharged = totalFeeChargesCharged;
        this.totalPenaltyChargesCharged = totalPenaltyChargesCharged;
        this.totalRepaymentExpected = totalRepaymentExpected;
        this.totalOutstanding = totalOutstanding;
    }

    public LoanScheduleData toData() {

        final int decimalPlaces = this.totalPrincipalDisbursed.getCurrencyDigitsAfterDecimal();
        final Integer inMultiplesOf = this.totalPrincipalDisbursed.getCurrencyInMultiplesOf();
        final CurrencyData currency = applicationCurrency.toData(decimalPlaces,inMultiplesOf);

        final Collection<LoanSchedulePeriodData> periodsData = new ArrayList<LoanSchedulePeriodData>();
        for (LoanScheduleModelPeriod modelPeriod : this.periods) {
            periodsData.add(modelPeriod.toData());
        }

        BigDecimal totalWaived = null;
        BigDecimal totalWrittenOff = null;
        BigDecimal totalRepayment = null;
        BigDecimal totalPaidInAdvance = null;
        BigDecimal totalPaidLate = null;

        return new LoanScheduleData(currency, periodsData, this.loanTermInDays, this.totalPrincipalDisbursed.getAmount(),
                this.totalPrincipalExpected, this.totalPrincipalPaid, this.totalInterestCharged, this.totalFeeChargesCharged,
                this.totalPenaltyChargesCharged, totalWaived, totalWrittenOff, this.totalRepaymentExpected, totalRepayment,
                totalPaidInAdvance, totalPaidLate, this.totalOutstanding);
    }

    public Collection<LoanScheduleModelPeriod> getPeriods() {
        return this.periods;
    }
}