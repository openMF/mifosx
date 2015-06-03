/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.serialization;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.serialization.AbstractFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.serialization.FromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.client.command.ClientIdentifierCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Implementation of {@link FromApiJsonDeserializer} for
 * {@link ClientIdentifierCommandFromApiJsonDeserializer} 's.
 */
@Component
public final class ClientIdentifierCommandFromApiJsonDeserializer extends AbstractFromApiJsonDeserializer<ClientIdentifierCommand> {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> supportedParameters = new HashSet<>(Arrays.asList("documentTypeId", "proofTypeId","documentKey", "validity","locale", "dateFormat", "description"));

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ClientIdentifierCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    @Override
    public ClientIdentifierCommand commandFromApiJson(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final Long documentTypeId = this.fromApiJsonHelper.extractLongNamed("documentTypeId", element);
        final Long proofTypeId = this.fromApiJsonHelper.extractLongNamed("proofTypeId", element);
        final String documentKey = this.fromApiJsonHelper.extractStringNamed("documentKey", element);
        final String locale = this.fromApiJsonHelper.extractStringNamed("locale", element);
        final String dateFormat = this.fromApiJsonHelper.extractStringNamed("dateFormat",element);
        final String documentDescription = this.fromApiJsonHelper.extractStringNamed("documentDescription", element);
        	final LocalDate validity = this.fromApiJsonHelper.extractLocalDateNamed("validity", element);
        	return new ClientIdentifierCommand(documentTypeId,proofTypeId, documentKey, validity,locale,dateFormat, documentDescription);
    }
}