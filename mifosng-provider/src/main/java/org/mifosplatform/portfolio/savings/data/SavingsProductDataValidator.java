package org.mifosplatform.portfolio.savings.data;

import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.SAVINGS_PRODUCT_REQUEST_DATA_PARAMETERS;
import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.SAVINGS_PRODUCT_RESOURCE_NAME;
import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.annualFeeAmountParamName;
import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.annualFeeOnMonthDayParamName;
import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.currencyCodeParamName;
import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.descriptionParamName;
import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.digitsAfterDecimalParamName;
import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.interestCalculationDaysInYearTypeParamName;
import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.interestCalculationTypeParamName;
import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.interestCompoundingPeriodTypeParamName;
import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.interestPostingPeriodTypeParamName;
import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.lockinPeriodFrequencyParamName;
import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.lockinPeriodFrequencyTypeParamName;
import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.minRequiredOpeningBalanceParamName;
import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.nameParamName;
import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.nominalAnnualInterestRateParamName;
import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.withdrawalFeeAmountParamName;
import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.withdrawalFeeTypeParamName;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.MonthDay;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class SavingsProductDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public SavingsProductDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SAVINGS_PRODUCT_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SAVINGS_PRODUCT_RESOURCE_NAME);
        final JsonElement element = fromApiJsonHelper.parse(json);

        final String name = fromApiJsonHelper.extractStringNamed(nameParamName, element);
        baseDataValidator.reset().parameter(nameParamName).value(name).notBlank().notExceedingLengthOf(100);

        final String description = fromApiJsonHelper.extractStringNamed(descriptionParamName, element);
        baseDataValidator.reset().parameter(descriptionParamName).value(description).notBlank().notExceedingLengthOf(500);

        final String currencyCode = fromApiJsonHelper.extractStringNamed(currencyCodeParamName, element);
        baseDataValidator.reset().parameter(currencyCodeParamName).value(currencyCode).notBlank();

        final Integer digitsAfterDecimal = fromApiJsonHelper.extractIntegerSansLocaleNamed(digitsAfterDecimalParamName, element);
        baseDataValidator.reset().parameter(digitsAfterDecimalParamName).value(digitsAfterDecimal).notNull().inMinMaxRange(0, 6);

        final BigDecimal nominalAnnualInterestRate = fromApiJsonHelper.extractBigDecimalWithLocaleNamed(nominalAnnualInterestRateParamName,
                element);
        baseDataValidator.reset().parameter(nominalAnnualInterestRateParamName).value(nominalAnnualInterestRate).notNull()
                .zeroOrPositiveAmount();

        final Integer interestCompoundingPeriodType = fromApiJsonHelper.extractIntegerSansLocaleNamed(
                interestCompoundingPeriodTypeParamName, element);
        baseDataValidator.reset().parameter(interestCompoundingPeriodTypeParamName).value(interestCompoundingPeriodType).notNull()
                .isOneOfTheseValues(new Object[] { 1, 2, 3, 4, 5, 6, 7, 8 });

        final Integer interestPostingPeriodType = fromApiJsonHelper.extractIntegerSansLocaleNamed(interestPostingPeriodTypeParamName,
                element);
        baseDataValidator.reset().parameter(interestPostingPeriodTypeParamName).value(interestPostingPeriodType).notNull()
                .isOneOfTheseValues(new Object[] { 4, 5, 6, 7 });

        final Integer interestCalculationType = fromApiJsonHelper.extractIntegerSansLocaleNamed(interestCalculationTypeParamName, element);
        baseDataValidator.reset().parameter(interestCalculationTypeParamName).value(interestCalculationType).notNull()
                .isOneOfTheseValues(new Object[] { 1, 2 });

        final Integer interestCalculationDaysInYearType = fromApiJsonHelper.extractIntegerSansLocaleNamed(
                interestCalculationDaysInYearTypeParamName, element);
        baseDataValidator.reset().parameter(interestCalculationDaysInYearTypeParamName).value(interestCalculationDaysInYearType).notNull()
                .isOneOfTheseValues(360, 365);

        if (this.fromApiJsonHelper.parameterExists(minRequiredOpeningBalanceParamName, element)) {
            final BigDecimal minOpeningBalance = fromApiJsonHelper.extractBigDecimalWithLocaleNamed(minRequiredOpeningBalanceParamName,
                    element);
            baseDataValidator.reset().parameter(minRequiredOpeningBalanceParamName).value(minOpeningBalance).positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(lockinPeriodFrequencyParamName, element)) {

            final Integer lockinPeriodFrequency = fromApiJsonHelper.extractIntegerWithLocaleNamed(lockinPeriodFrequencyParamName, element);
            baseDataValidator.reset().parameter(lockinPeriodFrequencyParamName).value(lockinPeriodFrequency).ignoreIfNull()
                    .positiveAmount();

            if (lockinPeriodFrequency != null) {
                final Integer lockinPeriodFrequencyType = fromApiJsonHelper.extractIntegerSansLocaleNamed(
                        lockinPeriodFrequencyTypeParamName, element);
                baseDataValidator.reset().parameter(lockinPeriodFrequencyTypeParamName).value(lockinPeriodFrequencyType).notNull()
                        .inMinMaxRange(1, 3);
            }
        }

        if (this.fromApiJsonHelper.parameterExists(lockinPeriodFrequencyTypeParamName, element)) {
            final Integer lockinPeriodFrequencyType = fromApiJsonHelper.extractIntegerSansLocaleNamed(lockinPeriodFrequencyTypeParamName,
                    element);
            baseDataValidator.reset().parameter(lockinPeriodFrequencyTypeParamName).value(lockinPeriodFrequencyType).inMinMaxRange(1, 3);

            if (lockinPeriodFrequencyType != null) {
                final Integer lockinPeriodFrequency = fromApiJsonHelper.extractIntegerWithLocaleNamed(lockinPeriodFrequencyParamName,
                        element);
                baseDataValidator.reset().parameter(lockinPeriodFrequencyParamName).value(lockinPeriodFrequency).notNull().positiveAmount();
            }
        }

        if (this.fromApiJsonHelper.parameterExists(withdrawalFeeTypeParamName, element)) {
            final Integer withdrawalFeeType = fromApiJsonHelper.extractIntegerSansLocaleNamed(withdrawalFeeTypeParamName, element);
            baseDataValidator.reset().parameter(withdrawalFeeTypeParamName).value(withdrawalFeeType).isOneOfTheseValues(1, 2);

            if (withdrawalFeeType != null) {
                final BigDecimal withdrawalFeeAmount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed(withdrawalFeeAmountParamName,
                        element);
                baseDataValidator.reset().parameter(withdrawalFeeAmountParamName).value(withdrawalFeeAmount).notNull().positiveAmount();
            }
        }

        if (this.fromApiJsonHelper.parameterExists(annualFeeAmountParamName, element)) {
            final BigDecimal annualFeeAmount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed(annualFeeAmountParamName, element);
            baseDataValidator.reset().parameter(annualFeeAmountParamName).value(annualFeeAmount).notNull().positiveAmount();

            if (annualFeeAmount != null) {
                MonthDay monthDayOfAnnualFee = fromApiJsonHelper.extractMonthDayNamed(annualFeeOnMonthDayParamName, element);
                baseDataValidator.reset().parameter(annualFeeOnMonthDayParamName).value(monthDayOfAnnualFee).notNull();
            }
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SAVINGS_PRODUCT_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SAVINGS_PRODUCT_RESOURCE_NAME);
        final JsonElement element = fromApiJsonHelper.parse(json);

        if (this.fromApiJsonHelper.parameterExists(nameParamName, element)) {
            final String name = fromApiJsonHelper.extractStringNamed(nameParamName, element);
            baseDataValidator.reset().parameter(nameParamName).value(name).notBlank().notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists(descriptionParamName, element)) {
            final String description = fromApiJsonHelper.extractStringNamed(descriptionParamName, element);
            baseDataValidator.reset().parameter(descriptionParamName).value(description).notBlank().notExceedingLengthOf(500);
        }

        if (this.fromApiJsonHelper.parameterExists(currencyCodeParamName, element)) {
            final String currencyCode = fromApiJsonHelper.extractStringNamed(currencyCodeParamName, element);
            baseDataValidator.reset().parameter(currencyCodeParamName).value(currencyCode).notBlank();
        }

        if (this.fromApiJsonHelper.parameterExists(digitsAfterDecimalParamName, element)) {
            final Integer digitsAfterDecimal = fromApiJsonHelper.extractIntegerSansLocaleNamed(digitsAfterDecimalParamName, element);
            baseDataValidator.reset().parameter(digitsAfterDecimalParamName).value(digitsAfterDecimal).notNull().inMinMaxRange(0, 6);
        }

        if (this.fromApiJsonHelper.parameterExists(nominalAnnualInterestRateParamName, element)) {
            final BigDecimal interestRate = fromApiJsonHelper.extractBigDecimalWithLocaleNamed(nominalAnnualInterestRateParamName, element);
            baseDataValidator.reset().parameter(nominalAnnualInterestRateParamName).value(interestRate).notNull().zeroOrPositiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(interestCompoundingPeriodTypeParamName, element)) {
            final Integer interestCompoundingPeriodType = fromApiJsonHelper.extractIntegerSansLocaleNamed(
                    interestCompoundingPeriodTypeParamName, element);
            baseDataValidator.reset().parameter(interestCompoundingPeriodTypeParamName).value(interestCompoundingPeriodType).notNull()
                    .inMinMaxRange(1, 8);
        }

        if (this.fromApiJsonHelper.parameterExists(interestCalculationTypeParamName, element)) {
            final Integer interestCalculationType = fromApiJsonHelper.extractIntegerSansLocaleNamed(interestCalculationTypeParamName,
                    element);
            baseDataValidator.reset().parameter(interestCalculationTypeParamName).value(interestCalculationType).notNull()
                    .inMinMaxRange(1, 2);
        }

        if (this.fromApiJsonHelper.parameterExists(interestCalculationDaysInYearTypeParamName, element)) {
            final Integer interestCalculationDaysInYearType = fromApiJsonHelper.extractIntegerSansLocaleNamed(
                    interestCalculationDaysInYearTypeParamName, element);
            baseDataValidator.reset().parameter(interestCalculationDaysInYearTypeParamName).value(interestCalculationDaysInYearType)
                    .notNull().isOneOfTheseValues(360, 365);
        }

        if (this.fromApiJsonHelper.parameterExists(lockinPeriodFrequencyParamName, element)) {

            final Integer lockinPeriodFrequency = fromApiJsonHelper.extractIntegerWithLocaleNamed(lockinPeriodFrequencyParamName, element);
            baseDataValidator.reset().parameter(lockinPeriodFrequencyParamName).value(lockinPeriodFrequency).ignoreIfNull()
                    .positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(lockinPeriodFrequencyTypeParamName, element)) {
            final Integer lockinPeriodFrequencyType = fromApiJsonHelper.extractIntegerSansLocaleNamed(lockinPeriodFrequencyTypeParamName,
                    element);
            baseDataValidator.reset().parameter(lockinPeriodFrequencyTypeParamName).value(lockinPeriodFrequencyType).inMinMaxRange(1, 3);
        }

        if (this.fromApiJsonHelper.parameterExists(withdrawalFeeAmountParamName, element)) {
            final BigDecimal withdrawalFeeAmount = fromApiJsonHelper
                    .extractBigDecimalWithLocaleNamed(withdrawalFeeAmountParamName, element);
            baseDataValidator.reset().parameter(withdrawalFeeAmountParamName).value(withdrawalFeeAmount).ignoreIfNull().positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(withdrawalFeeTypeParamName, element)) {
            final Integer withdrawalFeeType = fromApiJsonHelper.extractIntegerSansLocaleNamed(withdrawalFeeTypeParamName, element);
            baseDataValidator.reset().parameter(withdrawalFeeTypeParamName).value(withdrawalFeeType).ignoreIfNull()
                    .isOneOfTheseValues(1, 2);
        }

        if (this.fromApiJsonHelper.parameterExists(annualFeeAmountParamName, element)) {
            final BigDecimal annualFeeAmount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed(annualFeeAmountParamName, element);
            baseDataValidator.reset().parameter(annualFeeAmountParamName).value(annualFeeAmount).ignoreIfNull().positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists(annualFeeOnMonthDayParamName, element)) {
            MonthDay monthDayOfAnnualFee = fromApiJsonHelper.extractMonthDayNamed(annualFeeOnMonthDayParamName, element);
            baseDataValidator.reset().parameter(annualFeeOnMonthDayParamName).value(monthDayOfAnnualFee).ignoreIfNull();
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}