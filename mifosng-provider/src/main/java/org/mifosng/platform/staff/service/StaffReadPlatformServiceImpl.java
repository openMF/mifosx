package org.mifosng.platform.staff.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifosng.platform.api.data.ClientAccountSummaryData;
import org.mifosng.platform.api.data.GroupAccountSummaryData;
import org.mifosng.platform.api.data.StaffAccountSummaryCollectionData;
import org.mifosng.platform.api.data.StaffData;
import org.mifosng.platform.client.service.ClientReadPlatformService;
import org.mifosng.platform.exceptions.StaffNotFoundException;
import org.mifosng.platform.group.service.GroupReadPlatformService;
import org.mifosng.platform.infrastructure.JdbcSupport;
import org.mifosng.platform.infrastructure.TenantAwareRoutingDataSource;
import org.mifosng.platform.security.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class StaffReadPlatformServiceImpl implements StaffReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
    private final ClientReadPlatformService clientReadPlatformService;
    private final GroupReadPlatformService groupReadPlatformService;

	@Autowired
	public StaffReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource,
            final ClientReadPlatformService clientReadPlatformService,
            final GroupReadPlatformService groupReadPlatformService) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.clientReadPlatformService = clientReadPlatformService;
        this.groupReadPlatformService = groupReadPlatformService;
	}

	private static final class StaffMapper implements RowMapper<StaffData> {

		public String schema() {
			return " s.id as id,s.office_id as officeId, o.name as officeName, s.firstname as firstname, s.lastname as lastname,"
					+ " s.display_name as displayName, s.is_loan_officer as isLoanOfficer from m_staff s "
					+ " join m_office o on o.id = s.office_id";
		}

		@Override
		public StaffData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String firstname = rs.getString("firstname");
			String lastname = rs.getString("lastname");
			String displayName = rs.getString("displayName");
			Long officeId = rs.getLong("officeId");
			boolean isLoanOfficer = rs.getBoolean("isLoanOfficer");
			String officeName = rs.getString("officeName");

			return new StaffData(id, firstname, lastname, displayName,
					officeId, officeName, isLoanOfficer);
		}

	}

	@Override
	public Collection<StaffData> retrieveAllStaff(final String extraCriteria) {

		context.authenticatedUser();

		StaffMapper rm = new StaffMapper();
		String sql = "select " + rm.schema();
		if (StringUtils.isNotBlank(extraCriteria)) {
			sql += " where " + extraCriteria;
		}
		sql = sql + " order by s.lastname";
		return this.jdbcTemplate.query(sql, rm, new Object[] {});
	}

	@Override
	public Collection<StaffData> retrieveAllLoanOfficersByOffice(final Long officeId) {
		return retrieveAllStaff(" office_id=" + officeId + " and is_loan_officer=1");
	}

	@Override
	public StaffData retrieveStaff(final Long staffId) {

		try {
			context.authenticatedUser();

			StaffMapper rm = new StaffMapper();
			String sql = "select " + rm.schema() + " where s.id = ?";

			StaffData selectedStaff = this.jdbcTemplate.queryForObject(sql, rm,
					new Object[] { staffId });

			return selectedStaff;
		} catch (EmptyResultDataAccessException e) {
			throw new StaffNotFoundException(staffId);
		}
	}

	@Override
	public StaffAccountSummaryCollectionData retrieveLoanOfficerAccountSummary(final Long loanOfficerId) {

		context.authenticatedUser();

		StaffClientMapper staffClientMapper = new StaffClientMapper();
		String clientSql = "select distinct " + staffClientMapper.schema();

		StaffGroupMapper staffGroupMapper = new StaffGroupMapper();
		String groupSql = "select distinct " + staffGroupMapper.schema();

		List<StaffAccountSummaryCollectionData.ClientSummary> clientSummaryList = this.jdbcTemplate
				.query(clientSql, staffClientMapper,
						new Object[] { loanOfficerId });

		for (StaffAccountSummaryCollectionData.ClientSummary clientSummary : clientSummaryList) {

			Collection<ClientAccountSummaryData> clientLoanAccounts = this.clientReadPlatformService
					.retrieveClientLoanAccountsByLoanOfficerId(
							clientSummary.getId(), loanOfficerId);

			clientSummary.setLoans(clientLoanAccounts);
		}

		List<StaffAccountSummaryCollectionData.GroupSummary> groupSummaryList = this.jdbcTemplate
				.query(groupSql, staffGroupMapper, new Object[] { loanOfficerId });

		for (StaffAccountSummaryCollectionData.GroupSummary groupSummary : groupSummaryList) {

			Collection<GroupAccountSummaryData> groupLoanAccounts = this.groupReadPlatformService
					.retrieveGroupLoanAccountsByLoanOfficerId(
							groupSummary.getId(), loanOfficerId);

			groupSummary.setLoans(groupLoanAccounts);
		}

		return new StaffAccountSummaryCollectionData(clientSummaryList, groupSummaryList);
	}

    private static final class StaffClientMapper implements RowMapper<StaffAccountSummaryCollectionData.ClientSummary> {

        public String schema() {
            return " c.id as id, c.display_name as displayName from m_client c " +
                   " join m_loan l on c.id = l.client_id where l.loan_officer_id = ? ";
        }

        @Override
        public StaffAccountSummaryCollectionData.ClientSummary mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final String displayName = rs.getString("displayName");

            return new StaffAccountSummaryCollectionData.ClientSummary(id, displayName);
        }
    }

    private static final class StaffGroupMapper implements RowMapper<StaffAccountSummaryCollectionData.GroupSummary> {

        public String schema() {
            return " g.id as id, g.name as name from m_group g" +
                   " join m_loan l on g.id = l.group_id where l.loan_officer_id = ? ";
        }

        @Override
        public StaffAccountSummaryCollectionData.GroupSummary mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
        	final Long id = JdbcSupport.getLong(rs, "id");
        	final String name = rs.getString("name");

            return new StaffAccountSummaryCollectionData.GroupSummary(id, name);
        }
    }
}