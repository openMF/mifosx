package org.openmf.mifos.dataimport.dto;

import java.util.ArrayList;
import com.google.gson.annotations.SerializedName;

public class Office {
    
	@SerializedName("id")
    private final Integer id;
    
	@SerializedName("name")
    private final String name;
    
	@SerializedName("externalId")
    private final String externalId;
    
	@SerializedName("openingDate")
    private final ArrayList<Integer> openingDate;
    
	@SerializedName("parentName")
    private final String parentName;
	
	@SerializedName("hierarchy")
    private final String hierarchy;

    public Office(Integer id, String name, String externalId, ArrayList<Integer> openingDate, String parentName, String hierarchy ) {
        this.id = id;
        this.name = name;
        this.parentName = parentName;
        this.externalId = externalId;
        this.openingDate = openingDate;
        this.hierarchy = hierarchy;
    }
    
    @Override
	public String toString() {
	   return "OfficeObject [id=" + id + ", name=" + name + ", externalId=" + externalId + ", openingDate=" + openingDate + ", parentName=" + parentName + "]";
	}
    
    public Integer getId() {
    	return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getParentName() {
        return this.parentName;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public ArrayList<Integer> getOpeningDate() {
        return this.openingDate;
    }
    
    public String getHierarchy() {
        return this.hierarchy;
    }

}
