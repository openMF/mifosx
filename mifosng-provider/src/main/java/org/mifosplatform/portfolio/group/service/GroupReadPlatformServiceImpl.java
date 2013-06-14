/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.group.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.service.CodeValueReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiParameterHelper;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.organisation.staff.data.StaffData;
import org.mifosplatform.organisation.staff.service.StaffReadPlatformService;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.client.service.LoanStatusMapper;
import org.mifosplatform.portfolio.group.api.GroupingTypesApiConstants;
import org.mifosplatform.portfolio.group.data.CenterData;
import org.mifosplatform.portfolio.group.data.GroupAccountSummaryCollectionData;
import org.mifosplatform.portfolio.group.data.GroupAccountSummaryData;
import org.mifosplatform.portfolio.group.data.GroupGeneralData;
import org.mifosplatform.portfolio.group.domain.GroupTypes;
import org.mifosplatform.portfolio.group.exception.GroupNotFoundException;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class GroupReadPlatformServiceImpl implements GroupReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final ClientReadPlatformService clientReadPlatformService;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final StaffReadPlatformService staffReadPlatformService;
    private final CenterReadPlatformService centerReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;

    private final AllGroupTypesDataMapper allGroupTypesDataMapper = new AllGroupTypesDataMapper();
    private final PaginationHelper<GroupGeneralData> paginationHelper = new PaginationHelper<GroupGeneralData>();

    @Autowired
    public GroupReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource,
            final CenterReadPlatformService centerReadPlatformService, final ClientReadPlatformService clientReadPlatformService,
            final OfficeReadPlatformService officeReadPlatformService, final StaffReadPlatformService staffReadPlatformService,
            final CodeValueReadPlatformService codeValueReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.centerReadPlatformService = centerReadPlatformService;
        this.clientReadPlatformService = clientReadPlatformService;
        this.officeReadPlatformService = officeReadPlatformService;
        this.staffReadPlatformService = staffReadPlatformService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
    }

    @Override
    public GroupGeneralData retrieveTemplate(final Long officeId, final boolean isCenterGroup, final boolean staffInSelectedOfficeOnly) {

        final Long defaultOfficeId = defaultToUsersOfficeIfNull(officeId);

        Collection<CenterData> centerOptions = null;
        if (isCenterGroup) {
            centerOptions = this.centerReadPlatformService.retrieveAllForDropdown(defaultOfficeId);
        }

        final Collection<OfficeData> officeOptions = this.officeReadPlatformService.retrieveAllOfficesForDropdown();

        final boolean loanOfficersOnly = false;
        Collection<StaffData> staffOptions = null;
        if (staffInSelectedOfficeOnly) {
            staffOptions = this.staffReadPlatformService.retrieveAllStaffForDropdown(defaultOfficeId);
        } else {
            staffOptions = this.staffReadPlatformService.retrieveAllStaffInOfficeAndItsParentOfficeHierarchy(defaultOfficeId,
                    loanOfficersOnly);
        }

        if (CollectionUtils.isEmpty(staffOptions)) {
            staffOptions = null;
        }

        Collection<ClientData> clientOptions = this.clientReadPlatformService.retrieveAllForLookupByOfficeId(defaultOfficeId);
        if (CollectionUtils.isEmpty(clientOptions)) {
            clientOptions = null;
        }

        final Collection<CodeValueData> availableRoles = this.codeValueReadPlatformService
                .retrieveCodeValuesByCode(GroupingTypesApiConstants.GROUP_ROLE_NAME);

        final Long centerId = null;
        final String centerName = null;
        final Long staffId = null;
        final String staffName = null;

        return GroupGeneralData.template(defaultOfficeId, centerId, centerName, staffId, staffName, centerOptions, officeOptions,
                staffOptions, clientOptions, availableRoles);
    }

    private Long defaultToUsersOfficeIfNull(final Long officeId) {
        Long defaultOfficeId = officeId;
        if (defaultOfficeId == null) {
            defaultOfficeId = this.context.authenticatedUser().getOffice().getId();
        }
        return defaultOfficeId;
    }

    @Override
    public Page<GroupGeneralData> retrieveAll(final SearchParameters searchParameters) {

        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
        sqlBuilder.append(this.allGroupTypesDataMapper.schema());
        sqlBuilder.append(" where o.hierarchy like ?");

        final String extraCriteria = getGroupExtraCriteria(searchParameters);

        if (StringUtils.isNotBlank(extraCriteria)) {
            sqlBuilder.append(" and (").append(extraCriteria).append(")");
        }

        if (searchParameters.isOrderByRequested()) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy()).append(' ').append(searchParameters.getSortOrder());
        }

        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }

        final String sqlCountRows = "SELECT FOUND_ROWS()";
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(),
                new Object[] { hierarchySearchString }, this.allGroupTypesDataMapper);
    }

    // 'g.' preffix because of ERROR 1052 (23000): Column 'column_name' in where
    // clause is ambiguous
    // caused by the same name of columns in m_office and m_group tables
    private String getGroupExtraCriteria(final SearchParameters searchCriteria) {

        String extraCriteria = " and g.level_Id = " + GroupTypes.GROUP.getId();

        String sqlSearch = searchCriteria.getSqlSearch();
        if (sqlSearch != null) {
            sqlSearch = sqlSearch.replaceAll(" display_name ", " g.display_name ");
            sqlSearch = sqlSearch.replaceAll("display_name ", "g.display_name ");
            extraCriteria += " and (" + sqlSearch + ")";
        }

        final Long officeId = searchCriteria.getOfficeId();
        if (officeId != null) {
            extraCriteria += " and g.office_id = " + officeId;
        }

        final String externalId = searchCriteria.getExternalId();
        if (externalId != null) {
            extraCriteria += " and g.external_id = " + ApiParameterHelper.sqlEncodeString(externalId);
        }

        final String name = searchCriteria.getName();
        if (name != null) {
            extraCriteria += " and g.display_name like " + ApiParameterHelper.sqlEncodeString("%" + name + "%");
        }

        final String hierarchy = searchCriteria.getHierarchy();
        if (hierarchy != null) {
            extraCriteria += " and o.hierarchy like " + ApiParameterHelper.sqlEncodeString(hierarchy + "%");
        }

        if (StringUtils.isNotBlank(extraCriteria)) {
            extraCriteria = extraCriteria.substring(4);
        }

        return extraCriteria;
    }

    @Override
    public GroupGeneralData retrieveOne(final Long groupId) {

        try {
            final AppUser currentUser = this.context.authenticatedUser();
            final String hierarchy = currentUser.getOffice().getHierarchy();
            final String hierarchySearchString = hierarchy + "%";

            final String sql = "select " + this.allGroupTypesDataMapper.schema() + " where g.id = ? and o.hierarchy like ?";
            return this.jdbcTemplate.queryForObject(sql, this.allGroupTypesDataMapper, new Object[] { groupId, hierarchySearchString });
        } catch (final EmptyResultDataAccessException e) {
            throw new GroupNotFoundException(groupId);
        }
    }

    @Override
    public GroupAccountSummaryCollectionData retrieveGroupAccountDetails(final Long groupId) {
        try {
            this.context.authenticatedUser();

            // Check if group exists
            // retrieveGroup(groupId);

            final List<GroupAccountSummaryData> pendingApprovalLoans = new ArrayList<GroupAccountSummaryData>();
            final List<GroupAccountSummaryData> awaitingDisbursalLoans = new ArrayList<GroupAccountSummaryData>();
            final List<GroupAccountSummaryData> openLoans = new ArrayList<GroupAccountSummaryData>();
            final List<GroupAccountSummaryData> closedLoans = new ArrayList<GroupAccountSummaryData>();

            final GroupLoanAccountSummaryDataMapper rm = new GroupLoanAccountSummaryDataMapper();

            String sql = "select " + rm.loanAccountSummarySchema() + " where l.group_id = ? and l.client_id is null";

            List<GroupAccountSummaryData> results = this.jdbcTemplate.query(sql, rm, new Object[] { groupId });
            if (results != null) {
                for (final GroupAccountSummaryData row : results) {

                    final LoanStatusMapper statusMapper = new LoanStatusMapper(row.getAccountStatusId());

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

            final List<GroupAccountSummaryData> pendingApprovalIndividualLoans = new ArrayList<GroupAccountSummaryData>();
            final List<GroupAccountSummaryData> awaitingDisbursalIndividualLoans = new ArrayList<GroupAccountSummaryData>();
            final List<GroupAccountSummaryData> openIndividualLoans = new ArrayList<GroupAccountSummaryData>();
            final List<GroupAccountSummaryData> closedIndividualLoans = new ArrayList<GroupAccountSummaryData>();

            sql = "select " + rm.loanAccountSummarySchema() + " where l.group_id = ? and l.client_id is not null";

            results = this.jdbcTemplate.query(sql, rm, new Object[] { groupId });
            if (results != null) {
                for (final GroupAccountSummaryData row : results) {

                    final LoanStatusMapper statusMapper = new LoanStatusMapper(row.getAccountStatusId());

                    if (statusMapper.isOpen()) {
                        openIndividualLoans.add(row);
                    } else if (statusMapper.isAwaitingDisbursal()) {
                        awaitingDisbursalIndividualLoans.add(row);
                    } else if (statusMapper.isPendingApproval()) {
                        pendingApprovalIndividualLoans.add(row);
                    } else {
                        closedIndividualLoans.add(row);
                    }
                }
            }

            return new GroupAccountSummaryCollectionData(pendingApprovalLoans, awaitingDisbursalLoans, openLoans, closedLoans,
                    pendingApprovalIndividualLoans, awaitingDisbursalIndividualLoans, openIndividualLoans, closedIndividualLoans);
        } catch (final EmptyResultDataAccessException e) {
            throw new GroupNotFoundException(groupId);
        }
    }

    @Override
    public Collection<GroupAccountSummaryData> retrieveGroupLoanAccountsByLoanOfficerId(final Long groupId, final Long loanOfficerId) {

        this.context.authenticatedUser();

        // Check if group exists
        // retrieveGroup(groupId);

        final GroupLoanAccountSummaryDataMapper rm = new GroupLoanAccountSummaryDataMapper();

        final String sql = "select " + rm.loanAccountSummarySchema()
                + " where l.group_id = ? and l.client_id is null and l.loan_officer_id = ?";

        final List<GroupAccountSummaryData> loanAccounts = this.jdbcTemplate.query(sql, rm, new Object[] { groupId, loanOfficerId });

        return loanAccounts;
    }

    private static final class GroupLoanAccountSummaryDataMapper implements RowMapper<GroupAccountSummaryData> {

        public String loanAccountSummarySchema() {

            final StringBuilder accountsSummary = new StringBuilder("l.id as id, l.external_id as externalId,");
            accountsSummary.append("l.product_id as productId, lp.name as productName,").append("l.loan_status_id as statusId, ")
                    .append("l.account_no as accountNo ").append("from m_loan l ")
                    .append("LEFT JOIN m_product_loan AS lp ON lp.id = l.product_id ");

            return accountsSummary.toString();
        }

        @Override
        public GroupAccountSummaryData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String externalId = rs.getString("externalId");
            final Long productId = JdbcSupport.getLong(rs, "productId");
            final String loanProductName = rs.getString("productName");
            final Integer loanStatusId = JdbcSupport.getInteger(rs, "statusId");
            final String accountNo = rs.getString("accountNo");
            return new GroupAccountSummaryData(id, externalId, productId, loanProductName, loanStatusId, accountNo);
        }
    }
}