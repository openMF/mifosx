package org.mifosplatform.infrastructure.configuration.data;

import java.io.Serializable;

public class ExternalServicesPropertiesData implements Serializable {
	private final String name;
	private final String value;
	
	public ExternalServicesPropertiesData(final String name, final String value){
		this.name = name;
		this.value= value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

}
