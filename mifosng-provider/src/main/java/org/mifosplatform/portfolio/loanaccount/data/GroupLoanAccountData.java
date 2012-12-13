package org.mifosplatform.portfolio.loanaccount.data;

import java.math.BigDecimal;
import java.util.Collection;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.portfolio.loanaccount.loanschedule.data.LoanScheduleData;

public class GroupLoanAccountData {

    private final Long id;
    private final String externalId;
    private final Long groupId;
    private final Long groupOfficeId;
    private final String groupName;
    private final Long loanProductId;
    private final String loanProductName;
    private final String loanProductDescription;
    private final EnumOptionData status;
    private final Long fundId;
    private final String fundName;
    private final Long loanOfficerId;
    private final String loanOfficerName;
    private final BigDecimal principal;
    private final BigDecimal inArrearsTolerance;
    private final CurrencyData currency;

    private final LocalDate submittedOnDate;
    private final LocalDate approvedOnDate;
    private final LocalDate expectedDisbursementDate;
    private final LocalDate actualDisbursementDate;
    private final LocalDate repaymentsStartingFromDate;
    private final LocalDate interestChargedFromDate;
    private final LocalDate closedOnDate;
    private final LocalDate expectedMaturityDate;
    private final LocalDate lifeCycleStatusDate;

    private final Collection<LoanBasicDetailsData> loanMembers;
    private final LoanScheduleData repaymentSchedule;

    public GroupLoanAccountData(GroupLoanBasicDetailsData basicDetails, Collection<LoanBasicDetailsData> loanMembers,
            LoanScheduleData repaymentSchedule) {
        this.loanMembers = loanMembers;
        this.repaymentSchedule = repaymentSchedule;

        this.id = basicDetails.getId();
        this.externalId = basicDetails.getExternalId();
        this.groupId = basicDetails.getGroupId();
        this.groupName = basicDetails.getGroupName();
        this.groupOfficeId = basicDetails.getGroupOfficeId();
        this.loanProductId = basicDetails.getLoanProductId();
        this.loanProductName = basicDetails.getLoanProductName();
        this.loanProductDescription = basicDetails.getLoanProductDescription();
        this.fundId = basicDetails.getFundId();
        this.fundName = basicDetails.getFundName();
        this.loanOfficerId = basicDetails.getLoanOfficerId();
        this.loanOfficerName = basicDetails.getLoanOfficerName();

        this.submittedOnDate = basicDetails.getSubmittedOnDate();
        this.approvedOnDate = basicDetails.getApprovedOnDate();
        this.expectedDisbursementDate = basicDetails.getExpectedDisbursementDate();
        this.actualDisbursementDate = basicDetails.getActualDisbursementDate();
        this.closedOnDate = basicDetails.getClosedOnDate();
        this.expectedMaturityDate = basicDetails.getExpectedMaturityDate();
        this.repaymentsStartingFromDate = basicDetails.getRepaymentsStartingFromDate();
        this.interestChargedFromDate = basicDetails.getInterestChargedFromDate();

        this.principal = basicDetails.getPrincipal();
        this.inArrearsTolerance = basicDetails.getInArrearsTolerance();
        this.currency = basicDetails.getCurrency();

        this.status = basicDetails.getStatus();
        this.lifeCycleStatusDate = basicDetails.getLifeCycleStatusDate();
    }
}
