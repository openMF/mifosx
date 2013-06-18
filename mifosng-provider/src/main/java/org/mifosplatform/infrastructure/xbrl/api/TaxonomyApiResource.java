package org.mifosplatform.infrastructure.xbrl.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/taxonomy")
@Component
@Scope("/singleton")
public class TaxonomyApiResource {

	private final PlatformSecurityContext context;
	
	@Autowired
	public TaxonomyApiResource(final PlatformSecurityContext context) {
		this.context = context;
	}
	
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String updateTaxonomyMapping() {
		return null;
	}
	
	@GET
	public String retrieveTaxonomyMapping(){
		return null;
	}
}
