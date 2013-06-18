package org.mifosplatform.infrastructure.xbrl.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/xbrlreport")
@Component
@Scope("/singleton")
public class XBRLApiResource {
	private final PlatformSecurityContext context;

	@Autowired
	public XBRLApiResource(final PlatformSecurityContext context) {
		this.context = context;
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String updateXBRLReport() {
		return null;
	}
	
	@GET
	public String retrieveXBRLReport() {
		return null;
	}
	
	@GET
	@Path("file")
	public Response downloadXBRLFile() {
		return null;
	}
	
}
