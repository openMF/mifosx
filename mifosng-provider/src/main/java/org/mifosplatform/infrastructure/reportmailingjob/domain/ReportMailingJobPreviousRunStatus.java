/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.domain;

public enum ReportMailingJobPreviousRunStatus {
    INVALID(-1, "ReportMailingJobPreviousRunStatus.invalid", "invalid"),
    SUCCESS(1, "ReportMailingJobPreviousRunStatus.success", "success"),
    ERROR(0, "ReportMailingJobPreviousRunStatus.error", "error");
    
    private final String code;
    private final String value;
    private final Integer id;
    
    private ReportMailingJobPreviousRunStatus(final Integer id, final String code, final String value) {
        this.value = value;
        this.code = code;
        this.id = id;
    }
    
    public static ReportMailingJobPreviousRunStatus instance(final String code) {
        ReportMailingJobPreviousRunStatus previousRunStatus = INVALID;
        
        switch (code) {
            case "success":
                previousRunStatus = SUCCESS;
                break;
                
            case "error":
                previousRunStatus = ERROR;
                break;
        }
        
        return previousRunStatus;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
    
    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }
    
    /** 
     * @return true/false 
     **/
    public boolean isSuccess() {
        return this.value.equals(SUCCESS.getValue());
    }
    
    /** 
     * @return boolean true/false 
     **/
    public boolean isError() {
        return this.value.equals(ERROR.getValue());
    }
}
