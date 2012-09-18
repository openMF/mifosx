package org.mifosng.platform.api.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;

/**
 * Immutable data object for core details of loan to be combined with {@link LoanAccountData} usage.
 */
public class LoanBasicDetailsData {

	private final Long id;
	private final String externalId;
	private final Long clientId;
	private final Long clientOfficeId;
	private final String clientName;
	private final Long loanProductId;
	private final String loanProductName;
	private final String loanProductDescription;
	private final EnumOptionData status;
	private final Long fundId;
	private final String fundName;
	private final Long loanOfficerId;
	private final String loanOfficerName;
	private final CurrencyData currency;
	private final BigDecimal principal;
	private final BigDecimal inArrearsTolerance;
	
	private final Integer termFrequency;
	private final EnumOptionData termPeriodFrequencyType;
	private final Integer numberOfRepayments;
	private final Integer repaymentEvery;
	private final EnumOptionData repaymentFrequencyType;
	private final Integer transactionStrategyId;
	private final EnumOptionData amortizationType;
	private final BigDecimal interestRatePerPeriod;
	private final EnumOptionData interestRateFrequencyType;
	private final BigDecimal annualInterestRate;
	private final EnumOptionData interestType;
	private final EnumOptionData interestCalculationPeriodType;
	
	private final LocalDate submittedOnDate;
	private final LocalDate approvedOnDate;
	private final LocalDate expectedDisbursementDate;
	private final LocalDate actualDisbursementDate;
	private final LocalDate repaymentsStartingFromDate;
	private final LocalDate interestChargedFromDate;
	private final LocalDate closedOnDate;
	private final LocalDate expectedMaturityDate;
	private final LocalDate lifeCycleStatusDate;
	
	private final Collection<LoanChargeData> charges;
	
	public static LoanBasicDetailsData populateForNewLoanCreation(final Long clientId, final String clientName, final LocalDate expectedDisbursementDate,
																	final Long clientOfficeId) {
		return new LoanBasicDetailsData(clientId, clientName, expectedDisbursementDate, clientOfficeId);
	}
	
	public static LoanBasicDetailsData populateForNewLoanCreation(final LoanBasicDetailsData loanAccount, final LoanProductData product) {
		
		final Integer termFrequency = product.getNumberOfRepayments() * product.getRepaymentEvery();
		final EnumOptionData termPeriodFrequencyType = product.getRepaymentFrequencyType();
        final Collection<LoanChargeData> charges = new ArrayList<LoanChargeData>();
        for (ChargeData charge : product.getCharges()){
            charges.add(new LoanChargeData(null, charge.getId(), charge.getName(), charge.getCurrency(),
                    charge.getAmount(), charge.getChargeTimeType(), charge.getChargeCalculationType()));
        }

		return new LoanBasicDetailsData(
				loanAccount.clientId, loanAccount.clientName, loanAccount.clientOfficeId,
				product.getId(), product.getName(), product.getDescription(), product.getFundId(), product.getFundName(), 
				product.getCurrency(), product.getPrincipal(), product.getInArrearsTolerance(),
				termFrequency, termPeriodFrequencyType, product.getNumberOfRepayments(), product.getRepaymentEvery(), product.getRepaymentFrequencyType(),
				product.getTransactionProcessingStrategyId(), product.getAmortizationType(), 
				product.getInterestRatePerPeriod(), product.getInterestRateFrequencyType(), product.getInterestType(), product.getInterestCalculationPeriodType(),
				loanAccount.expectedDisbursementDate, charges);
	}
	
