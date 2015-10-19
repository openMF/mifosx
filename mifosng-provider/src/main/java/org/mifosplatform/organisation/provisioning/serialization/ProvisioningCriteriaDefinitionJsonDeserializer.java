/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.provisioning.serialization;

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
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.organisation.provisioning.constants.ProvisioningCriteriaConstants;
import org.mifosplatform.organisation.provisioning.exception.ProvisioningCriteriaCannotBeCreatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class ProvisioningCriteriaDefinitionJsonDeserializer {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> supportedParameters = new HashSet<>(Arrays.asList("locale", ProvisioningCriteriaConstants.JSON_CRITERIANAME,
            "loanProducts", "provisioningcriteria"));

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ProvisioningCriteriaDefinitionJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new ProvisioningCriteriaCannotBeCreatedException(
                "error.msg.provisioningcriteria.cannot.be.created",
                "criterianame, loanproducts[], provisioningcriteria[] params are missing in the request");

        }
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("provisioningcriteria");
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(element.getAsJsonObject());
        final String name = this.fromApiJsonHelper.extractStringNamed(ProvisioningCriteriaConstants.JSON_CRITERIANAME, element);
        baseDataValidator.reset().parameter(ProvisioningCriteriaConstants.JSON_CRITERIANAME).value(name).notBlank()
                .notExceedingLengthOf(200);

        // if the param present, then we should have the loan product ids. If
        // not we will load all loan products
        if (this.fromApiJsonHelper.parameterExists(ProvisioningCriteriaConstants.JSON_LOANPRODUCTS, element)) {
            JsonArray jsonloanProducts = this.fromApiJsonHelper.extractJsonArrayNamed(ProvisioningCriteriaConstants.JSON_LOANPRODUCTS,
                    element);
            baseDataValidator.reset().parameter(ProvisioningCriteriaConstants.JSON_LOANPRODUCTS).value(jsonloanProducts)
                    .jsonArrayNotEmpty();
            // check for unsupported params
            int i = 0 ;
            for (JsonElement obj : jsonloanProducts) {
                Long productId = this.fromApiJsonHelper.extractLongNamed("id", obj.getAsJsonObject());
                baseDataValidator.reset().parameter(ProvisioningCriteriaConstants.JSON_LOANPRODUCTS)
                .parameterAtIndexArray(ProvisioningCriteriaConstants.JSON_PRODUCT_ID, i + 1).value(productId).notNull()
                .longGreaterThanZero();
                i++ ;
            }
        }

        if (this.fromApiJsonHelper.parameterExists(ProvisioningCriteriaConstants.JSON_PROVISIONING_DEFINITIONS, element)) {
            JsonArray jsonProvisioningCriteria = this.fromApiJsonHelper.extractJsonArrayNamed(
                    ProvisioningCriteriaConstants.JSON_PROVISIONING_DEFINITIONS, element);
            baseDataValidator.reset().parameter(ProvisioningCriteriaConstants.JSON_PROVISIONING_DEFINITIONS)
                    .value(jsonProvisioningCriteria).jsonArrayNotEmpty();
            for (int i = 0; i < jsonProvisioningCriteria.size(); i++) {
                // check for unsupported params
                JsonObject jsonObject = jsonProvisioningCriteria.get(i).getAsJsonObject();
                final Long categoryId = this.fromApiJsonHelper.extractLongNamed(ProvisioningCriteriaConstants.JSON_CATEOGRYID, jsonObject);
                baseDataValidator.reset().parameter(ProvisioningCriteriaConstants.JSON_PROVISIONING_DEFINITIONS)
                        .parameterAtIndexArray(ProvisioningCriteriaConstants.JSON_CATEOGRYID, i + 1).value(categoryId).notNull()
                        .longGreaterThanZero();

                Long minimumAge = this.fromApiJsonHelper.extractLongNamed(ProvisioningCriteriaConstants.JSON_MINIMUM_AGE, jsonObject);
                baseDataValidator.reset().parameter(ProvisioningCriteriaConstants.JSON_PROVISIONING_DEFINITIONS)
                        .parameterAtIndexArray(ProvisioningCriteriaConstants.JSON_MINIMUM_AGE, i + 1).value(minimumAge).notNull()
                        .notLessThanMin(new Integer(0)) ;

                Long maximumAge = this.fromApiJsonHelper.extractLongNamed(ProvisioningCriteriaConstants.JSON_MAXIMUM_AGE, jsonObject);
                baseDataValidator.reset().parameter(ProvisioningCriteriaConstants.JSON_PROVISIONING_DEFINITIONS)
                        .parameterAtIndexArray(ProvisioningCriteriaConstants.JSON_MAXIMUM_AGE, i + 1).value(maximumAge).notNull()
                        .notLessThanMin(new Integer(0));

                BigDecimal provisioningpercentage = this.fromApiJsonHelper.extractBigDecimalNamed(
                        ProvisioningCriteriaConstants.JSON_PROVISIONING_PERCENTAGE, jsonObject, locale);
                baseDataValidator.reset().parameter(ProvisioningCriteriaConstants.JSON_PROVISIONING_DEFINITIONS)
                        .parameterAtIndexArray(ProvisioningCriteriaConstants.JSON_PROVISIONING_PERCENTAGE, i + 1).value(provisioningpercentage)
                        .notNull().zeroOrPositiveAmount();

                Long liabilityAccountId = this.fromApiJsonHelper.extractLongNamed(ProvisioningCriteriaConstants.JSON_LIABILITY_ACCOUNT,
                        jsonObject);
                baseDataValidator.reset().parameter(ProvisioningCriteriaConstants.JSON_PROVISIONING_DEFINITIONS)
                        .parameterAtIndexArray(ProvisioningCriteriaConstants.JSON_LIABILITY_ACCOUNT, i + 1).value(liabilityAccountId)
                        .notNull().longGreaterThanZero() ;

                Long expenseAccountId = this.fromApiJsonHelper.extractLongNamed(ProvisioningCriteriaConstants.JSON_EXPENSE_ACCOUNT,
                        jsonObject);
                baseDataValidator.reset().parameter(ProvisioningCriteriaConstants.JSON_PROVISIONING_DEFINITIONS)
                        .parameterAtIndexArray(ProvisioningCriteriaConstants.JSON_EXPENSE_ACCOUNT, i + 1).value(expenseAccountId).notNull()
                        .longGreaterThanZero() ;
            }
        }

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        // final DataValidatorBuilder baseDataValidator = new
        // DataValidatorBuilder(dataValidationErrors).resource("provisioningcriteria");
        // final JsonElement element = this.fromApiJsonHelper.parse(json);

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

}
