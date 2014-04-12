package org.mifosplatform.infrastructure.smsgateway.domain;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.smsgateway.SmsGatewayApiConstants;
import org.mifosplatform.infrastructure.smsgateway.domain.SmsGateway;
import org.mifosplatform.infrastructure.smsgateway.domain.SmsGatewayRepository;
import org.mifosplatform.infrastructure.smsgateway.exception.SmsGatewayNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;

@Component
public class SmsGatewayAssembler {
    private final SmsGatewayRepository smsGatewayRepository;
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public SmsGatewayAssembler(final SmsGatewayRepository smsGatewayRepository, final FromJsonHelper fromApiJsonHelper) {
        this.smsGatewayRepository = smsGatewayRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public SmsGateway assembleFromJson(final JsonCommand command) {

        final JsonElement element = command.parsedJson();
        
        final String gatewayName = this.fromApiJsonHelper.extractStringNamed(SmsGatewayApiConstants.gatewayNameParamName, element);
        final String authToken = this.fromApiJsonHelper.extractStringNamed(SmsGatewayApiConstants.authTokenName, element);
        final String url = this.fromApiJsonHelper.extractStringNamed(SmsGatewayApiConstants.urlParamName, element);
        
        return SmsGateway.newSmsGateway(gatewayName, authToken, url);
    }

    public SmsGateway assembleFromResourceId(final Long resourceId) {
        final SmsGateway smsGateway = this.smsGatewayRepository.findOne(resourceId);
        if (smsGateway == null) { throw new SmsGatewayNotFoundException(resourceId); }
        return smsGateway;
    }
}