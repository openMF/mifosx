package org.mifosplatform.clientimpactportal.data;


public class ImpactPortalSqlData {

    @SuppressWarnings("unused")
    private String reportName;


    @SuppressWarnings("unused")
    private String reportType;

    @SuppressWarnings("unused")
    private String reportSubType;

    @SuppressWarnings("unused")
    private String reportCategory;

    @SuppressWarnings("unused")
    private String reportSql;

    @SuppressWarnings("unused")
    private String reportDescription;

    @SuppressWarnings("unused")
    private int coreReport;

    @SuppressWarnings("unused")
    private String useReport;

    public ImpactPortalSqlData(final String reportName, final String reportType, final String reportSubType, final String reportCategory
            , final String reportSql, final String reportDescription, final int coreReport, final String useReport){

        this.reportName=reportName;
        this.reportType=reportType;
        this.reportSubType=reportSubType;
        this.reportCategory=reportCategory;
        this.reportSql=reportSql;
        this.reportDescription=reportDescription;
        this.coreReport=coreReport;
        this.useReport=useReport;


    }
    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportSubType() {
        return reportSubType;
    }

    public void setReportSubType(String reportSubType) {
        this.reportSubType = reportSubType;
    }

    public String getReportCategory() {
        return reportCategory;
    }

    public void setReportCategory(String reportCategory) {
        this.reportCategory = reportCategory;
    }

    public String getReportSql() {
        return reportSql;
    }

    public void setReportSql(String reportSql) {
        this.reportSql = reportSql;
    }

    public String getReportDescription() {
        return reportDescription;
    }

    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
    }

    public int getCoreReport() {
        return coreReport;
    }

    public void setCoreReport(int coreReport) {
        this.coreReport = coreReport;
    }

    public String getUseReport() {
        return useReport;
    }

    public void setUseReport(String useReport) {
        this.useReport = useReport;
    }
}
