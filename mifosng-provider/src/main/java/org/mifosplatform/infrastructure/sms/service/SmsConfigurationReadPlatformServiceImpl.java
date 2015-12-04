/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.sms.data.SmsConfigurationData;
import org.mifosplatform.infrastructure.sms.exception.SmsConfigurationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class SmsConfigurationReadPlatformServiceImpl implements SmsConfigurationReadPlatformService {
	
	private final JdbcTemplate jdbcTemplate;
    private final SmsConfigurationRowMapper smsConfigurationRowMapper;
    
    @Autowired
    public SmsConfigurationReadPlatformServiceImpl(final RoutingDataSource dataSource) {
    	this.jdbcTemplate = new JdbcTemplate(dataSource);
    	this.smsConfigurationRowMapper = new SmsConfigurationRowMapper();
    	
    }
	
	private static final class SmsConfigurationRowMapper implements RowMapper<SmsConfigurationData> {
		
		final String schema;
		
		public SmsConfigurationRowMapper() {
			 final StringBuilder sql = new StringBuilder(300);
	            sql.append("cnf.id as id, ");
	            sql.append("cnf.name as name, ");
	            sql.append("cnf.value as value ");
	            sql.append("from sms_configuration cnf");
	            
	            this.schema = sql.toString();
		}
		
		public String schema() {
            return this.schema;
        }

		@Override
		public SmsConfigurationData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
			
			final Long id = JdbcSupport.getLong(rs, "id");
			final String name = rs.getString("name");
			final String value = rs.getString("value");
			
			return SmsConfigurationData.instance(id, name, value);
		}
		
	}

	@Override
	public Collection<SmsConfigurationData> retrieveAll() {
		final String sql = "select " + this.smsConfigurationRowMapper.schema();

        return this.jdbcTemplate.query(sql, this.smsConfigurationRowMapper, new Object[] {});
	}

	@Override
	public SmsConfigurationData retrieveOne(String name) {
		try {
			final String sql = "select " + this.smsConfigurationRowMapper.schema() + " where cnf.name = ?";

	        return this.jdbcTemplate.queryForObject(sql, this.smsConfigurationRowMapper, new Object[] { name });
		}
		
		catch(final EmptyResultDataAccessException e) {
			
			throw new SmsConfigurationNotFoundException(name);
		}
	}

}
