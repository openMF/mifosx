package org.mifosplatform.integrationtests;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.ExternalServicesConfigurationHelper;
import org.mifosplatform.integrationtests.common.Utils;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
public class ExternalServicesConfigurationTest {
	
	 private ResponseSpecification responseSpec;
	 private RequestSpecification requestSpec;
	 private ExternalServicesConfigurationHelper externalServicesConfigurationHelper;
	 private ResponseSpecification httpStatusForidden;
	 
	 @Before
	 public void setup() {
		 Utils.initializeRESTAssured();
		 this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
		 this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
		 this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
		 this.httpStatusForidden = new ResponseSpecBuilder().expectStatusCode(403).build();
		 
	 }
	 
	 @Test
	 public void testExternalServicesConfiguration(){
		 this.externalServicesConfigurationHelper = new ExternalServicesConfigurationHelper(this.requestSpec, this.responseSpec);	 
		 String configName = "s3_access_key";
		 ArrayList<HashMap> externalServicesConfig = this.externalServicesConfigurationHelper.getExternalServicesConfigurationByServiceName(requestSpec, responseSpec,
				 "S3");
		 Assert.assertNotNull(externalServicesConfig);
		 for (Integer  configIndex= 0; configIndex < (externalServicesConfig.size()); configIndex++) {
			 String name = (String)externalServicesConfig.get(configIndex).get("name");
			 String value = null;
			 if(name.equals(configName)){
				 value = (String)externalServicesConfig.get(configIndex).get("value");
				 String newValue = "test";
				 System.out.println(name + ":" + value);
				 HashMap arrayListValue = this.externalServicesConfigurationHelper.updateValueForExternaServicesConfiguration(requestSpec, responseSpec, "S3", name, newValue);
				 Assert.assertNotNull(arrayListValue.get("value"));
				 HashMap arrayListValue1 =this.externalServicesConfigurationHelper.updateValueForExternaServicesConfiguration(requestSpec, responseSpec, "S3", name, value);
				 Assert.assertNotNull(arrayListValue1.get("value"));
			 }
			 
			 
		 }
		 
	 }

}
