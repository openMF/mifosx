package org.mifosplatform.portfolio.loanaccount.domain;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.mifosplatform.portfolio.charge.domain.Charge;
import org.mifosplatform.portfolio.charge.domain.ChargeTimeType;
import org.mifosplatform.portfolio.charge.exception.LoanChargeWithoutMandatoryFieldException;
import org.mifosplatform.portfolio.loanaccount.command.GroupLoanChargeCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "m_group_loan_charge")
public class GroupLoanCharge extends AbstractPersistable<Long> {
    @ManyToOne(optional = false)
    @JoinColumn(name = "group_loan_id", referencedColumnName = "id", nullable=false)
    private GroupLoan groupLoan;

    @ManyToOne(optional = false)
    @JoinColumn(name = "charge_id", referencedColumnName = "id", nullable=false)
    private Charge charge;

    @Column(name = "charge_time_enum", nullable = false)
    private Integer chargeTime;

    @Temporal(TemporalType.DATE)
    @Column(name = "due_for_collection_as_of_date")
    private Date dueForCollectionAsOfDate;

    @Column(name = "is_penalty", nullable=false)
    private boolean penaltyCharge = false;

    @Column(name = "is_paid_derived", nullable=false)
    private boolean paid = false;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade =  CascadeType.ALL, mappedBy = "groupLoanCharge")
    private Set<LoanCharge> memberLoanCharges;

    public static GroupLoanCharge createNew(final Charge chargeDefinition, final Set<LoanCharge> memberCharges, final GroupLoanChargeCommand command){
        return new GroupLoanCharge(null, chargeDefinition, memberCharges, command );
    }

    protected GroupLoanCharge(){

    }

    private GroupLoanCharge(GroupLoan groupLoan, Charge chargeDefinition, Set<LoanCharge> memberLoanCharges, GroupLoanChargeCommand command) {
        this.groupLoan = groupLoan;
        this.charge = chargeDefinition;

        if (command.isChargeTimeTypeChanged()) {
            this.chargeTime = command.getChargeTimeType();
        } else {
            this.chargeTime = chargeDefinition.getChargeTime();
        }

        if (ChargeTimeType.fromInt(this.chargeTime).equals(ChargeTimeType.SPECIFIED_DUE_DATE)) {

            if (command.getSpecifiedDueDate() == null) {
                final String defaultUserMessage = "Loan charge is missing specified due date";
                throw new LoanChargeWithoutMandatoryFieldException("loancharge", "specifiedDueDate", defaultUserMessage, command.getId(), chargeDefinition.getName());
            }

            this.dueForCollectionAsOfDate = command.getSpecifiedDueDate().toDate();
        } else {
            this.dueForCollectionAsOfDate = null;
        }

        this.penaltyCharge = chargeDefinition.isPenalty();
        this.memberLoanCharges = memberLoanCharges;
    }

    public Set<LoanCharge> getMemberLoanCharges() {
        return memberLoanCharges;
    }

    public void updateGroupLoan(GroupLoan groupLoan){
        this.groupLoan = groupLoan;
    }
}
