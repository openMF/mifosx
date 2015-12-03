/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.reportmailingjob.data.ReportMailingJobConfigurationData;
import org.mifosplatform.infrastructure.reportmailingjob.exception.ReportMailingJobConfigurationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ReportMailingJobConfigurationReadPlatformServiceImpl implements ReportMailingJobConfigurationReadPlatformService {
    private final JdbcTemplate jdbcTemplate;
    
    /** 
     * ReportMailingJobConfigurationReadPlatformServiceImpl constructor
     **/
    @Autowired
    public ReportMailingJobConfigurationReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    @Override
    public Collection<ReportMailingJobConfigurationData> retrieveAllReportMailingJobConfigurations() {
        final ReportMailingJobConfigurationMapper mapper = new ReportMailingJobConfigurationMapper();
        final String sql = "select " + mapper.ReportMailingJobConfigurationSchema();
        
        return this.jdbcTemplate.query(sql, mapper, new Object[] {});
    }

    @Override
    public ReportMailingJobConfigurationData retrieveReportMailingJobConfiguration(String name) {
        try {
            final ReportMailingJobConfigurationMapper mapper = new ReportMailingJobConfigurationMapper();
            final String sql = "select " + mapper.ReportMailingJobConfigurationSchema() + " where rmjc.name = ?";
            
            return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { name });
        }
        
        catch (final EmptyResultDataAccessException ex) {
            throw new ReportMailingJobConfigurationNotFoundException(name);
        }
    }
    
    private static final class ReportMailingJobConfigurationMapper implements RowMapper<ReportMailingJobConfigurationData> {
        public String ReportMailingJobConfigurationSchema() {
            return "rmjc.id, rmjc.name, rmjc.value "
                    + "from m_report_mailing_job_configuration rmjc";
        }
        
        @Override
        public ReportMailingJobConfigurationData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Integer id = JdbcSupport.getInteger(rs, "id");
            final String name = rs.getString("name");
            final String value = rs.getString("value");
            
            return ReportMailingJobConfigurationData.instance(id, name, value);
        }
    }
}
