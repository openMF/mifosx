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
public class JpaTemplateDomainService implements TemplateDomainService {

	@Autowired
	private TemplateRepository templateRepository;
	
	@Override
	public Template save(Template template) {
		return templateRepository.save(template);
	}

	@Override
	public List<Template> getAll() {
		return templateRepository.findAll();
	}

	@Override
	public Template getById(Long id) {
		return templateRepository.findOne(id);
	}

	@Override
	public void delete(Long id) {
		templateRepository.delete(id);
	}

	@Transactional
	@Override
	public CommandProcessingResult createTemplate(JsonCommand command) {
		
		// TODO:
		// context.authenticatedUser();
        // this.fromApiJsonDeserializer.validateForCreate(command.json());

        //final SavingsProduct product = this.savingsProductAssembler.assemble(command);
		
		final Template template = Template.fromJson(command);

        this.templateRepository.saveAndFlush(template);

        return new CommandProcessingResultBuilder() //
                .withEntityId(template.getId()) //
                .build();
	}

	@Transactional
	@Override
	public CommandProcessingResult updateTemplate(Long templateId,
			JsonCommand command) {
		
		// TODO:
		// context.authenticatedUser();
        // this.fromApiJsonDeserializer.validateForCreate(command.json());
		
		final Template template = this.templateRepository.findOne(templateId);
        if (template == null ) { throw new TemplateNotFoundException(templateId); }
        
        //final Template template = Template.fromJson(command);
        template.setName(command.stringValueOfParameterNamed("name"));
        template.setText(command.stringValueOfParameterNamed("text"));
        
        this.templateRepository.save(template);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(template.getId()) //
                .build();
	}

	@Transactional
	@Override
	public CommandProcessingResult removeTemplate(Long templateId) {

        // TODO:
		// this.context.authenticatedUser();
		
		final Template template = this.templateRepository.findOne(templateId);
        if (template == null ) { throw new TemplateNotFoundException(templateId); }

        this.templateRepository.delete(template);

        return new CommandProcessingResultBuilder() //
                .withEntityId(templateId) //
                .build();
	}
}
