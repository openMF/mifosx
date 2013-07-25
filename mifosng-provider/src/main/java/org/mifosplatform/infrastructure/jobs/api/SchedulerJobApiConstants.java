package org.mifosplatform.infrastructure.jobs.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SchedulerJobApiConstants {

    public static final String JOB_RESOURCE_NAME = "schedulerjob";
    // response parameters
    public static final String jobIdentifierParamName = "jobId";
    public static final String displayNameParamName = "displayName";
    public static final String nextRunTimeParamName = "nextRunTime";
    public static final String initializingErrorParamName = "initializingError";
    public static final String jobActiveStatusParamName = "active";
    public static final String currentlyRunningParamName = "currentlyRunning";
    public static final String lastRunHistoryObjParamName = "lastRunHistory";

    public static final String versionParamName = "version";
    public static final String jobRunStartTimeParamName = "jobRunStartTime";
    public static final String jobRunEndTimeParamName = "jobRunEndTime";
    public static final String statusParamName = "status";
    public static final String jobRunErrorMessageParamName = "jobRunErrorMessage";
    public static final String triggerTypeParamName = "triggerType";
    public static final String jobRunErrorLogParamName = "jobRunErrorLog";
    public static final String cronExpressionParamName = "cronExpression";
    public static final String schedulerStatusParamName = "active";

    public static final Set<String> JOB_DETAIL_RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(jobIdentifierParamName,
            displayNameParamName, nextRunTimeParamName, initializingErrorParamName, jobActiveStatusParamName, currentlyRunningParamName,
            lastRunHistoryObjParamName));

    public static final Set<String> JOB_HISTORY_RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(versionParamName,
            jobRunStartTimeParamName, jobRunEndTimeParamName, statusParamName, jobRunErrorMessageParamName, triggerTypeParamName,
            jobRunErrorLogParamName));

    public static final Set<String> JOB_UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(displayNameParamName,
            jobActiveStatusParamName, cronExpressionParamName));

    public static final Set<String> SCHEDULER_DETAIL_RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(schedulerStatusParamName));

    public static final String COMMAND_EXECUTE_JOB = "executeJob";
    public static final String COMMAND_STOP_SCHEDULER = "stop";
    public static final String COMMAND_START_SCHEDULER = "start";
    public static final String COMMAND = "command";
    public static final String JOB_ID = "jobId";
    public static final String JOB_RUN_HISTORY = "runhistory";
    public static final String SCHEDULER_STATUS_PATH = "scheduler";
}
