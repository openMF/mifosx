package org.openmf.mifos.dataimport.dto;

import java.util.Locale;

public class Savings {

    private final transient Integer rowIndex;
	
	private final transient String status;
	
    private final String clientId;
	
	private final String fieldOfficerId;
	
	private final String productId;
	
	private final String submittedOnDate;
	
	private final String nominalAnnualInterestRate;
	
	private final String interestCompoundingPeriodType;
	
	private final String interestPostingPeriodType;
	
	private final String interestCalculationType;
	
	private final String interestCalculationDaysInYearType;
	
	private final String minRequiredOpeningBalance;
	
	private final String lockinPeriodFrequency;
	
	private final String lockinPeriodFrequencyType;
	
	private final String withdrawalFeeAmount;
	
	private final String withdrawalFeeType;
	
	private final String annualFeeAmount;
	
	private final String annualFeeOnMonthDay;
	
	private final String monthDayFormat;
	
    private final String dateFormat;
	
	private final Locale locale;
	
	public Savings( String clientId, String productId, String fieldOfficerId, String submittedOnDate, String nominalAnnualInterestRate, String interestCompoundingPeriodType,
			String interestPostingPeriodType, String interestCalculationType, String interestCalculationDaysInYearType, String minRequiredOpeningBalance, String lockinPeriodFrequency,
			String lockinPeriodFrequencyType, String  withdrawalFeeAmount, String withdrawalFeeType, String annualFeeAmount, String annualFeeOnMonthDay, Integer rowIndex, String status) {
		this.clientId = clientId;
		this.productId = productId;
		this.fieldOfficerId = fieldOfficerId;
		this.submittedOnDate = submittedOnDate;
		this.nominalAnnualInterestRate = nominalAnnualInterestRate;
		this.interestCompoundingPeriodType = interestCompoundingPeriodType;
		this.interestPostingPeriodType = interestPostingPeriodType;
		this.interestCalculationType = interestCalculationType;
		this.interestCalculationDaysInYearType = interestCalculationDaysInYearType;
		this.minRequiredOpeningBalance = minRequiredOpeningBalance;
		this.lockinPeriodFrequency = lockinPeriodFrequency;
		this.lockinPeriodFrequencyType = lockinPeriodFrequencyType;
		this.withdrawalFeeAmount = withdrawalFeeAmount;
		this.withdrawalFeeType = withdrawalFeeType;
		this.annualFeeAmount = annualFeeAmount;
		this.annualFeeOnMonthDay = annualFeeOnMonthDay;
		this.rowIndex = rowIndex;
		this.status = status;
		this.dateFormat = "dd MMMM yyyy";
		this.locale = Locale.ENGLISH;
		this.monthDayFormat =  "dd MMM";
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public String getFieldOfficerId() {
		return fieldOfficerId;
	}
	
	public String getProductId() {
		return productId;
	}
	
	public String getNominalAnnualInterestRate() {
		return nominalAnnualInterestRate;
	}

	public String getInterestCompoundingPeriodType() {
		return interestCompoundingPeriodType;
	}
	
	public String getInterestPostingPeriodType() {
		return interestPostingPeriodType;
	}
	
	public String getInterestCalculationType() {
		return interestCalculationType;
	}
	
	public String getInterestCalculationDaysInYearType() {
		return interestCalculationDaysInYearType;
	}
	
	public String getMinRequiredOpeningBalance() {
		return minRequiredOpeningBalance;
	}
	
	public String getLockinPeriodFrequency() {
		return lockinPeriodFrequency;
	}
	
	public String getLockinPeriodFrequencyType() {
		return lockinPeriodFrequencyType;
	}
	
	public String getWithdrawalFeeAmount() {
		return withdrawalFeeAmount;
	}
	
	public String getWithdrawalFeeType() {
		return withdrawalFeeType;
	}
	
	public String getAnnualFeeAmount() {
		return annualFeeAmount;
	}
	
	public String getAnnualFeeOnMonthDay() {
		return annualFeeOnMonthDay;
	}
	
	public String getDateFormat() {
		return dateFormat;
	}
	
	public String getMonthDayFormat() {
		return monthDayFormat;
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
	
	public String getSubmittedOnDate() {
		return submittedOnDate;
	}
}
