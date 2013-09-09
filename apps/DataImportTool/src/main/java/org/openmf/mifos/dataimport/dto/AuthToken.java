package org.openmf.mifos.dataimport.dto;


public class AuthToken {
    
    private String base64EncodedAuthenticationKey;

    public String getBase64EncodedAuthenticationKey() {
        return base64EncodedAuthenticationKey;
    }

    public void setBase64EncodedAuthenticationKey(String base64EncodedAuthenticationKey) {
        this.base64EncodedAuthenticationKey = base64EncodedAuthenticationKey;
    }

}
