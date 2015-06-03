/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.domain;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "m_client_identifier", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "document_type_id", "document_key" }, name = "unique_identifier_key"),
        @UniqueConstraint(columnNames = { "client_id", "document_type_id" }, name = "unique_client_identifier") })
public class ClientIdentifier extends AbstractAuditableCustom<AppUser, Long> {

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "document_type_id", nullable = false)
    private CodeValue documentType;
    
    @ManyToOne
    @JoinColumn(name ="proof_type_id", nullable = false)
    private CodeValue proofType;
    
    @Column(name = "document_key", length = 1000)
    private String documentKey;
    
    @Column(name = "validity")
    @Temporal(TemporalType.DATE)
    private Date validity;
    
    @Column(name = "description", length = 1000)
    private String description;

    public static ClientIdentifier fromJson(final Client client, final CodeValue documentType,final CodeValue proofType,final JsonCommand command) {
        final String documentKey = command.stringValueOfParameterNamed("documentKey");
        final LocalDate validity = command.localDateValueOfParameterNamed("validity");
        
        final String description = command.stringValueOfParameterNamed("description");
        return new ClientIdentifier(client, documentType,proofType, documentKey,validity, description);
    }

    protected ClientIdentifier() {
        //
    }

    private ClientIdentifier(final Client client, final CodeValue documentType,final CodeValue proofType, final String documentKey,final LocalDate validity, final String description) {
        this.client = client;
        this.documentType = documentType;
        this.proofType = proofType;
        this.documentKey = StringUtils.defaultIfEmpty(documentKey, null);
        this.validity = validity.toDate();
        this.description = StringUtils.defaultIfEmpty(description, null);
    }

    public void update(final CodeValue documentType, final CodeValue proofType) {
        this.documentType = documentType;
        this.proofType = proofType;
    }
    

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        final String documentTypeIdParamName = "documentTypeId";
        if (command.isChangeInLongParameterNamed(documentTypeIdParamName, this.documentType.getId())) {
            final Long newValue = command.longValueOfParameterNamed(documentTypeIdParamName);
            actualChanges.put(documentTypeIdParamName, newValue);
        }
        
        final String proofTypeIdParamName = "proofTypeId";
        if (command.isChangeInLongParameterNamed(proofTypeIdParamName,this.proofType.getId())){
        	final Long newValue = command.longValueOfParameterNamed(proofTypeIdParamName);
        	actualChanges.put(proofTypeIdParamName, newValue);
        }

        final String documentKeyParamName = "documentKey";
        if (command.isChangeInStringParameterNamed(documentKeyParamName, this.documentKey)) {
            final String newValue = command.stringValueOfParameterNamed(documentKeyParamName);
            actualChanges.put(documentKeyParamName, newValue);
            this.documentKey = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String descriptionParamName = "description";
        if (command.isChangeInStringParameterNamed(descriptionParamName, this.description)) {
            final String newValue = command.stringValueOfParameterNamed(descriptionParamName);
            actualChanges.put(descriptionParamName, newValue);
            this.description = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        final String validityParamName = "validity";
        if (command.isChangeInLocalDateParameterNamed(validityParamName, new LocalDate(this.validity))) {
        	final Date newValue = command.DateValueOfParameterNamed(validityParamName);
        	actualChanges.put(validityParamName,newValue);
        	this.validity = newValue;
        }

        return actualChanges;
    }

    public String documentKey() {
        return this.documentKey;
    }

    public Long documentTypeId() {
        return this.documentType.getId();
    }
    public Long proofTypeId(){
    	return this.proofType.getId();
    }
}