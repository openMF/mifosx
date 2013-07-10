package org.mifosplatform.scheduledjobs.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.security.service.TenantDetailsService;
import org.mifosplatform.scheduledjobs.annotation.CronMethodParser;
import org.mifosplatform.scheduledjobs.domain.ScheduledJobDetails;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

/**
 * Service class to create and load batch jobs to Scheduler using
 * {@link SchedulerFactoryBean} ,{@link MethodInvokingJobDetailFactoryBean} and
 * {@link CronTriggerFactoryBean}
 * 
 */
@Service
public class JobRegisterService {

    private final ApplicationContext applicationContext;

    private final SchedulerFactoryBean schedulerFactoryBean;

    private final SchedularService schedularService;

    private final TenantDetailsService tenantDetailsService;

    @Autowired
    public JobRegisterService(final ApplicationContext applicationContext, final SchedulerFactoryBean schedulerFactoryBean,
            final SchedularService schedularService, final TenantDetailsService tenantDetailsService) {
        this.applicationContext = applicationContext;
        this.schedularService = schedularService;
        this.schedulerFactoryBean = schedulerFactoryBean;
        this.tenantDetailsService = tenantDetailsService;
    }

    @PostConstruct
    public void loadAllJobs() {
        ThreadLocalContextUtil.setDataSourceContext(ThreadLocalContextUtil.CONTEXT_TENANTS);
        List<MifosPlatformTenant> allTenants = this.tenantDetailsService.findAllTenants();
        for (MifosPlatformTenant tenant : allTenants) {
            List<ScheduledJobDetails> scheduledJobDetails = this.schedularService.getJobDetailsByTenant(tenant.getId());
            for (ScheduledJobDetails jobDetails : scheduledJobDetails) {
                scheduleJob(jobDetails);
                this.schedularService.saveOrUpdate(jobDetails);
            }
        }
        ThreadLocalContextUtil.setDataSourceContext(null);
    }

    public void excuteJob(TriggerKey triggerKey) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(SchedularServiceConstants.TRIGGER_TYPE_REFERENCE, SchedularServiceConstants.TRIGGER_TYPE_APPLICATION);
        try {
            JobKey jobKey = schedulerFactoryBean.getScheduler().getTrigger(triggerKey).getJobKey();
            schedulerFactoryBean.getScheduler().triggerJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }

    public void rescheduleJob(ScheduledJobDetails scheduledJobDetails) {
        try {
            ThreadLocalContextUtil.setDataSourceContext(ThreadLocalContextUtil.CONTEXT_TENANTS);
            String triggerIdentity = scheduledJobDetails.getTriggerKey();
            String[] triggerKeyIds = triggerIdentity.split(SchedularServiceConstants.TRIGGER_KEY_SEPERATOR);
            TriggerKey triggerKey = new TriggerKey(triggerKeyIds[0].trim(), triggerKeyIds[1].trim());
            JobDetail jobDetail = createJobDetail(scheduledJobDetails);
            Trigger trigger = createTrigger(scheduledJobDetails, jobDetail);
            schedulerFactoryBean.getScheduler().rescheduleJob(triggerKey, trigger);
            scheduledJobDetails.updateTriggerKey(getTriggerKey(trigger.getKey()));
            scheduledJobDetails.updateNextRunTime(trigger.getNextFireTime());
            scheduledJobDetails.updateErrorLog(null);
            this.schedularService.saveOrUpdate(scheduledJobDetails);
        } catch (Throwable throwable) {
            String stackTrace = getStackTraceAsString(throwable);
            scheduledJobDetails.updateErrorLog(stackTrace);
            this.schedularService.saveOrUpdate(scheduledJobDetails);
        } finally {
            ThreadLocalContextUtil.setDataSourceContext(null);
        }
    }

    private void scheduleJob(ScheduledJobDetails scheduledJobDetails) {
        try {
            JobDetail jobDetail = createJobDetail(scheduledJobDetails);
            Trigger trigger = createTrigger(scheduledJobDetails, jobDetail);
            schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
            scheduledJobDetails.updateTriggerKey(getTriggerKey(trigger.getKey()));
            scheduledJobDetails.updateNextRunTime(trigger.getNextFireTime());
            scheduledJobDetails.updateErrorLog(null);
        } catch (Throwable throwable) {
            String stackTrace = getStackTraceAsString(throwable);
            scheduledJobDetails.updateErrorLog(stackTrace);
        }
    }

    private JobDetail createJobDetail(ScheduledJobDetails scheduledJobDetails) throws Exception {
        Object obj = this.applicationContext.getBean(scheduledJobDetails.getSchedulableJobs().getJobInstanceName());
        MethodInvokingJobDetailFactoryBean jobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();
        jobDetailFactoryBean.setName(scheduledJobDetails.getSchedulableJobs().getJobInstanceName() + "JobDetail"
                + scheduledJobDetails.getTenant().getId());
        jobDetailFactoryBean.setTargetObject(obj);
        String targetMethodName = CronMethodParser.findTargetMethodName(obj.getClass());
        jobDetailFactoryBean.setTargetMethod(targetMethodName);
        jobDetailFactoryBean.setGroup(scheduledJobDetails.getGroupName());
        jobDetailFactoryBean.setConcurrent(false);
        jobDetailFactoryBean.afterPropertiesSet();
        return jobDetailFactoryBean.getObject();
    }

    /**
     * @param scheduledJobDetails
     * @return
     */
    private Trigger createTrigger(ScheduledJobDetails scheduledJobDetails, JobDetail jobDetail) {
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setName(scheduledJobDetails.getSchedulableJobs().getJobInstanceName() + "Trigger"
                + scheduledJobDetails.getTenant().getId());
        cronTriggerFactoryBean.setJobDetail(jobDetail);
        cronTriggerFactoryBean.setGroup(scheduledJobDetails.getGroupName());
        cronTriggerFactoryBean.setCronExpression(scheduledJobDetails.getCroneExpression());
        cronTriggerFactoryBean.setPriority(scheduledJobDetails.getTaskPriority());
        cronTriggerFactoryBean.afterPropertiesSet();
        return cronTriggerFactoryBean.getObject();
    }

    /**
     * @param throwable
     * @return
     */
    private String getStackTraceAsString(Throwable throwable) {
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        StringBuffer sb = new StringBuffer(throwable.toString());
        for (StackTraceElement element : stackTraceElements) {
            sb.append("\n \t at ").append(element.getClassName()).append(".").append(element.getMethodName()).append("(")
                    .append(element.getLineNumber()).append(")");
        }
        return sb.toString();
    }

    private String getTriggerKey(TriggerKey triggerKey) {
        return triggerKey.getName() + SchedularServiceConstants.TRIGGER_KEY_SEPERATOR + triggerKey.getGroup();
    }

}
