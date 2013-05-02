/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.portfolio.charge.data.ChargeData;
import org.mifosplatform.portfolio.charge.service.ChargeDropdownReadPlatformService;
import org.mifosplatform.portfolio.charge.service.ChargeEnumerations;
import org.mifosplatform.portfolio.loanaccount.data.LoanChargeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class LoanChargeReadPlatformServiceImpl implements LoanChargeReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final ChargeDropdownReadPlatformService chargeDropdownReadPlatformService;

    @Autowired
    public LoanChargeReadPlatformServiceImpl(final PlatformSecurityContext context,
            final ChargeDropdownReadPlatformService chargeDropdownReadPlatformService, final TenantAwareRoutingDataSource dataSource) {
        this.context = context;
        this.chargeDropdownReadPlatformService = chargeDropdownReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class LoanChargeMapper implements RowMapper<LoanChargeData> {

        public String schema() {
            return "lc.id as id, c.id as chargeId, c.name as name, " + "lc.amount as amountDue, "
                    + "lc.amount_paid_derived as amountPaid, " + "lc.amount_waived_derived as amountWaived, "
                    + "lc.amount_writtenoff_derived as amountWrittenOff, " + "lc.amount_outstanding_derived as amountOutstanding, "
                    + "lc.calculation_percentage as percentageOf, lc.calculation_on_amount as amountPercentageAppliedTo, "
                    + "lc.charge_time_enum as chargeTime, " + "lc.is_penalty as penalty, "
                    + "lc.due_for_collection_as_of_date as dueAsOfDate, " + "lc.charge_calculation_enum as chargeCalculation, "
                    + "c.currency_code as currencyCode, oc.name as currencyName, "
                    + "oc.decimal_places as currencyDecimalPlaces, oc.display_symbol as currencyDisplaySymbol, "
                    + "oc.internationalized_name_code as currencyNameCode from m_charge c "
                    + "join m_organisation_currency oc on c.currency_code = oc.code " + "join m_loan_charge lc on lc.charge_id = c.id ";
        }

        @Override
        public LoanChargeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

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

            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDecimalPlaces, currencyDisplaySymbol,
                    currencyNameCode);

            final int chargeTime = rs.getInt("chargeTime");
            final EnumOptionData chargeTimeType = ChargeEnumerations.chargeTimeType(chargeTime);

            final LocalDate dueAsOfDate = JdbcSupport.getLocalDate(rs, "dueAsOfDate");

            final int chargeCalculation = rs.getInt("chargeCalculation");
            final EnumOptionData chargeCalculationType = ChargeEnumerations.chargeCalculationType(chargeCalculation);
            final boolean penalty = rs.getBoolean("penalty");

            return new LoanChargeData(id, chargeId, name, currency, amount, amountPaid, amountWaived, amountWrittenOff, amountOutstanding,
                    chargeTimeType, dueAsOfDate, chargeCalculationType, percentageOf, amountPercentageAppliedTo, penalty);
        }
    }

    @Override
    public ChargeData retrieveLoanChargeTemplate() {
        this.context.authenticatedUser();

        final List<EnumOptionData> allowedChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService.retrieveCalculationTypes();
        final List<EnumOptionData> allowedChargeTimeOptions = this.chargeDropdownReadPlatformService.retrieveCollectionTimeTypes();

        return ChargeData.template(null, allowedChargeCalculationTypeOptions, null, allowedChargeTimeOptions);
    }

    @Override
    public LoanChargeData retrieveLoanChargeDetails(final Long id, final Long loanId) {
        this.context.authenticatedUser();

        final LoanChargeMapper rm = new LoanChargeMapper();

        final String sql = "select " + rm.schema() + " where lc.id=? and lc.loan_id=?";

        return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { id, loanId });
    }

    @Override
    public Collection<LoanChargeData> retrieveLoanCharges(final Long loanId) {
        this.context.authenticatedUser();

        final LoanChargeMapper rm = new LoanChargeMapper();

        final String sql = "select " + rm.schema() + " where lc.loan_id=? "
                + " order by lc.charge_time_enum ASC, lc.due_for_collection_as_of_date ASC, lc.is_penalty ASC";

        return this.jdbcTemplate.query(sql, rm, new Object[] { loanId });
    }
}
