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

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_product_loan_configurable_attributes")
public class LoanProductConfigurableAttributes extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "loan_product_id", nullable = true)
    private LoanProduct loanProduct;

    @Column(name = "amortization", nullable = true)
    private Boolean amortization;

    @Column(name = "interest_method", nullable = true)
    private Boolean interestMethod;

    @Column(name = "repayment_strategy", nullable = true)
    private Boolean transactionProcessingStrategy;

    @Column(name = "interest_calculation_period", nullable = true)
    private Boolean interestCalcPeriod;

    @Column(name = "arrears_tolerance", nullable = true)
    private Boolean arrearsTolerance;

    @Column(name = "repay_every", nullable = true)
    private Boolean repaymentEvery;
    
    @Column(name = "moratorium", nullable = true)
    private Boolean graceOnPrincipalAndInterestPayment;
    
    @Column(name = "grace_on_arrears_aging", nullable = true)
    private Boolean graceOnArrearsAging;
    
    public static String[] supportedloanConfigurableAttributes = {"amortization","interestMethod","transactionProcessingStrategy",
			"interestCalcPeriod","arrearsTolerance","repaymentEvery","graceOnPrincipalAndInterestPayment","graceOnArrearsAging"};
    
	public static LoanProductConfigurableAttributes createFrom(JsonCommand command) {

		final Boolean amortization = command.parsedJson().getAsJsonObject().getAsJsonObject("allowAttributeOverrides").getAsJsonPrimitive("amortization").getAsBoolean();
		final Boolean interestMethod = command.parsedJson().getAsJsonObject().getAsJsonObject("allowAttributeOverrides").getAsJsonPrimitive("interestMethod").getAsBoolean();
		final Boolean transactionProcessingStrategy = command.parsedJson().getAsJsonObject().getAsJsonObject("allowAttributeOverrides").getAsJsonPrimitive("transactionProcessingStrategy").getAsBoolean();
		final Boolean interestCalcPeriod = command.parsedJson().getAsJsonObject().getAsJsonObject("allowAttributeOverrides").getAsJsonPrimitive("interestCalcPeriod").getAsBoolean();
		final Boolean arrearsTolerance = command.parsedJson().getAsJsonObject().getAsJsonObject("allowAttributeOverrides").getAsJsonPrimitive("arrearsTolerance").getAsBoolean();
		final Boolean repaymentEvery = command.parsedJson().getAsJsonObject().getAsJsonObject("allowAttributeOverrides").getAsJsonPrimitive("repaymentEvery").getAsBoolean();
		final Boolean graceOnPrincipalAndInterestPayment = command.parsedJson().getAsJsonObject().getAsJsonObject("allowAttributeOverrides").getAsJsonPrimitive("graceOnPrincipalAndInterestPayment").getAsBoolean();
		final Boolean graceOnArrearsAging = command.parsedJson().getAsJsonObject().getAsJsonObject("allowAttributeOverrides").getAsJsonPrimitive("graceOnArrearsAging").getAsBoolean();

		return new LoanProductConfigurableAttributes(amortization,
				interestMethod, transactionProcessingStrategy,
				interestCalcPeriod, arrearsTolerance,
				repaymentEvery, graceOnPrincipalAndInterestPayment,
				graceOnArrearsAging);
	}
		
	 public void updateLoanProduct(final LoanProduct loanProduct) {
	        this.loanProduct = loanProduct;
	    }
          
	 public static LoanProductConfigurableAttributes populateDefaultsForConfigurableAttributes(){
         final Boolean amortization = true;
         final Boolean interestMethod = true;
         final Boolean transactionProcessingStrategy = true;
         final Boolean interestCalcPeriod = true;
         final Boolean arrearsTolerance = true;
         final Boolean repaymentEvery = true;
         final Boolean graceOnPrincipalAndInterestPayment = true;
         final Boolean graceOnArrearsAging = true;
     
	return new LoanProductConfigurableAttributes(amortization,interestMethod,transactionProcessingStrategy,
			interestCalcPeriod,arrearsTolerance,repaymentEvery,graceOnPrincipalAndInterestPayment,graceOnArrearsAging);
	 }

    public LoanProductConfigurableAttributes(Boolean amortization,Boolean interestMethod,Boolean transactionProcessingStrategy,
    									Boolean interestCalcPeriod,Boolean arrearsTolerance,Boolean repaymentEvery,
    									Boolean graceOnPrincipalAndInterestPayment,Boolean graceOnArrearsAging){
    	this.amortization = amortization;
    	this.interestMethod = interestMethod;
    	this.arrearsTolerance = arrearsTolerance;
    	this.graceOnArrearsAging = graceOnArrearsAging;
    	this.interestCalcPeriod = interestCalcPeriod;
    	this.graceOnPrincipalAndInterestPayment = graceOnPrincipalAndInterestPayment;
    	this.repaymentEvery = repaymentEvery;
    	this.transactionProcessingStrategy = transactionProcessingStrategy;
    }
    
    protected LoanProductConfigurableAttributes(){
    	
    }

	public static String[] getAllowedLoanConfigurableAttributes() {
		return supportedloanConfigurableAttributes;
	}

	public LoanProduct getLoanProduct() {
		return loanProduct;
	}

	public Boolean getAmortizationBoolean() {
		return amortization;
	}

	public Boolean getInterestMethodBoolean() {
		return interestMethod;
	}

	public Boolean getTransactionProcessingStrategyBoolean() {
		return transactionProcessingStrategy;
	}

	public Boolean getInterestCalcPeriodBoolean() {
		return interestCalcPeriod;
	}

	public Boolean getArrearsToleranceBoolean() {
		return arrearsTolerance;
	}

	public Boolean getRepaymentEveryBoolean() {
		return repaymentEvery;
	}

	public Boolean getGraceOnPrincipalAndInterestPaymentBoolean() {
		return graceOnPrincipalAndInterestPayment;
	}

	public Boolean getGraceOnArrearsAgingBoolean() {
		return graceOnArrearsAging;
	}

}
