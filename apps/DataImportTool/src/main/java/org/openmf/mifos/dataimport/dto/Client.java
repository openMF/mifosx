package org.openmf.mifos.dataimport.dto;

import java.util.Locale;

public class Client {

    private final transient Integer rowIndex;
    
    private final String dateFormat;
    
    private final Locale locale;
    
    private final String officeId;
    
    private final String staffId;
    
    private final String firstname;
    
    private final String middlename;
    
    private final String lastname;
    
    private final String externalId;
    
    private final String active;
    
    private final String activationDate;
    
    
    public Client(String firstname, String lastname, String middlename, String activationDate, String active, String externalId, String officeId, String staffId, Integer rowIndex ) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.middlename = middlename;
        this.activationDate = activationDate;
        this.active = active;
        this.externalId = externalId;
        this.officeId = officeId;
        this.staffId = staffId;
        this.rowIndex = rowIndex;
        this.dateFormat = "dd MMMM yyyy";
        this.locale = Locale.ENGLISH;
    }
    
    public String getFirstName() {
        return this.firstname;
    }
    
    public String getLastName() {
        return this.lastname;
    }
    
    public String getMiddleName() {
        return this.middlename;
    }
    
    public String getActivationDate() {
        return this.activationDate;
    }
    
    public String isActive() {
        return this.active;
    }
    
    public String getExternalId() {
        return this.externalId;
    }
    
    public String getOfficeId() {
        return this.officeId;
    }
    
    public String getStaffId() {
        return this.staffId;
    }
    
    public Locale getLocale() {
    	return locale;
    }
    
    public String getDateFormat() {
    	return dateFormat;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }
}
