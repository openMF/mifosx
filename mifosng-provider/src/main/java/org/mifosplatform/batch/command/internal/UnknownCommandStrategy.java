package org.mifosplatform.batch.command.internal;

import org.mifosplatform.batch.command.CommandStrategy;
import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;

public class UnknownCommandStrategy implements CommandStrategy {

	@Override
	public BatchResponse execute(BatchRequest batchRequest) {
		// TODO Auto-generated method stub
		final BatchResponse batchResponse = new BatchResponse();
		
		batchResponse.setRequest(batchRequest.getRequestId());
		batchResponse.setStatusCode(501);
		
		return batchResponse;
	}

	
}
