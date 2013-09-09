package org.openmf.mifos.dataimport.dto;

import java.util.Locale;

public class Approval {
	
	 private final transient Integer rowIndex;

	 private final String approvedOnDate;
	
	 private final String dateFormat;
	
	 private final Locale locale;
	 
	 private final String note;
	 
	 public Approval(String approvedOnDate, Integer rowIndex ) {
	        this.approvedOnDate = approvedOnDate;
	        this.rowIndex = rowIndex;
	        this.dateFormat = "dd MMMM yyyy";
	        this.locale = Locale.ENGLISH;
	        this.note = "";
	    }
	 
	  public String getApprovedOnDate() {
		    return approvedOnDate;
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
	    
	    public String getNote() {
	    	return note;
	    }
	    
}
