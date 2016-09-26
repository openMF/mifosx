package org.openmf.mifos.dataimport.dto;

import com.google.gson.annotations.SerializedName;

public class Personnel {
     
	@SerializedName("id")
    private final Integer id;
	
	@SerializedName("firstname")
    private final String firstName;
    
	@SerializedName("lastname")
    private final String lastName;
	
	@SerializedName("officeId")
    private final Integer officeId;
    
	@SerializedName("officeName")
    private final String officeName;
    
	@SerializedName("isLoanOfficer")
    private final Boolean isLoanOfficer;   

    public Personnel(Integer id, String firstName, String lastName, Integer officeId, String officeName, Boolean isLoanOfficer) {
    	this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.officeId = officeId;
        this.officeName = officeName;
        this.isLoanOfficer = isLoanOfficer;
    }
    
    @Override
	public String toString() {
	   return "PersonnelObject [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", officeId=" + officeId +", officeName=" + officeName + ", isLoanOfficer=" + isLoanOfficer + "]";
	}
    
    public Integer getId() {
    	return this.id;
    }
    
    public String getFirstName() {
    	return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }
    
    public Integer getOfficeId() {
    	return this.officeId;
    }
    
    public String getOfficeName() {
        return this.officeName;
    }

    public Boolean isLoanOfficer() {
        return this.isLoanOfficer;
    }
}
