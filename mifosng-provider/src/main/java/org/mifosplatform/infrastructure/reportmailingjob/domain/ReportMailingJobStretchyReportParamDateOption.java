/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.domain;

public enum ReportMailingJobStretchyReportParamDateOption {
    INVALID(0, "reportMailingJobStretchyReportParamDateOption.invalid", "invalid"),
    TODAY(1, "reportMailingJobStretchyReportParamDateOption.today", "today"),
    YESTERDAY(2, "reportMailingJobStretchyReportParamDateOption.yesterday", "yesterday"),
    TOMORROW(3, "reportMailingJobStretchyReportParamDateOption.tomorrow", "tomorrow");
    
    private String code;
    private String value;
    private Integer id;
    
    /**
     * @param id
     * @param code
     * @param value
     */
    private ReportMailingJobStretchyReportParamDateOption(final Integer id, final String code, final String value) {
        this.value = value;
        this.code = code;
        this.id = id;
    }
    
    /**
     * @param value
     * @return
     */
    public static ReportMailingJobStretchyReportParamDateOption instance(final String value) {
        ReportMailingJobStretchyReportParamDateOption reportMailingJobStretchyReportParamDateOption = INVALID;
        
        switch (value) {
            case "today":
                reportMailingJobStretchyReportParamDateOption = TODAY;
                break;
                
            case "yesterday":
                reportMailingJobStretchyReportParamDateOption = YESTERDAY;
                break;
                
            case "tomorrow":
                reportMailingJobStretchyReportParamDateOption = TOMORROW;
                break;
        }
        
        return reportMailingJobStretchyReportParamDateOption;
    }
    
    /**
     * @param id
     * @return
     */
    public static ReportMailingJobStretchyReportParamDateOption instance(final Integer id) {
        ReportMailingJobStretchyReportParamDateOption reportMailingJobStretchyReportParamDateOption = INVALID;
        
        switch (id) {
            case 1:
                reportMailingJobStretchyReportParamDateOption = TODAY;
                break;
                
            case 2:
                reportMailingJobStretchyReportParamDateOption = YESTERDAY;
                break;
                
            case 3:
                reportMailingJobStretchyReportParamDateOption = TOMORROW;
                break;
        }
        
        return reportMailingJobStretchyReportParamDateOption;
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
     * @return list of valid ReportMailingJobEmailAttachmentFileFormat values
     **/
    public static Object[] validValues() {
        return new Object[] { TODAY.value, YESTERDAY.value, TOMORROW.value };
    }
}
