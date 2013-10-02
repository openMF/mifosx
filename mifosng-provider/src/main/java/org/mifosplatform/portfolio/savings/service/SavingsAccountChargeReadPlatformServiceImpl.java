/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.MonthDay;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.portfolio.charge.data.ChargeData;
import org.mifosplatform.portfolio.charge.exception.SavingsAccountChargeNotFoundException;
import org.mifosplatform.portfolio.charge.service.ChargeDropdownReadPlatformService;
import org.mifosplatform.portfolio.charge.service.ChargeEnumerations;
import org.mifosplatform.portfolio.savings.data.SavingsAccountChargeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class SavingsAccountChargeReadPlatformServiceImpl implements SavingsAccountChargeReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final ChargeDropdownReadPlatformService chargeDropdownReadPlatformService;

    @Autowired
    public SavingsAccountChargeReadPlatformServiceImpl(final PlatformSecurityContext context,
            final ChargeDropdownReadPlatformService chargeDropdownReadPlatformService, final RoutingDataSource dataSource) {
        this.context = context;
        this.chargeDropdownReadPlatformService = chargeDropdownReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class SavingsAccountChargeMapper implements RowMapper<SavingsAccountChargeData> {

        public String schema() {
            return "sc.id as id, c.id as chargeId, c.name as name, "
                    + "sc.amount as amountDue, "
                    + "sc.amount_paid_derived as amountPaid, "
                    + "sc.amount_waived_derived as amountWaived, "
                    + "sc.amount_writtenoff_derived as amountWrittenOff, "
                    + "sc.amount_outstanding_derived as amountOutstanding, "
                    + "sc.calculation_percentage as percentageOf, sc.calculation_on_amount as amountPercentageAppliedTo, "
                    + "sc.charge_time_enum as chargeTime, "
                    + "sc.is_penalty as penalty, "
                    + "sc.charge_due_date as dueAsOfDate, "
                    + "sc.fee_on_month as feeOnMonth, "
                    + "sc.fee_on_day as feeOnDay, "
                    + "sc.charge_calculation_enum as chargeCalculation, "
                    + "c.currency_code as currencyCode, oc.name as currencyName, "
                    + "oc.decimal_places as currencyDecimalPlaces, oc.currency_multiplesof as inMultiplesOf, oc.display_symbol as currencyDisplaySymbol, "
                    + "oc.internationalized_name_code as currencyNameCode from m_charge c "
                    + "join m_organisation_currency oc on c.currency_code = oc.code "
                    + "join m_savings_account_charge sc on sc.charge_id = c.id ";
        }

        @Override
        public SavingsAccountChargeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long chargeId = rs.getLong("chargeId");
            final String name = rs.getString("name");
            final BigDecimal amount = rs.getBigDecimal("amountDue");
            final BigDecimal amountPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "amountPaid");
            final BigDecimal amountWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "amountWaived");
            final BigDecimal amountWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "amountWrittenOff");
            final BigDecimal amountOutstanding = rs.getBigDecimal("amountOutstanding");

            final BigDecimal percentageOf = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "percentageOf");
            final BigDecimal amountPercentageAppliedTo = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "amountPercentageAppliedTo");

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDecimalPlaces = JdbcSupport.getInteger(rs, "currencyDecimalPlaces");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");

            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDecimalPlaces, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);

            final int chargeTime = rs.getInt("chargeTime");
            final EnumOptionData chargeTimeType = ChargeEnumerations.chargeTimeType(chargeTime);

            final LocalDate dueAsOfDate = JdbcSupport.getLocalDate(rs, "dueAsOfDate");
            MonthDay feeOnMonthDay = null;
            final Integer feeOnMonth = JdbcSupport.getInteger(rs, "feeOnMonth");
            final Integer feeOnDay = JdbcSupport.getInteger(rs, "feeOnDay");
            if (feeOnDay != null) {
                feeOnMonthDay = new MonthDay(feeOnMonth, feeOnDay);
            }
            
            final int chargeCalculation = rs.getInt("chargeCalculation");
            final EnumOptionData chargeCalculationType = ChargeEnumerations.chargeCalculationType(chargeCalculation);
            final boolean penalty = rs.getBoolean("penalty");

            final Collection<ChargeData> chargeOptions = null;

            return SavingsAccountChargeData.instance(id, chargeId, name, currency, amount, amountPaid, amountWaived, amountWrittenOff,
                    amountOutstanding, chargeTimeType, dueAsOfDate, chargeCalculationType, percentageOf, amountPercentageAppliedTo,
                    chargeOptions, penalty, feeOnMonthDay);
        }
    }

    @Override
    public ChargeData retrieveSavingsAccountChargeTemplate() {
        this.context.authenticatedUser();

        final List<EnumOptionData> allowedChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService.retrieveCalculationTypes();
        final List<EnumOptionData> allowedChargeTimeOptions = this.chargeDropdownReadPlatformService.retrieveCollectionTimeTypes();
        final List<EnumOptionData> loansChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService
                .retrieveLoanCalculationTypes();
        final List<EnumOptionData> loansChargeTimeTypeOptions = this.chargeDropdownReadPlatformService.retrieveLoanCollectionTimeTypes();
        final List<EnumOptionData> savingsChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService
                .retrieveSavingsCalculationTypes();
        final List<EnumOptionData> savingsChargeTimeTypeOptions = this.chargeDropdownReadPlatformService
                .retrieveSavingsCollectionTimeTypes();

        // TODO AA : revisit for merge conflict - Not sure method signature
        return ChargeData.template(null, allowedChargeCalculationTypeOptions, null, allowedChargeTimeOptions, null,
                loansChargeCalculationTypeOptions, loansChargeTimeTypeOptions, savingsChargeCalculationTypeOptions,
                savingsChargeTimeTypeOptions);
    }

    @Override
    public SavingsAccountChargeData retrieveSavingsAccountChargeDetails(final Long id, final Long savingsAccountId) {
        try {
            this.context.authenticatedUser();

            final SavingsAccountChargeMapper rm = new SavingsAccountChargeMapper();

            final String sql = "select " + rm.schema() + " where sc.id=? and sc.savings_account_id=?";

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { id, savingsAccountId });
        } catch (final EmptyResultDataAccessException e) {
            throw new SavingsAccountChargeNotFoundException(savingsAccountId);
        }
    }

    @Override
    public Collection<SavingsAccountChargeData> retrieveSavingsAccountCharges(final Long loanId) {
        this.context.authenticatedUser();

        final SavingsAccountChargeMapper rm = new SavingsAccountChargeMapper();

        final String sql = "select " + rm.schema() + " where sc.savings_account_id=? "
                + " order by sc.charge_time_enum ASC, sc.charge_due_date ASC, sc.is_penalty ASC";

        return this.jdbcTemplate.query(sql, rm, new Object[] { loanId });
    }
}
