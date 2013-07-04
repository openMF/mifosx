package org.mifosplatform.xbrl.mapping.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="m_taxonomy_mapping")
public class TaxonomyMapping extends AbstractPersistable<Long> {

	@Column(name="identifier")
	private String identifier;
	
	@Column(name="config")
	private String config;
	
	protected TaxonomyMapping() {
		// default
	}
	
	private TaxonomyMapping(final String identifier, final String config) {
		this.identifier = StringUtils.defaultIfEmpty(identifier, null);
		this.config = StringUtils.defaultIfEmpty(config, null);
	}
	
	public static TaxonomyMapping fromJson(final JsonCommand command) {
		final String identifier = command.stringValueOfParameterNamed("identifier");
		final String config = command.stringValueOfParameterNamed("config");
		return new TaxonomyMapping(identifier, config);
	}
	
	public void update(final JsonCommand command) {

        this.identifier = command.stringValueOfParameterNamed("identifier");
        this.config = command.stringValueOfParameterNamed("config");

	}
	
}
