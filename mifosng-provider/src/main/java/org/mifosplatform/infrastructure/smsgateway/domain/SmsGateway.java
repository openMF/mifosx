package org.mifosplatform.infrastructure.smsgateway.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.smsgateway.SmsGatewayApiConstants;
import org.mifosplatform.infrastructure.smsgateway.domain.SmsGateway;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name = "m_sms_gateway")
public class SmsGateway extends AbstractPersistable<Long> {

    @Column(name = "gateway_name", nullable = false, length = 100)
    private String gatewayName;

    @Column(name = "auth_token", nullable = false)
    private String authToken;
    
    @Column(name = "url", nullable = false)
    private String url;

    public static SmsGateway newSmsGateway(final String gatewayName, final String authToken, final String url) {
        return new SmsGateway(gatewayName, authToken, url);
    }

    protected SmsGateway() {
        this.gatewayName = null;
        this.authToken = null;
        this.url = null;
    }

    private SmsGateway(final String gatewayName, final String authToken, final String url) {
        this.gatewayName = gatewayName;
        this.authToken = authToken;
        this.url = url;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);

        if (command.isChangeInStringParameterNamed(SmsGatewayApiConstants.gatewayNameParamName, this.gatewayName)) {
            final String newValue = command.stringValueOfParameterNamed(SmsGatewayApiConstants.gatewayNameParamName);
            actualChanges.put(SmsGatewayApiConstants.gatewayNameParamName, newValue);
            this.gatewayName = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(SmsGatewayApiConstants.authTokenName, this.authToken)) {
            final String newValue = command.stringValueOfParameterNamed(SmsGatewayApiConstants.authTokenName);
            actualChanges.put(SmsGatewayApiConstants.authTokenName, newValue);
            this.authToken = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(SmsGatewayApiConstants.urlParamName, this.url)) {
            final String newValue = command.stringValueOfParameterNamed(SmsGatewayApiConstants.urlParamName);
            actualChanges.put(SmsGatewayApiConstants.urlParamName, newValue);
            this.url = StringUtils.defaultIfEmpty(newValue, null);
        }

        return actualChanges;
    }
    
    public String authToken() {
        return this.authToken;
    }
    
    public String url() {
        return this.url;
    }
}
