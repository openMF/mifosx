/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.api.JsonQuery;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.jobs.exception.JobExecutionException;
import org.mifosplatform.infrastructure.sms.data.PreviewCampaignMessage;

public interface SmsCampaignWritePlatformService {

    CommandProcessingResult create(JsonCommand command);

    CommandProcessingResult update(Long resourceId, JsonCommand command);

    CommandProcessingResult delete(Long resourceId);

    CommandProcessingResult activateSmsCampaign(Long campaignId, JsonCommand command);

    CommandProcessingResult closeSmsCampaign(Long campaignId, JsonCommand command);

    CommandProcessingResult reactivateSmsCampaign(Long campaignId,JsonCommand command);

    void storeTemplateMessageIntoSmsOutBoundTable() throws JobExecutionException;

    PreviewCampaignMessage previewMessage(JsonQuery query);


}
