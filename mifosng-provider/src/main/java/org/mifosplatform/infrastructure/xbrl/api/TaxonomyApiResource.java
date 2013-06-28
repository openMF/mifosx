package org.mifosplatform.infrastructure.xbrl.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.infrastructure.xbrl.data.TaxonomyData;
import org.mifosplatform.infrastructure.xbrl.service.ReadTaxonomyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/taxonomy")
@Component
@Scope("/singleton")
public class TaxonomyApiResource {

	private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(
			Arrays.asList("taxonomyId", "name", "namespace", "dimension",
					"description"));
	
	private final PlatformSecurityContext context;
	private final ToApiJsonSerializer<TaxonomyData> toApiJsonSerializer;
	private final ReadTaxonomyService readTaxonomyService;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	
	@Autowired
	public TaxonomyApiResource(final PlatformSecurityContext context, 
			final ToApiJsonSerializer<TaxonomyData> toApiJsonSerializer,
			final ReadTaxonomyService readTaxonomyService,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final ApiRequestParameterHelper apiRequestParameterHelper) {
		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.readTaxonomyService = readTaxonomyService;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
	}
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String retrieveTaxonomyList(@Context final UriInfo uriInfo) {
		context.authenticatedUser();
		List<TaxonomyData> taxonomyDatas = this.readTaxonomyService.retrieveAllTaxonomyMapping();
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, taxonomyDatas, RESPONSE_DATA_PARAMETERS);
	}
	
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String updateTaxonomyMapping(final String jsonRequestBody) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateTaxonomyMapping().withJson(jsonRequestBody).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
	}
	
}
