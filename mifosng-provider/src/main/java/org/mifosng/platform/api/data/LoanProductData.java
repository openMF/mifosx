package org.mifosng.platform.api.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;

public class LoanProductData {

	private Long id;
	private String name;
	private String description;

	private Long fundId;
	private String fundName;
	
	private Long transactionProcessingStrategyId;
	private String transactionProcessingStrategyName;

	private CurrencyData currency;
	private BigDecimal principal;
	private BigDecimal inArrearsTolerance;

	private Integer numberOfRepayments;
	private Integer loanTermFrequency;
	private Integer repaymentEvery;
	private BigDecimal interestRatePerPeriod = BigDecimal.ZERO;
	private BigDecimal annualInterestRate = BigDecimal.ZERO;

	private EnumOptionData loanTermFrequencyType;
	private EnumOptionData repaymentFrequencyType;
	private EnumOptionData interestRateFrequencyType;
	private EnumOptionData amortizationType;
	private EnumOptionData interestType;
	private EnumOptionData interestCalculationPeriodType;

    private Collection<ChargeData> charges;

	private DateTime createdOn;
	private DateTime lastModifedOn;

	private List<CurrencyData> currencyOptions = new ArrayList<CurrencyData>();
	private Collection<FundData> fundOptions = new ArrayList<FundData>();
	private Collection<TransactionProcessingStrategyData> transactionProcessingStrategyOptions = new ArrayList<TransactionProcessingStrategyData>();
	private List<EnumOptionData> amortizationTypeOptions = new ArrayList<EnumOptionData>();
	private List<EnumOptionData> interestTypeOptions = new ArrayList<EnumOptionData>();
	private List<EnumOptionData> interestCalculationPeriodTypeOptions = new ArrayList<EnumOptionData>();
	private List<EnumOptionData> repaymentFrequencyTypeOptions = new ArrayList<EnumOptionData>();
	private List<EnumOptionData> interestRateFrequencyTypeOptions = new ArrayList<EnumOptionData>();
	private List<EnumOptionData> loanTermFrequencyTypeOptions = new ArrayList<EnumOptionData>();
    private Collection<ChargeData> chargeOptions = new ArrayList<ChargeData>();

	public LoanProductData() {
		//
	}

	public LoanProductData(DateTime createdOn, DateTime lastModifedOn, Long id,
			String name, String description, MoneyData principal,
			MoneyData tolerance, Integer numberOfRepayments,
			Integer loanTermFrequency,
			Integer repaymentEvery, BigDecimal interestRatePerPeriod,
			BigDecimal annualInterestRate,
			EnumOptionData loanTermFrequencyType,
			EnumOptionData repaymentFrequencyType,
			EnumOptionData interestRateFrequencyType,
			EnumOptionData amortizationType, EnumOptionData interestType,
			EnumOptionData interestCalculationPeriodType, Long fundId,
			String fundName, Long transactionProcessingStrategyId, String transactionProcessingStrategyName) {
		this.createdOn = createdOn;
		this.lastModifedOn = lastModifedOn;
		this.id = id;
		this.name = name;
		this.description = description;
		this.currency = new CurrencyData(principal.getCurrencyCode(), principal.getDefaultName(), principal.getDigitsAfterDecimal(), principal.getDisplaySymbol(), principal.getNameCode());
		this.principal = principal.getAmount();
		this.inArrearsTolerance = tolerance.getAmount();
		this.numberOfRepayments = numberOfRepayments;
		this.loanTermFrequency = loanTermFrequency;
		this.repaymentEvery = repaymentEvery;
		this.interestRatePerPeriod = interestRatePerPeriod;
		this.annualInterestRate = annualInterestRate;
		this.loanTermFrequencyType = loanTermFrequencyType;
		this.repaymentFrequencyType = repaymentFrequencyType;
		this.interestRateFrequencyType = interestRateFrequencyType;
		this.amortizationType = amortizationType;
		this.interestType = interestType;
		this.interestCalculationPeriodType = interestCalculationPeriodType;
		this.fundId = fundId;
		this.fundName = fundName;
		this.transactionProcessingStrategyId = transactionProcessingStrategyId;
		this.transactionProcessingStrategyName = transactionProcessingStrategyName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getFundId() {
		return fundId;
	}

	public void setFundId(Long fundId) {
		this.fundId = fundId;
	}

	public String getFundName() {
		return fundName;
	}

	public void setFundName(String fundName) {
		this.fundName = fundName;
	}
	
	public Long getTransactionProcessingStrategyId() {
		return transactionProcessingStrategyId;
	}

	public void setTransactionProcessingStrategyId(Long transactionProcessingStrategyId) {
		this.transactionProcessingStrategyId = transactionProcessingStrategyId;
	}

	public String getTransactionProcessingStrategyName() {
		return transactionProcessingStrategyName;
	}

	public void setTransactionProcessingStrategyName(
			String transactionProcessingStrategyName) {
		this.transactionProcessingStrategyName = transactionProcessingStrategyName;
	}
	
	public CurrencyData getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyData currency) {
		this.currency = currency;
	}

