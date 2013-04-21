package org.mifosplatform.scheduledjobs;

import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class JobRunner {
    public static void main(String[] args) throws InterruptedException, SchedulerException, ParseException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/quartz.xml");
        Scheduler scheduler = (Scheduler) context.getBean("schedulerFactory");
        for (String groupName : scheduler.getJobGroupNames()) {

            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

                String jobName = jobKey.getName();

                //get job's trigger
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                Date nextFireTime = triggers.get(0).getNextFireTime();

                System.out.println("[jobName] : " + jobName + " [groupName] : "
                        + groupName + "[triggerName] :"+ triggers.get(0).getKey().getName()+" - " + nextFireTime);

            }

        }

        CronTrigger trigger = (CronTrigger)scheduler.getTrigger(new TriggerKey("cronTrigger", "DEFAULT"));

        Trigger newTrigger = newTrigger()
                .withIdentity(trigger.getKey())
                .withSchedule(cronSchedule("0/7 * * * * ?"))
//                .startAt(futureDate(10, MINUTES))
                .build();

        scheduler.rescheduleJob(trigger.getKey(), newTrigger);



    }
}
