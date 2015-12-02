/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.domain;

import org.mifosplatform.infrastructure.reportmailingjob.exception.ReportMailingJobConfigurationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportMailingJobConfigurationRepositoryWrapper {
    private final ReportMailingJobConfigurationRepository reportMailingJobConfigurationRepository;
    
    @Autowired
    public ReportMailingJobConfigurationRepositoryWrapper(ReportMailingJobConfigurationRepository reportMailingJobConfigurationRepository) {
        this.reportMailingJobConfigurationRepository = reportMailingJobConfigurationRepository;
    }
    
    /** 
     * find ReportMailingJobConfiguration by name, throw exception if not found
     * 
     * @param name -- the name of the configuration
     * @return ReportMailingJobConfiguration object
     **/
    public ReportMailingJobConfiguration findOneThrowExceptionIfNotFound(final String name) {
        final ReportMailingJobConfiguration reportMailingJobConfiguration = this.reportMailingJobConfigurationRepository.findByName(name);
        
        if (reportMailingJobConfiguration == null) {
            throw new ReportMailingJobConfigurationNotFoundException(name);
        }
        
        return reportMailingJobConfiguration;
    }
    
    /** 
     * @return ReportMailingJobConfigurationRepository JPA repository object
     **/
    public ReportMailingJobConfigurationRepository getReportMailingJobConfigurationRepository() {
        return reportMailingJobConfigurationRepository;
    }
}
