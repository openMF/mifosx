package org.mifosplatform.template.service;

import java.util.List;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.template.domain.Template;
import org.mifosplatform.template.domain.TemplateRepository;
import org.mifosplatform.template.exception.TemplateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JpaTemplateDomainService implements TemplateDomainService{
	
	private static final String PROPERTY_NAME = "name"; 
	private static final String PROPERTY_TEXT = "text"; 
	private static final String PROPERTY_MAPPERS = "mappers"; 

	@Autowired
	private TemplateRepository templateRepository;

	@Override
	public List<Template> getAll() {
		
		return templateRepository.findAll();
	}

	@Override
	public Template getById(Long id) {
		
		return templateRepository.findOne(id);
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
		
		final Template template = this.templateRepository.findOne(templateId);
        if (template == null ) { throw new TemplateNotFoundException(templateId); }
        
        template.setName(command.stringValueOfParameterNamed(PROPERTY_NAME));
        template.setText(command.stringValueOfParameterNamed(PROPERTY_TEXT));
        
        String mappers = command.jsonFragment(PROPERTY_MAPPERS);    	
        template.setMappers(command.mapValueOfParameterNamed(mappers));
        
        this.templateRepository.saveAndFlush(template);

        return new CommandProcessingResultBuilder() 
                .withCommandId(command.commandId()) 
                .withEntityId(template.getId())
                .build();
	}

	@Transactional
	@Override
	public CommandProcessingResult removeTemplate(Long templateId) {
		
		final Template template = this.templateRepository.findOne(templateId);
        if (template == null ) { throw new TemplateNotFoundException(templateId); }

        this.templateRepository.delete(template);

        return new CommandProcessingResultBuilder()
                .withEntityId(templateId)
                .build();
	}
}
