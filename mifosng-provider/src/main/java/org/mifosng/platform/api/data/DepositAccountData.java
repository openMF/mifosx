package org.mifosng.platform.api.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * Immutable data object for deposit accounts.
 */
public class DepositAccountData {

	private final Long id;
	private final String externalId;
	
	private final Long clientId;
	private final String clientName;
	private final Long productId;
	private final String productName;
	
	private final CurrencyData currency;
	private final BigDecimal deposit;
	private final BigDecimal maturityInterestRate;
	
	private final Integer tenureInMonths;
	private final LocalDate projectedCommencementDate;
	private final LocalDate actualCommencementDate;
	private final LocalDate projectedMaturityDate;
	private final LocalDate actualMaturityDate;
	private final BigDecimal projectedInterestAccrued;
	private final BigDecimal actualInterestAccrued;
	private final BigDecimal projectedMaturityAmount;
	private final BigDecimal actualMaturityAmount;
	
	private final Integer interestCompoundedEvery;
	private final EnumOptionData interestCompoundedEveryPeriodType;
	private final boolean renewalAllowed;
	private final boolean preClosureAllowed;
	private final BigDecimal preClosureInterestRate;
	
	private final DateTime createdOn;
	private final DateTime lastModifedOn;
	
	private final List<EnumOptionData> interestCompoundedEveryPeriodTypeOptions;
	
	private final List<DepositProductLookup> productOptions;
	
	private final EnumOptionData statusEnum;
	private final LocalDate withdrawnonDate;
	private final LocalDate rejectedonDate;
	private final LocalDate closedonDate;
	
	/*
	 * used when returning account template data but only a clientId is passed, no selected product.
	 */
	public static DepositAccountData createFrom(final Long clientId, final String clientDisplayName) {
		return new DepositAccountData(clientId, clientDisplayName);
	}
	
	private DepositAccountData(
			final Long clientId, 
			final String clientName) {
		this.createdOn=null;
		this.lastModifedOn=null;
		this.id=null;
		this.externalId = null;
		this.clientId = clientId;
		this.clientName = clientName;
		this.productId = null;
		this.productName = null;
		this.currency = null;
		this.deposit = null;
		this.maturityInterestRate=null;
		this.tenureInMonths = null;
		this.projectedCommencementDate = null;
		this.actualCommencementDate = null;
		this.projectedMaturityDate = null;
		this.actualMaturityDate = null;
		this.projectedInterestAccrued = null;
		this.actualInterestAccrued = null;
		this.projectedMaturityAmount = null;
		this.actualMaturityAmount = null;
		this.interestCompoundedEvery = null;
		this.interestCompoundedEveryPeriodType = null;
		this.renewalAllowed = false;
		this.preClosureAllowed = false;
		this.preClosureInterestRate = null;
		
		this.interestCompoundedEveryPeriodTypeOptions = new ArrayList<EnumOptionData>();
		this.productOptions = new ArrayList<DepositProductLookup>();
		
		this.withdrawnonDate=null;
		this.closedonDate=null;
		this.rejectedonDate=null;
		this.statusEnum = null;
		
	}

