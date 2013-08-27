package org.mifosplatform.template.service;

import java.util.List;

import org.mifosplatform.template.domain.TemplateAssignment;
import org.mifosplatform.template.domain.TemplateEntity;
import org.mifosplatform.template.domain.TemplateType;

public interface TemplateAssignmentService {
	
	List<TemplateAssignment> getTemplatesByEntityAndType(TemplateEntity entitiy, TemplateType type);
	
	List<TemplateAssignment> getAll();
	
	TemplateAssignment getById(long id);
	
	TemplateAssignment save(TemplateAssignment templateAssignment);
	
	void delete(long id);
}
