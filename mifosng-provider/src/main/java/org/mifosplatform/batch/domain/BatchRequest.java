package org.mifosplatform.batch.domain;

import java.util.Set;

public class BatchRequest {
	
	private long requestId;
	private String relativeUrl;
	private String method;
	private Set<Header> headers;
	private String body;
	
	public BatchRequest(long requestId, String relativeUrl, String method, Set<Header> headers, String body) {
		this.requestId = requestId;
		this.relativeUrl = relativeUrl;
		this.method = method;
		this.headers = headers;
		this.body = body;		
	}
	
	public long getRequest() {
		return this.requestId;
	}
	
	public void setRequest(long requestId) {
		this.requestId = requestId;
	}
	
	public String getRelativeUrl() {
		return this.relativeUrl;
	}
	
	public void setRelativeUrl(String relativeUrl) {
		this.relativeUrl = relativeUrl;
	}
	
	public String getMethod() {
		return this.method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public Set<Header> getHeaders() {
		return this.headers;
	}
	
	public void setHeaders(Set<Header> headers) {
		this.headers = headers;
	}
	
	public String getBody() {
		return this.body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
}
