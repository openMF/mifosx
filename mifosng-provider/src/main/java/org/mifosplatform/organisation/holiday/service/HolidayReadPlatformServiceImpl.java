package org.mifosplatform.organisation.holiday.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.holiday.data.HolidayData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class HolidayReadPlatformServiceImpl implements HolidayReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public HolidayReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    private static final class HolidayMapper implements RowMapper<HolidayData> {

        private final String schema;

        public HolidayMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append("h.id as id, h.name as name, h.from_date as fromDate, h.to_date as toDate, ");
            sqlBuilder.append("h.repayments_rescheduled_to as repaymentsScheduleTO ");
            sqlBuilder.append("from m_holiday h join m_holiday_office hf on h.id = hf.holiday_id and ");
            schema = sqlBuilder.toString();
        }
        
        public String schema() {
            return this.schema;
        }
        
        @Override
        public HolidayData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final LocalDate fromDate = JdbcSupport.getLocalDate(rs, "fromDate");
            final LocalDate toDate = JdbcSupport.getLocalDate(rs, "toDate");
            final LocalDate repaymentsScheduleTO = JdbcSupport.getLocalDate(rs, "repaymentsScheduleTO");
            
            return new HolidayData(id, name, fromDate, toDate, repaymentsScheduleTO);
        }
        
    }
    
    @Override
    public Collection<HolidayData> retrieveAllHolidaysBySearchParamerters(Long officeId, Date fromDate, Date toDate) {
        this.context.authenticatedUser();

        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        final Object[] objectArray = new Object[3];
        int arrayPos = 0;

        HolidayMapper rm = new HolidayMapper();
        String sql = "select " + rm.schema() + " hf.office_id = ? ";

        objectArray[arrayPos] = officeId;
        arrayPos = arrayPos + 1;

        if (fromDate != null || toDate != null) {
            sql += "and ";

            if (fromDate != null) {
                sql += "h.from_Date >= ? ";

                objectArray[arrayPos] = df.format(fromDate);
                arrayPos = arrayPos + 1;
            }

            if (toDate != null) {
                sql += fromDate != null ? "and " : "";
                sql += "h.to_date <= ? ";
                objectArray[arrayPos] = df.format(toDate);
                arrayPos = arrayPos + 1;
            }
        }
        
        final Object[] finalObjectArray = Arrays.copyOf(objectArray, arrayPos);

        return this.jdbcTemplate.query(sql, rm, finalObjectArray);
    }

}
