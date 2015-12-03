/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class SmsBusinessRuleNotFound extends AbstractPlatformResourceNotFoundException {

    public SmsBusinessRuleNotFound(final Long resourceId) {
        super("error.msg.sms.business.rule.not.found", "SMS business rule with identifier `" + resourceId + "` does not exist", resourceId);
    }
}
