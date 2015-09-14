/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.joda.time.LocalDate;
import org.joda.time.MonthDay;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.portfolio.charge.data.ChargeData;
import org.mifosplatform.portfolio.charge.service.ChargeEnumerations;
import org.mifosplatform.portfolio.client.data.ClientChargeData;
import org.mifosplatform.portfolio.client.data.ClientRecurringChargeData;
import org.mifosplatform.portfolio.common.service.CommonEnumerations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ClientRecurringChargeReadPlatformServiceImpl implements ClientRecurringChargeReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public ClientRecurringChargeReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public static final class ClientRecurringChargeMapper implements RowMapper<ClientRecurringChargeData> {

        @Override
        public ClientRecurringChargeData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final Long chargeId = rs.getLong("chargeId");
            final Long clientId = rs.getLong("clientId");
            final String name = rs.getString("name");

            final BigDecimal amount = rs.getBigDecimal("amount");
            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDecimalPlaces = JdbcSupport.getInteger(rs, "currencyDecimalPlaces");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");

            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDecimalPlaces, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);

            final int chargeAppliesTo = rs.getInt("chargeAppliesTo");
            final EnumOptionData chargeAppliesToType = ChargeEnumerations.chargeAppliesTo(chargeAppliesTo);

            final int chargeTime = rs.getInt("chargeTime");
            final EnumOptionData chargeTimeType = ChargeEnumerations.chargeTimeType(chargeTime);

            final int chargeCalculation = rs.getInt("chargeCalculation");
            final EnumOptionData chargeCalculationType = ChargeEnumerations.chargeCalculationType(chargeCalculation);

            final int paymentMode = rs.getInt("chargePaymentMode");
            final EnumOptionData chargePaymentMode = ChargeEnumerations.chargePaymentMode(paymentMode);

            final boolean penalty = rs.getBoolean("penalty");
            final boolean active = rs.getBoolean("active");

            final Integer feeInterval = JdbcSupport.getInteger(rs, "feeInterval");
            EnumOptionData feeFrequencyType = null;
            final Integer feeFrequency = JdbcSupport.getInteger(rs, "feeFrequency");
            if (feeFrequency != null) {
                feeFrequencyType = CommonEnumerations.termFrequencyType(feeFrequency, "feeFrequency");
            }
            MonthDay feeOnMonthDay = null;
            final Integer feeOnMonth = JdbcSupport.getInteger(rs, "feeOnMonth");
            final Integer feeOnDay = JdbcSupport.getInteger(rs, "feeOnDay");
            if (feeOnDay != null && feeOnMonth != null) {
                feeOnMonthDay = new MonthDay(feeOnMonth, feeOnDay);
            }
            final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "dueAsOfDate");
            final LocalDate inactivationDate = JdbcSupport.getLocalDate(rs, "inactivationDate");
            final BigDecimal minCap = rs.getBigDecimal("minCap");
            final BigDecimal maxCap = rs.getBigDecimal("maxCap");

            return ClientRecurringChargeData.instance(id, clientId, chargeId, name, active, penalty, currency, amount, dueDate,
                    chargeTimeType, chargeCalculationType, chargeAppliesToType, chargePaymentMode, feeOnMonthDay, feeInterval, minCap,
                    maxCap, feeFrequencyType, inactivationDate);
        }

        public String schema() {
            return " crc.id as id,crc.charge_Id as chargeId,crc.client_Id as clientId ,crc.charge_due_date as dueAsOfDate,crc.name as name, crc.amount as amount, crc.currency_code as currencyCode, "
                    + "crc.charge_applies_to_enum as chargeAppliesTo, crc.charge_time_enum as chargeTime, "
                    + "crc.charge_payment_mode_enum as chargePaymentMode, "
                    + "crc.charge_calculation_enum as chargeCalculation, crc.is_penalty as penalty, "
                    + "crc.is_active as active, oc.name as currencyName,crc.inactivated_on_date as inactivationDate,oc.decimal_places as currencyDecimalPlaces, "
                    + "oc.currency_multiplesof as inMultiplesOf, oc.display_symbol as currencyDisplaySymbol, "
                    + "oc.internationalized_name_code as currencyNameCode, crc.fee_on_day as feeOnDay, crc.fee_on_month as feeOnMonth, "
                    + "crc.fee_interval as feeInterval, crc.fee_frequency as feeFrequency,crc.min_cap as minCap,crc.max_cap as maxCap "
                    + "from m_client_recurring_charge crc join m_organisation_currency oc on crc.currency_code=oc.code ";
        }
    }

    @Override
    public Collection<ClientRecurringChargeData> retrieveClientRecurringCharges(Long clientId) {
        this.context.authenticatedUser();
        final ClientRecurringChargeMapper rm = new ClientRecurringChargeMapper();
        final StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select ").append(rm.schema()).append(" where crc.client_id=? ");
        sqlBuilder.append(" order by crc.charge_time_enum ASC, crc.charge_due_date ASC, crc.is_penalty ASC ");
        return this.jdbcTemplate.query(sqlBuilder.toString(), rm, new Object[] { clientId });
    }

}
