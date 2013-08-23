/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.group.service;

import java.util.Collection;

import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.portfolio.group.data.GroupGeneralData;
import org.mifosplatform.portfolio.group.data.GroupTransferData;

public interface GroupReadPlatformService {

    GroupGeneralData retrieveTemplate(Long officeId, boolean isCenterGroup, boolean staffInSelectedOfficeOnly);

    Page<GroupGeneralData> retrieveAll(SearchParameters searchParameters);

    GroupGeneralData retrieveOne(Long groupId);

    Collection<GroupGeneralData> retrieveGroupsForLookup(Long officeId, Long groupId);

    GroupTransferData retrieveClientTransferTemplate(Long officeId, Long groupId, boolean staffInSelectedOfficeOnly);
}