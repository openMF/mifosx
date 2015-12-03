/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.creditcheck.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

@SuppressWarnings("serial")
public class CreditCheckReportParamNotFoundException extends AbstractPlatformResourceNotFoundException {

    public CreditCheckReportParamNotFoundException(final Long loanId) {
        super("error.msg.credit.check.report.param.data.empty", 
                "Credit check report param data query returned empty resultset for loan with identifier " 
        + loanId, loanId);
    }
}
