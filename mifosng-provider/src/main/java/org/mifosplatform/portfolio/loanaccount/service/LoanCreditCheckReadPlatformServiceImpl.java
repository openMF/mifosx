/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.portfolio.creditcheck.data.CreditCheckEnumerations;
import org.mifosplatform.portfolio.creditcheck.service.CreditCheckDropdownReadPlatformService;
import org.mifosplatform.portfolio.loanaccount.data.LoanCreditCheckData;
import org.mifosplatform.portfolio.loanaccount.data.LoanCreditCheckDataTimelineData;
import org.mifosplatform.portfolio.loanaccount.exception.LoanCreditCheckNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class LoanCreditCheckReadPlatformServiceImpl implements LoanCreditCheckReadPlatformService {
    private final JdbcTemplate jdbcTemplate;
    private final CreditCheckDropdownReadPlatformService creditCheckDropdownReadPlatformService;
    
    @Autowired
    public LoanCreditCheckReadPlatformServiceImpl(final RoutingDataSource dataSource, 
            final CreditCheckDropdownReadPlatformService creditCheckDropdownReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.creditCheckDropdownReadPlatformService = creditCheckDropdownReadPlatformService;
    }

    @Override
    public Collection<LoanCreditCheckData> retrieveLoanCreditChecks(Long loanId) {
        final LoanCreditCheckMapper mapper = new LoanCreditCheckMapper();
        final String sql = "select " + mapper.loanCreditCheckSchema() + " where lcc.loan_id = ?";
        
        return this.jdbcTemplate.query(sql, mapper, new Object[] { loanId });
    }

    @Override
    public LoanCreditCheckData retrieveLoanCreditCheckEnumOptions() {
        final List<EnumOptionData> severityLevelOptions = this.creditCheckDropdownReadPlatformService.retrieveSeverityLevelOptions();
        
        return LoanCreditCheckData.instance(severityLevelOptions);
    }

    @Override
    public LoanCreditCheckData retrieveLoanCreditCheck(Long loanId, Long loanCreditCheckId) {
        try {
            final LoanCreditCheckMapper mapper = new LoanCreditCheckMapper();
            final String sql = "select " + mapper.loanCreditCheckSchema() + " where lcc.loan_id = ? and lcc.id = ?";
            
            return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { loanId, loanCreditCheckId });
        }
        
        catch (final EmptyResultDataAccessException e) {
            throw new LoanCreditCheckNotFoundException(loanCreditCheckId);
        }
    }
    
    private static final class LoanCreditCheckMapper implements RowMapper<LoanCreditCheckData> {
        
        public String loanCreditCheckSchema() {
            return "lcc.id, lcc.credit_check_id as creditCheckId, cc.name as name, "
                    + "lcc.expected_result as expectedResult, lcc.actual_result as actualResult, "
                    + "lcc.severity_level_enum_value as severityLevelEnumValue, "
                    + "lcc.has_been_triggered as hasBeenTriggered, lcc.triggered_on_date as triggeredOnDate, "
                    + "lcc.message, tbu.username as triggeredByUsername, tbu.firstname as triggeredByFirstname, "
                    + "tbu.lastname as triggeredByLastname, lcc.is_active as isActive "
                    + "from m_loan_credit_check lcc "
                    + "inner join m_credit_check cc "
                    + "on cc.id = lcc.credit_check_id "
                    + "left join m_appuser tbu "
                    + "on tbu.id = lcc.triggered_by_user_id";
        }

        @Override
        public LoanCreditCheckData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final Long creditCheckId = rs.getLong("creditCheckId");
            final String name = rs.getString("name");
            final String expectedResult = rs.getString("expectedResult");
            final String actualResult = rs.getString("actualResult");
            final Integer severityLevelEnumValue = JdbcSupport.getInteger(rs, "severityLevelEnumValue");
            final boolean hasBeenTriggered = rs.getBoolean("hasBeenTriggered");
            final String message = rs.getString("message");
            final LocalDate triggeredOnDate = JdbcSupport.getLocalDate(rs, "triggeredOnDate");
            final String triggeredByUsername = rs.getString("triggeredByUsername");
            final String triggeredByFirstname = rs.getString("triggeredByFirstname");
            final String triggeredByLastname = rs.getString("triggeredByLastname");
            final boolean isActive = rs.getBoolean("isActive");
            EnumOptionData severityLevelEnum = null;
            
            if (severityLevelEnumValue != null) {
                severityLevelEnum = CreditCheckEnumerations.severityLevel(severityLevelEnumValue);
            }
            
            final LoanCreditCheckDataTimelineData timeline = new LoanCreditCheckDataTimelineData(triggeredOnDate, 
                    triggeredByUsername, triggeredByFirstname, triggeredByLastname);
            
            return LoanCreditCheckData.instance(id, creditCheckId, name, expectedResult, actualResult, 
                    severityLevelEnum, message, isActive, hasBeenTriggered, timeline);
        }
    }
}
