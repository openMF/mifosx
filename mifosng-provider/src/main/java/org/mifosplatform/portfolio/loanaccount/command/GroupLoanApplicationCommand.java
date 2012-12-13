package org.mifosplatform.portfolio.loanaccount.command;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.mifosplatform.portfolio.loanaccount.loanschedule.command.CalculateLoanScheduleCommand;

public class GroupLoanApplicationCommand extends LoanApplicationCommand {

    private final MemberLoanCommand[] memberLoans;
    private final GroupLoanChargeCommand[] charges;

    public GroupLoanApplicationCommand(Set<String> modifiedParameters, Long loanId, Long groupId, Long productId, String externalId,
            Long fundId, Long loanOfficerId, Long transactionProcessingStrategyId, BigDecimal principal, BigDecimal inArrearsTolerance,
            Integer loanTermFrequency, Integer loanTermFrequencyType, Integer numberOfRepayments, Integer repaymentEvery,
            BigDecimal interestRatePerPeriod, Integer repaymentFrequencyType, Integer interestRateFrequencyType, Integer amortizationType,
            Integer interestType, Integer interestCalculationPeriodType, LocalDate expectedDisbursementDate,
            LocalDate repaymentsStartingFromDate, LocalDate interestChargedFromDate, LocalDate submittedOnDate, String submittedOnNote,
            MemberLoanCommand[] memberLoans, GroupLoanChargeCommand[] charges) {
        super(modifiedParameters, loanId, null, groupId, productId, externalId, fundId, transactionProcessingStrategyId, submittedOnDate,
                submittedOnNote, expectedDisbursementDate, repaymentsStartingFromDate, interestChargedFromDate, principal,
                interestRatePerPeriod, interestRateFrequencyType, interestType, interestCalculationPeriodType, repaymentEvery,
                repaymentFrequencyType, numberOfRepayments, amortizationType, loanTermFrequency, loanTermFrequencyType, inArrearsTolerance,
                null, loanOfficerId);
        this.memberLoans = memberLoans;
        this.charges = charges;
    }

    public CalculateLoanScheduleCommand toCalculateLoanScheduleCommand() {
        return new CalculateLoanScheduleCommand(this.productId, this.principal, this.interestRatePerPeriod, this.interestRateFrequencyType,
                this.interestType, this.interestCalculationPeriodType, this.repaymentEvery, this.repaymentFrequencyType,
                this.numberOfRepayments, this.amortizationType, this.loanTermFrequency, this.loanTermFrequencyType,
                this.expectedDisbursementDate, this.repaymentsStartingFromDate, this.interestChargedFromDate, this.charges);
    }

    public List<LoanApplicationCommand> getMembersLoanApplicationCommands() {
        List<LoanApplicationCommand> membersLoanApplicationCommands = new ArrayList<LoanApplicationCommand>();

        for (MemberLoanCommand memberLoan : memberLoans) {
            membersLoanApplicationCommands.add(new LoanApplicationCommand(memberLoan.getModifiedParameters(), loanId, memberLoan
                    .getClientId(), null, productId, memberLoan.getExternalId(), fundId, transactionProcessingStrategyId, submittedOnDate,
                    submittedOnNote, expectedDisbursementDate, repaymentsStartingFromDate, interestChargedFromDate, memberLoan
                            .getPrincipal(), interestRatePerPeriod, interestRateFrequencyType, interestType, interestCalculationPeriodType,
                    repaymentEvery, repaymentFrequencyType, numberOfRepayments, amortizationType, loanTermFrequency, loanTermFrequencyType,
                    inArrearsTolerance, null, loanOfficerId));
        }

        return membersLoanApplicationCommands;
    }

    public MemberLoanCommand[] getMemberLoans() {
        return memberLoans;
    }

    public GroupLoanChargeCommand[] getCharges() {
        return charges;
    }
}
