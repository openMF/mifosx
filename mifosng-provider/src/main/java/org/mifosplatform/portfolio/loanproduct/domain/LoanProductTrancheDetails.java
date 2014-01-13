package org.mifosplatform.portfolio.loanproduct.domain;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.portfolio.loanproduct.LoanProductConstants;

@Embeddable
public class LoanProductTrancheDetails {

    @Column(name = "multi_disburse_loan")
    private boolean multiDisburseLoan;

    @Column(name = "max_tranche_count", nullable = false)
    private Integer maxTrancheCount;

    @Column(name = "outstanding_loan_balance", scale = 6, precision = 19, nullable = false)
    private BigDecimal outstandingLoanBalance;

    protected LoanProductTrancheDetails() {
        // TODO Auto-generated constructor stub
    }

    public LoanProductTrancheDetails(final boolean multiDisburseLoan, final Integer maxTrancheCount, final BigDecimal outstandingLoanBalance) {
        this.multiDisburseLoan = multiDisburseLoan;
        this.maxTrancheCount = maxTrancheCount;
        this.outstandingLoanBalance = outstandingLoanBalance;
    }

    public void update(final JsonCommand command, final Map<String, Object> actualChanges, final String localeAsInput) {
        if (command.isChangeInBooleanParameterNamed(LoanProductConstants.multiDisburseLoanParameterName, this.multiDisburseLoan)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(LoanProductConstants.multiDisburseLoanParameterName);
            actualChanges.put(LoanProductConstants.multiDisburseLoanParameterName, newValue);
            this.multiDisburseLoan = newValue;
        }

        if (this.multiDisburseLoan) {
            if (command.isChangeInIntegerParameterNamed(LoanProductConstants.maxTrancheCountParameterName, this.maxTrancheCount)) {
                final Integer newValue = command.integerValueOfParameterNamed(LoanProductConstants.maxTrancheCountParameterName);
                actualChanges.put(LoanProductConstants.maxTrancheCountParameterName, newValue);
                this.maxTrancheCount = newValue;
            }

            if (command.isChangeInBigDecimalParameterNamed(LoanProductConstants.outstandingLoanBalanceParameterName,
                    this.outstandingLoanBalance)) {
                final BigDecimal newValue = command
                        .bigDecimalValueOfParameterNamed(LoanProductConstants.outstandingLoanBalanceParameterName);
                actualChanges.put(LoanProductConstants.outstandingLoanBalanceParameterName, newValue);
                actualChanges.put(LoanProductConstants.outstandingLoanBalanceParameterName, localeAsInput);
                this.outstandingLoanBalance = newValue;
            }
        } else {
            this.maxTrancheCount = null;
            this.outstandingLoanBalance = null;
        }
    }

    
    public boolean isMultiDisburseLoan() {
        return this.multiDisburseLoan;
    }

    
    public BigDecimal outstandingLoanBalance() {
        return this.outstandingLoanBalance;
    }

    
    public Integer maxTrancheCount() {
        return this.maxTrancheCount;
    }

}
