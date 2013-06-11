package org.mifosplatform.template.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_template")
public class Template extends AbstractPersistable<Long>{

	@Column(name = "name", nullable = false)
    private String name;

    @Column(name = "text", columnDefinition = "longtext", nullable = false)
    private String text;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
