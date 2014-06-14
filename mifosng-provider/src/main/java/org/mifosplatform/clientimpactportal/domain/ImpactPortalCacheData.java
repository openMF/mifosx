package org.mifosplatform.clientimpactportal.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Date;


@Entity
@Table(name = "impact_portal_cache")
public class ImpactPortalCacheData extends AbstractPersistable<Long> {

    @Column(name = "date_captured", nullable = true)
    private Date captureDate;

    @Column(name = "datapoint", nullable = true, length = 45)
    private String datapoint;

    @Column(name = "datapoint_label", nullable = true, length = 45)
    private String datapointLabel;

    @Column(name = "value", nullable = true, length = 1000)
    private String value;

    public  ImpactPortalCacheData(final Date captureDate, final String datapoint, final String dataPointLabel, final String value) {
        this.captureDate = captureDate;
        this.datapoint = datapoint;
        this.datapointLabel = dataPointLabel;
        this.value = value;
    }


    public Date getCaptureDate() {
        return captureDate;
    }

    public void setCaptureDate(Date captureDate) {
        this.captureDate = captureDate;
    }

    public String getDatapoint() {
        return datapoint;
    }

    public void setDatapoint(String datapoint) {
        this.datapoint = datapoint;
    }

    public String getDatapointLabel() {
        return datapointLabel;
    }

    public void setDatapointLabel(String datapointLabel) {
        this.datapointLabel = datapointLabel;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
