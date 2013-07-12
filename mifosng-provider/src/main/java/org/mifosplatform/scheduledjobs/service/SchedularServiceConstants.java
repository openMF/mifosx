package org.mifosplatform.scheduledjobs.service;

public interface SchedularServiceConstants {

    public static final String TRIGGER_KEY_SEPERATOR = " _ ";
    public static final String TRIGGER_TYPE_CRON = "cron";
    public static final String TRIGGER_TYPE_APPLICATION = "application";
    public static final String TRIGGER_TYPE_REFERENCE = "TRIGGER_TYPE_REFERENCE";
    public static final String SCHEDULAR_EXCEPTION = "SchedulerException";
    public static final String JOB_EXECUTION_EXCEPTION = "JobExecutionException";
    public static final String JOB_METHOD_INVOCATION_FAILED_EXCEPTION = "JobMethodInvocationFailedException";
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAILED = "failed";
    public static final String DEFAULT_LISTENER_NAME = "Global Listner";
    public static final int STACK_TRACE_LEVEL = 7;
    public static final String TENANT_IDENTIFIER = "tenantIdentifier";
}