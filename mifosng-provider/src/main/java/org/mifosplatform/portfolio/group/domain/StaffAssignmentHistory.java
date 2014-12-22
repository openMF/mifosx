package org.mifosplatform.portfolio.group.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apache.commons.lang.ObjectUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.organisation.staff.domain.Staff;
import org.mifosplatform.useradministration.domain.AppUser;

/*CREATE TABLE `m_staff_assignment_history` (
 `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
 `centre_id` BIGINT(20) NULL DEFAULT NULL,
 `staff_id` BIGINT(20) NOT NULL,
 `start_date` DATE NOT NULL,
 `end_date` DATE NULL DEFAULT NULL,
 `created_date` DATETIME NULL DEFAULT NULL,
 `createdby_id` BIGINT(20) NULL DEFAULT NULL,
 `updated_date` DATETIME NULL DEFAULT NULL,
 `updatedby_id` BIGINT(20) NULL DEFAULT NULL,
 PRIMARY KEY (`id`),
 INDEX `FK_m_staff_assignment_history_centre_id_m_group` (`centre_id`),
 INDEX `FK_m_staff_assignment_history_m_staff` (`staff_id`),
 CONSTRAINT `FK_m_staff_assignment_history_centre_id_m_group` FOREIGN KEY (`centre_id`) REFERENCES `m_group` (`id`),
 CONSTRAINT `FK_m_staff_assignment_history_m_staff` FOREIGN KEY (`staff_id`) REFERENCES `m_staff` (`id`)
 )*/
@Entity
@Table(name = "m_staff_assignment_history")
public class StaffAssignmentHistory extends AbstractAuditableCustom<AppUser, Long> {

    @ManyToOne
    @JoinColumn(name = "centre_id", nullable = true)
    private Group center;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = true)
    private Staff staff;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate;

    public static StaffAssignmentHistory createNew(final Group center, final Staff staff, final LocalDate startDate) {
        return new StaffAssignmentHistory(center, staff, startDate.toDate(), null);
        //insert into m_staff_assignment_history (createdby_id, created_date, lastmodifiedby_id, lastmodified_date, centre_id, end_date, staff_id, start_date)
    }

    protected StaffAssignmentHistory() {
        //
    }

    private StaffAssignmentHistory(final Group center, final Staff staff, final Date startDate, final Date endDate) {
        this.center = center;
        this.staff = staff;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateStaff(final Staff staff) {
        this.staff = staff;
    }

    public boolean isSameStaff(final Staff staff) {
        return this.staff.identifiedBy(staff);
    }

    public void updateStartDate(final LocalDate startDate) {
        this.startDate = startDate.toDate();
    }

    public void updateEndDate(final LocalDate endDate) {
        this.endDate = endDate.toDate();
    }

    public boolean matchesStartDateOf(final LocalDate matchingDate) {
        return getStartDate().isEqual(matchingDate);
    }

    public LocalDate getStartDate() {
        return new LocalDate(this.startDate);
    }

    public boolean isCurrentRecord() {
        return this.endDate == null;
    }

    /**
     * If endDate is null then return false.
     * 
     * @param compareDate
     * @return
     */
    public boolean isEndDateAfter(final LocalDate compareDate) {
        return this.endDate == null ? false : new LocalDate(this.endDate).isAfter(compareDate);
    }

    public LocalDate getEndDate() {
        return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(this.endDate), null);
    }

}
