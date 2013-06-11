package org.mifosplatform.template.service;

import java.util.List;

import org.mifosplatform.template.domain.Template;


public interface TemplateDomainService {

	Template save(Template template);
	
	List<Template> getAll();
	
	Template getById(Long id);
	
	void delete(Long id);
}
