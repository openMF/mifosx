/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class SmsCampaignNotFound extends AbstractPlatformResourceNotFoundException{

    public SmsCampaignNotFound(final Long resourceId) {
        super("error.msg.sms.campaign.identifier.not.found", "SMS_CAMPAIGN with identifier `" + resourceId + "` does not exist", resourceId);
    }
}
