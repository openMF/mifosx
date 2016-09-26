package org.openmf.mifos.dataimport.dto;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class SavingsProduct {

	@SerializedName("id")
    private final Integer id;
    
	@SerializedName("name")
    private final String name;
	
	@SerializedName("currency")
	private final Currency currency;
	
	@SerializedName("nominalAnnualInterestRate")
    private final Double nominalAnnualInterestRate;
	
	@SerializedName("interestCompoundingPeriodType")
	private final Type interestCompoundingPeriodType;
	
	@SerializedName("interestPostingPeriodType")
	private final Type interestPostingPeriodType;
	
	@SerializedName("interestCalculationType")
	private final Type interestCalculationType;
	
	@SerializedName("interestCalculationDaysInYearType")
	private final Type interestCalculationDaysInYearType;
	
	@SerializedName("minRequiredOpeningBalance")
    private final Double minRequiredOpeningBalance;
	
	@SerializedName("lockinPeriodFrequency")
    private final Integer lockinPeriodFrequency;
	
	@SerializedName("lockinPeriodFrequencyType")
	private final Type lockinPeriodFrequencyType;
	
	@SerializedName("withdrawalFeeAmount")
    private final Double withdrawalFeeAmount;
	
	@SerializedName("withdrawalFeeType")
	private final Type withdrawalFeeType;
	
	@SerializedName("annualFeeAmount")
    private final Double annualFeeAmount;
	
	@SerializedName("annualFeeOnMonthDay")
    private final ArrayList<Integer> annualFeeOnMonthDay;
	
	public SavingsProduct(Integer id, String name, Currency currency, Double nominalAnnualInterestRate, Type interestCompoundingPeriodType, Type interestPostingPeriodType, Type interestCalculationType, 
			Type interestCalculationDaysInYearType, Double minRequiredOpeningBalance, Integer lockinPeriodFrequency, Type lockinPeriodFrequencyType, Double withdrawalFeeAmount,
			Type withdrawalFeeType, Double annualFeeAmount, ArrayList<Integer> annualFeeOnMonthDay) {
		this.id = id;
		this.name = name;
		this.currency = currency;
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
	}
	
	public Integer getId() {
    	return this.id;
    }

    public String getName() {
        return this.name;
    }
    
    public Currency getCurrency() {
    	return this.currency;
    }
    
    public Double getNominalAnnualInterestRate() {
    	return this.nominalAnnualInterestRate;
    }
    
    public Type getInterestCompoundingPeriodType() {
    	return this.interestCompoundingPeriodType;
    }
    
    public Type getInterestPostingPeriodType() {
    	return this.interestPostingPeriodType;
    }
    
    public Type getInterestCalculationType() {
    	return this.interestCalculationType;
    }
    
    public Type getInterestCalculationDaysInYearType() {
    	return this.interestCalculationDaysInYearType;
    }
    
    public Double getMinRequiredOpeningBalance() {
    	return this.minRequiredOpeningBalance;
    }
    
    public Integer getLockinPeriodFrequency() {
    	return this.lockinPeriodFrequency;
    }
    
    public Type getLockinPeriodFrequencyType() {
    	return this.lockinPeriodFrequencyType;
    }
    
    public Double getWithdrawalFeeAmount() {
    	return this.withdrawalFeeAmount;
    }
    
    public Type getWithdrawalFeeType() {
    	return this.withdrawalFeeType;
    }
    
    public Double getAnnualFeeAmount() {
    	return this.annualFeeAmount;
    }
    
    public ArrayList<Integer> getAnnualFeeOnMonthDay() {
        return this.annualFeeOnMonthDay;
    }
}
