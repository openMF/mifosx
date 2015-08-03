/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.service.CodeValueReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.data.ClientAddressData;
import org.mifosplatform.portfolio.client.exception.DuplicateClientAddressException;
import org.mifosplatform.portfolio.client.service.ClientAddressReadPlatformService;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.resource.Singleton;

import javax.ws.rs.Path;

@Path("/clients/{clientId}/addresses")
@Component
@Scope("singleton")
public class ClientAddressApiResource {
	
	private static final Set<String> CLIENT_ADDRESS_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", 
			"clientId","addressType", "address_line", "address_line_two", "landmark", "pincode","isboth","city", "stateType","allowedAddressTypes", "allowedStateTypes"));
	
	private final String resourceNameForPermissions = "CLIENTADDRESS";
	
	private final PlatformSecurityContext context;
    private final ClientReadPlatformService clientReadPlatformService;
    private final ClientAddressReadPlatformService clientAddressReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final DefaultToApiJsonSerializer<ClientAddressData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    
    @Autowired
    
    public ClientAddressApiResource(final PlatformSecurityContext context, final ClientReadPlatformService readPlatformService,
    		final CodeValueReadPlatformService codeValueReadPlatformService,
    		final DefaultToApiJsonSerializer<ClientAddressData>toApiJsonSerializer,
    		final ApiRequestParameterHelper apiRequestParameterHelper,
    		final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,
    		final ClientAddressReadPlatformService clientAddressReadPlatformService) {
    	
    	this.context = context;
    	this.clientReadPlatformService = readPlatformService;
    	this.codeValueReadPlatformService = codeValueReadPlatformService;
    	this.toApiJsonSerializer = toApiJsonSerializer;
    	this.apiRequestParameterHelper = apiRequestParameterHelper;
    	this.commandsSourceWritePlatformService = commandSourceWritePlatformService;
    	this.clientAddressReadPlatformService = clientAddressReadPlatformService;
    	
    	
    }
    
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllClientAddresses(@Context final UriInfo uriInfo, @PathParam("clientId") final Long clientId ){
    	
    	
    	this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
    	
    	final Collection<ClientAddressData> clientAddresses = this.clientAddressReadPlatformService
                .retrieveClientAddresses(clientId);
    	
    	final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, clientAddresses, CLIENT_ADDRESS_DATA_PARAMETERS);
    	
    	
    }
    
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String newClientAddressDetails(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<CodeValueData> codeValues = this.codeValueReadPlatformService.retrieveCodeValuesByCode("Address Type");
        final Collection<CodeValueData> stateValues = this.codeValueReadPlatformService.retrieveCodeValuesByCode("State");
        
        final ClientAddressData clientAddressData = ClientAddressData.template(codeValues,stateValues);
        
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, clientAddressData, CLIENT_ADDRESS_DATA_PARAMETERS);
    }
    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createClientAddress(@PathParam("clientId") final Long clientId, final String apiRequestBodyAsJson) {

        try {
            final CommandWrapper commandRequest = new CommandWrapperBuilder().createClientAddress(clientId)
                    .withJson(apiRequestBodyAsJson).build();

            final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

            return this.toApiJsonSerializer.serialize(result);
        } catch (final DuplicateClientAddressException e) {
            DuplicateClientAddressException rethrowas = e;
            if (e.getAddressTypeId() != null) {
                // need to fetch client info
                final ClientData clientInfo = this.clientReadPlatformService.retrieveClientByAddress(e.getAddressTypeId(),
                        e.getAddress_Line());
                
                rethrowas = new DuplicateClientAddressException(clientInfo.displayName(), clientInfo.officeName(),
                        e.getAddressType(), e.getAddress_Line());
            }
            
            throw rethrowas;
        }
    }
    
    @GET
    @Path("{addressId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveClientIdentifiers(@PathParam("clientId") final Long clientId,
            @PathParam("addressId") final Long clientAddressId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        ClientAddressData clientAddressData = this.clientAddressReadPlatformService.retrieveClientAddress(clientId,
                clientAddressId);
        if (settings.isTemplate()) {
        	
        	final Collection<CodeValueData> codeValues = this.codeValueReadPlatformService.retrieveCodeValuesByCode("Address Type");
        	final Collection<CodeValueData> proofValues = this.codeValueReadPlatformService.retrieveCodeValuesByCode("State");
            clientAddressData = ClientAddressData.template(clientAddressData, codeValues, proofValues);
            
        
        }
        

        return this.toApiJsonSerializer.serialize(settings, clientAddressData, CLIENT_ADDRESS_DATA_PARAMETERS);
    }

    @PUT
    @Path("{addressId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateClientIdentifer(@PathParam("clientId") final Long clientId,
            @PathParam("addressId") final Long clientAddressId, final String apiRequestBodyAsJson) {

        try {
            final CommandWrapper commandRequest = new CommandWrapperBuilder().updateClientIdentifier(clientId, clientAddressId)
                    .withJson(apiRequestBodyAsJson).build();

            final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

            return this.toApiJsonSerializer.serialize(result);
        } catch (final DuplicateClientAddressException e) {
            DuplicateClientAddressException reThrowAs = e;
            if (e.getAddressTypeId() != null) {
                final ClientData clientInfo = this.clientReadPlatformService.retrieveClientByAddress(e.getAddressTypeId(),
                        e.getAddress_Line());
                reThrowAs = new DuplicateClientAddressException(clientInfo.displayName(), clientInfo.officeName(),
                        e.getAddressType(),e.getAddress_Line());
            }
            throw reThrowAs;
        }
    }

    @DELETE
    @Path("{addressId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteClientIdentifier(@PathParam("clientId") final Long clientId,
            @PathParam("addressId") final Long clientAddressId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteClientIdentifier(clientId, clientAddressId).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
    
    		
    		
    		
    	
    
}
