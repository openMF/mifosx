package org.openmf.mifos.dataimport.dto;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class LoanProduct {

	@SerializedName("id")
    private final Integer id;
    
	@SerializedName("name")
    private final String name;
	
	@SerializedName("fundName")
	private final String fundName;
	
	@SerializedName("principal")
	private final Integer principal;
	
	@SerializedName("minPrincipal")
	private final Integer minPrincipal;
	
	@SerializedName("maxPrincipal")
	private final Integer maxPrincipal;
	
	@SerializedName("numberOfRepayments")
	private final Integer numberOfRepayments;
	
	@SerializedName("minNumberOfRepayments")
	private final Integer minNumberOfRepayments;

	@SerializedName("maxNumberOfRepayments")
	private final Integer maxNumberOfRepayments;
	
	@SerializedName("repaymentEvery")
	private final Integer repaymentEvery;
	
	@SerializedName("repaymentFrequencyType")
	private final Type repaymentFrequencyType;
	
	@SerializedName("interestRatePerPeriod")
	private final Integer interestRatePerPeriod;
	
	@SerializedName("minInterestRatePerPeriod")
	private final Integer minInterestRatePerPeriod;
	
	@SerializedName("maxInterestRatePerPeriod")
	private final Integer maxInterestRatePerPeriod;
	
	@SerializedName("interestRateFrequencyType")
	private final Type interestRateFrequencyType;
	
	@SerializedName("amortizationType")
	private final Type amortizationType;
	
	@SerializedName("interestType")
	private final Type interestType;
	
	@SerializedName("interestCalculationPeriodType")
	private final Type interestCalculationPeriodType;
	
	@SerializedName("inArrearsTolerance")
	private final Integer inArrearsTolerance;
	
	@SerializedName("transactionProcessingStrategyName")
	private final String transactionProcessingStrategyName;
	
	@SerializedName("graceOnPrincipalPayment")
	private final Integer graceOnPrincipalPayment;
	
	@SerializedName("graceOnInterestPayment")
	private final Integer graceOnInterestPayment;
	
	@SerializedName("graceOnInterestCharged")
	private final Integer graceOnInterestCharged;
	
	@SerializedName("status")
	private final String status;
	
	@SerializedName("startDate")
    private final ArrayList<Integer> startDate;
	
	@SerializedName("closeDate")
    private final ArrayList<Integer> closeDate;
	
	public LoanProduct(Integer id, String name, String fundName, String status, Integer principal, Integer minPrincipal, Integer maxPrincipal, Integer numberOfRepayments, Integer minNumberOfRepayments,
			Integer maxNumberOfRepayments, Integer repaymentEvery, Type repaymentFrequencyType, Integer interestRatePerPeriod, Integer minInterestRatePerPeriod, Integer maxInterestRatePerPeriod,
			Type interestRateFrequencyType, Type amortizationType, Type interestType, Type interestCalculationPeriodType, Integer inArrearsTolerance, String transactionProcessingStrategyName,
			Integer graceOnPrincipalPayment, Integer graceOnInterestPayment, Integer graceOnInterestCharged, ArrayList<Integer> startDate, ArrayList<Integer> closeDate) {
		this.id = id;
		this.name = name;
		this.fundName = fundName;
		this.status = status;
		this.principal = principal;
		this.minPrincipal = minPrincipal;
		this.maxPrincipal = maxPrincipal;
		this.numberOfRepayments = numberOfRepayments;
		this.minNumberOfRepayments = minNumberOfRepayments;
		this.maxNumberOfRepayments = maxNumberOfRepayments;
		this.repaymentEvery = repaymentEvery;
		this.repaymentFrequencyType = repaymentFrequencyType;
		this.interestRatePerPeriod = interestRatePerPeriod;
		this.minInterestRatePerPeriod = minInterestRatePerPeriod;
		this.maxInterestRatePerPeriod = maxInterestRatePerPeriod;
	    this.interestRateFrequencyType = interestRateFrequencyType;
	    this.amortizationType = amortizationType;
	    this.interestType = interestType;
	    this.interestCalculationPeriodType = interestCalculationPeriodType;
	    this.inArrearsTolerance = inArrearsTolerance;
	    this.transactionProcessingStrategyName = transactionProcessingStrategyName;
	    this.graceOnPrincipalPayment = graceOnPrincipalPayment;
	    this.graceOnInterestPayment = graceOnInterestPayment;
	    this.graceOnInterestCharged = graceOnInterestCharged;
	    this.startDate = startDate;
	    this.closeDate = closeDate;
	}
	
	
	
	public Integer getId() {
    	return this.id;
    }

    public String getName() {
        return this.name;
    }
    
    public String getFundName() {
        return this.fundName;
    }
    
    public String getStatus() {
    	return this.status;
    }
    
    public Integer getPrincipal() {
    	return this.principal;
    }
    
    public Integer getMinPrincipal() {
    	return this.minPrincipal;
    }
    
    public Integer getMaxPrincipal() {
    	return this.maxPrincipal;
    }
    
    public Integer getNumberOfRepayments() {
    	return this.numberOfRepayments;
    }
    
    public Integer getMinNumberOfRepayments() {
    	return this.minNumberOfRepayments;
    }
    
    public Integer getMaxNumberOfRepayments() {
    	return this.maxNumberOfRepayments;
    }
    
    public Integer getRepaymentEvery() {
    	return this.repaymentEvery;
    }
    
    public Type getRepaymentFrequencyType() {
    	return this.repaymentFrequencyType;
    }
    
    public Integer getInterestRatePerPeriod() {
    	return this.interestRatePerPeriod;
    }
    
    public Integer getMinInterestRatePerPeriod() {
    	return this.minInterestRatePerPeriod;
    }
    
    public Integer getMaxInterestRatePerPeriod() {
    	return this.maxInterestRatePerPeriod;
    }
    
    public Type getInterestRateFrequencyType() {
    	return this.interestRateFrequencyType;
    }
    
    public Type getAmortizationType() {
    	return this.amortizationType;
    }
	
	public Type getInterestType() {
    	return this.interestType;
    }
	
	public Type getInterestCalculationPeriodType() {
    	return this.interestCalculationPeriodType;
    }
	
	public Integer getInArrearsTolerance() {
    	return this.inArrearsTolerance;
    }
	
	public String getTransactionProcessingStrategyName() {
        return this.transactionProcessingStrategyName;
    }
	
	public Integer getGraceOnPrincipalPayment() {
    	return this.graceOnPrincipalPayment;
    }
	
	public Integer getGraceOnInterestPayment() {
    	return this.graceOnInterestPayment;
    }
	
	public Integer getGraceOnInterestCharged() {
		return this.graceOnInterestCharged;
	}
	
	public ArrayList<Integer> getStartDate() {
        return this.startDate;
    }
	
	public ArrayList<Integer> getCloseDate() {
        return this.closeDate;
    }
}
