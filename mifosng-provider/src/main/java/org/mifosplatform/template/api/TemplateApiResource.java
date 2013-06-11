package org.mifosplatform.template.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.template.domain.Template;
import org.mifosplatform.template.service.TemplateDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/templates")
@Component
@Scope("singleton")
public class TemplateApiResource {
    
	private final Set<String> RESPONSE_DATA_PARAMETERS = 
			new HashSet<String>(Arrays.asList("id", "name", "text"));
	
	@Autowired
	private DefaultToApiJsonSerializer<Template> toApiJsonSerializer;
	@Autowired
	private ApiRequestParameterHelper apiRequestParameterHelper;
	@Autowired
	private TemplateDomainService templateDomainService;
	
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
    public String getTemplates(@Context final UriInfo uriInfo) {
		
		List<Template> templates = templateDomainService.getAll();
		
		final ApiRequestJsonSerializationSettings settings = 
				apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return toApiJsonSerializer.serialize(
				settings, templates, RESPONSE_DATA_PARAMETERS);
    }
	
	@GET
	@Path("{templateId}")
	@Produces({ MediaType.APPLICATION_JSON })
    public String getTemplate(@PathParam("templateId") final Long templateId, 
    		@Context final UriInfo uriInfo) {
		
		Template template = templateDomainService.getById(templateId);
		
		final ApiRequestJsonSerializationSettings settings = 
				apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return toApiJsonSerializer.serialize(
				settings, template, RESPONSE_DATA_PARAMETERS);
    }
	
	@PUT
	@Path("{templateId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
    public String saveTemplate(@PathParam("templateId") final Long templateId, 
    		@Context final UriInfo uriInfo) {
		
		throw new UnsupportedOperationException();
    }
	
	@DELETE
	@Path("{templateId}")
    public String deleteTemplate(@PathParam("templateId") final Long templateId, 
    		@Context final UriInfo uriInfo) {
		
		templateDomainService.delete(templateId);
		
		return getTemplates(uriInfo);
    }
	
	@POST
	@Path("{templateId}")
	@Produces(MediaType.TEXT_HTML)
    public String mergeTemplate(@PathParam("templateId") final Long templateId, 
    		final String apiRequestBodyAsJson, @Context final UriInfo uriInfo) {
		
		// TODO: load template and merge data from json
		
		throw new UnsupportedOperationException();
    }
}
