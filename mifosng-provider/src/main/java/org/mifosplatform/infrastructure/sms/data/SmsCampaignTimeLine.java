/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.data;

import org.joda.time.LocalDate;

public class SmsCampaignTimeLine {
    private final LocalDate submittedOnDate;
    private final String submittedByUsername;

    private final LocalDate activatedOnDate;
    private final String activatedByUsername;


    private final LocalDate closedOnDate;
    private final String closedByUsername;

    public SmsCampaignTimeLine(final LocalDate submittedOnDate, final String submittedByUsername,
                               final LocalDate activatedOnDate, final String activatedByUsername, final LocalDate closedOnDate, final String closedByUsername) {
        this.submittedOnDate = submittedOnDate;
        this.submittedByUsername = submittedByUsername;
        this.activatedOnDate = activatedOnDate;
        this.activatedByUsername = activatedByUsername;
        this.closedOnDate = closedOnDate;
        this.closedByUsername = closedByUsername;
    }
}
