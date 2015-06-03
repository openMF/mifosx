/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.collateral.command;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.portfolio.collateral.api.CollateralApiConstants.COLLATERAL_JSON_INPUT_PARAMS;

/**
 * Immutable command for creating or updating details of a Collateral.
 */
public class CollateralCommand {

    private final Long collateralTypeId;
    private final Long goldfineTypeId;
    private final Long jewelleryTypeId;
    private final Long maketwoTypeId;
    private final BigDecimal value;
    private final BigDecimal actualcost;
    private final BigDecimal gross;
    private final BigDecimal impurity;
    private final BigDecimal net;
    private final BigDecimal stone;
    private final String description;

    public CollateralCommand(final Long collateralTypeId,final Long goldfineTypeId, final Long jewelleryTypeId,final Long maketwoTypeId,final BigDecimal value,final BigDecimal actualcost,final BigDecimal gross,final BigDecimal impurity,final BigDecimal net, final BigDecimal stone, final String description) {
        this.collateralTypeId = collateralTypeId;
        this.goldfineTypeId = goldfineTypeId;
        this.jewelleryTypeId = jewelleryTypeId;
        this.maketwoTypeId = maketwoTypeId;
        this.value = value;
        this.actualcost = actualcost;
        this.gross = gross;
        this.impurity = impurity;
        this.net = net;
        this.stone = stone;
        this.description = description;
    }

    public Long getCollateralTypeId() {
        return this.collateralTypeId;
    }
 
    public Long getgoldfineTypeId(){
    	return this.goldfineTypeId;
    }
    public Long getjewelleryTypeId(){
    	return this.jewelleryTypeId;
    }
    public Long getMakeTwoTypeId(){
    	return this.maketwoTypeId;
    }
    
    public BigDecimal getValue() {
        return this.value;
    }
    public BigDecimal getActualCost(){
    	return this.actualcost;
    }
    public BigDecimal getGross(){
    	return this.gross;
    }
    public BigDecimal getImpurity(){
    	return this.impurity;
    }
    public BigDecimal getNet(){
    	return this.net;
    }
    public BigDecimal getStone(){
    	return this.stone;
    }

    public String getDescription() {
        return this.description;
    }

    public void validateForCreate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("collateral");

        baseDataValidator.reset().parameter(COLLATERAL_JSON_INPUT_PARAMS.COLLATERAL_TYPE_ID.getValue()).value(this.collateralTypeId)
                .notNull().integerGreaterThanZero();
        //baseDataValidator.reset().parameter(COLLATERAL_JSON_INPUT_PARAMS.GOLD_FINENESS_TYPE_ID.getValue()).value(this.goldfineTypeId)
        	//	.notNull().integerGreaterThanZero();
        baseDataValidator.reset().parameter(COLLATERAL_JSON_INPUT_PARAMS.GOLD_FINENESS_TYPE_ID.getValue()).value(this.goldfineTypeId)
         		.ignoreIfNull();
        baseDataValidator.reset().parameter(COLLATERAL_JSON_INPUT_PARAMS.TW_MAKE_TYPE.getValue()).value(this.maketwoTypeId)
 		.ignoreIfNull();
        baseDataValidator.reset().parameter(COLLATERAL_JSON_INPUT_PARAMS.VALUE.getValue()).value(this.value).ignoreIfNull()
                .positiveAmount();
        baseDataValidator.reset().parameter(COLLATERAL_JSON_INPUT_PARAMS.ACTUALCOST.getValue()).value(this.actualcost).ignoreIfNull()
        .positiveAmount();
        baseDataValidator.reset().parameter(COLLATERAL_JSON_INPUT_PARAMS.DESCRIPTION.getValue()).value(this.description).ignoreIfNull()
                .notExceedingLengthOf(500);

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

    public void validateForUpdate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("collateral");

        baseDataValidator.reset().parameter(COLLATERAL_JSON_INPUT_PARAMS.COLLATERAL_TYPE_ID.getValue()).value(this.collateralTypeId)
                .ignoreIfNull().integerGreaterThanZero();
        baseDataValidator.reset().parameter(COLLATERAL_JSON_INPUT_PARAMS.GOLD_FINENESS_TYPE_ID.getValue()).value(this.goldfineTypeId)
        .ignoreIfNull();
        baseDataValidator.reset().parameter(COLLATERAL_JSON_INPUT_PARAMS.TW_MAKE_TYPE.getValue()).value(this.maketwoTypeId)
        .ignoreIfNull();
        baseDataValidator.reset().parameter(COLLATERAL_JSON_INPUT_PARAMS.VALUE.getValue()).value(this.value).ignoreIfNull()
                .positiveAmount();
        baseDataValidator.reset().parameter(COLLATERAL_JSON_INPUT_PARAMS.ACTUALCOST.getValue()).value(this.actualcost).ignoreIfNull()
        .positiveAmount();
        baseDataValidator.reset().parameter(COLLATERAL_JSON_INPUT_PARAMS.DESCRIPTION.getValue()).value(this.description).ignoreIfNull()
                .notExceedingLengthOf(500);

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }
}