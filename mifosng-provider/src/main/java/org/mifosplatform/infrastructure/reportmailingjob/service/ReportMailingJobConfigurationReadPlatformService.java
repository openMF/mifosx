/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.service;

import java.util.Collection;

import org.mifosplatform.infrastructure.reportmailingjob.data.ReportMailingJobConfigurationData;

public interface ReportMailingJobConfigurationReadPlatformService {
    /** 
     * Retrieve all report mailing job configurations
     * 
     * @return ReportMailingJobConfigurationData object
     **/
    Collection<ReportMailingJobConfigurationData> retrieveAllReportMailingJobConfigurations();
    
    /** 
     * Retrieve report mailing job configuration by name
     * 
     * @return ReportMailingJobConfigurationData object
     **/
    ReportMailingJobConfigurationData retrieveReportMailingJobConfiguration(String name);
}
