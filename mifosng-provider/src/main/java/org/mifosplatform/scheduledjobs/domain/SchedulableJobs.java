package org.mifosplatform.scheduledjobs.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "schedulable_jobs")
public class SchedulableJobs extends AbstractPersistable<Long> {

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "desc")
    private String description;

    @Column(name = "job_instance_name")
    private String jobInstanceName;

    public SchedulableJobs() {

    }

    public SchedulableJobs(final String jobName, final String jobInstanceName, final String description) {
        this.jobName = jobName;
        this.description = description;
        this.jobInstanceName = jobInstanceName;
    }

    public String getJobInstanceName() {
        return this.jobInstanceName;
    }
}
