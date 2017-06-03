/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.adhocquery.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.adhocquery.api.AdHocJsonInputParams;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;


@Entity
@Table(name = "m_adhoc")
public class AdHoc extends AbstractAuditableCustom<AppUser, Long> {

   
    
   

	@Column(name = "name", length = 100)
    private  String name;
    
    @Column(name = "query", length = 2000)
    private  String query;
   	
    @Column(name = "table_name", length = 100)
    private  String tableName;
    
    @Column(name = "table_fields", length = 2000)
    private  String tableFields;
   	
   	@Column(name = "IsActive", nullable = false)
    private boolean isActive = false;
   	
   	
    private AdHoc(final String name, final String query,final String tableName,final String tableFields ,final boolean isActive) {
        this.name = StringUtils.defaultIfEmpty(name, null);
        this.query=StringUtils.defaultIfEmpty(query,null);
        this.tableName=StringUtils.defaultIfEmpty(tableName,null);
        this.tableFields=StringUtils.defaultIfEmpty(tableFields,null);
        this.isActive = BooleanUtils.toBooleanDefaultIfNull(isActive, false);
       
    }
    public static AdHoc fromJson(final JsonCommand command) {
        final String name = command.stringValueOfParameterNamed(AdHocJsonInputParams.NAME.getValue());
        final String query = command.stringValueOfParameterNamed(AdHocJsonInputParams.QUERY.getValue());
        final String tableName = command.stringValueOfParameterNamed(AdHocJsonInputParams.TABLENAME.getValue());
        final String tableFields = command.stringValueOfParameterNamed(AdHocJsonInputParams.TABLEFIELD.getValue());
        final boolean isActive = command.booleanPrimitiveValueOfParameterNamed(AdHocJsonInputParams.ISACTIVE.getValue());
        return new AdHoc(name,query,tableName,tableFields ,isActive);
    }
    
    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        final String nameParamName = "name";
        if (command.isChangeInStringParameterNamed(nameParamName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(nameParamName);
            actualChanges.put(nameParamName, newValue);
            this.name = newValue;
        }

        final String descriptionParamName = "query";
        if (command.isChangeInStringParameterNamed(descriptionParamName, this.query)) {
            final String newValue = command.stringValueOfParameterNamed(descriptionParamName);
            actualChanges.put(descriptionParamName, newValue);
            this.query = newValue;
        }
        final String tableName = "tableName";
        if (command.isChangeInStringParameterNamed(tableName, this.tableName)) {
            final String newValue = command.stringValueOfParameterNamed(tableName);
            actualChanges.put(tableName, newValue);
            this.tableName = newValue;
        }
        final String tableField = "tableField";
        if (command.isChangeInStringParameterNamed(tableField, this.tableFields)) {
            final String newValue = command.stringValueOfParameterNamed(tableField);
            actualChanges.put(tableField, newValue);
            this.tableFields = newValue;
        }
        final String paramisActive = "isActive";
        if (command.isChangeInBooleanParameterNamed(paramisActive, this.isActive)) {
        	final Boolean newValue = command.booleanObjectValueOfParameterNamed(paramisActive);
            actualChanges.put(tableField, newValue);
            this.isActive = newValue;
        }
        return actualChanges;
    }
/*    public Map<String, Object> update(final JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(15);
       
        handlePropertyUpdate(command, actualChanges, AdHocJsonInputParams.NAME.getValue(), this.name);
        handlePropertyUpdate(command, actualChanges, AdHocJsonInputParams.QUERY.getValue(), this.query);
        handlePropertyUpdate(command, actualChanges, AdHocJsonInputParams.TABLENAME.getValue(),this.tableName);
        handlePropertyUpdate(command, actualChanges, AdHocJsonInputParams.TABLEFIELD.getValue(),this.tableFields);
        handlePropertyUpdate(command, actualChanges, AdHocJsonInputParams.ISACTIVE.getValue(), this.isActive);
        return actualChanges;
    }
    private void handlePropertyUpdate(final JsonCommand command, final Map<String, Object> actualChanges, final String paramName,
            final String propertyToBeUpdated) {
        if (command.isChangeInStringParameterNamed(paramName, propertyToBeUpdated)) {
            final String newValue = command.stringValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            // now update actual property
            if (paramName.equals(AdHocJsonInputParams.NAME.getValue())) {
                this.name = newValue;
            } else if (paramName.equals(AdHocJsonInputParams.QUERY.getValue())) {
                this.query = newValue;
            } else if (paramName.equals(AdHocJsonInputParams.TABLENAME.getValue())) {
                this.tableName = newValue;
            }else if (paramName.equals(AdHocJsonInputParams.TABLEFIELD.getValue())) {
                this.tableFields = newValue;
            }
        }
    }

    private void handlePropertyUpdate(final JsonCommand command, final Map<String, Object> actualChanges, final String paramName,
            final boolean propertyToBeUpdated) {
        if (command.isChangeInBooleanParameterNamed(paramName, propertyToBeUpdated)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            // now update actual property
            if (paramName.equals(AdHocJsonInputParams.ISACTIVE.getValue())) {
                this.isActive = newValue;
            }
        }
    }*/

    public String getName() {
		return name;
	}
	public String getQuery() {
		return query;
	}
	public String getTableName() {
		return tableName;
	}
	public String getTableFields() {
		return tableFields;
	}
    public boolean isActive() {
        return this.isActive;
    }
    public void disableActive() {
        this.isActive = true;
    }
    public void enableActive() {
    	this.isActive = false;
    }

}