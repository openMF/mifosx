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

    @Column(name = "amortization_method_enum", nullable = true)
    private Boolean amortizationType;

    @Column(name = "interest_method_enum", nullable = true)
    private Boolean interestType;

    @Column(name = "loan_transaction_strategy_id", nullable = true)
    private Boolean transactionProcessingStrategyId;

    @Column(name = "interest_calculated_in_period_enum", nullable = true)
    private Boolean interestCalculationPeriodType;

    @Column(name = "arrearstolerance_amount", nullable = true)
    private Boolean inArrearsTolerance;

    @Column(name = "repay_every", nullable = true)
    private Boolean repaymentEvery;
    
    @Column(name = "moratorium", nullable = true)
    private Boolean graceOnPrincipalAndInterestPayment;
    
    @Column(name = "grace_on_arrears_ageing", nullable = true)
    private Boolean graceOnArrearsAgeing;
    
    public static String[] supportedloanConfigurableAttributes = {"amortizationType","interestType","transactionProcessingStrategyId",
			"interestCalculationPeriodType","inArrearsTolerance","repaymentEvery","graceOnPrincipalAndInterestPayment","graceOnArrearsAging"};
    
	public static LoanProductConfigurableAttributes createFrom(JsonCommand command) {

		final Boolean amortization = command.parsedJson().getAsJsonObject().getAsJsonObject("allowAttributeOverrides").getAsJsonPrimitive("amortizationType").getAsBoolean();
		final Boolean interestMethod = command.parsedJson().getAsJsonObject().getAsJsonObject("allowAttributeOverrides").getAsJsonPrimitive("interestType").getAsBoolean();
		final Boolean transactionProcessingStrategy = command.parsedJson().getAsJsonObject().getAsJsonObject("allowAttributeOverrides").getAsJsonPrimitive("transactionProcessingStrategyId").getAsBoolean();
		final Boolean interestCalcPeriod = command.parsedJson().getAsJsonObject().getAsJsonObject("allowAttributeOverrides").getAsJsonPrimitive("interestCalculationPeriodType").getAsBoolean();
		final Boolean arrearsTolerance = command.parsedJson().getAsJsonObject().getAsJsonObject("allowAttributeOverrides").getAsJsonPrimitive("inArrearsTolerance").getAsBoolean();
		final Boolean repaymentEvery = command.parsedJson().getAsJsonObject().getAsJsonObject("allowAttributeOverrides").getAsJsonPrimitive("repaymentEvery").getAsBoolean();
		final Boolean graceOnPrincipalAndInterestPayment = command.parsedJson().getAsJsonObject().getAsJsonObject("allowAttributeOverrides").getAsJsonPrimitive("graceOnPrincipalAndInterestPayment").getAsBoolean();
		final Boolean graceOnArrearsAging = command.parsedJson().getAsJsonObject().getAsJsonObject("allowAttributeOverrides").getAsJsonPrimitive("graceOnArrearsAgeing").getAsBoolean();

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
    	this.amortizationType = amortization;
    	this.interestType = interestMethod;
    	this.inArrearsTolerance = arrearsTolerance;
    	this.graceOnArrearsAgeing = graceOnArrearsAging;
    	this.interestCalculationPeriodType = interestCalcPeriod;
    	this.graceOnPrincipalAndInterestPayment = graceOnPrincipalAndInterestPayment;
    	this.repaymentEvery = repaymentEvery;
    	this.transactionProcessingStrategyId = transactionProcessingStrategy;
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
		return amortizationType;
	}

	public Boolean getInterestMethodBoolean() {
		return interestType;
	}

	public Boolean getTransactionProcessingStrategyBoolean() {
		return transactionProcessingStrategyId;
	}

	public Boolean getInterestCalcPeriodBoolean() {
		return interestCalculationPeriodType;
	}

	public Boolean getArrearsToleranceBoolean() {
		return inArrearsTolerance;
	}

	public Boolean getRepaymentEveryBoolean() {
		return repaymentEvery;
	}

	public Boolean getGraceOnPrincipalAndInterestPaymentBoolean() {
		return graceOnPrincipalAndInterestPayment;
	}

	public Boolean getGraceOnArrearsAgingBoolean() {
		return graceOnArrearsAgeing;
	}

	public void setLoanProduct(LoanProduct loanProduct) {
		this.loanProduct = loanProduct;
	}

	public void setAmortizationType(Boolean amortizationType) {
		this.amortizationType = amortizationType;
	}

	public void setInterestType(Boolean interestType) {
		this.interestType = interestType;
	}

	public void setTransactionProcessingStrategyId(
			Boolean transactionProcessingStrategyId) {
		this.transactionProcessingStrategyId = transactionProcessingStrategyId;
	}

	public void setInterestCalculationPeriodType(
			Boolean interestCalculationPeriodType) {
		this.interestCalculationPeriodType = interestCalculationPeriodType;
	}

	public void setInArrearsTolerance(Boolean inArrearsTolerance) {
		this.inArrearsTolerance = inArrearsTolerance;
	}

	public void setRepaymentEvery(Boolean repaymentEvery) {
		this.repaymentEvery = repaymentEvery;
	}

	public void setGraceOnPrincipalAndInterestPayment(
			Boolean graceOnPrincipalAndInterestPayment) {
		this.graceOnPrincipalAndInterestPayment = graceOnPrincipalAndInterestPayment;
	}

	public void setGraceOnArrearsAgeing(Boolean graceOnArrearsAgeing) {
		this.graceOnArrearsAgeing = graceOnArrearsAgeing;
	}

}
