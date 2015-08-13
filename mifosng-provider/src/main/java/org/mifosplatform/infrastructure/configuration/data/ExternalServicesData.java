package org.mifosplatform.infrastructure.configuration.data;

public class ExternalServicesData {
	
	private final Long id;
	private final String name;
	
	public ExternalServicesData(final Long id, final String name){
		this.id =id;
		this.name = name;		
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	

}
