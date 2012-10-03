package org.mifosng.platform.saving.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosng.platform.api.commands.DepositAccountCommand;
import org.mifosng.platform.client.domain.Client;
import org.mifosng.platform.client.domain.ClientRepository;
import org.mifosng.platform.currency.domain.Money;
import org.mifosng.platform.exceptions.ClientNotFoundException;
import org.mifosng.platform.exceptions.DepositAccounDataValidationtException;
import org.mifosng.platform.exceptions.DepositProductNotFoundException;
import org.mifosng.platform.loan.domain.PeriodFrequencyType;
import org.mifosng.platform.saving.domain.DepositAccount;
import org.mifosng.platform.saving.domain.DepositAccountStatus;
import org.mifosng.platform.saving.domain.DepositLifecycleStateMachine;
import org.mifosng.platform.saving.domain.DepositLifecycleStateMachineImpl;
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
		
		boolean isInterestWithdrawable = command.isInterestWithdrawable();
		
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
		
		BigDecimal preClosureInterestRate = product.getPreClosureInterestRate();
		if(command.getPreClosureInterestRate()!=null){
			preClosureInterestRate = command.getPreClosureInterestRate();
		}
		
		if(product.getMaturityMinInterestRate().compareTo(preClosureInterestRate)==-1){
			throw new DepositAccounDataValidationtException(preClosureInterestRate, product.getMaturityMinInterestRate());
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
		
		boolean interestCompoundingAllowed= product.isInterestCompoundingAllowed();
		if(command.isInterestCompoundingAllowedChanged()){
			interestCompoundingAllowed = command.isInterestCompoundingAllowed();
		}
		// end of details allowed to be overriden from product
		
		DepositAccount account =new DepositAccount().openNew(client, product, command.getExternalId(), 
				deposit, 
				maturityInterestRate, preClosureInterestRate, 
				tenureInMonths, compoundingInterestEvery, compoundingInterestFrequency, 
				command.getCommencementDate(), 
				renewalAllowed, preClosureAllowed, this.fixedTermDepositInterestCalculator,
				defaultDepositLifecycleStateMachine(),isInterestWithdrawable,interestCompoundingAllowed);
		
		return account;
	}
	
	private DepositLifecycleStateMachine defaultDepositLifecycleStateMachine() {
		List<DepositAccountStatus> allowedDepositStatuses = Arrays.asList(DepositAccountStatus.values());
		return new DepositLifecycleStateMachineImpl(allowedDepositStatuses);
	}

	public DepositAccount assembleFrom(DepositAccount account, DepositAccountCommand command) {
		
		Client client = account.client();
		
		DepositProduct product = account.product();
		if(command.getProductId() != null){
			 product = this.depositProductRepository.findOne(command.getProductId());
				if (product == null || product.isDeleted()) {
					throw new DepositProductNotFoundException(command.getProductId());
				} 
		}
		
		Money deposit = account.getDeposit();
		if(command.getDepositAmount() != null){
			deposit = Money.of(account.getDeposit().getCurrency(), command.getDepositAmount());
		}
		
		Integer tenureInMonths = account.getTenureInMonths();
		if(command.getTenureInMonths() != null){
			tenureInMonths =command.getTenureInMonths();
		}
		
		BigDecimal maturityInterestRate = account.getInterestRate();
		if(command.getMaturityInterestRate() != null){
			maturityInterestRate = command.getMaturityInterestRate();
		}
		
		BigDecimal preClosureInterestRate = account.getPreClosureInterestRate();
		if(command.getPreClosureInterestRate() != null){
			preClosureInterestRate = command.getPreClosureInterestRate();
		}
		
		
		if(product.getMaturityMinInterestRate().compareTo(preClosureInterestRate)==-1){
			throw new DepositAccounDataValidationtException(preClosureInterestRate, product.getMaturityMinInterestRate());
		}
		
		Integer compoundingInterestEvery = account.getInterestCompoundedEvery();
		if(command.getInterestCompoundedEvery() != null){
			compoundingInterestEvery = command.getInterestCompoundedEvery();
		}
		
		PeriodFrequencyType compoundingInterestFrequency = account.getInterestCompoundedFrequencyType();
		if(command.getInterestCompoundedEveryPeriodType() !=null){
			compoundingInterestFrequency = PeriodFrequencyType.fromInt(command.getInterestCompoundedEveryPeriodType());
		}
		
		boolean renewalAllowed = account.isRenewalAllowed();
		if(command.isRenewalAllowedChanged()){
			renewalAllowed = command.isRenewalAllowed();
		}
		
		boolean preClosureAllowed = account.isPreClosureAllowed();
		if(command.isPreClosureAllowedChanged()){
			preClosureAllowed =  command.isPreClosureAllowed();
		}
		
		boolean isInterestWithdrawable = account.isInterestWithdrawable();
		if(command.isInterestWithdrawableChanged()){
			isInterestWithdrawable = command.isInterestWithdrawable();
		}
		
		boolean isInterestCompoundingAllowed = account.isInterestCompoundingAllowed();
		if(command.isInterestCompoundingAllowedChanged()){
			isInterestCompoundingAllowed = command.isInterestCompoundingAllowed();
		}
		
		DepositAccount newAccount =new DepositAccount().openNew(client, product, null, 
				deposit, 
				maturityInterestRate, preClosureInterestRate, 
				tenureInMonths, compoundingInterestEvery, compoundingInterestFrequency, 
				account.maturesOnDate(), 
				renewalAllowed, preClosureAllowed, this.fixedTermDepositInterestCalculator, defaultDepositLifecycleStateMachine(),
				isInterestWithdrawable,isInterestCompoundingAllowed);
		
		newAccount.updateAccount(account);
		
		return newAccount;
	}

	public void adjustTotalAmountForPreclosureInterest(DepositAccount account) {
		account.adjustTotalAmountForPreclosureInterest(account,this.fixedTermDepositInterestCalculator);
	}

	public void assembleUpdatedDepositAccount(DepositAccount account,DepositAccountCommand command) {
		
		DepositProduct product = account.product();
		if(command.getProductId() != null){
			 product = this.depositProductRepository.findOne(command.getProductId());
				if (product == null || product.isDeleted()) {
					throw new DepositProductNotFoundException(command.getProductId());
				} 
		}
		
		String externalId = account.getExternalId();
		if(command.isExternalIdChanged()){
			externalId = command.getExternalId();
		}
		
		LocalDate commencementDate = account.getProjectedCommencementDate();
		if(command.isCommencementDateChanged()){
			commencementDate = new LocalDate(command.getCommencementDate());
		}
		
		Money deposit = account.getDeposit();
		if(command.isDepositAmountChanged()){
			deposit = Money.of(product.getCurrency(), command.getDepositAmount());
		}
		
		Integer tenureInMonths = account.getTenureInMonths();
		if(command.isTenureInMonthsChanged()){
			tenureInMonths =command.getTenureInMonths();
		}
		
		BigDecimal maturityInterestRate = account.getInterestRate();
		if(command.isMaturityActualInterestRateChanged()){
			maturityInterestRate = command.getMaturityInterestRate();
		}
		
		BigDecimal preClosureInterestRate = account.getPreClosureInterestRate();
		if(command.isPreClosureInterestRateChanged()){
			preClosureInterestRate = command.getPreClosureInterestRate();
		}
		
		if(product.getMaturityMinInterestRate().compareTo(preClosureInterestRate)==-1){
			throw new DepositAccounDataValidationtException(preClosureInterestRate, product.getMaturityMinInterestRate());
		}
		
		Integer compoundingInterestEvery = account.getInterestCompoundedEvery();
		if(command.isCompoundingInterestEveryChanged()){
			compoundingInterestEvery = command.getInterestCompoundedEvery();
		}
		
		PeriodFrequencyType compoundingInterestFrequency = account.getInterestCompoundedFrequencyType();
		if(command.getInterestCompoundedEveryPeriodType() !=null){
			compoundingInterestFrequency = PeriodFrequencyType.fromInt(command.getInterestCompoundedEveryPeriodType());
		}
		
		boolean renewalAllowed = account.isRenewalAllowed();
		if(command.isRenewalAllowedChanged()){
			renewalAllowed = command.isRenewalAllowed();
		}
		
		boolean preClosureAllowed = account.isPreClosureAllowed();
		if(command.isPreClosureAllowedChanged()){
			preClosureAllowed =  command.isPreClosureAllowed();
		}
		
		boolean isInterestWithdrawable = account.isInterestWithdrawable();
		if(command.isInterestWithdrawableChanged()){
			isInterestWithdrawable = command.isInterestWithdrawable();
		}
		
		boolean isInterestCompoundingAllowed = account.isInterestCompoundingAllowed();
		if(command.isInterestCompoundingAllowedChanged()){
			isInterestCompoundingAllowed = command.isInterestCompoundingAllowed();
		}
		account.update(product,externalId,commencementDate,deposit,tenureInMonths,maturityInterestRate,preClosureInterestRate,compoundingInterestEvery,compoundingInterestFrequency,renewalAllowed,
				preClosureAllowed,isInterestWithdrawable,isInterestCompoundingAllowed,this.fixedTermDepositInterestCalculator,defaultDepositLifecycleStateMachine());
	}

	public void updateApprovedDepositAccount(DepositAccount account, DepositAccountCommand command) {
		
		boolean renewalAllowed = account.isRenewalAllowed();
		if(command.isRenewalAllowedChanged()){
			renewalAllowed = command.isRenewalAllowed();
		}
		
		boolean isInterestWithdrawable = account.isInterestWithdrawable();
		if(command.isInterestWithdrawableChanged()){
			isInterestWithdrawable = command.isInterestWithdrawable();
		}
		account.update(renewalAllowed,isInterestWithdrawable);
		
	}
}