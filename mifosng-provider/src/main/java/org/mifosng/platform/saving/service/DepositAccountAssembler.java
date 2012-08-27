package org.mifosng.platform.saving.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosng.platform.api.commands.DepositAccountCommand;
import org.mifosng.platform.api.commands.DepositStateTransitionApprovalCommand;
import org.mifosng.platform.client.domain.Client;
import org.mifosng.platform.client.domain.ClientRepository;
import org.mifosng.platform.currency.domain.Money;
import org.mifosng.platform.exceptions.ClientNotFoundException;
import org.mifosng.platform.exceptions.DepositProductNotFoundException;
import org.mifosng.platform.exceptions.NoAuthorizationException;
import org.mifosng.platform.loan.domain.PeriodFrequencyType;
import org.mifosng.platform.saving.domain.DepositAccount;
import org.mifosng.platform.saving.domain.DepositApprovalData;
import org.mifosng.platform.saving.domain.DepositLifecycleStateMachine;
import org.mifosng.platform.saving.domain.DepositLifecycleStateMachineImpl;
import org.mifosng.platform.saving.domain.DepositStatus;
import org.mifosng.platform.saving.domain.FixedTermDepositInterestCalculator;
import org.mifosng.platform.savingproduct.domain.DepositProduct;
import org.mifosng.platform.savingproduct.domain.DepositProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * An assembler for turning {@link DepositAccountCommand} into {@link DepositAccount}'s.
 */
@Service
public class DepositAccountAssembler {

	private final ClientRepository clientRepository;
	private final DepositProductRepository depositProductRepository;
	private final FixedTermDepositInterestCalculator fixedTermDepositInterestCalculator;

	@Autowired
	public DepositAccountAssembler(final ClientRepository clientRepository, final DepositProductRepository depositProductRepository,
			final FixedTermDepositInterestCalculator fixedTermDepositInterestCalculator) {
		this.clientRepository = clientRepository;
		this.depositProductRepository = depositProductRepository;
		this.fixedTermDepositInterestCalculator = fixedTermDepositInterestCalculator;
	}

	public DepositAccount assembleFrom(final DepositAccountCommand command) {

		Client client = this.clientRepository.findOne(command.getClientId());
		if (client == null || client.isDeleted()) {
			throw new ClientNotFoundException(command.getClientId());
		}
		
		DepositProduct product = this.depositProductRepository.findOne(command.getProductId());
		if (product == null || product.isDeleted()) {
			throw new DepositProductNotFoundException(command.getProductId());
		} 
		
		// details inherited from product setting (unless allowed to be overridden through account creation api
		Money deposit = Money.of(product.getCurrency(), command.getDepositAmount());
		
		Integer tenureInMonths = product.getTenureInMonths();
		if (command.getTenureInMonths() != null) {
			tenureInMonths = command.getTenureInMonths();
		}
		
		BigDecimal maturityInterestRate = product.getMaturityDefaultInterestRate();
		if (command.getMaturityInterestRate() != null) {
			maturityInterestRate = command.getMaturityInterestRate();
		}
			
		Integer compoundingInterestEvery = product.getInterestCompoundedEvery();
		if (command.getInterestCompoundedEvery() != null) {
			compoundingInterestEvery = command.getInterestCompoundedEvery();
		}
		
		PeriodFrequencyType compoundingInterestFrequency = product.getInterestCompoundedEveryPeriodType();
		if (command.getInterestCompoundedEveryPeriodType() != null) {
			compoundingInterestFrequency = PeriodFrequencyType.fromInt(command.getInterestCompoundedEveryPeriodType());
		}
		
		boolean renewalAllowed = product.isRenewalAllowed();
		if (command.isRenewalAllowedChanged()) {
			renewalAllowed = command.isRenewalAllowed();
		}
		
		boolean preClosureAllowed = product.isPreClosureAllowed();
		if (command.isPreClosureAllowedChanged()) {
			preClosureAllowed = command.isPreClosureAllowed();
		}
		// end of details allowed to be overriden from product
		
		DepositAccount account =new DepositAccount().openNew(client, product, command.getExternalId(), 
				deposit, 
				maturityInterestRate, 
				tenureInMonths, compoundingInterestEvery, compoundingInterestFrequency, 
				command.getCommencementDate(), 
				renewalAllowed, preClosureAllowed, this.fixedTermDepositInterestCalculator, defaultDepositLifecycleStateMachine());
		
		return account;
	}
	
	public DepositApprovalData assembleFrom(DepositStateTransitionApprovalCommand command){
		
		DepositProduct product = this.depositProductRepository.findOne(command.getProductId());
		if (product == null || product.isDeleted()) {
			throw new DepositProductNotFoundException(command.getProductId());
		} 
		
		PeriodFrequencyType interestCompoundedEveryPeriodType= product.getInterestCompoundedEveryPeriodType();
		if (command.getInterestCompoundedEveryPeriodType() != null) {
			interestCompoundedEveryPeriodType = PeriodFrequencyType.fromInt(command.getInterestCompoundedEveryPeriodType());
		}
		
		Integer tenureInMonths = command.getTenureInMonths();
		Money deposit = Money.of(product.getCurrency(), command.getDepositAmount());
		BigDecimal maturityInterestRate = product.getMaturityDefaultInterestRate();
		Integer interestCompoundedEvery = product.getInterestCompoundedEvery();
		
  		return new DepositApprovalData(tenureInMonths, deposit, maturityInterestRate, interestCompoundedEvery, interestCompoundedEveryPeriodType, this.fixedTermDepositInterestCalculator);
		
	}
	
	
	private DepositLifecycleStateMachine defaultDepositLifecycleStateMachine() {
		List<DepositStatus> allowedDepositStatuses = Arrays.asList(DepositStatus.values());
		return new DepositLifecycleStateMachineImpl(allowedDepositStatuses);
	}
}