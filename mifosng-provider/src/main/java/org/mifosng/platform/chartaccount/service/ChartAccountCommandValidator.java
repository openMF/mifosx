package org.mifosng.platform.chartaccount.service;

import java.util.ArrayList;
import java.util.List;

import org.mifosng.platform.DataValidatorBuilder;
import org.mifosng.platform.api.commands.ChartAccountCommand;
import org.mifosng.platform.api.data.ApiParameterError;
import org.mifosng.platform.exceptions.PlatformApiDataValidationException;

public class ChartAccountCommandValidator {
private ChartAccountCommand command;
public ChartAccountCommandValidator(ChartAccountCommand command)
{
this.command=command;
}
public void validateForCreate() {
	List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

	DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(
			dataValidationErrors).resource("chartaccount");
	baseDataValidator.reset().parameter("chartcode").value(command.getChartcode()).notNull().integerGreaterThanZero();
	baseDataValidator.reset().parameter("description").value(command.getDescription()).notBlank();
	baseDataValidator.reset().parameter("type").value(command.getType()).notBlank();


	if (!dataValidationErrors.isEmpty()) {
		throw new PlatformApiDataValidationException(
				"validation.msg.validation.errors.exist",
				"Validation errors exist.", dataValidationErrors);
	}
}

}