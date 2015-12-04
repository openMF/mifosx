/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.service;

import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.SearchParameters;
import org.mifosplatform.infrastructure.reportmailingjob.data.ReportMailingJobRunHistoryData;

public interface ReportMailingJobRunHistoryReadPlatformService {
    /** 
     * Retrieve all report mailing run history with similar job id to the one passed
     * 
     * @param ReportMailingJobId -- ReportMailingJob identifier
     * @return collection of ReportMailingJobRunHistoryData objects
     **/
    Page<ReportMailingJobRunHistoryData> retrieveRunHistoryByJobId(Long reportMailingJobId, SearchParameters searchParameters);
}
