package org.mifosplatform.xbrl.mapping.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="m_taxonomy_mapping")
public class TaxonomyMapping extends AbstractPersistable<Long> {

	@Column(name="identifier")
	private String identifier;
	
	@Column(name="config")
	private String config;
	
	
	public static TaxonomyMapping fromJson(final JsonCommand command) {
		return null;
	}
	
}
