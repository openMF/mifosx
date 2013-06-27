package org.mifosplatform.integrationtests.common.templates;

import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class TemplateHelper {
	
    private static final String CREATE_CLIENT_URL = "/mifosng-provider/api/v1/clients?tenantIdentifier=default";

	private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;
    
    public TemplateHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
    	
    	this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }
    
    private String createURLForGettingTemplates (){
        return new String ("/mifosng-provider/api/v1/templates?tenantIdentifier=default");
    }
}
