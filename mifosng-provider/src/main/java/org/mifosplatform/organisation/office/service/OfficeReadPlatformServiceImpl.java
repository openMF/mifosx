/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.office.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.organisation.monetary.service.CurrencyReadPlatformService;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.data.OfficeTransactionData;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class OfficeReadPlatformServiceImpl implements OfficeReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final CurrencyReadPlatformService currencyReadPlatformService;
    private final static String nameDecoratedBaseOnHierarchy = "concat(substring('........................................', 1, ((LENGTH(o.hierarchy) - LENGTH(REPLACE(o.hierarchy, '.', '')) - 1) * 4)), o.name)";

    @Autowired
    public OfficeReadPlatformServiceImpl(final PlatformSecurityContext context,
            final CurrencyReadPlatformService currencyReadPlatformService, final TenantAwareRoutingDataSource dataSource) {
        this.context = context;
        this.currencyReadPlatformService = currencyReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class OfficeMapper implements RowMapper<OfficeData> {

        public String officeSchema() {
            return " o.id as id, o.name as name, "
                    + nameDecoratedBaseOnHierarchy
                    + " as nameDecorated, o.external_id as externalId, o.opening_date as openingDate, o.hierarchy as hierarchy, parent.id as parentId, parent.name as parentName "
                    + "from m_office o LEFT JOIN m_office AS parent ON parent.id = o.parent_id ";
        }

        @Override
        public OfficeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String nameDecorated = rs.getString("nameDecorated");
            final String externalId = rs.getString("externalId");
            final LocalDate openingDate = JdbcSupport.getLocalDate(rs, "openingDate");
            final String hierarchy = rs.getString("hierarchy");
            final Long parentId = JdbcSupport.getLong(rs, "parentId");
            final String parentName = rs.getString("parentName");

            return new OfficeData(id, name, nameDecorated, externalId, openingDate, hierarchy, parentId, parentName, null);
        }
    }

    private static final class OfficeDropdownMapper implements RowMapper<OfficeData> {

        public String schema() {
            return " o.id as id, " + nameDecoratedBaseOnHierarchy + " as nameDecorated, o.name as name from m_office o ";
        }

        @Override
        public OfficeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String nameDecorated = rs.getString("nameDecorated");

            return OfficeData.dropdown(id, name, nameDecorated);
        }
    }

    private static final class OfficeTransactionMapper implements RowMapper<OfficeTransactionData> {

        public String schema() {
            return " ot.id as id, ot.transaction_date as transactionDate, ot.from_office_id as fromOfficeId, fromoff.name as fromOfficeName, "
                    + " ot.to_office_id as toOfficeId, tooff.name as toOfficeName, ot.transaction_amount as transactionAmount, ot.description as description, "
                    + " ot.currency_code as currencyCode, rc.decimal_places as currencyDigits, "
                    + " rc.name as currencyName, rc.internationalized_name_code as currencyNameCode, rc.display_symbol as currencyDisplaySymbol "
                    + " from m_office_transaction ot "
                    + " left join m_office fromoff on fromoff.id = ot.from_office_id "
                    + " left join m_office tooff on tooff.id = ot.to_office_id " + " join m_currency rc on rc.`code` = ot.currency_code";
        }

        @Override
        public OfficeTransactionData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            Long id = rs.getLong("id");
            LocalDate transactionDate = JdbcSupport.getLocalDate(rs, "transactionDate");
            Long fromOfficeId = JdbcSupport.getLong(rs, "fromOfficeId");
            String fromOfficeName = rs.getString("fromOfficeName");
            Long toOfficeId = JdbcSupport.getLong(rs, "toOfficeId");
            String toOfficeName = rs.getString("toOfficeName");

            String currencyCode = rs.getString("currencyCode");
            String currencyName = rs.getString("currencyName");
            String currencyNameCode = rs.getString("currencyNameCode");
            String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");

            CurrencyData currencyData = new CurrencyData(currencyCode, currencyName, currencyDigits, currencyDisplaySymbol,
                    currencyNameCode);

            BigDecimal transactionAmount = rs.getBigDecimal("transactionAmount");
            String description = rs.getString("description");

            return OfficeTransactionData.instance(id, transactionDate, fromOfficeId, fromOfficeName, toOfficeId, toOfficeName,
                    currencyData, transactionAmount, description);
        }
    }

    @Override
    public Collection<OfficeData> retrieveAllOffices() {

        AppUser currentUser = context.authenticatedUser();

        String hierarchy = currentUser.getOffice().getHierarchy();
        String hierarchySearchString = hierarchy + "%";

        OfficeMapper rm = new OfficeMapper();
        String sql = "select " + rm.officeSchema() + "where o.hierarchy like ? order by o.hierarchy";

        return this.jdbcTemplate.query(sql, rm, new Object[] { hierarchySearchString });
    }

    @Override
    public Collection<OfficeData> retrieveAllOfficesForDropdown() {
        final AppUser currentUser = context.authenticatedUser();

        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final OfficeDropdownMapper rm = new OfficeDropdownMapper();
        final String sql = "select " + rm.schema() + "where o.hierarchy like ? order by o.hierarchy";

        return this.jdbcTemplate.query(sql, rm, new Object[] { hierarchySearchString });
    }

    @Override
    public OfficeData retrieveOffice(final Long officeId) {

        try {
            context.authenticatedUser();

            OfficeMapper rm = new OfficeMapper();
            String sql = "select " + rm.officeSchema() + " where o.id = ?";

            OfficeData selectedOffice = this.jdbcTemplate.queryForObject(sql, rm, new Object[] { officeId });

            return selectedOffice;
        } catch (EmptyResultDataAccessException e) {
            throw new OfficeNotFoundException(officeId);
        }
    }

    @Override
    public OfficeData retrieveNewOfficeTemplate() {

        context.authenticatedUser();

        return OfficeData.template(null, new LocalDate());
    }

    @Override
    public Collection<OfficeData> retrieveAllowedParents(final Long officeId) {

        context.authenticatedUser();
        Collection<OfficeData> filterParentLookups = new ArrayList<OfficeData>();

        if (isNotHeadOffice(officeId)) {
            Collection<OfficeData> parentLookups = retrieveAllOfficesForDropdown();

            for (OfficeData office : parentLookups) {
                if (!office.hasIdentifyOf(officeId)) {
                    filterParentLookups.add(office);
                }
            }
        }

        return filterParentLookups;
    }

    private boolean isNotHeadOffice(final Long officeId) {
        return !Long.valueOf(1).equals(officeId);
    }

    @Override
    public Collection<OfficeTransactionData> retrieveAllOfficeTransactions() {

        AppUser currentUser = context.authenticatedUser();

        String hierarchy = currentUser.getOffice().getHierarchy();
        String hierarchySearchString = hierarchy + "%";

        OfficeTransactionMapper rm = new OfficeTransactionMapper();
        String sql = "select " + rm.schema()
                + " where (fromoff.hierarchy like ? or tooff.hierarchy like ?) order by ot.transaction_date, ot.id";

        return this.jdbcTemplate.query(sql, rm, new Object[] { hierarchySearchString, hierarchySearchString });
    }

    @Override
    public OfficeTransactionData retrieveNewOfficeTransactionDetails() {
        context.authenticatedUser();

        final Collection<OfficeData> parentLookups = retrieveAllOfficesForDropdown();
        final Collection<CurrencyData> currencyOptions = currencyReadPlatformService.retrieveAllowedCurrencies();

        return OfficeTransactionData.template(new LocalDate(), parentLookups, currencyOptions);
    }
}