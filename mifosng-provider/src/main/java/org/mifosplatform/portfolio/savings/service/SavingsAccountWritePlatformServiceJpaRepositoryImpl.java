/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifosplatform.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrency;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.portfolio.client.domain.AccountNumberGenerator;
import org.mifosplatform.portfolio.client.domain.AccountNumberGeneratorFactory;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepositoryWrapper;
import org.mifosplatform.portfolio.group.domain.Group;
import org.mifosplatform.portfolio.group.domain.GroupRepository;
import org.mifosplatform.portfolio.group.exception.GroupNotFoundException;
import org.mifosplatform.portfolio.paymentdetail.domain.PaymentDetail;
import org.mifosplatform.portfolio.paymentdetail.service.PaymentDetailWritePlatformService;
import org.mifosplatform.portfolio.savings.api.SavingsApiConstants;
import org.mifosplatform.portfolio.savings.data.SavingsAccountDataValidator;
import org.mifosplatform.portfolio.savings.data.SavingsAccountTransactionDataValidator;
import org.mifosplatform.portfolio.savings.domain.SavingsAccount;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountAssembler;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountRepositoryWrapper;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountTransaction;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountTransactionRepository;
import org.mifosplatform.portfolio.savings.domain.SavingsProduct;
import org.mifosplatform.portfolio.savings.domain.SavingsProductRepository;
import org.mifosplatform.portfolio.savings.exception.SavingsProductNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SavingsAccountWritePlatformServiceJpaRepositoryImpl implements SavingsAccountWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(SavingsAccountWritePlatformServiceJpaRepositoryImpl.class);

    private final PlatformSecurityContext context;
    private final SavingsAccountRepositoryWrapper savingAccountRepository;
    private final SavingsAccountTransactionRepository savingsAccountTransactionRepository;
    private final SavingsAccountAssembler savingAccountAssembler;
    private final SavingsAccountDataValidator savingsAccountDataValidator;
    private final SavingsAccountTransactionDataValidator savingsAccountTransactionDataValidator;
    private final AccountNumberGeneratorFactory accountIdentifierGeneratorFactory;
    private final ClientRepositoryWrapper clientRepository;
    private final GroupRepository groupRepository;
    private final SavingsProductRepository savingsProductRepository;
    private final PaymentDetailWritePlatformService paymentDetailWritePlatformService;
    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService;

    @Autowired
    public SavingsAccountWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final SavingsAccountRepositoryWrapper savingAccountRepository,
            final SavingsAccountTransactionRepository savingsAccountTransactionRepository,
            final SavingsAccountAssembler savingAccountAssembler, final SavingsAccountDataValidator savingsAccountDataValidator,
            final SavingsAccountTransactionDataValidator savingsAccountTransactionDataValidator,
            final AccountNumberGeneratorFactory accountIdentifierGeneratorFactory, final ClientRepositoryWrapper clientRepository,
            final GroupRepository groupRepository, final SavingsProductRepository savingsProductRepository,
            final PaymentDetailWritePlatformService paymentDetailWritePlatformService,
            final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper,
            final JournalEntryWritePlatformService journalEntryWritePlatformService) {
        this.context = context;
        this.savingAccountRepository = savingAccountRepository;
        this.savingsAccountTransactionRepository = savingsAccountTransactionRepository;
        this.savingAccountAssembler = savingAccountAssembler;
        this.savingsAccountTransactionDataValidator = savingsAccountTransactionDataValidator;
        this.accountIdentifierGeneratorFactory = accountIdentifierGeneratorFactory;
        this.savingsAccountDataValidator = savingsAccountDataValidator;
        this.clientRepository = clientRepository;
        this.groupRepository = groupRepository;
        this.savingsProductRepository = savingsProductRepository;
        this.paymentDetailWritePlatformService = paymentDetailWritePlatformService;
        this.applicationCurrencyRepositoryWrapper = applicationCurrencyRepositoryWrapper;
        this.journalEntryWritePlatformService = journalEntryWritePlatformService;
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue
     * is.
     */
    private void handleDataIntegrityIssues(final JsonCommand command, final DataAccessException dve) {

        final StringBuilder errorCodeBuilder = new StringBuilder("error.msg.").append(SavingsApiConstants.SAVINGS_ACCOUNT_RESOURCE_NAME);

        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("sa_account_no_UNIQUE")) {
            final String accountNo = command.stringValueOfParameterNamed("accountNo");
            errorCodeBuilder.append(".duplicate.accountNo");
            throw new PlatformDataIntegrityException(errorCodeBuilder.toString(), "Savings account with accountNo " + accountNo
                    + " already exists", "accountNo", accountNo);

        } else if (realCause.getMessage().contains("sa_external_id_UNIQUE")) {

            final String externalId = command.stringValueOfParameterNamed("externalId");
            errorCodeBuilder.append(".duplicate.externalId");
            throw new PlatformDataIntegrityException(errorCodeBuilder.toString(), "Savings account with externalId " + externalId
                    + " already exists", "externalId", externalId);
        }

        errorCodeBuilder.append(".unknown.data.integrity.issue");
        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException(errorCodeBuilder.toString(), "Unknown data integrity issue with savings account.");
    }

    @Transactional
    @Override
    public CommandProcessingResult createSavingAccount(final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.savingsAccountDataValidator.validateForCreate(command.json());

            final List<Long> existingTransactionIds = new ArrayList<Long>();
            final List<Long> existingReversedTransactionIds = new ArrayList<Long>();
            final SavingsAccount account = this.savingAccountAssembler.assembleFrom(command, existingTransactionIds,
                    existingReversedTransactionIds);
            this.savingAccountRepository.save(account);

            if (account.isAccountNumberRequiresAutoGeneration()) {
                final AccountNumberGenerator accountNoGenerator = this.accountIdentifierGeneratorFactory
                        .determineLoanAccountNoGenerator(account.getId());
                account.updateAccountNo(accountNoGenerator.generate());

                this.savingAccountRepository.save(account);
            }

            postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds);

            final Long savingsId = account.getId();
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(savingsId) //
                    .withOfficeId(account.officeId()) //
                    .withClientId(account.clientId()) //
                    .withGroupId(account.groupId()) //
                    .withSavingsId(savingsId) //
                    .build();
        } catch (DataAccessException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateSavingAccount(final Long savingsId, final JsonCommand command) {
        try {
            this.context.authenticatedUser();
            this.savingsAccountDataValidator.validateForUpdate(command.json());

            final Map<String, Object> changes = new LinkedHashMap<String, Object>(20);

            final SavingsAccount account = this.savingAccountRepository.findOneWithNotFoundDetection(savingsId);

            account.update(command, changes);

            if (!changes.isEmpty()) {

                if (changes.containsKey(SavingsApiConstants.clientIdParamName)) {
                    final Long clientId = command.longValueOfParameterNamed(SavingsApiConstants.clientIdParamName);
                    if (clientId != null) {
                        final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
                        account.update(client);
                    } else {
                        final Client client = null;
                        account.update(client);
                    }
                }

                if (changes.containsKey(SavingsApiConstants.groupIdParamName)) {
                    final Long groupId = command.longValueOfParameterNamed(SavingsApiConstants.groupIdParamName);
                    if (groupId != null) {
                        final Group group = this.groupRepository.findOne(groupId);
                        if (group == null) { throw new GroupNotFoundException(groupId); }
                        account.update(group);
                    } else {
                        final Group group = null;
                        account.update(group);
                    }
                }

                if (changes.containsKey(SavingsApiConstants.productIdParamName)) {
                    final Long productId = command.longValueOfParameterNamed(SavingsApiConstants.productIdParamName);
                    final SavingsProduct product = this.savingsProductRepository.findOne(productId);
                    if (product == null) { throw new SavingsProductNotFoundException(productId); }

                    account.update(product);
                }

                this.savingAccountRepository.saveAndFlush(account);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(savingsId) //
                    .withOfficeId(account.officeId()) //
                    .withClientId(account.clientId()) //
                    .withGroupId(account.groupId()) //
                    .withSavingsId(savingsId) //
                    .with(changes) //
                    .build();
        } catch (DataAccessException dve) {
            handleDataIntegrityIssues(command, dve);
            return new CommandProcessingResult(Long.valueOf(-1));
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteSavingAccount(final Long savingsId) {

        this.context.authenticatedUser();

        final SavingsAccount account = this.savingAccountRepository.findOneWithNotFoundDetection(savingsId);

        this.savingAccountRepository.delete(account);

        return new CommandProcessingResultBuilder() //
                .withEntityId(savingsId) //
                .withOfficeId(account.officeId()) //
                .withClientId(account.clientId()) //
                .withGroupId(account.groupId()) //
                .withSavingsId(savingsId) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult activate(final Long savingsId, final JsonCommand command) {

        this.context.authenticatedUser();

        this.savingsAccountTransactionDataValidator.validateActivation(command);

        final SavingsAccount account = this.savingAccountRepository.findOneWithNotFoundDetection(savingsId);

        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
        final LocalDate activationDate = command.localDateValueOfParameterNamed("activationDate");
        final List<Long> existingTransactionIds = new ArrayList<Long>();
        final List<Long> existingReversedTransactionIds = new ArrayList<Long>();

        account.activate(fmt, activationDate, existingReversedTransactionIds, existingReversedTransactionIds);

        this.savingAccountRepository.save(account);

        postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds);

        return new CommandProcessingResultBuilder() //
                .withEntityId(savingsId) //
                .withOfficeId(account.officeId()) //
                .withClientId(account.clientId()) //
                .withGroupId(account.groupId()) //
                .withSavingsId(savingsId) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult deposit(final Long savingsId, final JsonCommand command) {

        this.context.authenticatedUser();

        this.savingsAccountTransactionDataValidator.validate(command);

        final SavingsAccount account = this.savingAccountRepository.findOneWithNotFoundDetection(savingsId);

        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);

        final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed("transactionAmount");

        final List<Long> existingTransactionIds = new ArrayList<Long>();
        final List<Long> existingReversedTransactionIds = new ArrayList<Long>();
        final Map<String, Object> changes = new LinkedHashMap<String, Object>();
        PaymentDetail paymentDetail = paymentDetailWritePlatformService.createAndPersistPaymentDetail(command, changes);

        final SavingsAccountTransaction deposit = account.deposit(fmt, transactionDate, transactionAmount, existingTransactionIds,
                existingReversedTransactionIds, paymentDetail);
        final Long transactionId = saveTransactionToGenerateTransactionId(deposit);

        this.savingAccountRepository.save(account);

        postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds);

        return new CommandProcessingResultBuilder() //
                .withEntityId(transactionId) //
                .withOfficeId(account.officeId()) //
                .withClientId(account.clientId()) //
                .withGroupId(account.groupId()) //
                .withSavingsId(savingsId) //
                .with(changes)//
                .build();
    }

    private Long saveTransactionToGenerateTransactionId(final SavingsAccountTransaction transaction) {
        this.savingsAccountTransactionRepository.save(transaction);
        return transaction.getId();
    }

    @Transactional
    @Override
    public CommandProcessingResult withdrawal(final Long savingsId, final JsonCommand command) {

        this.context.authenticatedUser();

        this.savingsAccountTransactionDataValidator.validate(command);

        final SavingsAccount account = this.savingAccountRepository.findOneWithNotFoundDetection(savingsId);

        final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed("transactionAmount");

        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);

        final List<Long> existingTransactionIds = new ArrayList<Long>();
        final List<Long> existingReversedTransactionIds = new ArrayList<Long>();
        final Map<String, Object> changes = new LinkedHashMap<String, Object>();
        PaymentDetail paymentDetail = paymentDetailWritePlatformService.createAndPersistPaymentDetail(command, changes);

        final SavingsAccountTransaction withdrawal = account.withdraw(fmt, transactionDate, transactionAmount, existingTransactionIds,
                existingReversedTransactionIds, paymentDetail);
        final Long transactionId = saveTransactionToGenerateTransactionId(withdrawal);
        this.savingAccountRepository.save(account);

        postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds);

        return new CommandProcessingResultBuilder() //
                .withEntityId(transactionId) //
                .withOfficeId(account.officeId()) //
                .withClientId(account.clientId()) //
                .withGroupId(account.groupId()) //
                .withSavingsId(savingsId) //
                .with(changes)//
                .build();
    }

    @Override
    public CommandProcessingResult calculateInterest(final Long savingsId, final JsonCommand command) {
        this.context.authenticatedUser();

        final SavingsAccount account = this.savingAccountRepository.findOneWithNotFoundDetection(savingsId);

        final LocalDate today = DateUtils.getLocalDateOfTenant();
        account.calculateInterest(today);
        this.savingAccountRepository.save(account);

        return new CommandProcessingResultBuilder() //
                .withEntityId(savingsId) //
                .withOfficeId(account.officeId()) //
                .withClientId(account.clientId()) //
                .withGroupId(account.groupId()) //
                .withSavingsId(savingsId) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult postInterest(final Long savingsId, final JsonCommand command) {
        this.context.authenticatedUser();
        final List<Long> existingTransactionIds = new ArrayList<Long>();
        final List<Long> existingReversedTransactionIds = new ArrayList<Long>();

        final SavingsAccount account = this.savingAccountRepository.findOneWithNotFoundDetection(savingsId);

        final LocalDate today = DateUtils.getLocalDateOfTenant();
        account.postInterest(today, existingTransactionIds, existingReversedTransactionIds);
        this.savingAccountRepository.save(account);

        postJournalEntries(account, existingTransactionIds, existingReversedTransactionIds);

        return new CommandProcessingResultBuilder() //
                .withEntityId(savingsId) //
                .withOfficeId(account.officeId()) //
                .withClientId(account.clientId()) //
                .withGroupId(account.groupId()) //
                .withSavingsId(savingsId) //
                .build();
    }

    private void postJournalEntries(final SavingsAccount savingsAccount, final List<Long> existingTransactionIds,
            final List<Long> existingReversedTransactionIds) {

        final MonetaryCurrency currency = savingsAccount.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepositoryWrapper.findOneWithNotFoundDetection(currency);

        final Map<String, Object> accountingBridgeData = savingsAccount.deriveAccountingBridgeData(applicationCurrency.toData(),
                existingTransactionIds, existingReversedTransactionIds);
        journalEntryWritePlatformService.createJournalEntriesForSavings(accountingBridgeData);
    }
}