package org.openmf.mifos.dataimport.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openmf.mifos.dataimport.http.SimpleHttpRequest.Method;

public class HttpRequestBuilder
{
	private String url;
	
	private Method method;
	
	private String content;
	
	private Map<String, String> headers = new HashMap<String, String>();

	public HttpRequestBuilder withURL(String url) {
		this.url = url;
		return this;
	}
	
	public HttpRequestBuilder withMethod(Method method) {
		this.method = method;
		return this;
	}
	
	public HttpRequestBuilder withContent(String content) {
		this.content = content;
		return this;
	}
	
	public HttpRequestBuilder addHeader(String name, String value) {
		headers.put(name, value);
		return this;
	}
	
	public SimpleHttpResponse execute() throws IOException {
		SimpleHttpRequest request = new SimpleHttpRequest(url, method, headers, content);
		return new SimpleHttpResponse(request.getConnection());
	}

}
