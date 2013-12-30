/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.interestratechart.domain;

import static org.mifosplatform.portfolio.interestratechart.InterestRateChartApiConstants.amountRangeFromParamName;
import static org.mifosplatform.portfolio.interestratechart.InterestRateChartApiConstants.amountRangeToParamName;
import static org.mifosplatform.portfolio.interestratechart.InterestRateChartApiConstants.annualInterestRateParamName;
import static org.mifosplatform.portfolio.interestratechart.InterestRateChartApiConstants.descriptionParamName;
import static org.mifosplatform.portfolio.interestratechart.InterestRateChartApiConstants.fromPeriodParamName;
import static org.mifosplatform.portfolio.interestratechart.InterestRateChartApiConstants.periodTypeParamName;
import static org.mifosplatform.portfolio.interestratechart.InterestRateChartApiConstants.toPeriodParamName;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;

@Embeddable
public class InterestRateChartSlabFields {

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "period_type_enum", nullable = false)
    private Integer periodType;

    @Column(name = "from_period", nullable = false)
    private Integer fromPeriod;

    @Column(name = "to_period", nullable = true)
    private Integer toPeriod;

    @Column(name = "amount_range_from", scale = 6, precision = 19)
    private BigDecimal amountRangeFrom;

    @Column(name = "amount_range_to", scale = 6, precision = 19)
    private BigDecimal amountRangeTo;

    @Column(name = "annual_interest_rate", scale = 6, precision = 19, nullable = false)
    private BigDecimal annualInterestRate;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    protected InterestRateChartSlabFields() {
        //
    }

    public static InterestRateChartSlabFields createNew(String description, Integer periodType, Integer fromPeriod, Integer toPeriod,
            BigDecimal amountRangeFrom, BigDecimal amountRangeTo, BigDecimal annualInterestRate, String currencyCode) {
        return new InterestRateChartSlabFields(description, periodType, fromPeriod, toPeriod, amountRangeFrom, amountRangeTo,
                annualInterestRate, currencyCode);
    }

    private InterestRateChartSlabFields(String description, Integer periodType, Integer fromPeriod, Integer toPeriod,
            BigDecimal amountRangeFrom, BigDecimal amountRangeTo, BigDecimal annualInterestRate, String currencyCode) {
        this.description = description;
        this.periodType = periodType;
        this.fromPeriod = fromPeriod;
        this.toPeriod = toPeriod;
        this.amountRangeFrom = amountRangeFrom;
        this.amountRangeTo = amountRangeTo;
        this.annualInterestRate = annualInterestRate;
        this.currencyCode = currencyCode;
    }

    public void update(JsonCommand command, final Map<String, Object> actualChanges, final DataValidatorBuilder baseDataValidator) {

        if (command.isChangeInStringParameterNamed(descriptionParamName, this.description)) {
            final String newValue = command.stringValueOfParameterNamed(descriptionParamName);
            actualChanges.put(descriptionParamName, newValue);
            this.description = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(periodTypeParamName, periodType)) {
            final Integer newValue = command.integerValueOfParameterNamed(periodTypeParamName);
            actualChanges.put(periodTypeParamName, newValue);
            this.periodType = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(fromPeriodParamName, fromPeriod)) {
            final Integer newValue = command.integerValueOfParameterNamed(fromPeriodParamName);
            actualChanges.put(fromPeriodParamName, newValue);
            this.fromPeriod = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(toPeriodParamName, toPeriod)) {
            final Integer newValue = command.integerValueOfParameterNamed(toPeriodParamName);
            actualChanges.put(toPeriodParamName, newValue);
            this.toPeriod = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(amountRangeFromParamName, amountRangeFrom)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(amountRangeFromParamName);
            actualChanges.put(amountRangeFromParamName, newValue);
            this.amountRangeFrom = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(amountRangeToParamName, amountRangeTo)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(amountRangeToParamName);
            actualChanges.put(amountRangeToParamName, newValue);
            this.amountRangeTo = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(annualInterestRateParamName, annualInterestRate)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(annualInterestRateParamName);
            actualChanges.put(annualInterestRateParamName, newValue);
            this.annualInterestRate = newValue;
        }
        
        this.validateChartSlabPlatformRules(command, baseDataValidator);
    }
    
    public void validateChartSlabPlatformRules(JsonCommand chartSlabsCommand, DataValidatorBuilder baseDataValidator) {
        if (this.isFromPeriodGreaterThanToPeriod()) {
            final Integer fromPeriod = chartSlabsCommand.integerValueOfParameterNamed(fromPeriodParamName);
            baseDataValidator.parameter(fromPeriodParamName).value(fromPeriod).failWithCode("from.period.is.greater.than.to.period");
        }

        if (this.isAmountRangeFromGreaterThanTo()) {
            final BigDecimal amountRangeFrom = chartSlabsCommand.bigDecimalValueOfParameterNamed(amountRangeFromParamName);
            baseDataValidator.parameter(amountRangeFromParamName).value(amountRangeFrom)
                    .failWithCode("amount.range.from.is.greater.than.amount.range.to");
        }
    }

    public boolean isFromPeriodGreaterThanToPeriod() {
        boolean isGreater = false;
        if (this.toPeriod != null && fromPeriod.compareTo(this.toPeriod) > 1) {
            isGreater = true;
        }
        return isGreater;
    }

    public boolean isAmountRangeFromGreaterThanTo() {
        boolean isGreater = false;
        if (this.amountRangeFrom != null && this.amountRangeTo != null && this.amountRangeFrom.compareTo(this.amountRangeTo) > 1) {
            isGreater = true;
        }
        return isGreater;
    }
    
    public Integer periodType() {
        return this.periodType;
    }

    public Integer fromPeriod() {
        return this.fromPeriod;
    }

    public Integer toPeriod() {
        return this.toPeriod;
    }

    public boolean isPeriodOverlapping(InterestRateChartSlabFields that) {
        return this.fromPeriod <= that.toPeriod && that.fromPeriod <= this.toPeriod;
    }
}