/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.data;

import java.util.Collection;
import java.util.Date;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;

/**
 * Immutable data object represent client identity data.
 */
public class ClientIdentifierData {

    private final Long id;
    private final Long clientId;
    private final CodeValueData documentType;
    private final CodeValueData proofType;
    private final String documentKey;
    private final LocalDate validity;
    private final String locale;
    private final String dateFormat;
    private final String description;
    @SuppressWarnings("unused")
    private final Collection<CodeValueData> allowedDocumentTypes;
    private final Collection<CodeValueData> allowedProofTypes;

    public static ClientIdentifierData singleItem(final Long id, final Long clientId, final CodeValueData documentType,
    		final CodeValueData proofType,
            final String documentKey,final LocalDate validity, final String locale, final String dateFormat, final String description) {
        return new ClientIdentifierData(id, clientId, documentType, proofType,documentKey,validity,locale,dateFormat, description, null, null);
    }

    public static ClientIdentifierData template(final Collection<CodeValueData> codeValues, final Collection<CodeValueData> proofValues) {
        return new ClientIdentifierData(null, null, null, null,null, null,null, null,null, codeValues, proofValues);
    }

    public static ClientIdentifierData template(final ClientIdentifierData data, final Collection<CodeValueData> codeValues, final Collection<CodeValueData> proofValues) {
        return new ClientIdentifierData(data.id, data.clientId, data.documentType,data.proofType, data.documentKey,data.validity,data.locale, data.dateFormat ,data.description, codeValues, proofValues);
    }

    public ClientIdentifierData(final Long id, final Long clientId, final CodeValueData documentType,final CodeValueData proofType, final String documentKey,final LocalDate validity,
    		final String locale, final String dateFormat,
            final String description, final Collection<CodeValueData> allowedDocumentTypes, final Collection<CodeValueData>allowedProofTypes) {
        this.id = id;
        this.clientId = clientId;
        this.documentType = documentType;
        this.proofType = proofType;
        this.documentKey = documentKey;
        this.validity = validity;
        this.locale = locale;
        this.dateFormat = dateFormat;
        this.description = description;
        this.allowedDocumentTypes = allowedDocumentTypes;
        this.allowedProofTypes = allowedProofTypes;
    }

	public Collection<CodeValueData> getallowedProofTypes() {
		return allowedProofTypes;
	}
}