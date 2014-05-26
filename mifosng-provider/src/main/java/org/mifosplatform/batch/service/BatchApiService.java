package org.mifosplatform.batch.service;

import java.util.List;

import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;

/**
 * Provides an interface for service class, that implements the method to 
 * handle separate Batch Requests.
 * 
 * @author Rishabh Shukla
<<<<<<< HEAD
 * 
=======
>>>>>>> added javadocs in domain and api classes
 * @see org.mifosplatform.batch.domain.BatchRequest
 * @see org.mifosplatform.batch.domain.BatchResponse
 * @see BatchApiServiceImpl
 */
public interface BatchApiService {
	
<<<<<<< HEAD
<<<<<<< HEAD
	/**
	 * returns a list of {@link org.mifosplatform.batch.domain.BatchResponse}s by getting
	 * the appropriate CommandStrategy for every {@link org.mifosplatform.batch.domain.BatchRequest}.
=======
	/**
	 * returns a list of {@link org.mifosplatform.batch.domain.BatchResponse}s by getting
	 * the appropriate CommandStrategy for every org.mifosplatform.batch.domain.BatchRequest}.
>>>>>>> Resource and service classes implemented
	 * 
	 * @param requestList
	 * @return List<BatchResponse>
	 */
<<<<<<< HEAD
=======
>>>>>>> added javadocs in domain and api classes
=======
>>>>>>> Resource and service classes implemented
	List<BatchResponse> handleBatchRequests(List<BatchRequest> requestList);
	
}
