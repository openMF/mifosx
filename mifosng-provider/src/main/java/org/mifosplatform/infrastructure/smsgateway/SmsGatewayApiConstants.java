package org.mifosplatform.infrastructure.smsgateway;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SmsGatewayApiConstants {
	public static final String RESOURCE_NAME = "smsgateway";

    // request and update parameters
    public static final String idParamName = "id";
    public static final String gatewayNameParamName = "gatewayName";
    public static final String authTokenName = "authToken";
    public static final String urlParamName = "url";

    //test parameters
    public static final String localeParamName = "locale";
    
    // response parameters
    public static final String statusParamName = "status";

    public static final Set<String> CREATE_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(gatewayNameParamName, authTokenName, urlParamName));

    public static final Set<String> UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(gatewayNameParamName, authTokenName, urlParamName));

    public static final Set<String> TEST_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(localeParamName));


}
