package org.mifosng.platform.loan.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifosng.platform.api.commands.LoanApplicationCommand;
import org.mifosng.platform.api.data.LoanSchedule;
import org.mifosng.platform.api.data.MoneyData;
import org.mifosng.platform.api.data.ScheduledLoanInstallment;
import org.mifosng.platform.client.domain.Client;
import org.mifosng.platform.currency.domain.MonetaryCurrency;
import org.mifosng.platform.currency.domain.Money;
import org.mifosng.platform.exceptions.InvalidLoanStateTransitionException;
import org.mifosng.platform.exceptions.InvalidLoanTransactionTypeException;
import org.mifosng.platform.fund.domain.Fund;
import org.mifosng.platform.infrastructure.AbstractAuditableCustom;
import org.mifosng.platform.staff.domain.Staff;
import org.mifosng.platform.user.domain.AppUser;

@Entity
@Table(name = "m_loan", uniqueConstraints = @UniqueConstraint(columnNames = { "external_id" }))
public class Loan extends AbstractAuditableCustom<AppUser, Long> {

	@ManyToOne
	@JoinColumn(name = "client_id", nullable = false)
	private Client client;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private LoanProduct loanProduct;

	@SuppressWarnings("unused")
	@ManyToOne
	@JoinColumn(name = "fund_id", nullable = true)
	private Fund fund;
	
	@ManyToOne
	@JoinColumn(name = "loan_officer_id", nullable = true)
	private Staff loanofficer;
	
	@ManyToOne
	@JoinColumn(name = "loan_transaction_strategy_id", nullable = true)
	private LoanTransactionProcessingStrategy transactionProcessingStrategy;

	@Column(name = "external_id")
	private String externalId;

	@Embedded
	private LoanProductRelatedDetail loanRepaymentScheduleDetail;
	
	@SuppressWarnings("unused")
	@Column(name = "term_frequency", nullable = false)
	private Integer termFrequency;

	@SuppressWarnings("unused")
	@Column(name = "term_period_frequency_enum", nullable = false)
	private Integer termPeriodFrequencyType;

