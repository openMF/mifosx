package org.openmf.mifos.dataimport.dto;

import java.util.Locale;

public class SavingsActivation {
	
	private final transient Integer rowIndex;

	 private final String activatedOnDate;
	
	 private final String dateFormat;
	
	 private final Locale locale;
	 
	 public SavingsActivation(String activatedOnDate, Integer rowIndex ) {
	        this.activatedOnDate = activatedOnDate;
	        this.rowIndex = rowIndex;
	        this.dateFormat = "dd MMMM yyyy";
	        this.locale = Locale.ENGLISH;
	    }
	 
	  public String getActivatedOnDate() {
		    return activatedOnDate;
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
