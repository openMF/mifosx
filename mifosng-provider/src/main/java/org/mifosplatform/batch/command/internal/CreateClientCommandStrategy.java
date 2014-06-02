package org.mifosplatform.batch.command.internal;

import org.mifosplatform.batch.command.CommandStrategy;
import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.exception.PlatformInternalServerException;
import org.mifosplatform.infrastructure.core.exception.UnsupportedParameterException;
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
		String responseBody;
		
		try {
			
			responseBody = clientsApiResource.create(request.getBody());
			response.setBody(responseBody);
			
		} catch (AbstractPlatformResourceNotFoundException e) {
			
			response.setStatusCode(404);
			response.setBody("error : " + e.toString());
			
		} catch (UnsupportedParameterException e) {
			
			response.setStatusCode(400);
			response.setBody("error : " + e.toString());
			
		} catch (PlatformDataIntegrityException e) {
			
			response.setStatusCode(403);
			response.setBody("error : " + e.toString());
			
		} catch (PlatformInternalServerException e) {
			
			response.setStatusCode(500);
			response.setBody("error : " + e.toString());
			
		}		
		
		response.setRequestId(request.getRequestId());
		response.setHeaders(request.getHeaders());
		
		if(response.getStatusCode() == null)
			response.setStatusCode(200);
		
		return response;		
	}

}
