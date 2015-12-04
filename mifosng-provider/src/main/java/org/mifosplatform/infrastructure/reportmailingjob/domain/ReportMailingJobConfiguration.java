/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.domain.AbstractPersistable;

@SuppressWarnings("serial")
@Entity
@Table(name = "m_report_mailing_job_configuration", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }, name = "unique_name") })
public class ReportMailingJobConfiguration extends AbstractPersistable<Integer> {
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "value", nullable = false)
    private String value;
    
    /** 
     * ReportMailingJobConfiguration protected constructor
     **/
    protected ReportMailingJobConfiguration() { }
    
    /** 
     * ReportMailingJobConfiguration private constructor
     **/
    private ReportMailingJobConfiguration(final String name, final String value) {
        this.name = name;
        this.value = value;
    }
    
    /** 
     * creates an instance of the ReportMailingJobConfiguration class
     * 
     * @return ReportMailingJobConfiguration object
     **/
    public static ReportMailingJobConfiguration instance(final String name, final String value) {
        return new ReportMailingJobConfiguration(name, value);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
}
