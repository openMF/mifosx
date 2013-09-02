package org.mifosplatform.xbrl.report.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="m_xbrl_namespace")
public class Namespace extends AbstractPersistable<Long> {

	@Column(name="prefix")
	private String prefix;
	
	@Column(name="url")
	private String url;
	
	protected Namespace() {
		// default
	}
	
	
}
