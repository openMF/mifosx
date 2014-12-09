/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.data;

import static org.mifosplatform.portfolio.savings.DepositsApiConstants.activatedOnDateParamName;
import static org.mifosplatform.portfolio.savings.DepositsApiConstants.bankNumberParamName;
import static org.mifosplatform.portfolio.savings.DepositsApiConstants.checkNumberParamName;
import static org.mifosplatform.portfolio.savings.DepositsApiConstants.closedOnDateParamName;
import static org.mifosplatform.portfolio.savings.DepositsApiConstants.onAccountClosureIdParamName;
import static org.mifosplatform.portfolio.savings.DepositsApiConstants.paymentTypeIdParamName;
import static org.mifosplatform.portfolio.savings.DepositsApiConstants.receiptNumberParamName;
import static org.mifosplatform.portfolio.savings.DepositsApiConstants.routingCodeParamName;
import static org.mifosplatform.portfolio.savings.DepositsApiConstants.toSavingsAccountIdParamName;
import static org.mifosplatform.portfolio.savings.DepositsApiConstants.transactionAccountNumberParamName;
import static org.mifosplatform.portfolio.savings.DepositsApiConstants.transactionAmountParamName;
import static org.mifosplatform.portfolio.savings.DepositsApiConstants.transactionDateParamName;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.savings.DepositAccountOnClosureType;
import org.mifosplatform.portfolio.savings.DepositAccountType;
import org.mifosplatform.portfolio.savings.DepositsApiConstants;
import org.mifosplatform.portfolio.savings.SavingsApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class DepositAccountTransactionDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public DepositAccountTransactionDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validate(final JsonCommand command, DepositAccountType depositAccountType) {

        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                DepositsApiConstants.DEPOSIT_ACCOUNT_TRANSACTION_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource(depositAccountType
                .resourceName());

        final JsonElement element = command.parsedJson();

        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed(transactionDateParamName, element);
        baseDataValidator.reset().parameter(transactionDateParamName).value(transactionDate).notNull();

        final BigDecimal transactionAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(transactionAmountParamName, element);
        baseDataValidator.reset().parameter(transactionAmountParamName).value(transactionAmount).notNull().positiveAmount();

        // Validate all string payment detail fields for max length
        final Integer paymentTypeId = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(paymentTypeIdParamName, element);
        baseDataValidator.reset().parameter(paymentTypeIdParamName).value(paymentTypeId).ignoreIfNull().integerGreaterThanZero();
        final Set<String> paymentDetailParameters = new HashSet<>(Arrays.asList(transactionAccountNumberParamName, checkNumberParamName,
                routingCodeParamName, receiptNumberParamName, bankNumberParamName));
        for (final String paymentDetailParameterName : paymentDetailParameters) {
            final String paymentDetailParameterValue = this.fromApiJsonHelper.extractStringNamed(paymentDetailParameterName, element);
            baseDataValidator.reset().parameter(paymentDetailParameterName).value(paymentDetailParameterValue).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateDepositAmountUpdate(final JsonCommand command) {
        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                DepositsApiConstants.DEPOSIT_ACCOUNT_RECOMMENDED_DEPOSIT_AMOUNT_UPDATE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SavingsApiConstants.SAVINGS_ACCOUNT_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();

        final LocalDate effectiveDate = this.fromApiJsonHelper.extractLocalDateNamed(DepositsApiConstants.effectiveDateParamName, element);
        baseDataValidator.reset().parameter(DepositsApiConstants.effectiveDateParamName).value(effectiveDate).notNull();

        final BigDecimal mandatoryRecommendedDepositAmount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(
                DepositsApiConstants.mandatoryRecommendedDepositAmountParamName, element);
        baseDataValidator.reset().parameter(DepositsApiConstants.mandatoryRecommendedDepositAmountParamName)
                .value(mandatoryRecommendedDepositAmount).notNull().positiveAmount();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateActivation(final JsonCommand command) {
        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                SavingsApiConstants.SAVINGS_ACCOUNT_ACTIVATION_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SavingsApiConstants.SAVINGS_ACCOUNT_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();

        final LocalDate activationDate = this.fromApiJsonHelper.extractLocalDateNamed(activatedOnDateParamName, element);
        baseDataValidator.reset().parameter(activatedOnDateParamName).value(activationDate).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validatePreMatureAmountCalculation(final String json, final DepositAccountType depositAccountType) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                DepositsApiConstants.DEPOSIT_ACCOUNT_PRE_MATURE_CALCULATION_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource(depositAccountType
                .resourceName());

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final LocalDate closeDate = this.fromApiJsonHelper.extractLocalDateNamed(closedOnDateParamName, element);
        baseDataValidator.reset().parameter(closedOnDateParamName).value(closeDate).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateClosing(final JsonCommand command, DepositAccountType depositAccountType, final boolean isPreMatureClose) {
        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                DepositsApiConstants.DEPOSIT_ACCOUNT_CLOSE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource(depositAccountType
                .resourceName());

        final JsonElement element = command.parsedJson();

        final LocalDate activationDate = this.fromApiJsonHelper.extractLocalDateNamed(closedOnDateParamName, element);
        baseDataValidator.reset().parameter(closedOnDateParamName).value(activationDate).notNull();

        final Integer onAccountClosureId = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(onAccountClosureIdParamName, element);
        baseDataValidator.reset().parameter(onAccountClosureIdParamName).value(onAccountClosureId).notBlank()
                .isOneOfTheseValues(DepositAccountOnClosureType.integerValues());

        if (onAccountClosureId != null) {
            final DepositAccountOnClosureType accountOnClosureType = DepositAccountOnClosureType.fromInt(onAccountClosureId);
            if (accountOnClosureType.isTransferToSavings()) {
                final Long toSavingsAccountId = this.fromApiJsonHelper.extractLongNamed(toSavingsAccountIdParamName, element);
                baseDataValidator
                        .reset()
                        .parameter(toSavingsAccountIdParamName)
                        .value(toSavingsAccountId)
                        .cantBeBlankWhenParameterProvidedIs(onAccountClosureIdParamName,
                                DepositAccountOnClosureType.fromInt(onAccountClosureId).getCode());
            } else if (accountOnClosureType.isReinvest() && isPreMatureClose) {
                baseDataValidator.reset().parameter(onAccountClosureIdParamName).value(onAccountClosureId)
                        .failWithCode("reinvest.not.allowed", "Re-Invest is not supported for account pre mature close");
            }
        }

        // Validate all string payment detail fields for max length
        final Integer paymentTypeId = this.fromApiJsonHelper.extractIntegerWithLocaleNamed(paymentTypeIdParamName, element);
        baseDataValidator.reset().parameter(paymentTypeIdParamName).value(paymentTypeId).ignoreIfNull().integerGreaterThanZero();
        final Set<String> paymentDetailParameters = new HashSet<>(Arrays.asList(transactionAccountNumberParamName, checkNumberParamName,
                routingCodeParamName, receiptNumberParamName, bankNumberParamName));
        for (final String paymentDetailParameterName : paymentDetailParameters) {
            final String paymentDetailParameterValue = this.fromApiJsonHelper.extractStringNamed(paymentDetailParameterName, element);
            baseDataValidator.reset().parameter(paymentDetailParameterName).value(paymentDetailParameterValue).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            //
            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                    dataValidationErrors);
        }
    }
}