package org.mifosplatform.infrastructure.smsgateway.data;

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
import org.mifosplatform.infrastructure.smsgateway.SmsGatewayApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class SmsGatewayDataValidator {
	private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public SmsGatewayDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SmsGatewayApiConstants.CREATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SmsGatewayApiConstants.RESOURCE_NAME);

        final String gatewayName = this.fromApiJsonHelper.extractStringNamed(SmsGatewayApiConstants.gatewayNameParamName, element);
        baseDataValidator.reset().parameter(SmsGatewayApiConstants.gatewayNameParamName).value(gatewayName).notBlank().notExceedingLengthOf(333);

        final String authToken = this.fromApiJsonHelper.extractStringNamed(SmsGatewayApiConstants.authTokenName, element);
        baseDataValidator.reset().parameter(SmsGatewayApiConstants.authTokenName).value(authToken).notBlank().notExceedingLengthOf(333);
        
        final String url = this.fromApiJsonHelper.extractStringNamed(SmsGatewayApiConstants.urlParamName, element);
        baseDataValidator.reset().parameter(SmsGatewayApiConstants.urlParamName).value(url).notBlank().notExceedingLengthOf(333);
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SmsGatewayApiConstants.UPDATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SmsGatewayApiConstants.RESOURCE_NAME);

        final String gatewayName = this.fromApiJsonHelper.extractStringNamed(SmsGatewayApiConstants.gatewayNameParamName, element);
        baseDataValidator.reset().parameter(SmsGatewayApiConstants.gatewayNameParamName).value(gatewayName).notExceedingLengthOf(333);

        final String authToken = this.fromApiJsonHelper.extractStringNamed(SmsGatewayApiConstants.authTokenName, element);
        baseDataValidator.reset().parameter(SmsGatewayApiConstants.authTokenName).value(authToken).notExceedingLengthOf(333);
        
        final String url = this.fromApiJsonHelper.extractStringNamed(SmsGatewayApiConstants.urlParamName, element);
        baseDataValidator.reset().parameter(SmsGatewayApiConstants.urlParamName).value(url).notExceedingLengthOf(333);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
	
}
