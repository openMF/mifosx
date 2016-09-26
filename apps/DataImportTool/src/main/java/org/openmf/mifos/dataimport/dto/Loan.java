package org.openmf.mifos.dataimport.dto;

import java.util.Locale;

public class Loan {

	private final transient Integer rowIndex;
	
	private final transient String status;
	
	private final String clientId;
	
	private final String productId;
	
	private final String loanOfficerId;
	
	private final String fundId;
	
	private final String submittedOnDate;
	
	private final String principal;
	
    private final String numberOfRepayments;
	
	private final String repaymentEvery;
	
	private final String repaymentFrequencyType;
	
    private final String loanTermFrequency;
	
	private final String loanTermFrequencyType;
	
	private final String interestRatePerPeriod;
	
	private final String expectedDisbursementDate;
	
	private final String amortizationType;
	
	private final String interestType;
	
	private final String interestCalculationPeriodType;
	
	private final String inArrearsTolerance;
	
	private final String transactionProcessingStrategyId;
	
	private final String graceOnInterestCharged;
	
	private final String graceOnInterestPayment;
	
	private final String graceOnPrincipalPayment;
	
	private final String interestChargedFromDate;
	
	private final String repaymentsStartingFromDate;
	
	private final String dateFormat;
	
	private final Locale locale;
	
	private final String loanType;
	
	public Loan( String clientId, String productId, String loanOfficerId, String submittedOnDate, String fundId, String principal, String numberOfRepayments, String repaymentEvery,
			String repaymentFrequencyType,  String loanTermFrequency, String loanTermFrequencyType, String interestRatePerPeriod, String expectedDisbursementDate, String amortizationType,
			String interestType, String interestCalculationPeriodType, String inArrearsTolerance, String transactionProcessingStrategyId, String graceOnPrincipalPayment,
			String graceOnInterestPayment, String graceOnInterestCharged, String interestChargedFromDate, String repaymentsStartingFromDate, Integer rowIndex, String status) {
		this.amortizationType = amortizationType;
		this.clientId = clientId;
		this.expectedDisbursementDate = expectedDisbursementDate;
		this.fundId = fundId;
		this.graceOnInterestCharged = graceOnInterestCharged;
		this.graceOnInterestPayment = graceOnInterestPayment;
		this.graceOnPrincipalPayment = graceOnPrincipalPayment;
		this.inArrearsTolerance = inArrearsTolerance;
		this.interestCalculationPeriodType = interestCalculationPeriodType;
		this.interestChargedFromDate = interestChargedFromDate;
		this.interestRatePerPeriod = interestRatePerPeriod;
		this.interestType = interestType;
		this.loanOfficerId = loanOfficerId;
		this.loanTermFrequency = loanTermFrequency;
		this.loanTermFrequencyType = loanTermFrequencyType;
		this.numberOfRepayments = numberOfRepayments;
		this.principal = principal;
		this.productId = productId;
		this.repaymentEvery = repaymentEvery;
		this.repaymentFrequencyType = repaymentFrequencyType;
		this.repaymentsStartingFromDate = repaymentsStartingFromDate;
		this.submittedOnDate = submittedOnDate;
		this.transactionProcessingStrategyId = transactionProcessingStrategyId;
		this.dateFormat = "dd MMMM yyyy";
		this.locale = Locale.ENGLISH;
		this.loanType = "individual";
		this.rowIndex = rowIndex;
		this.status = status;
	}
	
	public String getAmortizationType() {
		return amortizationType;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public String getExpectedDisbursementDate() {
		return expectedDisbursementDate;
	}
	
	public String getFundId() {
		return fundId;
	}
	
	public String getGraceOnInterestCharged() {
		return graceOnInterestCharged;
	}
	
	public String getGraceOnInterestPayment() {
		return graceOnInterestPayment;
	}
	
	public String getGraceOnPrincipalPayment() {
		return graceOnPrincipalPayment;
	}
	
	public String getInArrearsTolerance() {
		return inArrearsTolerance;
	}
	
	public String getInterestCalculationPeriodType() {
		return interestCalculationPeriodType;
	}
	
	public String getInterestChargedFromDate() {
		return interestChargedFromDate;
	}
	
	public String getInterestRatePerPeriod() {
		return interestRatePerPeriod;
	}
	
	public String getInterestType() {
		return interestType;
	}
	
	public String getLoanOfficerId() {
		return loanOfficerId;
	}
	
	public String getLoanTermFrequencyType() {
		return loanTermFrequencyType;
	}
	
	public String getLoanTermFrequency() {
		return loanTermFrequency;
	}
	
	public String getLoanType() {
		return loanType;
	}
	
	public String getNumberOfRepayments() {
		return numberOfRepayments;
	}
	
	public String getPrincipal() {
		return principal;
	}
	
	public String getProductId() {
		return productId;
	}
	
	public String getRepaymentEvery() {
		return repaymentEvery;
	}
	
	public String getRepaymentFrequencyType() {
		return repaymentFrequencyType;
	}
	
	public String getRepaymentsStartingFromDate() {
		return repaymentsStartingFromDate;
	}
	
	public String getSubmittedOnDate() {
		return submittedOnDate;
	}
	
	public String getTransactionProcessingStrategyId() {
		return transactionProcessingStrategyId;
	}
	
	public String getDateFormat() {
		return dateFormat;
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	public Integer getRowIndex() {
        return rowIndex;
    }
	
	public String getStatus() {
        return status;
    }
	
}
