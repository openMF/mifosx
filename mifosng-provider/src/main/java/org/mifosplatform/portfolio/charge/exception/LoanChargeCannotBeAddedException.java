/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.charge.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * {@link AbstractPlatformDomainRuleException} thrown when loan charge is not
 * allowed to be added to the loan.
 */
public class LoanChargeCannotBeAddedException extends AbstractPlatformDomainRuleException {

    public LoanChargeCannotBeAddedException(final String entity, final String postFix, final String defaultUserMessage,
            final Object... defaultUserMessageArgs) {
        super("error.msg." + entity + ".cannot.be.added.as." + postFix, defaultUserMessage, defaultUserMessageArgs);
    }

}