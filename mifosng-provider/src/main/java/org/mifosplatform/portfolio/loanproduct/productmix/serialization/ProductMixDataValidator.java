package org.mifosplatform.portfolio.loanproduct.productmix.serialization;

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
public final class ProductMixDataValidator {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("restrictedProducts"));

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ProductMixDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("productmix");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String[] restrictedProducts = this.fromApiJsonHelper.extractArrayNamed("restrictedProducts", element);
        baseDataValidator.reset().parameter("restrictedProducts").value(restrictedProducts).arrayNotEmpty();
        if (restrictedProducts != null) {
            validateRestrictedProducts(restrictedProducts, baseDataValidator);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void validateRestrictedProducts(final String[] restrictedProducts, final DataValidatorBuilder baseDataValidator) {
        for (final String restrictedId : restrictedProducts) {
            baseDataValidator.reset().parameter("restrictedProduct").value(restrictedId).notBlank().longGreaterThanZero();
        }
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("productmix");

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final String[] restrictedProducts = this.fromApiJsonHelper.extractArrayNamed("restrictedProducts", element);
        validateRestrictedProducts(restrictedProducts, baseDataValidator);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

}
