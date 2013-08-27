package org.mifosplatform.template.service;

import java.util.List;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.template.domain.Template;


public interface TemplateDomainService {
	
	List<Template> getAll();
	
	Template getById(Long id);
	
	Template updateTemplate(Template template);
	
    CommandProcessingResult createTemplate(final JsonCommand command);

    CommandProcessingResult updateTemplate(final Long templateId, final JsonCommand command);

    CommandProcessingResult removeTemplate(final Long templateId);
}
