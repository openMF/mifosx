/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.data;

import static org.mifosplatform.portfolio.savings.SavingsApiConstants.activatedOnDateParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.bankNumberParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.checkNumberParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.closedOnDateParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.paymentTypeIdParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.receiptNumberParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.routingCodeParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.transactionAccountNumberParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.transactionAmountParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.transactionDateParamName;

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
import org.mifosplatform.portfolio.savings.SavingsApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class SavingsAccountTransactionDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public SavingsAccountTransactionDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validate(final JsonCommand command) {

        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                SavingsApiConstants.SAVINGS_ACCOUNT_TRANSACTION_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SavingsApiConstants.SAVINGS_ACCOUNT_TRANSACTION_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();

        final LocalDate transactionDate = fromApiJsonHelper.extractLocalDateNamed(transactionDateParamName, element);
        baseDataValidator.reset().parameter(transactionDateParamName).value(transactionDate).notNull();

        final BigDecimal transactionAmount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed(transactionAmountParamName, element);
        baseDataValidator.reset().parameter(transactionAmountParamName).value(transactionAmount).notNull().positiveAmount();

        // Validate all string payment detail fields for max length
        final Integer paymentTypeId = fromApiJsonHelper.extractIntegerWithLocaleNamed(paymentTypeIdParamName, element);
        baseDataValidator.reset().parameter(paymentTypeIdParamName).value(paymentTypeId).ignoreIfNull().integerGreaterThanZero();
        final Set<String> paymentDetailParameters = new HashSet<String>(Arrays.asList(transactionAccountNumberParamName, checkNumberParamName,
                routingCodeParamName, receiptNumberParamName, bankNumberParamName));
        for (String paymentDetailParameterName : paymentDetailParameters) {
            final String paymentDetailParameterValue = fromApiJsonHelper.extractStringNamed(paymentDetailParameterName, element);
            baseDataValidator.reset().parameter(paymentDetailParameterName).value(paymentDetailParameterValue).ignoreIfNull()
                    .notExceedingLengthOf(50);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateActivation(final JsonCommand command) {
        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                SavingsApiConstants.SAVINGS_ACCOUNT_ACTIVATION_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SavingsApiConstants.SAVINGS_ACCOUNT_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();

        final LocalDate activationDate = fromApiJsonHelper.extractLocalDateNamed(activatedOnDateParamName, element);
        baseDataValidator.reset().parameter(activatedOnDateParamName).value(activationDate).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    public void validateClosing(final JsonCommand command) {
        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                SavingsApiConstants.SAVINGS_ACCOUNT_CLOSE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SavingsApiConstants.SAVINGS_ACCOUNT_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();

        final LocalDate activationDate = fromApiJsonHelper.extractLocalDateNamed(closedOnDateParamName, element);
        baseDataValidator.reset().parameter(closedOnDateParamName).value(activationDate).notNull();

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