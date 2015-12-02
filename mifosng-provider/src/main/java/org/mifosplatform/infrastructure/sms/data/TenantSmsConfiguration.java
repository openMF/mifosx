/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.data;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

/** 
 * immutable object representing a tenant's SMS configuration
 **/
public class TenantSmsConfiguration {
    private final String apiAuthUsername;
    private final String apiAuthPassword;
    private final String apiBaseUrl;
    private final String sourceAddress;
    private final Integer smsCredits;
    private final String countryCallingCode;
    private final static String API_AUTH_USERNAME = "API_AUTH_USERNAME";
    private final static String API_AUTH_PASSWORD = "API_AUTH_PASSWORD";
    private final static String API_BASE_URL = "API_BASE_URL";
    private final static String SMS_SOURCE_ADDRESS = "SMS_SOURCE_ADDRESS";
    private final static String SMS_CREDITS = "SMS_CREDITS";
    private final static String COUNTRY_CALLING_CODE = "COUNTRY_CALLING_CODE";
    
    /**
     * @param apiAuthUsername
     * @param apiAuthPassword
     * @param apiBaseUrl
     * @param sourceAddress
     * @param smsCredits
     * @param countryCallingCode
     */
    private TenantSmsConfiguration(String apiAuthUsername, String apiAuthPassword, String apiBaseUrl,
            String sourceAddress, Integer smsCredits, String countryCallingCode) {
        this.apiAuthUsername = apiAuthUsername;
        this.apiAuthPassword = apiAuthPassword;
        this.apiBaseUrl = apiBaseUrl;
        this.sourceAddress = sourceAddress;
        this.smsCredits = smsCredits;
        this.countryCallingCode = countryCallingCode;
    }
    
    /**
     * @param smsConfigurationDataCollection
     * @return {@link TenantSmsConfiguration}
     */
    public static TenantSmsConfiguration instance(final Collection<SmsConfigurationData> smsConfigurationDataCollection) {
        final String apiAuthUsername = getConfigurationValue(smsConfigurationDataCollection, API_AUTH_USERNAME);
        final String apiAuthPassword = getConfigurationValue(smsConfigurationDataCollection, API_AUTH_PASSWORD);
        final String apiBaseUrl = getConfigurationValue(smsConfigurationDataCollection, API_BASE_URL);
        final String sourceAddress = getConfigurationValue(smsConfigurationDataCollection, SMS_SOURCE_ADDRESS);
        final String smsCreditsString = getConfigurationValue(smsConfigurationDataCollection, SMS_CREDITS);
        Integer smsCredits = null;
        
        if (smsCreditsString != null) {
            smsCredits = Integer.parseInt(smsCreditsString);
        }
        
        final String countryCallingCode = getConfigurationValue(smsConfigurationDataCollection, COUNTRY_CALLING_CODE);
        
        return new TenantSmsConfiguration(apiAuthUsername, apiAuthPassword, apiBaseUrl, sourceAddress, smsCredits, 
                countryCallingCode);
    }
    
    /**
     * @param smsConfigurationDataCollection
     * @param configurationName
     * @return {@link SmsConfigurationData} value
     */
    private static String getConfigurationValue(final Collection<SmsConfigurationData> smsConfigurationDataCollection, 
            final String configurationName) {
        String configurationData = null;
        
        for (SmsConfigurationData smsConfigurationData : smsConfigurationDataCollection) {
            if (StringUtils.equals(configurationName, smsConfigurationData.getName())) {
                
                configurationData = smsConfigurationData.getValue();
                break;
            }
        }
        
        return configurationData;
    }

    /**
     * @return the apiAuthUsername
     */
    public String getApiAuthUsername() {
        return apiAuthUsername;
    }

    /**
     * @return the apiAuthPassword
     */
    public String getApiAuthPassword() {
        return apiAuthPassword;
    }

    /**
     * @return the apiBaseUrl
     */
    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    /**
     * @return the sourceAddress
     */
    public String getSourceAddress() {
        return sourceAddress;
    }

    /**
     * @return the smsCredits
     */
    public Integer getSmsCredits() {
        return smsCredits;
    }

    /**
     * @return the countryCallingCode
     */
    public String getCountryCallingCode() {
        return countryCallingCode;
    }
}