	private LoanBasicDetailsData(
			final Long clientId, final String clientName, final Long clientOfficeId,
			final Long productId, final String productName, final String productDescription, final Long fundId, final String fundName, 
			final CurrencyData currency,  final BigDecimal principal, final BigDecimal inArrearsTolerance,
			final Integer termFrequency,
			final EnumOptionData termPeriodFrequencyType,
			final Integer numberOfRepayments,
			final Integer repaymentEvery,
			final EnumOptionData repaymentFrequencyType,
			final Long transactionStrategyId,
			final EnumOptionData amortizationType,
			final BigDecimal interestRatePerPeriod,
			final EnumOptionData interestRateFrequencyType,
			final EnumOptionData interestType,
			final EnumOptionData interestCalculationPeriodType,
			final LocalDate expectedDisbursementDate, final Collection<LoanChargeData> charges) {
		this.id = null;
		this.externalId = null;
		this.clientId = clientId;
		this.clientName = clientName;
		this.clientOfficeId = clientOfficeId;
		this.loanProductId =  productId;
		this.loanProductName = productName;
		this.loanProductDescription= productDescription;
		this.status = null;
		this.fundId = fundId;
		this.fundName = fundName;
		this.loanOfficerId = null;
		this.loanOfficerName = null;
		this.currency = currency;
		this.principal = principal;
		this.inArrearsTolerance = inArrearsTolerance;
		
		this.termFrequency = termFrequency;
		this.termPeriodFrequencyType = termPeriodFrequencyType;
		this.numberOfRepayments = numberOfRepayments;
		this.repaymentEvery = repaymentEvery;
		this.repaymentFrequencyType = repaymentFrequencyType;
		
		// FIXME - kw - settle on using either long or integer for this throughout product and account fields for loan.
		if (transactionStrategyId != null) {
			this.transactionStrategyId = transactionStrategyId.intValue();
		} else {
			this.transactionStrategyId = null;
		}
		this.amortizationType = amortizationType;
		
		this.interestRatePerPeriod = interestRatePerPeriod;
		this.annualInterestRate = null;
		this.interestRateFrequencyType = interestRateFrequencyType;
		this.interestType = interestType;
		this.interestCalculationPeriodType = interestCalculationPeriodType;
		
		this.submittedOnDate = null;
		this.approvedOnDate = null;
		this.expectedDisbursementDate = expectedDisbursementDate;
		
		this.actualDisbursementDate = null;
		this.closedOnDate = null;
		this.expectedMaturityDate = null;
		this.repaymentsStartingFromDate = null;
		this.interestChargedFromDate = null;
		this.lifeCycleStatusDate = null;
		
		this.charges = charges;
	}
	
	private LoanBasicDetailsData(final Long clientId, final String clientName, final LocalDate expectedDisbursementDate, final Long clientOfficeId) {
		this.id = null;
		this.externalId = null;
		this.clientId = clientId;
		this.clientName = clientName;
		this.clientOfficeId = clientOfficeId;
		this.loanProductId =  null;
		this.loanProductName = null;
		this.loanProductDescription= null;
		this.status = null;
		this.fundId = null;
		this.fundName = null;
		this.loanOfficerId = null;
		this.loanOfficerName = null;
		this.currency = null;
		this.principal = null;
		this.inArrearsTolerance = null;
		this.termFrequency = null;
		this.termPeriodFrequencyType = null;
		this.numberOfRepayments = null;
		this.repaymentEvery = null;
		this.transactionStrategyId = null;
		this.interestRatePerPeriod = null;
		this.annualInterestRate = null;
		this.repaymentFrequencyType = null;
		this.interestRateFrequencyType = null;
		this.amortizationType = null;
		this.interestType = null;
		this.interestCalculationPeriodType = null;
		
		this.lifeCycleStatusDate = null;
		
		this.submittedOnDate = null;
		this.approvedOnDate = null;
		this.expectedDisbursementDate = expectedDisbursementDate;
		
		this.actualDisbursementDate = null;
		this.closedOnDate = null;
		this.expectedMaturityDate = null;
		this.repaymentsStartingFromDate = null;
		this.interestChargedFromDate = null;
		
		this.charges = null;
	}

