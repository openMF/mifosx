package org.mifosng.platform.api;

import java.util.Collection;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.mifosng.platform.api.commands.StaffCommand;
import org.mifosng.platform.api.data.EntityIdentifier;
import org.mifosng.platform.api.data.StaffData;
import org.mifosng.platform.api.infrastructure.ApiDataConversionService;
import org.mifosng.platform.api.infrastructure.ApiJsonSerializerService;
import org.mifosng.platform.api.infrastructure.ApiParameterHelper;
import org.mifosng.platform.staff.service.StaffReadPlatformService;
import org.mifosng.platform.staff.service.StaffWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/staff")
@Component
@Scope("singleton")
public class StaffApiResource {

	@Autowired
	private StaffReadPlatformService readPlatformService;

	@Autowired
	private StaffWritePlatformService writePlatformService;

	@Autowired
	private ApiDataConversionService apiDataConversionService;

	@Autowired
	private ApiJsonSerializerService apiJsonSerializerService;

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveStaffs(@Context final UriInfo uriInfo) {

		Set<String> responseParameters = ApiParameterHelper
				.extractFieldsForResponseIfProvided(uriInfo
						.getQueryParameters());
		boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo
				.getQueryParameters());

		final Collection<StaffData> staff = this.readPlatformService
				.retrieveAllStaff();

		return this.apiJsonSerializerService.serializeStaffDataToJson(
				prettyPrint, responseParameters, staff);
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createStaff(final String jsonRequestBody) {

		final StaffCommand command = this.apiDataConversionService
				.convertJsonToStaffCommand(null, jsonRequestBody);

		final Long staffId = this.writePlatformService.createStaff(command);

		return Response.ok().entity(new EntityIdentifier(staffId)).build();
	}

	@GET
	@Path("{staffId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retreiveStaff(@PathParam("staffId") final Long staffId,
			@Context final UriInfo uriInfo) {

		final Set<String> responseParameters = ApiParameterHelper
				.extractFieldsForResponseIfProvided(uriInfo
						.getQueryParameters());
		final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo
				.getQueryParameters());

		final StaffData staff = this.readPlatformService.retrieveStaff(staffId);

		return this.apiJsonSerializerService.serializeStaffDataToJson(
				prettyPrint, responseParameters, staff);
	}

	@PUT
	@Path("{staffId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateStaff(@PathParam("staffId") final Long staffId,
			final String jsonRequestBody) {

		final StaffCommand command = this.apiDataConversionService
				.convertJsonToStaffCommand(staffId, jsonRequestBody);

		final Long entityId = this.writePlatformService.updateStaff(command);

		return Response.ok().entity(new EntityIdentifier(entityId)).build();
	}
}