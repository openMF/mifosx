package org.mifosplatform.scheduledjobs.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "jobs_tenants")
public class ScheduledJobDetails extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "job_id")
    private SchedulableJobs schedulableJobs;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private MifosPlatformTenant tenant;

    @Column(name = "cron_expression")
    private String croneExpression;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "task_priority")
    private Short taskPriority;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "previous_run_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date previousRunStartTime;

    @Column(name = "next_run_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextRunTime;

    @Column(name = "trigger_key")
    private String triggerKey;

    /*
     * @LazyCollection(LazyCollectionOption.TRUE)
     * 
     * @OneToMany(cascade = CascadeType.ALL, mappedBy = "scheduledJobDetails")
     * private final List<ScheduledJobRunHistory> runHistories = new
     * ArrayList<ScheduledJobRunHistory>();
     */

    @Column(name = "job_initializing_errorlog")
    private String errorLog;

    public ScheduledJobDetails() {

    }

    public ScheduledJobDetails(final SchedulableJobs schedulableJobs, final MifosPlatformTenant tenant, final String croneExpression,
            Date createTime, final Short taskPriority, final String groupName, final Date previousRunStartTime, final Date nextRunTime,
            final String triggKey, final String errorLog) {
        this.schedulableJobs = schedulableJobs;
        this.tenant = tenant;
        this.croneExpression = croneExpression;
        this.createTime = createTime;
        this.taskPriority = taskPriority;
        this.groupName = groupName;
        this.previousRunStartTime = previousRunStartTime;
        this.nextRunTime = nextRunTime;
        this.triggerKey = triggKey;
        this.errorLog = errorLog;
    }

    public SchedulableJobs getSchedulableJobs() {
        return this.schedulableJobs;
    }

    public MifosPlatformTenant getTenant() {
        return this.tenant;
    }

    public String getCroneExpression() {
        return this.croneExpression;
    }

    public Short getTaskPriority() {
        return this.taskPriority;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public String getTriggerKey() {
        return this.triggerKey;
    }

    public void updateCroneExpression(String croneExpression) {
        this.croneExpression = croneExpression;
    }

    public void updatePreviousRunStartTime(Date previousRunStartTime) {
        this.previousRunStartTime = previousRunStartTime;
    }

    public void updateNextRunTime(Date nextRunTime) {
        this.nextRunTime = nextRunTime;
    }

    public void updateTriggerKey(String triggerKey) {
        this.triggerKey = triggerKey;
    }

    public void updateErrorLog(String errorLog) {
        this.errorLog = errorLog;
    }

}
