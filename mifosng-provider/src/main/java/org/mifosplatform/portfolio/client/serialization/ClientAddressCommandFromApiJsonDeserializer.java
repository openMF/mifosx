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
import org.mifosplatform.portfolio.client.command.ClientAddressCommand;
import org.mifosplatform.portfolio.client.command.ClientIdentifierCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;


@Component
public class ClientAddressCommandFromApiJsonDeserializer extends AbstractFromApiJsonDeserializer<ClientAddressCommand>{
	
	/**
	 * The Parmeter Supported for this command
	 */
	private final Set<String> supportedParameters = new HashSet<>(Arrays.asList("addressTypeId", "address_line", "address_line_two", "landmark", "city", "pincode", "isBoth", "stateTypeId"));
	
	private final FromJsonHelper fromApiJsonHelper;
	
	@Autowired
	public ClientAddressCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper){
		
		this.fromApiJsonHelper = fromApiJsonHelper;
		
	}
	
	@Override
	public ClientAddressCommand commandFromApiJson(final String json){
		
		if (StringUtils.isBlank(json)){throw new InvalidJsonException();}
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final Long addressTypeId = this.fromApiJsonHelper.extractLongNamed("addressTypeId", element);
        final String address_line = this.fromApiJsonHelper.extractStringNamed("address_line", element);
        final String address_line_two = this.fromApiJsonHelper.extractStringNamed("address_line_two", element);
        final String landmark = this.fromApiJsonHelper.extractStringNamed("landmark", element);
        final String city = this.fromApiJsonHelper.extractStringNamed("city",element);
        final String pincode = this.fromApiJsonHelper.extractStringNamed("pincode", element);
        final Boolean isBoth = this.fromApiJsonHelper.extractBooleanNamed("isBoth", element);
        final Long stateTypeId = this.fromApiJsonHelper.extractLongNamed("stateTypeId", element);
        
        	
        	return new ClientAddressCommand(addressTypeId,address_line, address_line_two, landmark,city,pincode,isBoth,stateTypeId);
    }
		
	}


