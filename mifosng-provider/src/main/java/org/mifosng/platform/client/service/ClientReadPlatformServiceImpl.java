package org.mifosng.platform.client.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifosng.platform.api.data.*;
import org.mifosng.platform.client.domain.NoteEnumerations;
import org.mifosng.platform.exceptions.ClientNotFoundException;
import org.mifosng.platform.exceptions.NoteNotFoundException;
import org.mifosng.platform.infrastructure.JdbcSupport;
import org.mifosng.platform.infrastructure.TenantAwareRoutingDataSource;
import org.mifosng.platform.organisation.service.OfficeReadPlatformService;
import org.mifosng.platform.security.PlatformSecurityContext;
import org.mifosng.platform.user.domain.AppUser;
import org.mifosng.platform.user.service.AppUserReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ClientReadPlatformServiceImpl implements ClientReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final AppUserReadPlatformService appUserReadPlatformService;

	@Autowired
	public ClientReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource,
			final OfficeReadPlatformService officeReadPlatformService,
			final AppUserReadPlatformService appUserReadPlatformService) {
		this.context = context;
		this.officeReadPlatformService = officeReadPlatformService;
		this.appUserReadPlatformService = appUserReadPlatformService;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private String generateOfficeIdInClause(List<OfficeData> offices) {
		String officeIdsList = "";
		for (int i = 0; i < offices.size(); i++) {
			Long id = offices.get(i).getId();
			if (i == 0) {
				officeIdsList = id.toString();
			} else {
				officeIdsList += "," + id.toString();
			}
		}
		return officeIdsList;
	}

	@Override
	public Collection<ClientData> retrieveAllIndividualClients(final String extraCriteria) {

		this.context.authenticatedUser();

		List<OfficeData> offices = new ArrayList<OfficeData>(officeReadPlatformService.retrieveAllOffices());
		String officeIdsList = generateOfficeIdInClause(offices);

		ClientMapper rm = new ClientMapper(offices);

		String sql = "select " + rm.clientSchema() + " where c.office_id in (" + officeIdsList + ") and c.is_deleted=0";

		if (StringUtils.isNotBlank(extraCriteria))			
			sql += " and " + extraCriteria;

		sql += " order by c.lastname ASC, c.firstname ASC";

		return this.jdbcTemplate.query(sql, rm, new Object[] {});
	}

	@Override
	public ClientData retrieveIndividualClient(final Long clientId) {

		try {
			this.context.authenticatedUser();
			// TODO - JW include office name in query rather than get the lot
			// for office name
			List<OfficeData> offices = new ArrayList<OfficeData>(
					officeReadPlatformService.retrieveAllOffices());
			ClientMapper rm = new ClientMapper(offices);

			String sql = "select " + rm.clientSchema() + " where c.id = ? and c.is_deleted=0";

			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] {clientId});
		} catch (EmptyResultDataAccessException e) {
			throw new ClientNotFoundException(clientId);
		}
	}

    @Override
    public Collection<ClientLookup> retrieveAllIndividualClientsForLookup() {

        this.context.authenticatedUser();

        ClientLookupMapper rm = new ClientLookupMapper();

        String sql = "select " + rm.clientLookupSchema() + " where c.is_deleted = 0";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
	public ClientData retrieveNewClientDetails() {

		AppUser currentUser = context.authenticatedUser();

		List<OfficeLookup> offices = new ArrayList<OfficeLookup>(
				officeReadPlatformService.retrieveAllOfficesForLookup());

		final Long officeId = currentUser.getOffice().getId();
		return new ClientData(officeId, new LocalDate(), offices);
	}



	private static final class ClientMapper implements RowMapper<ClientData> {

		private final List<OfficeData> offices;

		public ClientMapper(final List<OfficeData> offices) {
			this.offices = offices;
		}

		public String clientSchema() {
			return "c.office_id as officeId, c.id as id, c.firstname as firstname, c.lastname as lastname, c.external_id as externalId, " +
					"c.joining_date as joinedDate from portfolio_client c";
		}

		@Override
		public ClientData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {

			Long officeId = JdbcSupport.getLong(rs, "officeId");
			Long id = JdbcSupport.getLong(rs, "id");
			String firstname = rs.getString("firstname");
			if (StringUtils.isBlank(firstname)) {
				firstname = "";
			}
			String lastname = rs.getString("lastname");
			String externalId = rs.getString("externalId");
			LocalDate joinedDate = JdbcSupport.getLocalDate(rs, "joinedDate");

			String officeName = fromOfficeList(this.offices, officeId);

			return new ClientData(officeId, officeName, id, firstname,
					lastname, externalId, joinedDate);
		}

		private String fromOfficeList(final List<OfficeData> officeList,
				final Long officeId) {
			String match = "";
			for (OfficeData office : officeList) {
				if (office.getId().equals(officeId)) {
					match = office.getName();
				}
			}

			return match;
		}
	}

    private static final class ClientLookupMapper implements RowMapper<ClientLookup>{

        public String clientLookupSchema() {
            return "c.id as id, c.firstname as firstname, c.lastname as lastname from portfolio_client c";
        }

        @Override
        public ClientLookup mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong("id");
            String firstname = rs.getString("firstname");
            String lastname = rs.getString("lastname");
            if (StringUtils.isBlank(firstname)) {
                firstname = "";
            }
            return new ClientLookup(id, firstname, lastname);
        }
    }

	@Override
	public ClientLoanAccountSummaryCollectionData retrieveClientAccountDetails(final Long clientId) {

		try {
			this.context.authenticatedUser();
			
			// Check if client exists
			retrieveIndividualClient(clientId);

			List<ClientLoanAccountSummaryData> pendingApprovalLoans = new ArrayList<ClientLoanAccountSummaryData>();
			List<ClientLoanAccountSummaryData> awaitingDisbursalLoans = new ArrayList<ClientLoanAccountSummaryData>();
			List<ClientLoanAccountSummaryData> openLoans = new ArrayList<ClientLoanAccountSummaryData>();
			List<ClientLoanAccountSummaryData> closedLoans = new ArrayList<ClientLoanAccountSummaryData>();

			ClientLoanAccountSummaryDataMapper rm = new ClientLoanAccountSummaryDataMapper();

			String sql = "select " + rm.loanAccountSummarySchema()
					+ " where l.client_id = ?";

			List<ClientLoanAccountSummaryData> results = this.jdbcTemplate.query(sql, rm, new Object[] {clientId});
			if (results != null) {
				for (ClientLoanAccountSummaryData row : results) {

					LoanStatusMapper statusMapper = new LoanStatusMapper(
							row.getLoanStatusId());

					if (statusMapper.isOpen()) {
						openLoans.add(row);
					} else if (statusMapper.isAwaitingDisbursal()) {
						awaitingDisbursalLoans.add(row);
					} else if (statusMapper.isPendingApproval()) {
						pendingApprovalLoans.add(row);
					} else {
						closedLoans.add(row);
					}
				}
			}

			return new ClientLoanAccountSummaryCollectionData(
					pendingApprovalLoans, awaitingDisbursalLoans, openLoans,
					closedLoans);

		} catch (EmptyResultDataAccessException e) {
			throw new ClientNotFoundException(clientId);
		}
	}

	private static final class ClientLoanAccountSummaryDataMapper implements
			RowMapper<ClientLoanAccountSummaryData> {

		public String loanAccountSummarySchema() {

			StringBuilder loanAccountSummary = new StringBuilder(
					"l.id as id, l.external_id as externalId,");
			loanAccountSummary
					.append("l.product_id as productId, lp.name as productName,")
					.append("l.loan_status_id as statusId, ls.display_name as statusName ")
					.append("from portfolio_loan l ")
					.append("LEFT JOIN portfolio_product_loan AS lp ON lp.id = l.product_id ")
					.append("LEFT JOIN ref_loan_status AS ls ON ls.id = l.loan_status_id ");

			return loanAccountSummary.toString();
		}

		@Override
		public ClientLoanAccountSummaryData mapRow(final ResultSet rs,
				final int rowNum) throws SQLException {

			Long id = JdbcSupport.getLong(rs, "id");
			String externalId = rs.getString("externalId");
			Long productId = JdbcSupport.getLong(rs, "productId");
			String loanProductName = rs.getString("productName");
			Integer loanStatusId = JdbcSupport.getInteger(rs, "statusId");

			return new ClientLoanAccountSummaryData(id, externalId, productId, loanProductName, loanStatusId);
		}
	}

	@Override
	public NoteData retrieveClientNote(Long clientId, Long noteId) {

		try {
			context.authenticatedUser();
			
			// Check if client exists
			retrieveIndividualClient(clientId);

			// FIXME - use join on sql query to fetch user information for note
			// rather than fetching users?
			Collection<AppUserData> allUsers = this.appUserReadPlatformService
					.retrieveAllUsers();

			NoteMapper noteMapper = new NoteMapper(allUsers);

			String sql = "select " + noteMapper.schema()
					+ " where n.client_id = ? and n.id = ?";

			return this.jdbcTemplate.queryForObject(sql, noteMapper, new Object[] {clientId, noteId});
		} catch (EmptyResultDataAccessException e) {
			throw new NoteNotFoundException(clientId);
		}
	}

	@Override
	public Collection<NoteData> retrieveAllClientNotes(Long clientId) {

		context.authenticatedUser();
		
		// Check if client exists
		retrieveIndividualClient(clientId);

		// FIXME - use join on sql query to fetch user information for note
		// rather than fetching users?
		Collection<AppUserData> allUsers = this.appUserReadPlatformService
				.retrieveAllUsers();

		NoteMapper noteMapper = new NoteMapper(allUsers);

		String sql = "select "
				+ noteMapper.schema()
				+ " where n.client_id = ? order by n.created_date DESC";

		return this.jdbcTemplate.query(sql, noteMapper, new Object[] {clientId});
	}

	private static final class NoteMapper implements RowMapper<NoteData> {

		private final Collection<AppUserData> allUsers;

		public NoteMapper(Collection<AppUserData> allUsers) {
			this.allUsers = allUsers;
		}

		public String schema() {
			return "n.id as id, n.client_id as clientId, n.loan_id as loanId, n.loan_transaction_id as transactionId, n.note_type_enum as noteTypeEnum, n.note as note, "
					+ "n.created_date as createdDate, n.createdby_id as createdById, n.lastmodified_date as lastModifiedDate, n.lastmodifiedby_id as lastModifiedById"
					+ " from portfolio_note n";
		}

		@Override
		public NoteData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {

			Long id = JdbcSupport.getLong(rs, "id");
			Long clientId = JdbcSupport.getLong(rs, "clientId");
			Long loanId = JdbcSupport.getLong(rs, "loanId");
			Long transactionId = JdbcSupport.getLong(rs, "transactionId");
			Integer noteTypeId = JdbcSupport.getInteger(rs, "noteTypeEnum");
			EnumOptionData noteType = NoteEnumerations.noteType(noteTypeId);
			String note = rs.getString("note");

			DateTime createdDate = JdbcSupport.getDateTime(rs, "createdDate");
			Long createdById = JdbcSupport.getLong(rs, "createdById");
			String createdByUsername = findUserById(createdById, allUsers);

			DateTime lastModifiedDate = JdbcSupport.getDateTime(rs,
					"lastModifiedDate");
			Long lastModifiedById = JdbcSupport.getLong(rs, "lastModifiedById");
			String updatedByUsername = findUserById(createdById, allUsers);

			return new NoteData(id, clientId, loanId, transactionId, noteType,
					note, createdDate, createdById, createdByUsername,
					lastModifiedDate, lastModifiedById, updatedByUsername);
		}

		private String findUserById(Long createdById, Collection<AppUserData> allUsers) {
			String username = "";
			for (AppUserData appUserData : allUsers) {
				if (appUserData.getId().equals(createdById)) {
					username = appUserData.getUsername();
					break;
				}
			}
			return username;
		}
	}
}