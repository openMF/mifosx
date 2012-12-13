package org.mifosplatform.portfolio.loanaccount.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;

public class GroupLoanBasicDetailsData {

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

    private final LocalDate submittedOnDate;
    private final LocalDate approvedOnDate;
    private final LocalDate expectedDisbursementDate;
    private final LocalDate actualDisbursementDate;
    private final LocalDate repaymentsStartingFromDate;
    private final LocalDate interestChargedFromDate;
    private final LocalDate closedOnDate;
    private final LocalDate expectedMaturityDate;
    private final LocalDate lifeCycleStatusDate;

    private final CurrencyData currency;

    public GroupLoanBasicDetailsData(Long id, String externalId, Long groupId, Long groupOfficeId, String groupName, Long loanProductId,
            String loanProductName, String loanProductDescription, EnumOptionData status, Long fundId, String fundName, Long loanOfficerId,
            String loanOfficerName, BigDecimal principal, BigDecimal inArrearsTolerance, LocalDate submittedOnDate,
            LocalDate approvedOnDate, LocalDate expectedDisbursementDate, LocalDate actualDisbursementDate,
            LocalDate repaymentsStartingFromDate, LocalDate interestChargedFromDate, LocalDate closedOnDate,
            LocalDate expectedMaturityDate, LocalDate lifeCycleStatusDate, CurrencyData currency) {
        this.id = id;
        this.externalId = externalId;
        this.groupId = groupId;
        this.groupOfficeId = groupOfficeId;
        this.groupName = groupName;
        this.loanProductId = loanProductId;
        this.loanProductName = loanProductName;
        this.loanProductDescription = loanProductDescription;
        this.status = status;
        this.fundId = fundId;
        this.fundName = fundName;
        this.loanOfficerId = loanOfficerId;
        this.loanOfficerName = loanOfficerName;
        this.principal = principal;
        this.inArrearsTolerance = inArrearsTolerance;
        this.submittedOnDate = submittedOnDate;
        this.approvedOnDate = approvedOnDate;
        this.expectedDisbursementDate = expectedDisbursementDate;
        this.actualDisbursementDate = actualDisbursementDate;
        this.repaymentsStartingFromDate = repaymentsStartingFromDate;
        this.interestChargedFromDate = interestChargedFromDate;
        this.closedOnDate = closedOnDate;
        this.expectedMaturityDate = expectedMaturityDate;
        this.lifeCycleStatusDate = lifeCycleStatusDate;
        this.currency = currency;
    }

    public DisbursementData toDisbursementData() {
        return new DisbursementData(this.expectedDisbursementDate, this.actualDisbursementDate, this.principal);
    }

    public Long getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public Long getGroupOfficeId() {
        return groupOfficeId;
    }

    public String getGroupName() {
        return groupName;
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

    public Long getFundId() {
        return fundId;
    }

    public String getFundName() {
        return fundName;
    }

    public Long getLoanOfficerId() {
        return loanOfficerId;
    }

    public String getLoanOfficerName() {
        return loanOfficerName;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public BigDecimal getInArrearsTolerance() {
        return inArrearsTolerance;
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

    public LocalDate getLifeCycleStatusDate() {
        return lifeCycleStatusDate;
    }

    public CurrencyData getCurrency() {
        return currency;
    }
}
