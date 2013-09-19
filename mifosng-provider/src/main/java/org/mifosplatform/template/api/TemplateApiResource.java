package org.mifosplatform.template.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.map.ObjectMapper;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.template.data.TemplateData;
import org.mifosplatform.template.domain.Template;
import org.mifosplatform.template.domain.TemplateEntity;
import org.mifosplatform.template.domain.TemplateType;
import org.mifosplatform.template.service.TemplateDomainService;
import org.mifosplatform.template.service.TemplateMergeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/templates")
@Component
@Scope("singleton")
public class TemplateApiResource {
    
	private final Set<String> RESPONSE_TEMPLATES_DATA_PARAMETERS = 
			new HashSet<String>(Arrays.asList("id"));
	private final Set<String> RESPONSE_TEMPLATE_DATA_PARAMETERS = 
			new HashSet<String>(Arrays.asList("id","entities","types","template"));
	
	private final DefaultToApiJsonSerializer<Template> toApiJsonSerializer;
	private final DefaultToApiJsonSerializer<TemplateData> templateDataApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final TemplateDomainService templateService;
	private final TemplateMergeService templateMergeService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

	@Autowired
	public TemplateApiResource(
			DefaultToApiJsonSerializer<Template> toApiJsonSerializer,
			DefaultToApiJsonSerializer<TemplateData> templateDataApiJsonSerializer,
			ApiRequestParameterHelper apiRequestParameterHelper, 
			TemplateDomainService templateService,
			TemplateMergeService templateMergeService,
			PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
		
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.templateDataApiJsonSerializer = templateDataApiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.templateService = templateService;
		this.templateMergeService = templateMergeService;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
    public String getTemplates(@DefaultValue("-1") @QueryParam("typeId") int typeId,
    		@DefaultValue("-1") @QueryParam("entityId") int entityId,@Context final UriInfo uriInfo) {
		
		List<Template> templates = new ArrayList<Template>(); 
		
		if (typeId != -1 && entityId != -1){
			 templates = templateService.getAllByEntityAndType(
					 TemplateEntity.values()[entityId],
					 TemplateType.values()[typeId]);
		} else {
			templates = templateService.getAll();
		}
		
		final ApiRequestJsonSerializationSettings settings = 
				apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return toApiJsonSerializer.serialize(
				settings, templates, RESPONSE_TEMPLATES_DATA_PARAMETERS);
    }
	
	@GET
	@Path("template")
	@Produces({ MediaType.APPLICATION_JSON })
    public String getTemplatesByTemplate(@Context final UriInfo uriInfo) {
		
		TemplateData templateData = TemplateData.template();
		
		final ApiRequestJsonSerializationSettings settings = 
				apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return templateDataApiJsonSerializer.serialize(
				settings, templateData, RESPONSE_TEMPLATES_DATA_PARAMETERS);
    }
	
	@GET
	@Path("{templateId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public String getTemplate(@PathParam("templateId") final Long templateId, 
    		@Context final UriInfo uriInfo) {
		
		Template template = templateService.getById(templateId);
		
		final ApiRequestJsonSerializationSettings settings = 
				apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(
				settings, template, RESPONSE_TEMPLATES_DATA_PARAMETERS);
    }
	
	@GET
	@Path("{templateId}/template")
    @Produces({ MediaType.APPLICATION_JSON })
    public String getTemplateByTemplate(@PathParam("templateId") final Long templateId, 
    		@Context final UriInfo uriInfo) {
		
		TemplateData template = TemplateData.template(
				templateService.getById(templateId));
		
		final ApiRequestJsonSerializationSettings settings = 
				apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		System.out.println("QUERY_PARAMETER: "+uriInfo.getQueryParameters());
		System.out.println("SETTINGS: "+settings);
		System.out.println("FOO: "+this.templateDataApiJsonSerializer.serialize(
				settings, template, RESPONSE_TEMPLATE_DATA_PARAMETERS));
		
		return this.templateDataApiJsonSerializer.serialize(
				settings, template, RESPONSE_TEMPLATE_DATA_PARAMETERS);
    }
	
	@GET
	@Path("entities")
	@Produces({ MediaType.APPLICATION_JSON })
    public TemplateEntity[] getEntities() {
		
	    return TemplateEntity.values();
    }
	
	@PUT
	@Path("{templateId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
    public String saveTemplate(@PathParam("templateId") final Long templateId, 
    		final String apiRequestBodyAsJson) {
		
		final CommandWrapper commandRequest = new CommandWrapperBuilder()
		 		.updateTemplate(templateId).withJson(apiRequestBodyAsJson)
	            .build();

	    final CommandProcessingResult result = this.commandsSourceWritePlatformService.
	    		logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
	
	@DELETE
	@Path("{templateId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteTemplate(@PathParam("templateId") final Long templateId) {
		
		final CommandWrapper commandRequest = 
				new CommandWrapperBuilder().deleteTemplate(templateId).build();

		final CommandProcessingResult result = 
				this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

		return this.toApiJsonSerializer.serialize(result);
    }
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createTemplate(final String apiRequestBodyAsJson) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder()
						 			.createTemplate()
						 			.withJson(apiRequestBodyAsJson)
						 			.build();

		final CommandProcessingResult result = 
				this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

 		return this.toApiJsonSerializer.serialize(result);
    }
	
	@POST
	@Path("{templateId}")
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.TEXT_HTML})
    public String mergeTemplate(@PathParam("templateId") final Long templateId,
    		@Context final UriInfo uriInfo, final String apiRequestBodyAsJson) throws MalformedURLException, IOException {

		Template template = templateService.getById(templateId);
		
		@SuppressWarnings("unchecked")
		HashMap<String,Object> result =
		        new ObjectMapper().readValue(apiRequestBodyAsJson, HashMap.class);
		
		MultivaluedMap<String, String> parameters =  uriInfo.getQueryParameters();
		Map<String, Object> parametersMap = new HashMap<String, Object>();
		for (Map.Entry<String, List<String>> entry: parameters.entrySet()) {
			
			if(entry.getValue().size() == 1)
				parametersMap.put(entry.getKey(), entry.getValue().get(0));
			else
				parametersMap.put(entry.getKey(), entry.getValue());
		}
		
		parametersMap.put("BASE_URI", uriInfo.getBaseUri());
		parametersMap.putAll(result);
 		return templateMergeService.compile(template, parametersMap);
    }
	
}
