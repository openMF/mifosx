/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.LocalDate;
import org.mifosplatform.portfolio.creditcheck.domain.CreditCheck;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

@SuppressWarnings("serial")
@Entity
@Table(name = "m_loan_credit_check")
public class LoanCreditCheck extends AbstractPersistable<Long> {
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "credit_check_id", nullable = false)
    private CreditCheck creditCheck;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;
    
    @Column(name = "expected_result", nullable = false)
    private String expectedResult;
    
    @Column(name = "actual_result", nullable = true)
    private String actualResult;
    
    @Column(name = "severity_level_enum_value", nullable = false)
    private Integer severityLevelEnumValue;
    
    @Column(name = "has_been_triggered", nullable = false)
    private boolean hasBeenTriggered;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "triggered_on_date", nullable = false)
    private Date triggeredOnDate;
    
    @ManyToOne
    @JoinColumn(name = "triggered_by_user_id", nullable = false)
    private AppUser triggeredByUser;
    
    @Column(name = "message", nullable = false)
    private String message;
    
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    protected LoanCreditCheck() {}
    
    private LoanCreditCheck(final CreditCheck creditCheck, final Loan loan, final String expectedResult, 
            final String actualResult, final Integer severityLevelEnumValue, final boolean hasBeenTriggered, 
            final Date triggeredOnDate, final AppUser triggeredByUser, final String message, final boolean isActive) {
        this.creditCheck = creditCheck;
        this.loan = loan;
        this.expectedResult = expectedResult;
        this.actualResult = actualResult;
        this.severityLevelEnumValue = severityLevelEnumValue;
        this.hasBeenTriggered = hasBeenTriggered;
        this.triggeredOnDate = triggeredOnDate;
        this.triggeredByUser = triggeredByUser;
        this.message = message;
        this.isActive = isActive;
    }
    
    public static LoanCreditCheck instance(final CreditCheck creditCheck, final Loan loan, final String expectedResult, 
            final String actualResult, final Integer severityLevelEnumValue, final boolean hasBeenTriggered, 
            final Date triggeredOnDate, final AppUser triggeredByUser, final String message, final boolean isActive) {
        return new LoanCreditCheck(creditCheck, loan, expectedResult, actualResult, severityLevelEnumValue, 
                hasBeenTriggered, triggeredOnDate, triggeredByUser, message, isActive);
    }
    
    public static LoanCreditCheck instance(final CreditCheck creditCheck) {
        return new LoanCreditCheck(creditCheck, null, creditCheck.getExpectedResult(), null, 
                creditCheck.getSeverityLevelEnumValue(), false, null, null, creditCheck.getMessage(), true);
    }
    
    public void update(final Loan loan) {
        this.loan = loan;
    }
    
    /** 
     * @return the creditCheck 
     **/
    public CreditCheck getCreditCheck() {
        return this.creditCheck;
    }
    
    /** 
     * @return the loan 
     **/
    public Loan getLoan() {
        return this.loan;
    }
    
    /** 
     * @return the expectedResult 
     **/
    public String getExpectedResult() {
        return this.expectedResult;
    }
    
    /** 
     * @return the actualResult 
     **/
    public String getActualResult() {
        return this.actualResult;
    }
    
    /** 
     * @return the severityLevelEnumValue 
     **/
    public Integer getSeverityLevelEnumValue() {
        return this.severityLevelEnumValue;
    }
    
    /** 
     * @return the hasBeenTriggered 
     **/
    public boolean hasBeenTriggered() {
        return this.hasBeenTriggered;
    }
    
    /** 
     * @return the triggeredOnDate 
     **/
    public Date getTriggeredOnDate() {
        return this.triggeredOnDate;
    }
    
    /** 
     * @return the triggeredByUser 
     **/
    public AppUser getTriggeredByUser() {
        return this.triggeredByUser;
    }
    
    /** 
     * @return the message 
     **/
    public String getMessage() {
        return this.message;
    }
    
    /** 
     * @return the isActive 
     **/
    public boolean isActive() {
        return this.isActive;
    }

    /** 
     * trigger the loan  credit check
     * 
     * @return None 
     **/
    public void trigger(final AppUser triggeredByUser, final LocalDate triggeredOnDate, final String creditCheckResult) {
        this.hasBeenTriggered = true;
        this.triggeredByUser = triggeredByUser;
        this.triggeredOnDate = triggeredOnDate.toDate();
        this.actualResult = creditCheckResult;
    }
    
    /** 
     * @return true if actual result equals expected result 
     **/
    public boolean actualResultEqualsExpectedResult() {
        boolean result = false;
        
        if (this.actualResult != null && this.expectedResult != null) {
            result = this.actualResult.trim().equals(this.expectedResult.trim());
        }
        
        return result;
    }
}
