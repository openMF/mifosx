/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.data;

import static org.mifosplatform.portfolio.savings.api.SavingsApiConstants.activationDateParamName;

import java.lang.reflect.Type;
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
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public final class ClientDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ClientDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ClientApiConstants.CLIENT_CREATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiConstants.CLIENT_RESOURCE_NAME);

        final Long officeId = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.officeIdParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.officeIdParamName).value(officeId).notNull().integerGreaterThanZero();

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.groupIdParamName, element)) {
            final Long groupId = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.groupIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.groupIdParamName).value(groupId).notNull().integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.staffIdParamName, element)) {
            final Long staffId = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.staffIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.staffIdParamName).value(staffId).ignoreIfNull().longGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.accountNoParamName, element)) {
            final String accountNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.accountNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.accountNoParamName).value(accountNo).notBlank().notExceedingLengthOf(20);
        }

        if (isFullnameProvided(element) || isIndividualNameProvided(element)) {

            // 1. No individual name part provided and fullname provided
            if (isFullnameProvided(element) && !isIndividualNameProvided(element)) {
                fullnameCannotBeBlank(element, baseDataValidator);
            }

            // 2. no fullname provided and individual name part provided
            if (isIndividualNameProvided(element) && !isFullnameProvided(element)) {
                validateRequiredIndividualNamePartsExist(element, baseDataValidator);
            }

            // 3. both provided
            if (isFullnameProvided(element) && isIndividualNameProvided(element)) {
                validateIndividualNamePartsCannotBeUsedWithFullname(element, baseDataValidator);
            }
        } else {

            if (isFullnameParameterPassed(element) || isIndividualNamePartParameterPassed(element)) {

                // 1. No individual name parameter passed and fullname passed
                if (isFullnameParameterPassed(element) && !isIndividualNamePartParameterPassed(element)) {
                    fullnameCannotBeBlank(element, baseDataValidator);
                }

                // 2. no fullname passed and individual name part passed
                if (isIndividualNamePartParameterPassed(element) && !isFullnameParameterPassed(element)) {
                    validateRequiredIndividualNamePartsExist(element, baseDataValidator);
                }

                // 3. both parameter types passed
                if (isFullnameParameterPassed(element) && isIndividualNamePartParameterPassed(element)) {
                    baseDataValidator.reset().parameter(ClientApiConstants.idParamName).failWithCode(".no.name.details.passed");
                }

            } else {
                baseDataValidator.reset().parameter(ClientApiConstants.idParamName).failWithCode(".no.name.details.passed");
            }
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.externalIdParamName, element)) {
            final String externalId = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.externalIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.externalIdParamName).value(externalId).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }

        final Boolean active = this.fromApiJsonHelper.extractBooleanNamed(ClientApiConstants.activeParamName, element);
        if (active != null) {
            if (active.booleanValue()) {
                final LocalDate joinedDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.activationDateParamName,
                        element);
                baseDataValidator.reset().parameter(ClientApiConstants.activationDateParamName).value(joinedDate).notNull();
            }
        } else {
            baseDataValidator.reset().parameter(ClientApiConstants.activeParamName).value(active).trueOrFalseRequired(false);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void validateIndividualNamePartsCannotBeUsedWithFullname(final JsonElement element, final DataValidatorBuilder baseDataValidator) {
        final String firstnameParam = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.firstnameParamName, element);
        if (StringUtils.isNotBlank(firstnameParam)) {
            final String fullnameParam = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.fullnameParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.fullnameParamName).value(fullnameParam)
                    .mustBeBlankWhenParameterProvided(ClientApiConstants.firstnameParamName, firstnameParam);
        }

        final String middlenameParam = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.middlenameParamName, element);
        if (StringUtils.isNotBlank(middlenameParam)) {
            final String fullnameParam = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.fullnameParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.fullnameParamName).value(fullnameParam)
                    .mustBeBlankWhenParameterProvided(ClientApiConstants.middlenameParamName, middlenameParam);
        }

        final String lastnameParamName = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.lastnameParamName, element);
        if (StringUtils.isNotBlank(lastnameParamName)) {
            final String fullnameParam = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.fullnameParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.fullnameParamName).value(fullnameParam)
                    .mustBeBlankWhenParameterProvided(ClientApiConstants.lastnameParamName, lastnameParamName);
        }
    }

    private void validateRequiredIndividualNamePartsExist(final JsonElement element, final DataValidatorBuilder baseDataValidator) {
        final String firstnameParam = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.firstnameParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.firstnameParamName).value(firstnameParam).notBlank()
                .notExceedingLengthOf(50);

        final String middlenameParam = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.middlenameParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.middlenameParamName).value(middlenameParam).ignoreIfNull()
                .notExceedingLengthOf(50);

        final String lastnameParamName = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.lastnameParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.lastnameParamName).value(lastnameParamName).notBlank()
                .notExceedingLengthOf(50);
    }

    private void fullnameCannotBeBlank(final JsonElement element, final DataValidatorBuilder baseDataValidator) {
        final String fullnameParam = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.fullnameParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.fullnameParamName).value(fullnameParam).notBlank().notExceedingLengthOf(100);
    }

    private boolean isIndividualNamePartParameterPassed(final JsonElement element) {
        return this.fromApiJsonHelper.parameterExists(ClientApiConstants.firstnameParamName, element)
                || this.fromApiJsonHelper.parameterExists(ClientApiConstants.middlenameParamName, element)
                || this.fromApiJsonHelper.parameterExists(ClientApiConstants.lastnameParamName, element);
    }

    private boolean isFullnameParameterPassed(final JsonElement element) {
        return this.fromApiJsonHelper.parameterExists(ClientApiConstants.fullnameParamName, element);
    }

    private boolean isIndividualNameProvided(final JsonElement element) {
        final String firstname = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.firstnameParamName, element);
        final String middlename = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.middlenameParamName, element);
        final String lastname = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.lastnameParamName, element);

        return StringUtils.isNotBlank(firstname) || StringUtils.isNotBlank(middlename) || StringUtils.isNotBlank(lastname);
    }

    private boolean isFullnameProvided(final JsonElement element) {
        final String fullname = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.fullnameParamName, element);
        return StringUtils.isNotBlank(fullname);
    }

    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ClientApiConstants.CLIENT_UPDATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiConstants.CLIENT_RESOURCE_NAME);

        boolean atLeastOneParameterPassedForUpdate = false;
        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.accountNoParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String accountNo = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.accountNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.accountNoParamName).value(accountNo).notBlank().notExceedingLengthOf(20);
        }

        if (isFullnameProvided(element) || isIndividualNameProvided(element)) {

            // 1. No individual name part provided and fullname provided
            if (isFullnameProvided(element) && !isIndividualNameProvided(element)) {
                fullnameCannotBeBlank(element, baseDataValidator);
            }

            // 2. no fullname provided and individual name part provided
            if (isIndividualNameProvided(element) && !isFullnameProvided(element)) {
                validateRequiredIndividualNamePartsExist(element, baseDataValidator);
            }

            // 3. both provided
            if (isFullnameProvided(element) && isIndividualNameProvided(element)) {
                validateIndividualNamePartsCannotBeUsedWithFullname(element, baseDataValidator);
            }
        } else {

            if (isFullnameParameterPassed(element) || isIndividualNamePartParameterPassed(element)) {

                // 1. No individual name parameter passed and fullname passed
                if (isFullnameParameterPassed(element) && !isIndividualNamePartParameterPassed(element)) {
                    fullnameCannotBeBlank(element, baseDataValidator);
                }

                // 2. no fullname passed and individual name part passed
                if (isIndividualNamePartParameterPassed(element) && !isFullnameParameterPassed(element)) {
                    validateRequiredIndividualNamePartsExist(element, baseDataValidator);
                }

                // 3. both parameter types passed
                if (isFullnameParameterPassed(element) && isIndividualNamePartParameterPassed(element)) {
                    baseDataValidator.reset().parameter(ClientApiConstants.idParamName).failWithCode(".no.name.details.passed");
                }

            } else {
                baseDataValidator.reset().parameter(ClientApiConstants.idParamName).failWithCode(".no.name.details.passed");
            }
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.fullnameParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.lastnameParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.middlenameParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.firstnameParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.externalIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String externalId = this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.externalIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.externalIdParamName).value(externalId).notExceedingLengthOf(100);
        }

        final Boolean active = this.fromApiJsonHelper.extractBooleanNamed(ClientApiConstants.activeParamName, element);
        if (active != null) {
            if (active.booleanValue()) {
                final LocalDate joinedDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.activationDateParamName,
                        element);
                baseDataValidator.reset().parameter(ClientApiConstants.activationDateParamName).value(joinedDate).notNull();
            }
        }

        if (this.fromApiJsonHelper.parameterExists(ClientApiConstants.staffIdParamName, element)) {
            final Long staffId = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.staffIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.staffIdParamName).value(staffId).ignoreIfNull().longGreaterThanZero();
        }

        if (!atLeastOneParameterPassedForUpdate) {
            final Object forceError = null;
            baseDataValidator.reset().anyOfNotNull(forceError);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateActivation(final JsonCommand command) {
        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ClientApiConstants.ACTIVATION_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiConstants.CLIENT_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();

        final LocalDate activationDate = this.fromApiJsonHelper.extractLocalDateNamed(activationDateParamName, element);
        baseDataValidator.reset().parameter(activationDateParamName).value(activationDate).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            //
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }

    public void validateForUnassignStaff(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        final Set<String> supportedParametersUnassignStaff = new HashSet<String>(Arrays.asList(ClientApiConstants.staffIdParamName));

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParametersUnassignStaff);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiConstants.CLIENT_RESOURCE_NAME);

        final String staffIdParameterName = ClientApiConstants.staffIdParamName;
        final Long staffId = this.fromApiJsonHelper.extractLongNamed(staffIdParameterName, element);
        baseDataValidator.reset().parameter(staffIdParameterName).value(staffId).notNull().longGreaterThanZero();

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

    }

    public void validateForAssignStaff(String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();

        final Set<String> supportedParametersUnassignStaff = new HashSet<String>(Arrays.asList(ClientApiConstants.staffIdParamName));

        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParametersUnassignStaff);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiConstants.CLIENT_RESOURCE_NAME);

        final String staffIdParameterName = ClientApiConstants.staffIdParamName;
        final Long staffId = this.fromApiJsonHelper.extractLongNamed(staffIdParameterName, element);
        baseDataValidator.reset().parameter(staffIdParameterName).value(staffId).notNull().longGreaterThanZero();

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

    }
}