package org.mifosplatform.scheduledjobs;

import org.quartz.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.ParseException;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class JobRunner {
    public static void main(String[] args) throws InterruptedException, SchedulerException, ParseException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/quartz.xml");
        Scheduler scheduler = (Scheduler) context.getBean("schedulerFactory");
        System.out.println("Letting the preconfigured trigger run for sometime");
        Thread.sleep(11000);
        JobKey jobA = JobKey.jobKey("jobADetail", "mifosJobs");
        triggerOnDemand(scheduler, jobA);
        Thread.sleep(5000);
        rescheduleJob(scheduler, new TriggerKey("cronTrigger", "DEFAULT"), "*/7 * * * * ?");
        Thread.sleep(14000);
        changeJob(context, scheduler, jobA, "jobBDetail");

    }

    private static void triggerOnDemand(Scheduler scheduler, JobKey jobKey) throws SchedulerException {
        System.out.println("Trigger Job on Demand");
        scheduler.triggerJob(jobKey);
    }

    private static void changeJob(ClassPathXmlApplicationContext context, Scheduler scheduler, JobKey jobKey, String newJobName) throws SchedulerException {
        System.out.println("Changing Job");
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(new TriggerKey("cronTrigger", "DEFAULT"));
        scheduler.deleteJob(jobKey);

        JobDetail cronDetail1 = (JobDetail) context.getBean(newJobName);
        Trigger newTrigger = newTrigger().forJob(cronDetail1).withIdentity(trigger.getKey()).withSchedule(trigger.getScheduleBuilder()).build();
        scheduler.scheduleJob(cronDetail1, newTrigger);
    }

    private static void rescheduleJob(Scheduler scheduler, TriggerKey triggerKey, String newCronExpression) throws SchedulerException {
        System.out.println("Rescheduling Job");
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        Trigger newTrigger = newTrigger()
                .withIdentity(trigger.getKey())
                .withSchedule(cronSchedule(newCronExpression))
//                .startAt(futureDate(10, MINUTES))
                .build();

        scheduler.rescheduleJob(trigger.getKey(), newTrigger);
    }
}
