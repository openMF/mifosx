/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.domain;

import org.mifosplatform.infrastructure.reportmailingjob.exception.ReportMailingJobNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportMailingJobRepositoryWrapper {
    private final ReportMailingJobRepository reportMailingJobRepository;
    
    @Autowired
    public ReportMailingJobRepositoryWrapper(final ReportMailingJobRepository reportMailingJobRepository) {
        this.reportMailingJobRepository = reportMailingJobRepository;
    }
    
    /** 
     * find report mailing job by ID, throw a "entity not found" exception if the job does not exist
     * 
     * @param id -- the identifier of the report mailing job to be found
     * @return ReportMailingJob object
     **/
    public ReportMailingJob findOneThrowExceptionIfNotFound(final Long id) {
        final ReportMailingJob reportMailingJob = this.reportMailingJobRepository.findOne(id);
        
        if (reportMailingJob == null || reportMailingJob.isDeleted()) {
            throw new ReportMailingJobNotFoundException(id);
        }
        
        return reportMailingJob;
    }
    
    /** 
     * @return ReportMailingJobRepository Jpa Repository object
     **/
    public ReportMailingJobRepository getReportMailingJobRepository() {
        return this.reportMailingJobRepository;
    }
}
