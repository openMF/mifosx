/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.data;

import static org.mifosplatform.portfolio.savings.SavingsApiConstants.SAVINGS_ACCOUNT_CHARGES_ADD_REQUEST_DATA_PARAMETERS;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.SAVINGS_ACCOUNT_CHARGES_PAY_CHARGE_REQUEST_DATA_PARAMETERS;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.SAVINGS_ACCOUNT_CHARGE_RESOURCE_NAME;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.amountParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.chargeIdParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.dueAsOfDateParamName;
import static org.mifosplatform.portfolio.savings.SavingsApiConstants.feeOnMonthDayParamName;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
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
public class SavingsAccountChargeDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public SavingsAccountChargeDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateAdd(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SAVINGS_ACCOUNT_CHARGES_ADD_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SAVINGS_ACCOUNT_CHARGE_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final Long chargeId = this.fromApiJsonHelper.extractLongNamed(chargeIdParamName, element);
        baseDataValidator.reset().parameter(chargeIdParamName).value(chargeId).notNull().integerGreaterThanZero();

        final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(amountParamName, element);
        baseDataValidator.reset().parameter(amountParamName).value(amount).notNull().positiveAmount();

        if (this.fromApiJsonHelper.parameterExists(dueAsOfDateParamName, element)) {
            final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed(dueAsOfDateParamName, element); 
            baseDataValidator.reset().parameter(dueAsOfDateParamName).value(transactionDate).notNull();
        }
        
        if (this.fromApiJsonHelper.parameterExists(feeOnMonthDayParamName, element)) {
            final MonthDay monthDay = this.fromApiJsonHelper.extractMonthDayNamed(feeOnMonthDayParamName, element); 
            baseDataValidator.reset().parameter(feeOnMonthDayParamName).value(monthDay).notNull();
        }
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateUpdate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SAVINGS_ACCOUNT_CHARGES_ADD_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SAVINGS_ACCOUNT_CHARGE_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(amountParamName, element);
        baseDataValidator.reset().parameter(amountParamName).value(amount).notNull().positiveAmount();

        if (this.fromApiJsonHelper.parameterExists(dueAsOfDateParamName, element)) {
            final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed(dueAsOfDateParamName, element); 
            baseDataValidator.reset().parameter(dueAsOfDateParamName).value(transactionDate).notNull();
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validatePayCharge(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SAVINGS_ACCOUNT_CHARGES_PAY_CHARGE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SAVINGS_ACCOUNT_CHARGE_RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(amountParamName, element);
        baseDataValidator.reset().parameter(amountParamName).value(amount).notNull().positiveAmount();

        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed(dueAsOfDateParamName, element); 
        baseDataValidator.reset().parameter(dueAsOfDateParamName).value(transactionDate).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}