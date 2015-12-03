/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.creditcheck.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.dataqueries.domain.Report;
import org.mifosplatform.portfolio.creditcheck.CreditCheckConstants;
import org.mifosplatform.portfolio.creditcheck.data.CreditCheckData;
import org.mifosplatform.portfolio.creditcheck.data.CreditCheckEnumerations;
import org.springframework.data.jpa.domain.AbstractPersistable;

@SuppressWarnings("serial")
@Entity
@Table(name = "m_credit_check", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }, name = "unique_name") })
public class CreditCheck extends AbstractPersistable<Long> {
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "related_entity_enum_value", nullable = false)
    private Integer relatedEntityEnumValue;
    
    @Column(name = "expected_result", nullable = false)
    private String expectedResult;
    
    @Column(name = "severity_level_enum_value", nullable = false)
    private Integer severityLevelEnumValue;
    
    @ManyToOne
    @JoinColumn(name = "stretchy_report_id", nullable = false)
    private Report stretchyReport;
    
    @Column(name = "stretchy_report_param_map", nullable = true)
    private String stretchyReportParamMap;
    
    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;
    
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
    
    /** 
     * CreditCheck protected constructor
     **/
    protected CreditCheck() {}
    
    /** 
     * CreditCheck private constructor
     **/
    private CreditCheck(final String name, final CreditCheckRelatedEntity relatedEntityEnumValue, final String expectedResult, 
            final CreditCheckSeverityLevel severityLevelEnumValue, final Report stretchyReport, final String stretchyReportParamMap, 
            final String message, final boolean isActive) {
        this.name = name;
        this.relatedEntityEnumValue = relatedEntityEnumValue.getValue();
        this.expectedResult = expectedResult;
        this.severityLevelEnumValue = severityLevelEnumValue.getValue();
        this.stretchyReport = stretchyReport;
        this.stretchyReportParamMap = stretchyReportParamMap;
        this.message = message;
        this.isActive = isActive;
    }
    
    /** 
     * @return an instance of the CreditCheck object 
     **/
    public static CreditCheck instance(final String name, final CreditCheckRelatedEntity relatedEntityEnumValue, final String expectedResult, 
            final CreditCheckSeverityLevel severityLevelEnumValue, final Report stretchyReport, final String stretchyReportParamMap, 
            final String message, final boolean isActive) {
        return new CreditCheck(name, relatedEntityEnumValue, expectedResult, severityLevelEnumValue, stretchyReport, stretchyReportParamMap, 
                message, isActive);
    }
    
    /** 
     * @return the name of the credit check 
     **/
    public String getName() {
        return this.name;
    }
    
    /** 
     * @return the credit check related entity (loan/savings) enum value
     **/
    public Integer getRelatedEntityEnumValue() {
        return this.relatedEntityEnumValue;
    }
    
    /** 
     * @return the credit check expected result 
     **/
    public String getExpectedResult() {
        return this.expectedResult;
    }
    
    /** 
     * @return the credit check severity level enum value
     **/
    public Integer getSeverityLevelEnumValue() {
        return this.severityLevelEnumValue;
    }
    
    /** 
     * @return the stretchy report associated with the credit check 
     **/
    public Report getStretchyReport() {
        return this.stretchyReport;
    }
    
    /** 
     * @return the stretchy report parameters JSON key/value map 
     **/
    public String getStretchyReportParamMap() {
        return this.stretchyReportParamMap;
    }
    
    /** 
     * @return the credit check display message 
     **/
    public String getMessage() {
        return this.message;
    }
    
    /** 
     * @return true if isActive property is 1, else false 
     **/
    public boolean isActive() {
        return this.isActive;
    }
    
    /** 
     * @return true if isDeleted property is 1, else false 
     **/
    public boolean isDeleted() {
        return this.isDeleted;
    }
    
    /** 
     * delete the credit check, set the isDeleted property to 1 and alter the name 
     * 
     * @return None
     **/
    public void delete() {
        this.isDeleted = true;
        this.name = this.getId() + "_" + this.name;
    }
    
    /** 
     * create an instance of the CreditCheck class from a JsonCommand object 
     * 
     * @param jsonCommand JsonCommand object
     * @param stretchyReport stretchy report associated with the credit check
     * @return instance of the CreditCheck class
     **/
    public static CreditCheck instance(final JsonCommand jsonCommand, final Report stretchyReport) {
        final String name = jsonCommand.stringValueOfParameterNamed(CreditCheckConstants.NAME_PARAM_NAME);
        final CreditCheckRelatedEntity creditCheckRelatedEntity = CreditCheckRelatedEntity
                .fromInt(jsonCommand.integerValueOfParameterNamed(CreditCheckConstants.RELATED_ENTITY_PARAM_NAME));
        final boolean isActive = jsonCommand.booleanPrimitiveValueOfParameterNamed(CreditCheckConstants.IS_ACTIVE_PARAM_NAME);
        final String expectedResult = jsonCommand.stringValueOfParameterNamed(CreditCheckConstants.EXPECTED_RESULT_PARAM_NAME);
        final CreditCheckSeverityLevel creditCheckSeverityLevel = CreditCheckSeverityLevel
                .fromInt(jsonCommand.integerValueOfParameterNamed(CreditCheckConstants.SEVERITY_LEVEL_PARAM_NAME));
        final String stretchyReportParamMap = jsonCommand.stringValueOfParameterNamed(
                CreditCheckConstants.STRETCHY_REPORT_PARAM_MAP_PARAM_NAME);
        final String message = jsonCommand.stringValueOfParameterNamed(CreditCheckConstants.MESSAGE_PARAM_NAME);
        
        return new CreditCheck(name, creditCheckRelatedEntity, expectedResult, creditCheckSeverityLevel, stretchyReport, 
                stretchyReportParamMap, message, isActive);
    }
    
    /** 
     * update the credit check entity 
     * 
     * @param jsonCommand JsonCommand object
     * @return map of string to object
     **/
    public Map<String, Object> update(final JsonCommand jsonCommand) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>();
        
        if (jsonCommand.isChangeInStringParameterNamed(CreditCheckConstants.NAME_PARAM_NAME, this.name)) {
            final String name = jsonCommand.stringValueOfParameterNamed(CreditCheckConstants.NAME_PARAM_NAME);
            actualChanges.put(CreditCheckConstants.NAME_PARAM_NAME, name);
            
            this.name = name;
        }
        
        if (jsonCommand.isChangeInBooleanParameterNamed(CreditCheckConstants.IS_ACTIVE_PARAM_NAME, this.isActive)) {
            final boolean isActive = jsonCommand.booleanPrimitiveValueOfParameterNamed(CreditCheckConstants.IS_ACTIVE_PARAM_NAME);
            actualChanges.put(CreditCheckConstants.IS_ACTIVE_PARAM_NAME, isActive);
            
            this.isActive = isActive;
        }
        
        if (jsonCommand.isChangeInStringParameterNamed(CreditCheckConstants.EXPECTED_RESULT_PARAM_NAME, this.expectedResult)) {
            final String expectedResult = jsonCommand.stringValueOfParameterNamed(CreditCheckConstants.EXPECTED_RESULT_PARAM_NAME);
            actualChanges.put(CreditCheckConstants.EXPECTED_RESULT_PARAM_NAME, expectedResult);
            
            this.expectedResult = expectedResult;
        }
        
        if (jsonCommand.isChangeInIntegerParameterNamed(CreditCheckConstants.SEVERITY_LEVEL_PARAM_NAME, this.severityLevelEnumValue)) {
            final Integer severityLevelEnumValue = jsonCommand.integerValueOfParameterNamed(
                    CreditCheckConstants.SEVERITY_LEVEL_PARAM_NAME);
            actualChanges.put(CreditCheckConstants.SEVERITY_LEVEL_PARAM_NAME, severityLevelEnumValue);
            
            this.severityLevelEnumValue = severityLevelEnumValue;
        }
        
        Long currentStretchyReportId = null;
        
        if (this.stretchyReport != null) {
            currentStretchyReportId = this.stretchyReport.getId();
        }
        
        if (jsonCommand.isChangeInLongParameterNamed(CreditCheckConstants.STRETCHY_REPORT_ID_PARAM_NAME, currentStretchyReportId)) {
            final Long updatedStretchyReportId = jsonCommand.longValueOfParameterNamed(
                    CreditCheckConstants.STRETCHY_REPORT_ID_PARAM_NAME);
            actualChanges.put(CreditCheckConstants.STRETCHY_REPORT_ID_PARAM_NAME, updatedStretchyReportId);
        }
        
        if (jsonCommand.isChangeInStringParameterNamed(CreditCheckConstants.MESSAGE_PARAM_NAME, this.message)) {
            final String message = jsonCommand.stringValueOfParameterNamed(CreditCheckConstants.MESSAGE_PARAM_NAME);
            actualChanges.put(CreditCheckConstants.MESSAGE_PARAM_NAME, message);
            
            this.message = message;
        }
        
        return actualChanges;
    }
    
    /** 
     * update the stretchy report entity associated with the credit check 
     * 
     * @param stretchyReport -- Report entity
     * @return None
     **/
    public void update(final Report stretchyReport) {
        if (stretchyReport != null) {
            this.stretchyReport = stretchyReport;
        }
    }
    
    /** 
     * @return true if the credit check is related to a loan entity 
     **/
    public boolean isLoanCreditCheck() {
        return CreditCheckRelatedEntity.fromInt(this.relatedEntityEnumValue).isLoanCreditCheck();
    }
    
    /** 
     * @return true if the credit check is related to a savings entity 
     **/
    public boolean isSavingsCreditCheck() {
        return CreditCheckRelatedEntity.fromInt(this.relatedEntityEnumValue).isSavingsCreditCheck();
    }
    
    /** 
     * convert CreditCheck object to CreditCheckData 
     * 
     * @return CreditCheckData instance
     **/
    public CreditCheckData toData() {
        final EnumOptionData relatedEntityEnum = CreditCheckEnumerations.CreditCheckRelatedEntity(this.relatedEntityEnumValue);
        final EnumOptionData severityLevelEnum = CreditCheckEnumerations.severityLevel(this.severityLevelEnumValue);
        
        return CreditCheckData.instance(this.getId(), this.name, relatedEntityEnum, this.expectedResult, severityLevelEnum, 
                this.stretchyReport.getId(), this.stretchyReportParamMap, this.message, this.isActive);
    }
}