	public LoanBasicDetailsData(
			final Long id, 
			final String externalId,
			final Long clientId, final String clientName,
			final Long clientOfficeId, final Long loanProductId,
			final String loanProductName,
			final String loanProductDescription,
			final Long fundId, String fundName,
			final LocalDate closedOnDate, 
			final LocalDate submittedOnDate,
			final LocalDate approvedOnDate, 
			final LocalDate expectedDisbursementDate,
			final LocalDate actualDisbursementDate, 
			final LocalDate expectedMaturityDate,
			final LocalDate repaymentsStartingFromDate,
			final LocalDate interestChargedFromDate, 
			final CurrencyData currency,
			final BigDecimal principal,
			final BigDecimal inArrearsTolerance, 
			final Integer numberOfRepayments,
			final Integer repaymentEvery, 
			final BigDecimal interestRatePerPeriod,
			final BigDecimal annualInterestRate,
			final EnumOptionData repaymentFrequencyType,
			final EnumOptionData interestRateFrequencyType,
			final EnumOptionData amortizationType, 
			final EnumOptionData interestType,
			final EnumOptionData interestCalculationPeriodType,
			final EnumOptionData status,
			final LocalDate lifeCycleStatusDate, 
			final Integer termFrequency, 
			final EnumOptionData termPeriodFrequencyType, 
			final Integer transactionStrategyId,
			final Collection<LoanChargeData> charges,
			final Long loanOfficerId, String loanOfficerName) {
		this.id = id;
		this.externalId = externalId;
		this.clientId = clientId;
		this.clientName = clientName;
		this.clientOfficeId = clientOfficeId;
		this.loanProductId = loanProductId;
		this.loanProductName = loanProductName;
		this.loanProductDescription = loanProductDescription;
		this.fundId = fundId;
		this.fundName = fundName;
	    this.loanOfficerId = loanOfficerId;
		this.loanOfficerName = loanOfficerName;
		this.closedOnDate = closedOnDate;
		this.submittedOnDate = submittedOnDate;
		this.approvedOnDate = approvedOnDate;
		this.expectedDisbursementDate = expectedDisbursementDate;
		this.actualDisbursementDate = actualDisbursementDate;
		this.expectedMaturityDate = expectedMaturityDate;
		this.repaymentsStartingFromDate = repaymentsStartingFromDate;
		this.interestChargedFromDate = interestChargedFromDate;
		this.currency = currency;
		this.principal = principal;
		this.inArrearsTolerance = inArrearsTolerance;
		this.numberOfRepayments = numberOfRepayments;
		this.repaymentEvery = repaymentEvery;
		this.interestRatePerPeriod = interestRatePerPeriod;
		this.annualInterestRate = annualInterestRate;
		this.repaymentFrequencyType = repaymentFrequencyType;
		this.interestRateFrequencyType = interestRateFrequencyType;
		this.amortizationType = amortizationType;
		this.interestType = interestType;
		this.interestCalculationPeriodType = interestCalculationPeriodType;
		this.status = status;
		this.lifeCycleStatusDate = lifeCycleStatusDate;
		this.termFrequency = termFrequency;
		this.termPeriodFrequencyType = termPeriodFrequencyType;
		this.transactionStrategyId = transactionStrategyId;
		this.charges = charges;
	}

	public int getMaxSubmittedOnOffsetFromToday() {
		return Days.daysBetween(new DateTime(),
				this.getSubmittedOnDate().toDateMidnight().toDateTime())
				.getDays();
	}

	public int getMaxApprovedOnOffsetFromToday() {

		int offset = 0;
		if (this.getApprovedOnDate() != null) {
			offset = Days.daysBetween(new DateTime(),
					this.getApprovedOnDate().toDateMidnight().toDateTime())
					.getDays();
		}

		return offset;
	}

	public int getMaxDisbursedOnOffsetFromToday() {

		int offset = 0;
		if (this.getActualDisbursementDate() != null) {
			offset = Days.daysBetween(
					new DateTime(),
					this.getActualDisbursementDate().toDateMidnight()
							.toDateTime()).getDays();
		}

		return offset;
	}

	public int getActualLoanTermInDays() {

		LocalDate dateToUse = getExpectedDisbursementDate();
		if (getActualDisbursementDate() != null) {
			dateToUse = getActualDisbursementDate();
		}

		LocalDate closingDateToUse = getExpectedMaturityDate();
		if (getClosedOnDate() != null) {
			closingDateToUse = getClosedOnDate();
		}

		return Days.daysBetween(dateToUse.toDateMidnight().toDateTime(),
				closingDateToUse.toDateMidnight().toDateTime()).getDays();
	}

