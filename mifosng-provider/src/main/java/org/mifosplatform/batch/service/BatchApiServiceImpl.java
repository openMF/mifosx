package org.mifosplatform.batch.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.mifosplatform.batch.domain.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation for {@link BatchApiService} to iterate through all the incoming
 * requests and obtain the appropriate CommandStrategy from CommandStrategyFactory. 
 * 
 * @author Rishabh Shukla
 *
 * @see org.mifosplatform.batch.domain.BatchRequest
 * @see org.mifosplatform.batch.domain.BatchResponse
 * @see CommandStrategyFactory
 */
@Service
public class BatchApiServiceImpl implements BatchApiService{
	
	private final CommandStrategyFactory strategyFactory;
	
	/**
	 * Constructs a 'BatchApiServiceImpl' with an argument of {@link CommandStrategyFactory} type.
	 * 
	 * @param strategyFactory
	 */
	@Autowired
	public BatchApiServiceImpl(final CommandStrategyFactory strategyFactory) {
		this.strategyFactory = strategyFactory;
	}
	
	@Override
	public List<BatchResponse> handleBatchRequests(List<BatchRequest> requestList) {
		
		List<BatchResponse> responseList = new ArrayList<BatchResponse>();
		Iterator<BatchRequest> itr = requestList.iterator();
		
		while(itr.hasNext()) {
			BatchRequest request = itr.next();
			final Long requestId = request.getRequestId();
			final Integer statusCode = 200;
			final Set<Header> headers = request.getHeaders(); 
			final String body = request.getBody();
			
			BatchResponse response = new BatchResponse(requestId, statusCode, headers, body);
			responseList.add(response);
		}
		
		return responseList;
	}
}
