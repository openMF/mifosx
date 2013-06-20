package org.mifosplatform.infrastructure.xbrl.data;

import org.mifosplatform.infrastructure.xbrl.domain.Unit;

public class TaxonomyData {

	private final Long id;
	private final String name;
	private final String namespace;
	private final String dimension;
	private final Unit unit;
	private final String description;
	private final String mapping;
	
	public TaxonomyData(Long id, String name, String namespace,
			String dimension, Unit unit, String description, String mapping) {
		
		this.id = id;
		this.name = name;
		this.namespace = namespace;
		this.dimension = dimension;
		this.unit = unit;
		this.description = description;
		this.mapping = mapping;
	}
	
	
}
