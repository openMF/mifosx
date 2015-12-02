/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.creditcheck.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.portfolio.creditcheck.data.CreditCheckReportParamData;
import org.mifosplatform.portfolio.creditcheck.exception.CreditCheckReportParamNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class CreditCheckReportParamReadPlatformServiceImpl implements CreditCheckReportParamReadPlatformService {
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public CreditCheckReportParamReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    @Override
    public CreditCheckReportParamData retrieveCreditCheckReportParameters(Long loanId, Long userId) {
        try {
            final CreditCheckReportParamMapper mapper = new CreditCheckReportParamMapper(userId);
            final String sql = "select " + mapper.CreditCheckReportParamSchema() + " where ml.id = ? limit 1";
            
            return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { loanId });
        }
        
        catch (final EmptyResultDataAccessException e) {
            throw new CreditCheckReportParamNotFoundException(loanId);
        }
    }
    
    private static final class CreditCheckReportParamMapper implements RowMapper<CreditCheckReportParamData> {
       private final Long userId;
        
        public String CreditCheckReportParamSchema() {
            return "ml.id as loanId, mc.id as clientId, mc.office_id as officeId, mc.staff_id as staffId, "
                    + "mgc.group_id as groupId, ml.product_id as productId "
                    + "from m_loan ml "
                    + "inner join m_client mc "
                    + "on ml.client_id = mc.id "
                    + "left join m_group_client mgc "
                    + "on mgc.client_id = ml.client_id";
        }
        
        public CreditCheckReportParamMapper(Long userId) {
           this.userId = userId;
        }

        @Override
        public CreditCheckReportParamData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long loanId = rs.getLong("loanId");
            final Long clientId = rs.getLong("clientId");
            final Long officeId = rs.getLong("officeId");
            final Long staffId = rs.getLong("staffId");
            final Long userId = this.userId;
            final Long groupId = rs.getLong("groupId");
            final Long productId = rs.getLong("productId");
            
            return CreditCheckReportParamData.instance(clientId, loanId, groupId, userId, staffId, officeId, productId);
        }
    }
}
