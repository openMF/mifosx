package org.mifosplatform.infrastructure.smsgateway.data;

import org.mifosplatform.infrastructure.smsgateway.data.SmsGatewayData;

public class SmsGatewayData {
	@SuppressWarnings("unused")
	private final Long id;
	@SuppressWarnings("unused")
	private final String gatewayName;
	@SuppressWarnings("unused")
	private final String authToken;
	@SuppressWarnings("unused")
	private final String url;

	public static SmsGatewayData instance(final Long id, final String gatewayName, final String authToken, final String url) {
		return new SmsGatewayData(id, gatewayName, authToken, url);
	}

	private SmsGatewayData(final Long id, final String gatewayName, final String authToken, final String url) {
		this.id = id;
		this.gatewayName = gatewayName;
		this.authToken = authToken;
		this.url = url;
	}
}