	@Column(name = "loan_status_id", nullable = false)
	private Integer loanStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "submittedon_date")
	private Date submittedOnDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "rejectedon_date")
	private Date rejectedOnDate;

	@SuppressWarnings("unused")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "withdrawnon_date")
	private Date withdrawnOnDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "approvedon_date")
	private Date approvedOnDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "expected_disbursedon_date")
	private Date expectedDisbursedOnDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "expected_firstrepaymenton_date")
	private Date expectedFirstRepaymentOnDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "interest_calculated_from_date")
	private Date interestChargedFromDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "disbursedon_date")
	private Date disbursedOnDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "closedon_date")
	private Date closedOnDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "writtenoffon_date")
	private Date writtenOffOnDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "rescheduledon_date")
	private Date rescheduledOnDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "expected_maturedon_date")
	private Date expectedMaturityDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "maturedon_date")
	private Date maturedOnDate;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loan", orphanRemoval = true)
    private Set<LoanCharge> charges;

	// see
	// http://stackoverflow.com/questions/4334970/hibernate-cannot-simultaneously-fetch-multiple-bags
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "loan", orphanRemoval = true)
	private final List<LoanRepaymentScheduleInstallment> repaymentScheduleInstallments = new ArrayList<LoanRepaymentScheduleInstallment>();

	// see
	// http://stackoverflow.com/questions/4334970/hibernate-cannot-simultaneously-fetch-multiple-bags
	@OrderBy(value = "dateOf, id")
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "loan", orphanRemoval = true)
	private final List<LoanTransaction> loanTransactions = new ArrayList<LoanTransaction>();

	@Column(name = "interest_rebate_amount", scale = 6, precision = 19)
	private BigDecimal interestRebateOwed;

	@Transient
	private final InterestRebateCalculatorFactory interestRebateCalculatorFactory = new DailyEquivalentInterestRebateCalculatorFactory();
	
	@Transient
	private final LoanRepaymentScheduleTransactionProcessorFactory transactionProcessor = new LoanRepaymentScheduleTransactionProcessorFactory();

	public static Loan createNew(final Fund fund,final Staff loanOfficer, final LoanTransactionProcessingStrategy transactionProcessingStrategy,
			final LoanProduct loanProduct, final Client client,
			final LoanProductRelatedDetail loanRepaymentScheduleDetail) {
		return new Loan(client, fund,loanOfficer, transactionProcessingStrategy, loanProduct, loanRepaymentScheduleDetail, null);
	}

	protected Loan() {
		this.client = null;
		this.loanProduct = null;
		this.loanRepaymentScheduleDetail = null;
        this.charges = null;
	}

	public Loan(final Client client, Fund fund, Staff loanOfficer, LoanTransactionProcessingStrategy transactionProcessingStrategy, final LoanProduct loanProduct,
			final LoanProductRelatedDetail loanRepaymentScheduleDetail,
			final LoanStatus loanStatus) {
		this.client = client;
		this.fund = fund;
		this.loanofficer = loanOfficer;
		this.transactionProcessingStrategy = transactionProcessingStrategy;
		this.loanProduct = loanProduct;
		this.loanRepaymentScheduleDetail = loanRepaymentScheduleDetail;
		if (loanStatus != null) {
			this.loanStatus = loanStatus.getValue();
		} else {
			this.loanStatus = null;
		}
		this.interestRebateOwed = BigDecimal.ZERO;
	}

	public Client client() {
		return this.client;
	}

	public LoanProduct loanProduct() {
		return this.loanProduct;
	}

	public Staff getLoanofficer() {
		return loanofficer;
	}

	public LoanProductRelatedDetail repaymentScheduleDetail() {
		return this.loanRepaymentScheduleDetail;
	}
	
	public void modifyLoanApplication(final LoanApplicationCommand command, final Client client, final LoanProduct loanProduct, 
			final Fund fund, final LoanTransactionProcessingStrategy strategy, final LoanSchedule modifiedLoanSchedule, final Set<LoanCharge> charges,
			final Staff loanOfficer) {

		if (command.isClientChanged()) {
			this.client = client;
		}
		if (command.isProductChanged()) {
			this.loanProduct = loanProduct;
		}
		if (command.isFundChanged()) {
			this.fund = fund;
		}
		if(command.isLoanOfficerChanged()){
			this.loanofficer = loanOfficer;
		}
		if (command.isTransactionStrategyChanged()) {
			this.transactionProcessingStrategy = strategy;
		}
		
		if (command.isTermFrequencyChanged()) {
			this.termFrequency = command.getLoanTermFrequency();
		}
		
		if (command.isTermFrequencyTypeChanged()) {
			this.termPeriodFrequencyType = PeriodFrequencyType.fromInt(command.getLoanTermFrequencyType()).getValue();
		}
		
		if (command.isSubmittedOnDateChanged()) {
			this.submittedOnDate = command.getSubmittedOnDate().toDate();
		}
		
		if (command.isExpectedDisbursementDateChanged()) {
			if (command.getExpectedDisbursementDate() != null) {
				this.expectedDisbursedOnDate = command.getExpectedDisbursementDate().toDate();
			} else {
				this.expectedDisbursedOnDate = null;
			}
		}
		
		if (command.isRepaymentsStartingFromDateChanged()) {
			if (command.getRepaymentsStartingFromDate() != null) {
				this.expectedFirstRepaymentOnDate = command.getRepaymentsStartingFromDate().toDate();
			} else {
				this.expectedFirstRepaymentOnDate = null;
			}
		}
		
		if (command.isInterestChargedFromDateChanged()) {
			if (command.getInterestChargedFromDate() != null) {
				this.interestChargedFromDate = command.getInterestChargedFromDate().toDate();
			} else {
				this.interestChargedFromDate = null;
			}
		}

        if (command.isChargesChanged()) {
			this.charges.clear();
			this.charges.addAll(charges);
		}

		this.loanRepaymentScheduleDetail.update(command.toLoanProductCommand());
		
		// FIXME - rewrite over loan schedule by default for now but worth putting in check to see if required
		// i.e. only a client change wouldn't require it, only if one of parameters related to loan schedule calculation is changed.
		this.repaymentScheduleInstallments.clear();
		for (ScheduledLoanInstallment scheduledLoanInstallment : modifiedLoanSchedule.getScheduledLoanInstallments()) {

			MoneyData readPrincipalDue = scheduledLoanInstallment.getPrincipalDue();
			MoneyData readInterestDue = scheduledLoanInstallment.getInterestDue();

			LoanRepaymentScheduleInstallment installment = new LoanRepaymentScheduleInstallment(
					this, scheduledLoanInstallment.getInstallmentNumber(),
					scheduledLoanInstallment.getPeriodEnd(), readPrincipalDue.getAmount(),
					readInterestDue.getAmount());
			this.addRepaymentScheduleInstallment(installment);
		}
		
		// if the loan application/contract is modified when repayments are already against it - then need to re-process it
		List<LoanTransaction> repaymentsOrWaivers = new ArrayList<LoanTransaction>();
		for (LoanTransaction transaction : this.loanTransactions) {
			if (!transaction.isDisbursement() && transaction.isNotContra()) {
				repaymentsOrWaivers.add(transaction);
			}
		}
		LoanTransactionComparator transactionComparator = new LoanTransactionComparator();
		Collections.sort(repaymentsOrWaivers, transactionComparator);
		
		final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessor.determineProcessor(this.transactionProcessingStrategy);
		loanRepaymentScheduleTransactionProcessor.handleTransaction(repaymentsOrWaivers, getCurrency(), this.repaymentScheduleInstallments);
	}

	public void submitApplication(
			final Integer loanTermFrequency, 
			final PeriodFrequencyType loanTermFrequencyType, 
			final LocalDate submittedOn,
			final LocalDate expectedDisbursementDate,
			final LocalDate repaymentsStartingFromDate,
			final LocalDate interestChargedFromDate,
			final LoanLifecycleStateMachine lifecycleStateMachine) {
		
		LoanStatus from = null;
		if (this.loanStatus != null) {
			from = LoanStatus.fromInt(this.loanStatus);
		}

		LoanStatus statusEnum = lifecycleStateMachine.transition(LoanEvent.LOAN_CREATED, from);
		this.loanStatus = statusEnum.getValue();

		this.termFrequency = loanTermFrequency;
		this.termPeriodFrequencyType = loanTermFrequencyType.getValue();
		
		this.submittedOnDate = submittedOn.toDateTimeAtCurrentTime().toDate();

		// TODO - this should also match expectedDisbursmentDate + loan term
		this.expectedMaturityDate = this.repaymentScheduleInstallments
				.get(this.repaymentScheduleInstallments.size() - 1)
				.getDueDate().toDateMidnight().toDate();
		if (expectedDisbursementDate != null) {
			// can be null during bulk upload of loans
			this.expectedDisbursedOnDate = expectedDisbursementDate
					.toDateMidnight().toDate();
		}

		if (repaymentsStartingFromDate != null) {
			this.expectedFirstRepaymentOnDate = repaymentsStartingFromDate
					.toDateMidnight().toDate();
		}

		if (interestChargedFromDate != null) {
			this.interestChargedFromDate = interestChargedFromDate
					.toDateMidnight().toDate();
		}

		if (submittedOn.isAfter(new LocalDate())) {
			final String errorMessage = "The date on which a loan is submitted cannot be in the future.";
			throw new InvalidLoanStateTransitionException("submittal",
					"cannot.be.a.future.date", errorMessage, submittedOn);
		}

		if (submittedOn.isAfter(getExpectedDisbursedOnLocalDate())) {
			final String errorMessage = "The date on which a loan is submitted cannot be after its expected disbursement date: "
					+ getExpectedDisbursedOnLocalDate().toString();
			throw new InvalidLoanStateTransitionException("submittal",
					"cannot.be.after.expected.disbursement.date", errorMessage,
					submittedOn, getExpectedDisbursedOnLocalDate());
		}
	}

	public void reject(final LocalDate rejectedOn, final LoanLifecycleStateMachine loanLifecycleStateMachine) {

		LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_REJECTED, LoanStatus.fromInt(this.loanStatus));
		this.loanStatus = statusEnum.getValue();
		
		this.rejectedOnDate = rejectedOn.toDateTimeAtCurrentTime().toDate();
		this.closedOnDate = rejectedOn.toDateTimeAtCurrentTime().toDate();

		if (rejectedOn.isBefore(getSubmittedOnDate())) {
			final String errorMessage = "The date on which a loan is rejected cannot be before its submittal date: "
					+ getSubmittedOnDate().toString();
			throw new InvalidLoanStateTransitionException("reject",
					"cannot.be.before.submittal.date", errorMessage,
					rejectedOn, getSubmittedOnDate());
		}
		if (rejectedOn.isAfter(new LocalDate())) {
			final String errorMessage = "The date on which a loan is rejected cannot be in the future.";
			throw new InvalidLoanStateTransitionException("reject",
					"cannot.be.a.future.date", errorMessage, rejectedOn);
		}
	}

	public void withdraw(final LocalDate withdrawnOn,
			LoanLifecycleStateMachine loanLifecycleStateMachine) {

		LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_WITHDRAWN, LoanStatus.fromInt(this.loanStatus));
		this.loanStatus = statusEnum.getValue();
		
		this.withdrawnOnDate = withdrawnOn.toDateTimeAtCurrentTime().toDate();
		this.closedOnDate = withdrawnOn.toDateTimeAtCurrentTime().toDate();

		if (withdrawnOn.isBefore(getSubmittedOnDate())) {
			final String errorMessage = "The date on which a loan is withdrawn cannot be before its submittal date: "
					+ getSubmittedOnDate().toString();
			throw new InvalidLoanStateTransitionException("reject",
					"cannot.be.before.submittal.date", errorMessage,
					withdrawnOn, getSubmittedOnDate());
		}
		if (withdrawnOn.isAfter(new LocalDate())) {
			final String errorMessage = "The date on which a loan is withdrawn cannot be in the future.";
			throw new InvalidLoanStateTransitionException("reject",
					"cannot.be.a.future.date", errorMessage, withdrawnOn);
		}
	}

	public void approve(final LocalDate approvedOn,
			LoanLifecycleStateMachine loanLifecycleStateMachine) {
		
		LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_APPROVED, LoanStatus.fromInt(this.loanStatus));
		this.loanStatus = statusEnum.getValue();
		
		this.approvedOnDate = approvedOn.toDateTimeAtCurrentTime().toDate();

		LocalDate submittalDate = new LocalDate(this.submittedOnDate);
		if (approvedOn.isBefore(submittalDate)) {
			final String errorMessage = "The date on which a loan is approved cannot be before its submittal date: "
					+ submittalDate.toString();
			throw new InvalidLoanStateTransitionException("approval",
					"cannot.be.before.submittal.date", errorMessage,
					getApprovedOnDate(), submittalDate);
		}
		if (approvedOn.isAfter(new LocalDate())) {
			final String errorMessage = "The date on which a loan is approved cannot be in the future.";
			throw new InvalidLoanStateTransitionException("approval",
					"cannot.be.a.future.date", errorMessage,
					getApprovedOnDate());
		}
	}

	public void undoApproval(LoanLifecycleStateMachine loanLifecycleStateMachine) {
		
		LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_APPROVAL_UNDO, LoanStatus.fromInt(this.loanStatus));
		this.loanStatus = statusEnum.getValue();
		
		this.approvedOnDate = null;
	}

	public void disburseWithModifiedRepaymentSchedule(
			final LocalDate disbursedOn,
			final List<LoanRepaymentScheduleInstallment> modifiedLoanRepaymentSchedule,
			LoanLifecycleStateMachine loanLifecycleStateMachine) {
		this.repaymentScheduleInstallments.clear();
		for (LoanRepaymentScheduleInstallment modifiedInstallment : modifiedLoanRepaymentSchedule) {
			modifiedInstallment.updateLoan(this);
			this.repaymentScheduleInstallments.add(modifiedInstallment);
		}
		disburse(disbursedOn, loanLifecycleStateMachine);
	}

	public void disburse(final LocalDate disbursedOn,
			LoanLifecycleStateMachine loanLifecycleStateMachine) {
		
		LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_DISBURSED, LoanStatus.fromInt(this.loanStatus));
		this.loanStatus = statusEnum.getValue();
		
		this.disbursedOnDate = disbursedOn.toDateTimeAtCurrentTime().toDate();
		this.expectedMaturityDate = this.repaymentScheduleInstallments
				.get(this.repaymentScheduleInstallments.size() - 1)
				.getDueDate().toDateMidnight().toDate();

		LoanTransaction loanTransaction = LoanTransaction.disbursement(
				this.loanRepaymentScheduleDetail.getPrincipal(), disbursedOn);
		loanTransaction.updateLoan(this);
		this.loanTransactions.add(loanTransaction);

		if (disbursedOn.isBefore(getApprovedOnDate())) {
			final String errorMessage = "The date on which a loan is disbursed cannot be before its approval date: "
					+ getApprovedOnDate().toString();
			throw new InvalidLoanStateTransitionException("disbursal",
					"cannot.be.before.approval.date", errorMessage,
					disbursedOn, getApprovedOnDate());
		}

		if (disbursedOn.isAfter(new LocalDate())) {
			final String errorMessage = "The date on which a loan is disbursed cannot be in the future.";
			throw new InvalidLoanStateTransitionException("disbursal",
					"cannot.be.a.future.date", errorMessage, disbursedOn);
		}

		LocalDate firstRepaymentDueDate = this.repaymentScheduleInstallments
				.get(0).getDueDate();
		if (disbursedOn.isAfter(firstRepaymentDueDate)) {
			final String errorMessage = "The date on which a loan is disbursed cannot be after the first expected repayment date: "
					+ firstRepaymentDueDate.toString();
			throw new InvalidLoanStateTransitionException("disbursal",
					"cannot.be.after.first.repayment.due.date", errorMessage,
					disbursedOn, firstRepaymentDueDate);
		}
	}

	public void undoDisbursal(
			LoanLifecycleStateMachine loanLifecycleStateMachine) {
		
		LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_DISBURSAL_UNDO, LoanStatus.fromInt(this.loanStatus));
		this.loanStatus = statusEnum.getValue();
		
		this.loanTransactions.clear();
		this.disbursedOnDate = null;
	}

	public void waive(final LoanTransaction loanTransaction, final LoanLifecycleStateMachine loanLifecycleStateMachine) {
		
		LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_REPAYMENT, LoanStatus.fromInt(this.loanStatus));
		this.loanStatus = statusEnum.getValue();
		
		loanTransaction.updateLoan(this);
		this.loanTransactions.add(loanTransaction);

		LocalDate loanTransactionDate = loanTransaction.getTransactionDate();
		if (loanTransactionDate.isBefore(this.getDisbursementDate())) {
			final String errorMessage = "The transaction date cannot be before the loan disbursement date: "
					+ getApprovedOnDate().toString();
			throw new InvalidLoanStateTransitionException("waive",
					"cannot.be.before.disbursement.date", errorMessage,
					loanTransactionDate, this.getDisbursementDate());
		}

		if (loanTransactionDate.isAfter(new LocalDate())) {
			final String errorMessage = "The transaction date cannot be in the future.";
			throw new InvalidLoanStateTransitionException("waive",
					"cannot.be.a.future.date", errorMessage, loanTransactionDate);
		}

		if (getTotalOutstanding().isGreaterThan(this.getInArrearsTolerance())) {
			final String errorMessage = "Waiver is only allowed when the total outstanding amount left on loan ("
					+ getTotalOutstanding()
					+ ") is less than the in arrears tolerance setting of "
					+ getInArrearsTolerance().getAmount();
			throw new InvalidLoanStateTransitionException("waive",
					"cannot.exceed.in.arrears.tolerance.setting", errorMessage,
					getTotalOutstanding(), getInArrearsTolerance());
		}

		Money waived = Money.of(getCurrency(), loanTransaction.getAmount());
		if (waived.isGreaterThan(this.getInArrearsTolerance())) {
			final String errorMessage = "The amount being waived cannot exceed the in arrears tolerance setting of "
					+ getInArrearsTolerance().getAmount();
			throw new InvalidLoanStateTransitionException("waive",
					"cannot.exceed.in.arrears.tolerance.setting", errorMessage,
					waived, getInArrearsTolerance());
		}

		doPostLoanTransactionChecks(loanTransaction, loanLifecycleStateMachine);
	}

	public void makeRepayment(final LoanTransaction loanTransaction, final LoanLifecycleStateMachine loanLifecycleStateMachine) {
		handleRepaymentTransaction(loanTransaction, loanLifecycleStateMachine, false);
	}

	private void handleRepaymentTransaction(
			final LoanTransaction loanTransaction,
			final LoanLifecycleStateMachine loanLifecycleStateMachine, final boolean adjusted) {
		
		LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_REPAYMENT, LoanStatus.fromInt(this.loanStatus));
		this.loanStatus = statusEnum.getValue();
		
		loanTransaction.updateLoan(this);
		
		boolean isTransactionChronologicallyLatest = isChronologicallyLatestRepaymentOrWaiver(loanTransaction, this.loanTransactions);
		
		this.loanTransactions.add(loanTransaction);

		if (loanTransaction.isNotRepayment()) {
			final String errorMessage = "A transaction of type repayment was expected but not received.";
			throw new InvalidLoanTransactionTypeException("transaction",
					"is.not.a.repayment.transaction", errorMessage);
		}

		LocalDate loanTransactionDate = loanTransaction.getTransactionDate();
		if (loanTransactionDate.isBefore(this.getDisbursementDate())) {
			final String errorMessage = "The transaction date cannot be before the loan disbursement date: "
					+ getApprovedOnDate().toString();
			throw new InvalidLoanStateTransitionException("repayment",
					"cannot.be.before.disbursement.date", errorMessage,
					loanTransactionDate, this.getDisbursementDate());
		}

		if (loanTransactionDate.isAfter(new LocalDate())) {
			final String errorMessage = "The transaction date cannot be in the future.";
			throw new InvalidLoanStateTransitionException("repayment",
					"cannot.be.a.future.date", errorMessage, loanTransactionDate);
		}
		
		List<LoanTransaction> repaymentsOrWaivers = new ArrayList<LoanTransaction>();
		for (LoanTransaction transaction : this.loanTransactions) {
			if (!transaction.isDisbursement() && transaction.isNotContra()) {
				repaymentsOrWaivers.add(transaction);
			}
		}
		LoanTransactionComparator transactionComparator = new LoanTransactionComparator();
		Collections.sort(repaymentsOrWaivers, transactionComparator);
		
		
		final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessor.determineProcessor(this.transactionProcessingStrategy);
		if (isTransactionChronologicallyLatest && !adjusted) {
			loanRepaymentScheduleTransactionProcessor.handleTransaction(loanTransaction, getCurrency(), this.repaymentScheduleInstallments);
		} else {
			loanRepaymentScheduleTransactionProcessor.handleTransaction(repaymentsOrWaivers, getCurrency(), this.repaymentScheduleInstallments);
		}
		
		doPostLoanTransactionChecks(loanTransaction, loanLifecycleStateMachine);
	}

	private void doPostLoanTransactionChecks(final LoanTransaction loanTransaction, final LoanLifecycleStateMachine loanLifecycleStateMachine) {
		
		if (this.isOverPaid()) {
			handleLoanOverpayment(loanTransaction, loanLifecycleStateMachine);
		} else if (this.isRepaidInFull()) {
			handleLoanRepaymentInFull(loanTransaction, loanLifecycleStateMachine);
		}
	}

	private void handleLoanRepaymentInFull(final LoanTransaction loanTransaction, final LoanLifecycleStateMachine loanLifecycleStateMachine) {
		
		LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.REPAID_IN_FULL, LoanStatus.fromInt(this.loanStatus));
		this.loanStatus = statusEnum.getValue();
		
		this.closedOnDate = loanTransaction.getTransactionDate().toDate();
		this.maturedOnDate = loanTransaction.getTransactionDate().toDate();

		if (isInterestRebateAllowed()) {
			Money rebateDue = calculateRebateWhenPaidInFullOn(loanTransaction.getTransactionDate());
			if (rebateDue.isGreaterThanZero()) {
				statusEnum = loanLifecycleStateMachine.transition(LoanEvent.INTERST_REBATE_OWED, LoanStatus.fromInt(this.loanStatus));
				this.loanStatus = statusEnum.getValue();
				
				this.interestRebateOwed = rebateDue.getAmount();
			}
		}
	}

	private void handleLoanOverpayment(@SuppressWarnings("unused") final LoanTransaction loanTransaction, final LoanLifecycleStateMachine loanLifecycleStateMachine) {
		
		LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_OVERPAYMENT, LoanStatus.fromInt(this.loanStatus));
		this.loanStatus = statusEnum.getValue();
		
		this.closedOnDate = null;
		this.maturedOnDate = null;
	}

	private boolean isChronologicallyLatestRepaymentOrWaiver(
			final LoanTransaction loanTransaction,
			final List<LoanTransaction> loanTransactions) {
		
		boolean isChronologicallyLatestRepaymentOrWaiver = true;
		
		LocalDate currentTransactionDate = loanTransaction.getTransactionDate();
		for (LoanTransaction previousTransaction : loanTransactions) {
			if (!previousTransaction.isDisbursement() && previousTransaction.isNotContra()) {
				if (currentTransactionDate.isBefore(previousTransaction.getTransactionDate()) ||
						currentTransactionDate.isEqual(previousTransaction.getTransactionDate())) {
					isChronologicallyLatestRepaymentOrWaiver = false;
					break;
				}
			}
		}
		
		return isChronologicallyLatestRepaymentOrWaiver;
	}

	public LocalDate possibleNextRepaymentDate() {
		LocalDate earliestUnpaidInstallmentDate = new LocalDate();
		for (LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
			if (installment.isNotFullyCompleted()) {
				earliestUnpaidInstallmentDate = installment.getDueDate();
				break;
			}
		}

		LocalDate lastTransactionDate = null;
		for (LoanTransaction transaction : this.loanTransactions) {
			if (transaction.isRepayment() && transaction.isNonZero()) {
				lastTransactionDate = transaction.getTransactionDate();
			}
		}

		LocalDate possibleNextRepaymentDate = earliestUnpaidInstallmentDate;
		if (lastTransactionDate != null
				&& lastTransactionDate.isAfter(earliestUnpaidInstallmentDate)) {
			possibleNextRepaymentDate = lastTransactionDate;
		}

		return possibleNextRepaymentDate;
	}

	public Money possibleNextRepaymentAmount() {
		MonetaryCurrency currency = this.loanRepaymentScheduleDetail
				.getPrincipal().getCurrency();
		Money possibleNextRepaymentAmount = Money.zero(currency);

		for (LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
			if (installment.isNotFullyCompleted()) {
				possibleNextRepaymentAmount = installment.getTotalDue(currency);
				break;
			}
		}

		return possibleNextRepaymentAmount;
	}

	public void adjustExistingTransaction(
			LoanTransaction transactionForAdjustment,
			LoanTransaction newTransactionDetail,
			LoanLifecycleStateMachine loanLifecycleStateMachine) {

		if (transactionForAdjustment.isNotRepayment()
				&& transactionForAdjustment.isNotWaiver()) {
			final String errorMessage = "A transaction of type repayment or waiver was expected but not received.";
			throw new InvalidLoanTransactionTypeException("transaction",
					"is.not.a.repayment.or.waiver.transaction", errorMessage);
		}

		transactionForAdjustment.contra();
		if (newTransactionDetail.isRepayment()) {
			handleRepaymentTransaction(newTransactionDetail, loanLifecycleStateMachine, true);
		}

		if (newTransactionDetail.isWaiver()) {
			waive(newTransactionDetail, loanLifecycleStateMachine);
		}
	}

	private boolean isRepaidInFull() {
		
		MonetaryCurrency currency = loanCurrency();
		
		Money cumulativeOriginalPrincipalExpected = Money.zero(currency);
		Money cumulativeOriginalInterestExpected = Money.zero(currency);
		Money cumulativeOriginalTotalExpected = Money.zero(currency);
		
		Money cumulativeTotalPaid = Money.zero(currency);
		Money cumulativeTotalWaived = Money.zero(currency);
		
		for (LoanRepaymentScheduleInstallment scheduledRepayment : this.repaymentScheduleInstallments) {
			cumulativeOriginalPrincipalExpected = cumulativeOriginalPrincipalExpected.plus(scheduledRepayment.getPrincipal(currency));
			cumulativeOriginalInterestExpected = cumulativeOriginalInterestExpected.plus(scheduledRepayment.getInterest(currency));
			
			cumulativeTotalPaid = cumulativeTotalPaid.plus(scheduledRepayment.getPrincipalCompleted(currency).plus(scheduledRepayment.getInterestCompleted(currency)));
			cumulativeTotalWaived = cumulativeTotalWaived.plus(scheduledRepayment.getInterestWaived(currency));
		}
		
		cumulativeOriginalTotalExpected = cumulativeOriginalPrincipalExpected.plus(cumulativeOriginalInterestExpected);
		
		Money totalOutstanding = cumulativeOriginalTotalExpected.minus(cumulativeTotalPaid.plus(cumulativeTotalWaived));
		boolean isRepaidInFull = totalOutstanding.isZero();
		
		return isRepaidInFull;
	}
	
	private boolean isOverPaid() {
		
		Money totalPaidInRepayments = getTotalPaidInRepayments();
		
		MonetaryCurrency currency = loanCurrency();

		Money cumulativeOriginalExpectedTotal = Money.zero(currency);
		Money cumulativeTotalPaidOnInstallments = Money.zero(currency);
		Money cumulativeTotalWaivedOnInstallments = Money.zero(currency);
		
		for (LoanRepaymentScheduleInstallment scheduledRepayment : this.repaymentScheduleInstallments) {
			cumulativeOriginalExpectedTotal = cumulativeOriginalExpectedTotal.plus(scheduledRepayment.getPrincipal(currency));
			cumulativeTotalPaidOnInstallments = cumulativeTotalPaidOnInstallments.plus(scheduledRepayment.getPrincipalCompleted(currency).plus(scheduledRepayment.getInterestCompleted(currency)));
			cumulativeTotalWaivedOnInstallments = cumulativeTotalWaivedOnInstallments.plus(scheduledRepayment.getInterestWaived(currency));
		}
		
		return totalPaidInRepayments.isGreaterThan(cumulativeTotalPaidOnInstallments);
	}

	private MonetaryCurrency loanCurrency() {
		return this.loanRepaymentScheduleDetail.getCurrency();
	}

	public void writeOff(final DateTime writtenOffOn,
			final LoanLifecycleStateMachine loanLifecycleStateMachine) {
		
		LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_WRITE_OFF, LoanStatus.fromInt(this.loanStatus));
		this.loanStatus = statusEnum.getValue();
		
		this.closedOnDate = writtenOffOn.toDate();
		this.writtenOffOnDate = writtenOffOn.toDate();

		LocalDate writtenOffOnLocalDate = new LocalDate(writtenOffOnDate);
		if (writtenOffOnLocalDate.isBefore(this.getDisbursementDate())) {
			final String errorMessage = "The date on which a loan is withdrawn cannot be before the loan disbursement date: "
					+ getDisbursementDate().toString();
			throw new InvalidLoanStateTransitionException("writeoff",
					"cannot.be.before.submittal.date", errorMessage,
					writtenOffOnLocalDate, getDisbursementDate());
		}
		if (writtenOffOnLocalDate.isAfter(new LocalDate())) {
			final String errorMessage = "The date on which a loan is written off cannot be in the future.";
			throw new InvalidLoanStateTransitionException("writeoff",
					"cannot.be.a.future.date", errorMessage,
					writtenOffOnLocalDate);
		}
	}

	public void reschedule(final DateTime rescheduledOn,
			final LoanLifecycleStateMachine loanLifecycleStateMachine) {
		
		LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_RESCHEDULE, LoanStatus.fromInt(this.loanStatus));
		this.loanStatus = statusEnum.getValue();
		
		this.closedOnDate = rescheduledOn.toDate();
		this.rescheduledOnDate = rescheduledOn.toDate();

		LocalDate rescheduledOnLocalDate = new LocalDate(rescheduledOnDate);
		if (rescheduledOnLocalDate.isBefore(this.getDisbursementDate())) {
			final String errorMessage = "The date on which a loan is rescheduled cannot be before the loan disbursement date: "
					+ getDisbursementDate().toString();
			throw new InvalidLoanStateTransitionException("writeoff",
					"cannot.be.before.submittal.date", errorMessage,
					rescheduledOnLocalDate, getDisbursementDate());
		}
		if (rescheduledOnLocalDate.isAfter(new LocalDate())) {
			final String errorMessage = "The date on which a loan is rescheduled cannot be in the future.";
			throw new InvalidLoanStateTransitionException("writeoff",
					"cannot.be.a.future.date", errorMessage,
					rescheduledOnLocalDate);
		}
	}

	public boolean isNotSubmittedAndPendingApproval() {
		return !isSubmittedAndPendingApproval();
	}
	
	private LoanStatus status() {
		return LoanStatus.fromInt(this.loanStatus);
	}

	public boolean isSubmittedAndPendingApproval() {
		return status().isSubmittedAndPendingApproval();
	}

	public boolean isApproved() {
		return status().isApproved();
	}

	public boolean isNotApproved() {
		return !isApproved();
	}

	public boolean isWaitingForDisbursal() {
		return this.isApproved() && this.isNotDisbursed();
	}

	public boolean isNotDisbursed() {
		return !this.isDisbursed();
	}

	public boolean isDisbursed() {
		return hasDisbursementTransaction();
	}

	public boolean isUndoDisbursalAllowed() {
		return isDisbursed() && this.hasNoRepaymentTransaction();
	}

	public boolean isClosed() {
		return status().isClosed() || this.isCancelled();
	}

	public boolean isCancelled() {
		return this.isRejected() || this.isWithdrawn();
	}

	public boolean isWithdrawn() {
		return status().isWithdrawnByClient();
	}

	public boolean isRejected() {
		return status().isRejected();
	}

	public boolean isNotClosed() {
		return !this.isClosed();
	}

	public boolean isOpen() {
		return status().isActive();
	}

	public boolean isOpenWithNoRepaymentMade() {
		return this.isOpen() && hasNoRepaymentTransaction();
	}

	private boolean hasNoRepaymentTransaction() {
		return !hasRepaymentTransaction();
	}

	private boolean hasRepaymentTransaction() {
		boolean hasRepaymentTransaction = false;
		for (LoanTransaction loanTransaction : this.loanTransactions) {
			if (loanTransaction.isRepayment()) {
				hasRepaymentTransaction = true;
				break;
			}
		}
		return hasRepaymentTransaction;
	}

	private boolean hasDisbursementTransaction() {
		boolean hasRepaymentTransaction = false;
		for (LoanTransaction loanTransaction : this.loanTransactions) {
			if (loanTransaction.isDisbursement()) {
				hasRepaymentTransaction = true;
				break;
			}
		}
		return hasRepaymentTransaction;
	}

	public boolean isOpenWithRepaymentMade() {
		return this.isOpen() && this.hasRepaymentTransaction();
	}

	public List<LoanRepaymentScheduleInstallment> getRepaymentScheduleInstallments() {
		return this.repaymentScheduleInstallments;
	}

	public String getExternalId() {
		return this.externalId;
	}

	public void setExternalId(final String externalSystemIdentifer) {
		if (StringUtils.isNotBlank(externalSystemIdentifer)) {
			this.externalId = externalSystemIdentifer.trim();
		} else {
			this.externalId = null;
		}
	}

	public LocalDate getSubmittedOnDate() {
		return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(
				this.submittedOnDate), null);
	}

	public LocalDate getRejectedOnDate() {
		return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(
				this.rejectedOnDate), null);
	}

	public LocalDate getApprovedOnDate() {
		LocalDate date = null;
		if (this.approvedOnDate != null) {
			date = new LocalDate(this.approvedOnDate);
		}
		return date;
	}

	public LocalDate getDisbursedOnDate() {
		LocalDate date = null;
		if (this.disbursedOnDate != null) {
			date = new LocalDate(this.disbursedOnDate);
		}
		return date;
	}

	public LocalDate getClosedOnDate() {
		LocalDate date = null;
		if (this.closedOnDate != null) {
			date = new LocalDate(this.closedOnDate);
		}
		return date;
	}

	public LocalDate getWrittenOffOnDate() {
		LocalDate date = null;
		if (this.writtenOffOnDate != null) {
			date = new LocalDate(this.writtenOffOnDate);
		}
		return date;
	}

	public Date getExpectedDisbursedOnDate() {
		return this.expectedDisbursedOnDate;
	}

	public LocalDate getExpectedDisbursedOnLocalDate() {
		LocalDate expectedDisbursementDate = null;
		if (this.expectedDisbursedOnDate != null) {
			expectedDisbursementDate = new LocalDate(
					this.expectedDisbursedOnDate);
		}
		return expectedDisbursementDate;
	}

	public LocalDate getExpectedFirstRepaymentOnDate() {
		LocalDate firstRepaymentDate = null;
		if (this.expectedFirstRepaymentOnDate != null) {
			firstRepaymentDate = new LocalDate(
					this.expectedFirstRepaymentOnDate);
		}
		return firstRepaymentDate;
	}

	public LocalDate getDisbursementDate() {
		LocalDate disbursementDate = getExpectedDisbursedOnLocalDate();
		if (this.disbursedOnDate != null) {
			disbursementDate = new LocalDate(this.disbursedOnDate);
		}
		return disbursementDate;
	}

	public LocalDate getExpectedMaturityDate() {
		LocalDate possibleMaturityDate = null;
		if (this.expectedMaturityDate != null) {
			possibleMaturityDate = new LocalDate(this.expectedMaturityDate);
		}
		return possibleMaturityDate;
	}

	public LocalDate getActualMaturityDate() {
		LocalDate possibleMaturityDate = null;
		if (this.maturedOnDate != null) {
			possibleMaturityDate = new LocalDate(this.maturedOnDate);
		}
		return possibleMaturityDate;
	}

	public LocalDate getMaturityDate() {
		LocalDate possibleMaturityDate = null;

		if (this.expectedMaturityDate != null) {
			possibleMaturityDate = new LocalDate(this.expectedMaturityDate);
		}
		if (this.maturedOnDate != null) {
			possibleMaturityDate = new LocalDate(this.maturedOnDate);
		}
		return possibleMaturityDate;
	}

    public Set<LoanCharge> getCharges() {
        return charges;
    }

    public void setCharges(Set<LoanCharge> charges) {
        this.charges = charges;
    }

    public void addRepaymentScheduleInstallment(
			final LoanRepaymentScheduleInstallment installment) {
		installment.updateLoan(this);
		this.repaymentScheduleInstallments.add(installment);
	}

	public boolean isActualDisbursedOnDateEarlierOrLaterThanExpected() {
		return isActualDisbursedOnDateEarlierOrLaterThanExpected(new LocalDate(
				this.disbursedOnDate));
	}

	public boolean isActualDisbursedOnDateEarlierOrLaterThanExpected(
			final LocalDate actualDisbursedOnDate) {
		return !new LocalDate(this.expectedDisbursedOnDate)
				.isEqual(actualDisbursedOnDate);
	}

	public boolean isFlexibleRepaymentSchedule() {
		return this.loanProduct.isFlexibleRepaymentSchedule();
	}

	public boolean isInterestRebateAllowed() {
		return this.loanProduct.isInterestRebateAllowed();
	}

	public boolean isRepaymentScheduleRegenerationRequiredForDisbursement(
			final LocalDate actualDisbursementDate) {

		boolean regenerationRequired = false;

		if (isFlexibleRepaymentSchedule()) {
			regenerationRequired = false;
		} else {
			if (isActualDisbursedOnDateEarlierOrLaterThanExpected(actualDisbursementDate)) {
				regenerationRequired = true;
			}
		}

		return regenerationRequired;
	}

	public LoanPayoffSummary getPayoffSummaryOn(
			final LocalDate projectedPayoffDate) {

		LocalDate acutalDisbursementDate = new LocalDate(this.disbursedOnDate);

		Money totalPaidToDate = this.getTotalPaidInRepayments();

		Money totalOutstandingBasedOnExpectedMaturityDate = this
				.getTotalOutstanding();
		Money totalOutstandingBasedOnPayoffDate = totalOutstandingBasedOnExpectedMaturityDate;

		Money rebateGivenOnProjectedPayoffDate = Money
				.zero(this.loanRepaymentScheduleDetail.getPrincipal()
						.getCurrency());
		if (isInterestRebateAllowed()) {
			rebateGivenOnProjectedPayoffDate = calculateRebateWhenPaidInFullOn(projectedPayoffDate);
			totalOutstandingBasedOnPayoffDate = totalOutstandingBasedOnExpectedMaturityDate
					.minus(rebateGivenOnProjectedPayoffDate);
		}

		return new LoanPayoffSummary(this.getId(), acutalDisbursementDate,
				this.getMaturityDate(), projectedPayoffDate, totalPaidToDate,
				totalOutstandingBasedOnExpectedMaturityDate,
				totalOutstandingBasedOnPayoffDate,
				rebateGivenOnProjectedPayoffDate);
	}

	public Money getTotalOutstanding() {
		return getTotalPrincipalOnLoan().plus(getTotalInterestOnLoan()).minus(getTotalPaidInRepayments());
	}

	private Money getTotalPaidInRepayments() {
		Money cumulativePaid = Money.zero(this.loanRepaymentScheduleDetail.getPrincipal().getCurrency());

		for (LoanTransaction repayment : this.loanTransactions) {
			if (repayment.isRepayment()) {
				cumulativePaid = cumulativePaid.plus(repayment.getAmount());
			}
		}

		return cumulativePaid;
	}

	public Money calculateRebateWhenPaidInFullOn(final LocalDate paidInFullDate) {

		Money loanPrincipal = this.loanRepaymentScheduleDetail.getPrincipal();
		Money rebate = Money.zero(loanPrincipal.getCurrency());

		if (this.isDisbursed()
				&& !paidInFullDate.isBefore(this.getDisbursementDate())) {

			InterestRebateCalculator interestRebateCalculator = this.interestRebateCalculatorFactory
					.createCalcualtor(this.loanRepaymentScheduleDetail
							.getInterestMethod(),
							this.loanRepaymentScheduleDetail
									.getAmortizationMethod());

			rebate = interestRebateCalculator.calculate(this
					.getDisbursementDate(), paidInFullDate, loanPrincipal,
					this.loanRepaymentScheduleDetail
							.getAnnualNominalInterestRate(),
					this.repaymentScheduleInstallments, this.loanTransactions);
		}

		return rebate;
	}

	private Money getTotalInterestOnLoan() {
		Money cumulativeInterest = Money.zero(this.loanRepaymentScheduleDetail
				.getPrincipal().getCurrency());

		for (LoanRepaymentScheduleInstallment scheduledRepayment : this.repaymentScheduleInstallments) {
			cumulativeInterest = cumulativeInterest.plus(scheduledRepayment
					.getInterest(loanCurrency()));
		}

		return cumulativeInterest;
	}

	private Money getTotalPrincipalOnLoan() {
		Money cumulativePrincipal = Money.zero(this.loanRepaymentScheduleDetail
				.getPrincipal().getCurrency());

		for (LoanRepaymentScheduleInstallment scheduledRepayment : this.repaymentScheduleInstallments) {
			cumulativePrincipal = cumulativePrincipal.plus(scheduledRepayment
					.getPrincipal(loanCurrency()));
		}

		return cumulativePrincipal;
	}

	public Money getInterestRebateOwed() {
		return Money.of(this.loanRepaymentScheduleDetail.getCurrency(),
				this.interestRebateOwed);
	}

	public Money getInArrearsTolerance() {
		return this.loanRepaymentScheduleDetail.getInArrearsTolerance();
	}

	public boolean identifiedBy(String identifier) {
		return identifier.equalsIgnoreCase(this.externalId)
				|| identifier.equalsIgnoreCase(this.getId().toString());
	}

	public MonetaryCurrency getCurrency() {
		return this.loanRepaymentScheduleDetail.getCurrency();
	}

	public LocalDate getInterestChargedFromDate() {
		LocalDate interestChargedFrom = null;
		if (this.interestChargedFromDate != null) {
			interestChargedFrom = new LocalDate(this.interestChargedFromDate);
		}
		return interestChargedFrom;
	}

	public LocalDate getLoanStatusSinceDate() {

		LocalDate statusSinceDate = getSubmittedOnDate();
		if (isApproved()) {
			statusSinceDate = new LocalDate(this.approvedOnDate);
		}

		if (isDisbursed()) {
			statusSinceDate = new LocalDate(this.disbursedOnDate);
		}

		if (isClosed()) {
			statusSinceDate = new LocalDate(this.closedOnDate);
		}

		return statusSinceDate;
	}

	public String getCurrencyCode() {
		return this.loanRepaymentScheduleDetail.getPrincipal()
				.getCurrencyCode();
	}
}