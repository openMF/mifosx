package org.mifosplatform.xbrl.taxonomy.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.xbrl.report.data.NamespaceData;
import org.mifosplatform.xbrl.report.service.ReadNamespaceService;
import org.mifosplatform.xbrl.taxonomy.data.TaxonomyData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ReadTaxonomyServiceImpl implements ReadTaxonomyService {
	private final JdbcTemplate jdbcTemplate;
	private final ReadNamespaceService readNamespaceService;
	
	@Autowired
	public ReadTaxonomyServiceImpl(final TenantAwareRoutingDataSource dataSource, 
									final ReadNamespaceService readNamespaceService) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.readNamespaceService = readNamespaceService;
	}
	
	private static final class TaxonomyMapper implements RowMapper<TaxonomyData> {

		public String schema() {
			return "tx.id as id, name, dimension, type, unit, period, description, prefix "
					+ "from m_taxonomy tx left join m_xbrl_namespace xn on tx.namespace_id=xn.id";
		}
		
		@Override
		public TaxonomyData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final long id = rs.getLong("id");
			final String name = rs.getString("name");
			final String namespace = rs.getString("prefix");
			
			final String dimension = rs.getString("dimension");
			final Integer type = rs.getInt("type");
			final Integer unit = rs.getInt("unit");
			final Integer period = rs.getInt("period");
			final String desc = rs.getString("description");
			return new TaxonomyData(id,name,namespace,dimension,type,unit,period,desc);
		}
		
	}

	@Override
	public List<TaxonomyData> retrieveAllTaxonomy() {
		final TaxonomyMapper rm = new TaxonomyMapper();
		String sql = "select " + rm.schema();
		return this.jdbcTemplate.query(sql, rm);
	}

	@Override
	public TaxonomyData retrieveTaxonomyById(Long id) {
		final TaxonomyMapper rm = new TaxonomyMapper();
		String sql = "select " + rm.schema() + " where tx.id =" + id;
		return this.jdbcTemplate.queryForObject(sql, rm);
	}
	
}
