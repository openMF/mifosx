package org.mifosplatform.infrastructure.configuration.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.configuration.service.ExternalServicesConstants.EXTERNALSERVICEPROPERTIES_JSON_INPUT_PARAMS;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class ExternalServicesPropertiesCommandFromApiJsonDeserializer {

	private final Set<String> supportedParameters = EXTERNALSERVICEPROPERTIES_JSON_INPUT_PARAMS.getAllValues();
	private final FromJsonHelper fromApiJsonHelper;
	
	@Autowired
	public ExternalServicesPropertiesCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper){
		this.fromApiJsonHelper = fromApiJsonHelper;		 
	}
	
	public void validateForUpdate(final String json) {
		if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
		
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);
		
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("external.service");
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        
	 }
	
	public Set<String> getNameKeys(final String json){
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		Map<String, String> jsonMap = this.fromApiJsonHelper.extractDataMap(typeOfMap, json);
		Set<String> keyNames = jsonMap.keySet();
		return keyNames;
	}
}
