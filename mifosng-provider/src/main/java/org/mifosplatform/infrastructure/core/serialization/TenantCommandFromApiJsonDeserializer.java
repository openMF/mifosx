package org.mifosplatform.infrastructure.core.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.reflect.TypeToken;

@Component
public class TenantCommandFromApiJsonDeserializer {
	
	private final Set<String> supportedParameters = new HashSet<String>(
			Arrays.asList("identifier", "name", "schemaName", "timezoneId",
					"countryId", "joinedDate", "createdDate",
					"lastmodifiedDate", "schemaServer", "schemaServerPort",
					"schemaUsername", "schemaPassword", "autoUpdate"));
	
	private final FromJsonHelper fromApiJsonHelper;
	
	@Autowired
	public TenantCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
		
		this.fromApiJsonHelper = fromApiJsonHelper;
	}
	
	public void validateForCreate(final String json) {
		
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		
		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}
	
	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }
}
