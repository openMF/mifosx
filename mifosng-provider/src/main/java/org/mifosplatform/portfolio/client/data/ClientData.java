/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.data;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.staff.data.StaffData;
import org.mifosplatform.portfolio.group.data.GroupGeneralData;
import org.mifosplatform.portfolio.savings.data.SavingsAccountData;
import org.mifosplatform.portfolio.savings.data.SavingsProductData;

/**
 * Immutable data object representing client data.
 */
final public class ClientData implements Comparable<ClientData> {

    private final Long id;
    private final String accountNo;
    private final String externalId;

    private final EnumOptionData status;
    private final CodeValueData subStatus;

    @SuppressWarnings("unused")
    private final Boolean active;
    private final LocalDate activationDate;

    private final String firstname;
    private final String middlename;
    private final String lastname;
    private final String fathername;
    private final String emailAddress;
    private final String code;
    private final String fullname;
    private final String displayName;
    private final String mobileNo;
    private final LocalDate dateOfBirth;
    private final CodeValueData gender;
    private final CodeValueData marital;
    private final CodeValueData religion;
    private final CodeValueData dependent;
    private final CodeValueData education;
    private final CodeValueData clientType;
    private final CodeValueData clientClassification;
    

    private final Long officeId;
    private final String officeName;
    private final Long transferToOfficeId;
    private final String transferToOfficeName;

    private final Long imageId;
    private final Boolean imagePresent;
    private final Long staffId;
    private final String staffName;
    private final ClientTimelineData timeline;

    private final Long savingsProductId;
    private final String savingsProductName;

    private final Long savingsAccountId;

    // associations
    private final Collection<GroupGeneralData> groups;
    private Collection<ClientAddressData> addresses;

    // template
    private final Collection<OfficeData> officeOptions;
    private final Collection<StaffData> staffOptions;
    private final Collection<CodeValueData> narrations;
    private final Collection<SavingsProductData> savingProductOptions;
    private final Collection<SavingsAccountData> savingAccountOptions;
    private final Collection<CodeValueData> genderOptions;
    private final Collection<CodeValueData> maritalOptions;
    private final Collection<CodeValueData> religionOptions;
    private final Collection<CodeValueData> dependentOptions;
    private final Collection<CodeValueData> educationOptions;
    private final Collection<CodeValueData> clientTypeOptions;
    private final Collection<CodeValueData> clientClassificationOptions;
	
    
    
   
  
    public static ClientData associations(final ClientData clientData, final Collection<ClientAddressData> addresses ){
    	return new ClientData(clientData.accountNo, clientData.status, clientData.subStatus, clientData.officeId, clientData.officeName, clientData.transferToOfficeId, clientData.transferToOfficeName, clientData.id, clientData.firstname,
    			clientData.middlename, clientData.lastname, clientData.fullname, clientData.displayName,clientData.fathername,clientData.emailAddress,clientData.code,
    			clientData.externalId, clientData.mobileNo, clientData.dateOfBirth, clientData.gender,clientData.marital,clientData.religion,clientData.dependent,clientData.education,
    			clientData.activationDate, clientData.imageId, clientData.staffId,
    			clientData.staffName, clientData.officeOptions, clientData.groups, clientData.staffOptions, clientData.narrations, clientData.genderOptions, clientData.maritalOptions,clientData.religionOptions,clientData.dependentOptions,clientData.educationOptions, clientData.timeline, clientData.savingProductOptions,
    			clientData.savingsProductId, clientData.savingsProductName, clientData.savingsAccountId, clientData.savingAccountOptions, clientData.clientType, clientData.clientClassification,
                clientData.clientTypeOptions, clientData.clientClassificationOptions, addresses);
    }
    public static ClientData associations(final ClientData clientData){
    	return associations(clientData, clientData.addresses );
    	
    }
	

