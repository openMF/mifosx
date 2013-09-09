package org.openmf.mifos.dataimport.dto;

import com.google.gson.annotations.SerializedName;

public class Currency {
	
	@SerializedName("code")
    private final String code;
    
	@SerializedName("name")
    private final String name;
	
	@SerializedName("decimalPlaces")
    private final Integer decimalPlaces;
	
	@SerializedName("inMultiplesOf")
    private final Integer inMultiplesOf;
	
	@SerializedName("displaySymbol")
    private final String displaySymbol;
    
	public Currency(String code, String name, Integer decimalPlaces, Integer inMultiplesOf, String displaySymbol) {
		this.code = code;
		this.name = name;
		this.decimalPlaces = decimalPlaces;
		this.inMultiplesOf = inMultiplesOf;
		this.displaySymbol = displaySymbol;
	}
	
	public String getCode() {
    	return this.code;
    }

    public String getName() {
        return this.name;
    }
    
    public Integer getDecimalPlaces() {
    	return this.decimalPlaces;
    }
    
    public Integer getInMultiplesOf() {
    	return this.inMultiplesOf;
    }
    
    public String getDisplaySymbol() {
        return this.displaySymbol;
    }
}
