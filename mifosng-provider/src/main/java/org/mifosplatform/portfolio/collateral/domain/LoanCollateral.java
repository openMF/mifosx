/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.collateral.domain;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.portfolio.collateral.api.CollateralApiConstants.COLLATERAL_JSON_INPUT_PARAMS;
import org.mifosplatform.portfolio.collateral.data.CollateralData;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_loan_collateral")
public class LoanCollateral extends AbstractPersistable<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne
    @JoinColumn(name = "type_cv_id", nullable = false)
    private CodeValue type;
    
    @ManyToOne
    @JoinColumn(name = "gold_fine_cv_id", nullable = true)
    private CodeValue goldfine;
    
    @ManyToOne
    @JoinColumn (name = "jewellery_cv_id", nullable = true)
    private CodeValue jewellery;
    
    @ManyToOne
    @JoinColumn (name = "two_cv_id", nullable = false)
    private CodeValue maketwo;
    

    @Column(name = "value", scale = 6, precision = 19)
    private BigDecimal value;
    
    @Column(name = "actualcost", scale = 2, precision = 19)
    private BigDecimal actualcost;
    
    @Column(name = "gross", scale = 2, precision = 19)
    private BigDecimal gross;
    
    @Column(name = "impurity", scale = 2, precision = 19)
    private BigDecimal impurity;
    
    @Column(name = "net", scale = 2, precision = 19)
    private BigDecimal net;
    
    @Column(name = "stone", scale = 2, precision = 19)
    private BigDecimal stone;
    

    @Column(name = "description", length = 500)
    private String description;

	//private CodeValue twoType;

    public static LoanCollateral from(final CodeValue collateralType,final CodeValue goldfineType,final CodeValue jewelleryType, final CodeValue maketwoType,final BigDecimal value,final BigDecimal actualcost, final BigDecimal gross,final BigDecimal impurity, final BigDecimal net, final BigDecimal stone, final String description) {
        return new LoanCollateral(null, collateralType,goldfineType,jewelleryType,maketwoType, value,actualcost,impurity, net, stone,gross, description);
    }

    protected LoanCollateral() {
        //
    }

    private LoanCollateral(final Loan loan, final CodeValue collateralType,final CodeValue goldfineType,final CodeValue jewelleryType,final CodeValue maketwoType, final BigDecimal value,final BigDecimal actualcost, final BigDecimal gross,final BigDecimal impurity, final BigDecimal net, final BigDecimal stone,final String description) {
        this.loan = loan;
        this.type = collateralType;
        this.goldfine = goldfineType;
        this.jewellery = jewelleryType;
        this.maketwo = maketwoType;
        this.value = value;
        this.actualcost = actualcost;
        this.gross = gross;
        this.impurity = impurity;
        this.net = net;
        this.stone = stone;
        this.description = StringUtils.defaultIfEmpty(description, null);
    }

    public void assembleFrom(final CodeValue collateralType,final CodeValue goldfineType,final CodeValue jewelleryType, final CodeValue maketwoType,final BigDecimal value,final BigDecimal actualcost,final BigDecimal gross,final BigDecimal impurity, final BigDecimal net, final BigDecimal stone, final String description) {
        this.type = collateralType;
        this.goldfine = goldfineType;
        this.jewellery = jewelleryType;
        this.maketwo = maketwoType;
        this.description = description;
        this.value = value;
        this.actualcost = actualcost;
        this.gross = gross;
        this.impurity = impurity;
        this.net = net;
        this.stone = stone;
        
    }

    public void associateWith(final Loan loan) {
        this.loan = loan;
    }

    public static LoanCollateral fromJson(final Loan loan, final CodeValue collateralType,final CodeValue goldfineType,final CodeValue jewelleryType,final CodeValue twoType, final JsonCommand command) {
        final String description = command.stringValueOfParameterNamed(COLLATERAL_JSON_INPUT_PARAMS.DESCRIPTION.getValue());
        final BigDecimal value = command.bigDecimalValueOfParameterNamed(COLLATERAL_JSON_INPUT_PARAMS.VALUE.getValue());
        final BigDecimal actualcost = command.bigDecimalValueOfParameterNamed(COLLATERAL_JSON_INPUT_PARAMS.ACTUALCOST.getValue());
        final BigDecimal gross = command.bigDecimalValueOfParameterNamed(COLLATERAL_JSON_INPUT_PARAMS.GROSS_WEIGHT.getValue());
        final BigDecimal impurity = command.bigDecimalValueOfParameterNamed(COLLATERAL_JSON_INPUT_PARAMS.IMPURITY_WEIGHT.getValue());
        final BigDecimal net = command.bigDecimalValueOfParameterNamed(COLLATERAL_JSON_INPUT_PARAMS.NET_WEIGHT.getValue());
        final BigDecimal stone = command.bigDecimalValueOfParameterNamed(COLLATERAL_JSON_INPUT_PARAMS.STONE_WT.getValue());
        return new LoanCollateral(loan, collateralType,goldfineType,jewelleryType,twoType, value,actualcost,gross,impurity,net,stone, description);
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        final String collateralTypeIdParamName = COLLATERAL_JSON_INPUT_PARAMS.COLLATERAL_TYPE_ID.getValue();
        if (command.isChangeInLongParameterNamed(collateralTypeIdParamName, this.type.getId())) {
            final Long newValue = command.longValueOfParameterNamed(collateralTypeIdParamName);
            actualChanges.put(collateralTypeIdParamName, newValue);
        }
        
     
        final String goldfineTypeIdParamName = COLLATERAL_JSON_INPUT_PARAMS.GOLD_FINENESS_TYPE_ID.getValue();
        if (command.isChangeInLongParameterNamed(goldfineTypeIdParamName, this.goldfine.getId())) {
            final Long newValue = command.longValueOfParameterNamed(goldfineTypeIdParamName);
            actualChanges.put(goldfineTypeIdParamName, newValue);
        }

        final String jewelleryTypeIdParamName = COLLATERAL_JSON_INPUT_PARAMS.JEWELLERY_KIND_TYPE_ID.getValue();
        if (command.isChangeInLongParameterNamed(jewelleryTypeIdParamName, this.jewellery.getId())) {
            final Long newValue = command.longValueOfParameterNamed(jewelleryTypeIdParamName);
            actualChanges.put(jewelleryTypeIdParamName, newValue);
        }
        final String maketwoTypeIdParamName = COLLATERAL_JSON_INPUT_PARAMS.TW_MAKE_TYPE.getValue();
        if (command.isChangeInLongParameterNamed(maketwoTypeIdParamName, this.maketwo.getId())) {
            final Long newValue = command.longValueOfParameterNamed(maketwoTypeIdParamName);
            actualChanges.put(maketwoTypeIdParamName, newValue);
        }
        
        final String descriptionParamName = COLLATERAL_JSON_INPUT_PARAMS.DESCRIPTION.getValue();
        if (command.isChangeInStringParameterNamed(descriptionParamName, this.description)) {
            final String newValue = command.stringValueOfParameterNamed(descriptionParamName);
            actualChanges.put(descriptionParamName, newValue);
            this.description = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String valueParamName = COLLATERAL_JSON_INPUT_PARAMS.VALUE.getValue();
        if (command.isChangeInBigDecimalParameterNamed(valueParamName, this.value)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(valueParamName);
            actualChanges.put(valueParamName, newValue);
            this.value = newValue;
        }
        final String actualcostParamName = COLLATERAL_JSON_INPUT_PARAMS.ACTUALCOST.getValue();
        if (command.isChangeInBigDecimalParameterNamed(valueParamName, this.actualcost)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(actualcostParamName);
            actualChanges.put(actualcostParamName, newValue);
            this.value = newValue;
        }
        final String grossParamName = COLLATERAL_JSON_INPUT_PARAMS.GROSS_WEIGHT.getValue();
        if (command.isChangeInBigDecimalParameterNamed(grossParamName, this.gross)){
        	final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(grossParamName);
        	actualChanges.put(grossParamName, newValue);
        	this.gross = newValue;
        }
        final String impurityParamName = COLLATERAL_JSON_INPUT_PARAMS.IMPURITY_WEIGHT.getValue();
        if (command.isChangeInBigDecimalParameterNamed(impurityParamName, this.impurity)){
        	final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(impurityParamName);
        	actualChanges.put(impurityParamName, newValue);
        	this.impurity = newValue;
        }
        final String netParamName = COLLATERAL_JSON_INPUT_PARAMS.NET_WEIGHT.getValue();
        if (command.isChangeInBigDecimalParameterNamed(netParamName, this.net)){
        	final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(netParamName);
        	actualChanges.put(netParamName, newValue);
        	this.net = newValue;
        }
        final String stoneParamName = COLLATERAL_JSON_INPUT_PARAMS.STONE_WT.getValue();
        if (command.isChangeInBigDecimalParameterNamed(stoneParamName, this.stone)){
        	final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(stoneParamName);
        	actualChanges.put(stoneParamName, newValue);
        	this.stone = newValue;
        }

        return actualChanges;
    }

    public CollateralData toData() {
        final CodeValueData typeData = this.type.toData();
        final CodeValueData goldfineData = this.type.toData();
        final CodeValueData jewelleryData = this.jewellery.toData();
        final CodeValueData maketwoData = this.maketwo.toData();
        return CollateralData.instance(getId(), typeData,goldfineData,jewelleryData,maketwoData, this.value,this.actualcost,this.gross,this.impurity,this.net,this.stone, this.description, null);
    }

    public void setCollateralType(final CodeValue type) {
        this.type = type;
    }
    
   
    public void setgoldfineType(final CodeValue goldfine) {
        this.goldfine = goldfine;
    }
    
    public void setJewellery(CodeValue jewellery) {
		this.jewellery = jewellery;
	}
    public void setmaketwoType(final CodeValue maketwo){
    	this.maketwo = maketwo;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        final LoanCollateral rhs = (LoanCollateral) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)) //
                .append(getId(), rhs.getId()) //
                .append(this.type.getId(), rhs.type.getId()) //
                .append(this.goldfine.getId(), rhs.goldfine.getId())//
                .append(this.jewellery.getId(), rhs.jewellery.getId())//
                .append(this.maketwo.getId(), rhs.maketwo.getId())//
                .append(this.description, rhs.description) //
                .append(this.value, this.value)//
                .append(this.actualcost, this.actualcost)//
                .append(this.gross, this.gross)//
                .append(this.impurity, this.impurity)//
                .append(this.net, this.net)//
                .append(this.stone, this.stone)//
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(3, 5) //
                .append(getId()) //
                .append(this.type.getId()) //
                .append(this.goldfine.getId())//
                .append(this.maketwo.getId())//
                .append(this.description) //
                .append(this.value)//
                .append(this.actualcost)//
                .append(this.gross)//
                .append(this.impurity)//
                .append(this.net)//
                .append(this.stone)//
                .toHashCode();
    }
}