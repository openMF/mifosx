package org.mifosplatform.template.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.template.domain.Template;
import org.mifosplatform.template.domain.TemplateAssignment;
import org.mifosplatform.template.domain.TemplateEntity;
import org.mifosplatform.template.domain.TemplateType;
import org.mifosplatform.template.service.TemplateAssignmentService;
import org.mifosplatform.template.service.TemplateDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path("/templateassignment")
@Component
public class TemplateAssignmentApi {
	
	private final Set<String> RESPONSE_DATA_PARAMETERS = 
			new HashSet<String>(Arrays.asList("templates"));
	
	private final TemplateAssignmentService templateAssignmentService;
	private final TemplateDomainService templateService;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final DefaultToApiJsonSerializer<TemplateAssignment> toApiJsonSerializer;
	
	@Autowired
	public TemplateAssignmentApi (TemplateAssignmentService templateAssignmentService,
			TemplateDomainService templateService,
			PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			ApiRequestParameterHelper apiRequestParameterHelper,
			DefaultToApiJsonSerializer<TemplateAssignment> toApiJsonSerializer) {
		
		this.templateAssignmentService = templateAssignmentService;
		this.templateService = templateService;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.toApiJsonSerializer = toApiJsonSerializer;
	}
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
    public String getAllAssignments(@DefaultValue("-1") @QueryParam("typeId") int typeId,
    		@DefaultValue("-1") @QueryParam("entityId") int entityId,@Context final UriInfo uriInfo) {
		
		List<TemplateAssignment> templates = new ArrayList<TemplateAssignment>();
		if (typeId != -1 && entityId != -1){
			 templates = templateAssignmentService.getTemplatesByEntityAndType(
					 TemplateEntity.values()[entityId],
					 TemplateType.values()[typeId]);
		} else {
			templates = templateAssignmentService.getAll();
		}
		
		final ApiRequestJsonSerializationSettings settings = 
				apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return toApiJsonSerializer.serialize(
				settings, templates, RESPONSE_DATA_PARAMETERS);
    }
	
	@GET
	@Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public TemplateAssignment getTemplate(@PathParam("id") final Long id) {
		
		TemplateAssignment assignment = templateAssignmentService.getById(id);

		return assignment;
    }
	
	@PUT
	@Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public TemplateAssignment updateTemplate(@PathParam("id") final Long id,
    		@QueryParam("templateId") long templateId,
    		@QueryParam("entityId") int entityId,
			@QueryParam("typeId") int typeId, 
			final String apiRequestBodyAsJson) {
		
		TemplateAssignment assignment = templateAssignmentService.getById(id);
		assignment.setEntity(TemplateEntity.values()[entityId]);
		assignment.setType(TemplateType.values()[typeId]);
		
		Template template = updateTemplate(templateId, apiRequestBodyAsJson);
		assignment.setTemplate(template);

		templateAssignmentService.save(assignment);
		return assignment;
    }

	@GET
	@Path("entities")
	@Produces({ MediaType.APPLICATION_JSON })
    public TemplateEntity[] getEntities() {
		
	    return TemplateEntity.values();
    }
	
	@GET
	@Path("types")
	@Produces({ MediaType.APPLICATION_JSON })
    public TemplateType[] getTypes() {

		return TemplateType.values();
    }
	
	@DELETE
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
    public String deleteTemplate(@PathParam("id") final Long id){
		
		templateAssignmentService.delete(id);
		
		return "{}";
    }
	
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
    public TemplateAssignment getTemplateForEntity(@QueryParam("entityId") int entityId,
			@QueryParam("typeId") int typeId,
			final String apiRequestBodyAsJson) {
		
		TemplateEntity entity = TemplateEntity.values()[entityId]; 
		TemplateType type = TemplateType.values()[typeId];
		
		Template template = createTemplate(apiRequestBodyAsJson);
		
		return templateAssignmentService.save(new TemplateAssignment(entity, type, template));
    }
	
	private Template createTemplate(String apiRequestBodyAsJson) {
		
		final CommandWrapper commandRequest = new CommandWrapperBuilder()
			.createTemplate()
			.withJson(apiRequestBodyAsJson)
			.build();
		
		final CommandProcessingResult result = 
				this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		
		
		return templateService.getById(result.resourceId());
	}
	
	private Template updateTemplate(long templateId, String apiRequestBodyAsJson) {
		
		final CommandWrapper commandRequest = new CommandWrapperBuilder()
 		.updateTemplate(templateId).withJson(apiRequestBodyAsJson)
        .build();

		final CommandProcessingResult result = 
				this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

		return templateService.getById(result.resourceId());
	}
}
