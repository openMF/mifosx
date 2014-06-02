package org.mifosplatform.batch.command.internal;

import org.mifosplatform.batch.command.CommandStrategy;
import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.mifosplatform.portfolio.client.api.ClientsApiResource;
import org.springframework.beans.factory.annotation.Autowired;

public class CreateClientCommandStrategy implements CommandStrategy{

	private final ClientsApiResource clientsApiResource;
	
	@Autowired
	public CreateClientCommandStrategy(final ClientsApiResource clientsApiResource) {
		this.clientsApiResource = clientsApiResource;
	}
	
	@Override
	public BatchResponse execute(BatchRequest request) {
		
		BatchResponse response = new BatchResponse();		
		String responseBody = clientsApiResource.create(request.getBody());
		
		response.setBody(responseBody);
		response.setRequestId(request.getRequestId());
		response.setHeaders(request.getHeaders());
		response.setStatusCode(200);
		
		return response;
		
	}

}
