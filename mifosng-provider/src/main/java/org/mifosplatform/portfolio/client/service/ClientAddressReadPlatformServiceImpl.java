/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.client.data.ClientAddressData;
import org.mifosplatform.portfolio.client.data.ClientIdentifierData;
import org.mifosplatform.portfolio.client.exception.ClientAddressNotFoundException;
import org.mifosplatform.portfolio.client.exception.ClientIdentifierNotFoundException;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service 
public class ClientAddressReadPlatformServiceImpl implements ClientAddressReadPlatformService {
	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	
	@Autowired
	public ClientAddressReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource ){
		this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public Collection<ClientAddressData> retrieveClientAddresses(final Long clientId){
		final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final ClientAddressMapper rm = new ClientAddressMapper();
        
        String sql = "select " + rm.schema();
        
        sql += "and ca.id";
        
        return this.jdbcTemplate.query(sql, rm, new Object[] { clientId, hierarchySearchString });
        
	}
	
	@Override
	public ClientAddressData retrieveClientAddress(final Long clientId, final Long clientAddressId){
		
		try {
            final AppUser currentUser = this.context.authenticatedUser();
            final String hierarchy = currentUser.getOffice().getHierarchy();
            final String hierarchySearchString = hierarchy + "%";

            final ClientAddressMapper rm = new ClientAddressMapper();

            String sql = "select " + rm.schema();

            sql += " and ca.id = ?";

            final ClientAddressData clientAddressData = this.jdbcTemplate.queryForObject(sql, rm, new Object[] { clientId,
                    hierarchySearchString, clientAddressId });

            return clientAddressData;
        } catch (final EmptyResultDataAccessException e) {
            throw new ClientIdentifierNotFoundException(clientAddressId);
        }
	}
	
	private static final class ClientAddressMapper implements RowMapper<ClientAddressData> {

        public ClientAddressMapper() {}

        public String schema() {
            return "ca.id as id, ca.client_id as clientId, ca.address_type_id as addressTypeId,ca.address_line as address_line, ca.address_line_two as address_line_two, ca.landmark as landmark, ca.city as city,ca.pincode as pincode,ca.isBoth_perma_same as isBoth, ca.state_type_id as stateTypeId,"
            		+ "at.code_value as addressType,"
            		+ "st.code_value as stateType"
                    + " from m_client_address ca, m_client c, m_office o, m_code_value at, m_code_value st "
                    + " where ca.client_id = c.id and c.office_id = o.id " + " and ca.address_type_id=at.id and ca.state_type_id=st.id"
                    + " and ca.client_id = ? and o.hierarchy like ?";
        }
        @Override
        public ClientAddressData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final Long addressTypeId = JdbcSupport.getLong(rs, "addressTypeId");
            final Long stateTypeId = JdbcSupport.getLong(rs, "stateTypeId");
            final String  address_line = rs.getString("address_line");
            final String address_line_two = rs.getString("address_line_two");
            final String landmark = rs.getString("landmark");
            final String city = rs.getString("city");
            
            final String pincode = rs.getString("pincode");
            final Boolean isBoth = rs.getBoolean("isBoth");
            
            final String addressTypeName = rs.getString("addressType");
            final String stateTypeName = rs.getString("stateType");

            final CodeValueData addressType = CodeValueData.instance(addressTypeId, addressTypeName);
            final CodeValueData stateType = CodeValueData.instance(stateTypeId, stateTypeName);

            return ClientAddressData.singleItem(id, clientId, addressType,address_line ,address_line_two,landmark,city,pincode,isBoth,stateType);
        }

	}
}
