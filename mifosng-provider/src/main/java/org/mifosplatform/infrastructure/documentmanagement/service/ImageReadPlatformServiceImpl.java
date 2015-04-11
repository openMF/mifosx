/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.documentmanagement.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.documentmanagement.contentrepository.ContentRepository;
import org.mifosplatform.infrastructure.documentmanagement.contentrepository.ContentRepositoryFactory;
import org.mifosplatform.infrastructure.documentmanagement.data.ImageData;
import org.mifosplatform.organisation.staff.domain.Staff;
import org.mifosplatform.organisation.staff.domain.StaffRepositoryWrapper;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepositoryWrapper;
import org.mifosplatform.portfolio.client.exception.ImageNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ImageReadPlatformServiceImpl implements ImageReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final ContentRepositoryFactory contentRepositoryFactory;
    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final StaffRepositoryWrapper staffRepositoryWrapper;

    @Autowired
    public ImageReadPlatformServiceImpl(final RoutingDataSource dataSource, final ContentRepositoryFactory documentStoreFactory,
            final ClientRepositoryWrapper clientRepositoryWrapper, StaffRepositoryWrapper staffRepositoryWrapper) {
        this.staffRepositoryWrapper = staffRepositoryWrapper;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.contentRepositoryFactory = documentStoreFactory;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
    }

    private static final class ImageMapper implements RowMapper<ImageData> {

        private final String entityDisplayName;

        public ImageMapper(final String entityDisplayName) {
            this.entityDisplayName = entityDisplayName;
        }

        public String schema(String clientName) {
            StringBuilder builder = new StringBuilder("image.id as id, image.location as location, image.storage_type_enum as storageType ");
            if (ImageWritePlatformServiceJpaRepositoryImpl.IMAGE_TYPE.CLIENTS.toString().equals(clientName)) {
                builder.append(" from m_image image , m_client client " + " where client.image_id = image.id and client.id=?");
            } else if (ImageWritePlatformServiceJpaRepositoryImpl.IMAGE_TYPE.STAFF.toString().equals(clientName)) {
                builder.append("from m_image image , m_staff staff " + " where staff.image_id = image.id and staff.id=?");
            }
            return builder.toString();
        }

        @Override
        public ImageData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String location = rs.getString("location");
            final Integer storageType = JdbcSupport.getInteger(rs, "storageType");
            return new ImageData(id, location, storageType, this.entityDisplayName);
        }
    }

    @Override
    public ImageData retrieveImage(String clientName, final Long clientId) {
        try {
            Object owner;
            String displayName = null;
            if (ImageWritePlatformServiceJpaRepositoryImpl.IMAGE_TYPE.CLIENTS.toString().equals(clientName)) {
                owner = this.clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
                displayName = ((Client) owner).getDisplayName();
            } else if (ImageWritePlatformServiceJpaRepositoryImpl.IMAGE_TYPE.STAFF.toString().equals(clientName)) {
                owner = this.staffRepositoryWrapper.findOneWithNotFoundDetection(clientId);
                displayName = ((Staff) owner).displayName();
            }
            final ImageMapper imageMapper = new ImageMapper(displayName);

            final String sql = "select " + imageMapper.schema(clientName);

            final ImageData imageData = this.jdbcTemplate.queryForObject(sql, imageMapper, new Object[] { clientId });
            final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository(imageData.storageType());
            final ImageData result = contentRepository.fetchImage(imageData);

            if (result.getContent() == null) { throw new ImageNotFoundException("clients", clientId); }

            return result;
        } catch (final EmptyResultDataAccessException e) {
            throw new ImageNotFoundException("clients", clientId);
        }
    }
}