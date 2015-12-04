/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.data;

import java.util.Map;

public class SmsBusinessRulesData {

    @SuppressWarnings("unused")
    private final Long reportId;

    @SuppressWarnings("unused")
    private final String reportName;

    @SuppressWarnings("unused")
    private final String reportType;

    @SuppressWarnings("unused")
    private final String reportDescription;

    @SuppressWarnings("unused")
    private final Map<String,Object> reportParamName;



    public SmsBusinessRulesData(final Long reportId, final String reportName,final String reportType, final Map<String,Object> reportParamName,
                                final String reportDescription) {
        this.reportId = reportId;
        this.reportName = reportName;
        this.reportType = reportType;
        this.reportParamName = reportParamName;
        this.reportDescription = reportDescription;
    }


    public static SmsBusinessRulesData instance(final Long reportId, final String reportName,final String reportType,final Map<String,Object> reportParamName,
                                                final String reportDescription){
        return new SmsBusinessRulesData(reportId,reportName,reportType,reportParamName,reportDescription);
    }

    public Map<String, Object> getReportParamName() {
        return reportParamName;
    }

    public String getReportType() {
        return reportType;
    }

    public String getReportName() {
        return reportName;
    }

    public Long getReportId() {
        return reportId;
    }

    public String getReportDescription() {
        return reportDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmsBusinessRulesData that = (SmsBusinessRulesData) o;

        if (reportId != null ? !reportId.equals(that.reportId) : that.reportId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return reportId != null ? reportId.hashCode() : 0;
    }
}
