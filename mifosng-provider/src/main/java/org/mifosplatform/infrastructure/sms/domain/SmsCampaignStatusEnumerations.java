/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.domain;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public class SmsCampaignStatusEnumerations {
    public static EnumOptionData status(final Integer statusId) {
        return status(SmsCampaignStatus.fromInt(statusId));
    }

    public static EnumOptionData status(final SmsCampaignStatus status) {
        EnumOptionData optionData = new EnumOptionData(SmsCampaignStatus.INVALID.getValue().longValue(),
                SmsCampaignStatus.INVALID.getCode(), "Invalid");
        switch (status) {
            case INVALID:
                optionData = new EnumOptionData(SmsCampaignStatus.INVALID.getValue().longValue(),
                        SmsCampaignStatus.INVALID.getCode(), "Invalid");
                break;
            case PENDING:
                optionData = new EnumOptionData(SmsCampaignStatus.PENDING.getValue().longValue(),
                        SmsCampaignStatus.PENDING.getCode(), "Pending");
                break;
            case ACTIVE:
                optionData = new EnumOptionData(SmsCampaignStatus.ACTIVE.getValue().longValue(), SmsCampaignStatus.ACTIVE.getCode(),
                        "active");
                break;
            case CLOSED:
                optionData = new EnumOptionData(SmsCampaignStatus.CLOSED.getValue().longValue(),
                        SmsCampaignStatus.CLOSED.getCode(), "closed");
                break;

        }

        return optionData;
    }
}
