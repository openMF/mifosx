/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.creditcheck.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when credit check resources are not found.
 **/
@SuppressWarnings("serial")
public class CreditCheckNotFoundException extends AbstractPlatformResourceNotFoundException {

    public CreditCheckNotFoundException(final Long creditCheckId) {
        super("error.msg.credit.check.id.invalid", 
                "Credit check with identifier " + creditCheckId + " does not exist", creditCheckId);
    }
}
