package org.mifosplatform.infrastructure.xbrl.data;


public class TaxonomyData {

	private final Long id;
	private final String name;
	private final String namespace;
	private final String dimension;
	private final String description;
	private final String mapping;
	
	public TaxonomyData(Long id, String name, String namespace,
			String dimension, String description, String mapping) {
		
		this.id = id;
		this.name = name;
		this.namespace = namespace;
		this.dimension = dimension;
		this.description = description;
		this.mapping = mapping;
	}
	
	
}
