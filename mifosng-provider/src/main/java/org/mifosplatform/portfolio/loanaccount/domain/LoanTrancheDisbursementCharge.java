package org.mifosplatform.portfolio.loanaccount.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="m_loan_tranche_disbursement_charge")
public class LoanTrancheDisbursementCharge extends AbstractPersistable<Long> {
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_charge_id", referencedColumnName = "id", nullable = false)
    private LoanCharge loancharge;
    
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name="disbursement_detail_id", referencedColumnName = "id", nullable = false)
    private LoanDisbursementDetails loanDisbursementDetails; 
    
    public LoanTrancheDisbursementCharge(){
        
    }
    
    public LoanTrancheDisbursementCharge(final LoanCharge loanCharge, final LoanDisbursementDetails loanDisbursementDetails){
        this.loancharge = loanCharge;
        this.loanDisbursementDetails = loanDisbursementDetails;
    }
    
    public LoanDisbursementDetails getloanDisbursementDetails(){
        return this.loanDisbursementDetails;
    }

}