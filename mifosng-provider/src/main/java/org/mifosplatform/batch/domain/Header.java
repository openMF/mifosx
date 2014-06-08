package org.mifosplatform.batch.domain;

/**
 * Provides an object to handle HTTP headers as name and value pairs for Batch API.
 * It is used in {@link BatchRequest} and {@link BatchResponse} to store the 
 * information regarding the headers in incoming and outgoing JSON Strings.
 * 
 * @author Rishabh Shukla
 * @see BatchRequest
 * @see BatchResponse
 */
public class Header {
	
	private String name;
	private String value;

	/**
	 * Constructs a 'Header' with the name and value of HTTP headers.
	 * 
	 * @param name of the HTTP header.
	 * @param value of the HTTP header.
	 */
	public Header(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Constructs a default constructor of 'Header' 
	 */
	public Header() {
		
	}
	
	/**
	 * returns the 'name' data field of the object of this class.
	 * 
	 * @return name data field of this class 
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * sets 'name' data field of this class.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * returns the 'value' data field of the object of this class.
	 * 
	 * @return value data field of this class
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * sets 'value' data field of this class.
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
