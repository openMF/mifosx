package org.mifosplatform.portfolio.savings.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.organisation.staff.domain.Staff;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "m_savings_officer_assignment_history")
public class SavingsOfficerAssignmentHistory extends AbstractAuditableCustom<AppUser, Long> {

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private SavingsAccount savingsAccount;

    @ManyToOne
    @JoinColumn(name = "savings_officer_id", nullable = true)
    private Staff savingsOfficer;

    public static SavingsOfficerAssignmentHistory createNew(final SavingsAccount account, final Staff savingsOfficer) {
        return new SavingsOfficerAssignmentHistory(account, savingsOfficer);
    }

    protected SavingsOfficerAssignmentHistory() {
        //
    }

    private SavingsOfficerAssignmentHistory(final SavingsAccount account, final Staff savingsOfficer) {
        this.savingsAccount = account;
        this.savingsOfficer = savingsOfficer;
    }

    public void updateSavingsOfficer(final Staff savingsOfficer) {
        this.savingsOfficer = savingsOfficer;
    }

    public boolean isSameSavingsOfficer(final Staff staff) {
        return this.savingsOfficer.identifiedBy(staff);
    }
}
