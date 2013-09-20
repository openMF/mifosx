/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.charge.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * A {@link RuntimeException} thrown when Calendar resources are not found.
 */
public class ChargeParameterUpdateNotSupportedException extends AbstractPlatformDomainRuleException {

    public ChargeParameterUpdateNotSupportedException(final String postFix, final String defaultUserMessage,
            final Object... defaultUserMessageArgs) {
        super("error.msg.charge.update.of." + postFix + ".is.not.supported", defaultUserMessage, defaultUserMessageArgs);
    }
}