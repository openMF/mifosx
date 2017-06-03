/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.adhocquery.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.fineract.adhocquery.data.AdHocData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.useradministration.exception.RoleNotFoundException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class AdHocReadPlatformServiceImpl implements AdHocReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final AdHocMapper adHocRowMapper;

    @Autowired
    public AdHocReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.adHocRowMapper = new AdHocMapper();
    }

    @Override
    public Collection<AdHocData> retrieveAll() {
        final String sql = "select " + this.adHocRowMapper.schema() + " order by r.id";

        return this.jdbcTemplate.query(sql, this.adHocRowMapper);
    }

    @Override
    public Collection<AdHocData> retrieveAllActiveRoles() {
        final String sql = "select " + this.adHocRowMapper.schema() + " where r.is_disabled = 0 order by r.id";

        return this.jdbcTemplate.query(sql, this.adHocRowMapper);
    }

    @Override
    public AdHocData retrieveOne(final Long id) {

        try {
            final String sql = "select " + this.adHocRowMapper.schema() + " where r.id=?";

            return this.jdbcTemplate.queryForObject(sql, this.adHocRowMapper, new Object[] { id });
        } catch (final EmptyResultDataAccessException e) {
            throw new RoleNotFoundException(id);
        }
    }

    protected static final class AdHocMapper implements RowMapper<AdHocData> {

        @Override
        public AdHocData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String name = rs.getString("name");
            final String query = rs.getString("query");
            final String tableName=rs.getString("tableName");
            final String tableFields=rs.getString("tableField");
            final Boolean isActive = rs.getBoolean("isActive");
            final LocalDate createdDate = JdbcSupport.getLocalDate(rs, "createdDate");
            final Long creatingByUserId = rs.getLong("creatingUserId");
           
            return new AdHocData(id,name,query, tableName,tableFields,isActive,createdDate,creatingByUserId);
        }

        public String schema() {
            return " r.id as id, r.name as name, r.query as query, r.table_name as tableName,r.table_fields as tableField ,r.IsActive as isActive from m_adhoc r";
        }
    }

   
}