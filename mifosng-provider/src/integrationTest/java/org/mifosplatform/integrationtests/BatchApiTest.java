package org.mifosplatform.integrationtests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.mifosplatform.common.Utils;
import org.mifosplatform.integrationtests.common.BatchHelper;

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
	
	/**
	 * A test function to check the appropriate response by the Batch API, given
	 * a BatchRequest as the input.
	 */
	@Test
	public void shouldReturnStatusNotImplementedUnknownCommand() {
		
		final BatchRequest br = new BatchRequest();
		br.setRequestId(4711L);
		br.setRelativeUrl("/nirvana");
		br.setMethod("POST");
		
		List<BatchRequest> batchRequests = new ArrayList<>();
		batchRequests.add(br);
		
		final String jsonifiedRequest = BatchHelper.toJsonString(batchRequests);
		final List<BatchResponse> response = BatchHelper.postBatchRequests(this.requestSpec, this.responseSpec, jsonifiedRequest);
		
		//verifies that the response result is there
		Assert.assertNotNull(response);
		Assert.assertTrue(response.size() > 0);
		
		//verify that only 501 is returned as the status code
		for(BatchResponse resp : response) {
			Assert.assertEquals("Verifying Status code 501",(long) 501,(long) resp.getstatusCode());
		}
	}
}
