package org.mifosplatform.xbrl.report.api;

import java.sql.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.xbrl.mapping.service.ReadTaxonomyMappingService;
import org.mifosplatform.xbrl.report.service.XBRLBuilder;
import org.mifosplatform.xbrl.report.service.XBRLResultService;
import org.mifosplatform.xbrl.taxonomy.data.XBRLData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/xbrlreport")
@Component
@Scope("singleton")
public class XBRLReportApiResource {
	private final PlatformSecurityContext context;
	private final ToApiJsonSerializer<XBRLData> toApiJsonSerializer;
	private final ReadTaxonomyMappingService readTaxonomyMappingService;
	private final XBRLResultService xbrlResultService;
	@Autowired
	private XBRLBuilder xbrlBuilder;

	@Autowired
	public XBRLReportApiResource(final PlatformSecurityContext context, 
			final ToApiJsonSerializer<XBRLData> toApiJsonSerializer,
			final ReadTaxonomyMappingService readTaxonomyMappingService,
			final XBRLResultService xbrlResultService) {
		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.readTaxonomyMappingService = readTaxonomyMappingService;
		this.xbrlResultService = xbrlResultService;
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String updateXBRLReport(final String apiRequestBodyAsJson) {
		return null;
	}
	
	@GET
	@Produces({ MediaType.APPLICATION_XML })
	public String retrieveXBRLReport(@QueryParam("startDate") final Date startDate, 
			@QueryParam("endDate") final Date endDate, 
			@QueryParam("currency") final String currency, 
			@Context final UriInfo uriInfo) {
		
		context.authenticatedUser();

		String xbrl = xbrlBuilder.build(this.xbrlResultService.getXBRLResult(startDate, endDate, currency));
		return xbrl;
	}
	
	@GET
	@Path("file")
	public Response downloadXBRLFile(@Context final UriInfo uriInfo) {
		context.authenticatedUser();
		return null;
	}
	
}
