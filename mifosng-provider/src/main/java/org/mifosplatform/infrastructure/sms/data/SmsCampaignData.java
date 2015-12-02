/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.data;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;

import java.util.Map;

public class SmsCampaignData {

    @SuppressWarnings("unused")
    private Long id;
    @SuppressWarnings("unused")
    private final String campaignName;
    @SuppressWarnings("unused")
    private final Integer campaignType;
    @SuppressWarnings("unused")
    private final Long runReportId;

    @SuppressWarnings("unused")
    private final String paramValue;
    @SuppressWarnings("unused")
    private final EnumOptionData campaignStatus;
    @SuppressWarnings("unused")
    private final String message;
    @SuppressWarnings("unused")
    private final DateTime nextTriggerDate;
    @SuppressWarnings("unused")
    private final LocalDate lastTriggerDate;
    @SuppressWarnings("unused")
    private final SmsCampaignTimeLine smsCampaignTimeLine;

    @SuppressWarnings("unused")
    private final DateTime recurrenceStartDate;

    private final String recurrence;

    private SmsCampaignData(final Long id,final String campaignName, final Integer campaignType, final Long runReportId,
                           final String paramValue,final EnumOptionData campaignStatus,
                           final String message,final DateTime nextTriggerDate,final LocalDate lastTriggerDate,final SmsCampaignTimeLine smsCampaignTimeLine,
                           final DateTime recurrenceStartDate, final String recurrence) {
        this.id = id;
        this.campaignName = campaignName;
        this.campaignType = campaignType;
        this.runReportId = runReportId;
        this.paramValue = paramValue;
        this.campaignStatus =campaignStatus;
        this.message = message;
        if(nextTriggerDate !=null){
            this.nextTriggerDate = nextTriggerDate;
        }else{
            this.nextTriggerDate = null;
        }
        if(lastTriggerDate !=null){
            this.lastTriggerDate = lastTriggerDate;
        }else{
            this.lastTriggerDate = null;
        }
        this.smsCampaignTimeLine =smsCampaignTimeLine;
        this.recurrenceStartDate = recurrenceStartDate;
        this.recurrence  = recurrence;
    }

    public static SmsCampaignData instance(final Long id,final String campaignName, final Integer campaignType, final Long runReportId,
                                           final String paramValue,final EnumOptionData campaignStatus,final String message,
                                           final DateTime nextTriggerDate, final LocalDate lastTriggerDate,final SmsCampaignTimeLine smsCampaignTimeLine,
                                           final DateTime recurrenceStartDate, final String recurrence){
        return new SmsCampaignData(id,campaignName,campaignType,runReportId,paramValue,
                campaignStatus,message,nextTriggerDate,lastTriggerDate,smsCampaignTimeLine,recurrenceStartDate,recurrence);
    }


    public Long getId() {
        return id;
    }

    public String getCampaignName() {
        return this.campaignName;
    }

    public Integer getCampaignType() {
        return this.campaignType;
    }

    public Long getRunReportId() {
        return this.runReportId;
    }

    public String getParamValue() {
        return this.paramValue;
    }

    public EnumOptionData getCampaignStatus() {
        return this.campaignStatus;
    }

    public String getMessage() {
        return this.message;
    }


    public DateTime getNextTriggerDate() {
        return this.nextTriggerDate;
    }

    public LocalDate getLastTriggerDate() {
        return this.lastTriggerDate;
    }

    public String getRecurrence() {return this.recurrence;}

    public DateTime getRecurrenceStartDate() {return this.recurrenceStartDate;}
}
