/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.security.service;

import org.mifosplatform.useradministration.domain.AppUser;

public interface PlatformSecurityContext {

    AppUser authenticatedUser();
    
    void validateAccessRights(String resourceOfficeHierarchy);
}