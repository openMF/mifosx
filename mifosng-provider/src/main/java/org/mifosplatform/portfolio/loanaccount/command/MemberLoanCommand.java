package org.mifosplatform.portfolio.loanaccount.command;


import java.math.BigDecimal;
import java.util.Set;

public class MemberLoanCommand {

    private final Long loanId;
    private final Long clientId;
    private final String externalId;

    private final BigDecimal principal;

    private final Set<String> modifiedParameters;

    public MemberLoanCommand(Set<String> modifiedParameters, Long loanId, Long clientId, String externalId, BigDecimal principal) {
        this.modifiedParameters = modifiedParameters;
        this.loanId = loanId;
        this.clientId = clientId;
        this.externalId = externalId;
        this.principal = principal;
    }

    public Long getLoanId() {
        return loanId;
    }

    public Long getClientId() {
        return clientId;
    }

    public String getExternalId() {
        return externalId;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public Set<String> getModifiedParameters() {
        return modifiedParameters;
    }
}
