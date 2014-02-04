/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanproduct.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * {@link AbstractPlatformDomainRuleException} thrown when a value is not within
 * minimum and maximum values.
 * 
 */
public class NotInMinMaxRangeException extends AbstractPlatformDomainRuleException {

    public NotInMinMaxRangeException(final String entity, final String postFix, final String defaultUserMessage,
            final Object... defaultUserMessageArgs) {
        super("validation.msg." + entity + "." + postFix + ".is.not.within.min.max.range", defaultUserMessage, defaultUserMessageArgs);
    }
}