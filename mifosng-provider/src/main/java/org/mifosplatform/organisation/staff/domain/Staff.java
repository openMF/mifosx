/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.staff.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.office.domain.Office;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_staff", uniqueConstraints = { @UniqueConstraint(columnNames = { "display_name" }, name = "display_name") ,
@UniqueConstraint(columnNames = { "external_id" }, name = "external_id_UNIQUE") })
public class Staff extends AbstractPersistable<Long> {

    @Column(name = "firstname", length = 50)
    private String firstname;

    @Column(name = "lastname", length = 50)
    private String lastname;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "external_id", length = 100, nullable = true, unique = true)
    private String externalId;

    // Office to which this employee belongs
    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    // Flag determines if employee is a loan Officer
    @Column(name = "is_loan_officer ", nullable = false)
    private boolean loanOfficer;

    @Column(name = "organisational_role_enum", nullable = true)
    private Integer organisationalRoleType;

    @SuppressWarnings("unused")
    @ManyToOne
    @JoinColumn(name = "organisational_role_parent_staff_id", nullable = true)
    private Staff organisationalRoleParentStaff;

    public static Staff fromJson(final Office staffOffice, final JsonCommand command) {

        final String firstnameParamName = "firstname";
        String firstname = command.stringValueOfParameterNamed(firstnameParamName);

        final String lastnameParamName = "lastname";
        String lastname = command.stringValueOfParameterNamed(lastnameParamName);
        
        final String externalIdParamName = "externalId";
        String externalId = command.stringValueOfParameterNamedAllowingNull(externalIdParamName);

        final String isLoanOfficerParamName = "isLoanOfficer";
        final boolean isLoanOfficer = command.booleanPrimitiveValueOfParameterNamed(isLoanOfficerParamName);

        return new Staff(staffOffice, firstname, lastname, externalId, isLoanOfficer);
    }

    public static Staff createNew(final Office staffOffice, final String firstname, final String lastname, final String externalId, final boolean isLoanOfficer) {
        return new Staff(staffOffice, firstname, lastname, externalId, isLoanOfficer);
    }

    protected Staff() {
        //
    }

    private Staff(final Office staffOffice, final String firstname, final String lastname, final String externalId, final boolean isLoanOfficer) {
        this.office = staffOffice;
        this.firstname = StringUtils.defaultIfEmpty(firstname, null);
        this.lastname = StringUtils.defaultIfEmpty(lastname, null);
        this.externalId = externalId;
        this.loanOfficer = isLoanOfficer;
        deriveDisplayName(firstname);
    }

    public EnumOptionData organisationalRoleData() {
        EnumOptionData organisationalRole = null;
        if (this.organisationalRoleType != null) {
            organisationalRole = StaffEnumerations.organisationalRole(this.organisationalRoleType);
        }
        return organisationalRole;
    }

    public void changeOffice(final Office newOffice) {
        this.office = newOffice;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(7);

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

        final String externalIdParamName = "externalId";
        if (command.isChangeInStringParameterNamed(externalIdParamName, this.externalId)) {
            final String newValue = command.stringValueOfParameterNamed(externalIdParamName);
            actualChanges.put(externalIdParamName, newValue);
            this.externalId = newValue;
        }
        
        final String isLoanOfficerParamName = "isLoanOfficer";
        if (command.isChangeInBooleanParameterNamed(isLoanOfficerParamName, this.loanOfficer)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(isLoanOfficerParamName);
            actualChanges.put(isLoanOfficerParamName, newValue);
            this.loanOfficer = newValue;
        }

        return actualChanges;
    }

    public boolean isNotLoanOfficer() {
        return !isLoanOfficer();
    }

    public boolean isLoanOfficer() {
        return this.loanOfficer;
    }

    private void deriveDisplayName(final String firstname) {
        if (!StringUtils.isBlank(firstname)) {
            this.displayName = this.lastname + ", " + this.firstname;
        } else {
            this.displayName = this.lastname;
        }
    }

    public boolean identifiedBy(final Staff staff) {
        return getId().equals(staff.getId());
    }

    public Long officeId() {
        return this.office.getId();
    }

    public String displayName() {
        return this.displayName;
    }
    
    public Office office() {
        return this.office;
    }
}