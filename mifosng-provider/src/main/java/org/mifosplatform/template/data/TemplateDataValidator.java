/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.template.data;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TemplateDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public TemplateDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    /*
      validate data sent against supported params for creating
      template
     */
    public void validateForCreateTemplate(final String json){
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, TemplateApiConstants.supportedParams);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(TemplateApiConstants.TEMPLATE_RESOURCE_NAME);

        final String name = this.fromApiJsonHelper.extractStringNamed(TemplateApiConstants.documentName, element);
        baseDataValidator.reset().parameter(TemplateApiConstants.documentName).value(name).notNull().notExceedingLengthOf(100);

        final String entity = this.fromApiJsonHelper.extractStringNamed(TemplateApiConstants.entity, element);
        baseDataValidator.reset().parameter(TemplateApiConstants.entity).value(entity).notNull().notExceedingLengthOf(100);


    }

}
