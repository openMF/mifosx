/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.mifosplatform.portfolio.calendar.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_calendar_history")
public class CalendarHistory extends AbstractPersistable<Long>{

    @ManyToOne(optional = false)
    @JoinColumn(name = "calendar_id", referencedColumnName = "id", nullable = false)
    private Calendar calendar;
    
    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "description", length = 100, nullable = true)
    private String description;

    @Column(name = "location", length = 100, nullable = true)
    private String location;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "duration", nullable = true)
    private Integer duration;

    @Column(name = "calendar_type_enum", nullable = false)
    private Integer typeId;

    @Column(name = "repeating", nullable = false)
    private boolean repeating = false;

    @Column(name = "recurrence", length = 100, nullable = true)
    private String recurrence;

    @Column(name = "remind_by_enum", nullable = true)
    private Integer remindById;

    @Column(name = "first_reminder", nullable = true)
    private Integer firstReminder;

    @Column(name = "second_reminder", nullable = true)
    private Integer secondReminder;

    protected CalendarHistory() {

    }

    public CalendarHistory(Calendar calendar, Date startDate) {
        this.calendar = calendar;
        this.title = calendar.getTitle();
        this.description = calendar.getDescription();
        this.location = calendar.getLocation();
        this.startDate = startDate;
        this.endDate = calendar.getStartDate();//Calendar start date become end date for history data.
        this.duration = calendar.getDuration();
        this.typeId = calendar.getTypeId();
        this.repeating = calendar.isRepeating();
        this.recurrence = calendar.getRecurrence();
        this.remindById = calendar.getRemindById();
        this.firstReminder = calendar.getFirstReminder();
        this.secondReminder = calendar.getSecondReminder();
    }
    
    public String getRecurrence() {
        return this.recurrence;
    }

    public LocalDate getStartDateLocalDate() {
        LocalDate startDateLocalDate = null;
        if (this.startDate != null) {
            startDateLocalDate = LocalDate.fromDateFields(this.startDate);
        }
        return startDateLocalDate;
    }

    public LocalDate getEndDateLocalDate() {
        LocalDate endDateLocalDate = null;
        if (this.endDate != null) {
            endDateLocalDate = LocalDate.fromDateFields(this.endDate);
        }
        return endDateLocalDate;
    }

    public boolean isEndDateAfterOrEqual(final LocalDate compareDate) {
        if (this.endDate != null && compareDate != null) {
            if (getEndDateLocalDate().isAfter(compareDate) || getEndDateLocalDate().isEqual(compareDate)) { return true; }
        }
        return false;
    }

    public boolean isStartDateBeforeOrEqual(final LocalDate compareDate) {
        if (this.startDate != null && compareDate != null) {
            if (getStartDateLocalDate().isBefore(compareDate) || getStartDateLocalDate().equals(compareDate)) { return true; }
        }
        return false;
    }

    public boolean isBetweenStartAndEndDate(final LocalDate compareDate){
        if (isStartDateBeforeOrEqual(compareDate)){
            if (getEndDateLocalDate() == null || isEndDateAfterOrEqual(compareDate)){
                return true;
            }
        }
        return false;
    }
    
    public void updateEndDate(Date historyCalEndDate){
        this.endDate = historyCalEndDate;
    }
}
