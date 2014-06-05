package org.mifosplatform.infrastructure.smsgateway.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.smsgateway.data.SmsGatewayData;
import org.mifosplatform.infrastructure.smsgateway.exception.SmsGatewayNotFoundException;
import org.mifosplatform.infrastructure.smsgateway.service.SmsGatewayReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class SmsGatewayReadPlatformServiceImpl implements SmsGatewayReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final SmsGatewayMapper smsGatewayRowMapper;

    @Autowired
    public SmsGatewayReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.smsGatewayRowMapper = new SmsGatewayMapper();
    }

    private static final class SmsGatewayMapper implements RowMapper<SmsGatewayData> {

        final String schema;

        public SmsGatewayMapper() {
            final StringBuilder sql = new StringBuilder(300);
            sql.append("r.id as id, ");
            sql.append("r.gateway_name as gatewayName, ");
            sql.append("r.auth_token as authToken, ");
            sql.append("r.url as url ");
            sql.append("from m_sms_gateway r");

            this.schema = sql.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public SmsGatewayData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String gatewayName = rs.getString("gatewayName");
            final String authToken = rs.getString("authToken");
            final String url = rs.getString("url");

            return SmsGatewayData.instance(id, gatewayName, authToken, url);
        }
    }

    @Override
    public Collection<SmsGatewayData> retrieveAll() {

        final String sql = "select " + this.smsGatewayRowMapper.schema();

        return this.jdbcTemplate.query(sql, this.smsGatewayRowMapper, new Object[] {});
    }

    @Override
    public SmsGatewayData retrieveOne(final Long resourceId) {
        try {
            final String sql = "select " + this.smsGatewayRowMapper.schema() + " where r.id = ?";

            return this.jdbcTemplate.queryForObject(sql, this.smsGatewayRowMapper, new Object[] { resourceId });
        } catch (final EmptyResultDataAccessException e) {
            throw new SmsGatewayNotFoundException(resourceId);
        }
    }
}