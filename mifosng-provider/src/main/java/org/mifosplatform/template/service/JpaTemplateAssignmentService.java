package org.mifosplatform.template.service;

import java.util.List;

import org.mifosplatform.template.domain.TemplateAssignment;
import org.mifosplatform.template.domain.TemplateAssignmentRepository;
import org.mifosplatform.template.domain.TemplateEntity;
import org.mifosplatform.template.domain.TemplateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class JpaTemplateAssignmentService implements TemplateAssignmentService{

	@Autowired
	TemplateAssignmentRepository templateAssignmentRepository;

	@Override
	public TemplateAssignment save(TemplateAssignment templateAssignment) {

		return templateAssignmentRepository.saveAndFlush(templateAssignment);
	}

	@Override
	public List<TemplateAssignment> getAll() {
		
		return templateAssignmentRepository.findAll(sortByEntityAndTypeAsc());
	}

	@Override
	public TemplateAssignment getById(long id) {
		
		return templateAssignmentRepository.findOne(id);
	}

	@Override
	public void delete(long id) {
		
		templateAssignmentRepository.delete(id);
	}
	
	private Sort sortByEntityAndTypeAsc() {
        return new Sort(Sort.Direction.ASC, "entity", "type");
    }

	@Override
	public List<TemplateAssignment> getTemplatesByEntityAndType(
			TemplateEntity entity, TemplateType type) {
		
		return templateAssignmentRepository.findAllByEntityAndType(entity, type);
	}
}
