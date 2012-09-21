package org.mifosng.platform.saving.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.mifosng.platform.api.commands.DepositAccountCommand;
import org.mifosng.platform.api.commands.DepositAccountWithdrawInterestCommand;
import org.mifosng.platform.api.commands.DepositAccountWithdrawalCommand;
import org.mifosng.platform.api.commands.DepositStateTransitionApprovalCommand;
import org.mifosng.platform.api.commands.DepositStateTransitionCommand;
import org.mifosng.platform.api.commands.UndoStateTransitionCommand;
import org.mifosng.platform.api.data.EntityIdentifier;
import org.mifosng.platform.client.domain.Note;
import org.mifosng.platform.client.domain.NoteRepository;
import org.mifosng.platform.currency.domain.Money;
import org.mifosng.platform.exceptions.DepositAccountNotFoundException;
import org.mifosng.platform.exceptions.DepositAccountReopenException;
import org.mifosng.platform.exceptions.NoAuthorizationException;
import org.mifosng.platform.exceptions.PlatformDataIntegrityException;
import org.mifosng.platform.exceptions.ProductNotFoundException;
import org.mifosng.platform.saving.domain.DepositAccount;
import org.mifosng.platform.saving.domain.DepositAccountRepository;
import org.mifosng.platform.saving.domain.DepositAccountStatus;
import org.mifosng.platform.saving.domain.DepositAccountTransaction;
import org.mifosng.platform.saving.domain.DepositAccountTransactionType;
import org.mifosng.platform.saving.domain.DepositLifecycleStateMachine;
import org.mifosng.platform.saving.domain.DepositLifecycleStateMachineImpl;
import org.mifosng.platform.saving.domain.FixedTermDepositInterestCalculator;
import org.mifosng.platform.security.PlatformSecurityContext;
import org.mifosng.platform.user.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepositAccountWritePlatformServiceJpaRepositoryImpl implements DepositAccountWritePlatformService {

	private final static Logger logger = LoggerFactory.getLogger(DepositAccountWritePlatformServiceJpaRepositoryImpl.class);
	
	private final PlatformSecurityContext context;
	private final DepositAccountRepository depositAccountRepository;
	private final DepositAccountAssembler depositAccountAssembler;
	private final FixedTermDepositInterestCalculator fixedTermDepositInterestCalculator;
	private final NoteRepository noteRepository;
	
	@Autowired
	public DepositAccountWritePlatformServiceJpaRepositoryImpl(
			final PlatformSecurityContext context, 
			final DepositAccountRepository depositAccountRepository, 
			final DepositAccountAssembler depositAccountAssembler,
			final FixedTermDepositInterestCalculator fixedTermDepositInterestCalculator,
			final NoteRepository noteRepository
			) {
		this.context=context;
		this.depositAccountRepository = depositAccountRepository;
		this.depositAccountAssembler = depositAccountAssembler;
		this.fixedTermDepositInterestCalculator = fixedTermDepositInterestCalculator;
		this.noteRepository = noteRepository;
	}

	/*
	 * Guaranteed to throw an exception no matter what the data integrity issue is.
	 */
	private void handleDataIntegrityIssues(final DepositAccountCommand command, DataIntegrityViolationException dve)  {
		
		Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("deposit_acc_external_id")) {
			throw new PlatformDataIntegrityException("error.msg.desposit.account.duplicate.externalId", "Deposit account with externalId " + command.getExternalId() + " already exists", "externalId", command.getExternalId());
		}
		
		logger.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException("error.msg.deposit.account.unknown.data.integrity.issue", "Unknown data integrity issue with resource.");
	}
	
	@Transactional
	@Override
	public EntityIdentifier createDepositAccount(final DepositAccountCommand command) {
		
		try {
			this.context.authenticatedUser();
			
			DepositAccountCommandValidator validator = new DepositAccountCommandValidator(command);
			validator.validateForCreate();
			
			final DepositAccount account = this.depositAccountAssembler.assembleFrom(command);
			this.depositAccountRepository.save(account);
			
			return new EntityIdentifier(account.getId());
		} catch (DataIntegrityViolationException dve) {
			 handleDataIntegrityIssues(command, dve);
			 return new EntityIdentifier(Long.valueOf(-1));
		}
	}

	@Transactional
	@Override
	public EntityIdentifier deleteDepositAccount(final Long accountId) {
		
		this.context.authenticatedUser();
		
		DepositAccount account = this.depositAccountRepository.findOne(accountId);
		if (account==null || account.isDeleted()) {
			throw new ProductNotFoundException(accountId);
		}
		
		account.delete();
		this.depositAccountRepository.save(account);
		
		return new EntityIdentifier(accountId);
	}

	@Transactional
	@Override
	public EntityIdentifier approveDepositApplication(final DepositStateTransitionApprovalCommand command) {
		
		AppUser currentUser = context.authenticatedUser();
		
		DepositStateTransitionApprovalCommandValidator validator = new DepositStateTransitionApprovalCommandValidator(command);
		validator.validate();
		
		DepositAccount account = this.depositAccountRepository.findOne(command.getAccountId());
		if (account == null || account.isDeleted()) {
			throw new DepositAccountNotFoundException(command.getAccountId());
		}
		
		// FIXME - madhukar - you are checking for loan permission here instead of some specific deposit account permission.
		LocalDate eventDate = command.getEventDate();
		if (this.isBeforeToday(eventDate) && currentUser.canNotApproveLoanInPast()) {
			throw new NoAuthorizationException("User has no authority to approve deposit with a date in the past.");
		}
		
		account.approve(eventDate, defaultDepositLifecycleStateMachine(), command, this.fixedTermDepositInterestCalculator);
		
		this.depositAccountRepository.save(account);
		
		String noteText = command.getNote();
		if (StringUtils.isNotBlank(noteText)) {
			Note note = Note.depositNote(account, noteText);
			this.noteRepository.save(note);
		}
		

		return new EntityIdentifier(account.getId());
	
	}
	
	private boolean isBeforeToday(final LocalDate date) {
		return date.isBefore(new LocalDate());
	}
	private DepositLifecycleStateMachine defaultDepositLifecycleStateMachine() {
		List<DepositAccountStatus> allowedDepositStatuses = Arrays.asList(DepositAccountStatus.values());
		return new DepositLifecycleStateMachineImpl(allowedDepositStatuses);
	}

	@Transactional
	@Override
	public EntityIdentifier rejectDepositApplication(DepositStateTransitionCommand command) {
		
		AppUser currentUser = context.authenticatedUser();
		
		DepositStateTransitionCommandValidator validator = new DepositStateTransitionCommandValidator(command);
		validator.validate();
		
		DepositAccount account = this.depositAccountRepository.findOne(command.getAccountId());
		if (account == null || account.isDeleted()) {
			throw new DepositAccountNotFoundException(command.getAccountId());
		}
		
		LocalDate eventDate = command.getEventDate();
		if (this.isBeforeToday(eventDate) && currentUser.canNotApproveLoanInPast()) {
			throw new NoAuthorizationException("User has no authority to approve deposit with a date in the past.");
		}
		
		account.reject(eventDate, defaultDepositLifecycleStateMachine());
		this.depositAccountRepository.save(account);
		
		String noteText = command.getNote();
		if (StringUtils.isNotBlank(noteText)) {
			Note note = Note.depositNote(account, noteText);
			this.noteRepository.save(note);
		}

		return new EntityIdentifier(account.getId());
	}

	@Transactional
	@Override
	public EntityIdentifier withdrawDepositApplication(DepositStateTransitionCommand command) {
		
		AppUser currentUser = context.authenticatedUser();
		
		DepositStateTransitionCommandValidator validator = new DepositStateTransitionCommandValidator(command);
		validator.validate();
		
		DepositAccount account = this.depositAccountRepository.findOne(command.getAccountId());
		if (account == null || account.isDeleted()) {
			throw new DepositAccountNotFoundException(command.getAccountId());
		}
		
		LocalDate eventDate = command.getEventDate();
		if (this.isBeforeToday(eventDate) && currentUser.canNotApproveLoanInPast()) {
			throw new NoAuthorizationException("User has no authority to approve deposit with a date in the past.");
		}
		
		account.withdrawnByApplicant(eventDate, defaultDepositLifecycleStateMachine());
		this.depositAccountRepository.save(account);
		
		String noteText = command.getNote();
		if (StringUtils.isNotBlank(noteText)) {
			Note note = Note.depositNote(account, noteText);
			this.noteRepository.save(note);
		}
		
		return new EntityIdentifier(account.getId());
	}

	@Transactional
	@Override
	public EntityIdentifier undoDepositApproval(UndoStateTransitionCommand command) {
		
		context.authenticatedUser();
		
		DepositAccount account = this.depositAccountRepository.findOne(command.getLoanId());
		if (account == null || account.isDeleted()) {
			throw new DepositAccountNotFoundException(command.getLoanId());
		}
		
		account.undoDepositApproval(defaultDepositLifecycleStateMachine());
		this.depositAccountRepository.save(account);
		
		String noteText = command.getNote();
		if (StringUtils.isNotBlank(noteText)) {
			Note note = Note.depositNote(account, noteText);
			this.noteRepository.save(note);
		}
		return new EntityIdentifier(account.getId());
	}

	/*@Transactional
	@Override
	public EntityIdentifier matureDepositApplication(DepositStateTransitionCommand command) {
		
		AppUser currentUser = context.authenticatedUser();
		
		DepositStateTransitionCommandValidator validator = new DepositStateTransitionCommandValidator(command);
		validator.validate();
		
		DepositAccount account = this.depositAccountRepository.findOne(command.getAccountId());
		if (account == null || account.isDeleted()) {
			throw new DepositAccountNotFoundException(command.getAccountId());
		}
		
		LocalDate eventDate = command.getEventDate();
		if (this.isBeforeToday(eventDate) && currentUser.canNotApproveLoanInPast()) {
			throw new NoAuthorizationException("User has no authority to mature deposit with a date in the past.");
		}
		
		account.matureDepositApplication(eventDate, defaultDepositLifecycleStateMachine());
		this.depositAccountRepository.save(account);
		
		if(account.isRenewalAllowed()){
			final DepositAccount renewedAccount = this.depositAccountAssembler.assembleFrom(account);
			this.depositAccountRepository.save(renewedAccount);
			return new EntityIdentifier(renewedAccount.getId()); //returns the new deposit application id
		}
		
		String noteText = command.getNote();
		if (StringUtils.isNotBlank(noteText)) {
			Note note = Note.depositNote(account, noteText);
			this.noteRepository.save(note);
		}
		
		return new EntityIdentifier(account.getId());
	}*/

	@Transactional
	@Override
	public EntityIdentifier withdrawDepositAccountMoney(final DepositAccountWithdrawalCommand command) {
		
		context.authenticatedUser();
		
		DepositAccountWithdrawalCommandValidator validator = new DepositAccountWithdrawalCommandValidator(command);
		validator.validate();
		
		DepositAccount account = this.depositAccountRepository.findOne(command.getAccountId());
		if (account == null || account.isDeleted()) {
			throw new DepositAccountNotFoundException(command.getAccountId());
		}
		
		if(new LocalDate().isBefore(account.maturesOnDate())){
			this.depositAccountAssembler.adjustTotalAmountForPreclosureInterest(account);
		}
		account.withdrawDepositAccountMoney(command.isRenewAccount(), defaultDepositLifecycleStateMachine());
		this.depositAccountRepository.save(account);
		
		if(command.isRenewAccount()){
			
			BigDecimal deposit = account.getTotal();
			if(command.getDeposit() != null)
				deposit = command.getDeposit();
			if(new LocalDate().isBefore(account.maturesOnDate())){
				throw new DepositAccountReopenException(account.maturesOnDate());
			}
			final DepositAccount renewedAccount = this.depositAccountAssembler.assembleFrom(account,deposit);
			this.depositAccountRepository.save(renewedAccount);
			return new EntityIdentifier(renewedAccount.getId()); //returns the new deposit application id
		}
		
		String noteText = command.getNote();
		if (StringUtils.isNotBlank(noteText)) {
			Note note = Note.depositNote(account, noteText);
			this.noteRepository.save(note);
		}
		return new EntityIdentifier(account.getId());
	}

	@Transactional
	@Override
	public EntityIdentifier withdrawDepositAccountInterestMoney(DepositAccountWithdrawInterestCommand command) {
		
		context.authenticatedUser();
		
		WithDrawDepositAccountInterestCommandValidator validator = new WithDrawDepositAccountInterestCommandValidator(command);
		validator.validate();
		
		DepositAccount account = this.depositAccountRepository.findOne(command.getAccountId());
		if (account == null || account.isDeleted()) {
			throw new DepositAccountNotFoundException(command.getAccountId());
		}
		if(account.isInterestWithdrawable()){
			/*BigDecimal interstGettingForPeriod = BigDecimal.valueOf(account.getAccuredInterest().getAmount().doubleValue()/new Double(account.getTenureInMonths()));
			LocalDate lastInterestTakenDate = getLastTxnDate(account);
			Integer noOfMonthsforInterestCal = Months.monthsBetween(lastInterestTakenDate, new LocalDate()).getMonths();
			Integer noOfPeriods = noOfMonthsforInterestCal / account.getInterestCompoundedEvery();
			BigDecimal availableInterestAmountForWithDrawal = interstGettingForPeriod.multiply(new BigDecimal(noOfPeriods));
			Integer iswithdrawable = availableInterestAmountForWithDrawal.compareTo(command.getWithdrawInterest());*/
			
			BigDecimal totalAvailableInterestForWithdrawal = getTotalWithdrawableInterestAvailable(account);
			BigDecimal interestPaid = account.getInterstPaid();
			BigDecimal remainInterestForWithdrawal = totalAvailableInterestForWithdrawal.subtract(interestPaid);
			
			if(remainInterestForWithdrawal.doubleValue() > 0){
				if(remainInterestForWithdrawal.doubleValue() > command.getWithdrawInterest().doubleValue()){
					account.withdrawInterest(Money.of(account.getDeposit().getCurrency(), command.getWithdrawInterest()));
					this.depositAccountRepository.save(account);
				}else {
					throw new RuntimeException("You can Withdraw "+remainInterestForWithdrawal+" only, \n please enter a valid amount for withdrawal");
				}
				
			}
		
			/*if(noOfPeriods > 0 ){
			if(iswithdrawable >= 0){
				account.withdrawInterest(Money.of(account.getDeposit().getCurrency(), command.getWithdrawInterest()));
				this.depositAccountRepository.save(account);
			}else if(iswithdrawable == -1){
				throw new RuntimeException();
			}
			}else if(noOfPeriods <= 0){
					throw new RuntimeException();
			}*/
		}else{
			throw new RuntimeException("You can not withdraw interst for this account");
		}
		return new EntityIdentifier(account.getId());
	}

	private LocalDate getLastTxnDate(DepositAccount account) {
		List<LocalDate> lastTransactionDates = new ArrayList<LocalDate>();
		LocalDate lastTransactionDate=null;
		List<DepositAccountTransaction> depositAccountTransactions=account.getDepositaccountTransactions();
		for(DepositAccountTransaction depositAccountTransaction : depositAccountTransactions){
			if(depositAccountTransaction.getTypeOf().equals(DepositAccountTransactionType.WITHDRAW)){
				lastTransactionDate = depositAccountTransaction.getTransactionDate();
				lastTransactionDates.add(lastTransactionDate);
			}
		}
		if(lastTransactionDates.size()>0)
			lastTransactionDate = Collections.max(lastTransactionDates);
		else
			lastTransactionDate = account.getActualCommencementDate();
		return lastTransactionDate;
	}
	
	private BigDecimal getTotalWithdrawableInterestAvailable(DepositAccount account){
		BigDecimal interstGettingForPeriod = BigDecimal.valueOf(account.getAccuredInterest().getAmount().doubleValue()/new Double(account.getTenureInMonths()));
		Integer noOfMonthsforInterestCal = Months.monthsBetween(account.getActualCommencementDate(), new LocalDate()).getMonths();
		Integer noOfPeriods = noOfMonthsforInterestCal / account.getInterestCompoundedEvery();
		return interstGettingForPeriod.multiply(new BigDecimal(noOfPeriods));
	}
}