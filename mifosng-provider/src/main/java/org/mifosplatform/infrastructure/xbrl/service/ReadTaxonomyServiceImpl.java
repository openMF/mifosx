package org.mifosplatform.infrastructure.xbrl.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.xbrl.data.TaxonomyData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ReadTaxonomyServiceImpl implements ReadTaxonomyService {
	private final JdbcTemplate jdbcTemplate;
	
	@Autowired
	public ReadTaxonomyServiceImpl(final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	private static final class TaxonomyMapper implements RowMapper<TaxonomyData> {

		public String schema() {
			return "tx.id as id, name as name, namespace as namespace, dimension as dimension, description as description, "
					+ "mapping as mapping "
					+ "from m_taxonomy tx left join m_taxonomy_mapping tm on tx.id=tm.taxonomyId";
		}
		
		@Override
		public TaxonomyData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final long id = rs.getLong("id");
			final String name = rs.getString("name");
			final String namespace = rs.getString("namespace");
			final String dimension = rs.getString("dimension");
			final String desc = rs.getString("description");
			final String mapping = rs.getString("mapping");
			return new TaxonomyData(id,name,namespace,dimension,desc,mapping);
		}
		
	}

	@Override
	public List<TaxonomyData> retrieveAllTaxonomyMapping() {
		final TaxonomyMapper rm = new TaxonomyMapper();
		String sql = "select " + rm.schema();
		return this.jdbcTemplate.query(sql, rm);
	}

}
