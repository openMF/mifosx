package org.mifosplatform.portfolio.client.data;

import java.util.List;

/**
 * Immutable data object representing a summary of a clients various accounts. 
 */
public class ClientAccountSummaryCollectionData {

	private final int anyLoanCount;
	private final int pendingApprovalLoanCount;
	private final List<ClientAccountSummaryData> pendingApprovalLoans;
	private final int awaitingDisbursalLoanCount;
	private final List<ClientAccountSummaryData> awaitingDisbursalLoans;
	private final int activeLoanCount;
	private final List<ClientAccountSummaryData> openLoans;
	private final int closedLoanCount;
	private final List<ClientAccountSummaryData> closedLoans;
	private final int pendingApprovalDespositAccountsCount;
	private final List<ClientAccountSummaryData> pendingApprovalDepositAccounts;
	private final int approvedDespositAccountsCount;
	private final List<ClientAccountSummaryData> approvedDepositAccounts;
	private final int withdrawnByClientDespositAccountsCount;
	private final List<ClientAccountSummaryData> withdrawnByClientDespositAccounts;
	private final int closedDepositAccountsCount;
	private final List<ClientAccountSummaryData> closedDepositAccounts;
	private final int rejectedDepositAccountsCount;
	private final List<ClientAccountSummaryData> rejectedDepositAccounts;
	private final int preclosedDepositAccountsCount;
	private final List<ClientAccountSummaryData> preclosedDepositAccounts;
	private final int maturedDepositAccountsCount;
	private final List<ClientAccountSummaryData> maturedDepositAccounts;
	private final int pendingApprovalSavingAccountsCount;
	private final List<ClientAccountSummaryData> pendingApprovalSavingAccounts;
	private final int approvedSavingAccountsCount;
	private final List<ClientAccountSummaryData> approvedSavingAccounts;
	private final int withdrawnByClientSavingAccountsCount;
	private final List<ClientAccountSummaryData> withdrawnByClientSavingAccounts;
	private final int rejectedSavingAccountsCount;
	private final List<ClientAccountSummaryData> rejectedSavingAccounts;
	private final int closedSavingAccountsCount;
	private final List<ClientAccountSummaryData> closedSavingAccounts;
	
	public ClientAccountSummaryCollectionData(
			final List<ClientAccountSummaryData> pendingApprovalLoans,
			final List<ClientAccountSummaryData> awaitingDisbursalLoans,
			final List<ClientAccountSummaryData> openLoans,
			final List<ClientAccountSummaryData> closedLoans, 
			final List<ClientAccountSummaryData> pendingApprovalDepositAccounts, 
			final List<ClientAccountSummaryData> approvedDepositAccounts,
			final List<ClientAccountSummaryData> withdrawnByClientDespositAccounts,
			final List<ClientAccountSummaryData> rejectedDepositAccounts,
			final List<ClientAccountSummaryData> closedDepositAccounts,
			final List<ClientAccountSummaryData> preclosedDepositAccounts,
			final List<ClientAccountSummaryData> maturedDepositAccounts,
			final List<ClientAccountSummaryData> pendingApprovalSavingAccounts,
			final List<ClientAccountSummaryData> approvedSavingAccounts,
			final List<ClientAccountSummaryData> withdrawnByClientSavingAccounts,
			final List<ClientAccountSummaryData> rejectedSavingAccounts,
			final List<ClientAccountSummaryData> closedSavingAccounts) {
		this.pendingApprovalLoans = pendingApprovalLoans;
		this.awaitingDisbursalLoans = awaitingDisbursalLoans;
		this.openLoans = openLoans;
		this.closedLoans = closedLoans;
		this.pendingApprovalDepositAccounts = pendingApprovalDepositAccounts;
		this.approvedDepositAccounts = approvedDepositAccounts;
		this.withdrawnByClientDespositAccounts = withdrawnByClientDespositAccounts;
		this.closedDepositAccounts = closedDepositAccounts;
		this.rejectedDepositAccounts = rejectedDepositAccounts;
		this.preclosedDepositAccounts = preclosedDepositAccounts;
		this.maturedDepositAccounts = maturedDepositAccounts;
		
		// saving accounts
		this.pendingApprovalSavingAccounts = pendingApprovalSavingAccounts;
		this.approvedSavingAccounts = approvedSavingAccounts;
		this.withdrawnByClientSavingAccounts = withdrawnByClientSavingAccounts;
		this.rejectedSavingAccounts = rejectedSavingAccounts;
		this.closedSavingAccounts = closedSavingAccounts;
		
		this.pendingApprovalLoanCount = this.pendingApprovalLoans.size();
		this.awaitingDisbursalLoanCount = this.awaitingDisbursalLoans.size();
		this.activeLoanCount = this.openLoans.size();
		this.closedLoanCount = this.closedLoans.size();
		
		this.pendingApprovalDespositAccountsCount = this.pendingApprovalDepositAccounts.size();
		this.approvedDespositAccountsCount = this.approvedDepositAccounts.size();
		this.withdrawnByClientDespositAccountsCount = this.withdrawnByClientDespositAccounts.size();
		this.closedDepositAccountsCount = this.closedDepositAccounts.size();
		this.rejectedDepositAccountsCount = this.rejectedDepositAccounts.size();
		this.preclosedDepositAccountsCount = this.preclosedDepositAccounts.size();
		this.maturedDepositAccountsCount = this.maturedDepositAccounts.size();
		this.pendingApprovalSavingAccountsCount = this.pendingApprovalSavingAccounts.size();
		this.approvedSavingAccountsCount = this.approvedSavingAccounts.size();
		this.withdrawnByClientSavingAccountsCount = this.withdrawnByClientSavingAccounts.size();
		this.rejectedSavingAccountsCount = this.rejectedSavingAccounts.size();
		this.closedSavingAccountsCount = this.closedSavingAccounts.size();
		
		this.anyLoanCount = this.pendingApprovalLoanCount + this.awaitingDisbursalLoanCount + this.activeLoanCount + this.closedLoanCount
				+this.pendingApprovalDespositAccountsCount+this.approvedDespositAccountsCount+this.withdrawnByClientDespositAccountsCount+this.closedDepositAccountsCount
				+this.rejectedDepositAccountsCount+this.preclosedDepositAccountsCount+this.maturedDepositAccountsCount
				+this.pendingApprovalSavingAccountsCount+this.approvedSavingAccountsCount+this.withdrawnByClientSavingAccountsCount
				+this.rejectedSavingAccountsCount+this.closedSavingAccountsCount;
		
	}

