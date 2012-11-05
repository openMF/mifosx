package org.mifosng.platform.chartaccount.service;

import java.util.Collection;

import org.mifosng.platform.api.data.ChartAccountData;

public interface ChartAccountReadPlatformService {
	public Collection<ChartAccountData> retrieveAllChartAccount();
	public ChartAccountData retrieveChartAccount(final Long id);
}
