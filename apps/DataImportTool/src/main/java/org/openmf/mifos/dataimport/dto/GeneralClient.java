package org.openmf.mifos.dataimport.dto;

import java.util.ArrayList;
import com.google.gson.annotations.SerializedName;


public class GeneralClient {
	
	@SerializedName("id")
    private final Integer id;
	
	@SerializedName("displayName")
    private final String displayName;
    
	@SerializedName("officeName")
    private final String officeName;
    
	@SerializedName("activationDate")
    private final ArrayList<Integer> activationDate;
	
	@SerializedName("active")
    private final Boolean active;  

	public GeneralClient(Integer id, String displayName,  String officeName, ArrayList<Integer> activationDate, Boolean active) {
		this.id = id;
        this.displayName = displayName;
        this.activationDate = activationDate;
        this.officeName = officeName;
        this.active = active;
    }
	
	public Integer getId() {
		return this.id;
	}
	
	public String getDisplayName() {
        return this.displayName;
    }
    
    public ArrayList<Integer> getActivationDate() {
        return this.activationDate;
    }
    
    public String getOfficeName() {
        return this.officeName;
    }
    
    public Boolean isActive() {
        return this.active;
    }
}
