package org.mifosplatform.integrationtests.common;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

/**
 * Helper class for {@link org.mifosplatform.integrationtests.BatchApiTest}.
 * It takes care of creation of {@code BatchRequest} list and posting this
 * list to the server.
 * 
 * @author Rishabh Shukla
 * @see org.mifosplatform.integrationtests.BatchApiTest
 */
public class BatchHelper {

	private static final String BATCH_API_URL = "/mifosng-provider/api/v1/batch?tenantIdentifier=default";
	
	private BatchHelper() {
		super();
	}
	
	/**
	 * returns a JSON String for a list of {@code BatchRequest}s
	 * 
	 * @param batchRequests
	 * @return JSON String of BatchRequest
	 */
	public static String toJsonString(final List<BatchRequest> batchRequests) {
		return new Gson().toJson(batchRequests);
	}

	/**
	 * returns the converted string response into JSON.
	 * 
	 * @param json
	 * @return List<BatchResponse>
	 */
    private static List<BatchResponse> fromJsonString(final String json) {
        return new Gson().fromJson(json, new TypeToken<List<BatchResponse>>(){}.getType());
    }
    

	/**
	 * returns a list of BatchResponse by posting the jsonified
	 * BatchRequest to the server.
	 * 
	 * @param requestSpec
	 * @param responseSpec
	 * @param jsonifiedBatchRequests
	 * @return a list of BatchResponse
	 */
	public static List<BatchResponse> postBatchRequests(final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec, final String jsonifiedBatchRequests) {
		final String response = Utils.performServerPost(requestSpec, responseSpec, BATCH_API_URL, jsonifiedBatchRequests, null);
	    return BatchHelper.fromJsonString(response);
	}
	
    /**
     * returns a BatchResponse based on the given BatchRequest, by posting the request
     * to the server.
     * 
     * @param BatchRequest
     * @return List<BatchResponse>
     */
    public static List<BatchResponse> createRequest(final RequestSpecification requestSpec,
    		final ResponseSpecification responseSpec, BatchRequest br) {
    	
    	List<BatchRequest> batchRequests = new ArrayList<>();
		batchRequests.add(br);
		
		final String jsonifiedRequest = BatchHelper.toJsonString(batchRequests);
		final List<BatchResponse> response = BatchHelper.postBatchRequests(requestSpec, responseSpec, jsonifiedRequest);
		
		//verifies that the response result is there
		Assert.assertNotNull(response);
		Assert.assertTrue(response.size() > 0);
		
		return response;
    }
}