    public static ClientData template(final Long officeId, final LocalDate joinedDate, final Collection<OfficeData> officeOptions,
            final Collection<StaffData> staffOptions, final Collection<CodeValueData> narrations,
            final Collection<CodeValueData> genderOptions, final Collection<CodeValueData> maritalOptions,final Collection<CodeValueData> religionOptions,final Collection<CodeValueData> dependentOptions, final Collection<CodeValueData> educationOptions,final Collection<SavingsProductData> savingProductOptions,
            final Collection<CodeValueData> clientTypeOptions, final Collection<CodeValueData> clientClassificationOptions) {
        final String accountNo = null;
        final EnumOptionData status = null;
        final CodeValueData subStatus = null;
        final String officeName = null;
        final Long transferToOfficeId = null;
        final String transferToOfficeName = null;
        final Long id = null;
        final String firstname = null;
        final String middlename = null;
        final String lastname = null;
        final String fullname = null;
        final String fathername = null;
        final String emailAddress = null;
        final String code = null;
        final String displayName = null;
        final String externalId = null;
        final String mobileNo = null;
        final LocalDate dateOfBirth = null;
        final CodeValueData gender = null;
        final CodeValueData marital = null;
        final CodeValueData religion = null;
        final CodeValueData dependent = null;
        final CodeValueData education = null;
        final Long imageId = null;
        final Long staffId = null;
        final String staffName = null;
        final Collection<GroupGeneralData> groups = null;
        final Collection<ClientAddressData> addresses = null;
        final ClientTimelineData timeline = null;
        final Long savingsProductId = null;
        final String savingsProductName = null;
        final Long savingsAccountId = null;
        final Collection<SavingsAccountData> savingAccountOptions = null;
        final CodeValueData clientType = null;
        final CodeValueData clientClassification = null;
        return new ClientData(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id, firstname,
                middlename, lastname, fullname, displayName,fathername,emailAddress,code, externalId, mobileNo, dateOfBirth, gender,marital,religion,dependent,education, joinedDate, imageId, staffId,
                staffName, officeOptions, groups, staffOptions, narrations, genderOptions, maritalOptions,religionOptions,dependentOptions,educationOptions, timeline, savingProductOptions,
                savingsProductId, savingsProductName, savingsAccountId, savingAccountOptions, clientType, clientClassification,
                clientTypeOptions, clientClassificationOptions,addresses);

    }

    public static ClientData templateOnTop(final ClientData clientData, final ClientData templateData) {

        return new ClientData(clientData.accountNo,clientData.status, clientData.subStatus, clientData.officeId, clientData.officeName,
                clientData.transferToOfficeId, clientData.transferToOfficeName, clientData.id, clientData.firstname, clientData.middlename,
                clientData.lastname, clientData.fullname, clientData.displayName,clientData.fathername,clientData.emailAddress,clientData.code,clientData.externalId, clientData.mobileNo,
                clientData.dateOfBirth, clientData.gender, clientData.marital, clientData.religion,clientData.dependent,clientData.education,clientData.activationDate, clientData.imageId, clientData.staffId,
                clientData.staffName, templateData.officeOptions, clientData.groups, templateData.staffOptions, templateData.narrations,
                templateData.genderOptions,templateData.maritalOptions,templateData.religionOptions,templateData.dependentOptions,templateData.educationOptions,clientData.timeline, templateData.savingProductOptions, clientData.savingsProductId,
                clientData.savingsProductName, clientData.savingsAccountId, clientData.savingAccountOptions, clientData.clientType,
                clientData.clientClassification, templateData.clientTypeOptions, templateData.clientClassificationOptions,clientData.addresses);

    }

