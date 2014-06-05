package org.mifosplatform.infrastructure.smsgateway.api;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.infrastructure.smsgateway.data.SmsGatewayData;
import org.mifosplatform.infrastructure.smsgateway.service.SmsGatewayReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/smsgateway")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Component
@Scope("singleton")
public class SmsGatewayApiResource {
	private final String resourceNameForPermissions = "SMSGATEWAY";

	private final PlatformSecurityContext context;
	private final SmsGatewayReadPlatformService readPlatformService;
	private final DefaultToApiJsonSerializer<SmsGatewayData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

	@Autowired
	public SmsGatewayApiResource(final PlatformSecurityContext context, final SmsGatewayReadPlatformService readPlatformService,
			final DefaultToApiJsonSerializer<SmsGatewayData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
		this.context = context;
		this.readPlatformService = readPlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
	}

	@GET
	public String retrieveAll(@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

		final Collection<SmsGatewayData> smsGateways = this.readPlatformService.retrieveAll();

		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, smsGateways);
	}

	@POST
	public String create(final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().createSmsGateway().withJson(apiRequestBodyAsJson).build();

		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

		return this.toApiJsonSerializer.serialize(result);
	}

	@GET
	@Path("{resourceId}")
	public String retrieveOne(@PathParam("resourceId") final Long resourceId, @Context final UriInfo uriInfo) {

		final SmsGatewayData smsGateway = this.readPlatformService.retrieveOne(resourceId);

		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, smsGateway);
	}

	@PUT
	@Path("{resourceId}")
	public String update(@PathParam("resourceId") final Long resourceId, final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateSmsGateway(resourceId).withJson(apiRequestBodyAsJson).build();

		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

		return this.toApiJsonSerializer.serialize(result);
	}

	@DELETE
	@Path("{resourceId}")
	public String delete(@PathParam("resourceId") final Long resourceId) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteSmsGateway(resourceId).build();

		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

		return this.toApiJsonSerializer.serialize(result);
	}

	@POST
	@Path("{resourceId}")
	public String test(@PathParam("resourceId") final Long resourceId, @QueryParam("command") final String commandParam,
			final String apiRequestBodyAsJson) {

		final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

		CommandProcessingResult result = null;
		CommandWrapper commandRequest = null;
		if (is(commandParam, "test")) {
			commandRequest = builder.testSmsGateway(resourceId).build();
			result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		}

		if (result == null) {
			throw new UnrecognizedQueryParamException("command", commandParam, new Object[] { "test" });
		}

		return this.toApiJsonSerializer.serialize(result);
	}

	private boolean is(final String commandParam, final String commandValue) {
		return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
	}
}