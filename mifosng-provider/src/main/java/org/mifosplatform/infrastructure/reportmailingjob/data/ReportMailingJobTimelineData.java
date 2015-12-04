/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.data;

import org.joda.time.LocalDate;

/** 
 * Immutable data object represent the timeline events of a report mailing job (creation)
 **/
@SuppressWarnings("unused")
public class ReportMailingJobTimelineData {
    private final LocalDate createdOnDate;
    private final String createdByUsername;
    private final String createdByFirstname;
    private final String createdByLastname;
    
    public ReportMailingJobTimelineData(final LocalDate createdOnDate, final String createdByUsername, final String createdByFirstname, 
            final String createdByLastname) {
        this.createdOnDate = createdOnDate;
        this.createdByUsername = createdByUsername;
        this.createdByFirstname = createdByFirstname;
        this.createdByLastname = createdByLastname;
    }
}
