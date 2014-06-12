package org.mifosplatform.clientimpactportal.service;

import org.mifosplatform.clientimpactportal.data.ImpactPortalSqlData;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;



@Service
public class ImpactPortalReportReadServiceImpl implements ImpactPortalReportReadService {


    private final JdbcTemplate jdbcTemplate;

    // mappers
    private final ImpactPortalDataDetailMapper impactPortalDataDetailMapper;

    @Autowired
    public ImpactPortalReportReadServiceImpl(final RoutingDataSource dataSource){
           this.jdbcTemplate=new JdbcTemplate(dataSource);
           this.impactPortalDataDetailMapper=new ImpactPortalDataDetailMapper();
    }

    @Override
    public Collection<ImpactPortalSqlData> retrieveSqlForPortalReports(){
            final String sql= impactPortalDataDetailMapper.schema()+"where report_category='Portal'";
        return this.jdbcTemplate.query(sql, this.impactPortalDataDetailMapper, new Object[] {});
    }

    private static final class ImpactPortalDataDetailMapper implements RowMapper<ImpactPortalSqlData> {

        private final StringBuilder sqlBuilder = new StringBuilder("select")
                .append(" * from stretchy_report ");

        public String schema() {
            return this.sqlBuilder.toString();
        }

        @Override
        public ImpactPortalSqlData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {


            final String reportName= rs.getString("report_name");
            final String reportType=rs.getString("report_type");
            final String reportSubType=rs.getString("report_subtype");
            final String reportCategory=rs.getString("report_category");
            final String reportSql=rs.getString("report_sql");
            final String reportDescription=rs.getString("description");
            final int coreReport=rs.getInt("core_report");
            final String useReport=rs.getString("use_report");


            final ImpactPortalSqlData sqlData = new ImpactPortalSqlData(reportName,reportType,reportSubType,reportCategory,reportSql,reportDescription,
                    coreReport,useReport);
            return sqlData;
        }

    }
}
