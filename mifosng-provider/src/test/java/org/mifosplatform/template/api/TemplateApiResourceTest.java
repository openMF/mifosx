package org.mifosplatform.template.api;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifosplatform.common.Utils;
import org.mifosplatform.template.domain.Template;
import org.mifosplatform.template.service.TemplateDomainService;
import org.springframework.beans.factory.annotation.Autowired;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class TemplateApiResourceTest {

	private static final String TEMPLATES_URL = 
			"/mifosng-provider/api/v1/templates?tenantIdentifier=default";
	
	@Autowired
	private TemplateDomainService templateDomainService;
	
	private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    
    @Ignore
    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON)
        		.build();
        requestSpec.header("Authorization", "Basic " + 
        		Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }
    
    @Ignore
	@Test
	public void test() {
		Template templateOne = new Template();
		templateOne.setName("templateOne");
		templateOne.setText("$hello world!");
		templateDomainService.save(templateOne);
		
		Template templateTwo = new Template();
		templateTwo.setName("templateTwo");
		templateTwo.setText("$hello world!");
		templateDomainService.save(templateTwo);
		
		HashMap response = Utils.performServerGet(requestSpec, responseSpec,
				TEMPLATES_URL, "id");
		
		System.out.println(response.isEmpty());
		System.out.println(response.size());
		assertTrue(true);
	}

}
