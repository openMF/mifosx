/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.data;

import org.joda.time.DateTime;

/** 
 * Immutable data object representing report mailing job run history data. 
 **/
public class ReportMailingJobRunHistoryData {
    private final Long id;
    private final Long reportMailingJobId;
    private final DateTime startDateTime;
    private final DateTime endDateTime;
    private final String status;
    private final String errorMessage;
    private final String errorLog;
    
    /** 
     * ReportMailingJobRunHistoryData private constructor 
     **/
    private ReportMailingJobRunHistoryData(Long id, Long reportMailingJobId, DateTime startDateTime,
            DateTime endDateTime, String status, String errorMessage, String errorLog) {
        this.id = id;
        this.reportMailingJobId = reportMailingJobId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
        this.errorMessage = errorMessage;
        this.errorLog = errorLog;
    }
    
    /** 
     * creates an instance of the ReportMailingJobRunHistoryData class
     * 
     * @return ReportMailingJobRunHistoryData object
     **/
    public static ReportMailingJobRunHistoryData instance(Long id, Long reportMailingJobId, DateTime startDateTime,
            DateTime endDateTime, String status, String errorMessage, String errorLog) {
        return new ReportMailingJobRunHistoryData(id, reportMailingJobId, startDateTime, endDateTime, status, errorMessage, errorLog);
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the reportMailingJobId
     */
    public Long getReportMailingJobId() {
        return reportMailingJobId;
    }

    /**
     * @return the startDateTime
     */
    public DateTime getStartDateTime() {
        return startDateTime;
    }

    /**
     * @return the endDateTime
     */
    public DateTime getEndDateTime() {
        return endDateTime;
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
