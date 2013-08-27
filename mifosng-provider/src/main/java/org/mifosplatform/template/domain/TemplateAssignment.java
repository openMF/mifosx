package org.mifosplatform.template.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_template_assignment")
public class TemplateAssignment extends AbstractPersistable<Long>{

	@Enumerated
	private TemplateEntity entity;
	
	@Enumerated
	private TemplateType type;
	
	@OneToOne(cascade= {CascadeType.MERGE,CascadeType.REFRESH}, fetch=FetchType.EAGER)
	@PrimaryKeyJoinColumn
	private Template template;
	
	protected TemplateAssignment() {
		
	}
	
	public TemplateAssignment(TemplateEntity entity, TemplateType type, Template template) {
		
		this.entity = entity;
		this.type = type;
		this.template = template;
	}

	public org.mifosplatform.template.domain.TemplateEntity getEntity() {
		return this.entity;
	}

	public void setEntity(org.mifosplatform.template.domain.TemplateEntity entity) {
		this.entity = entity;
	}

	public TemplateType getType() {
		return this.type;
	}

	public void setType(TemplateType type) {
		this.type = type;
	}

	public Template getTemplate() {
		
		return this.template;
	}

	public void setTemplates(Template template) {
		this.template = template;
	}

}
