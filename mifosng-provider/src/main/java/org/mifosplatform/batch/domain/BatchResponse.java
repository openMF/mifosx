package org.mifosplatform.batch.domain;

import java.util.Set;

public class BatchResponse {

	private long requestId;
	private int statusCode;
	private Set<Header> headers;
	private String body;
	
	public BatchResponse(long requestId, int statusCode, Set<Header> headers, String body) {
		this.requestId = requestId;
		this.statusCode = statusCode;
		this.headers = headers;
		this.body = body;		
	}
	
	public long getRequest() {
		return this.requestId;
	}
	
	public void setRequest(long requestId) {
		this.requestId = requestId;
	}
	
	public int getstatusCode() {
		return this.statusCode;
	}
	
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
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
