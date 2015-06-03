/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;

/**
 * Immutable command for creating or updating details of a client identifier.
 */
public class ClientIdentifierCommand {

    private final Long documentTypeId;
    private final Long proofTypeId;
    private final String documentKey;
    private final LocalDate validity;
    private final String locale;
    private final String dateFormat;
    private final String description;

    public ClientIdentifierCommand(final Long documentTypeId,final Long proofTypeId, final String documentKey, final LocalDate validity,final String locale, final String dateFormat,final String description) {
        this.documentTypeId = documentTypeId;
        this.proofTypeId = proofTypeId;
        this.documentKey = documentKey;
        this.validity = validity;
        this.locale = locale;
        this.dateFormat = dateFormat;
        this.description = description;
    }

    public Long getDocumentTypeId() {
        return this.documentTypeId;
    }

    public String getDocumentKey() {
        return this.documentKey;
    }

    public String getDescription() {
        return this.description;
    }
    
    public LocalDate getValidity() {
		return this.validity;
	}
    
    public String getlocale(){
    	return this.locale;
    }
    
    public Long getproofTypeId(){
    	return this.proofTypeId;
    }
 

    public void validateForCreate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("clientIdentifier");

        baseDataValidator.reset().parameter("documentTypeId").value(this.documentTypeId).notNull().integerGreaterThanZero();
        baseDataValidator.reset().parameter("documentKey").value(this.documentKey).notBlank();

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

    public void validateForUpdate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("clientIdentifier");

        baseDataValidator.reset().parameter("documentKey").value(this.documentKey).ignoreIfNull().notBlank();

        // FIXME - KW - add in validation
        // if (command.isDocumentTypeChanged()) {
        // baseDataValidator.reset().parameter("documentTypeId").value(command.getDocumentTypeId()).notNull().integerGreaterThanZero();
        // }

        baseDataValidator.reset().anyOfNotNull(this.documentTypeId, this.documentKey);

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }
}