package org.openmf.mifos.dataimport.dto;

import com.google.gson.annotations.SerializedName;

public class Type {
	
	@SerializedName("id")
	private final Integer id;
	
	@SerializedName("code")
	private final String code;
	
	@SerializedName("value")
	private final String value;

	public Type(Integer id, String code, String value) {
		this.id = id;
		this.code = code;
		this.value = value;
	}
	
	public Integer getId() {
    	return this.id;
    }
	
	public String getCode() {
    	return this.code;
    }
	
	public String getValue() {
    	return this.value;
    }

}
