/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.exception;


import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SmsCampaignMustBeClosedToEditException extends AbstractPlatformDomainRuleException {

    public SmsCampaignMustBeClosedToEditException(final Long resourceId) {
        super("error.msg.sms.campaign.cannot.be.updated",
                "Campaign with identifier " + resourceId + " cannot be updated as it is not in `Closed` state.", resourceId);
    }
}
