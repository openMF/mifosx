/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * 
 * @author saranshsharma
 * Checking the duplication of Address Type and returns exception
 */

public class DuplicateClientAddressException extends AbstractPlatformDomainRuleException {
	
	private Long addressTypeId;
	private String address_line;
	//private Long stateTypeId;
	//private String city;
	//private String pincode;
	//private Boolean isBoth;
	private final String addressType;
	//private final String stateType;
	
	public DuplicateClientAddressException(final String addressType){
		super("error.msg.clientaddress.type.duplicate", "Client Address of Type " + addressType
				+ "is already present", addressType);
		this.addressType = addressType;

	}
	
	
	public DuplicateClientAddressException(final Long addressTypeId, final String addressType, final String address_line){
		
		super("error.msg.clientaddress.address_line.duplicate", "Client Address of Type " + addressType + "with value of " 
				  + "already exists", addressType);
		this.addressTypeId = addressTypeId;
		this.addressType = addressType;
		//this.stateTypeId = stateTypeId;
		this.address_line = address_line;
		//this.stateType = stateType;
		
	}
	
	public DuplicateClientAddressException(final String clientName, final String officeName, final String addressType, 
				final String address_line
                      ) {
        super("error.msg.clientaddress.address_line.duplicate", "Client " + clientName + "under " + officeName + " Branch already has a "
                + addressType + " with unique address "  + "with unique state" , clientName, officeName, addressType);
        this.addressType = addressType;
        this.address_line = address_line;
       // this.stateType = stateType;
    }
	
	
	public Long getAddressTypeId(){
		return this.addressTypeId;
	}
	
 //		return this.stateTypeId;
//	}
//	public String getCity(){
//		return this.city;
//	}
//	public String getPinCode(){
//		return this.pincode;
//	}
	
	public String getAddress_Line(){
		return this.address_line;
	}
	
	public String getAddressType(){
		return this.addressType;
	}
	
//	public String getStateType(){
	//	return this.stateType;
//	}
	
//	public Boolean getIsBoth(){
//		return this.isBoth;
//	}
//	
	
	

}
