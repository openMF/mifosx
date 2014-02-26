/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.fund.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_fund", uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "fund_type_cv_id" }, name = "fund_name_org"),
        @UniqueConstraint(columnNames = { "external_id" }, name = "fund_externalid_org") })
public class Fund extends AbstractPersistable<Long> {

    @Column(name = "name")
    private String name;

    @Column(name = "external_id", length = 100)
    private String externalId;
    
    @Column(name = "fund_type_cv_id")
    private Long fundTypeId;

    public static Fund fromJson(final JsonCommand command) {

        final String firstnameParamName = "name";
        final String name = command.stringValueOfParameterNamed(firstnameParamName);

        final String lastnameParamName = "externalId";
        final String externalId = command.stringValueOfParameterNamed(lastnameParamName);
        
        final String fundTypeIdParamName = "fundTypeId";
        final Long fundTypeId = command.longValueOfParameterNamed(fundTypeIdParamName);

        return new Fund(name, externalId, fundTypeId);
    }

    protected Fund() {
        //
    }

    private Fund(final String fundName, final String externalId, final Long fundTypeId) {
        this.name = StringUtils.defaultIfEmpty(fundName, null);
        this.externalId = StringUtils.defaultIfEmpty(externalId, null);
        this.fundTypeId = fundTypeId;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(7);

        final String nameParamName = "name";
        if (command.isChangeInStringParameterNamed(nameParamName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(nameParamName);
            actualChanges.put(nameParamName, newValue);
            this.name = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String externalIdParamName = "externalId";
        if (command.isChangeInStringParameterNamed(externalIdParamName, this.externalId)) {
            final String newValue = command.stringValueOfParameterNamed(externalIdParamName);
            actualChanges.put(externalIdParamName, newValue);
            this.externalId = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        final String fundTypeIdParamName = "fundTypeId";
        if (command.isChangeInLongParameterNamed(fundTypeIdParamName, this.fundTypeId)) {
        	final Long newValue = command.longValueOfParameterNamed(fundTypeIdParamName);
            actualChanges.put(fundTypeIdParamName, newValue);
            this.fundTypeId = newValue;
        }

        return actualChanges;
    }
}