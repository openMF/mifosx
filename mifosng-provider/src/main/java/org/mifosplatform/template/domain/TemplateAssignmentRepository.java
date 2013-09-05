package org.mifosplatform.template.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateAssignmentRepository extends JpaRepository<TemplateAssignment, Long> {
	
	List<TemplateAssignment> findAllByEntityAndType(TemplateEntity entity, TemplateType type);
}
