package org.openmf.mifos.dataimport.http;


public interface RestClient {
    
    String post(String path, String payload);
    
    String get(String path);
    
    void createAuthToken();

}
