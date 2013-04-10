/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.group.service;

import java.util.Collection;

import org.mifosplatform.organisation.monetary.data.MoneyData;
import org.mifosplatform.organisation.staff.data.StaffData;
import org.mifosplatform.portfolio.client.data.ClientLookup;
import org.mifosplatform.portfolio.group.data.GroupAccountSummaryCollectionData;
import org.mifosplatform.portfolio.group.data.GroupAccountSummaryData;
import org.mifosplatform.portfolio.group.data.GroupData;
import org.mifosplatform.portfolio.group.data.GroupLevelData;
import org.mifosplatform.portfolio.group.data.GroupLookup;

public interface GroupReadPlatformService {

    Collection<GroupData> retrieveAllGroups(String extraCriteria);

	GroupData retrieveGroup(Long groupId, Long levelId);

    GroupData retrieveNewGroupDetails(Long officeId, Long levelId);

    Collection<ClientLookup> retrieveClientMembers(Long groupId);

    GroupAccountSummaryCollectionData retrieveGroupAccountDetails(Long groupId);

    Collection<GroupAccountSummaryData> retrieveGroupLoanAccountsByLoanOfficerId(Long groupId, Long loanOfficerId);

    Collection<GroupLookup> retrieveAllGroupsbyOfficeIdAndLevelId(Long officeId, Long levelId);

    GroupLevelData retrieveGroupLevelDetails(Long levelId);

    Collection<StaffData> retrieveStaffsbyOfficeId(Long officeId);

	GroupData retrieveGroupDetails(Long groupId, Long levelId, boolean template);

    Collection<GroupLookup> retrieveChildGroupsbyGroupId(Long groupId);

    Long retrieveTotalNoOfChildGroups(Long groupId);

    Long retrieveTotalClients(String hierarchy);

    Collection<MoneyData> retrieveGroupLoanPortfolio(String hierarchy);

    GroupData retrieveNewChildGroupDetails(Long officeId, Long levelId, Long parentGroupId);

    StaffData retrieveStaffsbyId(Long staffId);
    
    Long getLevelIdByGroupId(final Long groupId);

}
