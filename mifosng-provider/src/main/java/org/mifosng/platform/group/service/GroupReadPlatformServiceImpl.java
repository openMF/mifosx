package org.mifosng.platform.group.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.mifosng.platform.api.data.ClientMemberData;
import org.mifosng.platform.api.data.GroupData;
import org.mifosng.platform.exceptions.GroupNotFoundException;
import org.mifosng.platform.infrastructure.TenantAwareRoutingDataSource;
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

    @Autowired
    public GroupReadPlatformServiceImpl(final PlatformSecurityContext context,
            final TenantAwareRoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
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

    private static final class GroupMapper implements RowMapper<GroupData> {

        public String groupSchema() {
            return "g.id as id, g.external_id as externalId, g.name as name from portfolio_group g";
        }

        @Override
        public GroupData mapRow(ResultSet rs, int rowNum) throws SQLException {

            Long id = rs.getLong("id");
            String name = rs.getString("name");
            String externalId = rs.getString("externalId");

            return new GroupData(id, name, externalId);
        }

    }

    @Override
    public Collection<ClientMemberData> retrieveClientMembers(Long groupId){
        
        this.context.authenticatedUser();
        
        ClientMemberSummaryDataMapper rm = new ClientMemberSummaryDataMapper();
        
        String sql = "select " + rm.clientMemberSummarySchema() + " where cm.is_deleted = 0 and pgc.group_id = ?";
        
        return this.jdbcTemplate.query(sql, rm, new Object[] {groupId});
    }
    
    private static final class ClientMemberSummaryDataMapper implements RowMapper<ClientMemberData> {

        public String clientMemberSummarySchema() {
            return "cm.id, cm.firstname, cm.lastname from portfolio_client cm INNER JOIN portfolio_group_client pgc ON pgc.client_id = cm.id";
        }

        @Override
        public ClientMemberData mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong("id");
            String firstname = rs.getString("firstname");
            String lastname = rs.getString("lastname");
            if (StringUtils.isBlank(firstname)) {
                firstname = "";
            }
            return new ClientMemberData(id, firstname, lastname);
        }

    }
    
}
