package org.mifosng.platform.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.mifosng.platform.api.commands.ChartAccountCommand;
import org.mifosng.platform.api.data.ChartAccountData;
import org.mifosng.platform.api.data.EntityIdentifier;
import org.mifosng.platform.api.infrastructure.ApiDataConversionService;
import org.mifosng.platform.api.infrastructure.ApiJsonSerializerService;
import org.mifosng.platform.api.infrastructure.ApiParameterHelper;
import org.mifosng.platform.chartaccount.service.ChartAccountReadPlatformService;
import org.mifosng.platform.chartaccount.service.ChartAccountWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/chartaccounts")
@Component
@Scope("singleton")
public class ChartAccountApiResource 
{

	@Autowired
	private ChartAccountWritePlatformService chartAccountwritePlatformservice;
	@Autowired
	private ApiDataConversionService apiDataConversionService;
	@Autowired
	private ApiJsonSerializerService apiJsonSerializerService;
	@Autowired
	private ChartAccountReadPlatformService readPlatformService;
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveChartAccountDatas(@Context final UriInfo uriInfo) {
		
		Set<String> typicalResponseParameters = new HashSet<String>(
				Arrays.asList("chartcode" ,"description", "type"));

		Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		if (responseParameters.isEmpty()) {
			responseParameters.addAll(typicalResponseParameters);
		}
		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
		
		final Collection<ChartAccountData> datas = this.readPlatformService.retrieveAllChartAccount();
		
		return this.apiJsonSerializerService.serializeChartAccountDataToJson(prettyPrint, responseParameters, datas);
	}
	@GET
	@Path("{chartcode}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveChartAccountData(@PathParam("chartcode") final Long chartcode,@Context final UriInfo uriInfo) {

		Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
		
		final ChartAccountData data = this.readPlatformService.retrieveChartAccount(chartcode);
		
		return this.apiJsonSerializerService.serializeChartAccountDataToJson(prettyPrint, responseParameters, data);
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createChartAccount(final String jsonRequestBody) {

		final ChartAccountCommand command = this.apiDataConversionService.convertJsonToChartAccountCommand(null, jsonRequestBody);
		Long chartcode=chartAccountwritePlatformservice.createChartAccount(command);

		return Response.ok().entity(new EntityIdentifier(chartcode)).build();
		
	}
}


