/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.template.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.io.CharStreams;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.mifosplatform.infrastructure.dataqueries.data.GenericResultsetData;
import org.mifosplatform.infrastructure.dataqueries.service.GenericDataService;
import org.mifosplatform.infrastructure.dataqueries.service.ReadReportingService;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.template.domain.Template;
import org.mifosplatform.useradministration.domain.AppUser;
import org.pentaho.reporting.libraries.base.util.LinkedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

@Service
public class TemplateMergeService {
	private final static Logger logger = LoggerFactory.getLogger(TemplateMergeService.class);
	

    // private final FromJsonHelper fromApiJsonHelper;
    private Map<String, Object> scopes;
    private String authToken;

    // @Autowired
    // public TemplateMergeService(final FromJsonHelper fromApiJsonHelper) {
    // this.fromApiJsonHelper = fromApiJsonHelper;
    //

	private Map<String,Object> smsParams;
	
    public void setAuthToken(final String authToken) {
        //final String auth = ThreadLocalContextUtil.getAuthToken();
    	this.authToken =  authToken;
    }
    

    public String compile(final Template template, final Map<String, Object> scopes) throws MalformedURLException, IOException {
        this.scopes = scopes;
        this.scopes.put("static", TemplateMergeService.now());
        
        final MustacheFactory mf = new DefaultMustacheFactory();
        final Mustache mustache = mf.compile(new StringReader(template.getText()), template.getName());

        final Map<String, Object> mappers = getCompiledMapFromMappers(template.getMappersAsMap());
        this.scopes.putAll(mappers);

        expandMapArrays(scopes);

        final StringWriter stringWriter = new StringWriter();
        mustache.execute(stringWriter, this.scopes);

        return stringWriter.toString();
    }
	
    public static String now() {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        final Date date = new Date();

        return dateFormat.format(date);
    }

	private Map<String, Object> getCompiledMapFromMappers(final Map<String, String> data) {
        final MustacheFactory mf = new DefaultMustacheFactory();

        if (data != null) {
            for (final Map.Entry<String, String> entry : data.entrySet()) {
                final Mustache mappersMustache = mf.compile(new StringReader(entry.getValue()), "");
                final StringWriter stringWriter = new StringWriter();
				
				System.out.println("see whats in scopes " + this.scopes);

                mappersMustache.execute(stringWriter, this.scopes);
                String url = stringWriter.toString();
                if (!url.startsWith("http")) {
                    url = this.scopes.get("BASE_URI") + url;
                }
                try {
                    this.scopes.put(entry.getKey(), getMapFromUrl(url));
                } catch (final IOException e) {
                	logger.error("getCompiledMapFromMappers() failed", e);
                }
            }
        }
        return this.scopes;
    }

    @SuppressWarnings("unchecked")
    private  Map<String, Object>  getMapFromUrl(final String url) throws MalformedURLException, IOException {
        final HttpURLConnection connection = getConnection(url);

        final String response = getStringFromInputStream(connection.getInputStream());
        HashMap<String, Object> result = new HashMap<>();
        if (connection.getContentType().equals("text/plain")) {
				result.put("src", response);
        } else {
            result = new ObjectMapper().readValue(response, HashMap.class);
        }
        return result;
    }

    private HttpURLConnection getConnection(final String url) {
        if (this.authToken == null) {
            final String name =SecurityContextHolder.getContext().getAuthentication().getName();
            final String password = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
            Authenticator.setDefault(new Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(name, password.toCharArray());
                }
            });
        }

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            if (this.authToken != null) {
                connection.setRequestProperty("Authorization", "Basic " + this.authToken);
            }
            TrustModifier.relaxHostChecking(connection);

            connection.setDoInput(true);

        } catch (IOException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
        	logger.error("getConnection() failed, return null", e);
        }

        return connection;
    }

    // TODO Replace this with appropriate alternative available in Guava
    private static String getStringFromInputStream(final InputStream is) {
        BufferedReader br = null;
        final StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (final IOException e) {
        	logger.error("getStringFromInputStream() failed", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }
    
    /*
      Gets the object from a runReport query
     */
    private List<HashMap<String,Object>> getRunReportObject(final String url) throws MalformedURLException, IOException{

        final HttpURLConnection connection = getConnection(url);

        final String response = getStringFromInputStream(connection.getInputStream());
        List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        result = new ObjectMapper().readValue(response, new TypeReference<List<HashMap<String,Object>>>(){});
        return result;
    }






     public Map<String, List<HashMap<String,Object>>> compileMappers(final Map<String, String> templateMappers,Map<String,Object> smsParams) {

        final MustacheFactory mf = new DefaultMustacheFactory();

        final Map<String,List<HashMap<String,Object>>> runReportObject = new HashMap<String, List<HashMap<String,Object>>>();

        if(templateMappers !=null){
            for(Map.Entry<String,String> entry : templateMappers.entrySet()){
                /*
                    "mapperkey": "runreports",
                    "mappervalue": "runreports/{{runreportId}}?associations=all&tenantIdentifier={{tenantIdentifier}}",
                    entry.getValue represents mapperValue
                 */
                final Mustache urlMustache = mf.compile(new StringReader(entry.getValue()),"");

                final StringWriter stringWriter = new StringWriter();
                //execute to replace params in the mapperValue above ex {{loanId}} = 4
                urlMustache.execute(stringWriter,smsParams);
                String url = stringWriter.toString(); //holds the url to query for object from runReport
                if (!url.startsWith("http")) {
                    url = smsParams.get("BASE_URI") + url;
                }
                try{
                    runReportObject.put(entry.getKey(), getRunReportObject(url));
                }catch(final MalformedURLException e){
                    //TODO throw something here
                }catch (final IOException e){
                   // TODO throw something here
                }
            }

        }
        return runReportObject; //contains list of runReport object runReport,{Object}
    }
	
	@SuppressWarnings("unchecked")
	private void expandMapArrays(Object value) {
		if (value instanceof Map) {
			Map<String, Object> valueAsMap = (Map<String, Object>) value;
			//Map<String, Object> newValue = null;
			Map<String,Object> valueAsMap_second = new HashMap<>();
			for (Entry<String, Object> valueAsMapEntry : valueAsMap.entrySet()) {
				Object valueAsMapEntryValue = valueAsMapEntry.getValue();
				if (valueAsMapEntryValue instanceof Map) { // JSON Object
					expandMapArrays(valueAsMapEntryValue);
				} else if (valueAsMapEntryValue instanceof Iterable) { // JSON Array
					Iterable<Object> valueAsMapEntryValueIterable = (Iterable<Object>) valueAsMapEntryValue;
					String valueAsMapEntryKey = valueAsMapEntry.getKey();
					int i = 0;
					for (Object object : valueAsMapEntryValueIterable) {
						valueAsMap_second.put(valueAsMapEntryKey + "#" + i, object);
						++i;
						expandMapArrays(object);
						
					}
				}

			}
			valueAsMap.putAll(valueAsMap_second);

		}		
	}

}