	public BigDecimal getPrincipal() {
		return principal;
	}

	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}

	public BigDecimal getInArrearsTolerance() {
		return inArrearsTolerance;
	}

	public void setInArrearsTolerance(BigDecimal inArrearsTolerance) {
		this.inArrearsTolerance = inArrearsTolerance;
	}

	public Integer getNumberOfRepayments() {
		return numberOfRepayments;
	}

	public void setNumberOfRepayments(Integer numberOfRepayments) {
		this.numberOfRepayments = numberOfRepayments;
	}
	
	public Integer getLoanTermFrequency() {
		return loanTermFrequency;
	}

	public void setLoanTermFrequency(Integer loanTermFrequency) {
		this.loanTermFrequency = loanTermFrequency;
	}

	public Integer getRepaymentEvery() {
		return repaymentEvery;
	}

	public void setRepaymentEvery(Integer repaymentEvery) {
		this.repaymentEvery = repaymentEvery;
	}

	public BigDecimal getInterestRatePerPeriod() {
		return interestRatePerPeriod;
	}

	public void setInterestRatePerPeriod(BigDecimal interestRatePerPeriod) {
		this.interestRatePerPeriod = interestRatePerPeriod;
	}

	public BigDecimal getAnnualInterestRate() {
		return annualInterestRate;
	}

	public void setAnnualInterestRate(BigDecimal annualInterestRate) {
		this.annualInterestRate = annualInterestRate;
	}

	public DateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(DateTime createdOn) {
		this.createdOn = createdOn;
	}

	public DateTime getLastModifedOn() {
		return lastModifedOn;
	}

	public void setLastModifedOn(DateTime lastModifedOn) {
		this.lastModifedOn = lastModifedOn;
	}

	public EnumOptionData getRepaymentFrequencyType() {
		return repaymentFrequencyType;
	}

	public void setRepaymentFrequencyType(EnumOptionData repaymentFrequencyType) {
		this.repaymentFrequencyType = repaymentFrequencyType;
	}

	public EnumOptionData getInterestRateFrequencyType() {
		return interestRateFrequencyType;
	}

	public void setInterestRateFrequencyType(
			EnumOptionData interestRateFrequencyType) {
		this.interestRateFrequencyType = interestRateFrequencyType;
	}

	public EnumOptionData getAmortizationType() {
		return amortizationType;
	}

	public void setAmortizationType(EnumOptionData amortizationType) {
		this.amortizationType = amortizationType;
	}

	public EnumOptionData getInterestType() {
		return interestType;
	}

	public void setInterestType(EnumOptionData interestType) {
		this.interestType = interestType;
	}

	public EnumOptionData getInterestCalculationPeriodType() {
		return interestCalculationPeriodType;
	}

	public void setInterestCalculationPeriodType(
			EnumOptionData interestCalculationPeriodType) {
		this.interestCalculationPeriodType = interestCalculationPeriodType;
	}

	public List<EnumOptionData> getRepaymentFrequencyTypeOptions() {
		return repaymentFrequencyTypeOptions;
	}

	public void setRepaymentFrequencyTypeOptions(
			List<EnumOptionData> repaymentFrequencyTypeOptions) {
		this.repaymentFrequencyTypeOptions = repaymentFrequencyTypeOptions;
	}

	public List<EnumOptionData> getInterestRateFrequencyTypeOptions() {
		return interestRateFrequencyTypeOptions;
	}

	public void setInterestRateFrequencyTypeOptions(
			List<EnumOptionData> interestRateFrequencyTypeOptions) {
		this.interestRateFrequencyTypeOptions = interestRateFrequencyTypeOptions;
	}
	
	public EnumOptionData getLoanTermFrequencyType() {
		return loanTermFrequencyType;
	}

	public void setLoanTermFrequencyType(EnumOptionData loanTermFrequencyType) {
		this.loanTermFrequencyType = loanTermFrequencyType;
	}

	public List<EnumOptionData> getLoanTermFrequencyTypeOptions() {
		return loanTermFrequencyTypeOptions;
	}

	public void setLoanTermFrequencyTypeOptions(
			List<EnumOptionData> loanTermFrequencyTypeOptions) {
		this.loanTermFrequencyTypeOptions = loanTermFrequencyTypeOptions;
	}

	public List<CurrencyData> getCurrencyOptions() {
		return currencyOptions;
	}

	public void setCurrencyOptions(List<CurrencyData> currencyOptions) {
		this.currencyOptions = currencyOptions;
	}

	public Collection<FundData> getFundOptions() {
		return fundOptions;
	}

	public void setFundOptions(Collection<FundData> fundOptions) {
		this.fundOptions = fundOptions;
	}
	
	public Collection<TransactionProcessingStrategyData> getTransactionProcessingStrategyOptions() {
		return transactionProcessingStrategyOptions;
	}

	public void setTransactionProcessingStrategyOptions(
			Collection<TransactionProcessingStrategyData> transactionProcessingStrategyOptions) {
		this.transactionProcessingStrategyOptions = transactionProcessingStrategyOptions;
	}

	public List<EnumOptionData> getAmortizationTypeOptions() {
		return amortizationTypeOptions;
	}

	public void setAmortizationTypeOptions(
			List<EnumOptionData> amortizationTypeOptions) {
		this.amortizationTypeOptions = amortizationTypeOptions;
	}

	public List<EnumOptionData> getInterestTypeOptions() {
		return interestTypeOptions;
	}

	public void setInterestTypeOptions(List<EnumOptionData> interestTypeOptions) {
		this.interestTypeOptions = interestTypeOptions;
	}

	public List<EnumOptionData> getInterestCalculationPeriodTypeOptions() {
		return interestCalculationPeriodTypeOptions;
	}

	public void setInterestCalculationPeriodTypeOptions(
			List<EnumOptionData> interestCalculationPeriodTypeOptions) {
		this.interestCalculationPeriodTypeOptions = interestCalculationPeriodTypeOptions;
	}

    public Collection<ChargeData> getCharges() {
        return charges;
    }

    public void setCharges(Collection<ChargeData> charges) {
        this.charges = charges;
    }

    public Collection<ChargeData> getChargeOptions() {
        return chargeOptions;
    }

    public void setChargeOptions(Collection<ChargeData> chargeOptions) {
        this.chargeOptions = chargeOptions;
    }
}