	public DepositAccountData(
			final DepositAccountData account, 
			final List<EnumOptionData> interestCompoundedEveryPeriodTypeOptions,
			final Collection<DepositProductLookup> productOptions) {
		this.createdOn = account.getCreatedOn();
		this.lastModifedOn = account.getLastModifedOn();
		this.id = account.getId();
		this.externalId = account.getExternalId();
		this.clientId = account.getClientId();
		this.clientName = account.getClientName();
		this.productId = account.getProductId();
		this.productName = account.getProductName();
		this.currency = account.getCurrency();
		this.deposit = account.getDeposit();
		this.maturityInterestRate = account.getMaturityInterestRate();
		this.tenureInMonths = account.getTenureInMonths();
		this.projectedCommencementDate = account.getProjectedCommencementDate();
		this.actualCommencementDate = account.getActualCommencementDate();
		this.projectedMaturityDate = account.getProjectedMaturityDate();
		this.actualMaturityDate = account.getActualMaturityDate();
		this.projectedInterestAccrued = account.getProjectedInterestAccrued();
		this.actualInterestAccrued = account.getActualInterestAccrued();
		this.projectedMaturityAmount = account.getProjectedMaturityAmount();
		this.actualMaturityAmount = account.getActualMaturityAmount();
		this.interestCompoundedEvery = account.getInterestCompoundedEvery();
		this.interestCompoundedEveryPeriodType = account.getInterestCompoundedEveryPeriodType();
		this.renewalAllowed = account.isRenewalAllowed();
		this.preClosureAllowed = account.isPreClosureAllowed();
		this.preClosureInterestRate = account.getPreClosureInterestRate();
		
		this.interestCompoundedEveryPeriodTypeOptions = interestCompoundedEveryPeriodTypeOptions;
		this.productOptions = new ArrayList<DepositProductLookup>(productOptions);
		
		this.statusEnum =account.getStatusEnum();
		this.withdrawnonDate=account.getWithdrawnonDate();
		this.rejectedonDate=account.getRejectedonDate();
		this.closedonDate=account.getClosedonDate();
	}
	
	public DepositAccountData(
			final DateTime createdOn, 
			final DateTime lastModifedOn, 
			final Long id,
			final String externalId,
			final Long clientId, 
			final String clientName, 
			final Long productId, 
			final String productName, 
			final CurrencyData currency,
			final BigDecimal deposit, final BigDecimal interestRate, 
			final Integer tenureInMonths, 
			final LocalDate projectedCommencementDate, 
			final LocalDate actualCommencementDate, 
			final LocalDate projectedMaturityDate, 
			final LocalDate actualMaturityDate, 
			final BigDecimal projectedInterestAccrued, 
			final BigDecimal actualInterestAccrued, 
			final BigDecimal projectedMaturityAmount, 
			final BigDecimal actualMaturityAmount,
			final Integer interestCompoundedEvery, 
			final EnumOptionData interestCompoundedEveryPeriodType, 
			final boolean renewalAllowed, 
			final boolean preClosureAllowed, 
			final BigDecimal preClosureInterestRate,
			final EnumOptionData statusEnumType,
			final LocalDate withdrawnonDate,
			final LocalDate rejectedonDate,
			final LocalDate closedonDate
			) {
		this.createdOn=createdOn;
		this.lastModifedOn=lastModifedOn;
		this.id=id;
		this.externalId = externalId;
		this.clientId = clientId;
		this.clientName = clientName;
		this.productId = productId;
		this.productName = productName;
		this.currency = currency;
		this.deposit = deposit;
		this.maturityInterestRate=interestRate;
		this.tenureInMonths = tenureInMonths;
		this.projectedCommencementDate = projectedCommencementDate;
		this.actualCommencementDate = actualCommencementDate;
		this.projectedMaturityDate = projectedMaturityDate;
		this.actualMaturityDate = actualMaturityDate;
		this.projectedInterestAccrued = projectedInterestAccrued;
		this.actualInterestAccrued = actualInterestAccrued;
		this.projectedMaturityAmount = projectedMaturityAmount;
		this.actualMaturityAmount = actualMaturityAmount;
		this.interestCompoundedEvery = interestCompoundedEvery;
		this.interestCompoundedEveryPeriodType = interestCompoundedEveryPeriodType;
		this.renewalAllowed = renewalAllowed;
		this.preClosureAllowed = preClosureAllowed;
		this.preClosureInterestRate = preClosureInterestRate;
		
		this.interestCompoundedEveryPeriodTypeOptions = new ArrayList<EnumOptionData>();
		this.productOptions = new ArrayList<DepositProductLookup>();
		
		this.statusEnum = statusEnumType;
		this.withdrawnonDate = withdrawnonDate;
		this.rejectedonDate = rejectedonDate;
		this.closedonDate = closedonDate;
	}
	
