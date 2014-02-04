package org.mifosplatform.template.service;

import java.util.List;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.template.domain.Template;
import org.mifosplatform.template.domain.TemplateEntity;
import org.mifosplatform.template.domain.TemplateType;

public interface TemplateDomainService {

    List<Template> getAll();

    List<Template> getAllByEntityAndType(TemplateEntity entity, TemplateType type);

    Template findOneById(Long id);

    Template updateTemplate(Template template);

    CommandProcessingResult createTemplate(final JsonCommand command);

    CommandProcessingResult updateTemplate(final Long templateId, final JsonCommand command);

    CommandProcessingResult removeTemplate(final Long templateId);
}
