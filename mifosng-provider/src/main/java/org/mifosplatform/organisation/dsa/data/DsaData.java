/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.dsa.data;

import java.util.Collection;

import org.mifosplatform.organisation.office.data.OfficeData;


public class DsaData {
	
	private final Long id;
	private final String firstname;
	private final String lastname;
	private final String displayName;
	private final Long officeId;
	private final String officeName;
	private final String mobileNo;
	private final Boolean isActive;
	
	
	@SuppressWarnings("unused")
	private final Collection<OfficeData> allowedOffices;
	
	public static DsaData templateData(final DsaData dsaData, final Collection<OfficeData> allowedOffices){
		return new DsaData(dsaData.id,dsaData.firstname,dsaData.lastname,dsaData.displayName,dsaData.officeId,dsaData.officeName,
				dsaData.mobileNo,dsaData.isActive, allowedOffices);
	}
	
	public static DsaData lookup(final Long id,final String displayName){
		return new DsaData(id,null,null,displayName,null,null,null,null,null);
	}
	public static DsaData instance(final Long id, final String firstname, final String lastname, final String displayName,
            final Long officeId, final String officeName, final String mobileNo,final boolean isActive) {
        return new DsaData(id, firstname, lastname, displayName, officeId, officeName,mobileNo,
                isActive,null);
    }
	
	private DsaData(final Long id, final String firstname,final String lastname,final String displayName,final Long officeId, final String officeName,
			final String mobileNo, final Boolean isActive,final Collection<OfficeData> allowedOffices){
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.displayName = displayName;
		this.officeId = officeId;
		this.officeName = officeName;
		this.mobileNo = mobileNo;
		this.isActive = isActive;
		this.allowedOffices = allowedOffices;
	}
	
	public Long getId(){
		return this.id;
	}
	public String getDisplayName(){
		return this.displayName;
	}
	public String getFirstName(){
		return this.firstname;
	}
	public String getLastName(){
		return this.lastname;
	}
	public String getOfficeName(){
		return this.officeName;
	}
	public Long getOfficeId(){
		return this.officeId;
	}

}
