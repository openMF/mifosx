/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.creditcheck.domain;

public enum CreditCheckSeverityLevel {
    INVALID(0, "creditCheckSeverityLevel.invalid"),
    ERROR(1, "creditCheckSeverityLevel.error"),
    WARNING(2, "creditCheckSeverityLevel.warning"),
    NOTICE(3, "creditCheckSeverityLevel.notice");
    
    private final Integer value;
    private final String code;
    
    public static CreditCheckSeverityLevel fromInt(final Integer levelValue) {
        CreditCheckSeverityLevel severityLevel = CreditCheckSeverityLevel.INVALID;
        
        switch (levelValue) {
            case 1:
                severityLevel = CreditCheckSeverityLevel.ERROR;
                break;
                
            case 2:
                severityLevel = CreditCheckSeverityLevel.WARNING;
                break;
                
            case 3:
                severityLevel = CreditCheckSeverityLevel.NOTICE;
                break;
        }
        
        return severityLevel;
    }

    private CreditCheckSeverityLevel(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    /**
     * @return the value
     */
    public Integer getValue() {
        return value;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }
    
    /** 
     * @return true/false 
     **/
    public boolean isError() {
        return this.value.equals(CreditCheckSeverityLevel.ERROR.getValue());
    }
    
    /** 
     * @return true/false 
     **/
    public boolean isWarning() {
        return this.value.equals(CreditCheckSeverityLevel.WARNING.getValue());
    }
    
    /** 
     * @return true/false 
     **/
    public boolean isNotice() {
        return this.value.equals(CreditCheckSeverityLevel.NOTICE.getValue());
    }
    
    public static Object[] validValues() {
        return new Object[] { CreditCheckSeverityLevel.ERROR.getValue(), CreditCheckSeverityLevel.WARNING.getValue(), 
                CreditCheckSeverityLevel.NOTICE.getValue()};
    }
}
