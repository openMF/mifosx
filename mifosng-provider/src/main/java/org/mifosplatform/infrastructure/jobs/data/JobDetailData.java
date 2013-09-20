package org.mifosplatform.infrastructure.jobs.data;

import java.util.Date;

public class JobDetailData {

    @SuppressWarnings("unused")
    private final Long jobId;

    @SuppressWarnings("unused")
    private final String displayName;

    @SuppressWarnings("unused")
    private final Date nextRunTime;

    @SuppressWarnings("unused")
    private final String initializingError;

    @SuppressWarnings("unused")
    private final String cronExpression;

    @SuppressWarnings("unused")
    private final boolean active;

    @SuppressWarnings("unused")
    private final boolean currentlyRunning;

    @SuppressWarnings("unused")
    private final JobDetailHistoryData lastRunHistory;

    public JobDetailData(final Long jobId, final String displayName, final Date nextRunTime, final String initializingError,
            final String cronExpression, final boolean active, final boolean currentlyRunning, final JobDetailHistoryData lastRunHistory) {
        this.jobId = jobId;
        this.displayName = displayName;
        this.nextRunTime = nextRunTime;
        this.initializingError = initializingError;
        this.cronExpression = cronExpression;
        this.active = active;
        this.lastRunHistory = lastRunHistory;
        this.currentlyRunning = currentlyRunning;
    }
}
