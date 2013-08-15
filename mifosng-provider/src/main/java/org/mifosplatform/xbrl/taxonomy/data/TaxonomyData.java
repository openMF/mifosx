package org.mifosplatform.xbrl.taxonomy.data;



public class TaxonomyData {

	private final Long id;
	private final String name;
	private final String namespace;
	private final String dimension;
	private final Integer type;
	private final Integer unit;
	private final Integer period;
	private final String description;
	
	public TaxonomyData(Long id, String name, String namespace,
			String dimension, Integer type, Integer unit, Integer period, String description) {
		
		this.id = id;
		this.name = name;
		this.namespace = namespace;
		this.dimension = dimension;
		this.type = type;
		this.unit = unit;
		this.period = period;
		this.description = description;
	}

	public Long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public String getDimension() {
		return this.dimension;
	}

	public String getDescription() {
		return this.description;
	}

	public Integer getType() {
		return this.type;
	}

	public Integer getUnit() {
		return this.unit;
	}

	public Integer getPeriod() {
		return this.period;
	}
	
	public boolean isPortfolio() {
		return this.type == 5;
	}
	
}