	public DepositAccountData(
			final Long clientId, 
			final String clientName, 
			final Long productId, 
			final String productName, 
			final CurrencyData currency,
			final BigDecimal deposit, final BigDecimal interestRate, 
			final Integer tenureInMonths, 
			final Integer interestCompoundedEvery, 
			final EnumOptionData interestCompoundedEveryPeriodType, 
			final boolean renewalAllowed, 
			final boolean preClosureAllowed, 
			final BigDecimal preClosureInterestRate) {
		this.createdOn=null;
		this.lastModifedOn=null;
		this.id=null;
		this.externalId = null;
		this.clientId = clientId;
		this.clientName = clientName;
		this.productId = productId;
		this.productName = productName;
		this.currency = currency;
		this.deposit = deposit;
		this.maturityInterestRate=interestRate;
		this.tenureInMonths = tenureInMonths;
		this.projectedCommencementDate = null;
		this.actualCommencementDate = null;
		this.projectedMaturityDate = null;
		this.actualMaturityDate = null;
		this.projectedInterestAccrued = null;
		this.actualInterestAccrued = null;
		this.projectedMaturityAmount = null;
		this.actualMaturityAmount = null;
		this.interestCompoundedEvery = interestCompoundedEvery;
		this.interestCompoundedEveryPeriodType = interestCompoundedEveryPeriodType;
		this.renewalAllowed = renewalAllowed;
		this.preClosureAllowed = preClosureAllowed;
		this.preClosureInterestRate = preClosureInterestRate;
		
		this.interestCompoundedEveryPeriodTypeOptions = new ArrayList<EnumOptionData>();
		this.productOptions = new ArrayList<DepositProductLookup>();
		
		this.statusEnum =null; 
		this.withdrawnonDate=null;
		this.closedonDate=null;
		this.rejectedonDate=null;
	}

	public Long getId() {
		return id;
	}
	
	public String getExternalId() {
		return externalId;
	}

	public Long getClientId() {
		return clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public Long getProductId() {
		return productId;
	}

	public String getProductName() {
		return productName;
	}

	public CurrencyData getCurrency() {
		return currency;
	}

	public BigDecimal getDeposit() {
		return deposit;
	}
	
	public BigDecimal getMaturityInterestRate() {
		return maturityInterestRate;
	}

	public Integer getTenureInMonths() {
		return tenureInMonths;
	}

	public LocalDate getProjectedCommencementDate() {
		return projectedCommencementDate;
	}

	public LocalDate getActualCommencementDate() {
		return actualCommencementDate;
	}

	public LocalDate getProjectedMaturityDate() {
		return projectedMaturityDate;
	}

	public LocalDate getActualMaturityDate() {
		return actualMaturityDate;
	}

	public BigDecimal getProjectedInterestAccrued() {
		return projectedInterestAccrued;
	}

	public BigDecimal getActualInterestAccrued() {
		return actualInterestAccrued;
	}

	public BigDecimal getProjectedMaturityAmount() {
		return projectedMaturityAmount;
	}

	public BigDecimal getActualMaturityAmount() {
		return actualMaturityAmount;
	}
	
	public Integer getInterestCompoundedEvery() {
		return interestCompoundedEvery;
	}

	public EnumOptionData getInterestCompoundedEveryPeriodType() {
		return interestCompoundedEveryPeriodType;
	}

	public boolean isRenewalAllowed() {
		return renewalAllowed;
	}

	public boolean isPreClosureAllowed() {
		return preClosureAllowed;
	}

	public BigDecimal getPreClosureInterestRate() {
		return preClosureInterestRate;
	}

	public DateTime getCreatedOn() {
		return createdOn;
	}

	public DateTime getLastModifedOn() {
		return lastModifedOn;
	}

	public List<EnumOptionData> getInterestCompoundedEveryPeriodTypeOptions() {
		return interestCompoundedEveryPeriodTypeOptions;
	}

	public List<DepositProductLookup> getProductOptions() {
		return productOptions;
	}

	public EnumOptionData getStatusEnum() {
		return statusEnum;
	}

	public LocalDate getWithdrawnonDate() {
		return withdrawnonDate;
	}

	public LocalDate getRejectedonDate() {
		return rejectedonDate;
	}

	public LocalDate getClosedonDate() {
		return closedonDate;
	}
	
	
}