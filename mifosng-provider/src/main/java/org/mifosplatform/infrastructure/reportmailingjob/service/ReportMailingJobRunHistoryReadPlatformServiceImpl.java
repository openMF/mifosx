/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.core.service.SearchParameters;
import org.mifosplatform.infrastructure.reportmailingjob.data.ReportMailingJobRunHistoryData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ReportMailingJobRunHistoryReadPlatformServiceImpl implements ReportMailingJobRunHistoryReadPlatformService {
    private final JdbcTemplate jdbcTemplate;
    private final ReportMailingJobRunHistoryMapper reportMailingJobRunHistoryMapper;
    private final PaginationHelper<ReportMailingJobRunHistoryData> paginationHelper = new PaginationHelper<>();
    
    @Autowired
    public ReportMailingJobRunHistoryReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.reportMailingJobRunHistoryMapper = new ReportMailingJobRunHistoryMapper();
    }
    
    @Override
    public Page<ReportMailingJobRunHistoryData> retrieveRunHistoryByJobId(final Long reportMailingJobId, 
            final SearchParameters searchParameters) {
        final StringBuilder sqlStringBuilder = new StringBuilder(200);
        final List<Object> queryParameters = new ArrayList<>();
        
        sqlStringBuilder.append("select SQL_CALC_FOUND_ROWS ");
        sqlStringBuilder.append(this.reportMailingJobRunHistoryMapper.ReportMailingJobRunHistorySchema());
        
        if (reportMailingJobId != null) {
            sqlStringBuilder.append(" where rmjrh.job_id = ? ");
            queryParameters.add(reportMailingJobId);
        }
        
        if (searchParameters.isOrderByRequested()) {
            sqlStringBuilder.append(" order by ").append(searchParameters.getOrderBy());

            if (searchParameters.isSortOrderProvided()) {
                sqlStringBuilder.append(" ").append(searchParameters.getSortOrder());
            }
        }

        if (searchParameters.isLimited()) {
            sqlStringBuilder.append(" limit ").append(searchParameters.getLimit());
            
            if (searchParameters.isOffset()) {
                sqlStringBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }
        
        return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlStringBuilder.toString(), 
                queryParameters.toArray(), this.reportMailingJobRunHistoryMapper);
    }
    
    private static final class ReportMailingJobRunHistoryMapper implements RowMapper<ReportMailingJobRunHistoryData> {
        public String ReportMailingJobRunHistorySchema() {
            return "rmjrh.id, rmjrh.job_id as reportMailingJobId, rmjrh.start_datetime as startDateTime, "
                    + "rmjrh.end_datetime as endDateTime, rmjrh.status, rmjrh.error_message as errorMessage, "
                    + "rmjrh.error_log as errorLog "
                    + "from m_report_mailing_job_run_history rmjrh";
        }
        
        @Override
        public ReportMailingJobRunHistoryData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final Long reportMailingJobId = JdbcSupport.getLong(rs, "reportMailingJobId");
            final DateTime startDateTime = JdbcSupport.getDateTime(rs, "startDateTime");
            final DateTime endDateTime = JdbcSupport.getDateTime(rs, "endDateTime");
            final String status = rs.getString("status");
            final String errorMessage = rs.getString("errorMessage");
            final String errorLog = rs.getString("errorLog");
            
            return ReportMailingJobRunHistoryData.instance(id, reportMailingJobId, startDateTime, endDateTime, status, 
                    errorMessage, errorLog);
        }
    }
}
