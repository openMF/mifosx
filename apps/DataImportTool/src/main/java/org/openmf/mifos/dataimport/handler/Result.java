package org.openmf.mifos.dataimport.handler;

import java.util.ArrayList;
import java.util.List;


public class Result {
	  
    private List<String> errors = new ArrayList<String>();
    
    public void addError(String message) {
        errors.add(message);
    }
    
    public List<String> getErrors() {
        return errors;
    }

    public boolean isSuccess() {
        return errors.isEmpty();
    }

}
