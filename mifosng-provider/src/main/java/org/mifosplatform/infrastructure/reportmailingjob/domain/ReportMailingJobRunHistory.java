/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.data.jpa.domain.AbstractPersistable;

@SuppressWarnings("serial")
@Entity
@Table(name = "m_report_mailing_job_run_history")
public class ReportMailingJobRunHistory extends AbstractPersistable<Long> {
    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private ReportMailingJob reportMailingJob;
    
    @Column(name = "start_datetime", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDateTime;
    
    @Column(name = "end_datetime", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDateTime;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    @Column(name = "error_message", nullable = false)
    private String errorMessage;
    
    @Column(name = "error_log", nullable = false)
    private String errorLog;
    
    /** 
     * ReportMailingJobRunHistory protected constructor 
     **/
    protected ReportMailingJobRunHistory() { }

    /** 
     * ReportMailingJobRunHistory private constructor
     **/
    private ReportMailingJobRunHistory(final ReportMailingJob reportMailingJob, final DateTime startDateTime, final DateTime endDateTime, final String status,
            final String errorMessage, final String errorLog) {
        this.reportMailingJob = reportMailingJob;
        this.startDateTime = null;
        
        if (startDateTime != null) {
            this.startDateTime = startDateTime.toDate();
        }
        
        this.endDateTime = null;
        
        if (endDateTime != null) {
            this.endDateTime = endDateTime.toDate();
        }
        
        this.status = status;
        this.errorMessage = errorMessage;
        this.errorLog = errorLog;
    } 
    
    /** 
     * Creates an instance of the ReportMailingJobRunHistory class
     * 
     * @return ReportMailingJobRunHistory object
     **/
    public static ReportMailingJobRunHistory instance(final ReportMailingJob reportMailingJob, final DateTime startDateTime, final DateTime endDateTime, 
            final String status, final String errorMessage, final String errorLog) {
        return new ReportMailingJobRunHistory(reportMailingJob, startDateTime, endDateTime, status, errorMessage, errorLog);
    }

    /**
     * @return the reportMailingJobId
     */
    public ReportMailingJob getReportMailingJob() {
        return this.reportMailingJob;
    }

    /**
     * @return the startDateTime
     */
    public DateTime getStartDateTime() {
        return (this.startDateTime != null) ? new DateTime(this.startDateTime) : null;
    }

    /**
     * @return the endDateTime
     */
    public DateTime getEndDateTime() {
        return (this.endDateTime != null) ? new DateTime(this.endDateTime) : null;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @return the errorLog
     */
    public String getErrorLog() {
        return errorLog;
    }
}
