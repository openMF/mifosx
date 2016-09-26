package org.openmf.mifos.dataimport.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import javax.net.ssl.SSLSession;

import org.openmf.mifos.dataimport.dto.AuthToken;
import org.openmf.mifos.dataimport.http.SimpleHttpRequest.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;


public class MifosRestClient implements RestClient {
	
	private static final Logger logger = LoggerFactory.getLogger(MifosRestClient.class);
    
    private final String baseURL;

    private final String userName;

    private final String password;

    private final String tenantId;

    private String authToken;
    
    static {
	    //for localhost testing only
	    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
	    new javax.net.ssl.HostnameVerifier(){

	    	@Override
	        public boolean verify(String hostname, @SuppressWarnings("unused") SSLSession sslSession) {
	            if (hostname.equals("localhost")) {
	                return true;
	            }
	            return false;
	        }
	    });
	}
    
    public MifosRestClient() {
    	
        baseURL = "https://localhost:8443/mifosng-provider/api/v1/"; // System.getProperty("mifos.endpoint");
        userName = "mifos"; // System.getProperty("mifos.user.id");
        password = "password"; // System.getProperty("mifos.password");
        tenantId = "default"; // System.getProperty("mifos.tenant.id");
    };

    public static final class Header {
        public static final String AUTHORIZATION = "Authorization";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String MIFOS_TENANT_ID = "X-Mifos-Platform-TenantId";
    }
    

    @Override
    public String post(String path, String payload) {
        String url = baseURL + path;
        try {

                SimpleHttpResponse response = new HttpRequestBuilder().withURL(url).withMethod(Method.POST)
                                .addHeader(Header.AUTHORIZATION, "Basic " + authToken)
                                .addHeader(Header.CONTENT_TYPE, "application/json; charset=utf-8")
                                .addHeader(Header.MIFOS_TENANT_ID, tenantId)
                                .withContent(payload).execute();
                String content = readContentAndClose(response.getContent());
            if (response.getStatus() != HttpURLConnection.HTTP_OK) 
              { 
            	throw new IllegalStateException(content);
              }
            return content;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public String get(String path) {
    	String url = baseURL + path;
    	try {
    		      SimpleHttpResponse response = new HttpRequestBuilder().withURL(url).withMethod(Method.GET)
    		    		          .addHeader(Header.AUTHORIZATION, "Basic " + authToken)
    		    		          .addHeader(Header.MIFOS_TENANT_ID,tenantId)
    		    		          .execute();
    		      String content = readContentAndClose(response.getContent());
    		      if(response.getStatus() != HttpURLConnection.HTTP_OK)
    		      {
    		    	  throw new IllegalStateException(content);
    		      }
    		      return content;
    	} catch (IOException e) {
    		  throw new IllegalStateException(e);
    	}
    }

    @Override
    public void createAuthToken() {
        String url = baseURL + "authentication?username=" + userName + "&password=" + password;
        try {
            SimpleHttpResponse response = new HttpRequestBuilder().withURL(url).withMethod(Method.POST)
                        .addHeader(Header.MIFOS_TENANT_ID, tenantId)
                        .addHeader(Header.CONTENT_TYPE, "application/json; charset=utf-8").execute();
            logger.info("Status: "+response.getStatus() + " - Auth Token Created");
            String content = readContentAndClose(response.getContent());
            AuthToken auth = new Gson().fromJson(content, AuthToken.class);
            authToken = auth.getBase64EncodedAuthenticationKey();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private String readContentAndClose(InputStream content) throws IOException {
        InputStreamReader stream = new InputStreamReader(content,"UTF-8");
        BufferedReader reader = new BufferedReader(stream);
        String data = reader.readLine();
        stream.close();
        reader.close();
        return data;
    }

}
