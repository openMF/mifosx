package org.mifosplatform.portfolio.loanaccount.api;


public interface LoanApiConstants {

    public static final String emiAmountParameterName = "fixedEmiAmount";
    public static final String disbursementDataParameterName = "disbursementData";
    
    public static final String disbursementDateParameterName = "expectedDisbursementDate";
    
    public static final String disbursementPrincipalParameterName = "principal";
    
    public static final String DISBURSEMENT_DATE_START_WITH_ERROR = "first.disbursement.date.must.start.with.expected.disbursement.date";
    
    public static final String PRINCIPAL_AMOUNT_SHOULD_BE_SAME = "sum.of.multi.disburse.amounts.must.equal.with.total.principal";
    public static final String disbursementIdParameterName = "id";
}
