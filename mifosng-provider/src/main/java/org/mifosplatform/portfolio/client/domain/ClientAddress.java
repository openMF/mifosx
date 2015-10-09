/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

import com.mchange.v1.lang.BooleanUtils;


@Entity
@Table(name = "m_client_address", uniqueConstraints ={
		@UniqueConstraint(columnNames ={ "address_type_id", "address_line" }, name = "unique_address_type" ),
		/*@UniqueConstraint(columnNames ={ "client_id", "address_type_id" } , name = "unique_client_address")*/ })
public class ClientAddress extends AbstractAuditableCustom<AppUser, Long>{
	
	@ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
	
	@ManyToOne
	@JoinColumn(name = "address_type_id", nullable = false)
	private CodeValue addressType;
	
	@Column(name = "address_line")
	private String address_line;
	
	@Column(name = "address_line_two")
	private String address_line_two;
	
	@Column(name = "landmark")
	private String landmark;
	
	@Column(name ="city")
	private String city;
	
	@Column(name ="pincode")
	private String pincode;
	
	@Column(name ="isBoth_perma_same")
	private Boolean isBoth;
	
	@ManyToOne
	@JoinColumn(name = "state_type_id", nullable = false)
	private CodeValue stateType;
	
	
	public static ClientAddress fromJson(final Client client, final CodeValue addressType, final CodeValue stateType, final JsonCommand command ){
		
		final String address_line = command.stringValueOfParameterNamed("address_line");
		final String address_line_two = command.stringValueOfParameterNamed("address_line_two");
		
		final String landmark = command.stringValueOfParameterNamed("landmark");
		final String city = command.stringValueOfParameterNamed("city");
		
		final String pincode = command.stringValueOfParameterNamed("pincode");
		final Boolean isBoth = command.booleanObjectValueOfParameterNamed("isBoth");
		
		return new ClientAddress(client, addressType,address_line, address_line_two, landmark,city,pincode,isBoth,stateType);
		
	}
	
	protected ClientAddress() {
		//
	}
	
	private ClientAddress(final Client client, final CodeValue addressType, final String address_line, final String address_line_two, final String landmark, final String city,final String pincode,final Boolean isBoth, final CodeValue stateType){
		
		this.client = client;
		this.addressType = addressType;
		this.address_line = StringUtils.defaultIfEmpty(address_line, null);
		this.address_line_two = StringUtils.defaultIfEmpty(address_line_two, null);
		this.landmark = StringUtils.defaultIfEmpty(landmark, null);
		this.city = StringUtils.defaultIfEmpty(city, null);
		this.pincode = StringUtils.defaultIfEmpty(pincode, null);
		this.isBoth = Boolean.valueOf(isBoth);
		this.stateType = stateType;
	}
	
	public void update(final CodeValue addressType, final CodeValue stateType){
		this.addressType = addressType;
		this.stateType = stateType;
	} 
	
	public Map<String, Object> update(final JsonCommand command){
		
		final Map<String, Object> actualChanges = new LinkedHashMap<>(7);
		
		final String addressTypeIdParamName = "addressTypeId";
        if (command.isChangeInLongParameterNamed(addressTypeIdParamName, this.addressType.getId())) {
            final Long newValue = command.longValueOfParameterNamed(addressTypeIdParamName);
            actualChanges.put(addressTypeIdParamName, newValue);
        }
        
        final String address_lineParamName = "address_line";
        if (command.isChangeInStringParameterNamed(address_lineParamName,this.address_line)){
        	final String newValue = command.stringValueOfParameterNamed(address_lineParamName);
        	actualChanges.put(address_lineParamName, newValue);
        	this.address_line = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String address_line_twoParamName = "address_line_two";
        if (command.isChangeInStringParameterNamed(address_line_twoParamName, this.address_line_two)) {
            final String newValue = command.stringValueOfParameterNamed(address_line_twoParamName);
            actualChanges.put(address_line_twoParamName, newValue);
            this.address_line_two = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        final String landmarkParamName = "landmark";
        if (command.isChangeInStringParameterNamed(landmarkParamName, this.landmark)) {
            final String newValue = command.stringValueOfParameterNamed(landmarkParamName);
            actualChanges.put(landmarkParamName, newValue);
            this.landmark = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        final String cityParamName = "city";
        if (command.isChangeInStringParameterNamed(cityParamName, this.city)) {
            final String newValue = command.stringValueOfParameterNamed(cityParamName);
            actualChanges.put(cityParamName, newValue);
            this.city = StringUtils.defaultIfEmpty(newValue, null);
        }
        final String pincodeParamName = "pincode";
        if(command.isChangeInStringParameterNamed(pincodeParamName, this.pincode)){
        	final String newValue = command.stringValueOfParameterNamed(pincodeParamName);
        	actualChanges.put(pincodeParamName, newValue);
        	this.pincode = StringUtils.defaultIfEmpty(pincodeParamName, null);
        }
        final String isBothParamName = "isBoth";
        if(command.isChangeInBooleanParameterNamed(isBothParamName, this.isBoth)){
        	final Boolean newValue = command.booleanObjectValueOfParameterNamed(isBothParamName);
        	actualChanges.put(isBothParamName, newValue);
        	this.isBoth = Boolean.valueOf(isBothParamName); 
        }
        
        
        final String stateTypeIdParamName = "stateTypeId";
        if (command.isChangeInLongParameterNamed(stateTypeIdParamName, this.stateType.getId())) {
            final Long newValue = command.longValueOfParameterNamed(stateTypeIdParamName);
            actualChanges.put(stateTypeIdParamName, newValue);
        }
        
        return actualChanges;
        
        
	}
	
	public String addressLine(){
		return this.address_line;
	}
	public Long addressTypeId(){
		return this.addressType.getId();	
	}
	public Long stateTypeId(){
		return this.stateType.getId();
		
	}
	public Boolean IsBoth(){
		return this.isBoth;
	}
	
}

