package org.mifosplatform.infrastructure.configuration.data;

public class ExternalServicesData {
	
	private final int id;
	private final String name;
	
	public ExternalServicesData(final int id, final String name){
		this.id =id;
		this.name = name;		
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	

}
