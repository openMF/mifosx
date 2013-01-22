package org.mifosplatform.portfolio.savingsaccount.command;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class CalculateSavingScheduleCommand {

    private final Long productId;
    private final BigDecimal deposit;
    private final Integer depositEvery;
    private final Integer paymentFrequencyType;
    private final BigDecimal interestRate;
    private final LocalDate paymentsStartingFromDate;
    private final Integer tenure;
    private final Integer tenureType;
    private final Integer interestPostEvery; 
    private final Integer interestPostFrequency;
    private final Integer interestCalculationMethod;

    public CalculateSavingScheduleCommand(final Long productId, final BigDecimal deposit, final Integer depositEvery,
            final Integer paymentFrequencyType, final BigDecimal interestRate, final LocalDate paymentsStartingFromDate,
            final Integer tenure, final Integer tenureType, final Integer interestPostEvery, final Integer interestPostFrequency, final Integer interestCalculationMethod) {

        this.productId = productId;
        this.deposit = deposit;
        this.depositEvery = depositEvery;
        this.paymentFrequencyType = paymentFrequencyType;
        this.interestRate = interestRate;
        this.paymentsStartingFromDate = paymentsStartingFromDate;
        this.tenure = tenure;
        this.tenureType = tenureType;
        this.interestPostEvery = interestPostEvery;
        this.interestPostFrequency = interestPostFrequency;
        this.interestCalculationMethod = interestCalculationMethod;

    }

    public Long getProductId() {
        return this.productId;
    }

    public BigDecimal getDeposit() {
        return this.deposit;
    }

    public Integer getDepositEvery() {
        return this.depositEvery;
    }

    public Integer getPaymentFrequencyType() {
        return this.paymentFrequencyType;
    }

    public BigDecimal getInterestRate() {
        return this.interestRate;
    }

    public LocalDate getPaymentsStartingFromDate() {
        return this.paymentsStartingFromDate;
    }

    public Integer getTenure() {
        return this.tenure;
    }

	public Integer getInterestPostEvery() {
		return this.interestPostEvery;
	}

	public Integer getInterestPostFrequency() {
		return this.interestPostFrequency;
	}

	public Integer getTenureType() {
		return this.tenureType;
	}

	public Integer getInterestCalculationMethod() {
		return this.interestCalculationMethod;
	}

}
