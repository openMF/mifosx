package org.mifosplatform.infrastructure.xbrl.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="m_taxonomy_mapping")
public class TaxonomyMapping extends AbstractPersistable<Long> {

	@Column(name="taxonomyId")
	private Long taxonomyId;
	
	@Column(name="mapping")
	private String mapping;
	
	
	public static TaxonomyMapping fromJson(final JsonCommand command) {
		return null;
	}
}
