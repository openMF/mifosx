package org.mifosplatform.batch.service;

import java.util.List;

import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;

/**
 * Provides an interface for service class, that implements the method to 
 * handle separate Batch Requests.
 * 
 * @author Rishabh Shukla
 * @see org.mifosplatform.batch.domain.BatchRequest
 * @see org.mifosplatform.batch.domain.BatchResponse
 * @see BatchApiServiceImpl
 */
public interface BatchApiService {
	
	List<BatchResponse> handleBatchRequests(List<BatchRequest> requestList);
	
}
