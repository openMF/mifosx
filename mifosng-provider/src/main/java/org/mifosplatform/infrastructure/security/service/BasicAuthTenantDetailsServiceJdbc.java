/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.security.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenantConnection;
import org.mifosplatform.infrastructure.security.exception.InvalidTenantIdentiferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

/**
 * A JDBC implementation of {@link BasicAuthTenantDetailsService} for loading a
 * tenants details by a <code>tenantIdentifier</code>.
 */
@Service
public class BasicAuthTenantDetailsServiceJdbc implements BasicAuthTenantDetailsService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BasicAuthTenantDetailsServiceJdbc(@Qualifier("tenantDataSourceJndi") final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class TenantMapper implements RowMapper<MifosPlatformTenant> {

        private final StringBuilder sqlBuilder = new StringBuilder(
                "t.id,t.oltp_id as oltpId,t.report_id as reportId,ts.id as oltpConnectionId ,ts1.id as reportConnectionId ,"
                        + "t.timezone_id as timezoneId , t.name,t.identifier, ts.schema_name as oltpSchemaName, ts.schema_server as oltpSchemaServer,"
                        + "ts.schema_server_port as oltpSchemaServerPort, ts.auto_update as oltpAutoUpdate,")//
                .append(" ts.schema_username as oltpSchemaUsername, ts.schema_password as oltpSchemaPassword , ts.pool_initial_size as oltpInitialSize,")//
                .append(" ts.pool_validation_interval as oltpValidationInterval, ts.pool_remove_abandoned as oltpRemoveAbandoned, ts.pool_remove_abandoned_timeout as oltpRemoveAbandonedTimeout,")//
                .append(" ts.pool_log_abandoned as oltpLogAbandoned, ts.pool_abandon_when_percentage_full as oltpAbandonedWhenPercentageFull, ts.pool_test_on_borrow as oltpTestOnBorrow,")//
                .append(" ts.pool_max_active as oltpPoolMaxActive, ts.pool_min_idle as oltpPoolMinIdle, ts.pool_max_idle as oltpPoolMaxIdle,")//
                .append(" ts.pool_suspect_timeout as oltpPoolSuspectTimeout, ts.pool_time_between_eviction_runs_millis as oltpPoolTimeBetweenEvictionRunsMillis,")//
                .append(" ts.pool_min_evictable_idle_time_millis as oltpPoolMinEvictableIdleTimeMillis,")//
                .append(" ts.deadlock_max_retries as oltpMaxRetriesOnDeadlock,")//
                .append(" ts.deadlock_max_retry_interval as oltpMaxIntervalBetweenRetries,")//
                .append("ts1.schema_name as reportSchemaName,ts1.schema_server as reportSchemaServer, ts1.schema_server_port as reportSchemaServerPort, ts1.auto_update as reportAutoUpdate,")//
                .append(" ts1.schema_username as reportSchemaUsername, ts1.schema_password as reportSchemaPassword ,ts1.pool_initial_size as reportInitialSize,")//
                .append(" ts1.pool_validation_interval as reportValidationInterval, ts1.pool_remove_abandoned as reportRemoveAbandoned, ts1.pool_remove_abandoned_timeout as reportRemoveAbandonedTimeout,")//
                .append(" ts1.pool_log_abandoned as reportLogAbandoned, ts1.pool_abandon_when_percentage_full as reportAbandonedWhenPercentageFull, ts1.pool_test_on_borrow as reportTestOnBorrow,")//
                .append(" ts1.pool_max_active as reportPoolMaxActive, ts1.pool_min_idle as reportPoolMinIdle, ts1.pool_max_idle as reportPoolMaxIdle,")//
                .append(" ts1.pool_suspect_timeout as reportPoolSuspectTimeout, ts1.pool_time_between_eviction_runs_millis as reportPoolTimeBetweenEvictionRunsMillis,")//
                .append(" ts1.pool_min_evictable_idle_time_millis as reportPoolMinEvictableIdleTimeMillis,")//
                .append(" ts1.deadlock_max_retries as reportMaxRetriesOnDeadlock,")//
                .append(" ts1.deadlock_max_retry_interval as reportMaxIntervalBetweenRetries ")//
                .append(" from tenants t left join tenant_server_connections ts on t.oltp_Id=ts.id ")
                .append("left join tenant_server_connections ts1 on t.report_Id=ts1.id");//

        public String schema() {
            return this.sqlBuilder.toString();
        }

        @Override
        public MifosPlatformTenant mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            MifosPlatformTenantConnection oltpConnection = null;
            MifosPlatformTenantConnection reportConnection = null;

            oltpConnection = getDBConnection(rs, "oltp");
            reportConnection = getDBConnection(rs, "report");

            final Long id = rs.getLong("id");
            final String tenantIdentifier = rs.getString("identifier");
            final String name = rs.getString("name");
            final String timezoneId = rs.getString("timezoneId");
            final Long oltpId = rs.getLong("oltpId");
            final Long reportId = JdbcSupport.getLong(rs, "reportId");
            return new MifosPlatformTenant(id, oltpId, reportId, tenantIdentifier, name, timezoneId, oltpConnection, reportConnection);
        }

        // gets the DB connection
        private MifosPlatformTenantConnection getDBConnection(ResultSet rs, String suffix) throws SQLException {

            final Long connectionId = rs.getLong(suffix + "ConnectionId");
            final String schemaName = rs.getString(suffix + "SchemaName");
            final String schemaServer = rs.getString(suffix + "SchemaServer");
            final String schemaServerPort = rs.getString(suffix + "SchemaServerPort");
            final String schemaUsername = rs.getString(suffix + "SchemaUsername");
            final String schemaPassword = rs.getString(suffix + "SchemaPassword");
            final boolean autoUpdateEnabled = rs.getBoolean(suffix + "AutoUpdate");
            final int initialSize = rs.getInt(suffix + "InitialSize");
            final boolean testOnBorrow = rs.getBoolean(suffix + "TestOnBorrow");
            final long validationInterval = rs.getLong(suffix + "ValidationInterval");
            final boolean removeAbandoned = rs.getBoolean(suffix + "RemoveAbandoned");
            final int removeAbandonedTimeout = rs.getInt(suffix + "RemoveAbandonedTimeout");
            final boolean logAbandoned = rs.getBoolean(suffix + "LogAbandoned");
            final int abandonWhenPercentageFull = rs.getInt(suffix + "AbandonedWhenPercentageFull");
            final int maxActive = rs.getInt(suffix + "PoolMaxActive");
            final int minIdle = rs.getInt(suffix + "PoolMinIdle");
            final int maxIdle = rs.getInt(suffix + "PoolMaxIdle");
            final int suspectTimeout = rs.getInt(suffix + "PoolSuspectTimeout");
            final int timeBetweenEvictionRunsMillis = rs.getInt(suffix + "PoolTimeBetweenEvictionRunsMillis");
            final int minEvictableIdleTimeMillis = rs.getInt(suffix + "PoolMinEvictableIdleTimeMillis");
            int maxRetriesOnDeadlock = rs.getInt(suffix + "MaxRetriesOnDeadlock");
            int maxIntervalBetweenRetries = rs.getInt(suffix + "MaxIntervalBetweenRetries");

            maxRetriesOnDeadlock = bindValueInMinMaxRange(maxRetriesOnDeadlock, 0, 15);
            maxIntervalBetweenRetries = bindValueInMinMaxRange(maxIntervalBetweenRetries, 1, 15);

            return new MifosPlatformTenantConnection(connectionId, schemaName, schemaServer, schemaServerPort, schemaUsername,
                    schemaPassword, autoUpdateEnabled, initialSize, validationInterval, removeAbandoned, removeAbandonedTimeout,
                    logAbandoned, abandonWhenPercentageFull, maxActive, minIdle, maxIdle, suspectTimeout, timeBetweenEvictionRunsMillis,
                    minEvictableIdleTimeMillis, maxRetriesOnDeadlock, maxIntervalBetweenRetries, testOnBorrow);
        }

        private int bindValueInMinMaxRange(final int value, int min, int max) {
            if (value < min) {
                return min;
            } else if (value > max) { return max; }
            return value;
        }
    }

    @Override
    @Cacheable(value = "tenantsById")
    public MifosPlatformTenant loadTenantById(final String tenantIdentifier) {

        try {
            final TenantMapper rm = new TenantMapper();
            final String sql = "select  " + rm.schema() + " where t.identifier like ?";

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { tenantIdentifier });
        } catch (final EmptyResultDataAccessException e) {
            throw new InvalidTenantIdentiferException("The tenant identifier: " + tenantIdentifier + " is not valid.");
        }
    }

}