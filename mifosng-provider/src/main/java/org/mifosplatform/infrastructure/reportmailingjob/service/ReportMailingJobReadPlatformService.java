/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.service;

import java.util.Collection;

import org.mifosplatform.infrastructure.reportmailingjob.data.ReportMailingJobData;

public interface ReportMailingJobReadPlatformService {
    /** 
     * Retrieve all report mailing jobs that have the isDeleted property set to 0  
     **/
    Collection<ReportMailingJobData> retrieveAllReportMailingJobs();
    
    /** 
     * Retrieve a report mailing job that has the isDeleted property set to 0 
     **/
    ReportMailingJobData retrieveReportMailingJob(Long reportMailingJobId);
    
    /** 
     * Retrieve the report mailing job enumeration/dropdown options 
     **/
    ReportMailingJobData retrieveReportMailingJobEnumOptions();

    /** 
     * Retrieve all active report mailing jobs that have their isDeleted property set to 0 
     **/
    Collection<ReportMailingJobData> retrieveAllActiveReportMailingJobs();
}
