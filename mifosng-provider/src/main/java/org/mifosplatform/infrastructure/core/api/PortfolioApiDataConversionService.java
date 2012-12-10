package org.mifosplatform.infrastructure.core.api;

import org.mifosplatform.portfolio.loanaccount.command.GroupLoanApplicationCommand;
import org.mifosplatform.organisation.staff.command.BulkTransferLoanOfficerCommand;
import org.mifosplatform.portfolio.client.command.NoteCommand;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.group.command.GroupCommand;
import org.mifosplatform.portfolio.loanaccount.command.AdjustLoanTransactionCommand;
import org.mifosplatform.portfolio.loanaccount.command.LoanApplicationCommand;
import org.mifosplatform.portfolio.loanaccount.command.LoanChargeCommand;
import org.mifosplatform.portfolio.loanaccount.command.LoanStateTransitionCommand;
import org.mifosplatform.portfolio.loanaccount.command.LoanTransactionCommand;
import org.mifosplatform.portfolio.loanaccount.gaurantor.command.GuarantorCommand;
import org.mifosplatform.portfolio.savingsaccount.command.SavingAccountCommand;
import org.mifosplatform.portfolio.savingsaccountproduct.command.SavingProductCommand;
import org.mifosplatform.portfolio.savingsdepositaccount.command.DepositAccountCommand;
import org.mifosplatform.portfolio.savingsdepositaccount.command.DepositAccountWithdrawInterestCommand;
import org.mifosplatform.portfolio.savingsdepositaccount.command.DepositAccountWithdrawalCommand;
import org.mifosplatform.portfolio.savingsdepositaccount.command.DepositStateTransitionApprovalCommand;
import org.mifosplatform.portfolio.savingsdepositaccount.command.DepositStateTransitionCommand;
import org.mifosplatform.portfolio.savingsdepositproduct.command.DepositProductCommand;

public interface PortfolioApiDataConversionService {

    SavingProductCommand convertJsonToSavingProductCommand(Long resourceIdentifier, String json);

    DepositProductCommand convertJsonToDepositProductCommand(Long resourceIdentifier, String json);

    ClientData convertInternalJsonFormatToClientDataChange(Long clientId, String json);

    GroupCommand convertJsonToGroupCommand(Long resourceIdentifier, String json);

    LoanApplicationCommand convertApiRequestJsonToLoanApplicationCommand(Long resourceIdentifier, String json);

    GroupLoanApplicationCommand convertJsonToGroupLoanApplicationCommand(Long resourceIdentifier, String json);

    LoanChargeCommand convertJsonToLoanChargeCommand(Long loanChargeId, Long loanId, String json);

    LoanStateTransitionCommand convertJsonToLoanStateTransitionCommand(Long resourceIdentifier, String json);

    LoanTransactionCommand convertJsonToLoanTransactionCommand(Long resourceIdentifier, String json);

    AdjustLoanTransactionCommand convertJsonToAdjustLoanTransactionCommand(Long loanId, Long transactionId, String json);

    NoteCommand convertJsonToNoteCommand(Long resourceIdentifier, Long clientId, String json);

    DepositAccountCommand convertJsonToDepositAccountCommand(Long resourceIdentifier, String json);

    DepositStateTransitionCommand convertJsonToDepositStateTransitionCommand(Long resourceIdentifier, String json);

    DepositStateTransitionApprovalCommand convertJsonToDepositStateTransitionApprovalCommand(Long resourceIdentifier, String json);

    BulkTransferLoanOfficerCommand convertJsonToLoanReassignmentCommand(Long resourceIdentifier, String json);

    BulkTransferLoanOfficerCommand convertJsonToBulkLoanReassignmentCommand(String json);

    DepositAccountWithdrawalCommand convertJsonToDepositWithdrawalCommand(Long resourceIdentifier, String json);

    DepositAccountWithdrawInterestCommand convertJsonToDepositAccountWithdrawInterestCommand(Long resourceIdentifier, String json);

    SavingAccountCommand convertJsonToSavingAccountCommand(Long resourceIdentifier, String json);

    GuarantorCommand convertJsonToGuarantorCommand(Long resourceIdentifier, Long loanId, String json);
}