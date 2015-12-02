/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.data;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.sms.domain.SmsCampaignType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class SmsCampaignValidator {

   

    public static final String RESOURCE_NAME = "sms";
    public static final String campaignName  = "campaignName";
    public static final String campaignType = "campaignType";
    public static final String runReportId = "runReportId";
    public static final String paramValue = "paramValue";
    public static final String message   = "message";
    public static final String activationDateParamName = "activationDate";
    public static final String recurrenceStartDate = "recurrenceStartDate";
    public static final String submittedOnDateParamName = "submittedOnDate";
    public static final String closureDateParamName = "closureDate";
    public static final String recurrenceParamName = "recurrence";
    public static final String statusParamName = "status";

    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";


    private final FromJsonHelper fromApiJsonHelper;


    public static final Set<String> supportedParams = new HashSet<String>(Arrays.asList(campaignName, campaignType,localeParamName,dateFormatParamName,
            runReportId,paramValue,message,recurrenceStartDate,activationDateParamName,submittedOnDateParamName,closureDateParamName,recurrenceParamName));

    public static final Set<String> supportedParamsForUpdate = new HashSet<>(Arrays.asList(campaignName, campaignType,localeParamName,dateFormatParamName,
            runReportId,paramValue,message,recurrenceStartDate,activationDateParamName,recurrenceParamName));

    public static final Set<String> ACTIVATION_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(localeParamName,
            dateFormatParamName, activationDateParamName));

    public static final Set<String> CLOSE_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(localeParamName,
            dateFormatParamName, closureDateParamName));

    public static final Set<String> PREVIEW_REQUEST_DATA_PARAMETERS= new HashSet<String>(Arrays.asList(paramValue,message));

    @Autowired
    public SmsCampaignValidator(FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }


    public void validateCreate(String json){
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SmsCampaignValidator.supportedParams);
        
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SmsCampaignValidator.RESOURCE_NAME);
        
        final String campaignName =  this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.campaignName,element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.campaignName).value(campaignName).notBlank().notExceedingLengthOf(100);
        
        
        final Long campaignType = this.fromApiJsonHelper.extractLongNamed(SmsCampaignValidator.campaignType,element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.campaignType).value(campaignType).notNull().integerGreaterThanZero();

        if(campaignType.intValue() == SmsCampaignType.SCHEDULE.getValue()){
            final String recurrenceParamName =  this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.recurrenceParamName, element);
            baseDataValidator.reset().parameter(SmsCampaignValidator.recurrenceParamName).value(recurrenceParamName).notBlank();

            final String recurrenceStartDate =  this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.recurrenceStartDate, element);
            baseDataValidator.reset().parameter(SmsCampaignValidator.recurrenceStartDate).value(recurrenceStartDate).notBlank();
        }

        final Long runReportId = this.fromApiJsonHelper.extractLongNamed(SmsCampaignValidator.runReportId,element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.runReportId).value(runReportId).notNull().integerGreaterThanZero();

        final String message = this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.message, element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.message).value(message).notBlank().notExceedingLengthOf(480);

        final String paramValue = this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.paramValue, element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.paramValue).value(paramValue).notBlank();



        if (this.fromApiJsonHelper.parameterExists(SmsCampaignValidator.submittedOnDateParamName, element)) {
            final LocalDate submittedOnDate = this.fromApiJsonHelper.extractLocalDateNamed(SmsCampaignValidator.submittedOnDateParamName,
                    element);
            baseDataValidator.reset().parameter(SmsCampaignValidator.submittedOnDateParamName).value(submittedOnDate).notNull();
        }



        throwExceptionIfValidationWarningsExist(dataValidationErrors);

    }

    public void validateForUpdate(String json){
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SmsCampaignValidator.supportedParamsForUpdate);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SmsCampaignValidator.RESOURCE_NAME);

        final String campaignName =  this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.campaignName,element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.campaignName).value(campaignName).notBlank().notExceedingLengthOf(100);


        final Long campaignType = this.fromApiJsonHelper.extractLongNamed(SmsCampaignValidator.campaignType,element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.campaignType).value(campaignType).notNull().integerGreaterThanZero();

        if(campaignType.intValue() == SmsCampaignType.SCHEDULE.getValue()){
            final String recurrenceParamName =  this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.recurrenceParamName, element);
            baseDataValidator.reset().parameter(SmsCampaignValidator.recurrenceParamName).value(recurrenceParamName).notBlank();

            final String recurrenceStartDate =  this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.recurrenceStartDate, element);
            baseDataValidator.reset().parameter(SmsCampaignValidator.recurrenceStartDate).value(recurrenceStartDate).notBlank();
        }

        final Long runReportId = this.fromApiJsonHelper.extractLongNamed(SmsCampaignValidator.runReportId,element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.runReportId).value(runReportId).notNull().integerGreaterThanZero();

        final String message = this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.message, element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.message).value(message).notBlank().notExceedingLengthOf(480);

        final String paramValue = this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.paramValue, element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.paramValue).value(paramValue).notBlank();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);


    }

    public void validatePreviewMessage(String json){
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SmsCampaignValidator.PREVIEW_REQUEST_DATA_PARAMETERS);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SmsCampaignValidator.RESOURCE_NAME);

        final String paramValue = this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.paramValue, element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.paramValue).value(paramValue).notBlank();

        final String message = this.fromApiJsonHelper.extractStringNamed(SmsCampaignValidator.message, element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.message).value(message).notBlank().notExceedingLengthOf(480);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);


    }

    public void validateClosedDate(String json){
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SmsCampaignValidator.CLOSE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SmsCampaignValidator.RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final LocalDate closeDate = this.fromApiJsonHelper.extractLocalDateNamed(SmsCampaignValidator.closureDateParamName, element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.closureDateParamName).value(closeDate).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    public void validateActivation(String json){
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SmsCampaignValidator.ACTIVATION_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SmsCampaignValidator.RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final LocalDate activationDate = this.fromApiJsonHelper.extractLocalDateNamed(SmsCampaignValidator.activationDateParamName, element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.activationDateParamName).value(activationDate).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void ValidateClosure(String json){
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SmsCampaignValidator.CLOSE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(SmsCampaignValidator.RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final LocalDate closeDate = this.fromApiJsonHelper.extractLocalDateNamed(SmsCampaignValidator.closureDateParamName, element);
        baseDataValidator.reset().parameter(SmsCampaignValidator.closureDateParamName).value(closeDate).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}
