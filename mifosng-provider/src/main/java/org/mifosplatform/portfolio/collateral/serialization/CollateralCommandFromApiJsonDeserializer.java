/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.collateral.serialization;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.serialization.AbstractFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.serialization.FromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.collateral.api.CollateralApiConstants.COLLATERAL_JSON_INPUT_PARAMS;
import org.mifosplatform.portfolio.collateral.command.CollateralCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * Implementation of {@link FromApiJsonDeserializer} for
 * {@link CollateralCommand}'s.
 */
@Component
public final class CollateralCommandFromApiJsonDeserializer extends AbstractFromApiJsonDeserializer<CollateralCommand> {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public CollateralCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    @Override
    public CollateralCommand commandFromApiJson(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        final Set<String> supportedParameters = COLLATERAL_JSON_INPUT_PARAMS.getAllValues();
        supportedParameters.add("locale");
        supportedParameters.add("dateFormat");
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final JsonObject topLevelJsonElement = element.getAsJsonObject();
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

        final Long collateralTypeId = this.fromApiJsonHelper.extractLongNamed(COLLATERAL_JSON_INPUT_PARAMS.COLLATERAL_TYPE_ID.getValue(),
                element);

        final Long goldfineTypeId = this.fromApiJsonHelper.extractLongNamed(COLLATERAL_JSON_INPUT_PARAMS.GOLD_FINENESS_TYPE_ID.getValue(),
        		element);
        final Long jewelleryTypeId = this.fromApiJsonHelper.extractLongNamed(COLLATERAL_JSON_INPUT_PARAMS.JEWELLERY_KIND_TYPE_ID.getValue(), 
        		element);
        final Long maketwoTypeId = this.fromApiJsonHelper.extractLongNamed(COLLATERAL_JSON_INPUT_PARAMS.TW_MAKE_TYPE.getValue(), 
        		element);
        final String description = this.fromApiJsonHelper.extractStringNamed(COLLATERAL_JSON_INPUT_PARAMS.DESCRIPTION.getValue(), element);
        final BigDecimal value = this.fromApiJsonHelper.extractBigDecimalNamed(COLLATERAL_JSON_INPUT_PARAMS.VALUE.getValue(), element,
                locale);
        final BigDecimal actualcost = this.fromApiJsonHelper.extractBigDecimalNamed(COLLATERAL_JSON_INPUT_PARAMS.ACTUALCOST.getValue(), element,
                locale);
        final BigDecimal gross = this.fromApiJsonHelper.extractBigDecimalNamed(COLLATERAL_JSON_INPUT_PARAMS.GROSS_WEIGHT.getValue(), element,
        		 locale);
        final BigDecimal impurity = this.fromApiJsonHelper.extractBigDecimalNamed(COLLATERAL_JSON_INPUT_PARAMS.IMPURITY_WEIGHT.getValue(), element,
       		 locale);
        final BigDecimal net = this.fromApiJsonHelper.extractBigDecimalNamed(COLLATERAL_JSON_INPUT_PARAMS.NET_WEIGHT.getValue(), element,
       		 locale);
        final BigDecimal stone = this.fromApiJsonHelper.extractBigDecimalNamed(COLLATERAL_JSON_INPUT_PARAMS.STONE_WT.getValue(), element,
       		 locale);

        return new CollateralCommand(collateralTypeId,goldfineTypeId,jewelleryTypeId,maketwoTypeId, value,actualcost,gross,impurity,net, stone, description);
    }
}