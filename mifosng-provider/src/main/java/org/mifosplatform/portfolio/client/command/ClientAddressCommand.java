/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.Chronology;
import org.joda.time.LocalDate;
import org.joda.time.ReadablePartial;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;

public class ClientAddressCommand {

	private final Long addressTypeId;
	private final String address_line;
	private final String address_line_two;
	private final String landmark;
	private final String city;
	private final String pincode;
	private final Boolean isBoth;
	private final Long stateTypeId;
	
	
	public ClientAddressCommand(final Long addressTypeId, final String address_line, final String address_line_two, final String landmark, final String city,final String pincode, final boolean isBoth, final Long stateTypeId){
		
		this.addressTypeId = addressTypeId;
		this.address_line = address_line;
		this.address_line_two = address_line_two;
		this.landmark = landmark;
		this.city = city;
		this.pincode = pincode;
		this.isBoth = isBoth;
		this.stateTypeId = stateTypeId;
		
	}
	
	public Long getAddressTypeId(){
		return this.addressTypeId;
	}
	
	public String getAddressLine(){
		return this.address_line;
	}
	
	public String getAddressLineTwo(){
		return this.address_line_two;
	}
	
	public String getLandMark(){
		return this.landmark;
	}
	
	public String getCity(){
		return this.city;
	}
	public String getPinCode(){
		return this.pincode;
	}
	public Boolean getIsBoth(){
		return this.isBoth;
	}
	
	public Long getStateTypeId(){
		return this.stateTypeId;
		
	}
	
	public void validateForCreate(){
		
		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("clientAddress");
		 baseDataValidator.reset().parameter("addressTypeId").value(this.addressTypeId).notNull().integerGreaterThanZero();
	        baseDataValidator.reset().parameter("address_line").value(this.address_line).notBlank();
	        	baseDataValidator.reset().parameter("pincode").value(this.pincode).notBlank().notExceedingLengthOf(6);
			

	        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
	                "Validation errors exist.", dataValidationErrors); }
	}
	
	public void validateForUpdate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("clientAddress");
        String expression = "\\d{5}(-\\d{4})?";

        baseDataValidator.reset().parameter("address_line").value(this.address_line).ignoreIfNull().notBlank();
        baseDataValidator.reset().parameter("pincode").value(this.pincode).notBlank().notExceedingLengthOf(6).matchesRegularExpression(expression);

        // FIXME - KW - add in validation
        // if (command.isDocumentTypeChanged()) {
        // baseDataValidator.reset().parameter("documentTypeId").value(command.getDocumentTypeId()).notNull().integerGreaterThanZero();
        // }

        baseDataValidator.reset().anyOfNotNull(this.addressTypeId, this.address_line);

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }
}
