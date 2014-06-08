package org.mifosplatform.integrationtests;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.mifosplatform.integrationtests.common.BatchHelper;
import org.mifosplatform.integrationtests.common.Utils;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

/**
 * Test class for {@link org.mifosplatform.batch.command.CommandStrategyProvider}.
 * This tests the response provided by commandStrategy by injecting it with
 * a {@code BatchRequest}.
 * 
 * @author RishabhShukla
 * @see org.mifosplatform.integrationtests.common.BatchHelper
 * @see org.mifosplatform.batch.domain.BatchRequest
 */
public class BatchApiTest {

	private ResponseSpecification responseSpec;
	private RequestSpecification requestSpec;
	
	public BatchApiTest() {
		super();
	}
	
	/**
	 * Sets up the essential settings for the TEST like contentType, expectedStatusCode.
	 * It uses the '@Before' annotation provided by jUnit.
	 */
	@Before
	public void setup() {
		
		Utils.initializeRESTAssured();
		this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
		this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
		this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
	}
	
	//tests for the unimplemented command Strategies by returning 501 status code
	@Test
	public void shouldReturnStatusNotImplementedUnknownCommand() {

		final BatchRequest br = new BatchRequest();
		br.setRequestId(4711L);
		br.setRelativeUrl("/nirvana");
		br.setMethod("POST");

		List<BatchResponse> response = BatchHelper.createRequest(this.requestSpec, this.responseSpec, br);

		//verify that only 501 is returned as the status code
		for(BatchResponse resp : response) {
			Assert.assertEquals("Verifying Status code 501",(long) 501,(long) resp.getStatusCode());
		}
	}
	
	//tests for the successful response for a createClient request from createClientCommand
	@Test
	public void shouldReturnOkStatusForCreateClientCommand() {
		final BatchRequest br = new BatchRequest();
    	br.setRequestId(4712L);
		br.setRelativeUrl("clients");
		br.setMethod("POST");
		
		//generate a random externalId for the client
		String extId = "ext" + String.valueOf((10000 * Math.random())) + String.valueOf((10000 * Math.random()));
		
		final String body = "{ \"officeId\": 1, \"firstname\": \"Petra\", \"lastname\": \"Yton\"," +  
		"\"externalId\": " + extId + ",  \"dateFormat\": \"dd MMMM yyyy\", \"locale\": \"en\"," + 
		"\"active\": true, \"activationDate\": \"04 March 2009\", \"submittedOnDate\": \"04 March 2009\", \"savingsProductId\" : 1 }";
		
		br.setBody(body);
		
		List<BatchResponse> response = BatchHelper.createRequest(this.requestSpec, this.responseSpec, br);
		
		//verify that a 200 response is returned as the status code
		for(BatchResponse resp : response) {
			Assert.assertEquals("Verifying Status code 200",(long) 200,(long) resp.getStatusCode());
		}
	}	
}