/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanproduct.serialization;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.accounting.common.AccountingConstants.LOAN_PRODUCT_ACCOUNTING_PARAMS;
import org.mifosplatform.accounting.common.AccountingRuleType;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.loanproduct.domain.LoanProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public final class LoanProductDataValidator {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("locale", "dateFormat", "name", "description",
            "fundId", "currencyCode", "digitsAfterDecimal", "inMultiplesOf", "principal", "minPrincipal", "maxPrincipal", "repaymentEvery",
            "numberOfRepayments", "minNumberOfRepayments", "maxNumberOfRepayments", "repaymentFrequencyType", "interestRatePerPeriod",
            "minInterestRatePerPeriod", "maxInterestRatePerPeriod", "interestRateFrequencyType", "amortizationType", "interestType",
            "interestCalculationPeriodType", "inArrearsTolerance", "transactionProcessingStrategyId", "graceOnPrincipalPayment",
            "graceOnInterestPayment", "graceOnInterestCharged", "charges", "accountingRule", "includeInBorrowerCycle", "startDate",
            "closeDate", "externalId", LOAN_PRODUCT_ACCOUNTING_PARAMS.FEES_RECEIVABLE.getValue(),
            LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(),
            LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue(), LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue(),
            LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_RECEIVABLE.getValue(), LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue(),
            LOAN_PRODUCT_ACCOUNTING_PARAMS.OVERPAYMENT.getValue(), LOAN_PRODUCT_ACCOUNTING_PARAMS.TRANSFERS_SUSPENSE.getValue(),
            LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue(), LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTIES_RECEIVABLE.getValue(),
            LOAN_PRODUCT_ACCOUNTING_PARAMS.PAYMENT_CHANNEL_FUND_SOURCE_MAPPING.getValue(),
            LOAN_PRODUCT_ACCOUNTING_PARAMS.FEE_INCOME_ACCOUNT_MAPPING.getValue(),
            LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTY_INCOME_ACCOUNT_MAPPING.getValue()));

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public LoanProductDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loanproduct");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String name = this.fromApiJsonHelper.extractStringNamed("name", element);
        baseDataValidator.reset().parameter("name").value(name).notBlank().notExceedingLengthOf(100);

        final String description = this.fromApiJsonHelper.extractStringNamed("description", element);
        baseDataValidator.reset().parameter("description").value(description).notExceedingLengthOf(500);

        if (this.fromApiJsonHelper.parameterExists("fundId", element)) {
            final Long fundId = this.fromApiJsonHelper.extractLongNamed("fundId", element);
            baseDataValidator.reset().parameter("fundId").value(fundId).ignoreIfNull().integerGreaterThanZero();
        }

        final Boolean includeInBorrowerCycle = this.fromApiJsonHelper.extractBooleanNamed("includeInBorrowerCycle", element);
        baseDataValidator.reset().parameter("includeInBorrowerCycle").value(includeInBorrowerCycle).ignoreIfNull()
                .validateForBooleanValue();

        // terms
        final String currencyCode = this.fromApiJsonHelper.extractStringNamed("currencyCode", element);
        baseDataValidator.reset().parameter("currencyCode").value(currencyCode).notBlank().notExceedingLengthOf(3);

        final Integer digitsAfterDecimal = this.fromApiJsonHelper.extractIntegerNamed("digitsAfterDecimal", element, Locale.getDefault());
        baseDataValidator.reset().parameter("digitsAfterDecimal").value(digitsAfterDecimal).notNull().inMinMaxRange(0, 6);

        final Integer inMultiplesOf = this.fromApiJsonHelper.extractIntegerNamed("inMultiplesOf", element, Locale.getDefault());
        baseDataValidator.reset().parameter("inMultiplesOf").value(inMultiplesOf).ignoreIfNull().integerZeroOrGreater();

        final BigDecimal principal = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("principal", element);
        baseDataValidator.reset().parameter("principal").value(principal).notNull().positiveAmount();

        final String minPrincipalParameterName = "minPrincipal";
        BigDecimal minPrincipalAmount = null;
        if (this.fromApiJsonHelper.parameterExists(minPrincipalParameterName, element)) {
            minPrincipalAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(minPrincipalParameterName, element);
            baseDataValidator.reset().parameter(minPrincipalParameterName).value(minPrincipalAmount).ignoreIfNull().positiveAmount();
        }

        final String maxPrincipalParameterName = "maxPrincipal";
        BigDecimal maxPrincipalAmount = null;
        if (this.fromApiJsonHelper.parameterExists(maxPrincipalParameterName, element)) {
            maxPrincipalAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(maxPrincipalParameterName, element);
            baseDataValidator.reset().parameter(maxPrincipalParameterName).value(maxPrincipalAmount).ignoreIfNull().positiveAmount();
        }

        if (maxPrincipalAmount != null && maxPrincipalAmount.compareTo(BigDecimal.ZERO) != -1) {
            if (minPrincipalAmount != null && minPrincipalAmount.compareTo(BigDecimal.ZERO) != -1) {
                baseDataValidator.reset().parameter(maxPrincipalParameterName).value(maxPrincipalAmount).notLessThanMin(minPrincipalAmount);
                if (minPrincipalAmount.compareTo(maxPrincipalAmount) <= 0) {
                    baseDataValidator.reset().parameter("principal").value(principal)
                            .inMinAndMaxAmountRange(minPrincipalAmount, maxPrincipalAmount);
                }
            } else {
                baseDataValidator.reset().parameter("principal").value(principal).notGreaterThanMax(maxPrincipalAmount);
            }
        } else if (minPrincipalAmount != null && minPrincipalAmount.compareTo(BigDecimal.ZERO) != -1) {
            baseDataValidator.reset().parameter("principal").value(principal).notLessThanMin(minPrincipalAmount);
        }

        final Integer numberOfRepayments = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("numberOfRepayments", element);
        baseDataValidator.reset().parameter("numberOfRepayments").value(numberOfRepayments).notNull().integerGreaterThanZero();

        final String minNumberOfRepaymentsParameterName = "minNumberOfRepayments";
        Integer minNumberOfRepayments = null;
        if (this.fromApiJsonHelper.parameterExists(minNumberOfRepaymentsParameterName, element)) {
            minNumberOfRepayments = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(minNumberOfRepaymentsParameterName, element);
            baseDataValidator.reset().parameter(minNumberOfRepaymentsParameterName).value(minNumberOfRepayments).ignoreIfNull()
                    .integerGreaterThanZero();
        }

        final String maxNumberOfRepaymentsParameterName = "maxNumberOfRepayments";
        Integer maxNumberOfRepayments = null;
        if (this.fromApiJsonHelper.parameterExists(maxNumberOfRepaymentsParameterName, element)) {
            maxNumberOfRepayments = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(maxNumberOfRepaymentsParameterName, element);
            baseDataValidator.reset().parameter(maxNumberOfRepaymentsParameterName).value(maxNumberOfRepayments).ignoreIfNull()
                    .integerGreaterThanZero();
        }

        if (maxNumberOfRepayments != null && maxNumberOfRepayments.compareTo(0) == 1) {
            if (minNumberOfRepayments != null && minNumberOfRepayments.compareTo(0) == 1) {
                baseDataValidator.reset().parameter(maxNumberOfRepaymentsParameterName).value(maxNumberOfRepayments)
                        .notLessThanMin(minNumberOfRepayments);
                if (minNumberOfRepayments.compareTo(maxNumberOfRepayments) <= 0) {
                    baseDataValidator.reset().parameter("numberOfRepayments").value(numberOfRepayments)
                            .inMinMaxRange(minNumberOfRepayments, maxNumberOfRepayments);
                }
            } else {
                baseDataValidator.reset().parameter("numberOfRepayments").value(numberOfRepayments)
                        .notGreaterThanMax(maxNumberOfRepayments);
            }
        } else if (minNumberOfRepayments != null && minNumberOfRepayments.compareTo(0) == 1) {
            baseDataValidator.reset().parameter("numberOfRepayments").value(numberOfRepayments).notLessThanMin(minNumberOfRepayments);
        }

        final Integer repaymentEvery = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("repaymentEvery", element);
        baseDataValidator.reset().parameter("repaymentEvery").value(repaymentEvery).notNull().integerGreaterThanZero();

        final Integer repaymentFrequencyType = this.fromApiJsonHelper.extractIntegerNamed("repaymentFrequencyType", element,
                Locale.getDefault());
        baseDataValidator.reset().parameter("repaymentFrequencyType").value(repaymentFrequencyType).notNull().inMinMaxRange(0, 3);

        final BigDecimal interestRatePerPeriod = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("interestRatePerPeriod", element);
        baseDataValidator.reset().parameter("interestRatePerPeriod").value(interestRatePerPeriod).notNull().zeroOrPositiveAmount();

        final String minInterestRatePerPeriodParameterName = "minInterestRatePerPeriod";
        BigDecimal minInterestRatePerPeriod = null;
        if (this.fromApiJsonHelper.parameterExists(minInterestRatePerPeriodParameterName, element)) {
            minInterestRatePerPeriod = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(minInterestRatePerPeriodParameterName,
                    element);
            baseDataValidator.reset().parameter(minInterestRatePerPeriodParameterName).value(minInterestRatePerPeriod).ignoreIfNull()
                    .zeroOrPositiveAmount();
        }

        final String maxInterestRatePerPeriodParameterName = "maxInterestRatePerPeriod";
        BigDecimal maxInterestRatePerPeriod = null;
        if (this.fromApiJsonHelper.parameterExists(maxInterestRatePerPeriodParameterName, element)) {
            maxInterestRatePerPeriod = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(maxInterestRatePerPeriodParameterName,
                    element);
            baseDataValidator.reset().parameter(maxInterestRatePerPeriodParameterName).value(maxInterestRatePerPeriod).ignoreIfNull()
                    .zeroOrPositiveAmount();
        }

        if (maxInterestRatePerPeriod != null && maxInterestRatePerPeriod.compareTo(BigDecimal.ZERO) != -1) {
            if (minInterestRatePerPeriod != null && minInterestRatePerPeriod.compareTo(BigDecimal.ZERO) != -1) {
                baseDataValidator.reset().parameter(maxInterestRatePerPeriodParameterName).value(maxInterestRatePerPeriod)
                        .notLessThanMin(minInterestRatePerPeriod);
                if (minInterestRatePerPeriod.compareTo(maxInterestRatePerPeriod) <= 0) {
                    baseDataValidator.reset().parameter("interestRatePerPeriod").value(interestRatePerPeriod)
                            .inMinAndMaxAmountRange(minInterestRatePerPeriod, maxInterestRatePerPeriod);
                }
            } else {
                baseDataValidator.reset().parameter("interestRatePerPeriod").value(interestRatePerPeriod)
                        .notGreaterThanMax(maxInterestRatePerPeriod);
            }
        } else if (minInterestRatePerPeriod != null && minInterestRatePerPeriod.compareTo(BigDecimal.ZERO) != -1) {
            baseDataValidator.reset().parameter("interestRatePerPeriod").value(interestRatePerPeriod)
                    .notLessThanMin(minInterestRatePerPeriod);
        }

        final Integer interestRateFrequencyType = this.fromApiJsonHelper.extractIntegerNamed("interestRateFrequencyType", element,
                Locale.getDefault());
        baseDataValidator.reset().parameter("interestRateFrequencyType").value(interestRateFrequencyType).notNull().inMinMaxRange(0, 3);

        // settings
        final Integer amortizationType = this.fromApiJsonHelper.extractIntegerNamed("amortizationType", element, Locale.getDefault());
        baseDataValidator.reset().parameter("amortizationType").value(amortizationType).notNull().inMinMaxRange(0, 1);

        final Integer interestType = this.fromApiJsonHelper.extractIntegerNamed("interestType", element, Locale.getDefault());
        baseDataValidator.reset().parameter("interestType").value(interestType).notNull().inMinMaxRange(0, 1);

        final Integer interestCalculationPeriodType = this.fromApiJsonHelper.extractIntegerNamed("interestCalculationPeriodType", element,
                Locale.getDefault());
        baseDataValidator.reset().parameter("interestCalculationPeriodType").value(interestCalculationPeriodType).notNull()
                .inMinMaxRange(0, 1);

        final BigDecimal inArrearsTolerance = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("inArrearsTolerance", element);
        baseDataValidator.reset().parameter("inArrearsTolerance").value(inArrearsTolerance).ignoreIfNull().zeroOrPositiveAmount();

        final Long transactionProcessingStrategyId = this.fromApiJsonHelper.extractLongNamed("transactionProcessingStrategyId", element);
        baseDataValidator.reset().parameter("transactionProcessingStrategyId").value(transactionProcessingStrategyId).notNull()
                .integerGreaterThanZero();

        // grace validation
        final Integer graceOnPrincipalPayment = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("graceOnPrincipalPayment", element);
        baseDataValidator.reset().parameter("graceOnPrincipalPayment").value(graceOnPrincipalPayment).zeroOrPositiveAmount();

        final Integer graceOnInterestPayment = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("graceOnInterestPayment", element);
        baseDataValidator.reset().parameter("graceOnInterestPayment").value(graceOnInterestPayment).zeroOrPositiveAmount();

        final Integer graceOnInterestCharged = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("graceOnInterestCharged", element);
        baseDataValidator.reset().parameter("graceOnInterestCharged").value(graceOnInterestCharged).zeroOrPositiveAmount();

        // accounting related data validation
        final Integer accountingRuleType = this.fromApiJsonHelper.extractIntegerNamed("accountingRule", element, Locale.getDefault());
        baseDataValidator.reset().parameter("accountingRule").value(accountingRuleType).notNull().inMinMaxRange(1, 3);

        if (isCashBasedAccounting(accountingRuleType) || isAccrualBasedAccounting(accountingRuleType)) {

            final Long fundAccountId = this.fromApiJsonHelper.extractLongNamed(LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(),
                    element);
            baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue()).value(fundAccountId).notNull()
                    .integerGreaterThanZero();

            final Long loanPortfolioAccountId = this.fromApiJsonHelper.extractLongNamed(
                    LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue(), element);
            baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue()).value(loanPortfolioAccountId)
                    .notNull().integerGreaterThanZero();

            final Long transfersInSuspenseAccountId = this.fromApiJsonHelper.extractLongNamed(
                    LOAN_PRODUCT_ACCOUNTING_PARAMS.TRANSFERS_SUSPENSE.getValue(), element);
            baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.TRANSFERS_SUSPENSE.getValue())
                    .value(transfersInSuspenseAccountId).notNull().integerGreaterThanZero();

            final Long incomeFromInterestId = this.fromApiJsonHelper.extractLongNamed(
                    LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue(), element);
            baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue()).value(incomeFromInterestId)
                    .notNull().integerGreaterThanZero();

            final Long incomeFromFeeId = this.fromApiJsonHelper.extractLongNamed(
                    LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(), element);
            baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue()).value(incomeFromFeeId)
                    .notNull().integerGreaterThanZero();

            final Long incomeFromPenaltyId = this.fromApiJsonHelper.extractLongNamed(
                    LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue(), element);
            baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue()).value(incomeFromPenaltyId)
                    .notNull().integerGreaterThanZero();

            final Long writeOffAccountId = this.fromApiJsonHelper.extractLongNamed(
                    LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue(), element);
            baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue()).value(writeOffAccountId)
                    .notNull().integerGreaterThanZero();

            final Long overpaymentAccountId = this.fromApiJsonHelper.extractLongNamed(
                    LOAN_PRODUCT_ACCOUNTING_PARAMS.OVERPAYMENT.getValue(), element);
            baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.OVERPAYMENT.getValue()).value(overpaymentAccountId)
                    .notNull().integerGreaterThanZero();

            validatePaymentChannelFundSourceMappings(baseDataValidator, element);
            validateChargeToIncomeAccountMappings(baseDataValidator, element);

        }

        if (isAccrualBasedAccounting(accountingRuleType)) {

            final Long receivableInterestAccountId = this.fromApiJsonHelper.extractLongNamed(
                    LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_RECEIVABLE.getValue(), element);
            baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_RECEIVABLE.getValue())
                    .value(receivableInterestAccountId).notNull().integerGreaterThanZero();

            final Long receivableFeeAccountId = this.fromApiJsonHelper.extractLongNamed(
                    LOAN_PRODUCT_ACCOUNTING_PARAMS.FEES_RECEIVABLE.getValue(), element);
            baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.FEES_RECEIVABLE.getValue()).value(receivableFeeAccountId)
                    .notNull().integerGreaterThanZero();

            final Long receivablePenaltyAccountId = this.fromApiJsonHelper.extractLongNamed(
                    LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTIES_RECEIVABLE.getValue(), element);
            baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTIES_RECEIVABLE.getValue())
                    .value(receivablePenaltyAccountId).notNull().integerGreaterThanZero();
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json, final LoanProduct loanProduct) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loanproduct");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        if (this.fromApiJsonHelper.parameterExists("name", element)) {
            final String name = this.fromApiJsonHelper.extractStringNamed("name", element);
            baseDataValidator.reset().parameter("name").value(name).notBlank().notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists("description", element)) {
            final String description = this.fromApiJsonHelper.extractStringNamed("description", element);
            baseDataValidator.reset().parameter("description").value(description).notExceedingLengthOf(500);
        }

        if (this.fromApiJsonHelper.parameterExists("fundId", element)) {
            final Long fundId = this.fromApiJsonHelper.extractLongNamed("fundId", element);
            baseDataValidator.reset().parameter("fundId").value(fundId).ignoreIfNull().integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists("includeInBorrowerCycle", element)) {
            final Boolean includeInBorrowerCycle = this.fromApiJsonHelper.extractBooleanNamed("includeInBorrowerCycle", element);
            baseDataValidator.reset().parameter("includeInBorrowerCycle").value(includeInBorrowerCycle).ignoreIfNull()
                    .validateForBooleanValue();
        }

        if (this.fromApiJsonHelper.parameterExists("currencyCode", element)) {
            final String currencyCode = this.fromApiJsonHelper.extractStringNamed("currencyCode", element);
            baseDataValidator.reset().parameter("currencyCode").value(currencyCode).notBlank().notExceedingLengthOf(3);
        }

        if (this.fromApiJsonHelper.parameterExists("digitsAfterDecimal", element)) {
            final Integer digitsAfterDecimal = this.fromApiJsonHelper.extractIntegerNamed("digitsAfterDecimal", element,
                    Locale.getDefault());
            baseDataValidator.reset().parameter("digitsAfterDecimal").value(digitsAfterDecimal).notNull().inMinMaxRange(0, 6);
        }

        if (this.fromApiJsonHelper.parameterExists("inMultiplesOf", element)) {
            final Integer inMultiplesOf = this.fromApiJsonHelper.extractIntegerNamed("inMultiplesOf", element, Locale.getDefault());
            baseDataValidator.reset().parameter("inMultiplesOf").value(inMultiplesOf).ignoreIfNull().integerZeroOrGreater();
        }

        final String minPrincipalParameterName = "minPrincipal";
        BigDecimal minPrincipalAmount = null;
        if (this.fromApiJsonHelper.parameterExists(minPrincipalParameterName, element)) {
            minPrincipalAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(minPrincipalParameterName, element);
            baseDataValidator.reset().parameter(minPrincipalParameterName).value(minPrincipalAmount).ignoreIfNull().positiveAmount();
        }

        final String maxPrincipalParameterName = "maxPrincipal";
        BigDecimal maxPrincipalAmount = null;
        if (this.fromApiJsonHelper.parameterExists(maxPrincipalParameterName, element)) {
            maxPrincipalAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(maxPrincipalParameterName, element);
            baseDataValidator.reset().parameter(maxPrincipalParameterName).value(maxPrincipalAmount).ignoreIfNull().positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("principal", element)) {
            final BigDecimal principal = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("principal", element);
            baseDataValidator.reset().parameter("principal").value(principal).notNull().positiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("inArrearsTolerance", element)) {
            final BigDecimal inArrearsTolerance = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("inArrearsTolerance", element);
            baseDataValidator.reset().parameter("inArrearsTolerance").value(inArrearsTolerance).ignoreIfNull().zeroOrPositiveAmount();
        }

        final String minInterestRatePerPeriodParameterName = "minInterestRatePerPeriod";
        BigDecimal minInterestRatePerPeriod = null;
        if (this.fromApiJsonHelper.parameterExists(minInterestRatePerPeriodParameterName, element)) {
            minInterestRatePerPeriod = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(minInterestRatePerPeriodParameterName,
                    element);
            baseDataValidator.reset().parameter(minInterestRatePerPeriodParameterName).value(minInterestRatePerPeriod).ignoreIfNull()
                    .zeroOrPositiveAmount();
        }

        final String maxInterestRatePerPeriodParameterName = "maxInterestRatePerPeriod";
        BigDecimal maxInterestRatePerPeriod = null;
        if (this.fromApiJsonHelper.parameterExists(maxInterestRatePerPeriodParameterName, element)) {
            maxInterestRatePerPeriod = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(maxInterestRatePerPeriodParameterName,
                    element);
            baseDataValidator.reset().parameter(maxInterestRatePerPeriodParameterName).value(maxInterestRatePerPeriod).ignoreIfNull()
                    .zeroOrPositiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("interestRatePerPeriod", element)) {
            final BigDecimal interestRatePerPeriod = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("interestRatePerPeriod",
                    element);
            baseDataValidator.reset().parameter("interestRatePerPeriod").value(interestRatePerPeriod).notNull().zeroOrPositiveAmount();
        }

        final String minNumberOfRepaymentsParameterName = "minNumberOfRepayments";
        Integer minNumberOfRepayments = null;
        if (this.fromApiJsonHelper.parameterExists(minNumberOfRepaymentsParameterName, element)) {
            minNumberOfRepayments = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(minNumberOfRepaymentsParameterName, element);
            baseDataValidator.reset().parameter(minNumberOfRepaymentsParameterName).value(minNumberOfRepayments).ignoreIfNull()
                    .integerGreaterThanZero();
        }

        final String maxNumberOfRepaymentsParameterName = "maxNumberOfRepayments";
        Integer maxNumberOfRepayments = null;
        if (this.fromApiJsonHelper.parameterExists(maxNumberOfRepaymentsParameterName, element)) {
            maxNumberOfRepayments = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(maxNumberOfRepaymentsParameterName, element);
            baseDataValidator.reset().parameter(maxNumberOfRepaymentsParameterName).value(maxNumberOfRepayments).ignoreIfNull()
                    .integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists("numberOfRepayments", element)) {
            final Integer numberOfRepayments = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("numberOfRepayments", element);
            baseDataValidator.reset().parameter("numberOfRepayments").value(numberOfRepayments).notNull().integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists("repaymentEvery", element)) {
            final Integer repaymentEvery = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("repaymentEvery", element);
            baseDataValidator.reset().parameter("repaymentEvery").value(repaymentEvery).notNull().integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists("repaymentFrequencyType", element)) {
            final Integer repaymentFrequencyType = this.fromApiJsonHelper.extractIntegerNamed("repaymentFrequencyType", element,
                    Locale.getDefault());
            baseDataValidator.reset().parameter("repaymentFrequencyType").value(repaymentFrequencyType).notNull().inMinMaxRange(0, 3);
        }

        if (this.fromApiJsonHelper.parameterExists("transactionProcessingStrategyId", element)) {
            final Long transactionProcessingStrategyId = this.fromApiJsonHelper
                    .extractLongNamed("transactionProcessingStrategyId", element);
            baseDataValidator.reset().parameter("transactionProcessingStrategyId").value(transactionProcessingStrategyId).notNull()
                    .integerGreaterThanZero();
        }

        // grace validation
        if (this.fromApiJsonHelper.parameterExists("graceOnPrincipalPayment", element)) {
            final Integer graceOnPrincipalPayment = this.fromApiJsonHelper
                    .extractIntegerWithLocaleNamed("graceOnPrincipalPayment", element);
            baseDataValidator.reset().parameter("graceOnPrincipalPayment").value(graceOnPrincipalPayment).zeroOrPositiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("graceOnInterestPayment", element)) {
            final Integer graceOnInterestPayment = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("graceOnInterestPayment", element);
            baseDataValidator.reset().parameter("graceOnInterestPayment").value(graceOnInterestPayment).zeroOrPositiveAmount();
        }

        if (this.fromApiJsonHelper.parameterExists("graceOnInterestCharged", element)) {
            final Integer graceOnInterestCharged = this.fromApiJsonHelper.extractIntegerWithLocaleNamed("graceOnInterestCharged", element);
            baseDataValidator.reset().parameter("graceOnInterestCharged").value(graceOnInterestCharged).zeroOrPositiveAmount();
        }

        //
        if (this.fromApiJsonHelper.parameterExists("interestRateFrequencyType", element)) {
            final Integer interestRateFrequencyType = this.fromApiJsonHelper.extractIntegerNamed("interestRateFrequencyType", element,
                    Locale.getDefault());
            baseDataValidator.reset().parameter("interestRateFrequencyType").value(interestRateFrequencyType).notNull().inMinMaxRange(0, 3);
        }

        if (this.fromApiJsonHelper.parameterExists("amortizationType", element)) {
            final Integer amortizationType = this.fromApiJsonHelper.extractIntegerNamed("amortizationType", element, Locale.getDefault());
            baseDataValidator.reset().parameter("amortizationType").value(amortizationType).notNull().inMinMaxRange(0, 1);
        }

        if (this.fromApiJsonHelper.parameterExists("interestType", element)) {
            final Integer interestType = this.fromApiJsonHelper.extractIntegerNamed("interestType", element, Locale.getDefault());
            baseDataValidator.reset().parameter("interestType").value(interestType).notNull().inMinMaxRange(0, 1);
        }

        if (this.fromApiJsonHelper.parameterExists("interestCalculationPeriodType", element)) {
            final Integer interestCalculationPeriodType = this.fromApiJsonHelper.extractIntegerNamed("interestCalculationPeriodType",
                    element, Locale.getDefault());
            baseDataValidator.reset().parameter("interestCalculationPeriodType").value(interestCalculationPeriodType).notNull()
                    .inMinMaxRange(0, 1);
        }

        final Integer accountingRuleType = this.fromApiJsonHelper.extractIntegerNamed("accountingRule", element, Locale.getDefault());
        baseDataValidator.reset().parameter("accountingRule").value(accountingRuleType).ignoreIfNull().inMinMaxRange(1, 3);

        final Long fundAccountId = this.fromApiJsonHelper.extractLongNamed(LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue()).value(fundAccountId).ignoreIfNull()
                .integerGreaterThanZero();

        final Long loanPortfolioAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.LOAN_PORTFOLIO.getValue()).value(loanPortfolioAccountId)
                .ignoreIfNull().integerGreaterThanZero();

        final Long transfersInSuspenseAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.TRANSFERS_SUSPENSE.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.TRANSFERS_SUSPENSE.getValue())
                .value(transfersInSuspenseAccountId).ignoreIfNull().integerGreaterThanZero();

        final Long incomeFromInterestId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_ON_LOANS.getValue()).value(incomeFromInterestId)
                .ignoreIfNull().integerGreaterThanZero();

        final Long incomeFromFeeId = this.fromApiJsonHelper.extractLongNamed(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue(),
                element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_FEES.getValue()).value(incomeFromFeeId)
                .ignoreIfNull().integerGreaterThanZero();

        final Long incomeFromPenaltyId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_FROM_PENALTIES.getValue()).value(incomeFromPenaltyId)
                .ignoreIfNull().integerGreaterThanZero();

        final Long writeOffAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.LOSSES_WRITTEN_OFF.getValue()).value(writeOffAccountId)
                .ignoreIfNull().integerGreaterThanZero();

        final Long overpaymentAccountId = this.fromApiJsonHelper.extractLongNamed(LOAN_PRODUCT_ACCOUNTING_PARAMS.OVERPAYMENT.getValue(),
                element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.OVERPAYMENT.getValue()).value(overpaymentAccountId)
                .ignoreIfNull().integerGreaterThanZero();

        final Long receivableInterestAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_RECEIVABLE.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.INTEREST_RECEIVABLE.getValue())
                .value(receivableInterestAccountId).ignoreIfNull().integerGreaterThanZero();

        final Long receivableFeeAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.FEES_RECEIVABLE.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.FEES_RECEIVABLE.getValue()).value(receivableFeeAccountId)
                .ignoreIfNull().integerGreaterThanZero();

        final Long receivablePenaltyAccountId = this.fromApiJsonHelper.extractLongNamed(
                LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTIES_RECEIVABLE.getValue(), element);
        baseDataValidator.reset().parameter(LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTIES_RECEIVABLE.getValue())
                .value(receivablePenaltyAccountId).ignoreIfNull().integerGreaterThanZero();

        validatePaymentChannelFundSourceMappings(baseDataValidator, element);
        validateChargeToIncomeAccountMappings(baseDataValidator, element);

        validateMinMaxConstraints(element, baseDataValidator, loanProduct);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    /*
     * Validation for advanced accounting options
     */
    private void validatePaymentChannelFundSourceMappings(final DataValidatorBuilder baseDataValidator, final JsonElement element) {
        if (this.fromApiJsonHelper.parameterExists(LOAN_PRODUCT_ACCOUNTING_PARAMS.PAYMENT_CHANNEL_FUND_SOURCE_MAPPING.getValue(), element)) {
            final JsonArray paymentChannelMappingArray = this.fromApiJsonHelper.extractJsonArrayNamed(
                    LOAN_PRODUCT_ACCOUNTING_PARAMS.PAYMENT_CHANNEL_FUND_SOURCE_MAPPING.getValue(), element);
            if (paymentChannelMappingArray != null && paymentChannelMappingArray.size() > 0) {
                int i = 0;
                do {
                    final JsonObject jsonObject = paymentChannelMappingArray.get(i).getAsJsonObject();
                    final Long paymentTypeId = this.fromApiJsonHelper.extractLongNamed(
                            LOAN_PRODUCT_ACCOUNTING_PARAMS.PAYMENT_TYPE.getValue(), jsonObject);
                    final Long paymentSpecificFundAccountId = this.fromApiJsonHelper.extractLongNamed(
                            LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue(), jsonObject);

                    baseDataValidator
                            .reset()
                            .parameter(
                                    LOAN_PRODUCT_ACCOUNTING_PARAMS.PAYMENT_CHANNEL_FUND_SOURCE_MAPPING.getValue() + "[" + i + "]."
                                            + LOAN_PRODUCT_ACCOUNTING_PARAMS.PAYMENT_TYPE.getValue()).value(paymentTypeId).notNull()
                            .integerGreaterThanZero();
                    baseDataValidator
                            .reset()
                            .parameter(
                                    LOAN_PRODUCT_ACCOUNTING_PARAMS.PAYMENT_CHANNEL_FUND_SOURCE_MAPPING.getValue() + "[" + i + "]."
                                            + LOAN_PRODUCT_ACCOUNTING_PARAMS.FUND_SOURCE.getValue()).value(paymentSpecificFundAccountId)
                            .notNull().integerGreaterThanZero();
                    i++;
                } while (i < paymentChannelMappingArray.size());
            }
        }
    }

    private void validateChargeToIncomeAccountMappings(final DataValidatorBuilder baseDataValidator, final JsonElement element) {
        // validate for both fee and penalty charges
        validateChargeToIncomeAccountMappings(baseDataValidator, element, true);
        validateChargeToIncomeAccountMappings(baseDataValidator, element, true);
    }

    private void validateChargeToIncomeAccountMappings(final DataValidatorBuilder baseDataValidator, final JsonElement element,
            final boolean isPenalty) {
        String parameterName;
        if (isPenalty) {
            parameterName = LOAN_PRODUCT_ACCOUNTING_PARAMS.PENALTY_INCOME_ACCOUNT_MAPPING.getValue();
        } else {
            parameterName = LOAN_PRODUCT_ACCOUNTING_PARAMS.FEE_INCOME_ACCOUNT_MAPPING.getValue();
        }

        if (this.fromApiJsonHelper.parameterExists(parameterName, element)) {
            final JsonArray chargeToIncomeAccountMappingArray = this.fromApiJsonHelper.extractJsonArrayNamed(parameterName, element);
            if (chargeToIncomeAccountMappingArray != null && chargeToIncomeAccountMappingArray.size() > 0) {
                int i = 0;
                do {
                    final JsonObject jsonObject = chargeToIncomeAccountMappingArray.get(i).getAsJsonObject();
                    final Long chargeId = this.fromApiJsonHelper.extractLongNamed(LOAN_PRODUCT_ACCOUNTING_PARAMS.CHARGE_ID.getValue(),
                            jsonObject);
                    final Long incomeAccountId = this.fromApiJsonHelper.extractLongNamed(
                            LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_ACCOUNT_ID.getValue(), jsonObject);
                    baseDataValidator.reset()
                            .parameter(parameterName + "[" + i + "]." + LOAN_PRODUCT_ACCOUNTING_PARAMS.CHARGE_ID.getValue())
                            .value(chargeId).notNull().integerGreaterThanZero();
                    baseDataValidator.reset()
                            .parameter(parameterName + "[" + i + "]." + LOAN_PRODUCT_ACCOUNTING_PARAMS.INCOME_ACCOUNT_ID.getValue())
                            .value(incomeAccountId).notNull().integerGreaterThanZero();
                    i++;
                } while (i < chargeToIncomeAccountMappingArray.size());
            }
        }
    }

    public void validateMinMaxConstraints(final JsonElement element, final DataValidatorBuilder baseDataValidator,
            final LoanProduct loanProduct) {

        validatePrincipalMinMaxConstraint(element, loanProduct, baseDataValidator);

        validateNumberOfRepaymentsMinMaxConstraint(element, loanProduct, baseDataValidator);

        validateNominalInterestRatePerPeriodMinMaxConstraint(element, loanProduct, baseDataValidator);
    }

    private void validatePrincipalMinMaxConstraint(final JsonElement element, final LoanProduct loanProduct,
            final DataValidatorBuilder baseDataValidator) {

        boolean principalUpdated = false;
        boolean minPrincipalUpdated = false;
        boolean maxPrincipalUpdated = false;
        final String principalParameterName = "principal";
        BigDecimal principalAmount = null;
        if (this.fromApiJsonHelper.parameterExists(principalParameterName, element)) {
            principalAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(principalParameterName, element);
            principalUpdated = true;
        } else {
            principalAmount = loanProduct.getPrincipalAmount().getAmount();
        }

        final String minPrincipalParameterName = "minPrincipal";
        BigDecimal minPrincipalAmount = null;
        if (this.fromApiJsonHelper.parameterExists(minPrincipalParameterName, element)) {
            minPrincipalAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(minPrincipalParameterName, element);
            minPrincipalUpdated = true;
        } else {
            minPrincipalAmount = loanProduct.getMinPrincipalAmount().getAmount();
        }

        final String maxPrincipalParameterName = "maxPrincipal";
        BigDecimal maxPrincipalAmount = null;
        if (this.fromApiJsonHelper.parameterExists(maxPrincipalParameterName, element)) {
            maxPrincipalAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(maxPrincipalParameterName, element);
            maxPrincipalUpdated = true;
        } else {
            maxPrincipalAmount = loanProduct.getMaxPrincipalAmount().getAmount();
        }

        if (minPrincipalUpdated) {
            baseDataValidator.reset().parameter(minPrincipalParameterName).value(minPrincipalAmount).notGreaterThanMax(maxPrincipalAmount);
        }

        if (maxPrincipalUpdated) {
            baseDataValidator.reset().parameter(maxPrincipalParameterName).value(maxPrincipalAmount).notLessThanMin(minPrincipalAmount);
        }

        if ((principalUpdated || minPrincipalUpdated || maxPrincipalUpdated)) {

            if ((minPrincipalAmount != null && minPrincipalAmount.compareTo(BigDecimal.ZERO) == 1)
                    && (maxPrincipalAmount != null && maxPrincipalAmount.compareTo(BigDecimal.ZERO) == 1)) {
                baseDataValidator.reset().parameter(principalParameterName).value(principalAmount)
                        .inMinAndMaxAmountRange(minPrincipalAmount, maxPrincipalAmount);
            } else {
                if (minPrincipalAmount != null && minPrincipalAmount.compareTo(BigDecimal.ZERO) == 1) {
                    baseDataValidator.reset().parameter(principalParameterName).value(principalAmount).notLessThanMin(minPrincipalAmount);
                } else if (maxPrincipalAmount != null && maxPrincipalAmount.compareTo(BigDecimal.ZERO) == 1) {
                    baseDataValidator.reset().parameter(principalParameterName).value(principalAmount)
                            .notGreaterThanMax(maxPrincipalAmount);
                }
            }
        }
    }

    private void validateNumberOfRepaymentsMinMaxConstraint(final JsonElement element, final LoanProduct loanProduct,
            final DataValidatorBuilder baseDataValidator) {
        boolean numberOfRepaymentsUpdated = false;
        boolean minNumberOfRepaymentsUpdated = false;
        boolean maxNumberOfRepaymentsUpdated = false;

        final String numberOfRepaymentsParameterName = "numberOfRepayments";
        Integer numberOfRepayments = null;
        if (this.fromApiJsonHelper.parameterExists(numberOfRepaymentsParameterName, element)) {
            numberOfRepayments = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(numberOfRepaymentsParameterName, element);
            numberOfRepaymentsUpdated = true;
        } else {
            numberOfRepayments = loanProduct.getNumberOfRepayments();
        }

        final String minNumberOfRepaymentsParameterName = "minNumberOfRepayments";
        Integer minNumberOfRepayments = null;
        if (this.fromApiJsonHelper.parameterExists(minNumberOfRepaymentsParameterName, element)) {
            minNumberOfRepayments = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(minNumberOfRepaymentsParameterName, element);
            minNumberOfRepaymentsUpdated = true;
        } else {
            minNumberOfRepayments = loanProduct.getMinNumberOfRepayments();
        }

        final String maxNumberOfRepaymentsParameterName = "maxNumberOfRepayments";
        Integer maxNumberOfRepayments = null;
        if (this.fromApiJsonHelper.parameterExists(maxNumberOfRepaymentsParameterName, element)) {
            maxNumberOfRepayments = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(maxNumberOfRepaymentsParameterName, element);
            maxNumberOfRepaymentsUpdated = true;
        } else {
            maxNumberOfRepayments = loanProduct.getMaxNumberOfRepayments();
        }

        if (minNumberOfRepaymentsUpdated) {
            baseDataValidator.reset().parameter(minNumberOfRepaymentsParameterName).value(minNumberOfRepayments).ignoreIfNull()
                    .notGreaterThanMax(maxNumberOfRepayments);
        }

        if (maxNumberOfRepaymentsUpdated) {
            baseDataValidator.reset().parameter(maxNumberOfRepaymentsParameterName).value(maxNumberOfRepayments)
                    .notLessThanMin(minNumberOfRepayments);
        }

        if (numberOfRepaymentsUpdated || minNumberOfRepaymentsUpdated || maxNumberOfRepaymentsUpdated) {
            if (maxNumberOfRepayments != null && maxNumberOfRepayments.compareTo(0) == 1) {
                if (minNumberOfRepayments != null && minNumberOfRepayments.compareTo(0) == 1) {
                    baseDataValidator.reset().parameter(numberOfRepaymentsParameterName).value(numberOfRepayments)
                            .inMinMaxRange(minNumberOfRepayments, maxNumberOfRepayments);
                } else {
                    baseDataValidator.reset().parameter(numberOfRepaymentsParameterName).value(numberOfRepayments)
                            .notGreaterThanMax(maxNumberOfRepayments);
                }
            } else if (minNumberOfRepayments != null && minNumberOfRepayments.compareTo(0) == 1) {
                baseDataValidator.reset().parameter(numberOfRepaymentsParameterName).value(numberOfRepayments)
                        .notLessThanMin(minNumberOfRepayments);
            }
        }
    }

    private void validateNominalInterestRatePerPeriodMinMaxConstraint(final JsonElement element, final LoanProduct loanProduct,
            final DataValidatorBuilder baseDataValidator) {

        boolean iRPUpdated = false;
        boolean minIRPUpdated = false;
        boolean maxIRPUpdated = false;
        final String interestRatePerPeriodParameterName = "interestRatePerPeriod";
        BigDecimal interestRatePerPeriod = null;

        if (this.fromApiJsonHelper.parameterExists(interestRatePerPeriodParameterName, element)) {
            interestRatePerPeriod = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(interestRatePerPeriodParameterName, element);
            iRPUpdated = true;
        } else {
            interestRatePerPeriod = loanProduct.getNominalInterestRatePerPeriod();
        }

        final String minInterestRatePerPeriodParameterName = "minInterestRatePerPeriod";
        BigDecimal minInterestRatePerPeriod = null;
        if (this.fromApiJsonHelper.parameterExists(minInterestRatePerPeriodParameterName, element)) {
            minInterestRatePerPeriod = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(minInterestRatePerPeriodParameterName,
                    element);
            minIRPUpdated = true;
        } else {
            minInterestRatePerPeriod = loanProduct.getMinNominalInterestRatePerPeriod();
        }

        final String maxInterestRatePerPeriodParameterName = "maxInterestRatePerPeriod";
        BigDecimal maxInterestRatePerPeriod = null;
        if (this.fromApiJsonHelper.parameterExists(maxInterestRatePerPeriodParameterName, element)) {
            maxInterestRatePerPeriod = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(maxInterestRatePerPeriodParameterName,
                    element);
            maxIRPUpdated = true;
        } else {
            maxInterestRatePerPeriod = loanProduct.getMaxNominalInterestRatePerPeriod();
        }

        if (minIRPUpdated) {
            baseDataValidator.reset().parameter(minInterestRatePerPeriodParameterName).value(minInterestRatePerPeriod).ignoreIfNull()
                    .notGreaterThanMax(maxInterestRatePerPeriod);
        }

        if (maxIRPUpdated) {
            baseDataValidator.reset().parameter(maxInterestRatePerPeriodParameterName).value(maxInterestRatePerPeriod).ignoreIfNull()
                    .notLessThanMin(minInterestRatePerPeriod);
        }

        if (iRPUpdated || minIRPUpdated || maxIRPUpdated) {
            if (maxInterestRatePerPeriod != null) {
                if (minInterestRatePerPeriod != null) {
                    baseDataValidator.reset().parameter(interestRatePerPeriodParameterName).value(interestRatePerPeriod)
                            .inMinAndMaxAmountRange(minInterestRatePerPeriod, maxInterestRatePerPeriod);
                } else {
                    baseDataValidator.reset().parameter(interestRatePerPeriodParameterName).value(interestRatePerPeriod)
                            .notGreaterThanMax(maxInterestRatePerPeriod);
                }
            } else if (minInterestRatePerPeriod != null) {
                baseDataValidator.reset().parameter(interestRatePerPeriodParameterName).value(interestRatePerPeriod)
                        .notLessThanMin(minInterestRatePerPeriod);
            }
        }
    }

    private boolean isCashBasedAccounting(final Integer accountingRuleType) {
        return AccountingRuleType.CASH_BASED.getValue().equals(accountingRuleType);
    }

    private boolean isAccrualBasedAccounting(final Integer accountingRuleType) {
        return AccountingRuleType.ACCRUAL_BASED.getValue().equals(accountingRuleType);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }
}