	public int getActualLoanTermInMonths() {

		LocalDate dateToUse = getExpectedDisbursementDate();
		if (getActualDisbursementDate() != null) {
			dateToUse = getActualDisbursementDate();
		}

		LocalDate closingDateToUse = getExpectedMaturityDate();
		if (getClosedOnDate() != null) {
			closingDateToUse = getClosedOnDate();
		}

		return Months.monthsBetween(dateToUse.toDateMidnight().toDateTime(),
				closingDateToUse.toDateMidnight().toDateTime()).getMonths();
	}

	public int getLoanTermInDays() {

		LocalDate dateToUse = getExpectedDisbursementDate();
		if (getActualDisbursementDate() != null) {
			dateToUse = getActualDisbursementDate();
		}

		LocalDate closingDateToUse = getExpectedMaturityDate();

		return Days.daysBetween(dateToUse.toDateMidnight().toDateTime(),
				closingDateToUse.toDateMidnight().toDateTime()).getDays();
	}

	public int getLoanTermInMonths() {

		LocalDate dateToUse = getExpectedDisbursementDate();
		if (getActualDisbursementDate() != null) {
			dateToUse = getActualDisbursementDate();
		}

		LocalDate closingDateToUse = getExpectedMaturityDate();

		return Months.monthsBetween(dateToUse.toDateMidnight().toDateTime(),
				closingDateToUse.toDateMidnight().toDateTime()).getMonths();
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
	
	public Long getClientOfficeId() {
		return clientOfficeId;
	}

	public Long getFundId() {
		return fundId;
	}

	public String getFundName() {
		return fundName;
	}

	public Long getLoanProductId() {
		return loanProductId;
	}

	public String getLoanProductName() {
		return loanProductName;
	}

	public String getLoanProductDescription() {
		return loanProductDescription;
	}
	
	public EnumOptionData getStatus() {
		return status;
	}
	
	public Integer getTermFrequency() {
		return termFrequency;
	}

	public EnumOptionData getTermPeriodFrequencyType() {
		return termPeriodFrequencyType;
	}

	public LocalDate getSubmittedOnDate() {
		return submittedOnDate;
	}

	public LocalDate getApprovedOnDate() {
		return approvedOnDate;
	}

	public LocalDate getExpectedDisbursementDate() {
		return expectedDisbursementDate;
	}

	public LocalDate getActualDisbursementDate() {
		return actualDisbursementDate;
	}
	
	public LocalDate getRepaymentsStartingFromDate() {
		return repaymentsStartingFromDate;
	}

	public LocalDate getInterestChargedFromDate() {
		return interestChargedFromDate;
	}

	public LocalDate getClosedOnDate() {
		return closedOnDate;
	}

	public LocalDate getExpectedMaturityDate() {
		return expectedMaturityDate;
	}
	
	public CurrencyData getCurrency() {
		return currency;
	}

	public BigDecimal getPrincipal() {
		return principal;
	}

	public BigDecimal getInArrearsTolerance() {
		return inArrearsTolerance;
	}

	public Integer getNumberOfRepayments() {
		return numberOfRepayments;
	}

	public Integer getRepaymentEvery() {
		return repaymentEvery;
	}

	public BigDecimal getInterestRatePerPeriod() {
		return interestRatePerPeriod;
	}

	public BigDecimal getAnnualInterestRate() {
		return annualInterestRate;
	}

	public EnumOptionData getRepaymentFrequencyType() {
		return repaymentFrequencyType;
	}

	public EnumOptionData getInterestRateFrequencyType() {
		return interestRateFrequencyType;
	}

	public EnumOptionData getAmortizationType() {
		return amortizationType;
	}

	public EnumOptionData getInterestType() {
		return interestType;
	}

	public EnumOptionData getInterestCalculationPeriodType() {
		return interestCalculationPeriodType;
	}

	public LocalDate getLifeCycleStatusDate() {
		return lifeCycleStatusDate;
	}

	public Integer getTransactionStrategyId() {
		return transactionStrategyId;
	}

	public Collection<LoanChargeData> getCharges() {
		return charges;
	}

	public Long getLoanOfficerId() {
		return loanOfficerId;
	}

	public String getLoanOfficerName() {
		return loanOfficerName;
	}
}