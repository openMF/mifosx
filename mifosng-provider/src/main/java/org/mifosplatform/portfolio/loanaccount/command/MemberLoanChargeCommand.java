package org.mifosplatform.portfolio.loanaccount.command;

import java.math.BigDecimal;
import java.util.Set;

import org.joda.time.LocalDate;

public class MemberLoanChargeCommand extends LoanChargeCommand {

    private final Long clientId;

    public MemberLoanChargeCommand(Set<String> parametersPassedInCommand, Long id, Long loanId, Long chargeId, BigDecimal amount,
            Integer chargeTimeType, Integer chargeCalculationType, LocalDate specifiedDueDate, Long clientId) {
        super(parametersPassedInCommand, id, loanId, chargeId, amount, chargeTimeType, chargeCalculationType, specifiedDueDate);
        this.clientId = clientId;
    }

    public Long getClientId() {
        return clientId;
    }
}
