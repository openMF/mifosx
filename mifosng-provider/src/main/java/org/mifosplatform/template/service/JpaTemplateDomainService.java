package org.mifosplatform.template.service;

import java.util.List;

import org.mifosplatform.template.domain.Template;
import org.mifosplatform.template.domain.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JpaTemplateDomainService implements TemplateDomainService{

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
}
