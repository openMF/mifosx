package org.mifosng.platform.loan.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.LocalDate;
import org.mifosng.platform.currency.domain.MonetaryCurrency;
import org.mifosng.platform.currency.domain.Money;
import org.mifosng.platform.infrastructure.AbstractAuditableCustom;
import org.mifosng.platform.user.domain.AppUser;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * All monetary transactions against a loan are modelled through this entity.
 * Disbursements, Repayments, Waivers, Write-off etc
 */
@Entity
@Table(name = "m_loan_transaction")
public class LoanTransaction extends AbstractAuditableCustom<AppUser, Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id", nullable=false)
    private Loan        loan;

	@Column(name = "amount", scale = 6, precision = 19, nullable = false)
	private BigDecimal amount;
	
	@Column(name = "principal_portion_derived", scale = 6, precision = 19, nullable = true)
	private BigDecimal principalPortion = BigDecimal.ZERO;
	
	@Column(name = "interest_portion_derived", scale = 6, precision = 19, nullable = true)
	private BigDecimal interestPortion = BigDecimal.ZERO;
	
	@Column(name = "fee_charges_portion_derived", scale = 6, precision = 19, nullable = true)
	private BigDecimal feeChargesPortion = BigDecimal.ZERO;
	
	@Column(name = "penalty_charges_portion_derived", scale = 6, precision = 19, nullable = true)
	private BigDecimal penaltyChargesPortion = BigDecimal.ZERO;
	
    @Temporal(TemporalType.DATE)
    @Column(name = "transaction_date", nullable=false)
    private final Date  dateOf;
    
    @Enumerated(EnumType.ORDINAL)
	@Column(name = "transaction_type_enum", nullable = false)
	private Integer typeOf;
    
    @OneToOne(optional=true, cascade={CascadeType.PERSIST})
    @JoinColumn(name="contra_id")
    private LoanTransaction contra;
    
    protected LoanTransaction() {
        this.loan = null;
        this.dateOf = null;
        this.typeOf = null;
    }
    
    public static LoanTransaction disbursement(final Money amount, final LocalDate disbursementDate) {
		return new LoanTransaction(null, LoanTransactionType.DISBURSEMENT, amount.getAmount(), disbursementDate);
	}
    
	public static LoanTransaction repayment(final Money amount, final LocalDate paymentDate) {
		return new LoanTransaction(null, LoanTransactionType.REPAYMENT, amount.getAmount(), paymentDate);
	}
	
	public static LoanTransaction repaymentAtDisbursement(final Money amount, final LocalDate paymentDate) {
		return new LoanTransaction(null, LoanTransactionType.REPAYMENT_AT_DISBURSEMENT, amount.getAmount(), paymentDate);
	}
	
	public static LoanTransaction waiver(final Loan loan, final Money waived, final LocalDate waiveDate) {
		return new LoanTransaction(loan, LoanTransactionType.WAIVE_INTEREST, waived.getAmount(), waiveDate);
	}
	
	private static LoanTransaction contra(final LoanTransaction originalTransaction) {
		LoanTransaction contra = new LoanTransaction(null, LoanTransactionType.CONTRA, originalTransaction.getAmount().negate(), new LocalDate(originalTransaction.getDateOf()));
		contra.updateContra(originalTransaction);
		return contra;
	}
	
	public static LoanTransaction writeoff(final Loan loan, final LocalDate writeOffDate) {
		return new LoanTransaction(loan, LoanTransactionType.WRITEOFF, null, writeOffDate);
	}
	
	public void updateContra(final LoanTransaction transaction) {
		this.contra = transaction;
	}

	private LoanTransaction(final Loan loan, final LoanTransactionType type, final BigDecimal amount, final LocalDate date) {
		this.loan = loan;
		this.typeOf = type.getValue();
        this.amount = amount;
		this.dateOf = date.toDateMidnight().toDate();
    }

    public BigDecimal getAmount() {
        return this.amount;
    }
    
	public Money getAmount(MonetaryCurrency currency) {
		return Money.of(currency, this.amount);
	}

    @DateTimeFormat(style="-M")
    public LocalDate getTransactionDate() {
        return new LocalDate(this.dateOf);
    }

	public Date getDateOf() {
		return dateOf;
	}

	public LoanTransactionType getTypeOf() {
		return LoanTransactionType.fromInt(this.typeOf);
	}

	public boolean isRepayment() {
		return LoanTransactionType.REPAYMENT.equals(getTypeOf()) && isNotContra();
	}
	
	public boolean isNotRepayment() {
		return !isRepayment();
	}
	
	public boolean isNotContra() {
		return this.contra == null;
	}

	public boolean isDisbursement() {
		return LoanTransactionType.DISBURSEMENT.equals(getTypeOf());
	}
	
	public boolean isRepaymentAtDisbursement() {
		return LoanTransactionType.REPAYMENT_AT_DISBURSEMENT.equals(getTypeOf());
	}
	
	public boolean isInterestWaiver() {
		return LoanTransactionType.WAIVE_INTEREST.equals(getTypeOf()) && isNotContra();
	}
	
	public boolean isNotInterestWaiver() {
		return !isInterestWaiver();
	}
	
	public boolean isWriteOff() {
		return getTypeOf().isWriteOff() && isNotContra();
	}

	public boolean isIdentifiedBy(final Long identifier) {
		return this.getId().equals(identifier);
	}
	
	public boolean isBelongingToLoanOf(final Loan check) {
		return this.loan.getId().equals(check.getId());
	}
	
	public boolean isNotBelongingToLoanOf(final Loan check) {
		return !isBelongingToLoanOf(check);
	}

	public void contra() {
		this.contra = LoanTransaction.contra(this);
		contra.updateLoan(this.loan);
	}

    public void updateLoan(final Loan loan) {
        this.loan = loan;
    }

	public boolean isNonZero() {
		return this.amount.subtract(BigDecimal.ZERO).doubleValue() > 0;
	}
	
	/**
	 * This updates the derived fields of a loan transaction for the principal, interest and interest waived portions.
	 * 
	 * This accumulates the values passed to the already existent values for each of the portions.
	 */
	public void updateComponents(final Money principal, final Money interest, final Money feeCharges, final Money penaltyCharges) {
		final MonetaryCurrency currency = principal.getCurrency();
		this.principalPortion = defaultToNullIfZero(getPrincipalPortion(currency).plus(principal).getAmount());
		this.interestPortion = defaultToNullIfZero(getInterestPortion(currency).plus(interest).getAmount());
		this.feeChargesPortion = defaultToNullIfZero(getFeeChargesPortion(currency).plus(feeCharges).getAmount());
		this.penaltyChargesPortion = defaultToNullIfZero(getPenaltyChargesPortion(currency).plus(penaltyCharges).getAmount());
	}
	
	public void updateComponentsAndTotal(final Money principal, final Money interest, final Money feeCharges, final Money penaltyCharges) {
		updateComponents(principal, interest, feeCharges, penaltyCharges);
		
		final MonetaryCurrency currency = principal.getCurrency();
		this.amount = getPrincipalPortion(currency).plus(getInterestPortion(currency)).plus(getFeeChargesPortion(currency)).plus(getPenaltyChargesPortion(currency)).getAmount();
	}

	public Money getPrincipalPortion(final MonetaryCurrency currency) {
		return Money.of(currency, this.principalPortion);
	}

	public Money getInterestPortion(final MonetaryCurrency currency) {
		return Money.of(currency, this.interestPortion);
	}
	
	public Money getFeeChargesPortion(final MonetaryCurrency currency) {
		return Money.of(currency, this.feeChargesPortion);
	}
	
	public Money getPenaltyChargesPortion(final MonetaryCurrency currency) {
		return Money.of(currency, this.penaltyChargesPortion);
	}

	public void resetDerivedComponents() {
		this.principalPortion = null;
		this.interestPortion = null;
		this.feeChargesPortion = null;
	}

	public boolean isGreaterThanZero(final MonetaryCurrency currency) {
		return getAmount(currency).isGreaterThanZero();
	}

	public boolean isNotZero(final MonetaryCurrency currency) {
		return !getAmount(currency).isZero();
	}
	
	private BigDecimal defaultToNullIfZero(final BigDecimal value) {
		BigDecimal result = value;
		if (BigDecimal.ZERO.compareTo(value) == 0) {
			result = null;
		}
		return result;
	}
}