/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.creditcheck.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.portfolio.creditcheck.data.CreditCheckData;
import org.mifosplatform.portfolio.creditcheck.data.CreditCheckEnumerations;
import org.mifosplatform.portfolio.creditcheck.domain.CreditCheckRelatedEntity;
import org.mifosplatform.portfolio.creditcheck.exception.CreditCheckNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class CreditCheckReadPlatformServiceImpl implements CreditCheckReadPlatformService {
    private final JdbcTemplate jdbcTemplate;
    private final CreditCheckDropdownReadPlatformService creditCheckDropdownReadPlatformService;
    
    @Autowired
    public CreditCheckReadPlatformServiceImpl(final RoutingDataSource dataSource, 
            final CreditCheckDropdownReadPlatformService creditCheckDropdownReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.creditCheckDropdownReadPlatformService = creditCheckDropdownReadPlatformService;
    }

    @Override
    public Collection<CreditCheckData> retrieveAllCreditChecks() {
        final CreditCheckMapper mapper = new CreditCheckMapper();
        final String sql = "select " + mapper.creditCheckSchema() + " where cc.is_deleted = 0 order by cc.name";
        
        return this.jdbcTemplate.query(sql, mapper, new Object[] {});
    }

    @Override
    public CreditCheckData retrieveCreditCheck(Long creditCheckId) {
        try {
            final CreditCheckMapper mapper = new CreditCheckMapper();
            final String sql = "select " + mapper.creditCheckSchema() + " where cc.id = ? and cc.is_deleted = 0";
            
            return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { creditCheckId });
        }
        
        catch (final EmptyResultDataAccessException e) {
            throw new CreditCheckNotFoundException(creditCheckId);
        }
    }
    
    private static final class CreditCheckMapper implements RowMapper<CreditCheckData> {
        
        public String creditCheckSchema() {
            return "cc.id, cc.name, cc.related_entity_enum_value as relatedEntityEnumValue, "
                    + "cc.expected_result as expectedResult, "
                    + "cc.severity_level_enum_value as severityLevelEnumValue, "
                    + "cc.stretchy_report_id as stretchyReportId, "
                    + "cc.stretchy_report_param_map as stretchyReportParamMap, cc.message, "
                    + "cc.is_active as isActive, cc.is_deleted as isDeleted "
                    + "from m_credit_check cc";
        }
        
        public String loanProductCreditCheckSchema() {
            return creditCheckSchema() + " inner join m_product_loan_credit_check plcc on plcc.credit_check_id = cc.id";
        }

        @Override
        public CreditCheckData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final Integer relatedEntityEnumValue = JdbcSupport.getInteger(rs, "relatedEntityEnumValue");
            final String expectedResult = rs.getString("expectedResult");
            final Integer severityLevelEnumValue = JdbcSupport.getInteger(rs, "severityLevelEnumValue");
            final Long stretchyReportId = rs.getLong("stretchyReportId");
            final String stretchyReportParamMap = rs.getString("stretchyReportParamMap");
            final String message = rs.getString("message");
            final boolean isActive = rs.getBoolean("isActive");
            EnumOptionData relatedEntityEnum = null;
            EnumOptionData severityLevelEnum = null;
            
            if (relatedEntityEnumValue != null) {
                relatedEntityEnum = CreditCheckEnumerations.CreditCheckRelatedEntity(relatedEntityEnumValue);
            }
            
            if (severityLevelEnumValue != null) {
                severityLevelEnum = CreditCheckEnumerations.severityLevel(severityLevelEnumValue);
            }
            
            return CreditCheckData.instance(id, name, relatedEntityEnum, expectedResult, severityLevelEnum, 
                    stretchyReportId, stretchyReportParamMap, message, isActive);
        }
    }

    @Override
    public CreditCheckData retrieveCreditCheckEnumOptions() {
        final List<EnumOptionData> relatedEntityOptions = this.creditCheckDropdownReadPlatformService.retrieveRelatedEntityOptions();
        final List<EnumOptionData> severityLevelOptions = this.creditCheckDropdownReadPlatformService.retrieveSeverityLevelOptions();
        
        return CreditCheckData.instance(relatedEntityOptions, severityLevelOptions);
    }

    @Override
    public Collection<CreditCheckData> retrieveAllLoanApplicableCreditChecks() {
        final CreditCheckMapper mapper = new CreditCheckMapper();
        final String sql = "select " + mapper.creditCheckSchema() 
                + " where cc.is_deleted = 0 and cc.is_active = 1 and cc.related_entity_enum_value = ? order by cc.name";
        
        return this.jdbcTemplate.query(sql, mapper, new Object[] { CreditCheckRelatedEntity.LOAN.getValue() });
    }

    @Override
    public Collection<CreditCheckData> retrieveLoanProductCreditChecks(final Long loanProductId) {
        final CreditCheckMapper mapper = new CreditCheckMapper();
        final String sql = "select " + mapper.loanProductCreditCheckSchema() 
                + " where cc.is_deleted = 0 and cc.is_active = 1 and plcc.product_loan_id = ? order by cc.name";
        
        return this.jdbcTemplate.query(sql, mapper, new Object[] { loanProductId });
    }
}
