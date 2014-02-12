package org.mifosplatform.portfolio.fund.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_fund_mapping_history", uniqueConstraints = { @UniqueConstraint(columnNames = { "loan_id", "fund_type_cv_id" }, name = "fund_loan_id_fundtype") })
public class FundMappingHistory extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne
    @JoinColumn(name = "fund_type_cv_id", nullable = false)
    private CodeValue fundTypeCodeValue;

    @Column(name = "start_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "locked", nullable = false)
    private Boolean locked;

    @Column(name = "create_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date createDate;

    @Column(name = "update_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date updateDate;

    public static FundMappingHistory createNewInstance(final Loan loan, final CodeValue fundTypeCodeValue) {
        return new FundMappingHistory(loan, fundTypeCodeValue, loan.getSubmittedOnDate().toDate(), null, false, null, null);
    }

    protected FundMappingHistory() {
        //
    }

    private FundMappingHistory(final Loan loan, final CodeValue fundTypeCodeValue, final Date startDate, final Date endDate,
            final Boolean locked, final Date createDate, final Date updateDate) {
        this.loan = loan;
        this.fundTypeCodeValue = fundTypeCodeValue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.locked = locked;
        this.createDate = createDate;
        this.updateDate = updateDate;

    }

    public void setEndDate(final Date endDate) {
        this.endDate = endDate;
    }

    public CodeValue getFundTypeCodeValue() {
        return this.fundTypeCodeValue;
    }
}
