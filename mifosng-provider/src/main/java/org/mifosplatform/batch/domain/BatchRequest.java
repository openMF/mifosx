package org.mifosplatform.batch.domain;

import java.util.Set;

/**
 * Provides an object for separate HTTP requests in the Batch Request for Batch API.
 * A requestId is also included as data field which takes care of dependency issues
 * among various requests. This class also provides getter and setter functions to
 * access Batch Request data fields.
 * 
 * @author Rishabh Shukla 
 * @see org.mifosplatform.batch.api.BatchApiResource
 * @see Header
 */
public class BatchRequest {
	
	private Long requestId;
	private String relativeUrl;
	private String method;
	private Set<Header> headers;
	private Long reference;
	private String body;
	
	/**
	 * Constructs a 'BatchRequest' with requestId, relativeUrl, method, headers, reference and
	 * body of the incoming request.
	 * 
	 * @param requestId of HTTP request.
	 * @param relativeUrl of HTTP request.
	 * @param method of HTTP request.
	 * @param headers of HTTP request.
	 * @param reference of HTTP request.
	 * @param body of HTTP request.
	 * 
	 * @see Header
	 */
	public BatchRequest(Long requestId, String relativeUrl, String method, Set<Header> headers,
			Long reference, String body) {
		
		this.requestId = requestId;
		this.relativeUrl = relativeUrl;
		this.method = method;
		this.headers = headers;
		this.reference = reference;
		this.body = body;		
	}
	
	/**
	 * Constructs a default constructor of 'BatchRequest' 
	 */
	public BatchRequest() {
		
	}
	
	/**
	 * returns the value of 'requestId' of an object of this class.
	 * 
	 * @return requestId of the HTTP request.
	 */
	public Long getRequestId() {
		return this.requestId;
	}
	
	/**
	 * sets the value of 'requestId' of an object of this class.
	 * 
	 * @param requestId
	 */
	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}
	
	/**
	 * returns the value of 'relativeUrl' of an object of this class.
	 * 
	 * @return relativeUrl of the HTTP request.
	 */
	public String getRelativeUrl() {
		return this.relativeUrl;
	}
	
	/**
	 * sets the value of 'relativeUrl' of an object of this class.
	 * 
	 * @param relativeUrl
	 */
	public void setRelativeUrl(String relativeUrl) {
		this.relativeUrl = relativeUrl;
	}
	
	/**
	 * returns the value of 'method' of an object of this class.
	 * 
	 * @return method of the HTTP request.
	 */
	public String getMethod() {
		return this.method;
	}
	
	/**
	 * sets the value of 'method' of the object of this class.
	 * 
	 * @param method
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	
	/**
	 * returns the values of 'headers' of {@link Header} type of an object of this class.
	 * 
	 * @return headers of the HTTP request.
	 * @see Header
	 */
	public Set<Header> getHeaders() {
		return this.headers;
	}
	
	/**
	 * sets the values of 'headers' of {@link Header} type of an object of this class.
	 * 
	 * @param headers
	 * @see Header
	 */
	public void setHeaders(Set<Header> headers) {
		this.headers = headers;
	}
	
	/**
	 * returns the value of 'reference' of an object of this class
	 * 
	 * @return reference of the HTTP request
	 */
	public Long getReference() {
		return this.reference;
	}
	
	/**
	 * sets the value of 'reference' of an object of this class.
	 * 
	 * @param reference
	 */
	public void setReference(Long reference) {
		this.reference = reference;
	}
	
	/**
	 * returns the value of 'body' of an object of this class.
	 * 
	 * @return body of the HTTP request.
	 */	
	public String getBody() {
		return this.body;
	}
	
	/**
	 * sets the value of 'body' of an object of this class.
	 * 
	 * @param body
	 */
	public void setBody(String body) {
		this.body = body;
	}
}
