package org.mifosplatform.template.service;

import java.util.ArrayList;
import java.util.List;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.template.domain.Template;
import org.mifosplatform.template.domain.TemplateEntity;
import org.mifosplatform.template.domain.TemplateMapper;
import org.mifosplatform.template.domain.TemplateRepository;
import org.mifosplatform.template.domain.TemplateType;
import org.mifosplatform.template.exception.TemplateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class JpaTemplateDomainService implements TemplateDomainService {
	
	private static final String PROPERTY_NAME = "name"; 
	private static final String PROPERTY_TEXT = "text"; 
	private static final String PROPERTY_MAPPERS = "mappers"; 
	private static final String PROPERTY_ENTITY = "entity"; 
	private static final String PROPERTY_TYPE = "type"; 

	@Autowired
	private TemplateRepository templateRepository;

	@Override
	public List<Template> getAll() {
		return templateRepository.findAll();
	}

	@Override
	public Template getById(Long id) {
		final Template template = templateRepository.findOne(id);
        if (template == null ) { throw new TemplateNotFoundException(id); }
		return template;
	}

	@Transactional
	@Override
	public CommandProcessingResult createTemplate(JsonCommand command) {
		final Template template = Template.fromJson(command);

        this.templateRepository.saveAndFlush(template);
        return new CommandProcessingResultBuilder()
                .withEntityId(template.getId())
                .build();
	}

	@Transactional
	@Override
	public CommandProcessingResult updateTemplate(Long templateId,
			JsonCommand command) {
		final Template template = this.getById(templateId);
        System.out.println("COMM: "+command);
        template.setName(command.stringValueOfParameterNamed(PROPERTY_NAME));
        template.setText(command.stringValueOfParameterNamed(PROPERTY_TEXT));
        template.setEntity(TemplateEntity.values()[command.integerValueSansLocaleOfParameterNamed(PROPERTY_ENTITY)]);
        template.setType(TemplateType.values()[command.integerValueSansLocaleOfParameterNamed(PROPERTY_TYPE)]);
        
        JsonArray array = command.arrayOfParameterNamed("mappers");
    	List<TemplateMapper> mappersList = new ArrayList<TemplateMapper>();
    	for(JsonElement element : array) {
    		mappersList.add(new TemplateMapper(
    				element.getAsJsonObject().get("mappersorder").getAsInt(), 
    				element.getAsJsonObject().get("mapperskey").getAsString(),
    				element.getAsJsonObject().get("mappersvalue").getAsString()));
    	}
    	template.setMappers(mappersList);
        
        this.templateRepository.saveAndFlush(template);

        return new CommandProcessingResultBuilder() 
                .withCommandId(command.commandId()) 
                .withEntityId(template.getId())
                .build();
	}

	@Transactional
	@Override
	public CommandProcessingResult removeTemplate(Long templateId) {
		final Template template = this.getById(templateId);

        this.templateRepository.delete(template);

        return new CommandProcessingResultBuilder()
                .withEntityId(templateId)
                .build();
	}

	@Override
	public Template updateTemplate(Template template) {
		return this.templateRepository.saveAndFlush(template);
	}

	@Override
	public List<Template> getAllByEntityAndType(TemplateEntity entity, TemplateType type) {

		return this.templateRepository.findByEntityAndType(entity, type);
	}
}
