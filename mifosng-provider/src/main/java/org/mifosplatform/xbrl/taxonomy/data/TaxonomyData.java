package org.mifosplatform.xbrl.taxonomy.data;


public class TaxonomyData {

	private final Long id;
	private final String name;
	private final String namespace;
	private final String dimension;
	private final String description;
	
	public TaxonomyData(Long id, String name, String namespace,
			String dimension, String description) {
		
		this.id = id;
		this.name = name;
		this.namespace = namespace;
		this.dimension = dimension;
		this.description = description;
	}
	
	
}
