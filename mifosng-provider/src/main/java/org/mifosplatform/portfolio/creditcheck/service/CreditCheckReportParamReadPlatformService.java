/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.creditcheck.service;

import org.mifosplatform.portfolio.creditcheck.data.CreditCheckReportParamData;

public interface CreditCheckReportParamReadPlatformService {
    CreditCheckReportParamData retrieveCreditCheckReportParameters(Long loanId, Long userId);
}