    public static ClientData templateWithSavingAccountOptions(final ClientData clientData,
            final Collection<SavingsAccountData> savingAccountOptions) {

        return new ClientData(clientData.accountNo, clientData.status, clientData.subStatus, clientData.officeId, clientData.officeName,
                clientData.transferToOfficeId, clientData.transferToOfficeName, clientData.id, clientData.firstname, clientData.middlename,
                clientData.lastname, clientData.fullname, clientData.displayName,clientData.fathername, clientData.emailAddress,clientData.code,clientData.externalId, clientData.mobileNo,
                clientData.dateOfBirth, clientData.gender,clientData.marital,clientData.religion,clientData.dependent,clientData.education, clientData.activationDate, clientData.imageId, clientData.staffId,
                clientData.staffName, clientData.officeOptions, clientData.groups, clientData.staffOptions, clientData.narrations,
                clientData.genderOptions,clientData.maritalOptions,clientData.religionOptions,clientData.dependentOptions,clientData.educationOptions, clientData.timeline, clientData.savingProductOptions, clientData.savingsProductId,
                clientData.savingsProductName, clientData.savingsAccountId, savingAccountOptions, clientData.clientType,
                clientData.clientClassification, clientData.clientTypeOptions, clientData.clientClassificationOptions,clientData.addresses);

    }

    public static ClientData setParentGroups(final ClientData clientData, final Collection<GroupGeneralData> parentGroups) {
        return new ClientData(clientData.accountNo, clientData.status, clientData.subStatus, clientData.officeId, clientData.officeName,
                clientData.transferToOfficeId, clientData.transferToOfficeName, clientData.id, clientData.firstname, clientData.middlename,
                clientData.lastname, clientData.fullname, clientData.displayName, clientData.fathername,clientData.emailAddress,clientData.code,clientData.externalId, clientData.mobileNo,
                clientData.dateOfBirth, clientData.gender,clientData.marital,clientData.religion,clientData.dependent,clientData.education,clientData.activationDate, clientData.imageId, clientData.staffId,
                clientData.staffName, clientData.officeOptions, parentGroups, clientData.staffOptions, clientData.maritalOptions,clientData.religionOptions, clientData.dependentOptions,clientData.educationOptions,null, null, clientData.timeline,
                clientData.savingProductOptions, clientData.savingsProductId, clientData.savingsProductName, clientData.savingsAccountId,
                clientData.savingAccountOptions, clientData.clientType, clientData.clientClassification, clientData.clientTypeOptions,
                clientData.clientClassificationOptions,clientData.addresses);

    }

    public static ClientData clientIdentifier(final Long id, final String accountNo,final String firstname, final String middlename,
            final String lastname, final String fullname, final String displayName, final String fathername,final String emailAddress,final String code, final Long officeId, final String officeName) {

        final Long transferToOfficeId = null;
        final String transferToOfficeName = null;
        final String externalId = null;
        final String mobileNo = null;
        final LocalDate dateOfBirth = null;
        final CodeValueData gender = null;
        final CodeValueData marital = null;
        final CodeValueData religion = null;
        final CodeValueData dependent = null;
        final CodeValueData education = null;
        //final String emailAddress = null;
        final LocalDate activationDate = null;
        final Long imageId = null;
        final Long staffId = null;
        final String staffName = null;
        final Collection<OfficeData> allowedOffices = null;
        final Collection<GroupGeneralData> groups = null;
        final Collection<ClientAddressData> addresses = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<CodeValueData> closureReasons = null;
        final Collection<CodeValueData> genderOptions = null;
        final Collection<CodeValueData>maritalOptions = null;
        final Collection<CodeValueData>religionOptions = null;
        final Collection<CodeValueData>dependentOptions = null;
        final Collection<CodeValueData>educationOptions = null;
        final ClientTimelineData timeline = null;
        final Collection<SavingsProductData> savingProductOptions = null;
        final Long savingsProductId = null;
        final String savingsProductName = null;
        final Long savingsAccountId = null;
        final Collection<SavingsAccountData> savingAccountOptions = null;
        final CodeValueData clientType = null;
        final CodeValueData clientClassification = null;
        final Collection<CodeValueData> clientTypeOptions = null;
        final Collection<CodeValueData> clientClassificationOptions = null;
        final EnumOptionData status = null;
        final CodeValueData subStatus = null;
        return new ClientData(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id, firstname,
                middlename, lastname, fullname, displayName,fathername,emailAddress,code, externalId, mobileNo, dateOfBirth, gender,marital,religion,dependent,education, activationDate, imageId, staffId,
                staffName, allowedOffices, groups, staffOptions, closureReasons, genderOptions,maritalOptions,religionOptions,dependentOptions,educationOptions, timeline, savingProductOptions,
                savingsProductId, savingsProductName, savingsAccountId, savingAccountOptions, clientType, clientClassification,
                clientTypeOptions, clientClassificationOptions,addresses);
    }
    
