/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.portfolio.creditcheck.data.CreditCheckEnumerations;
import org.mifosplatform.portfolio.creditcheck.domain.CreditCheck;
import org.mifosplatform.portfolio.loanaccount.domain.LoanCreditCheck;
import org.mifosplatform.useradministration.domain.AppUser;

public class LoanCreditCheckData {
    private final Long id;
    private final Long creditCheckId;
    private final String name;
    private final String expectedResult;
    private final String actualResult;
    private final EnumOptionData severityLevel;
    private final String message;
    private final boolean isActive;
    private final boolean hasBeenTriggered;
    private final LoanCreditCheckDataTimelineData timeline;
    private final List<EnumOptionData> severityLevelOptions;
    
    private LoanCreditCheckData(final Long id, final Long creditCheckId, final String name, final String expectedResult, 
            final String actualResult, final EnumOptionData severityLevel, final String message, 
            final boolean isActive, final boolean hasBeenTriggered, final LoanCreditCheckDataTimelineData timeline, 
            final List<EnumOptionData> severityLevelOptions) {
        this.id = id;
        this.creditCheckId = creditCheckId;
        this.name = name;
        this.expectedResult = expectedResult;
        this.actualResult = actualResult;
        this.severityLevel = severityLevel;
        this.message = message;
        this.isActive = isActive;
        this.timeline = timeline;
        this.severityLevelOptions = severityLevelOptions;
        this.hasBeenTriggered = hasBeenTriggered;
    }
    
    public static LoanCreditCheckData instance(final Long id, final Long creditCheckId, final String name, 
            final String expectedResult, final String actualResult, final EnumOptionData severityLevel, final String message, 
            final boolean isActive, final boolean hasBeenTriggered, final LoanCreditCheckDataTimelineData timeline, 
            final List<EnumOptionData> severityLevelOptions) {
        return new LoanCreditCheckData(id, creditCheckId, name, expectedResult, actualResult, severityLevel, 
                message, isActive, hasBeenTriggered, timeline, severityLevelOptions);
    }
    
    public static LoanCreditCheckData instance(final Long id, final Long creditCheckId, final String name, 
            final String expectedResult, final String actualResult, final EnumOptionData severityLevel, 
            final String message, final boolean isActive, final boolean hasBeenTriggered, 
            final LoanCreditCheckDataTimelineData timeline) {
        return new LoanCreditCheckData(id, creditCheckId, name, expectedResult, actualResult, severityLevel, 
                message, isActive, hasBeenTriggered, timeline, null);
    }
    
    public static LoanCreditCheckData instance(final List<EnumOptionData> severityLevelOptions) {
        return new LoanCreditCheckData(null, null, null, null, null, null, 
                null, false, false, null, severityLevelOptions);
    }
    
    public static LoanCreditCheckData instance(final LoanCreditCheck loanCreditCheck) {
        final CreditCheck creditCheck = loanCreditCheck.getCreditCheck();
        EnumOptionData severityLevel = null;
        LoanCreditCheckDataTimelineData timeline = null;
        final AppUser triggeredByUser = loanCreditCheck.getTriggeredByUser();
        
        if (loanCreditCheck.getSeverityLevelEnumValue() != null) {
            severityLevel = CreditCheckEnumerations.severityLevel(loanCreditCheck.getSeverityLevelEnumValue());
        }
        
        if (triggeredByUser != null) {
            LocalDate triggeredOnDate = new LocalDate(loanCreditCheck.getTriggeredOnDate());
            
            timeline = new LoanCreditCheckDataTimelineData(triggeredOnDate, triggeredByUser.getUsername(), 
                    triggeredByUser.getFirstname(), triggeredByUser.getLastname());
        }
        
        return new LoanCreditCheckData(loanCreditCheck.getId(), creditCheck.getId(), creditCheck.getName(), 
                loanCreditCheck.getExpectedResult(), loanCreditCheck.getActualResult(), severityLevel, loanCreditCheck.getMessage(), 
                loanCreditCheck.isActive(), loanCreditCheck.hasBeenTriggered(), timeline, null);
    }
    
    public static Collection<LoanCreditCheckData> instance(final Collection<LoanCreditCheck> loanCreditChecks) {
        final Collection<LoanCreditCheckData> loanCreditCheckDataList = new ArrayList<>();
        
        for (LoanCreditCheck loanCreditCheck : loanCreditChecks) {
            LoanCreditCheckData loanCreditCheckData = LoanCreditCheckData.instance(loanCreditCheck);
            
            loanCreditCheckDataList.add(loanCreditCheckData);
        }
        
        return loanCreditCheckDataList;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the creditCheckId
     */
    public Long getCreditCheckId() {
        return creditCheckId;
    }

    /**
     * @return the expectedResult
     */
    public String getExpectedResult() {
        return expectedResult;
    }

    /**
     * @return the actualResult
     */
    public String getActualResult() {
        return actualResult;
    }

    /**
     * @return the severityLevel
     */
    public EnumOptionData getSeverityLevel() {
        return severityLevel;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the isActive
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * @return the timeline
     */
    public LoanCreditCheckDataTimelineData getTimeline() {
        return timeline;
    }

    /**
     * @return the hasBeenTriggered
     */
    public boolean hasBeenTriggered() {
        return hasBeenTriggered;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the severityLevelOptions
     */
    public List<EnumOptionData> getSeverityLevelOptions() {
        return severityLevelOptions;
    }
    
    /** 
     * @return true if actual result equals expected result 
     **/
    public boolean actualResultEqualsExpectedResult() {
        boolean result = false;
        
        if (this.actualResult != null && this.expectedResult != null) {
            result = this.actualResult.trim().equals(this.expectedResult.trim());
        }
        
        return result;
    }
}
