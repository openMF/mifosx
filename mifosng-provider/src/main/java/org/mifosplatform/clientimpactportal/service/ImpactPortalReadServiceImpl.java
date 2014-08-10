package org.mifosplatform.clientimpactportal.service;

import org.mifosplatform.clientimpactportal.data.ImpactPortalData;
import org.mifosplatform.clientimpactportal.exception.ImpactPortalDataNotFoundException;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;


@Service
public class ImpactPortalReadServiceImpl implements ImpactPortalReadService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ImpactPortalReadServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public ImpactPortalData getDataByNameForYesterday(String name) {
        try{
        final ImpactPortalDataDetailMapper detailMapper = new ImpactPortalDataDetailMapper();
        final String sql = detailMapper.schema() + " where date(date_captured) in (SELECT max(date(date_captured)) FROM impact_portal_cache) and datapoint_label=?";
        return this.jdbcTemplate.queryForObject(sql, detailMapper, new Object[] { name });
    } catch (final EmptyResultDataAccessException e) {
        throw new ImpactPortalDataNotFoundException(name);
    }
   }

    @Override
    public ImpactPortalData getDataByDate(String name,String date) {
        try{
            final ImpactPortalDataDetailMapper detailMapper = new ImpactPortalDataDetailMapper();
            final String sql = detailMapper.schema() + " where date(date_captured)=? and datapoint_label=?";
            return this.jdbcTemplate.queryForObject(sql, detailMapper, new Object[] { date,name });
        } catch (final EmptyResultDataAccessException e) {
            throw new ImpactPortalDataNotFoundException(name);
        }
    }

    @Override
    public Collection<ImpactPortalData> getDataByDateRange(String name,String startDate,String endDate) {
        try{
            final ImpactPortalDataDetailMapper detailMapper = new ImpactPortalDataDetailMapper();
            final String sql = detailMapper.schema() + " where (date(date_captured) BETWEEN ? AND ?) AND datapoint_label=?";
            return this.jdbcTemplate.query(sql, detailMapper, new Object[]{startDate, endDate, name});
        } catch (final EmptyResultDataAccessException e) {
            throw new ImpactPortalDataNotFoundException(name);
        }
    }

    private static final class ImpactPortalDataDetailMapper implements RowMapper<ImpactPortalData> {

        private final StringBuilder sqlBuilder = new StringBuilder("select")
                .append(" * from impact_portal_cache ");

        public String schema() {
            return this.sqlBuilder.toString();
        }

        @Override
        public ImpactPortalData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            
            final int id= rs.getInt("id");
            final Date dateCaptured= rs.getDate("date_captured");
            final String dataPoint=rs.getString("datapoint");
            final String dataPointLabel=rs.getString("datapoint_label");
            final String value=rs.getString("value");
            

            final ImpactPortalData portalData = new ImpactPortalData(id,dateCaptured,dataPoint,dataPointLabel,value);
            return portalData;
        }

    }
}
