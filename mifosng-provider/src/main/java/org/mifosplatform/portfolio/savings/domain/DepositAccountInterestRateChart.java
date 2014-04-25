/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.joda.time.LocalDate;
import org.mifosplatform.portfolio.interestratechart.domain.InterestRateChart;
import org.mifosplatform.portfolio.interestratechart.domain.InterestRateChartFields;
import org.mifosplatform.portfolio.interestratechart.domain.InterestRateChartSlab;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_savings_account_interest_rate_chart")
public class DepositAccountInterestRateChart extends AbstractPersistable<Long> {

    @Embedded
    private InterestRateChartFields chartFields;

    @OneToOne
    @JoinColumn(name = "savings_account_id", nullable = false)
    private SavingsAccount account;

    @OneToMany(mappedBy = "depositAccountInterestRateChart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<DepositAccountInterestRateChartSlabs> chartSlabs = new HashSet<DepositAccountInterestRateChartSlabs>();

    protected DepositAccountInterestRateChart() {
        //
    }

    public static DepositAccountInterestRateChart from(InterestRateChart productChart) {
        final Set<InterestRateChartSlab> chartSlabs = productChart.setOfChartSlabs();
        final Set<DepositAccountInterestRateChartSlabs> depostiChartSlabs = new HashSet<DepositAccountInterestRateChartSlabs>();
        for (InterestRateChartSlab interestRateChartSlab : chartSlabs) {
            depostiChartSlabs.add(DepositAccountInterestRateChartSlabs.from(interestRateChartSlab.slabFields(), null));
        }
        final DepositAccountInterestRateChart depositChart = new DepositAccountInterestRateChart(productChart.chartFields(), null, depostiChartSlabs);
        //update deposit account interest rate chart ference to chart Slabs
        depositChart.updateChartSlabsReference();
        return depositChart;
    }

    private DepositAccountInterestRateChart(InterestRateChartFields chartFields, SavingsAccount account,
            Set<DepositAccountInterestRateChartSlabs> chartSlabs) {
        this.chartFields = chartFields;
        this.account = account;
        this.chartSlabs = chartSlabs;
    }

    private void updateChartSlabsReference(){
        final Set<DepositAccountInterestRateChartSlabs> chartSlabs = setOfChartSlabs();
        for (DepositAccountInterestRateChartSlabs chartSlab : chartSlabs) {
            chartSlab.updateChartReference(this);
        }
    }
    
    public Set<DepositAccountInterestRateChartSlabs> setOfChartSlabs() {
        if (this.chartSlabs == null) {
            this.chartSlabs = new HashSet<DepositAccountInterestRateChartSlabs>();
        }
        return this.chartSlabs;
    }

    public DepositAccountInterestRateChartSlabs findChartSlab(Long chartSlabId) {
        final Set<DepositAccountInterestRateChartSlabs> chartSlabs = setOfChartSlabs();

        for (DepositAccountInterestRateChartSlabs interestRateChartSlab : chartSlabs) {
            if (interestRateChartSlab.getId().equals(chartSlabId)) { return interestRateChartSlab; }
        }
        return null;
    }

    /*private boolean removeChartSlab(DepositAccountInterestRateChartSlabs chartSlab) {
        final Set<DepositAccountInterestRateChartSlabs> chartSlabs = setOfChartSlabs();
        return chartSlabs.remove(chartSlab);
    }*/

    public LocalDate getFromDateAsLocalDate() {
        return this.chartFields.getFromDateAsLocalDate();
    }

    public LocalDate getEndDateAsLocalDate() {
        return this.chartFields.getEndDateAsLocalDate();
    }

    public Long savingsAccountId() {
        return this.account == null ? null : this.account.getId();
    }

    public SavingsAccount savingsAccount() {
        return this.account;
    }

    /*private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }*/

    public InterestRateChartFields chartFields() {
        return this.chartFields;
    }
    
    public void updateDepositAccountReference(final SavingsAccount account){
        this.account = account;
    }
    
    public BigDecimal getApplicableInterestRate(final BigDecimal depositAmount, final LocalDate periodStartDate, final LocalDate periodEndDate){
        BigDecimal effectiveInterestRate = BigDecimal.ZERO;
        for (DepositAccountInterestRateChartSlabs slab : setOfChartSlabs()) {
            if(slab.slabFields().isBetweenPeriod(periodStartDate, periodEndDate) && slab.slabFields().isAmountBetween(depositAmount)){
                effectiveInterestRate = slab.slabFields().annualInterestRate();
            }
        }
       
        return effectiveInterestRate;
    }
}