	public int getAnyLoanCount() {
		return anyLoanCount;
	}

	public int getPendingApprovalLoanCount() {
		return pendingApprovalLoanCount;
	}

	public List<ClientAccountSummaryData> getPendingApprovalLoans() {
		return pendingApprovalLoans;
	}

	public int getAwaitingDisbursalLoanCount() {
		return awaitingDisbursalLoanCount;
	}

	public List<ClientAccountSummaryData> getAwaitingDisbursalLoans() {
		return awaitingDisbursalLoans;
	}

	public int getActiveLoanCount() {
		return activeLoanCount;
	}

	public List<ClientAccountSummaryData> getOpenLoans() {
		return openLoans;
	}

	public int getClosedLoanCount() {
		return closedLoanCount;
	}

	public List<ClientAccountSummaryData> getClosedLoans() {
		return closedLoans;
	}

	public int getPendingApprovalDespositAccountsCount() {
		return pendingApprovalDespositAccountsCount;
	}

	public List<ClientAccountSummaryData> getPendingApprovalDepositAccounts() {
		return pendingApprovalDepositAccounts;
	}

	public int getApprovedDespositAccountsCount() {
		return approvedDespositAccountsCount;
	}

	public List<ClientAccountSummaryData> getApprovedDepositAccounts() {
		return approvedDepositAccounts;
	}

	public int getWithdrawnByClientDespositAccountsCount() {
		return withdrawnByClientDespositAccountsCount;
	}

	public List<ClientAccountSummaryData> getWithdrawnByClientDespositAccounts() {
		return withdrawnByClientDespositAccounts;
	}

	public int getClosedDepositAccountsCount() {
		return closedDepositAccountsCount;
	}

	public List<ClientAccountSummaryData> getClosedDepositAccounts() {
		return closedDepositAccounts;
	}

	public int getRejectedDepositAccountsCount() {
		return rejectedDepositAccountsCount;
	}

	public List<ClientAccountSummaryData> getRejectedDepositAccounts() {
		return rejectedDepositAccounts;
	}

	public int getPreclosedDepositAccountsCount() {
		return preclosedDepositAccountsCount;
	}

	public List<ClientAccountSummaryData> getPreclosedDepositAccounts() {
		return preclosedDepositAccounts;
	}
}