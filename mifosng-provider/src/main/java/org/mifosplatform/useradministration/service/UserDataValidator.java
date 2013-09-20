/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.useradministration.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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
public final class UserDataValidator {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("username", "firstname", "lastname", "password",
            "repeatPassword", "email", "officeId", "notSelectedRoles", "roles", "sendPasswordToEmail"));

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public UserDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("user");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String username = this.fromApiJsonHelper.extractStringNamed("username", element);
        baseDataValidator.reset().parameter("username").value(username).notBlank().notExceedingLengthOf(100);

        final String firstname = this.fromApiJsonHelper.extractStringNamed("firstname", element);
        baseDataValidator.reset().parameter("firstname").value(firstname).notBlank().notExceedingLengthOf(100);

        final String lastname = this.fromApiJsonHelper.extractStringNamed("lastname", element);
        baseDataValidator.reset().parameter("lastname").value(lastname).notBlank().notExceedingLengthOf(100);

        final Boolean sendPasswordToEmail = this.fromApiJsonHelper.extractBooleanNamed("sendPasswordToEmail", element);
        if (sendPasswordToEmail != null) {
            if (sendPasswordToEmail.booleanValue()) {
                final String email = this.fromApiJsonHelper.extractStringNamed("email", element);
                baseDataValidator.reset().parameter("email").value(email).notBlank().notExceedingLengthOf(100);
            } else {
                final String password = this.fromApiJsonHelper.extractStringNamed("password", element);
                final String repeatPassword = this.fromApiJsonHelper.extractStringNamed("repeatPassword", element);
                baseDataValidator.reset().parameter("password").value(password).notBlank().notExceedingLengthOf(50);
                if (StringUtils.isNotBlank(password)) {
                    baseDataValidator.reset().parameter("password").value(password).equalToParameter("repeatPassword", repeatPassword);
                }
            }
        } else {
            baseDataValidator.reset().parameter("sendPasswordToEmail").value(sendPasswordToEmail).trueOrFalseRequired(false);
        }

        final Long officeId = this.fromApiJsonHelper.extractLongNamed("officeId", element);
        baseDataValidator.reset().parameter("officeId").value(officeId).notNull().integerGreaterThanZero();

        final String[] roles = this.fromApiJsonHelper.extractArrayNamed("roles", element);
        baseDataValidator.reset().parameter("roles").value(roles).arrayNotEmpty();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("user");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        if (this.fromApiJsonHelper.parameterExists("officeId", element)) {
            final Long officeId = this.fromApiJsonHelper.extractLongNamed("officeId", element);
            baseDataValidator.reset().parameter("officeId").value(officeId).notNull().integerGreaterThanZero();
        }

        if (this.fromApiJsonHelper.parameterExists("username", element)) {
            final String username = this.fromApiJsonHelper.extractStringNamed("username", element);
            baseDataValidator.reset().parameter("username").value(username).notBlank().notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists("firstname", element)) {
            final String firstname = this.fromApiJsonHelper.extractStringNamed("firstname", element);
            baseDataValidator.reset().parameter("firstname").value(firstname).notBlank().notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists("lastname", element)) {
            final String lastname = this.fromApiJsonHelper.extractStringNamed("lastname", element);
            baseDataValidator.reset().parameter("lastname").value(lastname).notBlank().notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists("email", element)) {
            final String email = this.fromApiJsonHelper.extractStringNamed("email", element);
            baseDataValidator.reset().parameter("email").value(email).notBlank().notExceedingLengthOf(100);
        }

        if (this.fromApiJsonHelper.parameterExists("roles", element)) {
            final String[] roles = this.fromApiJsonHelper.extractArrayNamed("roles", element);
            baseDataValidator.reset().parameter("roles").value(roles).arrayNotEmpty();
        }

        if (this.fromApiJsonHelper.parameterExists("password", element)) {
            final String password = this.fromApiJsonHelper.extractStringNamed("password", element);
            final String repeatPassword = this.fromApiJsonHelper.extractStringNamed("repeatPassword", element);
            baseDataValidator.reset().parameter("password").value(password).notBlank().notExceedingLengthOf(50);
            if (StringUtils.isNotBlank(password)) {
                baseDataValidator.reset().parameter("password").value(password).equalToParameter("repeatPassword", repeatPassword);
            }
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
}