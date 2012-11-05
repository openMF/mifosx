package org.mifosng.platform.chartaccount.service;

import org.mifosng.platform.api.commands.ChartAccountCommand;

public interface ChartAccountWritePlatformService {
	
	public Long createChartAccount(final ChartAccountCommand command);

}
