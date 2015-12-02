/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.creditcheck.data;

import java.util.List;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;

/** 
 * Immutable data object representing credit check data. 
 **/
public class CreditCheckData {
    
    private final Long id;
    private final String name;
    private final EnumOptionData relatedEntity;
    private final String expectedResult;
    private final EnumOptionData severityLevel;
    private final Long stretchyReportId;
    private final String stretchyReportParamMap;
    private final String message;
    private final boolean isActive;
    private final List<EnumOptionData> relatedEntityOptions;
    private final List<EnumOptionData> severityLevelOptions;

    /** 
     * CreditCheckData private constructor
     * 
     * @return None 
     **/
    private CreditCheckData(final Long id, final String name, final EnumOptionData relatedEntity, 
            final String expectedResult, final EnumOptionData severityLevel, final Long stretchyReportId, 
            final String stretchyReportParamMap, final String message, final boolean isActive, 
            final List<EnumOptionData> relatedEntityOptions, List<EnumOptionData> severityLevelOptions) {
        this.id = id;
        this.name = name;
        this.relatedEntity = relatedEntity;
        this.expectedResult = expectedResult;
        this.severityLevel = severityLevel;
        this.stretchyReportId = stretchyReportId;
        this.stretchyReportParamMap = stretchyReportParamMap;
        this.message = message;
        this.isActive = isActive;
        this.relatedEntityOptions = relatedEntityOptions;
        this.severityLevelOptions = severityLevelOptions;
    }
    
    /** 
     * @return new instance of the CreditCheckData class 
     **/
    public static CreditCheckData instance(final Long id, final String name, final EnumOptionData relatedEntity, 
            final String expectedResult, final EnumOptionData severityLevel, final Long stretchyReportId, 
            final String stretchyReportParamMap, final String message, final boolean isActive) {
        return new CreditCheckData(id, name, relatedEntity, expectedResult, severityLevel, stretchyReportId, stretchyReportParamMap, 
                message, isActive, null, null);
    }
    
    /** 
     * @return new instance of the CreditCheckData class 
     **/
    public static CreditCheckData instance(final CreditCheckData dataWithoutEnumOptions, final CreditCheckData dataWithEnumOptions) {
        return new CreditCheckData(dataWithoutEnumOptions.id, dataWithoutEnumOptions.name, dataWithoutEnumOptions.relatedEntity, 
                dataWithoutEnumOptions.expectedResult, dataWithoutEnumOptions.severityLevel, dataWithoutEnumOptions.stretchyReportId, 
                dataWithoutEnumOptions.stretchyReportParamMap, dataWithoutEnumOptions.message, dataWithoutEnumOptions.isActive, 
                dataWithEnumOptions.relatedEntityOptions, dataWithEnumOptions.severityLevelOptions);
    }
    
    /** 
     * @return new instance of the CreditCheckData class
     **/
    public static CreditCheckData instance(final List<EnumOptionData> relatedEntityOptions, 
            final List<EnumOptionData> severityLevelOptions) {
        return new CreditCheckData(null, null, null, null, null, null, null, null, false, relatedEntityOptions, 
                severityLevelOptions);
    }

    /**
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the expectedResult
     */
    public String getExpectedResult() {
        return this.expectedResult;
    }

    /**
     * @return the severityLevel
     */
    public EnumOptionData getSeverityLevel() {
        return this.severityLevel;
    }

    /**
     * @return the stretchyReportId
     */
    public Long getStretchyReportId() {
        return this.stretchyReportId;
    }

    /**
     * @return the stretchyReportParamMap
     */
    public String getStretchyReportParamMap() {
        return this.stretchyReportParamMap;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @return the relatedEntity
     */
    public EnumOptionData getRelatedEntity() {
        return relatedEntity;
    }

    /**
     * @return the isActive
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * @return the relatedEntityOptions
     */
    public List<EnumOptionData> getRelatedEntityOptions() {
        return relatedEntityOptions;
    }

    /**
     * @return the severityLevelOptions
     */
    public List<EnumOptionData> getSeverityLevelOptions() {
        return severityLevelOptions;
    }
}