    public static ClientData clientAddress(final Long id, final String accountNo,final String firstname, final String middlename,
            final String lastname, final String fullname, final String displayName, final String fathername,final String emailAddress,final String code, final Long officeId, final String officeName) {

        final Long transferToOfficeId = null;
        final String transferToOfficeName = null;
        final String externalId = null;
        final String mobileNo = null;
        final LocalDate dateOfBirth = null;
        final CodeValueData gender = null;
        final CodeValueData marital = null;
        final CodeValueData religion = null;
        final CodeValueData dependent = null;
        final CodeValueData education = null;
        //final String emailAddress = null;
        final LocalDate activationDate = null;
        final Long imageId = null;
        final Long staffId = null;
        final String staffName = null;
        final Collection<OfficeData> allowedOffices = null;
        final Collection<GroupGeneralData> groups = null;
        final Collection<ClientAddressData> addresses = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<CodeValueData> closureReasons = null;
        final Collection<CodeValueData> genderOptions = null;
        final Collection<CodeValueData>maritalOptions = null;
        final Collection<CodeValueData>religionOptions = null;
        final Collection<CodeValueData>dependentOptions = null;
        final Collection<CodeValueData>educationOptions = null;
        final ClientTimelineData timeline = null;
        final Collection<SavingsProductData> savingProductOptions = null;
        final Long savingsProductId = null;
        final String savingsProductName = null;
        final Long savingsAccountId = null;
        final Collection<SavingsAccountData> savingAccountOptions = null;
        final CodeValueData clientType = null;
        final CodeValueData clientClassification = null;
        final Collection<CodeValueData> clientTypeOptions = null;
        final Collection<CodeValueData> clientClassificationOptions = null;
        final EnumOptionData status = null;
        final CodeValueData subStatus = null;
        return new ClientData(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id, firstname,
                middlename, lastname, fullname, displayName,fathername,emailAddress,code, externalId, mobileNo, dateOfBirth, gender,marital,religion,dependent,education, activationDate, imageId, staffId,
                staffName, allowedOffices, groups,staffOptions, closureReasons, genderOptions,maritalOptions,religionOptions,dependentOptions,educationOptions, timeline, savingProductOptions,
                savingsProductId, savingsProductName, savingsAccountId, savingAccountOptions, clientType, clientClassification,
                clientTypeOptions, clientClassificationOptions,addresses);
    }

    public static ClientData lookup(final Long id, final String displayName, final Long officeId, final String officeName) {
        final String accountNo = null;
        final EnumOptionData status = null;
        final CodeValueData subStatus = null;
        final Long transferToOfficeId = null;
        final String transferToOfficeName = null;
        final String firstname = null;
        final String middlename = null;
        final String lastname = null;
        final String fullname = null;
        final String fathername = null;
        final String emailAddress = null;
        final String code = null;
        final String externalId = null;
        final String mobileNo = null;
        final LocalDate dateOfBirth = null;
        final CodeValueData gender = null;
        final CodeValueData marital = null;
        final CodeValueData religion = null;
        final CodeValueData dependent = null;
        final CodeValueData education = null;
        //final String emailAddress = null;
        final LocalDate activationDate = null;
        final Long imageId = null;
        final Long staffId = null;
        final String staffName = null;
        final Collection<OfficeData> allowedOffices = null;
        final Collection<GroupGeneralData> groups = null;
        final Collection<ClientAddressData> addresses = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<CodeValueData> closureReasons = null;
        final Collection<CodeValueData> genderOptions = null;
        final Collection<CodeValueData> maritalOptions = null;
        final Collection<CodeValueData> religionOptions = null;
        final Collection<CodeValueData> dependentOptions = null;
        final Collection<CodeValueData> educationOptions = null;
        final ClientTimelineData timeline = null;
        final Collection<SavingsProductData> savingProductOptions = null;
        final Long savingsProductId = null;
        final String savingsProductName = null;
        final Long savingsAccountId = null;
        final Collection<SavingsAccountData> savingAccountOptions = null;
        final CodeValueData clientType = null;
        final CodeValueData clientClassification = null;
        final Collection<CodeValueData> clientTypeOptions = null;
        final Collection<CodeValueData> clientClassificationOptions = null;
        return new ClientData(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id, firstname,
                middlename, lastname, fullname, displayName,fathername,emailAddress,code, externalId, mobileNo, dateOfBirth, gender,marital,religion,dependent,education,activationDate, imageId, staffId,
                staffName, allowedOffices, groups, staffOptions, closureReasons, genderOptions,maritalOptions,religionOptions,dependentOptions,educationOptions, timeline, savingProductOptions,
                savingsProductId, savingsProductName, savingsAccountId, savingAccountOptions, clientType, clientClassification,
                clientTypeOptions, clientClassificationOptions,addresses);

    }

