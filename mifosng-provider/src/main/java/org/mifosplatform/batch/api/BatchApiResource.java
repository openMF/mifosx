package org.mifosplatform.batch.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.mifosplatform.batch.service.BatchApiService;
import org.mifosplatform.infrastructure.core.serialization.BatchRequestJsonHelper;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Provides a REST resource for Batch Requests. This class acts as a proxy to 
 * {@link org.mifosplatform.batch.service.BatchApiService} and de-serializes 
 * the incoming JSON string to a list of {@link org.mifosplatform.batch.domain
 * .BatchRequest} type. This list is forwarded to BatchApiService which finally
 * returns a list of {@link org.mifosplatform.batch.domain.BatchResponse} type
 * which is then serialized into JSON response by this Resource class.
 * 
 * @author Rishabh Shukla
 * 
 * @see org.mifosplatform.batch.service.BatchApiService
 * @see org.mifosplatform.batch.domain.BatchRequest
 * @see org.mifosplatform.batch.domain.BatchResponse
 */
@Path("/batch")
@Component
@Scope("singleton")
public class BatchApiResource {
	
	private final PlatformSecurityContext context;
	private final ToApiJsonSerializer toApiJsonSerializer; 
	private final BatchApiService service;
	private final BatchRequestJsonHelper batchRequestJsonHelper;	
	
	/**
	 * Constructs a 'BatchApiService' with context, toApiJsonSerializer, service
	 * and batchRequestJsonHelper. 
	 * 
	 * @param context
	 * @param toApiJsonSerializer
	 * @param service
	 * @param batchRequestJsonHelper
	 */
	@Autowired
	public BatchApiResource(final PlatformSecurityContext context, final ToApiJsonSerializer toApiJsonSerializer,
			final BatchApiService service, final BatchRequestJsonHelper batchRequestJsonHelper) {
		
		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.service = service;
		this.batchRequestJsonHelper = batchRequestJsonHelper;
	}
	
	/**
	 * Provides a Rest assured POST method to get {@link BatchRequest} and returns back the 
	 * consolidated {@link BatchResponse}
	 * 
	 * @param jsonRequestString
	 * @return serialized JSON
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String handleBatchRequests(final String jsonRequestString) {
		
		//handles user authentication
		this.context.authenticatedUser();															
		
		//converts request array into BatchRequest List		
		final List<BatchRequest> requestList = this.batchRequestJsonHelper.extractList(jsonRequestString);
		
		//gets back the consolidated BatchResponse from BatchApiservice 
		final List<BatchResponse> result = service.handleBatchRequests(requestList);
		
		return this.toApiJsonSerializer.serialize(result);
		
	}
}
