package org.mifosng.platform.client.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifosng.platform.api.data.AppUserData;
import org.mifosng.platform.api.data.ClientAccountSummaryCollectionData;
import org.mifosng.platform.api.data.ClientAccountSummaryData;
import org.mifosng.platform.api.data.ClientData;
import org.mifosng.platform.api.data.ClientIdentifierData;
import org.mifosng.platform.api.data.ClientLookup;
import org.mifosng.platform.api.data.CodeValueData;
import org.mifosng.platform.api.data.EnumOptionData;
import org.mifosng.platform.api.data.NoteData;
import org.mifosng.platform.api.data.OfficeLookup;
import org.mifosng.platform.client.domain.NoteEnumerations;
import org.mifosng.platform.common.ApplicationConstants;
import org.mifosng.platform.exceptions.ClientIdentifierNotFoundException;
import org.mifosng.platform.exceptions.ClientNotFoundException;
import org.mifosng.platform.exceptions.NoteNotFoundException;
import org.mifosng.platform.infrastructure.JdbcSupport;
import org.mifosng.platform.infrastructure.TenantAwareRoutingDataSource;
import org.mifosng.platform.organisation.service.CodeValueReadPlatformService;
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
	private final CodeValueReadPlatformService codeValuePlatformService;

	@Autowired
	public ClientReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource,
			final OfficeReadPlatformService officeReadPlatformService,
			final AppUserReadPlatformService appUserReadPlatformService,
			final CodeValueReadPlatformService codeValuePlatformService) {
		this.context = context;
		this.officeReadPlatformService = officeReadPlatformService;
		this.appUserReadPlatformService = appUserReadPlatformService;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.codeValuePlatformService = codeValuePlatformService;
	}


	@Override
	public Collection<ClientData> retrieveAllIndividualClients(final String extraCriteria) {

		AppUser currentUser = context.authenticatedUser();
		String hierarchy = currentUser.getOffice().getHierarchy();
		String hierarchySearchString = hierarchy + "%";	
		
		ClientMapper rm = new ClientMapper();

		String sql = "select " + rm.clientSchema();

		if (StringUtils.isNotBlank(extraCriteria))			
			sql += " and (" + extraCriteria + ")";

		sql += " order by c.lastname ASC, c.firstname ASC";

		return this.jdbcTemplate.query(sql, rm, new Object[] {hierarchySearchString});
	}

	@Override
	public ClientData retrieveIndividualClient(final Long clientId) {

		try {

			AppUser currentUser = context.authenticatedUser();
			String hierarchy = currentUser.getOffice().getHierarchy();
			String hierarchySearchString = hierarchy + "%";	
			
			ClientMapper rm = new ClientMapper();

			String sql = "select " + rm.clientSchema() + " and c.id = " + clientId;

			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] {hierarchySearchString});
		} catch (EmptyResultDataAccessException e) {
			throw new ClientNotFoundException(clientId);
		}
	}

    @Override
    public Collection<ClientLookup> retrieveAllIndividualClientsForLookup(String extraCriteria) {

        this.context.authenticatedUser();

        ClientLookupMapper rm = new ClientLookupMapper();

        String sql = "select " + rm.clientLookupSchema();

        if (StringUtils.isNotBlank(extraCriteria)){
            sql += " and (" + extraCriteria + ")";
        }

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    public Collection<ClientLookup> retrieveAllIndividualClientsForLookupByOfficeId(Long officeId) {
        this.context.authenticatedUser();

        ClientLookupMapper rm = new ClientLookupMapper();

        String sql = "select " + rm.clientLookupSchema() + " and c.office_id = " + officeId;

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

		public String clientSchema() {
			return "c.office_id as officeId, o.name as officeName, c.id as id, c.firstname as firstname, c.lastname as lastname, c.display_name as displayName, " +
				   "c.external_id as externalId, c.joining_date as joinedDate from m_client c join m_office o on o.id = c.office_id " +
					" where o.hierarchy like ? and c.is_deleted=0 ";
		}

		@Override
		public ClientData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long officeId = JdbcSupport.getLong(rs, "officeId");
			Long id = JdbcSupport.getLong(rs, "id");
			String firstname = rs.getString("firstname");
			if (StringUtils.isBlank(firstname)) {
				firstname = "";
			}
			String lastname = rs.getString("lastname");
			String displayName = rs.getString("displayName");
			String externalId = rs.getString("externalId");
			LocalDate joinedDate = JdbcSupport.getLocalDate(rs, "joinedDate");

			String officeName = rs.getString("officeName");

			return new ClientData(officeId, officeName, id, firstname,
					lastname, displayName, externalId, joinedDate);
		}

	}

    private static final class ClientLookupMapper implements RowMapper<ClientLookup>{

        public String clientLookupSchema() {
            return "c.id as id, c.firstname as firstname, c.lastname as lastname, " +
                   "c.office_id as officeId, o.name as officeName " +
                   "from m_client c join m_office o on o.id = c.office_id where c.is_deleted=0 ";
        }
        
        public String clientLookupByIdentifierSchema() {
            return "c.id as id, c.firstname as firstname, c.lastname as lastname, " +
                   "c.office_id as officeId, o.name as officeName " +
                   "from m_client c, m_office o, m_client_identifier ci " +
                   "where o.id = c.office_id and c.id=ci.client_id " +
                   "and ci.document_type_id= ? and ci.document_key like ?";
        }

        @Override
        public ClientLookup mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            Long id = rs.getLong("id");
            String firstname = rs.getString("firstname");
            String lastname = rs.getString("lastname");
            if (StringUtils.isBlank(firstname)) {
                firstname = "";
            }

            Long officeId = rs.getLong("officeId");
            String officeName = rs.getString("officeName");

            return new ClientLookup(id, firstname, lastname, officeId, officeName);
        }
    }

	@Override
	public ClientAccountSummaryCollectionData retrieveClientAccountDetails(final Long clientId) {

		try {
			this.context.authenticatedUser();
			
			// Check if client exists
			retrieveIndividualClient(clientId);

			List<ClientAccountSummaryData> pendingApprovalLoans = new ArrayList<ClientAccountSummaryData>();
			List<ClientAccountSummaryData> awaitingDisbursalLoans = new ArrayList<ClientAccountSummaryData>();
			List<ClientAccountSummaryData> openLoans = new ArrayList<ClientAccountSummaryData>();
			List<ClientAccountSummaryData> closedLoans = new ArrayList<ClientAccountSummaryData>();

			ClientLoanAccountSummaryDataMapper rm = new ClientLoanAccountSummaryDataMapper();

			String sql = "select " + rm.loanAccountSummarySchema() + " where l.client_id = ?";

			List<ClientAccountSummaryData> results = this.jdbcTemplate.query(sql, rm, new Object[] {clientId});
			if (results != null) {
				for (ClientAccountSummaryData row : results) {

					LoanStatusMapper statusMapper = new LoanStatusMapper(row.getAccountStatusId());

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
			
			List<ClientAccountSummaryData> pendingApprovalDepositAccounts = new ArrayList<ClientAccountSummaryData>();
			List<ClientAccountSummaryData> approvedDepositAccounts = new ArrayList<ClientAccountSummaryData>();
			List<ClientAccountSummaryData> withdrawnByClientDespositAccounts = new ArrayList<ClientAccountSummaryData>();
			List<ClientAccountSummaryData> closedDepositAccounts = new ArrayList<ClientAccountSummaryData>();
			List<ClientAccountSummaryData> rejectedDepositAccounts = new ArrayList<ClientAccountSummaryData>();
			List<ClientAccountSummaryData> preclosedDepositAccounts = new ArrayList<ClientAccountSummaryData>();
			List<ClientAccountSummaryData> maturedDepositAccounts = new ArrayList<ClientAccountSummaryData>();
			
			ClientDespoitAccountSummaryDataMapper depositAccountMapper = new ClientDespoitAccountSummaryDataMapper();

			String depositAccountsSql = "select " + depositAccountMapper.schema() + " where da.client_id = ? and da.is_deleted=0";
			List<ClientAccountSummaryData> depositAccountResults = this.jdbcTemplate.query(depositAccountsSql, depositAccountMapper, new Object[] {clientId});
			if (depositAccountResults != null) {
				for (ClientAccountSummaryData row : depositAccountResults) {

					if (row.getAccountStatusId() == 100) {
						pendingApprovalDepositAccounts.add(row);
					} else if (row.getAccountStatusId() == 300) {
						approvedDepositAccounts.add(row);
					} else if (row.getAccountStatusId() == 400) {
						withdrawnByClientDespositAccounts.add(row);
					} else if (row.getAccountStatusId() == 500) {
						rejectedDepositAccounts.add(row);
					} else if (row.getAccountStatusId() == 600) {
						closedDepositAccounts.add(row);
					} else if (row.getAccountStatusId() == 700) {
						maturedDepositAccounts.add(row);
					}else if (row.getAccountStatusId() == 800) {
						preclosedDepositAccounts.add(row);
					}
				}
			}

			return new ClientAccountSummaryCollectionData(pendingApprovalLoans, awaitingDisbursalLoans, openLoans, closedLoans, 
					pendingApprovalDepositAccounts, approvedDepositAccounts, withdrawnByClientDespositAccounts,
					rejectedDepositAccounts,closedDepositAccounts,preclosedDepositAccounts,maturedDepositAccounts);
		} catch (EmptyResultDataAccessException e) {
			throw new ClientNotFoundException(clientId);
		}
	}

	private static final class ClientLoanAccountSummaryDataMapper implements
			RowMapper<ClientAccountSummaryData> {

		public String loanAccountSummarySchema() {

			StringBuilder accountsSummary = new StringBuilder("l.id as id, l.external_id as externalId,");
			accountsSummary
					.append("l.product_id as productId, lp.name as productName,")
					.append("l.loan_status_id as statusId ")
					.append("from m_loan l ")
					.append("LEFT JOIN m_product_loan AS lp ON lp.id = l.product_id ");

			return accountsSummary.toString();
		}

		@Override
		public ClientAccountSummaryData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum) throws SQLException {

			Long id = JdbcSupport.getLong(rs, "id");
			String externalId = rs.getString("externalId");
			Long productId = JdbcSupport.getLong(rs, "productId");
			String loanProductName = rs.getString("productName");
			Integer loanStatusId = JdbcSupport.getInteger(rs, "statusId");

			return new ClientAccountSummaryData(id, externalId, productId, loanProductName, loanStatusId);
		}
	}
	
	private static final class ClientDespoitAccountSummaryDataMapper implements RowMapper<ClientAccountSummaryData> {

		public String schema() {
		
			StringBuilder accountsSummary = new StringBuilder("da.id as id, da.external_id as externalId,");
			accountsSummary.append("da.product_id as productId, dp.name as productName,")
						   .append("da.status_enum as statusId ")
						   .append("from m_deposit_account da ")
						   .append("LEFT JOIN m_product_deposit AS dp ON dp.id = da.product_id ");
		
			return accountsSummary.toString();
		}
		
		@Override
		public ClientAccountSummaryData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum) throws SQLException {
		
			Long id = JdbcSupport.getLong(rs, "id");
			String externalId = rs.getString("externalId");
			Long productId = JdbcSupport.getLong(rs, "productId");
			String productName = rs.getString("productName");
			Integer accountStatusId = JdbcSupport.getInteger(rs, "statusId");
		
			return new ClientAccountSummaryData(id, externalId, productId, productName, accountStatusId);
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
					+ " from m_note n";
		}

		@Override
		public NoteData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
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

	@Override
	public Collection<ClientIdentifierData> retrieveClientIdentifiers(
			Long clientId) {

		AppUser currentUser = context.authenticatedUser();
		String hierarchy = currentUser.getOffice().getHierarchy();
		String hierarchySearchString = hierarchy + "%";

		ClientIdentityMapper rm = new ClientIdentityMapper();

		String sql = "select " + rm.schema();

		sql += " order by ci.id";

		return this.jdbcTemplate.query(sql, rm, new Object[] { clientId,
				hierarchySearchString });
	}
	
	@Override
	public ClientIdentifierData retrieveClientIdentifier(Long clientId,
			Long clientIdentifierId) {
		try {
			AppUser currentUser = context.authenticatedUser();
			String hierarchy = currentUser.getOffice().getHierarchy();
			String hierarchySearchString = hierarchy + "%";

			ClientIdentityMapper rm = new ClientIdentityMapper();

			String sql = "select " + rm.schema();

			sql += " and ci.id = ?";

			ClientIdentifierData clientIdentifierData = this.jdbcTemplate
					.queryForObject(sql, rm, new Object[] { clientId,
							hierarchySearchString, clientIdentifierId });

			clientIdentifierData
					.setAllowedDocumentTypes(new ArrayList<CodeValueData>(
							codeValuePlatformService
									.retrieveAllCodeValues(ApplicationConstants.CLIENT_IDENTITY_CODE_ID)));
			return clientIdentifierData;
		} catch (EmptyResultDataAccessException e) {
			throw new ClientIdentifierNotFoundException(clientIdentifierId);
		}

	}
	
	 @Override
	public ClientIdentifierData retrieveNewClientIdentifierDetails() {

		context.authenticatedUser();

		List<CodeValueData> codeValueDatas = new ArrayList<CodeValueData>(
				codeValuePlatformService.retrieveAllCodeValues(ApplicationConstants.CLIENT_IDENTITY_CODE_ID));

		return new ClientIdentifierData(null,null,null,null,null,codeValueDatas);
	}

	private static final class ClientIdentityMapper implements
			RowMapper<ClientIdentifierData> {

		public ClientIdentityMapper() {
		}

		public String schema() {
			return "ci.id as id, ci.client_id as clientId, ci.document_type_id as documentTypeId, ci.document_key as documentKey,"
					+ " ci.description as description, cv.code_value as documentType "
					+ " from m_client_identifier ci, m_client c, m_office o, m_code_value cv"
					+ " where ci.client_id=c.id and c.office_id=o.id"
					+ " and ci.document_type_id=cv.id"
					+ " and ci.client_id = ? and o.hierarchy like ? ";
		}

		@Override
		public ClientIdentifierData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = JdbcSupport.getLong(rs, "id");
			Long clientId = JdbcSupport.getLong(rs, "clientId");
			Long documentTypeId = JdbcSupport.getLong(rs, "documentTypeId");
			String documentKey = rs.getString("documentKey");
			String description = rs.getString("description");
			String documentTypeName = rs.getString("documentType");

			return new ClientIdentifierData(id, clientId, documentTypeId,
					documentKey, description,documentTypeName);
		}

	}

	@Override
	public Boolean existsClientIdentifier(Long clientId, Long identifierTypeId) {
		try {
			AppUser currentUser = context.authenticatedUser();
			String hierarchy = currentUser.getOffice().getHierarchy();
			String hierarchySearchString = hierarchy + "%";

			ClientIdentityMapper rm = new ClientIdentityMapper();

			String sql = "select " + rm.schema() + " and ci.document_type_id=?";
			/****query for Object throws an exception if exactly one row is not returned***/
			this.jdbcTemplate.queryForObject(sql, rm, new Object[] { clientId,
					hierarchySearchString, identifierTypeId });
			return true;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}


	@Override
	public ClientLookup getClientByIdentifier(Long identifierTypeId,
			String identifierKey) {
		try {
			ClientLookupMapper mapper = new ClientLookupMapper();

			String sql = "select " + mapper.clientLookupByIdentifierSchema();
			/****
			 * query for Object throws an exception if exactly one row is not
			 * returned
			 ***/
			ClientLookup clientLookup = jdbcTemplate.queryForObject(sql,
					mapper, new Object[] { identifierTypeId, identifierKey });
			return clientLookup;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
}