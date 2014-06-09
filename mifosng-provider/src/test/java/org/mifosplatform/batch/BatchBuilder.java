package org.mifosplatform.batch;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.mifosplatform.batch.service.BatchApiService;
import org.mockito.Mockito;

/**
 * runs a unit test for BatchApiResource by mocking the
 * BatchRequest and BatchApiService objects.
 * 
 * @author Rishabh Shukla
 */
public class BatchBuilder {

	// verify a non-empty response by BatchApiResource.	 
	@Test
	public void batchApiTest() {
		
		//mock a BatchRequest object
		final BatchRequest batchTest = Mockito.mock(BatchRequest.class);
		
		//mock a BatchApiService object
		final BatchApiService serviceTest = Mockito.mock(BatchApiService.class);
		
		List<BatchRequest> requestList = new ArrayList<>();
		requestList.add(batchTest);
		
		//call the BatchApiService using mocked objects
		final List<BatchResponse> result = serviceTest.handleBatchRequests(requestList);
	
		//verifies whether handleBatchRequests() function of BatchApiService was called
		Mockito.verify(serviceTest).handleBatchRequests(requestList);
		
		//verifies a non-empty response by the BatchApiResource
		Assert.assertNotNull(result);
	}
}
