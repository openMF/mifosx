package org.mifosplatform.infrastructure.smsgateway.service;

import java.util.Collection;

import org.mifosplatform.infrastructure.smsgateway.data.SmsGatewayData;

public interface SmsGatewayReadPlatformService {
	Collection<SmsGatewayData> retrieveAll();

    SmsGatewayData retrieveOne(Long resourceId);
}