/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.journalentry.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.accounting.closure.domain.GLClosure;
import org.mifosplatform.accounting.closure.domain.GLClosureRepository;
import org.mifosplatform.accounting.glaccount.data.GLAccountDataForLookup;
import org.mifosplatform.accounting.glaccount.domain.GLAccount;
import org.mifosplatform.accounting.glaccount.domain.GLAccountRepository;
import org.mifosplatform.accounting.glaccount.exception.GLAccountNotFoundException;
import org.mifosplatform.accounting.glaccount.service.GLAccountReadPlatformService;
import org.mifosplatform.accounting.journalentry.api.JournalEntryJsonInputParams;
import org.mifosplatform.accounting.journalentry.command.JournalEntryCommand;
import org.mifosplatform.accounting.journalentry.command.SingleDebitOrCreditEntryCommand;
import org.mifosplatform.accounting.journalentry.data.LoanDTO;
import org.mifosplatform.accounting.journalentry.data.SavingsDTO;
import org.mifosplatform.accounting.journalentry.domain.JournalEntry;
import org.mifosplatform.accounting.journalentry.domain.JournalEntryRepository;
import org.mifosplatform.accounting.journalentry.domain.JournalEntryType;
import org.mifosplatform.accounting.journalentry.exception.JournalEntriesNotFoundException;
import org.mifosplatform.accounting.journalentry.exception.JournalEntryInvalidException;
import org.mifosplatform.accounting.journalentry.exception.JournalEntryInvalidException.GL_JOURNAL_ENTRY_INVALID_REASON;
import org.mifosplatform.accounting.journalentry.serialization.JournalEntryCommandFromApiJsonDeserializer;
import org.mifosplatform.accounting.rule.domain.AccountingRule;
import org.mifosplatform.accounting.rule.domain.AccountingRuleRepository;
import org.mifosplatform.accounting.rule.exception.AccountingRuleNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.office.domain.OrganisationCurrencyRepositoryWrapper;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JournalEntryWritePlatformServiceJpaRepositoryImpl implements JournalEntryWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(JournalEntryWritePlatformServiceJpaRepositoryImpl.class);

    private final GLClosureRepository glClosureRepository;
    private final GLAccountRepository glAccountRepository;
    private final JournalEntryRepository glJournalEntryRepository;
    private final OfficeRepository officeRepository;
    private final AccountingProcessorForLoanFactory accountingProcessorForLoanFactory;
    private final AccountingProcessorForSavingsFactory accountingProcessorForSavingsFactory;
    private final AccountingProcessorHelper helper;
    private final JournalEntryCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final AccountingRuleRepository accountingRuleRepository;
    private final GLAccountReadPlatformService glAccountReadPlatformService;
    private final OrganisationCurrencyRepositoryWrapper organisationCurrencyRepository;
    private final PlatformSecurityContext context;

    @Autowired
    public JournalEntryWritePlatformServiceJpaRepositoryImpl(final GLClosureRepository glClosureRepository,
            final JournalEntryRepository glJournalEntryRepository, final OfficeRepository officeRepository,
            final GLAccountRepository glAccountRepository, final JournalEntryCommandFromApiJsonDeserializer fromApiJsonDeserializer,
            final AccountingProcessorHelper accountingProcessorHelper, final AccountingRuleRepository accountingRuleRepository,
            final AccountingProcessorForLoanFactory accountingProcessorForLoanFactory,
            final AccountingProcessorForSavingsFactory accountingProcessorForSavingsFactory,
            final GLAccountReadPlatformService glAccountReadPlatformService,
            final OrganisationCurrencyRepositoryWrapper organisationCurrencyRepository,
            final PlatformSecurityContext context) {
        this.glClosureRepository = glClosureRepository;
        this.officeRepository = officeRepository;
        this.glJournalEntryRepository = glJournalEntryRepository;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.glAccountRepository = glAccountRepository;
        this.accountingProcessorForLoanFactory = accountingProcessorForLoanFactory;
        this.accountingProcessorForSavingsFactory = accountingProcessorForSavingsFactory;
        this.helper = accountingProcessorHelper;
        this.accountingRuleRepository = accountingRuleRepository;
        this.glAccountReadPlatformService = glAccountReadPlatformService;
        this.organisationCurrencyRepository = organisationCurrencyRepository;
        this.context = context;
    }

    @Transactional
    @Override
    public CommandProcessingResult createJournalEntry(final JsonCommand command) {
        try {
            final JournalEntryCommand journalEntryCommand = this.fromApiJsonDeserializer.commandFromApiJson(command.json());
            journalEntryCommand.validateForCreate();

            // check office is valid
            final Long officeId = command.longValueOfParameterNamed(JournalEntryJsonInputParams.OFFICE_ID.getValue());
            final Office office = this.officeRepository.findOne(officeId);
            if (office == null) { throw new OfficeNotFoundException(officeId); }

            final Long accountRuleId = command.longValueOfParameterNamed(JournalEntryJsonInputParams.ACCOUNTING_RULE.getValue());
            final String currencyCode = command.stringValueOfParameterNamed(JournalEntryJsonInputParams.CURRENCY_CODE.getValue());

            validateBusinessRulesForJournalEntries(journalEntryCommand);

            /** Set a transaction Id and save these Journal entries **/
            final Date transactionDate = command.DateValueOfParameterNamed(JournalEntryJsonInputParams.TRANSACTION_DATE.getValue());
            final String transactionId = generateTransactionId(officeId);
            final String referenceNumber = command.stringValueOfParameterNamed(JournalEntryJsonInputParams.REFERENCE_NUMBER.getValue());

            if (accountRuleId != null) {

                final AccountingRule accountingRule = this.accountingRuleRepository.findOne(accountRuleId);
                if (accountingRule == null) { throw new AccountingRuleNotFoundException(accountRuleId); }

                if (accountingRule.getAccountToCredit() == null) {
                    if (journalEntryCommand.getCredits() == null) { throw new JournalEntryInvalidException(
                            GL_JOURNAL_ENTRY_INVALID_REASON.NO_DEBITS_OR_CREDITS, null, null, null); }
                    if (journalEntryCommand.getDebits() != null) {
                        checkDebitOrCreditAccountsAreValid(accountingRule, journalEntryCommand.getCredits(),
                                journalEntryCommand.getDebits());
                        checkDebitAndCreditAmounts(journalEntryCommand.getCredits(), journalEntryCommand.getDebits());
                    }

                    saveAllDebitOrCreditEntries(journalEntryCommand, office, currencyCode, transactionDate,
                            journalEntryCommand.getCredits(), transactionId, JournalEntryType.CREDIT, referenceNumber);
                } else {
                    final GLAccount creditAccountHead = accountingRule.getAccountToCredit();
                    validateGLAccountForTransaction(creditAccountHead);
                    validateDebitOrCreditArrayForExistingGLAccount(creditAccountHead, journalEntryCommand.getCredits());
                    saveAllDebitOrCreditEntries(journalEntryCommand, office, currencyCode, transactionDate,
                            journalEntryCommand.getCredits(), transactionId, JournalEntryType.CREDIT, referenceNumber);
                }

                if (accountingRule.getAccountToDebit() == null) {
                    if (journalEntryCommand.getDebits() == null) { throw new JournalEntryInvalidException(
                            GL_JOURNAL_ENTRY_INVALID_REASON.NO_DEBITS_OR_CREDITS, null, null, null); }
                    if (journalEntryCommand.getCredits() != null) {
                        checkDebitOrCreditAccountsAreValid(accountingRule, journalEntryCommand.getCredits(),
                                journalEntryCommand.getDebits());
                        checkDebitAndCreditAmounts(journalEntryCommand.getCredits(), journalEntryCommand.getDebits());
                    }

                    saveAllDebitOrCreditEntries(journalEntryCommand, office, currencyCode, transactionDate,
                            journalEntryCommand.getDebits(), transactionId, JournalEntryType.DEBIT, referenceNumber);
                } else {
                    final GLAccount debitAccountHead = accountingRule.getAccountToDebit();
                    validateGLAccountForTransaction(debitAccountHead);
                    validateDebitOrCreditArrayForExistingGLAccount(debitAccountHead, journalEntryCommand.getDebits());
                    saveAllDebitOrCreditEntries(journalEntryCommand, office, currencyCode, transactionDate,
                            journalEntryCommand.getDebits(), transactionId, JournalEntryType.DEBIT, referenceNumber);
                }
            } else {

                saveAllDebitOrCreditEntries(journalEntryCommand, office, currencyCode, transactionDate, journalEntryCommand.getDebits(),
                        transactionId, JournalEntryType.DEBIT, referenceNumber);

                saveAllDebitOrCreditEntries(journalEntryCommand, office, currencyCode, transactionDate, journalEntryCommand.getCredits(),
                        transactionId, JournalEntryType.CREDIT, referenceNumber);

            }

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withOfficeId(officeId)
                    .withTransactionId(transactionId).build();
        } catch (final DataIntegrityViolationException dve) {
            handleJournalEntryDataIntegrityIssues(dve);
            return null;
        }
    }

    private void validateDebitOrCreditArrayForExistingGLAccount(final GLAccount glaccount,
            final SingleDebitOrCreditEntryCommand[] creditOrDebits) {
        /**
         * If a glaccount is assigned for a rule the credits or debits array
         * should have only one entry and it must be same as existing account
         */
        if (creditOrDebits.length != 1) { throw new JournalEntryInvalidException(
                GL_JOURNAL_ENTRY_INVALID_REASON.INVALID_DEBIT_OR_CREDIT_ACCOUNTS, null, null, null); }
        for (final SingleDebitOrCreditEntryCommand creditOrDebit : creditOrDebits) {
            if (!glaccount.getId().equals(creditOrDebit.getGlAccountId())) { throw new JournalEntryInvalidException(
                    GL_JOURNAL_ENTRY_INVALID_REASON.INVALID_DEBIT_OR_CREDIT_ACCOUNTS, null, null, null); }
        }
    }

    @SuppressWarnings("null")
    private void checkDebitOrCreditAccountsAreValid(final AccountingRule accountingRule, final SingleDebitOrCreditEntryCommand[] credits,
            final SingleDebitOrCreditEntryCommand[] debits) {
        // Validate the debit and credit arrays are appropriate accounts
        List<GLAccountDataForLookup> allowedCreditGLAccounts = new ArrayList<GLAccountDataForLookup>();
        List<GLAccountDataForLookup> allowedDebitGLAccounts = new ArrayList<GLAccountDataForLookup>();
        final SingleDebitOrCreditEntryCommand[] validCredits = new SingleDebitOrCreditEntryCommand[credits.length];
        final SingleDebitOrCreditEntryCommand[] validDebits = new SingleDebitOrCreditEntryCommand[debits.length];

        if (credits != null && credits.length > 0) {
            allowedCreditGLAccounts = this.glAccountReadPlatformService.retrieveAccountsByTagId(accountingRule.getId(),
                    JournalEntryType.CREDIT.getValue());
            for (final GLAccountDataForLookup accountDataForLookup : allowedCreditGLAccounts) {
                for (int i = 0; i < credits.length; i++) {
                    final SingleDebitOrCreditEntryCommand credit = credits[i];
                    if (credit.getGlAccountId().equals(accountDataForLookup.getId())) {
                        validCredits[i] = credit;
                    }
                }
            }
            if (credits.length != validCredits.length) { throw new RuntimeException("Invalid credits"); }
        }

        if (debits != null && debits.length > 0) {
            allowedDebitGLAccounts = this.glAccountReadPlatformService.retrieveAccountsByTagId(accountingRule.getId(),
                    JournalEntryType.DEBIT.getValue());
            for (final GLAccountDataForLookup accountDataForLookup : allowedDebitGLAccounts) {
                for (int i = 0; i < debits.length; i++) {
                    final SingleDebitOrCreditEntryCommand debit = debits[i];
                    if (debit.getGlAccountId().equals(accountDataForLookup.getId())) {
                        validDebits[i] = debit;
                    }
                }
            }
            if (debits.length != validDebits.length) { throw new RuntimeException("Invalid debits"); }
        }
    }

    private void checkDebitAndCreditAmounts(final SingleDebitOrCreditEntryCommand[] credits, final SingleDebitOrCreditEntryCommand[] debits) {
        // sum of all debits must be = sum of all credits
        BigDecimal creditsSum = BigDecimal.ZERO;
        BigDecimal debitsSum = BigDecimal.ZERO;
        for (final SingleDebitOrCreditEntryCommand creditEntryCommand : credits) {
            if (creditEntryCommand.getAmount() == null || creditEntryCommand.getGlAccountId() == null) { throw new JournalEntryInvalidException(
                    GL_JOURNAL_ENTRY_INVALID_REASON.DEBIT_CREDIT_ACCOUNT_OR_AMOUNT_EMPTY, null, null, null); }
            creditsSum = creditsSum.add(creditEntryCommand.getAmount());
        }
        for (final SingleDebitOrCreditEntryCommand debitEntryCommand : debits) {
            if (debitEntryCommand.getAmount() == null || debitEntryCommand.getGlAccountId() == null) { throw new JournalEntryInvalidException(
                    GL_JOURNAL_ENTRY_INVALID_REASON.DEBIT_CREDIT_ACCOUNT_OR_AMOUNT_EMPTY, null, null, null); }
            debitsSum = debitsSum.add(debitEntryCommand.getAmount());
        }
        if (creditsSum.compareTo(debitsSum) != 0) { throw new JournalEntryInvalidException(
                GL_JOURNAL_ENTRY_INVALID_REASON.DEBIT_CREDIT_SUM_MISMATCH, null, null, null); }
    }

    private void validateGLAccountForTransaction(final GLAccount creditOrDebitAccountHead) {
        /***
         * validate that the account allows manual adjustments and is not
         * disabled
         **/
        if (creditOrDebitAccountHead.isDisabled()) {
            throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.GL_ACCOUNT_DISABLED, null,
                    creditOrDebitAccountHead.getName(), creditOrDebitAccountHead.getGlCode());
        } else if (!creditOrDebitAccountHead.isManualEntriesAllowed()) { throw new JournalEntryInvalidException(
                GL_JOURNAL_ENTRY_INVALID_REASON.GL_ACCOUNT_MANUAL_ENTRIES_NOT_PERMITTED, null, creditOrDebitAccountHead.getName(),
                creditOrDebitAccountHead.getGlCode()); }
    }

    @Transactional
    @Override
    public CommandProcessingResult revertJournalEntry(final JsonCommand command) {
        // is the transaction Id valid
        final List<JournalEntry> journalEntries = this.glJournalEntryRepository.findUnReversedManualJournalEntriesByTransactionId(command
                .getTransactionId());

        if (journalEntries.size() <= 1) { throw new JournalEntriesNotFoundException(command.getTransactionId()); }
        Long officeId = journalEntries.get(0).getOffice().getId();
        final String reversalTransactionId = generateTransactionId(officeId);
        final boolean manualEntry = true;
        
        for (final JournalEntry journalEntry : journalEntries) {
            JournalEntry reversalJournalEntry;
            final String reversalComment = "Reversal entry for Journal Entry with Entry Id  :" + journalEntry.getId()
                    + " and transaction Id " + command.getTransactionId();
            if (journalEntry.isDebitEntry()) {
                reversalJournalEntry = JournalEntry.createNew(journalEntry.getOffice(), journalEntry.getGlAccount(),
                        journalEntry.getCurrencyCode(), reversalTransactionId, manualEntry, journalEntry.getTransactionDate(),
                        JournalEntryType.CREDIT, journalEntry.getAmount(), reversalComment, null, null, journalEntry.getReferenceNumber());
            } else {
                reversalJournalEntry = JournalEntry.createNew(journalEntry.getOffice(), journalEntry.getGlAccount(),
                        journalEntry.getCurrencyCode(), reversalTransactionId, manualEntry, journalEntry.getTransactionDate(),
                        JournalEntryType.DEBIT, journalEntry.getAmount(), reversalComment, null, null, journalEntry.getReferenceNumber());
            }
            // save the reversal entry
            this.glJournalEntryRepository.saveAndFlush(reversalJournalEntry);
            journalEntry.setReversed(true);
            journalEntry.setReversalJournalEntry(reversalJournalEntry);
            // save the updated journal entry
            this.glJournalEntryRepository.saveAndFlush(journalEntry);
        }
        return new CommandProcessingResultBuilder().withTransactionId(reversalTransactionId).build();
    }

    @Transactional
    @Override
    public void createJournalEntriesForLoan(final Map<String, Object> accountingBridgeData) {

        final boolean cashBasedAccountingEnabled = (Boolean) accountingBridgeData.get("cashBasedAccountingEnabled");
        final boolean accrualBasedAccountingEnabled = (Boolean) accountingBridgeData.get("accrualBasedAccountingEnabled");

        if (cashBasedAccountingEnabled || accrualBasedAccountingEnabled) {
            final LoanDTO loanDTO = this.helper.populateLoanDtoFromMap(accountingBridgeData, cashBasedAccountingEnabled,
                    accrualBasedAccountingEnabled);
            final AccountingProcessorForLoan accountingProcessorForLoan = this.accountingProcessorForLoanFactory
                    .determineProcessor(loanDTO);
            accountingProcessorForLoan.createJournalEntriesForLoan(loanDTO);
        }
    }

    @Transactional
    @Override
    public void createJournalEntriesForSavings(final Map<String, Object> accountingBridgeData) {

        final boolean cashBasedAccountingEnabled = (Boolean) accountingBridgeData.get("cashBasedAccountingEnabled");
        final boolean accrualBasedAccountingEnabled = (Boolean) accountingBridgeData.get("accrualBasedAccountingEnabled");

        if (cashBasedAccountingEnabled || accrualBasedAccountingEnabled) {
            final SavingsDTO savingsDTO = this.helper.populateSavingsDtoFromMap(accountingBridgeData, cashBasedAccountingEnabled,
                    accrualBasedAccountingEnabled);
            final AccountingProcessorForSavings accountingProcessorForSavings = this.accountingProcessorForSavingsFactory
                    .determineProcessor(savingsDTO);
            accountingProcessorForSavings.createJournalEntriesForSavings(savingsDTO);
        }

    }

    private void validateBusinessRulesForJournalEntries(final JournalEntryCommand command) {
        /** check if date of Journal entry is valid ***/
        final LocalDate entryLocalDate = command.getTransactionDate();
        final Date transactionDate = entryLocalDate.toDateMidnight().toDate();
        // shouldn't be in the future
        final Date todaysDate = new Date();
        if (transactionDate.after(todaysDate)) { throw new JournalEntryInvalidException(GL_JOURNAL_ENTRY_INVALID_REASON.FUTURE_DATE,
                transactionDate, null, null); }
        // shouldn't be before an accounting closure
        final GLClosure latestGLClosure = this.glClosureRepository.getLatestGLClosureByBranch(command.getOfficeId());
        if (latestGLClosure != null) {
            if (latestGLClosure.getClosingDate().after(transactionDate) || latestGLClosure.getClosingDate().equals(transactionDate)) { throw new JournalEntryInvalidException(
                    GL_JOURNAL_ENTRY_INVALID_REASON.ACCOUNTING_CLOSED, latestGLClosure.getClosingDate(), null, null); }
        }

        /*** check if credits and debits are valid **/
        final SingleDebitOrCreditEntryCommand[] credits = command.getCredits();
        final SingleDebitOrCreditEntryCommand[] debits = command.getDebits();

        // atleast one debit or credit must be present
        if ((credits == null || credits.length <= 0) || (debits == null || debits.length <= 0)) { throw new JournalEntryInvalidException(
                GL_JOURNAL_ENTRY_INVALID_REASON.NO_DEBITS_OR_CREDITS, null, null, null); }

        checkDebitAndCreditAmounts(credits, debits);
    }

    private void saveAllDebitOrCreditEntries(final JournalEntryCommand command, final Office office, final String currencyCode,
            final Date transactionDate, final SingleDebitOrCreditEntryCommand[] singleDebitOrCreditEntryCommands,
            final String transactionId, final JournalEntryType type, final String referenceNumber) {
        final boolean manualEntry = true;
        for (final SingleDebitOrCreditEntryCommand singleDebitOrCreditEntryCommand : singleDebitOrCreditEntryCommands) {
            final GLAccount glAccount = this.glAccountRepository.findOne(singleDebitOrCreditEntryCommand.getGlAccountId());
            if (glAccount == null) { throw new GLAccountNotFoundException(singleDebitOrCreditEntryCommand.getGlAccountId()); }

            validateGLAccountForTransaction(glAccount);

            String comments = command.getComments();
            if (!StringUtils.isBlank(singleDebitOrCreditEntryCommand.getComments())) {
                comments = singleDebitOrCreditEntryCommand.getComments();
            }

            /** Validate current code is appropriate **/
            this.organisationCurrencyRepository.findOneWithNotFoundDetection(currencyCode);

            final JournalEntry glJournalEntry = JournalEntry.createNew(office, glAccount, currencyCode, transactionId, manualEntry,
                    transactionDate, type, singleDebitOrCreditEntryCommand.getAmount(), comments, null, null, referenceNumber);
            this.glJournalEntryRepository.saveAndFlush(glJournalEntry);
        }
    }

    /**
     * TODO: Need a better implementation with guaranteed uniqueness (but not a
     * long UUID)...maybe something tied to system clock..
     */
    private String generateTransactionId(Long officeId) {
        AppUser user = this.context.authenticatedUser();
        Long time = System.currentTimeMillis();
        String uniqueVal = String.valueOf(time) + user.getId() + officeId;
        String transactionId = Long.toHexString(Long.parseLong(uniqueVal)); 
        return transactionId;
    }

    private void handleJournalEntryDataIntegrityIssues(final DataIntegrityViolationException dve) {
        final Throwable realCause = dve.getMostSpecificCause();
        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.glJournalEntry.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource Journal Entry: " + realCause.getMessage());
    }

}
