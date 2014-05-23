package org.mifosplatform.batch.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.mifosplatform.batch.service.BatchApiService;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;

/**
 * Provides a REST resource for Batch Requests. This class acts as a proxy to 
 * {@link org.mifosplatform.batch.service.BatchApiService} and de-serializes 
 * the incoming JSON string to a list of {@link org.mifosplatform.batch.domain.BatchRequest}
 * type. This list is forwarded to BatchApiService which finally returns a list of 
 * {@link org.mifosplatform.batch.domain.BatchResponse} type which is then serialized into
 * JSON response by this Resource class.
 * 
 * @author Rishabh Shukla
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
	private final FromJsonHelper fromJsonHelper;
	private final BatchApiService service;
	
	public BatchApiResource(final PlatformSecurityContext context, final ToApiJsonSerializer toApiJsonSerializer, final BatchApiService service,final FromJsonHelper fromJsonHelper) {
		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.service = service;
		this.fromJsonHelper = fromJsonHelper;
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String handleBatchRequests(final String jsonRequestString) {
		
		this.context.authenticatedUser();															//handles user authentication
		
		final List<BatchRequest> requestList = new ArrayList<BatchRequest>();
		final JsonArray jsonList= this.fromJsonHelper.parse(jsonRequestString).getAsJsonArray();	//converts request array into json array
		Iterator itr = jsonList.iterator();											
		
		//iterate through all the requests and add those to the list
		while(itr.hasNext()) {
			requestList.add(this.fromJsonHelper.fromJson(itr.next().toString(), BatchRequest.class));
		}
		
		final List<BatchResponse> result = service.handleBatchRequests(requestList); 
		
		return this.toApiJsonSerializer.serialize(result);
	}
}
