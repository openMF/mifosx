/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.security.exception;

import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;

/**
 * A {@link RuntimeException} that is thrown in the case where a user does not
 * have sufficient authorization to execute operation on platform.
 */
public class ResetPasswordException extends PlatformApiDataValidationException {

    public ResetPasswordException() {
        super("error.msg.password.outdated","The current user password is too old, it needs to be reset",null);
    }
}