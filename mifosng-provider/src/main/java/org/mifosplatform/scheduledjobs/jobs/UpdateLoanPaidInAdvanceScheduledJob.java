/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.scheduledjobs.jobs;

import java.util.Date;

import org.mifosplatform.scheduledjobs.annotation.CronTargetMethod;
import org.mifosplatform.scheduledjobs.service.ScheduledJobRunnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateLoanPaidInAdvanceScheduledJob {

    private final static Logger logger = LoggerFactory.getLogger(UpdateLoanPaidInAdvanceScheduledJob.class);

    private final ScheduledJobRunnerService scheduledJobRunnerService;

    @Autowired
    public UpdateLoanPaidInAdvanceScheduledJob(final ScheduledJobRunnerService scheduledJobRunnerService) {
        this.scheduledJobRunnerService = scheduledJobRunnerService;
    }

    @CronTargetMethod
    public void execute() {
        logger.info("Running Job UpdateLoanPaidInAdvanceScheduledJob " + new Date());

        this.scheduledJobRunnerService.updateLoanPaidInAdvance();

        logger.info("Finishing Job UpdateLoanPaidInAdvanceScheduledJob " + new Date());
    }
}
