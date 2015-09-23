/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.domain;

import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.client.exception.ClientNotActiveException;
import org.mifosplatform.portfolio.client.exception.ClientNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Wrapper for {@link ClientRepository} that adds NULL checking and Error
 * handling capabilities
 * </p>
 */
@Service
public class ClientRepositoryWrapper {

    private final ClientRepository repository;
    private final PlatformSecurityContext context;

    @Autowired
    public ClientRepositoryWrapper(final ClientRepository repository, final PlatformSecurityContext context) {
        this.repository = repository;
        this.context = context;
    }

    public Client findOneWithNotFoundDetection(final Long id) {
        final Client client = this.repository.findOne(id);
        if (client == null) { throw new ClientNotFoundException(id); }
        return client;
    }

    public void save(final Client client) {
        this.repository.save(client);
    }

    public void saveAndFlush(final Client client) {
        this.repository.saveAndFlush(client);
    }

    public void delete(final Client client) {
        this.repository.delete(client);
    }

    public Client getActiveClientInUserScope(Long clientId) {
        final Client client = this.findOneWithNotFoundDetection(clientId);
        if (client.isNotActive()) { throw new ClientNotActiveException(client.getId()); }
        this.context.validateAccessRights(client.getOffice().getHierarchy());
        return client;
    }

}