    public static ClientData instance(final String accountNo, final EnumOptionData status, final CodeValueData subStatus,
            final Long officeId, final String officeName, final Long transferToOfficeId, final String transferToOfficeName, final Long id,
            final String firstname, final String middlename, final String lastname, final String fullname, final String displayName,final String fathername,
            final String emailAddress,final String code,
            final String externalId, final String mobileNo, final LocalDate dateOfBirth, final CodeValueData gender,final CodeValueData marital, final CodeValueData religion,final CodeValueData dependent,
            final CodeValueData education,
            final LocalDate activationDate, final Long imageId, final Long staffId, final String staffName,
            final ClientTimelineData timeline, final Long savingsProductId, final String savingsProductName, final Long savingsAccountId,
            final CodeValueData clientType, final CodeValueData clientClassification) {

        final Collection<OfficeData> allowedOffices = null;
        final Collection<GroupGeneralData> groups = null;
        final Collection<ClientAddressData> addresses = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<CodeValueData> closureReasons = null;
        final Collection<CodeValueData> genderOptions = null;
        final Collection<CodeValueData> maritalOptions = null;
        final Collection<CodeValueData> religionOptions = null;
        final Collection<CodeValueData> dependentOptions = null;
        final Collection<CodeValueData> educationOptions = null;
        final Collection<SavingsProductData> savingProductOptions = null;
        final Collection<CodeValueData> clientTypeOptions = null;
        final Collection<CodeValueData> clientClassificationOptions = null;
        return new ClientData(accountNo, status, subStatus, officeId, officeName, transferToOfficeId, transferToOfficeName, id, firstname,
                middlename, lastname, fullname, displayName,fathername,emailAddress,code, externalId, mobileNo, dateOfBirth, gender,marital,religion,dependent,education, activationDate, imageId, staffId,
                staffName, allowedOffices, groups,staffOptions, closureReasons, genderOptions,maritalOptions,religionOptions,dependentOptions,educationOptions, timeline, savingProductOptions,
                savingsProductId, savingsProductName, savingsAccountId, null, clientType, clientClassification, clientTypeOptions,
                clientClassificationOptions,addresses);

    }

