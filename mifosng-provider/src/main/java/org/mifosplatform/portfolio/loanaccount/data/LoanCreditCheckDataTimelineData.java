/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.data;

import org.joda.time.LocalDate;

@SuppressWarnings("unused")
public class LoanCreditCheckDataTimelineData {
    private final LocalDate triggeredOnDate;
    private final String triggeredByUsername;
    private final String triggeredByFirstname;
    private final String triggeredByLastname;
    
    public LoanCreditCheckDataTimelineData(final LocalDate triggeredOnDate, final String triggeredByUsername, 
            final String triggeredByFirstname, final String triggeredByLastname) {
        this.triggeredOnDate = triggeredOnDate;
        this.triggeredByUsername = triggeredByUsername;
        this.triggeredByFirstname = triggeredByFirstname;
        this.triggeredByLastname = triggeredByLastname;
    }
}
