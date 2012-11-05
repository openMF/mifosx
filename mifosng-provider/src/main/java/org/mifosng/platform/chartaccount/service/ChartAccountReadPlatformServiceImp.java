package org.mifosng.platform.chartaccount.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.mifosng.platform.api.data.ChartAccountData;
import org.mifosng.platform.infrastructure.JdbcSupport;
import org.mifosng.platform.infrastructure.TenantAwareRoutingDataSource;
import org.mifosng.platform.security.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
@Service
public class ChartAccountReadPlatformServiceImp implements ChartAccountReadPlatformService{
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	
	@Autowired
	public  ChartAccountReadPlatformServiceImp(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public Collection<ChartAccountData> retrieveAllChartAccount() {
		
		this.context.authenticatedUser();

		ChartAccountMapper mapper = new ChartAccountMapper();
		String sql = "select " + mapper.schema() + " order by c.chartcode";

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	@Override
	public ChartAccountData retrieveChartAccount(final Long chartcode) {
		
		this.context.authenticatedUser();
		
		ChartAccountMapper mapper = new ChartAccountMapper();
		String sql = "select " + mapper.schema() + " where c.chartcode=?";

		ChartAccountData chartAccountData=this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {chartcode});
		return chartAccountData;
	}
	
	protected static final class ChartAccountMapper implements RowMapper<ChartAccountData> {

		@Override
		public ChartAccountData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long chartcode = JdbcSupport.getLong(rs, "chartcode");
			String description = rs.getString("description");
			String type = rs.getString("type");

			return new ChartAccountData(chartcode,description,type);
			
		}
		public String schema() {
			return " c.chartcode as chartcode , c.description as description, c.type as type from m_chartaccount c";
		}
	}
	
}