    private ClientData(final String accountNo, final EnumOptionData status, final CodeValueData subStatus, final Long officeId,
            final String officeName, final Long transferToOfficeId, final String transferToOfficeName, final Long id,
            final String firstname, final String middlename, final String lastname, final String fullname, final String displayName,final String fathername,
            final String emailAddress,final String code,
            final String externalId, final String mobileNo, final LocalDate dateOfBirth, final CodeValueData gender,final CodeValueData marital,final CodeValueData religion,final CodeValueData dependent,
            final CodeValueData education,
            final LocalDate activationDate, final Long imageId, final Long staffId, final String staffName,
            final Collection<OfficeData> allowedOffices, final Collection<GroupGeneralData> groups,
            final Collection<StaffData> staffOptions, final Collection<CodeValueData> narrations,
            final Collection<CodeValueData> genderOptions,final Collection<CodeValueData> maritalOptions,final Collection<CodeValueData> religionOptions,final Collection<CodeValueData> dependentOptions,final Collection<CodeValueData> educationOptions, final ClientTimelineData timeline,
            final Collection<SavingsProductData> savingProductOptions, final Long savingsProductId, final String savingsProductName,
            final Long savingsAccountId, final Collection<SavingsAccountData> savingAccountOptions, final CodeValueData clientType,
            final CodeValueData clientClassification, final Collection<CodeValueData> clientTypeOptions,
            final Collection<CodeValueData> clientClassificationOptions, final Collection<ClientAddressData> addresses) {
        this.accountNo = accountNo;
        this.status = status;
        if (status != null) {
            this.active = status.getId().equals(300L);
        } else {
            this.active = null;
        }
        this.subStatus = subStatus;
        this.officeId = officeId;
        this.officeName = officeName;
        this.transferToOfficeId = transferToOfficeId;
        this.transferToOfficeName = transferToOfficeName;
        this.id = id;
        this.firstname = StringUtils.defaultIfEmpty(firstname, null);
        this.middlename = StringUtils.defaultIfEmpty(middlename, null);
        this.lastname = StringUtils.defaultIfEmpty(lastname, null);
        this.fullname = StringUtils.defaultIfEmpty(fullname, null);
        this.displayName = StringUtils.defaultIfEmpty(displayName, null);
        this.fathername = StringUtils.defaultIfEmpty(fathername, null);
        this.emailAddress = emailAddress;
        this.code = code;
        this.externalId = StringUtils.defaultIfEmpty(externalId, null);
        this.mobileNo = StringUtils.defaultIfEmpty(mobileNo, null);
        this.activationDate = activationDate;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.marital = marital;
        this.religion = religion;
        this.dependent = dependent;
        this.education = education;
        this.clientClassification = clientClassification;
        this.clientType = clientType;
        this.imageId = imageId;
        if (imageId != null) {
            this.imagePresent = Boolean.TRUE;
        } else {
            this.imagePresent = null;
        }
        this.staffId = staffId;
        this.staffName = staffName;

        // associations
        this.groups = groups;
        this.addresses = addresses;

        // template
        this.officeOptions = allowedOffices;
        this.staffOptions = staffOptions;
        this.narrations = narrations;

        this.genderOptions = genderOptions;
        this.maritalOptions = maritalOptions;
        this.religionOptions = religionOptions;
        this.dependentOptions = dependentOptions;
        this.educationOptions = educationOptions;
        this.clientClassificationOptions = clientClassificationOptions;
        this.clientTypeOptions = clientTypeOptions;

        this.timeline = timeline;
        this.savingProductOptions = savingProductOptions;
        this.savingsProductId = savingsProductId;
        this.savingsProductName = savingsProductName;
        this.savingsAccountId = savingsAccountId;
        this.savingAccountOptions = savingAccountOptions;

    }

    public Long id() {
        return this.id;
    }

    public String displayName() {
        return this.displayName;
    }

    public Long officeId() {
        return this.officeId;
    }

    public String officeName() {
        return this.officeName;
    }

    public Long getImageId() {
        return this.imageId;
    }

    public Boolean getImagePresent() {
        return this.imagePresent;
    }

    public ClientTimelineData getTimeline() {
        return this.timeline;
    }

    @Override
    public int compareTo(final ClientData obj) {
        if (obj == null) { return -1; }
        return new CompareToBuilder() //
                .append(this.id, obj.id) //
                .append(this.displayName, obj.displayName) //
                .append(this.mobileNo, obj.mobileNo) //
                .toComparison();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        final ClientData rhs = (ClientData) obj;
        return new EqualsBuilder() //
                .append(this.id, rhs.id) //
                .append(this.displayName, rhs.displayName) //
                .append(this.mobileNo, rhs.mobileNo) //
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37) //
                .append(this.id) //
                .append(this.displayName) //
                .toHashCode();
    }

    public String getExternalId() {
        return this.externalId;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public LocalDate getActivationDate() {
        return this.activationDate;
    }
    
}
