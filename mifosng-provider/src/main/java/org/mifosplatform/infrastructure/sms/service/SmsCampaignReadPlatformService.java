/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.service;

import org.mifosplatform.infrastructure.sms.data.PreviewCampaignMessage;
import org.mifosplatform.infrastructure.sms.data.SmsBusinessRulesData;
import org.mifosplatform.infrastructure.sms.data.SmsCampaignData;

import java.util.Collection;

public interface SmsCampaignReadPlatformService {

    Collection<SmsBusinessRulesData> retrieveAll();

    SmsBusinessRulesData retrieveOneTemplate(Long resourceId);

    SmsCampaignData retrieveOne(Long resourceId);

    Collection<SmsCampaignData> retrieveAllCampaign();

    Collection<SmsCampaignData> retrieveAllScheduleActiveCampaign();

}
