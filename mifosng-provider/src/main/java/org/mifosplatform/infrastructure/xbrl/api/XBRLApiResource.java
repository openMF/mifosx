package org.mifosplatform.infrastructure.xbrl.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.infrastructure.xbrl.data.XBRLData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/xbrlreport")
@Component
@Scope("/singleton")
public class XBRLApiResource {
	private final PlatformSecurityContext context;
	private final ToApiJsonSerializer<XBRLData> toApiJsonSerializer;
	

	@Autowired
	public XBRLApiResource(final PlatformSecurityContext context, 
			final ToApiJsonSerializer<XBRLData> toApiJsonSerializer) {
		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String updateXBRLReport(final String apiRequestBodyAsJson) {
		return null;
	}
	
	@GET
	public String retrieveXBRLReport(@Context final UriInfo uriInfo) {
		context.authenticatedUser();
		return null;
	}
	
	@GET
	@Path("file")
	public Response downloadXBRLFile(@Context final UriInfo uriInfo) {
		context.authenticatedUser();
		return null;
	}
	
}
