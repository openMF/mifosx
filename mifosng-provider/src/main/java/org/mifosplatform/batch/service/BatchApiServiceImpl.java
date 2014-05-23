package org.mifosplatform.batch.service;

import java.util.ArrayList;
import java.util.List;

import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;

public class BatchApiServiceImpl {
	
	public BatchApiServiceImpl() {
		
	}
	
	List<BatchResponse> handleBatchRequests(List<BatchRequest> requestList) {
		
		BatchRequest req = requestList.get(0);
		BatchResponse response = new BatchResponse(req.getRequestId(), 501, req.getHeaders(), req.getBody());
		
		List<BatchResponse> responseList = new ArrayList<BatchResponse>();
		responseList.add(response);
		
		return responseList;
	}
}
