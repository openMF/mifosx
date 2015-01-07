/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.security.service;

import org.mifosplatform.infrastructure.security.domain.PlatformUser;
import org.mifosplatform.infrastructure.security.domain.PlatformUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service("clientDetailsService")
public class TenantAwareJpaPlatformClientDetailsService implements PlatformClientDetailsService {

    @Autowired
    private PlatformUserRepository platformUserRepository;

    @Override
    @Cacheable(value = "usersByUsername", key = "T(org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat(#username+'ubu')")
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        final boolean deleted = false;
        final boolean enabled = true;
        final PlatformUser appUser = this.platformUserRepository.findByUsernameAndDeletedAndEnabled(clientId, deleted, enabled);
        if (appUser == null || !appUser.getUsername().equals(clientId)) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        }
        return new BaseClientDetails(appUser.getUsername(), null, "all",
                "client_credentials", StringUtils.arrayToCommaDelimitedString(appUser.getAuthorities().toArray()));
    }
}
