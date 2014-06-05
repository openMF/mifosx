package org.mifosplatform.infrastructure.smsgateway.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface SmsGatewayWritePlatformService {

    CommandProcessingResult create(JsonCommand command);

    CommandProcessingResult update(Long resourceId, JsonCommand command);

    CommandProcessingResult delete(Long resourceId);
    
    CommandProcessingResult testSmsGateway(Long resourceId, JsonCommand command);
}