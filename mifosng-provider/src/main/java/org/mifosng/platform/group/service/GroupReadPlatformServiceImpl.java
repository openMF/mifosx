package org.mifosng.platform.group.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifosng.platform.api.data.ClientLookup;
import org.mifosng.platform.api.data.GroupData;
import org.mifosng.platform.api.data.OfficeLookup;
import org.mifosng.platform.client.service.ClientReadPlatformService;
import org.mifosng.platform.exceptions.GroupNotFoundException;
import org.mifosng.platform.infrastructure.TenantAwareRoutingDataSource;
import org.mifosng.platform.organisation.service.OfficeReadPlatformService;
import org.mifosng.platform.security.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class GroupReadPlatformServiceImpl implements GroupReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final ClientReadPlatformService clientReadPlatformService;
    private final OfficeReadPlatformService officeReadPlatformService;

    @Autowired
    public GroupReadPlatformServiceImpl(final PlatformSecurityContext context,
            final TenantAwareRoutingDataSource dataSource, ClientReadPlatformService clientReadPlatformService,
            final OfficeReadPlatformService officeReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.clientReadPlatformService = clientReadPlatformService;
        this.officeReadPlatformService = officeReadPlatformService;
    }

    @Override
    public Collection<GroupData> retrieveAllGroups() {

        this.context.authenticatedUser();

        GroupMapper rm = new GroupMapper();

        String sql = "select " + rm.groupSchema() + " where g.is_deleted=0";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    public GroupData retrieveGroup(Long groupId) {
        
        try{
            this.context.authenticatedUser();
            
            GroupMapper rm = new GroupMapper();
            
            String sql = "select " + rm.groupSchema() + " where g.id = ? and g.is_deleted=0";
            
            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] {groupId});
        } catch (EmptyResultDataAccessException e){
            throw new GroupNotFoundException(groupId);
        }
    }

    @Override
    public GroupData retrieveNewGroupDetails() {

        this.context.authenticatedUser();

        List<ClientLookup> allowedClients = new ArrayList<ClientLookup>(
                this.clientReadPlatformService.retrieveAllIndividualClientsForLookup());

        List<OfficeLookup> allowedOffices = new ArrayList<OfficeLookup>(officeReadPlatformService.retrieveAllOfficesForLookup());

        return new GroupData(allowedClients, allowedOffices);
    }

    private static final class GroupMapper implements RowMapper<GroupData> {

        public String groupSchema() {
            return "g.office_id as officeId, o.name as officeName, g.id as id, g.external_id as externalId, " +
                   "g.name as name from m_group g join m_office o on o.id = g.office_id";
        }

        @Override
        public GroupData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {

            Long id = rs.getLong("id");
            String name = rs.getString("name");
            String externalId = rs.getString("externalId");

            Long officeId = rs.getLong("officeId");
            String officeName = rs.getString("officeName");

            return new GroupData(id, officeId, officeName, name, externalId);
        }

    }

    @Override
    public Collection<ClientLookup> retrieveClientMembers(Long groupId){
        
        this.context.authenticatedUser();
        
        ClientMemberSummaryDataMapper rm = new ClientMemberSummaryDataMapper();
        
        String sql = "select " + rm.clientMemberSummarySchema() + " where cm.is_deleted = 0 and pgc.group_id = ?";
        
        return this.jdbcTemplate.query(sql, rm, new Object[] {groupId});
    }
    
    private static final class ClientMemberSummaryDataMapper implements RowMapper<ClientLookup> {

        public String clientMemberSummarySchema() {
            return "cm.id, cm.firstname, cm.lastname from m_client cm INNER JOIN m_group_client pgc ON pgc.client_id = cm.id";
        }

        @Override
        public ClientLookup mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            Long id = rs.getLong("id");
            String firstname = rs.getString("firstname");
            String lastname = rs.getString("lastname");
            if (StringUtils.isBlank(firstname)) {
                firstname = "";
            }
            return new ClientLookup(id, firstname, lastname);
        }

    }
    
}
