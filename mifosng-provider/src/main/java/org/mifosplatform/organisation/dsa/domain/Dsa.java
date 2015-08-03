/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.dsa.domain;

import java.util.LinkedHashMap;
import java.util.Map;


import javax.persistence.*;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.office.domain.Office;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_dsa", uniqueConstraints = {@UniqueConstraint(columnNames = {"display_name"}, name = "display_name"),
		@UniqueConstraint(columnNames = {"mobile_no"}, name = "mobile_no_UNIQUE")
		//Add more unique constraint
})

public class Dsa extends AbstractPersistable<Long>{
	
	@Column(name = "firstname", length = 50)
    private String firstname;

    @Column(name = "lastname", length = 50)
    private String lastname;

    @Column(name = "display_name", length = 100)
    private String displayName;
    
    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;
    
    @Column(name = "mobile_no", length = 50, nullable = false, unique = true)
    private String mobileNo;
    
    @Column(name = "is_active", nullable = false)
    private boolean active;
    
    public static Dsa fromJson(final Office dsaOffice, final JsonCommand command){
    	final String firstnameParamName = "firstname";
        final String firstname = command.stringValueOfParameterNamed(firstnameParamName);

        final String lastnameParamName = "lastname";
        final String lastname = command.stringValueOfParameterNamed(lastnameParamName);
        
        final String mobileNoParamName = "mobileNo";
        final String mobileNo = command.stringValueOfParameterNamedAllowingNull(mobileNoParamName);
        
        final String isActiveParamName = "isActive";
        final Boolean isActive = command.booleanObjectValueOfParameterNamed(isActiveParamName);
        
        return new Dsa(dsaOffice, firstname,lastname,mobileNo,isActive);
    }
    
    protected Dsa(){
    	//
    }
    
    private Dsa(final Office dsaOffice,final String firstname,final String lastname,final String mobileNo, final Boolean isActive){
    	this.office = dsaOffice;
    	this.firstname = StringUtils.defaultIfEmpty(firstname, null);
    	this.lastname = StringUtils.defaultIfEmpty(lastname, null);
    	this.mobileNo = StringUtils.defaultIfEmpty(mobileNo, null);
    	this.active = (isActive == null) ? true : isActive;
    	deriveDisplayName(firstname);
    		
    }
    
    public void changeOffice(final Office newOffice){
    	this.office = newOffice;
    }
    
    public Map<String, Object> update(final JsonCommand command){
    	
    	final Map<String, Object> actualChanges = new LinkedHashMap<>(7);
    	
    	final String officeIdParamName = "officeId";
        if (command.isChangeInLongParameterNamed(officeIdParamName, this.office.getId())) {
            final Long newValue = command.longValueOfParameterNamed(officeIdParamName);
            actualChanges.put(officeIdParamName, newValue);
        }
        
        boolean firstnameChanged = false;
        final String firstnameParamName = "firstname";
        if (command.isChangeInStringParameterNamed(firstnameParamName, this.firstname)) {
            final String newValue = command.stringValueOfParameterNamed(firstnameParamName);
            actualChanges.put(firstnameParamName, newValue);
            this.firstname = newValue;
            firstnameChanged = true;
        }

        boolean lastnameChanged = false;
        final String lastnameParamName = "lastname";
        if (command.isChangeInStringParameterNamed(lastnameParamName, this.lastname)) {
            final String newValue = command.stringValueOfParameterNamed(lastnameParamName);
            actualChanges.put(lastnameParamName, newValue);
            this.lastname = newValue;
            lastnameChanged = true;
        }

        if (firstnameChanged || lastnameChanged) {
            deriveDisplayName(this.firstname);
        }
        
        final String mobileNoParamName = "mobileNo";
        if (command.isChangeInStringParameterNamed(mobileNoParamName, this.mobileNo)) {
            final String newValue = command.stringValueOfParameterNamed(mobileNoParamName);
            actualChanges.put(mobileNoParamName, newValue);
            this.mobileNo = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        final String isActiveParamName = "isActive";
        if (command.isChangeInBooleanParameterNamed(isActiveParamName, this.active)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(isActiveParamName);
            actualChanges.put(isActiveParamName, newValue);
            this.active = newValue;
        }
    	
        return actualChanges;
    }
    
    public boolean isNotActive() {
        return !isActive();
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    private void deriveDisplayName(final String firstname) {
        if (!StringUtils.isBlank(firstname)) {
            this.displayName = this.lastname + ", " + this.firstname;
        } else {
            this.displayName = this.lastname;
        }
    }
    
    public boolean identifiedBy(final Dsa dsa) {
        return getId().equals(dsa.getId());
    }
    public Long officeId() {
        return this.office.getId();
    }
    
    public String displayName() {
        return this.displayName;
    }

    public String mobileNo() {
        return this.mobileNo;
    }

    public Office office() {
        return this.office;
    }

}
