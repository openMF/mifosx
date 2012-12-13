package org.mifosplatform.portfolio.loanaccount.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.organisation.staff.domain.Staff;
import org.mifosplatform.portfolio.fund.domain.Fund;
import org.mifosplatform.portfolio.group.domain.Group;
import org.mifosplatform.portfolio.loanaccount.exception.InvalidLoanStateTransitionException;
import org.mifosplatform.portfolio.loanproduct.domain.LoanProduct;
import org.mifosplatform.portfolio.loanproduct.domain.PeriodFrequencyType;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "m_group_loan", uniqueConstraints = @UniqueConstraint(columnNames = { "external_id" }))
public class GroupLoan extends AbstractAuditableCustom<AppUser, Long> {

    @SuppressWarnings("unused")
    @ManyToOne(optional = true)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private LoanProduct loanProduct;

    @SuppressWarnings("unused")
    @ManyToOne
    @JoinColumn(name = "fund_id", nullable = true)
    private Fund fund;

    @ManyToOne
    @JoinColumn(name = "loan_officer_id", nullable = true)
    private Staff loanOfficer;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "loan_status_id", nullable = false)
    private Integer loanStatus;

    @Column(name = "arrearstolerance_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal inArrearsTolerance;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "submittedon_date")
    private Date submittedOnDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "rejectedon_date")
    private Date rejectedOnDate;

    @SuppressWarnings("unused")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "withdrawnon_date")
    private Date withdrawnOnDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "approvedon_date")
    private Date approvedOnDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "expected_disbursedon_date")
    private Date expectedDisbursedOnDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "expected_firstrepaymenton_date")
    private Date expectedFirstRepaymentOnDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "interest_calculated_from_date")
    private Date interestChargedFromDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "disbursedon_date")
    private Date disbursedOnDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "closedon_date")
    private Date closedOnDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "writtenoffon_date")
    private Date writtenOffOnDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "rescheduledon_date")
    private Date rescheduledOnDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "expected_maturedon_date")
    private Date expectedMaturityDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "maturedon_date")
    private Date maturedOnDate;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "groupLoan")
    private Set<Loan> memberLoans;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "groupLoan", orphanRemoval = true)
    private Set<GroupLoanCharge> charges;

    public static GroupLoan createNew(final Fund fund, final Staff loanOfficer, final LoanProduct loanProduct, final Group group,
            final BigDecimal inArrearsTolerance, Set<Loan> memberLoans, final Set<GroupLoanCharge> loanCharges) {
        return new GroupLoan(fund, loanOfficer, loanProduct, group, null, inArrearsTolerance, memberLoans, loanCharges);
    }

    protected GroupLoan() {

    }

    private GroupLoan(Fund fund, Staff loanOfficer, LoanProduct loanProduct, Group group, LoanStatus loanStatus,
            BigDecimal inArrearsTolerance, Set<Loan> memberLoans, Set<GroupLoanCharge> charges) {
        this.fund = fund;
        this.loanOfficer = loanOfficer;
        this.loanProduct = loanProduct;
        this.group = group;

        if (loanStatus != null) {
            this.loanStatus = loanStatus.getValue();
        } else {
            this.loanStatus = null;
        }

        this.inArrearsTolerance = inArrearsTolerance;
        this.memberLoans = memberLoans;
        this.charges = charges;
    }

    public void submitGroupApplication(final Integer loanTermFrequency, final PeriodFrequencyType loanTermFrequencyType,
            final LocalDate submittedOn, final LocalDate expectedDisbursementDate, final LocalDate repaymentsStartingFromDate,
            final LocalDate interestChargedFromDate, final LoanLifecycleStateMachine lifecycleStateMachine) {

        LoanStatus from = null;
        if (this.loanStatus != null) {
            from = LoanStatus.fromInt(this.loanStatus);
        }

        LoanStatus statusEnum = lifecycleStateMachine.transition(LoanEvent.LOAN_CREATED, from);
        this.loanStatus = statusEnum.getValue();

        this.submittedOnDate = submittedOn.toDate();
        if (expectedDisbursementDate != null) {
            // can be null during bulk upload of loans
            this.expectedDisbursedOnDate = expectedDisbursementDate.toDate();
        }

        if (repaymentsStartingFromDate != null) {
            this.expectedFirstRepaymentOnDate = repaymentsStartingFromDate.toDate();
        }

        if (interestChargedFromDate != null) {
            this.interestChargedFromDate = interestChargedFromDate.toDate();
        }

        if (submittedOn.isAfter(new LocalDate())) {
            final String errorMessage = "The date on which a group loan is submitted cannot be in the future.";
            throw new InvalidLoanStateTransitionException("submittal", "cannot.be.a.future.date", errorMessage, submittedOn);
        }

        if (submittedOn.isAfter(getExpectedDisbursedOnLocalDate())) {
            final String errorMessage = "The date on which a group loan is submitted cannot be after its expected disbursement date: "
                    + getExpectedDisbursedOnLocalDate().toString();
            throw new InvalidLoanStateTransitionException("submittal", "cannot.be.after.expected.disbursement.date", errorMessage,
                    submittedOn, getExpectedDisbursedOnLocalDate());
        }

        for (Loan member : memberLoans) {
            member.submitMemberApplication(this, loanTermFrequency, loanTermFrequencyType, submittedOn, expectedDisbursementDate,
                    repaymentsStartingFromDate, interestChargedFromDate, lifecycleStateMachine);
        }

        attachMemberChargesToMemberLoans();
    }

    public LocalDate getExpectedDisbursedOnLocalDate() {
        LocalDate expectedDisbursementDate = null;
        if (this.expectedDisbursedOnDate != null) {
            expectedDisbursementDate = new LocalDate(this.expectedDisbursedOnDate);
        }
        return expectedDisbursementDate;
    }

    private void attachMemberChargesToMemberLoans() {
        for (GroupLoanCharge groupCharge : this.charges) {
            groupCharge.updateGroupLoan(this);
            for (LoanCharge memberCharge : groupCharge.getMemberLoanCharges()) {
                memberCharge.getLoan().addLoanCharge(memberCharge);
                memberCharge.updateGroupCharge(groupCharge);
            }
        }
    }

    public Set<Loan> getMemberLoans() {
        return memberLoans;
    }
}
