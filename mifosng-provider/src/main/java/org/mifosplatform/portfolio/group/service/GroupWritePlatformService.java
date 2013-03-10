/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.group.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.security.access.prepost.PreAuthorize;

public interface GroupWritePlatformService {

    @PreAuthorize(value = "hasAnyRole('ALL_FUNCTIONS', 'CREATE_GROUP')")
    CommandProcessingResult createGroup(JsonCommand command);

    @PreAuthorize(value = "hasAnyRole('ALL_FUNCTIONS', 'UPDATE_GROUP')")
    CommandProcessingResult updateGroup(Long grouptId, JsonCommand command);

    @PreAuthorize(value = "hasAnyRole('ALL_FUNCTIONS', 'DELETE_GROUP')")
    CommandProcessingResult deleteGroup(Long groupId);

    @PreAuthorize(value = "hasAnyRole('ALL_FUNCTIONS', 'UPDATE_GROUP')")
    CommandProcessingResult unassignStaff(Long grouptId, JsonCommand command);

}
