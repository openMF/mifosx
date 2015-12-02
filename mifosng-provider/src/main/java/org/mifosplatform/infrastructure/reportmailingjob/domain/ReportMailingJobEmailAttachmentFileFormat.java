/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.domain;

public enum ReportMailingJobEmailAttachmentFileFormat {
    INVALID(0, "ReportMailingJobEmailAttachmentFileFormat.invalid", "invalid"),
    XLS(1, "ReportMailingJobEmailAttachmentFileFormat.xls", "xls"),
    PDF(2, "ReportMailingJobEmailAttachmentFileFormat.pdf", "pdf"),
    CSV(3, "ReportMailingJobEmailAttachmentFileFormat.csv", "csv");
    
    private String code;
    private String value;
    private Integer id;
    
    private ReportMailingJobEmailAttachmentFileFormat(final Integer id, final String code, final String value) {
        this.value = value;
        this.code = code;
        this.id = id;
    }
    
    public static ReportMailingJobEmailAttachmentFileFormat instance(final String value) {
        ReportMailingJobEmailAttachmentFileFormat emailAttachmentFileFormat = INVALID;
        
        switch (value) {
            case "xls":
                emailAttachmentFileFormat = XLS;
                break;
                
            case "pdf":
                emailAttachmentFileFormat = PDF;
                break;
                
            case "csv":
                emailAttachmentFileFormat = CSV;
                break;
                
            default:
                break;
        }
        
        return emailAttachmentFileFormat;
    }
    
    public static ReportMailingJobEmailAttachmentFileFormat instance(final Integer id) {
        ReportMailingJobEmailAttachmentFileFormat emailAttachmentFileFormat = INVALID;
        
        switch (id) {
            case 1:
                emailAttachmentFileFormat = XLS;
                break;
                
            case 2:
                emailAttachmentFileFormat = PDF;
                break;
                
            case 3:
                emailAttachmentFileFormat = CSV;
                break;
                
            default:
                break;
        }
        
        return emailAttachmentFileFormat;
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
     * @return list of valid ReportMailingJobEmailAttachmentFileFormat ids
     **/
    public static Object[] validValues() {
        return new Object[] { XLS.getId(), PDF.getId(), CSV.getId() };
    }
}
