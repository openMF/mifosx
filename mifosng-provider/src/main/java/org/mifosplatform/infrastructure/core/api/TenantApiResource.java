package org.mifosplatform.infrastructure.core.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.TenantCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.TenantData;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/tenants")
@Component
@Scope("singleton")
public class TenantApiResource {
	
	private final DefaultToApiJsonSerializer<TenantData> toApiJsonSerializer;
	private final TenantCommandSourceWritePlatformService commandsSourceWritePlatformService;
	
	@Autowired
	public TenantApiResource(final DefaultToApiJsonSerializer<TenantData> toApiJsonSerializer,
			final TenantCommandSourceWritePlatformService commandsSourceWritePlatformService) {
		
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createTenant(final String apiRequestBodyAsJson) {
		
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createTenant().withJson(apiRequestBodyAsJson).build();
		
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		
		return this.toApiJsonSerializer.serialize(result);
	}
}
