/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanproduct.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@Entity
@Table(name = "m_product_loan_configurable_attributes")
public class LoanProductConfigurableAttributes extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "loan_product_id", nullable = true)
    private LoanProduct loanProduct;

    @Column(name = "amortization", nullable = true)
    private Boolean configureAmortization;

    @Column(name = "interest_method", nullable = true)
    private Boolean configureInterestMethod;

    @Column(name = "repayment_strategy", nullable = true)
    private Boolean configureRepaymentStrategy;

    @Column(name = "interest_calculation_period", nullable = true)
    private Boolean configureInterestCalcPeriod;

    @Column(name = "arrears_tolerance", nullable = true)
    private Boolean configureArrearsTolerance;

    @Column(name = "repay_every", nullable = true)
    private Boolean configureRepaymentFrequency;
    
    @Column(name = "moratorium", nullable = true)
    private Boolean configureMoratorium;
    
    @Column(name = "grace_on_arrears_aging", nullable = true)
    private Boolean configureGraceOnArrearsAging;
    
    private static String[] loanConfigurableAttributes = {"configureAmortization","configureInterestMethod","configureRepaymentStrategy",
			"configureInterestCalcPeriod","configureArrearsTolerance","configureRepaymentFrequency","configureMoratorium","configureGraceOnArrearsAging"};
    
	public static LoanProductConfigurableAttributes createFrom(
			JsonArray attributeArray) {

		final Boolean[] values = new Boolean[8];
		
			for (int i = 0; i < values.length; i++) {
				JsonElement attributeElement = attributeArray.get(i);
				if (attributeElement.isJsonObject()) {
					final JsonObject object = attributeElement.getAsJsonObject();

				if (object.has(getAllowedLoanConfigurableAttributes()[i])
						) {
					values[i] = object.get(
							getAllowedLoanConfigurableAttributes()[i])
							.getAsJsonPrimitive().getAsBoolean();
				}
			}
		}
		final Boolean configureAmortization = values[0];
		final Boolean configureInterestMethod = values[1];
		final Boolean configureRepaymentStrategy = values[2];
		final Boolean configureInterestCalcPeriod = values[3];
		final Boolean configureArrearsTolerance = values[4];
		final Boolean configureRepaymentFrequency = values[5];
		final Boolean configureMoratorium = values[6];
		final Boolean configureGraceOnArrearsAging = values[7];

		return new LoanProductConfigurableAttributes(configureAmortization,
				configureInterestMethod, configureRepaymentStrategy,
				configureInterestCalcPeriod, configureArrearsTolerance,
				configureRepaymentFrequency, configureMoratorium,
				configureGraceOnArrearsAging);
	}
		
	 public void updateLoanProduct(final LoanProduct loanProduct) {
	        this.loanProduct = loanProduct;
	    }
          
	 public static LoanProductConfigurableAttributes populateDefaultsForConfigurableAttributes(){
         final Boolean configureAmortization = false;
         final Boolean configureInterestMethod = false;
         final Boolean configureRepaymentStrategy = false;
         final Boolean configureInterestCalcPeriod = false;
         final Boolean configureArrearsTolerance = false;
         final Boolean configureRepaymentFrequency = false;
         final Boolean configureMoratorium = false;
         final Boolean configureGraceOnArrearsAging = false;
     
	return new LoanProductConfigurableAttributes(configureAmortization,configureInterestMethod,configureRepaymentStrategy,
			configureInterestCalcPeriod,configureArrearsTolerance,configureRepaymentFrequency,configureMoratorium,configureGraceOnArrearsAging);
	 }

    public LoanProductConfigurableAttributes(Boolean configureAmortization,Boolean configureInterestMethod,Boolean configureRepaymentStrategy,
    									Boolean configureInterestCalcPeriod,Boolean configureArrearsTolerance,Boolean configureRepaymentFrequency,
    									Boolean configureMoratorium,Boolean configureGraceOnArrearsAging){
    	this.configureAmortization = configureAmortization;
    	this.configureInterestMethod = configureInterestMethod;
    	this.configureArrearsTolerance = configureArrearsTolerance;
    	this.configureGraceOnArrearsAging = configureGraceOnArrearsAging;
    	this.configureInterestCalcPeriod = configureInterestCalcPeriod;
    	this.configureMoratorium = configureMoratorium;
    	this.configureRepaymentFrequency = configureRepaymentFrequency;
    	this.configureRepaymentStrategy = configureRepaymentStrategy;
    }
    
    protected LoanProductConfigurableAttributes(){
    	
    }

	public static String[] getAllowedLoanConfigurableAttributes() {
		return loanConfigurableAttributes;
	}

	public LoanProduct getLoanProduct() {
		return loanProduct;
	}

	public Boolean getConfigureAmortization() {
		return configureAmortization;
	}

	public Boolean getConfigureInterestMethod() {
		return configureInterestMethod;
	}

	public Boolean getConfigureRepaymentStrategy() {
		return configureRepaymentStrategy;
	}

	public Boolean getConfigureInterestCalcPeriod() {
		return configureInterestCalcPeriod;
	}

	public Boolean getConfigureArrearsTolerance() {
		return configureArrearsTolerance;
	}

	public Boolean getConfigureRepaymentFrequency() {
		return configureRepaymentFrequency;
	}

	public Boolean getConfigureMoratorium() {
		return configureMoratorium;
	}

	public Boolean getConfigureGraceOnArrearsAging() {
		return configureGraceOnArrearsAging;
	}

}
