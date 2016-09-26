package org.openmf.mifos.dataimport.dto;


public class CorporateClient extends Client {

	    private final String fullname;
	    
	    
	    public CorporateClient(String fullname, String activationDate, String active, String externalId, String officeId, String staffId, Integer rowIndex ) {
	        super(null, null, null, activationDate, active, externalId, officeId, staffId, rowIndex);
	    	this.fullname = fullname;
	    }
	    
	    public String getFullName() {
	        return this.fullname;
	    }

}
