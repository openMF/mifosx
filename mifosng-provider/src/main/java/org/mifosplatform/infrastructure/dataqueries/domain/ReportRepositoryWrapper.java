/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.domain;

import org.mifosplatform.infrastructure.dataqueries.exception.ReportNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** 
 *  A wrapper class for the ReportRepository that provides a method that returns a Report entity if it exists, 
 *  else throws "ReportNotFoundException" exception if the Report does not exist
 **/
@Service
public class ReportRepositoryWrapper {
    private final ReportRepository reportRepository;
    
    @Autowired
    public ReportRepositoryWrapper(final ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
    
    public Report findOneThrowExceptionIfNotFound(final Long id) {
        final Report report = this.reportRepository.findOne(id);
        
        if (report == null) {
            throw new ReportNotFoundException(id);
        }
        
        return report;
    }
    
    public ReportRepository getReportRepository() {
        return this.reportRepository;
    }
}
