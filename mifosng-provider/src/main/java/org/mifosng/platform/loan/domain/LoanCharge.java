package org.mifosng.platform.loan.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mifosng.platform.api.commands.LoanChargeCommand;
import org.mifosng.platform.charge.domain.Charge;
import org.mifosng.platform.charge.domain.ChargeCalculationType;
import org.mifosng.platform.charge.domain.ChargeTimeType;
import org.mifosng.platform.currency.domain.Money;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity 
@Table(name = "m_loan_charge")
public class LoanCharge extends AbstractPersistable<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id", referencedColumnName = "id", nullable=false)
    private Loan loan;

    @ManyToOne(optional = false)
    @JoinColumn(name = "charge_id", referencedColumnName = "id", nullable=false)
    private Charge charge;

	@Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;

    @Column(name = "charge_time_enum", nullable = false)
    private Integer chargeTime;

    @Column(name = "charge_calculation_enum")
    private Integer chargeCalculation;

    public static LoanCharge createNew(final Loan loan, final Charge chargeDefinition, final LoanChargeCommand command) {
        return new LoanCharge(loan, chargeDefinition, command);
    }

	public static LoanCharge createNew(final Charge chargeDefinition, final LoanChargeCommand command) {
		return new LoanCharge(null, chargeDefinition, command);
	}
	
	public static LoanCharge createNew(final Charge chargeDefinition) {
		return new LoanCharge(null, chargeDefinition);
	}

    protected LoanCharge() {
    	//
    }

    private LoanCharge(final Loan loan, final Charge chargeDefinition, final LoanChargeCommand command) {
        this.loan = loan;
        this.charge = chargeDefinition;

        if (command.isAmountChanged()){
            this.amount = command.getAmount();
        } else {
            this.amount = chargeDefinition.getAmount();
        }

        if (command.isChargeTimeTypeChanged()){
            this.chargeTime = command.getChargeTimeType();
        } else {
            this.chargeTime = chargeDefinition.getChargeTime();
        }

        if (command.isChargeCalculationTypeChanged()){
            this.chargeCalculation = command.getChargeCalculationType();
        } else {
            this.chargeCalculation = chargeDefinition.getChargeCalculation();
        }
    }

    private LoanCharge(final Loan loan, final Charge chargeDefinition) {
        this(loan, chargeDefinition, chargeDefinition.getAmount(), chargeDefinition.getChargeTime(), chargeDefinition.getChargeCalculation());
    }

    private LoanCharge(final Loan loan, final Charge charge, final BigDecimal amount, final Integer chargeTime, final Integer chargeCalculation) {
        this.loan = loan;
        this.charge = charge;
        this.amount = amount;
        this.chargeTime = chargeTime;
        this.chargeCalculation = chargeCalculation;
    }

	public void update(final Loan loan) {
		this.loan = loan;
	}

    public void update(final LoanChargeCommand command){

        if (command.isAmountChanged()) {
            this.amount = command.getAmount();
        }

        if (command.isChargeTimeTypeChanged()){
            this.chargeTime = ChargeTimeType.fromInt(command.getChargeTimeType()).getValue();
        }

        if (command.isChargeCalculationTypeChanged()){
            this.chargeCalculation = ChargeCalculationType.fromInt(command.getChargeCalculationType()).getValue();
        }

    }

	public boolean isDueAtDisbursement() {
		return ChargeTimeType.fromInt(this.chargeTime).equals(ChargeTimeType.DISBURSEMENT);
	}

	public BigDecimal calculateMonetaryAmount(final Money principalDisbursed) {
		BigDecimal calculatedAmount = BigDecimal.ZERO;
		
		if (ChargeCalculationType.fromInt(chargeCalculation).equals(ChargeCalculationType.FLAT)) {
			calculatedAmount = this.amount;
		} else if (ChargeCalculationType.fromInt(chargeCalculation).equals(ChargeCalculationType.PERCENT_OF_AMOUNT)) {
			
			BigDecimal amountAsPercentageFactor = BigDecimal.ZERO;
			if (isGreaterThanZero(this.amount)) {
				amountAsPercentageFactor = this.amount.divide(BigDecimal.valueOf(Double.valueOf("100")));
			}
			
			calculatedAmount = principalDisbursed.multiplyRetainScale(amountAsPercentageFactor, RoundingMode.HALF_EVEN).getAmount();
		}
		
		return calculatedAmount;
	}
	
	public BigDecimal calculateMonetaryAmount() {
		return calculateMonetaryAmount(this.loan.getPrincpal());
	}

	private boolean isGreaterThanZero(final BigDecimal value) {
		return value.compareTo(BigDecimal.ZERO) == 1;
	}

	public LoanChargeCommand toData() {
		Set<String> modifiedParameters = new HashSet<String>();
		return new LoanChargeCommand(modifiedParameters, this.getId(), this.loan.getId(), this.charge.getId(),
                this.amount, this.chargeTime, this.chargeCalculation);
	}
}