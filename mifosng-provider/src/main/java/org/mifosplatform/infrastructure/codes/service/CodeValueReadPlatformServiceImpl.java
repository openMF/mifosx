package org.mifosplatform.infrastructure.codes.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class CodeValueReadPlatformServiceImpl implements CodeValueReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public CodeValueReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class CodeValueDataMapper implements RowMapper<CodeValueData> {

        public String schema() {
            return " cv.id as id, cv.code_value as value, cv.code_id as codeId, cv.order_position as position"
                    + " from m_code_value as cv join m_code c on cv.code_id = c.id ";
        }

        @Override
        public CodeValueData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String value = rs.getString("value");
            final Integer position = rs.getInt("position");

            return CodeValueData.instance(id, value, position);
        }
    }

    @Override
    public Collection<CodeValueData> retrieveCustomIdentifierCodeValues() {

        context.authenticatedUser();

        final CodeValueDataMapper rm = new CodeValueDataMapper();
        final String sql = "select " + rm.schema() + "where c.code_name like ? order by position";

        return this.jdbcTemplate.query(sql, rm, new Object[] { "Customer Identifier" });
    }

	@Override
	public Collection<CodeValueData> retrieveAllCodeValues(Long codeId) {
		
		context.authenticatedUser();

        final CodeValueDataMapper rm = new CodeValueDataMapper();
        final String sql = "select " + rm.schema() + "where cv.code_id = ? order by position";

        return this.jdbcTemplate.query(sql, rm, new Object[] { codeId });
	}
    
    
}