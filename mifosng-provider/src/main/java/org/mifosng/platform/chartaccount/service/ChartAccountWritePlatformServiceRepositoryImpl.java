package org.mifosng.platform.chartaccount.service;

import org.mifosng.platform.api.commands.ChartAccountCommand;
import org.mifosng.platform.chartaccount.domain.ChartAccount;
import org.mifosng.platform.chartaccount.domain.ChartAccountRepository;
import org.mifosng.platform.security.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChartAccountWritePlatformServiceRepositoryImpl implements  ChartAccountWritePlatformService{

	
	private final PlatformSecurityContext context;
	private final ChartAccountRepository chartaccountrepository;

	@Autowired
	public ChartAccountWritePlatformServiceRepositoryImpl(final PlatformSecurityContext context, final ChartAccountRepository chartaccountrepository) {
		this.context = context;
		this.chartaccountrepository = chartaccountrepository;
	}
		@Transactional
		@Override
		public Long createChartAccount(final ChartAccountCommand command) {
			
			try {
				context.authenticatedUser();
				
				ChartAccountCommandValidator validator=new ChartAccountCommandValidator(command);
				validator.validateForCreate();
				
				ChartAccount chartaccount = ChartAccount.createNew(command.getChartcode(), command.getDescription(),command.getType());
				
				this.chartaccountrepository.saveAndFlush(chartaccount);
				
				return chartaccount.getChartcode();
			} catch (DataIntegrityViolationException dve) {
				 return Long.valueOf(-1);
			}
		}

}
