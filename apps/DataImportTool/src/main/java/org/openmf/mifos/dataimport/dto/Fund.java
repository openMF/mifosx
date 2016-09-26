package org.openmf.mifos.dataimport.dto;

import com.google.gson.annotations.SerializedName;

public class Fund {
	
	@SerializedName("id")
    private final Integer id;
    
	@SerializedName("name")
    private final String name;
    
	public Fund(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Integer getId() {
    	return this.id;
    }

    public String getName() {
        return this.name;
    }
}
