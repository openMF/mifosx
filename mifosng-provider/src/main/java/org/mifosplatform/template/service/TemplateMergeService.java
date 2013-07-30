package org.mifosplatform.template.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.template.domain.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.reflect.TypeToken;


@Service
public class TemplateMergeService {
	
	
	private final FromJsonHelper fromApiJsonHelper;
	private Map<String, Object> scopes;
	
	@Autowired
	public TemplateMergeService (FromJsonHelper fromApiJsonHelper) {
		
		this.fromApiJsonHelper = fromApiJsonHelper;
	}
	
	public String compile(Template template, Map<String, Object> scopes) throws MalformedURLException, IOException {
		
		this.scopes = scopes;
		
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile(new StringReader(
	    		template.getText()),
				template.getName());
	    
	    Map<String, Object> mappers = getCompiledMapFromMappers(template.getMappers());
		this.scopes.putAll(mappers);
		
		StringWriter stringWriter = new StringWriter();
		mustache.execute(stringWriter, this.scopes);

		return stringWriter.toString();
	}
	
	private Map<String, Object> getCompiledMapFromMappers(Map<String, String> data) {
		
		MustacheFactory mf = new DefaultMustacheFactory();
		
		if(data != null) {
			for (Map.Entry<String, String> entry: data.entrySet()) {
				
				Mustache mappersMustache = mf.compile(new StringReader(entry.getValue()),"");
				StringWriter stringWriter = new StringWriter();
				
				mappersMustache.execute(stringWriter, this.scopes);
				String url = stringWriter.toString();
				if(!url.startsWith("http")) {
					url = this.scopes.get("BASE_URI")+url;
				}
				
				try {
					this.scopes.put(entry.getKey(), getMapFromUrl(url));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return scopes;
	}
	
	private Map<String, Object> getMapFromUrl(String url) throws  MalformedURLException, IOException{
	
		Map<String, Object> values = new HashMap<String, Object>();
		String jsonText = getStringFromInputStream(getInputstream(url));
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		values = fromApiJsonHelper.extractObjectMap(typeOfMap, jsonText);
		
		return values;
	}
	
	private InputStream getInputstream (String url) {
		
		final String name = SecurityContextHolder.getContext().getAuthentication().getName();
		final String password = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
		
		Authenticator.setDefault (new Authenticator() {
		    @Override
			protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication (name, password.toCharArray());
		    }
		});
		
		URL someUrl;
		InputStream inputstream = null ;
		try {
			someUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) someUrl.openConnection();
			TrustModifier.relaxHostChecking(connection);

			connection.setDoInput(true);
			inputstream = connection.getInputStream();
		} catch (MalformedURLException e) { e.printStackTrace(); } 
		  catch (IOException e) { e.printStackTrace(); } 
		  catch (KeyManagementException e) { e.printStackTrace(); } 
		  catch (NoSuchAlgorithmException e) { e.printStackTrace(); } 
		  catch (KeyStoreException e) {	e.printStackTrace();
		}
		
		return inputstream;
	}


	private static String getStringFromInputStream(InputStream is) {
		 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
	
		String line;
		try {
	
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
	
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
		return sb.toString();
	}
}
