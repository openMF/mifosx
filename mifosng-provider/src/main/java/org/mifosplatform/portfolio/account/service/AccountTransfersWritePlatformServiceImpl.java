/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.account.service;

import static org.mifosplatform.portfolio.account.AccountDetailConstants.fromAccountIdParamName;
import static org.mifosplatform.portfolio.account.AccountDetailConstants.fromAccountTypeParamName;
import static org.mifosplatform.portfolio.account.AccountDetailConstants.toAccountIdParamName;
import static org.mifosplatform.portfolio.account.AccountDetailConstants.toAccountTypeParamName;
import static org.mifosplatform.portfolio.account.api.AccountTransfersApiConstants.transferAmountParamName;
import static org.mifosplatform.portfolio.account.api.AccountTransfersApiConstants.transferDateParamName;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.portfolio.account.PortfolioAccountType;
import org.mifosplatform.portfolio.account.data.AccountTransferDTO;
import org.mifosplatform.portfolio.account.data.AccountTransfersDataValidator;
import org.mifosplatform.portfolio.account.domain.AccountTransferAssembler;
import org.mifosplatform.portfolio.account.domain.AccountTransferDetailRepository;
import org.mifosplatform.portfolio.account.domain.AccountTransferDetails;
import org.mifosplatform.portfolio.account.domain.AccountTransferRepository;
import org.mifosplatform.portfolio.account.domain.AccountTransferTransaction;
import org.mifosplatform.portfolio.account.domain.AccountTransferType;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.loanaccount.domain.LoanAccountDomainService;
import org.mifosplatform.portfolio.loanaccount.domain.LoanTransaction;
import org.mifosplatform.portfolio.loanaccount.domain.LoanTransactionType;
import org.mifosplatform.portfolio.loanaccount.exception.InvalidPaidInAdvanceAmountException;
import org.mifosplatform.portfolio.loanaccount.service.LoanAssembler;
import org.mifosplatform.portfolio.loanaccount.service.LoanReadPlatformService;
import org.mifosplatform.portfolio.paymentdetail.domain.PaymentDetail;
import org.mifosplatform.portfolio.savings.SavingsTransactionBooleanValues;
import org.mifosplatform.portfolio.savings.domain.SavingsAccount;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountAssembler;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountDomainService;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountTransaction;
import org.mifosplatform.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountTransfersWritePlatformServiceImpl implements AccountTransfersWritePlatformService {

    private final AccountTransfersDataValidator accountTransfersDataValidator;
    private final AccountTransferAssembler accountTransferAssembler;
    private final AccountTransferRepository accountTransferRepository;
    private final SavingsAccountAssembler savingsAccountAssembler;
    private final SavingsAccountDomainService savingsAccountDomainService;
    private final LoanAssembler loanAccountAssembler;
    private final LoanAccountDomainService loanAccountDomainService;
    private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;
    private final AccountTransferDetailRepository accountTransferDetailRepository;
    private final LoanReadPlatformService loanReadPlatformService;

    @Autowired
    public AccountTransfersWritePlatformServiceImpl(final AccountTransfersDataValidator accountTransfersDataValidator,
            final AccountTransferAssembler accountTransferAssembler, final AccountTransferRepository accountTransferRepository,
            final SavingsAccountAssembler savingsAccountAssembler, final SavingsAccountDomainService savingsAccountDomainService,
            final LoanAssembler loanAssembler, final LoanAccountDomainService loanAccountDomainService,
            final SavingsAccountWritePlatformService savingsAccountWritePlatformService,
            final AccountTransferDetailRepository accountTransferDetailRepository,
            final LoanReadPlatformService loanReadPlatformService) {
        this.accountTransfersDataValidator = accountTransfersDataValidator;
        this.accountTransferAssembler = accountTransferAssembler;
        this.accountTransferRepository = accountTransferRepository;
        this.savingsAccountAssembler = savingsAccountAssembler;
        this.savingsAccountDomainService = savingsAccountDomainService;
        this.loanAccountAssembler = loanAssembler;
        this.loanAccountDomainService = loanAccountDomainService;
        this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
        this.accountTransferDetailRepository = accountTransferDetailRepository;
        this.loanReadPlatformService = loanReadPlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult create(final JsonCommand command) {
        boolean isRegularTransaction = true;

        this.accountTransfersDataValidator.validate(command);

        final LocalDate transactionDate = command.localDateValueOfParameterNamed(transferDateParamName);
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed(transferAmountParamName);

        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);

        final Integer fromAccountTypeId = command.integerValueSansLocaleOfParameterNamed(fromAccountTypeParamName);
        final PortfolioAccountType fromAccountType = PortfolioAccountType.fromInt(fromAccountTypeId);

        final Integer toAccountTypeId = command.integerValueSansLocaleOfParameterNamed(toAccountTypeParamName);
        final PortfolioAccountType toAccountType = PortfolioAccountType.fromInt(toAccountTypeId);

        final PaymentDetail paymentDetail = null;
        Long fromSavingsAccountId = null;
        Long transferDetailId = null;
        boolean isInterestTransfer = false;
        boolean isAccountTransfer = true;
        Long fromLoanAccountId = null;
        boolean isWithdrawBalance = false;

        if (isSavingsToSavingsAccountTransfer(fromAccountType, toAccountType)) {

            fromSavingsAccountId = command.longValueOfParameterNamed(fromAccountIdParamName);
            final SavingsAccount fromSavingsAccount = this.savingsAccountAssembler.assembleFrom(fromSavingsAccountId);

            final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                    isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(), isInterestTransfer, isWithdrawBalance);
            final SavingsAccountTransaction withdrawal = this.savingsAccountDomainService.handleWithdrawal(fromSavingsAccount, fmt,
                    transactionDate, transactionAmount, paymentDetail, transactionBooleanValues);

            final Long toSavingsId = command.longValueOfParameterNamed(toAccountIdParamName);
            final SavingsAccount toSavingsAccount = this.savingsAccountAssembler.assembleFrom(toSavingsId);

            final SavingsAccountTransaction deposit = this.savingsAccountDomainService.handleDeposit(toSavingsAccount, fmt,
                    transactionDate, transactionAmount, paymentDetail, isAccountTransfer, isRegularTransaction);

            final AccountTransferDetails accountTransferDetails = this.accountTransferAssembler.assembleSavingsToSavingsTransfer(command,
                    fromSavingsAccount, toSavingsAccount, withdrawal, deposit);
            this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            transferDetailId = accountTransferDetails.getId();

        } else if (isSavingsToLoanAccountTransfer(fromAccountType, toAccountType)) {
            //
            fromSavingsAccountId = command.longValueOfParameterNamed(fromAccountIdParamName);
            final SavingsAccount fromSavingsAccount = this.savingsAccountAssembler.assembleFrom(fromSavingsAccountId);

            final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                    isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(), isInterestTransfer, isWithdrawBalance);
            final SavingsAccountTransaction withdrawal = this.savingsAccountDomainService.handleWithdrawal(fromSavingsAccount, fmt,
                    transactionDate, transactionAmount, paymentDetail, transactionBooleanValues);

            final Long toLoanAccountId = command.longValueOfParameterNamed(toAccountIdParamName);
            final Loan toLoanAccount = this.loanAccountAssembler.assembleFrom(toLoanAccountId);

            final boolean isRecoveryRepayment = false;
            final LoanTransaction loanRepaymentTransaction = this.loanAccountDomainService.makeRepayment(toLoanAccount,
                    new CommandProcessingResultBuilder(), transactionDate, transactionAmount, paymentDetail, null, null,
                    isRecoveryRepayment, isAccountTransfer);

            final AccountTransferDetails accountTransferDetails = this.accountTransferAssembler.assembleSavingsToLoanTransfer(command,
                    fromSavingsAccount, toLoanAccount, withdrawal, loanRepaymentTransaction);
            this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            transferDetailId = accountTransferDetails.getId();

        } else if (isLoanToSavingsAccountTransfer(fromAccountType, toAccountType)) {
            // FIXME - kw - ADD overpaid loan to savings account transfer
            // support.

            fromLoanAccountId = command.longValueOfParameterNamed(fromAccountIdParamName);
            final Loan fromLoanAccount = this.loanAccountAssembler.assembleFrom(fromLoanAccountId);

            final LoanTransaction loanRefundTransaction = this.loanAccountDomainService.makeRefund(fromLoanAccountId,
                    new CommandProcessingResultBuilder(), transactionDate, transactionAmount, paymentDetail, null, null);

            final Long toSavingsAccountId = command.longValueOfParameterNamed(toAccountIdParamName);
            final SavingsAccount toSavingsAccount = this.savingsAccountAssembler.assembleFrom(toSavingsAccountId);

            final SavingsAccountTransaction deposit = this.savingsAccountDomainService.handleDeposit(toSavingsAccount, fmt,
                    transactionDate, transactionAmount, paymentDetail, isAccountTransfer, isRegularTransaction);

            final AccountTransferDetails accountTransferDetails = this.accountTransferAssembler.assembleLoanToSavingsTransfer(command,
                    fromLoanAccount, toSavingsAccount, deposit, loanRefundTransaction);
            this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            transferDetailId = accountTransferDetails.getId();

        } else {

        }

        final CommandProcessingResultBuilder builder = new CommandProcessingResultBuilder().withEntityId(transferDetailId);

        if (fromAccountType.isSavingsAccount()) {
            builder.withSavingsId(fromSavingsAccountId);
        }
        if (fromAccountType.isLoanAccount()) {
            builder.withLoanId(fromLoanAccountId);
        }

        return builder.build();
    }

    @Override
    @Transactional
    public void reverseTransfersWithFromAccountType(final Long accountNumber, final PortfolioAccountType accountTypeId) {
        List<AccountTransferTransaction> acccountTransfers = null;
        if (accountTypeId.isLoanAccount()) {
            acccountTransfers = this.accountTransferRepository.findByFromLoanId(accountNumber);
        }
        if (acccountTransfers != null && acccountTransfers.size() > 0) {
            undoTransactions(acccountTransfers);
        }

    }
    
    @Override
    @Transactional
    public void reverseTransfersWithFromAccountTransactions(final Collection<Long> fromTransactionIds, final PortfolioAccountType accountTypeId) {
        List<AccountTransferTransaction> acccountTransfers = null;
        if (accountTypeId.isLoanAccount()) {
            acccountTransfers = this.accountTransferRepository.findByFromLoanTransactions(fromTransactionIds);
        }
        if (acccountTransfers != null && acccountTransfers.size() > 0) {
            undoTransactions(acccountTransfers);
        }

    }

    @Override
    @Transactional
    public void reverseAllTransactions(final Long accountId, final PortfolioAccountType accountTypeId) {
        List<AccountTransferTransaction> acccountTransfers = null;
        if (accountTypeId.isLoanAccount()) {
            acccountTransfers = this.accountTransferRepository.findAllByLoanId(accountId);
        }
        if (acccountTransfers != null && acccountTransfers.size() > 0) {
            undoTransactions(acccountTransfers);
        }
    }

    /**
     * @param acccountTransfers
     */
    private void undoTransactions(final List<AccountTransferTransaction> acccountTransfers) {
        for (final AccountTransferTransaction accountTransfer : acccountTransfers) {
            if (accountTransfer.getFromLoanTransaction() != null) {
                this.loanAccountDomainService.reverseTransfer(accountTransfer.getFromLoanTransaction());
            } else if (accountTransfer.getToLoanTransaction() != null) {
                this.loanAccountDomainService.reverseTransfer(accountTransfer.getToLoanTransaction());
            }
            if (accountTransfer.getFromTransaction() != null) {
                this.savingsAccountWritePlatformService.undoTransaction(accountTransfer.accountTransferDetails().fromSavingsAccount()
                        .getId(), accountTransfer.getFromTransaction().getId(), true);
            }
            if (accountTransfer.getToSavingsTransaction() != null) {
                this.savingsAccountWritePlatformService.undoTransaction(
                        accountTransfer.accountTransferDetails().toSavingsAccount().getId(), accountTransfer.getToSavingsTransaction()
                                .getId(), true);
            }
            accountTransfer.reverse();
            this.accountTransferRepository.save(accountTransfer);
        }
    }

    @Override
    @Transactional
    public Long transferFunds(final AccountTransferDTO accountTransferDTO) {
        Long transferTransactionId = null;
        final boolean isAccountTransfer = true;
        final boolean isRegularTransaction = accountTransferDTO.isRegularTransaction();
        AccountTransferDetails accountTransferDetails = accountTransferDTO.getAccountTransferDetails();
        if (isSavingsToLoanAccountTransfer(accountTransferDTO.getFromAccountType(), accountTransferDTO.getToAccountType())) {
            //
            SavingsAccount fromSavingsAccount = null;
            Loan toLoanAccount = null;
            if (accountTransferDetails == null) {
                if (accountTransferDTO.getFromSavingsAccount() == null) {
                    fromSavingsAccount = this.savingsAccountAssembler.assembleFrom(accountTransferDTO.getFromAccountId());
                } else {
                    fromSavingsAccount = accountTransferDTO.getFromSavingsAccount();
                    this.savingsAccountAssembler.setHelpers(fromSavingsAccount);
                }
                if (accountTransferDTO.getLoan() == null) {
                    toLoanAccount = this.loanAccountAssembler.assembleFrom(accountTransferDTO.getToAccountId());
                } else {
                    toLoanAccount = accountTransferDTO.getLoan();
                    this.loanAccountAssembler.setHelpers(toLoanAccount);
                }

            } else {
                fromSavingsAccount = accountTransferDetails.fromSavingsAccount();
                this.savingsAccountAssembler.setHelpers(fromSavingsAccount);
                toLoanAccount = accountTransferDetails.toLoanAccount();
                this.loanAccountAssembler.setHelpers(toLoanAccount);
            }

            final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                    isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(), AccountTransferType.fromInt(
                            accountTransferDTO.getTransferType()).isInterestTransfer(), accountTransferDTO.isExceptionForBalanceCheck());

            final SavingsAccountTransaction withdrawal = this.savingsAccountDomainService.handleWithdrawal(fromSavingsAccount,
                    accountTransferDTO.getFmt(), accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                    accountTransferDTO.getPaymentDetail(), transactionBooleanValues);

            LoanTransaction loanTransaction = null;

            if (AccountTransferType.fromInt(accountTransferDTO.getTransferType()).isChargePayment()) {
                loanTransaction = this.loanAccountDomainService.makeChargePayment(toLoanAccount, accountTransferDTO.getChargeId(),
                        accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                        accountTransferDTO.getPaymentDetail(), null, null, accountTransferDTO.getToTransferType(),
                        accountTransferDTO.getLoanInstallmentNumber());

            } else {
                final boolean isRecoveryRepayment = false;
                loanTransaction = this.loanAccountDomainService.makeRepayment(toLoanAccount, new CommandProcessingResultBuilder(),
                        accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                        accountTransferDTO.getPaymentDetail(), null, null, isRecoveryRepayment, isAccountTransfer);
            }

            accountTransferDetails = this.accountTransferAssembler.assembleSavingsToLoanTransfer(accountTransferDTO, fromSavingsAccount,
                    toLoanAccount, withdrawal, loanTransaction);
            this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            transferTransactionId = accountTransferDetails.getId();
        } else if (isSavingsToSavingsAccountTransfer(accountTransferDTO.getFromAccountType(), accountTransferDTO.getToAccountType())) {

            SavingsAccount fromSavingsAccount = null;
            SavingsAccount toSavingsAccount = null;
            if (accountTransferDetails == null) {
                if (accountTransferDTO.getFromSavingsAccount() == null) {
                    fromSavingsAccount = this.savingsAccountAssembler.assembleFrom(accountTransferDTO.getFromAccountId());
                } else {
                    fromSavingsAccount = accountTransferDTO.getFromSavingsAccount();
                    this.savingsAccountAssembler.setHelpers(fromSavingsAccount);
                }
                if (accountTransferDTO.getToSavingsAccount() == null) {
                    toSavingsAccount = this.savingsAccountAssembler.assembleFrom(accountTransferDTO.getToAccountId());
                } else {
                    toSavingsAccount = accountTransferDTO.getToSavingsAccount();
                    this.savingsAccountAssembler.setHelpers(toSavingsAccount);
                }
            } else {
                fromSavingsAccount = accountTransferDetails.fromSavingsAccount();
                this.savingsAccountAssembler.setHelpers(fromSavingsAccount);
                toSavingsAccount = accountTransferDetails.toSavingsAccount();
                this.savingsAccountAssembler.setHelpers(toSavingsAccount);
            }

            final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
                    isRegularTransaction, fromSavingsAccount.isWithdrawalFeeApplicableForTransfer(), AccountTransferType.fromInt(
                            accountTransferDTO.getTransferType()).isInterestTransfer(), accountTransferDTO.isExceptionForBalanceCheck());

            final SavingsAccountTransaction withdrawal = this.savingsAccountDomainService.handleWithdrawal(fromSavingsAccount,
                    accountTransferDTO.getFmt(), accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                    accountTransferDTO.getPaymentDetail(), transactionBooleanValues);

            final SavingsAccountTransaction deposit = this.savingsAccountDomainService.handleDeposit(toSavingsAccount,
                    accountTransferDTO.getFmt(), accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                    accountTransferDTO.getPaymentDetail(), isAccountTransfer, isRegularTransaction);

            accountTransferDetails = this.accountTransferAssembler.assembleSavingsToSavingsTransfer(accountTransferDTO, fromSavingsAccount,
                    toSavingsAccount, withdrawal, deposit);
            this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            transferTransactionId = accountTransferDetails.getId();

        } else if (isLoanToSavingsAccountTransfer(accountTransferDTO.getFromAccountType(), accountTransferDTO.getToAccountType())) {

            Loan fromLoanAccount = null;
            SavingsAccount toSavingsAccount = null;
            if (accountTransferDetails == null) {
                if (accountTransferDTO.getLoan() == null) {
                    fromLoanAccount = this.loanAccountAssembler.assembleFrom(accountTransferDTO.getFromAccountId());
                } else {
                    fromLoanAccount = accountTransferDTO.getLoan();
                    this.loanAccountAssembler.setHelpers(fromLoanAccount);
                }
                toSavingsAccount = this.savingsAccountAssembler.assembleFrom(accountTransferDTO.getToAccountId());
            } else {
                fromLoanAccount = accountTransferDetails.fromLoanAccount();
                this.loanAccountAssembler.setHelpers(fromLoanAccount);
                toSavingsAccount = accountTransferDetails.toSavingsAccount();
                this.savingsAccountAssembler.setHelpers(toSavingsAccount);
            }
            LoanTransaction loanTransaction = null;
            if (LoanTransactionType.DISBURSEMENT.getValue().equals(accountTransferDTO.getFromTransferType())) {
                loanTransaction = this.loanAccountDomainService.makeDisburseTransaction(accountTransferDTO.getFromAccountId(),
                        accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                        accountTransferDTO.getPaymentDetail(), accountTransferDTO.getNoteText(), accountTransferDTO.getTxnExternalId());
            } else {
                loanTransaction = this.loanAccountDomainService.makeRefund(accountTransferDTO.getFromAccountId(),
                        new CommandProcessingResultBuilder(), accountTransferDTO.getTransactionDate(),
                        accountTransferDTO.getTransactionAmount(), accountTransferDTO.getPaymentDetail(), accountTransferDTO.getNoteText(),
                        accountTransferDTO.getTxnExternalId());
            }

            final SavingsAccountTransaction deposit = this.savingsAccountDomainService.handleDeposit(toSavingsAccount,
                    accountTransferDTO.getFmt(), accountTransferDTO.getTransactionDate(), accountTransferDTO.getTransactionAmount(),
                    accountTransferDTO.getPaymentDetail(), isAccountTransfer, isRegularTransaction);
            accountTransferDetails = this.accountTransferAssembler.assembleLoanToSavingsTransfer(accountTransferDTO, fromLoanAccount,
                    toSavingsAccount, deposit, loanTransaction);
            this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
            transferTransactionId = accountTransferDetails.getId();
        }

        return transferTransactionId;
    }

    @Override
    @Transactional
    public void updateLoanTransaction(final Long loanTransactionId, final LoanTransaction newLoanTransaction) {
        final AccountTransferTransaction transferTransaction = this.accountTransferRepository.findByToLoanTransactionId(loanTransactionId);
        if (transferTransaction != null) {
            transferTransaction.updateToLoanTransaction(newLoanTransaction);
            this.accountTransferRepository.save(transferTransaction);
        }
    }

    private boolean isLoanToSavingsAccountTransfer(final PortfolioAccountType fromAccountType, final PortfolioAccountType toAccountType) {
        return fromAccountType.isLoanAccount() && toAccountType.isSavingsAccount();
    }

    private boolean isSavingsToLoanAccountTransfer(final PortfolioAccountType fromAccountType, final PortfolioAccountType toAccountType) {
        return fromAccountType.isSavingsAccount() && toAccountType.isLoanAccount();
    }

    private boolean isSavingsToSavingsAccountTransfer(final PortfolioAccountType fromAccountType, final PortfolioAccountType toAccountType) {
        return fromAccountType.isSavingsAccount() && toAccountType.isSavingsAccount();
    }
    
    @Override
    @Transactional
    public CommandProcessingResult refundByTransfer(JsonCommand command) {
        // TODO Auto-generated method stub
        this.accountTransfersDataValidator.validate(command);

        final LocalDate transactionDate = command.localDateValueOfParameterNamed(transferDateParamName);
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed(transferAmountParamName);

        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);

        final PaymentDetail paymentDetail = null; 
        Long transferTransactionId = null;

        final Long fromLoanAccountId = command.longValueOfParameterNamed(fromAccountIdParamName);
        final Loan fromLoanAccount = this.loanAccountAssembler.assembleFrom(fromLoanAccountId);

        BigDecimal overpaid = this.loanReadPlatformService.retrieveTotalPaidInAdvance(fromLoanAccountId).getPaidInAdvance();

        if (overpaid == null || overpaid.equals(BigDecimal.ZERO) || transactionAmount.floatValue() > overpaid.floatValue()) {
            if(overpaid == null)
                overpaid = BigDecimal.ZERO;
            throw new InvalidPaidInAdvanceAmountException(overpaid.toPlainString());
        }

        final LoanTransaction loanRefundTransaction = this.loanAccountDomainService.makeRefundForActiveLoan(fromLoanAccountId,
                new CommandProcessingResultBuilder(), transactionDate, transactionAmount, paymentDetail, null, null);

        final Long toSavingsAccountId = command.longValueOfParameterNamed(toAccountIdParamName);
        final SavingsAccount toSavingsAccount = this.savingsAccountAssembler.assembleFrom(toSavingsAccountId);

        final SavingsAccountTransaction deposit = this.savingsAccountDomainService.handleDeposit(toSavingsAccount, fmt, transactionDate,
                transactionAmount, paymentDetail, true, true);

        final AccountTransferDetails accountTransferDetails = this.accountTransferAssembler.assembleLoanToSavingsTransfer(command,
                fromLoanAccount, toSavingsAccount, deposit, loanRefundTransaction);
        this.accountTransferDetailRepository.saveAndFlush(accountTransferDetails);
        transferTransactionId = accountTransferDetails.getId();

        final CommandProcessingResultBuilder builder = new CommandProcessingResultBuilder().withEntityId(transferTransactionId);

        // if (fromAccountType.isSavingsAccount()) {

        builder.withSavingsId(toSavingsAccountId);
        // }

        return builder.build();
    }
}