package org.mifosplatform.portfolio.loanaccount.data;

import java.math.BigDecimal;
import java.util.Date;

import org.joda.time.LocalDate;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.portfolio.common.domain.PeriodFrequencyType;

public class LoanScheduleAccrualData {

    private final Long loanId;
    private final Long officeId;
    private final LocalDate accruedTill;
    private final PeriodFrequencyType repaymentFrequency;
    private final Integer repayEvery;
    private final Integer numberOfDaysInYear;
    private final Integer numberOfDaysInMonth;
    private final LocalDate dueDate;
    private final LocalDate fromDate;
    private final Long repaymentScheduleId;
    private final Long loanProductId;
    private final BigDecimal interestIncome;
    private final BigDecimal feeIncome;
    private final BigDecimal penaltyIncome;
    private final BigDecimal accruedInterestIncome;
    private final BigDecimal accruedFeeIncome;
    private final BigDecimal accruedPenaltyIncome;
    private final CurrencyData currencyData;
    private final BigDecimal dueDateFeeIncome;
    private final BigDecimal dueDatePenaltyIncome;

    public LoanScheduleAccrualData(final Long loanId, final Long officeId, final LocalDate accruedTill, final Integer numberOfDaysInMonth,
            final Integer numberOfDaysInYear, final PeriodFrequencyType repaymentFrequency, final Integer repayEvery,
            final LocalDate dueDate, final LocalDate fromDate, final Long repaymentScheduleId, final Long loanProductId,
            final BigDecimal interestIncome, final BigDecimal feeIncome, final BigDecimal penaltyIncome,
            final BigDecimal accruedInterestIncome, final BigDecimal accruedFeeIncome, final BigDecimal accruedPenaltyIncome,
            final CurrencyData currencyData, final BigDecimal dueDateFeeIncome, final BigDecimal dueDatePenaltyIncome) {
        this.loanId = loanId;
        this.officeId = officeId;
        this.accruedTill = accruedTill;
        this.numberOfDaysInMonth = numberOfDaysInMonth;
        this.numberOfDaysInYear = numberOfDaysInYear;
        this.dueDate = dueDate;
        this.fromDate = fromDate;
        this.repaymentScheduleId = repaymentScheduleId;
        this.loanProductId = loanProductId;
        this.interestIncome = interestIncome;
        this.feeIncome = feeIncome;
        this.penaltyIncome = penaltyIncome;
        this.accruedFeeIncome = accruedFeeIncome;
        this.accruedInterestIncome = accruedInterestIncome;
        this.accruedPenaltyIncome = accruedPenaltyIncome;
        this.currencyData = currencyData;
        this.repaymentFrequency = repaymentFrequency;
        this.repayEvery = repayEvery;
        this.dueDateFeeIncome = dueDateFeeIncome;
        this.dueDatePenaltyIncome = dueDatePenaltyIncome;
    }

    public Long getLoanId() {
        return this.loanId;
    }

    public Long getOfficeId() {
        return this.officeId;
    }

    public Date getDueDate() {
        return this.dueDate.toDate();
    }

    public LocalDate getDueDateAsLocaldate() {
        return this.dueDate;
    }

    public Long getRepaymentScheduleId() {
        return this.repaymentScheduleId;
    }

    public Long getLoanProductId() {
        return this.loanProductId;
    }

    public BigDecimal getInterestIncome() {
        return this.interestIncome;
    }

    public BigDecimal getFeeIncome() {
        return this.feeIncome;
    }

    public BigDecimal getPenaltyIncome() {
        return this.penaltyIncome;
    }

    public BigDecimal getAccruedInterestIncome() {
        return this.accruedInterestIncome;
    }

    public BigDecimal getAccruedFeeIncome() {
        return this.accruedFeeIncome;
    }

    public BigDecimal getAccruedPenaltyIncome() {
        return this.accruedPenaltyIncome;
    }

    public CurrencyData getCurrencyData() {
        return this.currencyData;
    }

    public LocalDate getAccruedTill() {
        return this.accruedTill;
    }

    public Integer getNumberOfDaysInYear() {
        return this.numberOfDaysInYear;
    }

    public Integer getNumberOfDaysInMonth() {
        return this.numberOfDaysInMonth;
    }

    public LocalDate getFromDateAsLocaldate() {
        return this.fromDate;
    }

    public PeriodFrequencyType getRepaymentFrequency() {
        return this.repaymentFrequency;
    }

    public Integer getRepayEvery() {
        return this.repayEvery;
    }

    public BigDecimal getDueDateFeeIncome() {
        return this.dueDateFeeIncome;
    }

    public BigDecimal getDueDatePenaltyIncome() {
        return this.dueDatePenaltyIncome;
    }

}
