/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.paymenttowhom.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.paymenttowhom.api.PaymentToWhomApiResourceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class PaymentToWhomDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public PaymentToWhomDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
                PaymentToWhomApiResourceConstants.CREATE_PAYMENT_TYPE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(PaymentToWhomApiResourceConstants.resourceNameForPermissions);

        if (this.fromApiJsonHelper.parameterExists(PaymentToWhomApiResourceConstants.NAME, element)) {
            final String name = this.fromApiJsonHelper.extractStringNamed(PaymentToWhomApiResourceConstants.NAME, element);
            baseDataValidator.reset().parameter(PaymentToWhomApiResourceConstants.NAME).value(name).notBlank();
        }

        if (this.fromApiJsonHelper.parameterExists(PaymentToWhomApiResourceConstants.DESCRIPTION, element)) {
            final String description = this.fromApiJsonHelper.extractStringNamed(PaymentToWhomApiResourceConstants.DESCRIPTION, element);
            baseDataValidator.reset().parameter(PaymentToWhomApiResourceConstants.DESCRIPTION).value(description).ignoreIfNull().notExceedingLengthOf(500);
        }

        if (this.fromApiJsonHelper.parameterExists(PaymentToWhomApiResourceConstants.ISCASHPAYMENT, element)) {
            final Boolean isCashPayment = this.fromApiJsonHelper
                    .extractBooleanNamed(PaymentToWhomApiResourceConstants.ISCASHPAYMENT, element);
            baseDataValidator.reset().parameter(PaymentToWhomApiResourceConstants.ISCASHPAYMENT).value(isCashPayment).validateForBooleanValue();
        }

        if (this.fromApiJsonHelper.parameterExists(PaymentToWhomApiResourceConstants.POSITION, element)) {
            final Long position = this.fromApiJsonHelper.extractLongNamed(PaymentToWhomApiResourceConstants.POSITION, element);
            baseDataValidator.reset().parameter(PaymentToWhomApiResourceConstants.POSITION).value(position).ignoreIfNull()
                    .integerZeroOrGreater();
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            //
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }

    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
        		PaymentToWhomApiResourceConstants.UPDATE_PAYMENT_TYPE_REQUEST_DATA_PARAMETERS);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(PaymentToWhomApiResourceConstants.resourceNameForPermissions);

        if (this.fromApiJsonHelper.parameterExists(PaymentToWhomApiResourceConstants.NAME, element)) {
            final String name = this.fromApiJsonHelper.extractStringNamed(PaymentToWhomApiResourceConstants.NAME, element);
            baseDataValidator.reset().parameter(PaymentToWhomApiResourceConstants.NAME).value(name);
        }

        if (this.fromApiJsonHelper.parameterExists(PaymentToWhomApiResourceConstants.DESCRIPTION, element)) {
            final String description = this.fromApiJsonHelper.extractStringNamed(PaymentToWhomApiResourceConstants.DESCRIPTION, element);
            baseDataValidator.reset().parameter(PaymentToWhomApiResourceConstants.DESCRIPTION).value(description).ignoreIfNull().notExceedingLengthOf(500);
        }

        if (this.fromApiJsonHelper.parameterExists(PaymentToWhomApiResourceConstants.ISCASHPAYMENT, element)) {
            final Boolean isCashPayment = this.fromApiJsonHelper
                    .extractBooleanNamed(PaymentToWhomApiResourceConstants.ISCASHPAYMENT, element);
            baseDataValidator.reset().parameter(PaymentToWhomApiResourceConstants.ISCASHPAYMENT).value(isCashPayment).validateForBooleanValue();
        }

        if (this.fromApiJsonHelper.parameterExists(PaymentToWhomApiResourceConstants.POSITION, element)) {
            final Long position = this.fromApiJsonHelper.extractLongNamed(PaymentToWhomApiResourceConstants.POSITION, element);
            baseDataValidator.reset().parameter(PaymentToWhomApiResourceConstants.POSITION).value(position).ignoreIfNull()
                    .integerZeroOrGreater();
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }
}
